# UiAutomator2 Instrumentation Crash — Analysis & Future Work

> Saved for later. This documents the `instrumentation process is not running` error
> seen on the full run, what it means, and the planned fix (driver-recreate retry).

## The exception

```
org.openqa.selenium.WebDriverException: 'POST /element' cannot be proxied to
UiAutomator2 server because the instrumentation process is not running
(probably crashed). Check the server log and/or the logcat output for more details
...
Command: [..., findElement {using=xpath,
   value=(//android.widget.ImageView[@resource-id="com.dogpack:id/ivPicture"])[1]}]
Capabilities: ... deviceApiLevel: 36, deviceManufacturer: samsung,
   deviceModel: SM-M356B, platformVersion: 16 ...
```

## What it means

Appium drives the device through a helper app — the **UiAutomator2 server**, an
**instrumentation process that runs on the device**. The `findElement` command was
forwarded ("proxied") to that on-device server, but the server process was **no longer
alive** (`probably crashed`). So the lookup never got a chance to run and Appium threw
`WebDriverException`.

Key phrase: **"the instrumentation process is not running (probably crashed)."**
It is **not** a wrong-locator problem — the locator never executed. The on-device
automation agent died.

## Why it crashes (most likely here)

- **Memory pressure (the big one on Android 14/16).** Caps confirm `deviceApiLevel: 36`
  (Android 16) on a `samsung SM-M356B`. Heavy flows (image-heavy screens, long forms,
  lots of scrolling) push the device until Android's low-memory killer terminates the
  UiAutomator2 instrumentation. The failing `ivPicture` lookup is an image-heavy screen,
  which fits.
- **App crash / ANR** taking the instrumentation down with it, or a foreground system dialog.
- **Device sleep / lost ADB connection**, or the OS reclaiming the process.

Once it dies, **every subsequent command in that session fails the same way** until the
driver is recreated — which is why one crash cascades into a string of red tests.

## What it is NOT

- Not a bug in the test code or the XPath.
- The framework already detects it gracefully: the listener logs
  *"Screenshot skipped — driver session/instrumentation unavailable (probable
  instrumentation crash)."* That log line and this exception are the **same event**.

## Mitigations already in place (AndroidBaseTest)

- `uiautomator2ServerInstallTimeout` / `uiautomator2ServerLaunchTimeout` (60s)
- `adbExecTimeout` (60s)
- `ignoreHiddenApiPolicyError: true`
- `disableSuppressAccessibilityService: true`
- `mjpegServerPort: 8484` (alternative screenshot channel)
- `newCommandTimeout: 300`, `enforceAppInstall: false`, `forceAppLaunch: true`

## Planned / recommended next steps

1. **Driver-recreate retry (primary fix).** Add a TestNG `IRetryAnalyzer` that detects
   this exact message ("instrumentation process is not running") and retries the test
   **once after tearing down and re-initializing the driver**, so one crash does not
   poison the rest of the class. This is the standard resilience pattern.
2. **Extra hardening caps:** `appium:disableWindowAnimations: true`; keep
   `newCommandTimeout` generous.
3. **Reduce on-device memory pressure:** close/relaunch the app between heavy classes,
   avoid holding huge image lists in view, run fewer classes per session if needed.
4. **Device hygiene:** keep it plugged in/awake, disable battery optimization for the
   app, ensure free RAM/storage before a full run.

## TODO when we pick this up

- [ ] Implement `IRetryAnalyzer` keyed on the instrumentation-crash message + driver
      re-init, and wire it via a `IAnnotationTransformer` (or `retryAnalyzer` on tests).
- [ ] Add `disableWindowAnimations` capability.
- [ ] Decide retry count (1 is usually right) and whether to mark a recovered test as
      "passed after retry" in the Nine Hertz report.

---

## Real occurrence — Dogpack_WithoutLogin (full run, 26 Jun)

Confirmed this crash in the wild. `Dogpack_WithoutLogin`: 3 pass, 5 fail, all 5 from ONE event.

- **Root: `ReportLostDogWithoutLogin`** ran 14 steps fine, last good action `[ACTION] Opened gallery picker` (23:59:26) + `[FLOW] Permission pre-grant skipped (Android 13+ kills app on pm grant)`, then the instrumentation crashed:
  `'POST /element' cannot be proxied ... instrumentation process is not running (probably crashed)` at `WithoutLoginPage.java:940`.
  => crash trigger is the **gallery / photo-permission step** on Android 16 (memory pressure / app kill).
- **Cascade:** the 4 dependents (`reportDogFound`, `copyURLFunctionality`, `CloseActionPopupFunctionality`, `handleReportOrDelete`) have `dependsOnMethods=ReportLostDogWithoutLogin` BUT `alwaysRun`, so they ran on the dead session and failed with `AssertionError: 3-dot menu button not found on any lost dog row` (`clickOnLostDogThreeDotAction:1101`), each logging the same instrumentation-crash WARN. Session never recovered.
- Verdict: **flaky infrastructure, not a code/locator bug** (14 steps passed first).

## Retry granularity — decided

IRetryAnalyzer retries the **whole `@Test` method**, from the first line — NOT the failing action, and not "resume from the crash." TestNG has no action/step-level or mid-method resume. For a dead-session crash this is the correct (and only workable) granularity: you must tear down the dead driver, build a fresh session, and re-run the method from the top. Action-level retry can't help because every call to the dead instrumentation fails.

Implications to handle when implementing:
- Method re-runs fully, so it must be **safe to repeat**. Check `ReportLostDogWithoutLogin` is idempotent — a partially-completed first attempt must not cause a duplicate lost-dog report or "already exists" state on retry.
- Driver rebuild needs a hook: cleanest is a `@BeforeMethod`/setUp check "is the session alive? if not, recreate it" + analyzer returning `true`. **Need `AndroidBaseTest`** (driver create/teardown) to wire this.
- **Scope strictly:** analyzer returns `true` ONLY when the throwable message contains "instrumentation process is not running" (or session-not-created), so real assertion failures aren't silently retried and masking bugs.
- Pair with `disableWindowAnimations` cap and easing memory pressure around the gallery step (recreate/relaunch around photo-picker access).

## STATUS: deferred (do not implement yet)

Decision (26 Jun): hold off. Only implement the retry-analyzer if this crash is seen **repeatedly** across runs. If/when we do: get `AndroidBaseTest`, wire the analyzer + driver-rebuild scoped to the crash message, retry once, and verify `ReportLostDogWithoutLogin` is repeat-safe.
