package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rahulshettyacademy.utils.AndroidActions;
import org.testng.Assert;

import com.google.common.collect.ImmutableMap;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

/**
 * Page Object for the NEW native (com.dogpack.mediaediting) Add Post module.
 *
 * IMPORTANT: This targets the native Kotlin media-editor rewrite of Add Post,
 * which as of this writing has NOT been released to the client yet. It is a
 * separate implementation from the old React Native composer automated in
 * PostingExperiencePage.java, which remains parked/untouched for that (soon
 * to be replaced) build. This class deliberately does NOT import, extend, or
 * otherwise depend on PostingExperiencePage - since that flow may be retired
 * or deleted once the native module ships, nothing here should break if it
 * is. Any reusable logic (e.g. the permission-dialog dismiss pattern below)
 * has been copied and adapted in place, not inherited or referenced.
 *
 * Flow covered - "Post image + caption text" (Inspector-verified locators,
 * confirmed against a real device recording on 2026-07-08):
 *
 *   1. Tap "+" bottom-tab            -> add-feed (accessibility id)
 *   2. Dismiss camera + audio permission dialogs (Camera screen first launch):
 *        - "Allow DogPack to take pictures and record video?" (camera+mic)
 *        - "Allow DogPack to record audio?" (mic-only)
 *      both answered "While using the app"
 *   3. Switch composer to Text mode  -> content-desc "Switch to Text mode"
 *   4. Tap the image icon            -> content-desc "Media image"
 *   5. Pick an image from the grid   -> "Media grid" nested View [2][2]
 *   6. Confirm selection             -> text "Done"
 *   7. Advance out of the editor     -> accessibility id "Create post"
 *   8. Submit from single-item preview -> content-desc "Post media"
 *   9. Type caption on the RN "Create Post" tag/caption screen (deep xpath -
 *      no resource-id/testID exposed on this RN screen yet)
 *  10. Tap final submit "Post"       -> (text="Post")[2]
 *
 * NOT currently part of the flow: reading the success toast
 * ("Post uploaded successfully"). verifyPostUploadedToast() exists and is
 * callable, but its captured xpath returns empty text on device - it's
 * disabled in postImageWithCaption() until that locator is re-verified.
 *
 * Steps 1, 3-8 and 10 map to confirmed native resource-ids from the app source
 * (CameraFragment / TextPostEditorFragment / FinalPreviewFragment). Step 9
 * (and the disabled toast check) sit on the React Native "Create Post"
 * hand-off screen (SharePostComponent) which currently has no clean ids -
 * the deep xpaths below came directly from Appium Inspector against a live
 * run and should be replaced with cleaner locators if/when testIDs are
 * added to that screen.
 */
public class AddPostPage extends AndroidActions {

    AndroidDriver driver;
    WebDriverWait wait;
    WebDriverWait shortWait;

    public AddPostPage(AndroidDriver driver) {
        super(driver);
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    // ================================================================
    // ===================  LOCATORS  =================================
    // ================================================================

    /** Home bottom-tab "+" button (same accessibility id as the legacy build). */
    @AndroidFindBy(accessibility = "add-feed")
    private WebElement addFeedBtn;

    /** Camera-screen mode tab - switches the composer into Text mode. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@content-desc=\"Switch to Text mode\"]")
    private WebElement switchToTextModeBtn;

    /** Image/background icon on the text composer canvas. */
    @AndroidFindBy(xpath = "//android.widget.FrameLayout[@content-desc=\"Media image\"]")
    private WebElement mediaImageIcon;

    /** Second row, second column tile in the gallery bottom-sheet grid. */
    @AndroidFindBy(xpath = "//android.view.View[@content-desc=\"Media grid\"]/android.view.View/android.view.View[2]/android.view.View[2]/android.view.View")
    private WebElement gallerySecondImage;

    /** Confirms the media selection in the gallery bottom sheet. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Done\"]")
    private WebElement doneBtn;

    /** Advances from the text/media composer into the single-item preview. */
    @AndroidFindBy(accessibility = "Create post")
    private WebElement createPostBtn;

    /** Submit button on the native single-item preview screen (FinalPreviewFragment). */
    @AndroidFindBy(xpath = "//android.widget.Button[@content-desc=\"Post media\"]")
    private WebElement postMediaBtn;

    /** Caption/description input on the RN "Create Post" tag screen - no clean id yet. */
    @AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[3]/android.widget.ScrollView")
    private WebElement captionInput;

    /** Final submit "Post" button on the RN Create Post screen (index 2 - index 1 is the top-tab label). */
    @AndroidFindBy(xpath = "(//android.widget.TextView[@text=\"Post\"])[2]")
    private WebElement finalPostBtn;

    /** Success toast shown after upload completes - deep xpath, no id exposed. */
    @AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[2]/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.view.ViewGroup[1]")
    private WebElement successToast;

    // ================================================================
    // ===================  DIAGNOSTIC LOGGING  =======================
    // ================================================================
    // Same greppable pattern used across the project:
    //   grep -E "===>|===<|FAIL|WARN|ASSERT" run.log

    private void testStart(String name) {
        System.out.println();
        System.out.println("===========================================");
        System.out.println("===> TEST START: " + name);
        System.out.println("===========================================");
    }

    private void testEnd(String name) {
        System.out.println("===< TEST END:   " + name);
        System.out.println("===========================================");
        System.out.println();
    }

    private void step(int num, String description) {
        System.out.println("[STEP " + num + "] " + description);
    }

    private void sleepQuiet(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignore) { }
    }

    // ================================================================
    // ===================  PERMISSION HANDLING  ======================
    // ================================================================

    /**
     * Best-effort dismiss of a SINGLE Android runtime permission dialog.
     * Ported from PostingExperiencePage's dismissAllPermissionDialogs - uses
     * raw findElements (no PageFactory proxy) so a missing dialog costs
     * ~0.5s, not a 10s implicit-wait timeout.
     *
     * @return true if a dialog button was found and clicked this call.
     */
    private boolean dismissAnyPermissionDialog() {
        boolean clicked = clickRawByLocator(org.openqa.selenium.By.id(
                "com.android.permissioncontroller:id/permission_allow_all_button"),
                "Allow All");
        if (!clicked) {
            clicked = clickRawByLocator(org.openqa.selenium.By.id(
                    "com.android.permissioncontroller:id/permission_allow_foreground_only_button"),
                    "While using the app");
        }
        if (!clicked) {
            clicked = clickRawByLocator(org.openqa.selenium.By.id(
                    "com.android.permissioncontroller:id/permission_allow_button"),
                    "Allow");
        }
        if (!clicked) {
            clicked = clickRawByLocator(AppiumBy.androidUIAutomator(
                    "new UiSelector().textMatches(\"(?i)while\\\\s+using\\\\s+the\\\\s+app\")"),
                    "While using the app (text match)");
        }
        if (!clicked) {
            clicked = clickRawByLocator(AppiumBy.androidUIAutomator(
                    "new UiSelector().textStartsWith(\"Allow\")"),
                    "Allow (textStartsWith)");
        }
        if (clicked) {
            System.out.println("[FLOW] Dismissed a permission dialog");
        }
        return clicked;
    }

    /**
     * Loops the single-dialog dismiss above {@code maxAttempts} times, to
     * cover screens that fire more than one permission prompt back-to-back -
     * specifically the Camera screen on first launch, which asks for:
     *   1. "Allow DogPack to take pictures and record video?" (camera + mic)
     *   2. "Allow DogPack to record audio?" (separate mic-only prompt)
     * both as "While using the app" dialogs.
     *
     * Sleeps ~1.2s between EVERY iteration regardless of outcome - the first
     * dialog can take a moment to render after tapping "+" (CameraX /
     * permission-request startup), so a single instant check can easily run
     * before the dialog exists and wrongly conclude there's nothing to
     * dismiss. Mirrors the proven pattern from PostingExperiencePage rather
     * than early-exiting on one narrow pre-check.
     */
    private void dismissAllPermissionDialogs(int maxAttempts) {
        int dismissedCount = 0;
        for (int i = 0; i < maxAttempts; i++) {
            if (dismissAnyPermissionDialog()) {
                dismissedCount++;
            } else if (i == 0) {
                System.out.println("[FLOW] No permission dialog on first check - "
                        + "waiting in case one is still rendering");
            }
            sleepQuiet(1200);
        }
        System.out.println("[FLOW] dismissAllPermissionDialogs done - "
                + dismissedCount + " dialog(s) dismissed across "
                + maxAttempts + " checks");
    }

    /** Raw (non-PageFactory) find + click, returns false instantly if not present. */
    private boolean clickRawByLocator(org.openqa.selenium.By locator, String label) {
        try {
            java.util.List<WebElement> found = driver.findElements(locator);
            if (!found.isEmpty() && found.get(0).isDisplayed()) {
                found.get(0).click();
                System.out.println("[ACTION] Clicked: " + label);
                return true;
            }
        } catch (Exception ignore) { /* dialog not present */ }
        return false;
    }

    // ================================================================
    // ===================  FLOW STEPS  ================================
    // ================================================================

    public void clickAddFeedIcon() {
        wait.until(ExpectedConditions.elementToBeClickable(addFeedBtn)).click();
        System.out.println("[ACTION] Tapped + (add-feed)");
    }

    public void switchToTextMode() {
        wait.until(ExpectedConditions.elementToBeClickable(switchToTextModeBtn)).click();
        System.out.println("[ACTION] Switched composer to Text mode");
    }

    public void clickMediaImageIcon() {
        wait.until(ExpectedConditions.elementToBeClickable(mediaImageIcon)).click();
        System.out.println("[ACTION] Tapped media image icon");
    }

    public void selectImageInGallery() {
        dismissAnyPermissionDialog();
        wait.until(ExpectedConditions.elementToBeClickable(gallerySecondImage)).click();
        System.out.println("[ACTION] Selected image from gallery grid");
    }

    public void clickDone() {
        wait.until(ExpectedConditions.elementToBeClickable(doneBtn)).click();
        System.out.println("[ACTION] Tapped Done");
    }

    public void clickCreatePost() {
        wait.until(ExpectedConditions.elementToBeClickable(createPostBtn)).click();
        System.out.println("[ACTION] Tapped Create post");
    }

    public void clickPostMedia() {
        wait.until(ExpectedConditions.elementToBeClickable(postMediaBtn)).click();
        System.out.println("[ACTION] Tapped Post media");
    }

    /**
     * Types the caption on the RN "Create Post" screen.
     *
     * NOTE: The captured xpath for this field resolves to an
     * android.widget.ScrollView (the RN multiline TextInput's scroll
     * wrapper), not the underlying EditText itself. Tapping it DOES focus
     * the real input and opens the keyboard, but calling sendKeys()/setValue
     * directly on a ScrollView throws InvalidElementStateException
     * ("Cannot set the element..."). Fix: tap to focus, then type into
     * whatever now has focus via Appium's "mobile: type" - this works
     * regardless of which exact node ends up focused. Direct sendKeys is
     * still tried first as a cheap fast-path in case the locator ever
     * resolves to the real EditText on a future app build.
     */
    public void enterCaptionText(String text) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(captionInput));
        field.click();
        sleepQuiet(300);
        try {
            field.sendKeys(text);
        } catch (org.openqa.selenium.InvalidElementStateException e) {
            System.out.println("[WARN] Direct sendKeys failed on caption field ("
                    + e.getMessage().split("\n")[0]
                    + ") - falling back to mobile: type on the focused element");
            ((JavascriptExecutor) driver).executeScript("mobile: type",
                    ImmutableMap.of("text", text));
        }
        System.out.println("[INPUT] Caption text: '" + text + "'");
    }

    public void clickFinalPost() {
        wait.until(ExpectedConditions.elementToBeClickable(finalPostBtn)).click();
        System.out.println("[ACTION] Tapped final Post submit");
    }

    /**
     * Reads the success toast text and asserts it contains the expected
     * confirmation phrase. Fails the test (not silently) if the toast never
     * appears within the wait window.
     *
     * NOT currently called from postImageWithCaption() - the captured
     * successToast xpath was coming back with empty text on device, most
     * likely resolving to a wrapper View rather than the actual TextView
     * holding the toast copy (same class of issue as the caption field's
     * ScrollView-vs-EditText mismatch). Left here, callable directly, for
     * whoever re-verifies the locator against a live toast.
     */
    public void verifyPostUploadedToast() {
        WebElement toast = wait.until(ExpectedConditions.visibilityOf(successToast));
        String toastText = toast.getText();
        System.out.println("[INFO] Toast text: '" + toastText + "'");
        Assert.assertTrue(toastText.contains("Post uploaded successfully"),
                "Expected toast to contain 'Post uploaded successfully' but was: '"
                        + toastText + "'");
        System.out.println("[ASSERT PASS] Post uploaded successfully toast confirmed");
    }

    /** Tap any element by a raw xpath (used for the GIF/background sub-flow). */
    public void clickByXpath(String xpath, String label) {
        wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(xpath))).click();
        System.out.println("[ACTION] Tapped: " + label);
    }

    /**
     * Hard-asserts the in-app "Post uploaded successfully." confirmation banner
     * (a real TextView in the app hierarchy, reliably catchable - distinct from
     * the older, flaky successToast wrapper). Fails the test if it never appears.
     */
    public void verifyPostUploadedBanner() {
        WebElement banner = wait.until(ExpectedConditions.visibilityOfElementLocated(
                AppiumBy.xpath("//android.widget.TextView[@text=\"Post uploaded successfully.\"]")));
        System.out.println("[INFO] Banner text: '" + banner.getText() + "'");
        Assert.assertTrue(banner.isDisplayed(),
                "Expected 'Post uploaded successfully.' banner to be visible after posting");
        System.out.println("[ASSERT PASS] 'Post uploaded successfully.' banner confirmed");
    }

    /**
     * Robust end-of-post verification, run immediately after the final Post tap.
     *
     * Two phases:
     *   Phase 1 - track the upload stage TextView (com.dogpack:id/uploadStageTitle),
     *             which cycles through: "Getting Ready" -> "Uploading" ->
     *             "Publishing" -> "Posted!". We poll (moderate interval) and log
     *             each distinct stage seen, until it reads "Posted!" OR the element
     *             disappears (either = upload finished), capped by a runaway guard.
     *   Phase 2 - the instant the stage completes, fast-poll for the in-app banner
     *             //TextView[@text="Post uploaded successfully."] over a short
     *             window (the banner is transient, so we look for it right when it
     *             is due).
     *
     * Failure policy (per request): every check is best-effort - a failure is
     * FETCHED and logged as [FAIL] but does NOT abort or skip the remaining
     * actions in this flow. Any failures are aggregated and surfaced ONCE at the
     * very end so the test still goes red, without skipping steps.
     *
     * NOTE: this method never throws mid-flow; only the final aggregate check
     * (called separately by the flow) decides pass/fail.
     */
    private static final String STAGE_XPATH =
            "//android.widget.TextView[@resource-id=\"com.dogpack:id/uploadStageTitle\"]";
    private static final String BANNER_XPATH =
            "//android.widget.TextView[@text=\"Post uploaded successfully.\"]";
    /** Lightweight, punctuation/whitespace-tolerant probe for the "Posted!" state. */
    private static final String POSTED_XPATH =
            "//android.widget.TextView[@resource-id=\"com.dogpack:id/uploadStageTitle\""
            + " and contains(@text,\"Posted\")]";

    /** Single fast presence probe by xpath (one findElements, no getText). */
    private boolean elementPresent(String xpath) {
        try {
            java.util.List<WebElement> els = driver.findElements(AppiumBy.xpath(xpath));
            return !els.isEmpty() && els.get(0).isDisplayed();
        } catch (Exception ignore) {
            return false;
        }
    }

    /** Reads the current upload-stage text, or null if the element isn't present. */
    private String readStageText() {
        try {
            java.util.List<WebElement> els = driver.findElements(AppiumBy.xpath(STAGE_XPATH));
            if (!els.isEmpty()) {
                return els.get(0).getText();
            }
        } catch (Exception ignore) { /* transient - treat as not-present */ }
        return null;
    }

    /**
     * Phase 1: poll the stage title until "Posted!" or the element disappears.
     * @return true if a terminal state ("Posted!" or element-gone after progress)
     *         was observed; false if the runaway guard elapsed first.
     */
    /**
     * Best-effort dismissal of the intermittent 5-step "Customize your Feed"
     * onboarding tour that can appear after posting and overlay the screen.
     *
     * One Skip tap dismisses the entire tour. The tour is RN-rendered (its text
     * is not in the native app source), so we anchor on the Skip control:
     * accessibility-id "Skip" (primary) with content-desc xpath as fallback.
     *
     * Non-fatal: never throws. Returns true only if a Skip was tapped. Because
     * the tour is intermittent and optional, its absence is NOT a failure.
     */
    private boolean dismissOnboardingTourIfPresent() {
        try {
            java.util.List<WebElement> skip = driver.findElements(AppiumBy.accessibilityId("Skip"));
            if (skip.isEmpty()) {
                skip = driver.findElements(
                        AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"Skip\"]"));
            }
            if (!skip.isEmpty() && skip.get(0).isDisplayed()) {
                skip.get(0).click();
                System.out.println("[FLOW] Onboarding tour detected -> tapped Skip "
                        + "(dismisses whole 5-step tour)");
                sleepQuiet(800);
                return true;
            }
        } catch (Exception e) {
            System.out.println("[WARN] Onboarding-tour dismiss attempt threw (ignored): "
                    + e.getMessage());
        }
        return false;
    }

    /**
     * Explicit onboarding-tour dismissal, run right after the final Post tap and
     * before stage tracking. The 5-step "Customize your Feed" tour appears
     * intermittently just after posting; one Skip clears all of it.
     *
     * To avoid delaying tour-free runs, this polls briefly for EITHER the Skip
     * control (tour present -> tap it, done) OR the first upload stage (tour
     * absent -> upload already progressing, return immediately). Best-effort;
     * never throws.
     */
    public void dismissOnboardingTourAfterPost() {
        System.out.println("[FLOW] Checking for onboarding tour after final Post ...");
        long windowMs = 6000;
        long pollMs   = 300;
        long deadline = System.currentTimeMillis() + windowMs;
        while (System.currentTimeMillis() < deadline) {
            if (dismissOnboardingTourIfPresent()) {
                return;  // tour found and skipped
            }
            // If an upload stage is already visible, no tour is blocking - stop waiting.
            if (readStageText() != null) {
                System.out.println("[FLOW] Upload stage visible - no onboarding tour, proceeding");
                return;
            }
            sleepQuiet(pollMs);
        }
        System.out.println("[FLOW] No onboarding tour appeared within "
                + (windowMs / 1000) + "s - proceeding");
    }

    /** Single-shot check: is the success banner currently displayed? */
    private boolean isBannerPresent() {
        try {
            java.util.List<WebElement> els = driver.findElements(AppiumBy.xpath(BANNER_XPATH));
            return !els.isEmpty() && els.get(0).isDisplayed();
        } catch (Exception ignore) {
            return false;
        }
    }

    /**
     * Unified upload verification, run after the final Post tap (and after the
     * onboarding-tour Skip step). Tracks the upload stage AND watches for the
     * Two phases:
     *   Phase 1 - track stages at a moderate poll, logging transitions, until we
     *             reach "Publishing" (the stage right before completion).
     *   Phase 2 - at "Publishing", run a TIGHT fast-retry loop that prioritises
     *             catching the brief "Posted!" state: a single lightweight probe
     *             (one findElements, no getText, contains(@text,"Posted")) checked
     *             every iteration at high frequency. The banner and the
     *             stage-disappearance check run less often so "Posted!" gets the
     *             highest sampling rate. "Posted!" is the robust primary signal;
     *             the banner is the secondary confirmation.
     *
     * Success policy: PASS if EITHER "Posted!" is observed OR the banner is seen.
     * FAIL only if neither positive signal is seen.
     *
     * @return 0 on pass, 1 on failure (so the caller can surface one aggregate
     *         assertion without aborting/skipping).
     */
    public int verifyPostUploadEndFlow() {
        System.out.println("[FLOW] Verifying upload: tracking stages, fast-retrying for Posted! ...");
        long guardMs = 90000;   // runaway guard only
        long deadline = System.currentTimeMillis() + guardMs;

        String last = null;
        boolean sawAnyStage = false;
        boolean sawPosted = false;
        boolean bannerSeen = false;
        boolean reachedPublishing = false;

        try {
            // --- Phase 1: track stages (moderate poll) until Publishing ---------
            long stagePollMs = 400;
            while (System.currentTimeMillis() < deadline) {
                String cur = readStageText();
                if (cur != null && !cur.equals(last)) {
                    System.out.println("[STAGE] " + cur);
                    last = cur;
                    sawAnyStage = true;
                }
                if (cur != null) {
                    String c = cur.trim();
                    if (c.equalsIgnoreCase("Posted!")) {   // caught it already
                        sawPosted = true;
                        System.out.println("[FLOW] Upload reached terminal stage: Posted!");
                        break;
                    }
                    if (c.equalsIgnoreCase("Publishing")) {
                        reachedPublishing = true;
                        break;   // hand off to the tight Posted! retry loop
                    }
                }
                if (cur == null && sawAnyStage) {
                    // Stage vanished before we ever saw Publishing/Posted! - go
                    // straight to a banner grace-watch below.
                    System.out.println("[FLOW] uploadStageTitle disappeared after progress "
                            + "(last seen: '" + last + "') before Publishing");
                    break;
                }
                sleepQuiet(stagePollMs);
            }

            // --- Phase 2: tight fast-retry for Posted! (prioritised) ------------
            if (!sawPosted) {
                if (reachedPublishing) {
                    System.out.println("[FLOW] At Publishing - fast-retrying for 'Posted!' ...");
                }
                long tightMs = 60;      // very fast retries
                long graceMs = 8000;    // banner grace after stage completes
                long completedAt = 0;
                int iter = 0;
                while (System.currentTimeMillis() < deadline) {
                    // Priority 1 (every iteration): the brief "Posted!" state.
                    if (elementPresent(POSTED_XPATH)) {
                        sawPosted = true;
                        System.out.println("[FLOW] Upload reached terminal stage: Posted!");
                        break;
                    }
                    // Priority 2 (less often): banner + completion check, so the
                    // Posted! probe keeps the highest sampling rate.
                    if (iter % 4 == 0) {
                        if (isBannerPresent()) {
                            bannerSeen = true;
                            System.out.println("[ASSERT PASS] 'Post uploaded successfully.' banner confirmed");
                            break;
                        }
                        if (!elementPresent(STAGE_XPATH)) {
                            if (completedAt == 0) {
                                completedAt = System.currentTimeMillis();
                                System.out.println("[FLOW] uploadStageTitle disappeared - "
                                        + "grace-watching for banner");
                            }
                            if (System.currentTimeMillis() - completedAt > graceMs) {
                                break;
                            }
                        }
                    }
                    iter++;
                    sleepQuiet(tightMs);
                }
            }
        } catch (Exception e) {
            System.out.println("[FAIL] Upload verification threw: " + e.getMessage());
        }

        boolean pass = sawPosted || bannerSeen;
        System.out.println("[FLOW] End-verification summary: sawPosted=" + sawPosted
                + ", bannerSeen=" + bannerSeen + ", pass=" + pass
                + " (last stage seen: '" + last + "')");
        if (!pass) {
            System.out.println("[FAIL] Neither 'Posted!' stage nor success banner was observed");
            return 1;
        }
        return 0;
    }

    /**
     * Full end-to-end flow: tap +, switch to Text mode, attach an image,
     * caption it, and submit - then verify the upload success toast.
     */
    public void postImageWithCaption(String captionText) throws InterruptedException {
        testStart("postImageWithCaption");
        try {
            step(1, "Tap + (add-feed)");
            clickAddFeedIcon();
            sleepQuiet(1000);

            step(2, "Dismiss camera + audio permission dialogs (Camera screen first launch)");
            dismissAllPermissionDialogs(3);

            step(3, "Switch to Text mode");
            switchToTextMode();
            sleepQuiet(500);

            step(4, "Tap media image icon");
            clickMediaImageIcon();
            sleepQuiet(1000);

            step(5, "Select image from gallery grid");
            selectImageInGallery();
            sleepQuiet(500);

            step(6, "Tap Done");
            clickDone();
            sleepQuiet(800);

            step(7, "Tap GIF");
            clickByXpath("//android.widget.TextView[@text=\"GIF\"]", "GIF");
            sleepQuiet(800);

            step(8, "Tap a GIF option from the grid (3rd Sticker)");
            clickByXpath("(//android.widget.ImageView[@content-desc=\"Sticker\"])[3]",
                    "GIF option [3]");
            sleepQuiet(800);

            step(9, "Tap Background color");
            clickByXpath("//android.widget.FrameLayout[@content-desc=\"Background color\"]",
                    "Background color");
            sleepQuiet(600);

            step(10, "Select Yellow color");
            clickByXpath("//android.widget.HorizontalScrollView/android.widget.LinearLayout"
                    + "/android.view.View[7]", "Yellow color");
            sleepQuiet(600);

            step(11, "Tap OK");
            clickByXpath("//android.widget.Button[@resource-id=\"android:id/button1\"]", "OK");
            sleepQuiet(800);

            step(12, "Tap Create post");
            clickCreatePost();
            sleepQuiet(1000);

            step(13, "Tap Post media (single-item preview submit)");
            clickPostMedia();
            sleepQuiet(1500);

            step(14, "Enter caption text on Create Post screen");
            enterCaptionText(captionText);
            sleepQuiet(500);

            step(15, "Tap final Post submit");
            clickFinalPost();
            sleepQuiet(1500);

            step(16, "Dismiss onboarding tour if it appears (tap Skip)");
            dismissOnboardingTourAfterPost();

            step(17, "Verify upload stages -> 'Posted!' then catch success banner");
            int verifyFailures = verifyPostUploadEndFlow();

            // Aggregate the outcome ONCE, as the final action - every check above
            // already ran (nothing skipped/aborted). If any failed, fail the test
            // now so it goes red without having skipped steps.
            Assert.assertEquals(verifyFailures, 0,
                    "Post-upload end verification had " + verifyFailures
                    + " failed check(s) - see [FAIL] log lines above");
        } finally {
            testEnd("postImageWithCaption");
        }
    }
}