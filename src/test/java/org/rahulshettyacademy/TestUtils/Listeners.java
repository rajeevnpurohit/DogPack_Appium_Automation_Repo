package org.rahulshettyacademy.TestUtils;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.rahulshettyacademy.utils.AppiumUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import io.appium.java_client.AppiumDriver;

/**
 * Listeners - HOTFIX for screenshot cascade-exception scenario.
 *
 * 🔴 CHANGES (May 21):
 *   - Wider exception catch around screenshot (catches WebDriverException too).
 *   - When UiAutomator2 instrumentation crashes mid-test, the test FAIL itself is
 *     logged correctly, but the FOLLOW-UP screenshot also fails with same error.
 *     Pehle wo cascade exception propagate hota tha. Ab gracefully handle.
 *
 * 🔴 CHANGES (Jun 1) - class-grouped Extent report:
 *   - Tests are now organized in a parent/child hierarchy in the
 *     ExtentReports output. Each TestNG class becomes a parent
 *     ExtentTest (e.g., "Dogpack_Marketplace") and each test method
 *     becomes a child node under that parent. The Spark theme renders
 *     this as collapsible class sections with their test methods
 *     nested underneath.
 *   - classTests map caches the parent ExtentTest per class name so
 *     the same parent is reused across all methods of a class.
 *   - onTestSkipped now defensively creates a child node if the
 *     "test" field is null (TestNG sometimes fires onTestSkipped
 *     without first calling onTestStart, for tests skipped via
 *     dependsOnMethods chain failure).
 *
 * NOTE: This is a minimal patch to the existing class.
 * For full logging + retry-awareness, use Batch 1's Listeners.java.
 */
public class Listeners extends AppiumUtils implements ITestListener {

	ExtentTest test;
	ExtentReports extent = ExtentReporterNG.getReporterObject();
	AppiumDriver driver;

	/**
	 * Cache of class-name -> parent ExtentTest. Populated on first
	 * test invocation of each class. Subsequent tests in the same
	 * class reuse the cached parent so all methods nest as children
	 * under one class-level row in the report.
	 */
	private static final Map<String, ExtentTest> classTests = new HashMap<>();

	/**
	 * Per-class counter of child nodes attached to each parent. Used
	 * in onFinish() to append "Test Cases (N)" to each parent's
	 * display name - the count then renders alongside the class name
	 * in both the Features list (left panel) and the test header
	 * (right panel) of the Spark report.
	 *
	 * Incremented inside every code path that calls parent.createNode():
	 *   - onTestStart            (normal test invocation)
	 *   - onTestSkipped          (when a fresh node has to be created
	 *                             for tests that never received onTestStart,
	 *                             e.g. skipped via dependsOnMethods chain)
	 */
	private static final Map<String, Integer> classTestCount = new HashMap<>();

	/** Current method's ExtentTest, per thread - used to route console
	 * action lines into the report's DETAILS area. */
	private static final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();

	private static volatile boolean teeInstalled = false;

	static {
		installReportTee();
		// Save reports even if the run is interrupted (Ctrl+C): a JVM shutdown
		// hook flushes ExtentReports and renders the Nine Hertz report on exit.
		Runtime.getRuntime().addShutdownHook(
				new Thread(Listeners::finalizeReports, "report-finalizer"));
	}

	private static final java.util.concurrent.atomic.AtomicBoolean reportsFinalized =
			new java.util.concurrent.atomic.AtomicBoolean(false);

	/** Flush + render both reports exactly once - called by onFinish and by the
	 *  shutdown hook, so an interrupted run still saves whatever completed. */
	static void finalizeReports() {
		if (!reportsFinalized.compareAndSet(false, true)) {
			return;
		}
		try {
			if (ExtentReporterNG.extent != null) {
				ExtentReporterNG.extent.flush();
			}
		} catch (Throwable ignored) {
		}
		try {
			NineHertzReport.render();
		} catch (Throwable ignored) {
		}
		System.out.println("[INFO] Reports finalized (flushed to reports/).");
	}

	/** Install a System.out wrapper (once) that mirrors tagged action lines
	 * into the current ExtentTest as PASS/FAIL/WARN rows. */
	private static synchronized void installReportTee() {
		if (teeInstalled) {
			return;
		}
		System.setOut(new ReportTee(System.out));
		teeInstalled = true;
	}

	private static final class ReportTee extends PrintStream {
		private final PrintStream orig;

		ReportTee(PrintStream orig) {
			super(orig);
			this.orig = orig;
		}

		@Override
		public void println(String x) {
			orig.println(x);
			route(x);
		}

		@Override
		public void println(Object x) {
			orig.println(x);
			route(String.valueOf(x));
		}

		private void route(String line) {
			if (line == null) {
				return;
			}
			// Feed the modern Nine Hertz report (guards itself on tag/current).
			NineHertzReport.logEvent(line.trim());
			ExtentTest t = currentTest.get();
			if (t == null) {
				return;
			}
			String sLine = line.trim();
			if (sLine.isEmpty()) {
				return;
			}
			String up = sLine.toUpperCase();
			Status st;
			if (up.startsWith("[WARN]") || up.startsWith("[WARNING]")) {
				// A handled warning stays a warning even if it mentions a
				// caught exception (e.g. "[WARN] ... NoSuchElementException").
				st = Status.WARNING;
			} else if (up.startsWith("[ASSERT FAIL]") || up.startsWith("[FAIL]")
					|| up.startsWith("[ERROR]") || up.contains("EXCEPTION")) {
				st = Status.FAIL;
			} else if (sLine.startsWith("[ACTION]") || sLine.startsWith("[INPUT]")
					|| sLine.startsWith("[INFO]") || sLine.startsWith("[FLOW]")
					|| sLine.startsWith("[OK]") || sLine.startsWith("[STEP]")
					|| sLine.startsWith("[ASSERT")) {
				st = Status.PASS;
			} else {
				return; // untagged lifecycle noise - keep it out of the report
			}
			try {
				t.log(st, escape(sLine));
			} catch (Exception ignore) {
				// never let report logging break the run
			}
		}

		private String escape(String x) {
			return x.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
		}
	}

	/**
	 * Get the parent ExtentTest for the given simple class name,
	 * creating it on first request. Synchronized to be safe if
	 * TestNG ever runs tests in parallel - the current suite is
	 * sequential so this is defensive.
	 */
	private synchronized ExtentTest getOrCreateClassParent(String simpleClassName) {
		ExtentTest parent = classTests.get(simpleClassName);
		if (parent == null) {
			parent = extent.createTest(simpleClassName);
			classTests.put(simpleClassName, parent);
		}
		return parent;
	}

	/**
	 * Helper to extract the simple class name from an ITestResult.
	 * TestNG returns the fully-qualified name (e.g.,
	 * "org.rahulshettyacademy.Dogpack_Marketplace"); we keep just
	 * the part after the last dot, which matches how users refer
	 * to the class in the suite XML and conversation.
	 */
	private String getSimpleClassName(ITestResult result) {
		String fqn = result.getMethod().getTestClass().getName();
		int lastDot = fqn.lastIndexOf('.');
		return (lastDot >= 0) ? fqn.substring(lastDot + 1) : fqn;
	}

	@Override
	public void onTestStart(ITestResult result) {
		// Get or create the parent ExtentTest for this class, then
		// attach this test method as a child node under it.
		String simpleName = getSimpleClassName(result);
		ExtentTest parent = getOrCreateClassParent(simpleName);
		test = parent.createNode(result.getMethod().getMethodName());
		classTestCount.merge(simpleName, 1, Integer::sum);
		currentTest.set(test);
		test.info("Test Actions performed");
		NineHertzReport.startTest(simpleName, result.getMethod().getMethodName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		test.log(Status.PASS, "Test Passed");
		String shot = captureAndAttach(result);
		NineHertzReport.endTest(getSimpleClassName(result),
				result.getMethod().getMethodName(), "pass", null, shot);
		currentTest.remove();
	}

	@Override
	public void onTestFailure(ITestResult result) {
		test.fail(result.getThrowable());
		String shot = captureAndAttach(result);
		NineHertzReport.endTest(getSimpleClassName(result),
				result.getMethod().getMethodName(), "fail",
				NineHertzReport.stackToString(result.getThrowable()), shot);
		currentTest.remove();
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		// Defensive: TestNG sometimes fires onTestSkipped for a test
		// that never had onTestStart called (e.g., skipped via
		// dependsOnMethods chain failure where the dependency failed
		// before reaching this test). In that case "test" still holds
		// the LAST started test's child node, so we'd misattribute
		// the skip. Detect and create a fresh child node bound to
		// the right parent + method name.
		boolean needFreshNode =
				(test == null)
				|| !result.getMethod().getMethodName().equals(
						getCurrentTestNodeName());
		if (needFreshNode) {
			String simpleName = getSimpleClassName(result);
			ExtentTest parent = getOrCreateClassParent(simpleName);
			test = parent.createNode(result.getMethod().getMethodName());
			classTestCount.merge(simpleName, 1, Integer::sum);
		}

		Throwable t = result.getThrowable();
		String reason = (t != null && t.getMessage() != null)
				? t.getMessage().split("\n")[0] : "skipped";
		test.skip("SKIPPED: " + reason);
		NineHertzReport.endTest(getSimpleClassName(result),
				result.getMethod().getMethodName(), "skip", reason, null);
		currentTest.remove();
	}

	/**
	 * Best-effort: read the test node's name to compare against the
	 * incoming test method name. ExtentReports exposes the model name
	 * via getModel().getName(). If the API is unavailable or null,
	 * returns null and the caller treats that as "create fresh".
	 */
	private String getCurrentTestNodeName() {
		try {
			if (test != null && test.getModel() != null) {
				return test.getModel().getName();
			}
		} catch (Exception ignore) {
			// fall through
		}
		return null;
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// no-op
	}

	@Override
	public void onStart(ITestContext context) {
		// no-op
	}

	@Override
	public void onFinish(ITestContext context) {
		appendTestCaseCountsToParentNames();
		extent.flush();
		postProcessReportLabels();
		// Generate the modern Nine Hertz report alongside ExtentReports.
		NineHertzReport.render();
		reportsFinalized.set(true);   // clean finish - shutdown hook will skip
	}

	// ====================================================================
	// 🔴 PARENT NAME DECORATION (Test Case count)
	// ====================================================================
	/**
	 * Iterate every parent ExtentTest cached in classTests and append
	 * " | Test Cases (N)" to its display name, where N is the number
	 * of child nodes attached to that parent (tracked in
	 * classTestCount).
	 *
	 * The renamed parent shows up in TWO places in the Spark report:
	 *   - Left panel ("Features" list)
	 *   - Right panel (test header above the start/end timestamps)
	 * Both render the parent's name field, so a single rename
	 * propagates to both.
	 *
	 * MUST run BEFORE extent.flush() because flush() is what
	 * serializes the in-memory model into reports/index.html.
	 * Renaming after flush would not affect the generated file.
	 *
	 * Wrapped in try/catch per parent so one bad rename can't block
	 * the others. Failures are logged but not thrown - a broken
	 * rename should never prevent the report from being written.
	 */
	private void appendTestCaseCountsToParentNames() {
		int renamed = 0;
		for (Map.Entry<String, ExtentTest> entry : classTests.entrySet()) {
			String className = entry.getKey();
			ExtentTest parent = entry.getValue();
			int count = classTestCount.getOrDefault(className, 0);
			try {
				String newName = className + "  |  Test Cases (" + count + ")";
				parent.getModel().setName(newName);
				renamed++;
			} catch (Exception e) {
				System.out.println("[WARN] Could not append count to parent "
						+ "for class '" + className + "' (non-fatal): "
						+ e.getClass().getSimpleName() + ": " + e.getMessage());
			}
		}
		System.out.println("[OK]   Decorated " + renamed + " parent test(s) "
				+ "with 'Test Cases (N)' suffix");
	}

	// ====================================================================
	// 🔴 REPORT-LABEL CUSTOMIZATION
	// ====================================================================
	/**
	 * Rewrites a small set of UI labels in the generated Spark HTML report
	 * after extent.flush() has produced it. Spark bundles these strings
	 * directly in its template HTML/JS - there is no built-in config to
	 * change them - so the only reliable customization is post-processing
	 * the file once it's been written.
	 *
	 * Replacements:
	 *   ">Tests<"  -> ">Features<"     (dashboard card label, sidebar)
	 *   ">Steps<"  -> ">Test Cases<"   (dashboard card label, sidebar)
	 *   "(N tests)" -> "(N Test Cases)"  (class header counts, pie captions)
	 *
	 * The first two patterns are anchored to angle brackets so they only
	 * match HTML tag content - class names and method names in
	 * data-attributes won't be touched. The numeric regex on "tests"
	 * only fires when preceded by a digit so unrelated text containing
	 * the substring "tests" (e.g. "tested", "testsuite") is safe.
	 *
	 * Idempotent and defensive - if the report file doesn't exist or
	 * any I/O fails, logs a warning and continues without throwing.
	 */
	private void postProcessReportLabels() {
		try {
			Path htmlPath = Paths.get(System.getProperty("user.dir"),
					"reports", "index.html");
			if (!Files.exists(htmlPath)) {
				System.out.println("[WARN] Report HTML not found at "
						+ htmlPath + " - skipping label rewrite");
				return;
			}

			String original = new String(
					Files.readAllBytes(htmlPath), StandardCharsets.UTF_8);
			String content = original;

			// Dashboard / sidebar tag-content labels
			content = content.replace(">Tests<", ">Features<");
			content = content.replace(">Steps<", ">Test Cases<");

			// Numeric "tests" forms - matches "8 tests", "0 tests", etc.
			// (anchored on a digit prefix so unrelated text is safe)
			content = content.replaceAll(
					"(\\d+)\\s+tests\\b", "$1 Test Cases");

			if (!content.equals(original)) {
				Files.write(htmlPath, content.getBytes(StandardCharsets.UTF_8));
				System.out.println("[OK]   Report labels rewritten: "
						+ "Tests->Features, Steps->Test Cases, tests->Test Cases");
			} else {
				System.out.println("[INFO] No target labels found in report "
						+ "- nothing to rewrite (may indicate a Spark version "
						+ "change; verify labels by opening reports/index.html)");
			}
		} catch (Exception e) {
			System.out.println("[WARN] Post-processing report labels failed "
					+ "(non-fatal): " + e.getClass().getSimpleName()
					+ ": " + e.getMessage());
		}
	}

	// ====================================================================
	// 🔴 SAFE SCREENSHOT HELPER - replaces inline try/catch (IOException) pattern
	// ====================================================================
	/**
	 * Capture screenshot and attach to current Extent test entry.
	 * Catches ALL exceptions (including WebDriverException for crashed sessions).
	 * If screenshot fails, just logs a warning - never throws.
	 */
	private String captureAndAttach(ITestResult result) {
		try {
			driver = (AppiumDriver) result.getTestClass().getRealClass()
					.getField("driver").get(result.getInstance());
		} catch (Exception fieldEx) {
			System.out.println("[WARN] Listener: could not access driver field - "
					+ fieldEx.getClass().getSimpleName());
			if (test != null) {
				test.log(Status.WARNING, "Screenshot skipped - driver field unavailable");
			}
			return null;
		}

		// Capture real device model + Android version from the live session
		// capabilities (UiAutomator2 returns the actual connected device info,
		// unlike the requested 'deviceName' which may be a hardcoded value).
		try {
			org.openqa.selenium.Capabilities caps = driver.getCapabilities();
			Object model = caps.getCapability("deviceModel");
			Object mfr = caps.getCapability("deviceManufacturer");
			Object pv = caps.getCapability("platformVersion");
			String dev = null;
			if (model != null && !model.toString().trim().isEmpty()) {
				String mm = model.toString().trim();
				if (mfr != null && !mfr.toString().trim().isEmpty()
						&& !mm.toLowerCase().startsWith(mfr.toString().trim().toLowerCase())) {
					mm = mfr.toString().trim() + " " + mm;
				}
				dev = mm;
			}
			String os = (pv != null && !pv.toString().trim().isEmpty())
					? "Android " + pv.toString().trim() : null;
			NineHertzReport.setEnv(null, dev, os);
		} catch (Exception ignore) {
			// keep defaults if capabilities are unavailable
		}

		try {
			String screenshotPath = getScreenshotPath(
					result.getMethod().getMethodName(), driver);
			if (screenshotPath != null) {
				test.addScreenCaptureFromPath(screenshotPath,
						result.getMethod().getMethodName());
				return screenshotPath;
			} else {
				// getScreenshotPath returned null - session was dead
				test.log(Status.WARNING,
						"Screenshot skipped - driver session unavailable "
						+ "(probable instrumentation crash)");
				return null;
			}
		} catch (Exception screenshotEx) {
			// Ye wo cascade exception thi - ab safe catch karte hain
			System.out.println("[WARN] Listener: screenshot attach failed - "
					+ screenshotEx.getClass().getSimpleName() + ": "
					+ (screenshotEx.getMessage() != null
							? screenshotEx.getMessage().split("\n")[0] : "no message"));
			test.log(Status.WARNING,
					"Screenshot capture failed: " + screenshotEx.getClass().getSimpleName());
			return null;
		}
	}
}