package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rahulshettyacademy.utils.AndroidActions;
import org.testng.Assert;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

/**
 * Page Object for the NEW native (com.dogpack.mediaediting) Photo capture
 * camera screen (CameraFragment / fragment_camera.xml).
 *
 * SCOPE: This is a self-contained, standalone page object for the native
 * Photo module. It deliberately does NOT import, extend, share, or copy any
 * code from the old React Native Add Post automation
 * (PostingExperiencePage / Dogpack_PostingExperience*), which runs against the
 * obsolete pre-migration composer and must keep running unchanged. It also
 * does not couple to AddPostPage (the native text-post page object) - any
 * helper it needs is copied in-file so this TC can be run, broken, and fixed
 * in complete isolation.
 *
 * Flow covered by THIS first slice (camera-controls smoke, stops before
 * capture) - Inspector-verified locators:
 *
 *   1. Tap "+" bottom-tab            -> content-desc "add-feed"
 *   2. Tap "No Effect" carousel item -> text "No Effect"
 *   3. Assert Magic-credits view exists -> id com.dogpack:id/llCreditView
 *   4. Tap Timer button              -> content-desc "Timer"
 *   5. Assert timer label now shows "3s" (cycle is 0 -> 3 -> 5 -> 10; the
 *      source sets the label text as "${seconds}s", so one tap yields "3s")
 *
 * NOTE on step 4: content-desc "Timer" is on the timer ICON (ImageView); the
 * actual click handler lives on its parent LinearLayout (id btnTimer). Using
 * the Inspector-captured ImageView locator here as given; if the tap does not
 * cycle the timer (click not bubbling to the parent), fall back to the
 * resource-id com.dogpack:id/btnTimer.
 */
public class AddPhotoPostPage extends AndroidActions {

    AndroidDriver driver;
    WebDriverWait wait;

    public AddPhotoPostPage(AndroidDriver driver) {
        super(driver);
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    // ================================================================
    // ===================  LOCATORS  =================================
    // ================================================================

    /** STEP 1 - Home bottom-tab "+" (add-feed) that launches the camera. */
    @AndroidFindBy(xpath = "//android.view.View[@content-desc=\"add-feed\"]")
    private WebElement addFeedBtn;

    /** STEP 2 - "No Effect" item in the bottom effect carousel. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text=\"No Effect\"]")
    private WebElement noEffectItem;

    /** STEP 4 - Timer button icon on the camera top bar. */
    @AndroidFindBy(xpath = "//android.widget.ImageView[@content-desc=\"Timer\"]")
    private WebElement timerBtn;

    /** STEP 5 - Timer value label that shows the selected duration (e.g. "3s"). */
    @AndroidFindBy(id = "com.dogpack:id/timerValueLabel")
    private WebElement timerValueLabel;

    /** STEP 12/14/16 - Flash button on the camera top bar. */
    @AndroidFindBy(xpath = "//android.widget.ImageView[@content-desc=\"Flash\"]")
    private WebElement flashBtn;

    /** STEP 18 - Aspect ratio button (opens the ratio selector popup). */
    @AndroidFindBy(xpath = "//android.widget.TextView[@content-desc=\"Aspect ratio\"]")
    private WebElement aspectRatioBtn;

    /** STEP 19 - "1:1" option inside the aspect ratio selector popup. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text=\"1:1\"]")
    private WebElement ratio1x1Option;

    /** STEP 20 - Camera preview render surface (diagnostic read). */
    @AndroidFindBy(xpath = "//android.view.TextureView")
    private WebElement previewTextureView;

    /** STEP 21 - Switch camera (front/back) button on the camera top bar. */
    @AndroidFindBy(id = "com.dogpack:id/btnSwitchCamera")
    private WebElement switchCameraBtn;

    // Locator strings used with raw findElements (existence checks / fallbacks).
    private static final String CREDIT_VIEW_ID = "com.dogpack:id/llCreditView";

    // ================================================================
    // ===================  DIAGNOSTIC LOGGING  =======================
    // ================================================================
    // Same greppable markers as the rest of the suite:
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
    // Copied in-file (NOT imported) so this page object stays standalone.
    // The camera screen fires camera+audio permission prompts on a fresh
    // app-data run; both appear as "While using the app" dialogs.

    private boolean clickRawByLocator(org.openqa.selenium.By locator, String label) {
        try {
            List<WebElement> found = driver.findElements(locator);
            if (!found.isEmpty() && found.get(0).isDisplayed()) {
                found.get(0).click();
                System.out.println("[ACTION] Clicked: " + label);
                return true;
            }
        } catch (Exception ignore) { /* not present */ }
        return false;
    }

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
        if (clicked) {
            System.out.println("[FLOW] Dismissed a permission dialog");
        }
        return clicked;
    }

    private void dismissAllPermissionDialogs(int maxRounds) {
        for (int i = 0; i < maxRounds; i++) {
            boolean any = dismissAnyPermissionDialog();
            sleepQuiet(1200);
            if (!any && i > 0) break;
        }
    }

    // ================================================================
    // ===================  FLOW STEPS  ===============================
    // ================================================================

    public void clickAddFeedIcon() {
        wait.until(ExpectedConditions.elementToBeClickable(addFeedBtn)).click();
        System.out.println("[ACTION] Tapped + (add-feed)");
    }

    public void clickNoEffect() {
        wait.until(ExpectedConditions.elementToBeClickable(noEffectItem)).click();
        System.out.println("[ACTION] Tapped No Effect carousel item");
    }

    /**
     * Asserts the Magic-credits container is present on the camera screen.
     * Uses raw findElements so a genuine absence is reported as a failed
     * assertion rather than a wait timeout.
     */
    public void verifyMagicCreditViewExists() {
        List<WebElement> found = driver.findElements(
                org.openqa.selenium.By.id(CREDIT_VIEW_ID));
        boolean present = !found.isEmpty();
        System.out.println("[INFO] Magic credit view (" + CREDIT_VIEW_ID
                + ") present: " + present);
        Assert.assertTrue(present,
                "Expected Magic credit view '" + CREDIT_VIEW_ID
                        + "' to exist on the camera screen, but it was not found.");
        System.out.println("[ASSERT PASS] Magic credit view exists");
    }

    public void clickTimerButton() {
        wait.until(ExpectedConditions.elementToBeClickable(timerBtn)).click();
        System.out.println("[ACTION] Tapped Timer button");
    }

    /**
     * Asserts the timer label reads "3s" after one tap. The source sets the
     * label as "${seconds}s" (cycle 0 -> 3 -> 5 -> 10), so a single tap from
     * the default (0 / hidden) yields "3s". Accepts "3s" or "3 seconds" to be
     * tolerant of copy differences across builds.
     */
    public void verifyTimerIsThreeSeconds() {
        verifyTimerContains("3s", "3 seconds");
    }

    /**
     * Asserts the timer label contains one of the accepted values. Kept generic
     * so each tap in the cycle (3s -> 5s -> 10s) can reuse it.
     */
    public void verifyTimerContains(String... accepted) {
        WebElement label = wait.until(
                ExpectedConditions.visibilityOf(timerValueLabel));
        String text = label.getText();
        System.out.println("[INFO] Timer label text: '" + text + "'");
        boolean ok = false;
        StringBuilder wanted = new StringBuilder();
        for (String a : accepted) {
            wanted.append("'").append(a).append("' ");
            if (text != null && text.contains(a)) { ok = true; }
        }
        Assert.assertTrue(ok,
                "Expected timer label to contain one of: " + wanted.toString().trim()
                        + " but was: '" + text + "'");
        System.out.println("[ASSERT PASS] Timer shows " + accepted[0]);
    }

    /**
     * Asserts the timer has cycled back to its "no value" state. After 10s the
     * next tap sets timerSeconds=0, which the source renders as an empty label
     * text AND sets the label visibility to GONE. So we do NOT wait for
     * visibility here (it would time out) - instead we read the element raw via
     * findElements and assert it is either absent, not displayed, or empty.
     */
    public void verifyTimerBackToNoValue() {
        List<WebElement> found = driver.findElements(
                org.openqa.selenium.By.id("com.dogpack:id/timerValueLabel"));
        if (found.isEmpty()) {
            System.out.println("[INFO] Timer label element not present (GONE) - treated as no value");
            System.out.println("[ASSERT PASS] Timer is back to no value (element gone)");
            return;
        }
        WebElement label = found.get(0);
        boolean displayed;
        String text;
        try {
            displayed = label.isDisplayed();
        } catch (Exception e) {
            displayed = false;
        }
        try {
            text = label.getText();
        } catch (Exception e) {
            text = "";
        }
        System.out.println("[INFO] Timer label displayed=" + displayed
                + " text='" + text + "'");
        boolean noValue = !displayed || text == null || text.trim().isEmpty();
        Assert.assertTrue(noValue,
                "Expected timer to be back to no value (empty/hidden) after cycling "
                        + "past 10s, but label was displayed with text: '" + text + "'");
        System.out.println("[ASSERT PASS] Timer is back to no value");
    }

    // ================================================================
    // ===================  FLASH (diagnostic)  =======================
    // ================================================================
    // NOTE: The flash button carries NO changing text and its content-desc
    // stays "Flash" across all states. updateFlashIcon() only swaps the
    // drawable (ic_flash / ic_flash_on / ic_flash_auto) and changes alpha
    // (photo-mode cycle is OFF -> ON -> AUTO -> OFF). None of that is a
    // readable string via Appium. So per the request ("throw the text it
    // shows / throw in the action logs"), these are DIAGNOSTIC dumps, not
    // assertions - they log whatever the element actually exposes so we can
    // see if anything is observable after each transition.

    public void clickFlash() {
        wait.until(ExpectedConditions.elementToBeClickable(flashBtn)).click();
        System.out.println("[ACTION] Tapped Flash button");
    }

    /** Logs every observable attribute of the flash element after a transition. */
    public void dumpFlashState(String afterTapLabel) {
        List<WebElement> found = driver.findElements(
                AppiumBy.xpath("//android.widget.ImageView[@content-desc=\"Flash\"]"));
        if (found.isEmpty()) {
            System.out.println("[FLASH " + afterTapLabel + "] element not found");
            return;
        }
        WebElement f = found.get(0);
        String text = safeAttr(f, "text");
        String desc = safeAttr(f, "content-desc");
        String selected = safeAttr(f, "selected");
        String checked = safeAttr(f, "checked");
        String enabled = safeAttr(f, "enabled");
        System.out.println("[FLASH " + afterTapLabel + "] text='" + text
                + "' content-desc='" + desc + "' selected=" + selected
                + " checked=" + checked + " enabled=" + enabled);
    }

    // ================================================================
    // ===================  ASPECT RATIO  =============================
    // ================================================================

    public void clickAspectRatio() {
        wait.until(ExpectedConditions.elementToBeClickable(aspectRatioBtn)).click();
        System.out.println("[ACTION] Tapped Aspect ratio button");
    }

    public void selectRatio1x1() {
        wait.until(ExpectedConditions.elementToBeClickable(ratio1x1Option)).click();
        System.out.println("[ACTION] Selected 1:1 ratio");
    }

    /**
     * Selects an aspect ratio option from the (already open) selector popup by
     * its exact visible label, e.g. "3:4", "9:4", "9:16", "Full".
     */
    public void selectRatioOption(String optionLabel) {
        WebElement opt = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath("//android.widget.TextView[@text=\"" + optionLabel + "\"]")));
        opt.click();
        System.out.println("[ACTION] Selected " + optionLabel + " ratio");
    }

    /**
     * Verifies the 1:1 ratio was actually applied. On selection the source
     * calls updateAspectRatioButton(), which sets the aspect button's TEXT to
     * the chosen label ("1:1"); the button's content-desc stays the static
     * "Aspect ratio" (set once at view creation, never overwritten). So we
     * locate the button by its unchanged content-desc and read its text.
     */
    public void verifyRatioIs1x1() {
        verifyRatioButtonText("1:1");
    }

    /**
     * Generic assert that the aspect button's text reflects the applied ratio.
     * The button is always located by its unchanged content-desc "Aspect
     * ratio"; only its text attribute changes to the selected label.
     */
    public void verifyRatioButtonText(String expectedLabel) {
        List<WebElement> found = driver.findElements(
                AppiumBy.xpath("//android.widget.TextView[@content-desc=\"Aspect ratio\"]"));
        Assert.assertFalse(found.isEmpty(),
                "Aspect ratio button (content-desc='Aspect ratio') not found after "
                        + "selecting " + expectedLabel + ".");
        String text = safeAttr(found.get(0), "text");
        System.out.println("[INFO] Aspect ratio button text after selection: '" + text + "'");
        Assert.assertEquals(text, expectedLabel,
                "Expected aspect ratio button text to read '" + expectedLabel
                        + "' after selection, but was: '" + text + "'");
        System.out.println("[ASSERT PASS] " + expectedLabel + " aspect ratio applied");
    }

    /**
     * Convenience helper: open the aspect selector, pick the given label, and
     * assert the button text updated to it. Combines the open + select + verify
     * cycle used for each ratio.
     */
    public void changeAndVerifyRatio(String optionLabel) {
        clickAspectRatio();
        sleepQuiet(600);
        selectRatioOption(optionLabel);
        sleepQuiet(600);
        verifyRatioButtonText(optionLabel);
    }

    // ================================================================
    // ===================  SWITCH CAMERA  ============================
    // ================================================================
    // NOTE: no verification yet. currentFacing (BACK <-> FRONT) lives in
    // CameraController as an in-process Kotlin field and is NOT exposed on any
    // UI attribute, so Appium cannot read it. Verification is deferred until
    // the dev reflects currentFacing onto a UI attribute (e.g. the button's
    // content-desc), after which we can assert front/back here.

    public void clickSwitchCamera() {
        wait.until(ExpectedConditions.elementToBeClickable(switchCameraBtn)).click();
        System.out.println("[ACTION] Tapped Switch Camera button");
    }

    // ================================================================
    // ===================  PREVIEW SURFACE (diagnostic)  =============
    // ================================================================
    // The preview is an androidx.camera.view.PreviewView, which renders via
    // an internal SurfaceView or TextureView depending on implementation mode.
    // //android.view.TextureView MAY resolve to that internal child, but a
    // TextureView exposes no text/content-desc (it's a raw render surface), so
    // this is a DIAGNOSTIC read of whatever it exposes, not an assertion.

    public void dumpTextureViewValue() {
        List<WebElement> found = driver.findElements(
                AppiumBy.xpath("//android.view.TextureView"));
        if (found.isEmpty()) {
            System.out.println("[TEXTUREVIEW] //android.view.TextureView NOT found "
                    + "(PreviewView likely rendering via SurfaceView, not TextureView)");
            return;
        }
        WebElement tv = found.get(0);
        String text = safeAttr(tv, "text");
        String desc = safeAttr(tv, "content-desc");
        String cls = safeAttr(tv, "class");
        System.out.println("[TEXTUREVIEW] found -> text='" + text
                + "' content-desc='" + desc + "' class='" + cls + "'");
    }

    /** Reads an attribute without throwing; returns "" on any error. */
    private String safeAttr(WebElement el, String name) {
        try {
            String v = el.getAttribute(name);
            return v == null ? "" : v;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * First Photo-module slice: camera-controls smoke test. Stops before
     * capture - taps +, selects No Effect, confirms the credits view exists,
     * taps the timer, and asserts it cycled to 3s.
     */
    public void runCameraTopControls() {
        testStart("runCameraTopControls");
        try {
            step(1, "Tap + (add-feed) to open the camera");
            clickAddFeedIcon();
            sleepQuiet(1000);

            step(2, "Dismiss camera + audio permission dialogs (first launch)");
            dismissAllPermissionDialogs(3);

            step(3, "Tap No Effect carousel item");
            clickNoEffect();
            sleepQuiet(500);

            step(4, "Assert Magic credit view exists");
            verifyMagicCreditViewExists();

            step(5, "Tap Timer button");
            clickTimerButton();
            sleepQuiet(500);

            step(6, "Assert timer label shows 3s");
            verifyTimerIsThreeSeconds();

            step(7, "Tap Timer again -> expect 5s");
            clickTimerButton();
            sleepQuiet(500);
            verifyTimerContains("5s", "5 seconds");

            step(8, "Tap Timer again -> expect 10s");
            clickTimerButton();
            sleepQuiet(500);
            verifyTimerContains("10s", "10 seconds");

            step(9, "Tap Timer again -> expect back to no value (0 / hidden)");
            clickTimerButton();
            sleepQuiet(500);
            verifyTimerBackToNoValue();

            step(10, "Tap Flash (transition 1) and log what it exposes");
            clickFlash();
            sleepQuiet(600);
            dumpFlashState("after tap 1");

            step(11, "Tap Flash (transition 2) and log what it exposes");
            clickFlash();
            sleepQuiet(600);
            dumpFlashState("after tap 2");

            step(12, "Tap Flash (transition 3) and log what it exposes");
            clickFlash();
            sleepQuiet(600);
            dumpFlashState("after tap 3");
        } finally {
            testEnd("runCameraTopControls");
        }
    }

    // ================================================================
    // ===================  SIDE CONTROLS (left toolbar)  =============
    // ================================================================
    // The left/side toolbar lists editing tools (beauty, filter, bg, layout,
    // fx ...) each rendered from item_camera_tool.xml. Every tool's ICON
    // carries the SAME content-desc "Editing tool" (me_acc_editor_tool), which
    // is why an index is needed to disambiguate. In photo mode the enabled
    // tools are beauty/filter/bg/layout/fx (5), followed by the collapse/expand
    // chevron - so (content-desc="Editing tool")[6] is the EXPAND control that
    // reveals the collapsed tool labels. Each tool's text label lives in the
    // TextView com.dogpack:id/cameraToolLabelStart (e.g. "Beauty").
    //
    // NOTE: the [6] index is config/mode dependent (enabled tools, image/video
    // editing flags). Captured live from Inspector; if the toolbar composition
    // changes, this index must be re-verified.

    /** Expand control of the left toolbar (6th "Editing tool" icon). */
    @AndroidFindBy(xpath = "(//android.widget.ImageView[@content-desc=\"Editing tool\"])[6]")
    private WebElement editingToolExpand;

    /** "Beauty" tool label (start-label TextView in the expanded toolbar). */
    @AndroidFindBy(xpath = "//android.widget.TextView[@resource-id=\"com.dogpack:id/cameraToolLabelStart\" and @text=\"Beauty\"]")
    private WebElement beautyToolLabel;

    public void clickEditingToolExpand() {
        wait.until(ExpectedConditions.elementToBeClickable(editingToolExpand)).click();
        System.out.println("[ACTION] Tapped Editing tool expand (side toolbar)");
    }

    /**
     * Taps the Filter tool icon (2nd "Editing tool" icon in the collapsed
     * toolbar) to TOGGLE the filter panel closed. Needed at the start of the
     * BackgroundFilter flow because the preceding filter test leaves the filter
     * panel open over the collapsed toolbar - in that state the expand chevron
     * won't reveal the labels until the filter panel is dismissed. Index-based
     * on content-desc "Editing tool" and therefore state-sensitive (captured
     * live from Inspector in the collapsed state).
     */
    public void clickFilterIconToClosePanel() {
        wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath("(//android.widget.ImageView[@content-desc="
                        + "\"Editing tool\"])[2]"))).click();
        System.out.println("[ACTION] Tapped Filter icon [2] to close filter panel");
    }

    public void clickBeautyControl() {
        wait.until(ExpectedConditions.elementToBeClickable(beautyToolLabel)).click();
        System.out.println("[ACTION] Tapped Beauty control");
    }

    /**
     * Camera SIDE-controls flow (left editing toolbar). Assumes the camera is
     * already open on the Photo screen - it does NOT tap "+" or handle
     * permissions itself. In the suite it runs at priority 3, immediately after
     * CameraTopControls (priority 2), which opens the camera and leaves the app
     * on it. (Priority-only ordering by design - no dependsOnMethods - so if
     * CameraTopControls fails, this will fail at step 1 rather than skip.)
     */
    /**
     * Defensive camera-open used by the independently-runnable side-control
     * methods. If the "+" (add-feed) button is visible we're on the feed, so we
     * open the camera and clear first-run permission dialogs. If add-feed is NOT
     * present we're already inside the camera (e.g. a prior camera test ran just
     * before in the same suite), so we skip the open. This lets each side-control
     * method run standalone from the feed AND chained after another camera test.
     */
    private void ensureCameraOpen() {
        List<WebElement> addFeed = driver.findElements(
                org.openqa.selenium.By.xpath(
                        "//android.view.View[@content-desc=\"add-feed\"]"));
        boolean onFeed = !addFeed.isEmpty();
        if (onFeed) {
            try { onFeed = addFeed.get(0).isDisplayed(); } catch (Exception e) { onFeed = false; }
        }
        if (onFeed) {
            addFeed.get(0).click();
            System.out.println("[ACTION] Tapped + (add-feed) to open the camera");
            sleepQuiet(1000);
            dismissAllPermissionDialogs(3);
        } else {
            System.out.println("[INFO] Camera already open (add-feed not present) - skipping open");
        }
    }

    /**
     * CameraSideControls_BeautyAndFilters: Beauty control + the full filter
     * panel sweep (Color/Camera/Moment/Vintage/Location/Aesthetic/Atmosphere/
     * LUT/Artistic/Creative/Illusion categories and their filters, with the
     * horizontal category/thumbnail scrolls). Independently runnable - opens
     * the camera itself and expands the side toolbar before starting.
     */
    public void runCameraSideControls_BeautyAndFilters() {
        testStart("CameraSideControls_BeautyAndFilters");
        try {
            ensureCameraOpen();

            step(1, "Tap Editing tool expand (6th Editing tool icon)");
            clickEditingToolExpand();
            sleepQuiet(600);

            step(2, "Tap Beauty control");
            clickBeautyControl();
            sleepQuiet(600);

            step(3, "Tap Filter control");
            clickToolLabel("Filter");
            sleepQuiet(800);

            step(4, "Tap Color category");
            clickByText("Color");
            sleepQuiet(600);

            step(5, "Select Gingham filter (3rd Apply-filter thumbnail)");
            clickApplyFilterByIndex(3);
            sleepQuiet(600);

            step(6, "Tap Camera category");
            clickByText("Camera");
            sleepQuiet(600);

            step(7, "Select 4K filter");
            clickFilterName("4K");
            sleepQuiet(600);

            step(8, "Tap Moment category");
            clickByText("Moment");
            sleepQuiet(600);

            step(9, "Select Grain filter");
            clickFilterName("Grain");
            sleepQuiet(600);

            step(10, "Tap Vintage category (1st match)");
            clickByTextIndexed("Vintage", 1);
            sleepQuiet(600);

            step(11, "Select Super 8mm filter");
            clickFilterName("Super 8mm");
            sleepQuiet(600);

            step(12, "Tap Location category");
            clickByText("Location");
            sleepQuiet(600);

            step(13, "Select New York filter");
            clickFilterName("New York");
            sleepQuiet(600);

            step(14, "Scroll the filter category row (horizontal)");
            scrollFilterCategoryRow();
            sleepQuiet(600);

            step(15, "Tap Aesthetic category (1st match)");
            clickByTextIndexed("Aesthetic", 1);
            sleepQuiet(600);

            step(16, "Select Soft Glow filter");
            clickFilterName("Soft Glow");
            sleepQuiet(600);

            step(17, "Tap Atmosphere category");
            clickByText("Atmosphere");
            sleepQuiet(600);

            step(18, "Select Dust filter");
            clickFilterName("Dust");
            sleepQuiet(600);

            step(19, "Tap LUT category");
            clickByText("LUT");
            sleepQuiet(600);

            step(20, "Select Normal filter");
            clickFilterName("Normal");
            sleepQuiet(600);

            step(21, "Scroll the filter category row so Artistic is visible");
            scrollFilterCategoryRow();
            sleepQuiet(600);

            step(22, "Tap Artistic category");
            clickByText("Artistic");
            sleepQuiet(600);

            step(23, "Select Pixel filter");
            clickFilterName("Pixel");
            sleepQuiet(600);

            step(24, "Scroll the filter category row again (horizontal)");
            scrollFilterCategoryRow();
            sleepQuiet(600);

            step(25, "Tap Creative category");
            clickByText("Creative");
            sleepQuiet(600);

            step(26, "Select Neon filter");
            clickFilterName("Neon");
            sleepQuiet(600);

            step(27, "Tap Illusion category");
            clickByText("Illusion");
            sleepQuiet(600);

            step(28, "Scroll the filter thumbnail row (horizontal)");
            scrollFilterRecycler();
            sleepQuiet(600);

            step(29, "Select Prism filter");
            clickFilterName("Prism");
            sleepQuiet(600);
        } finally {
            testEnd("CameraSideControls_BeautyAndFilters");
        }
    }

    /**
     * CameraSideControls_BackgroundFilter: the BG (background) panel flow -
     * Blur/Color(+swatch)/Image(+media grid)/DONE, then re-open the toolbar and
     * BG panel and Clear. Independently runnable - opens the camera itself and
     * expands the side toolbar before starting.
     *
     * NOTE: steps 1-2 (ensureCameraOpen + Editing tool expand) are added so this
     * runs on its own; in a chained full-suite run those simply no-op / re-open
     * the already-visible toolbar. Your captured log started at "BG" because the
     * camera was already open and expanded from the preceding filter test.
     */
    public void runCameraSideControls_BackgroundFilter() {
        testStart("CameraSideControls_BackgroundFilter");
        try {
            ensureCameraOpen();

            step(1, "Tap Filter icon [2] to close leftover filter panel");
            clickFilterIconToClosePanel();
            sleepQuiet(600);

            // step(2, "Tap Editing tool expand (open side toolbar)");
            // clickEditingToolExpand();
            // sleepQuiet(600);
            // DISABLED: after closing the filter panel via Filter icon [2], the
            // side toolbar is already in the required state; the expand toggle
            // here would collapse it. Re-enable if the toolbar starts collapsed.

            step(3, "Tap Background (BG) tool");
            clickToolLabel("BG");
            sleepQuiet(800);

            step(4, "Tap Blur option");
            clickByText("Blur");
            sleepQuiet(600);

            step(5, "Tap Color option");
            clickByText("Color");
            sleepQuiet(600);

            step(6, "Select Yellow color swatch");
            clickByXpath("//android.widget.LinearLayout[@resource-id="
                    + "\"com.dogpack:id/bottomBar\"]/android.widget.LinearLayout"
                    + "/android.widget.LinearLayout[1]/android.view.View[3]",
                    "Yellow color swatch");
            sleepQuiet(600);

            step(7, "Tap Image option");
            clickByText("Image");
            sleepQuiet(800);

            step(8, "Select an image from the media grid");
            clickByXpath("//android.view.View[@content-desc=\"Media grid\"]"
                    + "/android.view.View/android.view.View[4]/android.view.View[2]"
                    + "/android.view.View",
                    "Media grid image");
            sleepQuiet(800);

            step(9, "Tap DONE");
            clickByXpath("//androidx.compose.ui.platform.ComposeView/android.view.View"
                    + "/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[5]/android.view.View/android.view.View[3]"
                    + "/android.widget.Button",
                    "DONE");
            sleepQuiet(800);

            // step(10, "Tap Editing tool expand again (re-open side toolbar)");
            // clickEditingToolExpand();
            // sleepQuiet(600);
            // DISABLED: the expand chevron (//ImageView[@content-desc="Editing
            // tool"])[6] tap here was failing after DONE. Re-enable if the
            // toolbar is confirmed collapsed at this point in a future build.

            step(11, "Tap Background (BG) tool again");
            clickToolLabel("BG");
            sleepQuiet(800);

            step(12, "Tap Background (BG) tool again (repeat)");
            clickToolLabel("BG");
            sleepQuiet(800);

            step(13, "Tap Clear filter");
            clickByText("Clear");
            sleepQuiet(600);
        } finally {
            testEnd("CameraSideControls_BackgroundFilter");
        }
    }

    /**
     * CameraSideControls_Layout_FX: the Layout tool (collage window options -
     * two/three/six windows) and the FX tool. Independently runnable - opens
     * the camera itself before starting. Window options are positional children
     * of the layout HorizontalScrollView (captured live from Inspector): View[2]
     * = two-window, View[4] = three-window, View[7] = six-window.
     */
    public void runCameraSideControls_Layout_FX() {
        testStart("CameraSideControls_Layout_FX");
        try {
            ensureCameraOpen();

            // step(1, "Tap Editing tool expand (open side toolbar)");
            // clickEditingToolExpand();
            // sleepQuiet(600);
            // DISABLED: the expand chevron (//ImageView[@content-desc="Editing
            // tool"])[6] tap was failing here (toolbar state at entry differs
            // from a fresh camera). Re-enable once a stable expand locator is
            // available for this state.

            step(2, "Tap Layout tool");
            clickToolLabel("Layout");
            sleepQuiet(800);

            step(3, "Tap Two-window layout");
            clickByXpath("//android.widget.HorizontalScrollView"
                    + "/android.widget.LinearLayout/android.view.View[2]",
                    "Two-window layout");
            sleepQuiet(600);

            step(4, "Tap Three-window layout");
            clickByXpath("//android.widget.HorizontalScrollView"
                    + "/android.widget.LinearLayout/android.view.View[4]",
                    "Three-window layout");
            sleepQuiet(600);

            step(5, "Tap Six-window layout");
            clickByXpath("//android.widget.HorizontalScrollView"
                    + "/android.widget.LinearLayout/android.view.View[7]",
                    "Six-window layout");
            sleepQuiet(600);

            step(6, "Tap Layout tool again");
            clickToolLabel("Layout");
            sleepQuiet(800);

            step(7, "Tap FX tool");
            clickToolLabel("FX");
            sleepQuiet(800);
        } finally {
            testEnd("CameraSideControls_Layout_FX");
        }
    }

    // ================================================================
    // ===================  POSTING FLOW HELPERS  ====================
    // ================================================================

    /**
     * Dismisses a Material BottomSheetDialog (e.g. the GIF/Stickers browser) with
     * a device Back press. The sheet is a BottomSheetDialogFragment, which
     * intercepts Back and dismisses cleanly - locator-free, so it avoids the
     * brittleness of tapping a positional child inside the sheet.
     */
    public void closeBottomSheetWithBack() {
        driver.navigate().back();
        System.out.println("[ACTION] Closed bottom sheet via device Back");
    }

    /** Tap an editor tool by its toolLabel id + text (e.g. "Filters", "Text"). */
    public void clickEditorToolLabel(String label) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath("//android.widget.TextView[@resource-id="
                        + "\"com.dogpack:id/toolLabel\" and @text=\"" + label + "\"]")));
        el.click();
        System.out.println("[ACTION] Tapped editor tool: " + label);
    }

    /**
     * Types into the SharePost caption field. That field is a React Native
     * input that resolves to a ScrollView, so a plain sendKeys can throw
     * InvalidElementStateException - we fall back to the mobile: type command
     * (same approach used for the native text-post caption).
     */
    public void enterCaption(String xpath, String text) {
        WebElement field = wait.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.xpath(xpath)));
        try {
            field.click();
        } catch (Exception ignore) { /* container may not be directly clickable */ }
        try {
            field.sendKeys(text);
            System.out.println("[ACTION] Entered caption via sendKeys: " + text);
        } catch (Exception e) {
            try {
                ((JavascriptExecutor) driver).executeScript("mobile: type",
                        Map.of("text", text));
                System.out.println("[ACTION] Entered caption via mobile:type: " + text);
            } catch (Exception e2) {
                System.out.println("[WARN] Caption entry failed both ways: "
                        + e2.getMessage());
            }
        }
    }

    /**
     * Defensively taps the Android runtime permission "Allow" button if the
     * dialog is present; skips silently if it is not (e.g. permission already
     * granted on a re-run), so it never hangs waiting for a dialog that will
     * not appear.
     */
    public void allowPermissionIfPresent() {
        boolean tapped = clickRawByLocator(org.openqa.selenium.By.id(
                "com.android.permissioncontroller:id/permission_allow_button"),
                "Allow (music/audio permission)");
        if (!tapped) {
            System.out.println("[INFO] No music/audio permission dialog present - skipping");
        }
    }

    /**
     * CameraSideControls_Posting: CONTINUATION test - it runs only immediately
     * after CameraSideControls_Layout_FX (priority order), which leaves the FX
     * panel open (its last action was "Tap FX"). This method does NOT open the
     * camera or re-navigate; it starts at "Tap Fire". Run standalone it will
     * fail at step 1 because the FX panel would not be open. This flow performs
     * a real post to the tip@yopmail.com feed.
     */
    public void runCameraSideControls_Posting() {
        testStart("CameraSideControls_Posting");
        try {
            step(1, "Tap Fire FX");
            clickByText("Fire");
            sleepQuiet(2000);

            step(2, "Tap shutter button (centered frameStrip item)");
            clickByXpath("//androidx.recyclerview.widget.RecyclerView"
                    + "[@resource-id=\"com.dogpack:id/frameStrip\"]"
                    + "/android.widget.FrameLayout",
                    "Shutter");
            sleepQuiet(1500);

            step(3, "Tap carousel button (Editing tool [4])");
            clickByXpath("(//android.widget.ImageView[@content-desc="
                    + "\"Editing tool\"])[4]", "Carousel [4]");
            sleepQuiet(800);

            step(4, "Tap Filters editor tool");
            clickEditorToolLabel("Filters");
            sleepQuiet(800);

            step(5, "Tap Adjust editor tool");
            clickEditorToolLabel("Adjust");
            sleepQuiet(800);

            step(6, "Tap Beauty editor tool");
            clickEditorToolLabel("Beauty");
            sleepQuiet(800);

            step(7, "Tap BG editor tool");
            clickEditorToolLabel("BG");
            sleepQuiet(800);

            step(8, "Tap Draw editor tool");
            clickEditorToolLabel("Draw");
            sleepQuiet(800);

            step(9, "Tap carousel button again (Editing tool [4])");
            clickByXpath("(//android.widget.ImageView[@content-desc="
                    + "\"Editing tool\"])[4]", "Carousel [4]");
            sleepQuiet(800);

            step(10, "Tap GIF/Stickers editor tool");
            clickEditorToolLabel("GIF/Stickers");
            sleepQuiet(800);

            step(11, "Close the GIF/Stickers bottom sheet (device Back)");
            closeBottomSheetWithBack();
            sleepQuiet(800);

            step(12, "Tap Image Gallery editor tool");
            clickEditorToolLabel("Image Gallery");
            sleepQuiet(1000);

            step(13, "Select an image from the media grid");
            clickByXpath("//android.view.View[@content-desc=\"Media grid\"]"
                    + "/android.view.View/android.view.View[4]/android.view.View[2]"
                    + "/android.view.View",
                    "Media grid image");
            sleepQuiet(1000);

            step(14, "Tap Preview");
            clickByXpath("//androidx.compose.ui.platform.ComposeView/android.view.View"
                    + "/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[5]/android.view.View/android.view.View[2]"
                    + "/android.widget.Button",
                    "Preview");
            sleepQuiet(1000);

            step(15, "Tap Back button");
            clickByXpath("//android.view.ViewGroup/android.view.View/android.view.View"
                    + "/android.view.View/android.view.View[1]/android.widget.Button",
                    "Back");
            sleepQuiet(1000);

            step(16, "Tap Done");
            clickByXpath("//androidx.compose.ui.platform.ComposeView/android.view.View"
                    + "/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[5]/android.view.View/android.view.View[3]"
                    + "/android.widget.Button",
                    "Done");
            sleepQuiet(1000);

            step(17, "Tap Text editor tool");
            clickEditorToolLabel("Text");
            sleepQuiet(800);

            step(18, "Enter text into the text tool");
            WebElement textField = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath("//android.widget.EditText[@text=\"Type your text\u2026\"]")));
            textField.click();
            textField.sendKeys("Test text in native Photo posting");
            System.out.println("[ACTION] Entered editor text");
            sleepQuiet(600);

            step(19, "Tap Add Text (2nd match)");
            clickByXpath("(//android.widget.TextView[@text=\"Add Text\"])[2]", "Add Text");
            sleepQuiet(800);

            step(20, "Tap Music editor tool");
            clickEditorToolLabel("Music");
            sleepQuiet(1000);

            step(21, "Allow music/audio permission if prompted");
            allowPermissionIfPresent();
            sleepQuiet(800);

            step(22, "Tap a soundtrack (4th trackUseBtn)");
            clickByXpath("(//android.widget.TextView[@resource-id="
                    + "\"com.dogpack:id/trackUseBtn\"])[4]", "Soundtrack");
            sleepQuiet(1000);

            step(23, "Tap Done (music dialog)");
            clickByXpath("//android.widget.Button[@resource-id=\"android:id/button1\"]",
                    "Music Done");
            sleepQuiet(1000);

            step(24, "Tap NEXT");
            clickByXpath("//android.widget.Button[@content-desc=\"Next\"]", "NEXT");
            sleepQuiet(1500);

            step(25, "Tap NEXT / btnPost");
            clickByXpath("//android.widget.Button[@resource-id=\"com.dogpack:id/btnPost\"]",
                    "btnPost");
            sleepQuiet(1500);

            step(26, "Enter caption text on the SharePost screen");
            enterCaption("//android.widget.FrameLayout[@resource-id=\"android:id/content\"]"
                    + "/android.widget.FrameLayout/android.view.ViewGroup"
                    + "/android.view.ViewGroup/android.view.ViewGroup"
                    + "/android.view.ViewGroup/android.widget.ScrollView"
                    + "/android.view.ViewGroup/android.view.ViewGroup"
                    + "/android.widget.ScrollView/android.view.ViewGroup"
                    + "/android.widget.ScrollView/android.view.ViewGroup"
                    + "/android.view.ViewGroup[3]/android.widget.ScrollView",
                    "Posting Test Photo");
            sleepQuiet(1000);

            step(27, "Tap final Post (2nd match)");
            clickByXpath("(//android.widget.TextView[@text=\"Post\"])[2]", "Final Post");
            sleepQuiet(2000);

            step(28, "Dismiss onboarding tour if it appears (tap Skip)");
            dismissOnboardingTourAfterPost();

            step(29, "Verify upload: fast-retry for 'Posted!' then catch success banner");
            int verifyFailures = verifyPostUploadEndFlow();

            // Aggregate the outcome ONCE, as the final action - every check above
            // already ran (nothing skipped/aborted). If any failed, fail the test
            // now so it goes red without having skipped steps.
            Assert.assertEquals(verifyFailures, 0,
                    "Post-upload end verification had " + verifyFailures
                    + " failed check(s) - see [FAIL] log lines above");
        } finally {
            testEnd("CameraSideControls_Posting");
        }
    }

    // ================================================================
    // ==========  UPLOAD-COMPLETION VERIFICATION (self-contained)  ====
    // ================================================================
    // Copied in-file (NOT imported from AddPostPage) to keep this native photo
    // page object standalone. Mirrors the text-post verification: after Final
    // Post, dismiss the intermittent onboarding tour, then fast-retry for the
    // "Posted!" upload stage (primary, robust signal) with the success banner as
    // a secondary confirmation.
    //
    // Photo upload stages (uploadStageTitle cycles through, 5 states):
    //   Preparing -> Getting Ready -> Uploading -> Publishing -> Posted!
    // (Text posting has 4 - no "Preparing".) The logic is state-list-agnostic:
    // it logs every transition and only anchors on "Publishing" (hand off to the
    // tight Posted! retry) and "Posted!" (success), so the extra leading state
    // needs no special handling.

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
            List<WebElement> els = driver.findElements(AppiumBy.xpath(xpath));
            return !els.isEmpty() && els.get(0).isDisplayed();
        } catch (Exception ignore) {
            return false;
        }
    }

    /** Reads the current upload-stage text, or null if the element isn't present. */
    private String readStageText() {
        try {
            List<WebElement> els = driver.findElements(AppiumBy.xpath(STAGE_XPATH));
            if (!els.isEmpty()) {
                return els.get(0).getText();
            }
        } catch (Exception ignore) { /* transient - treat as not-present */ }
        return null;
    }

    /** Single-shot check: is the success banner currently displayed? */
    private boolean isBannerPresent() {
        return elementPresent(BANNER_XPATH);
    }

    /**
     * Best-effort dismissal of the intermittent 5-step "Customize your Feed"
     * onboarding tour that can appear after posting. One Skip tap dismisses the
     * whole tour. Anchored on the Skip control: accessibility-id "Skip" (primary)
     * with content-desc xpath as fallback. Never throws.
     */
    private boolean dismissOnboardingTourIfPresent() {
        try {
            List<WebElement> skip = driver.findElements(AppiumBy.accessibilityId("Skip"));
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
     * Explicit onboarding-tour dismissal, run right after Final Post and before
     * upload verification. Polls briefly for EITHER the Skip control (tour
     * present -> tap it) OR the first upload stage (tour absent -> proceed
     * immediately, no wasted wait on tour-free runs). Best-effort; never throws.
     */
    public void dismissOnboardingTourAfterPost() {
        System.out.println("[FLOW] Checking for onboarding tour after Final Post ...");
        long windowMs = 6000;
        long pollMs   = 300;
        long deadline = System.currentTimeMillis() + windowMs;
        while (System.currentTimeMillis() < deadline) {
            if (dismissOnboardingTourIfPresent()) {
                return;
            }
            if (readStageText() != null) {
                System.out.println("[FLOW] Upload stage visible - no onboarding tour, proceeding");
                return;
            }
            sleepQuiet(pollMs);
        }
        System.out.println("[FLOW] No onboarding tour appeared within "
                + (windowMs / 1000) + "s - proceeding");
    }

    /**
     * Upload verification. Phase 1: track stages at a moderate poll until
     * "Publishing". Phase 2: at "Publishing", tight fast-retry that prioritises
     * catching the brief "Posted!" state (single lightweight probe every
     * iteration; banner + disappearance checked every 4th iteration so "Posted!"
     * gets the highest sampling rate). PASS if EITHER "Posted!" or the banner is
     * seen; FAIL only if neither. Returns 0 on pass, 1 on failure.
     */
    public int verifyPostUploadEndFlow() {
        System.out.println("[FLOW] Verifying upload: tracking stages, fast-retrying for Posted! ...");
        long guardMs = 90000;
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
                    if (c.equalsIgnoreCase("Posted!")) {
                        sawPosted = true;
                        System.out.println("[FLOW] Upload reached terminal stage: Posted!");
                        break;
                    }
                    if (c.equalsIgnoreCase("Publishing")) {
                        reachedPublishing = true;
                        break;
                    }
                }
                if (cur == null && sawAnyStage) {
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
                long tightMs = 60;
                long graceMs = 8000;
                long completedAt = 0;
                int iter = 0;
                while (System.currentTimeMillis() < deadline) {
                    if (elementPresent(POSTED_XPATH)) {
                        sawPosted = true;
                        System.out.println("[FLOW] Upload reached terminal stage: Posted!");
                        break;
                    }
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

    // ================================================================
    // ===================  FILTER-PANEL HELPERS  ====================
    // ================================================================
    // All locators below are Inspector-verified. Two kinds of labels:
    //  - filter CATEGORY names are plain TextViews matched by text
    //    (e.g. "Color", "Camera", "Moment"), some duplicated so an index is
    //    used where the request specified one;
    //  - filter NAMES are TextViews with resource-id com.dogpack:id/filterName.
    // The category row (filterCategoryRow) and the thumbnail row
    // (filterRecycler) are HORIZONTAL RecyclerViews, so they need a horizontal
    // scroll (the shared AndroidActions scroll helpers are vertical only).

    /** Tap a left-toolbar tool by its start-label text (e.g. "Filter"). */
    public void clickToolLabel(String label) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath("//android.widget.TextView[@resource-id="
                        + "\"com.dogpack:id/cameraToolLabelStart\" and @text=\""
                        + label + "\"]")));
        el.click();
        System.out.println("[ACTION] Tapped tool label: " + label);
    }

    /** Tap any element by a raw xpath (used for deep positional locators). */
    public void clickByXpath(String xpath, String label) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(xpath)));
        el.click();
        System.out.println("[ACTION] Tapped: " + label);
    }

    /** Tap a plain TextView by exact text (filter category names). */
    public void clickByText(String text) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath("//android.widget.TextView[@text=\"" + text + "\"]")));
        el.click();
        System.out.println("[ACTION] Tapped: " + text);
    }

    /** Tap the Nth (1-based) TextView with the given exact text. */
    public void clickByTextIndexed(String text, int index) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath("(//android.widget.TextView[@text=\"" + text
                        + "\"])[" + index + "]")));
        el.click();
        System.out.println("[ACTION] Tapped: " + text + " [" + index + "]");
    }

    /** Tap a filter by its filterName label (resource-id com.dogpack:id/filterName). */
    public void clickFilterName(String name) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath("//android.widget.TextView[@resource-id="
                        + "\"com.dogpack:id/filterName\" and @text=\"" + name + "\"]")));
        el.click();
        System.out.println("[ACTION] Selected filter: " + name);
    }

    /** Tap the Nth (1-based) "Apply filter" thumbnail (e.g. Gingham = 3). */
    public void clickApplyFilterByIndex(int index) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath("(//android.widget.ImageView[@content-desc="
                        + "\"Apply filter\"])[" + index + "]")));
        el.click();
        System.out.println("[ACTION] Tapped Apply-filter thumbnail [" + index + "]");
    }

    /** Horizontally scroll the filter CATEGORY row (filterCategoryRow). */
    public void scrollFilterCategoryRow() {
        horizontalScroll("com.dogpack:id/filterCategoryRow", "filterCategoryRow");
    }

    /** Horizontally scroll the filter THUMBNAIL row (filterRecycler). */
    public void scrollFilterRecycler() {
        horizontalScroll("com.dogpack:id/filterRecycler", "filterRecycler");
    }

    /**
     * Performs a left-to-right-content horizontal scroll (reveals items further
     * right) inside the given RecyclerView. Uses mobile: scrollGesture bounded
     * to the element's own rect with direction "right" - the shared
     * AndroidActions scroll helpers only scroll vertically, so this is a
     * self-contained horizontal variant. Falls back silently if the element is
     * not present.
     */
    private void horizontalScroll(String resourceId, String label) {
        List<WebElement> found = driver.findElements(
                org.openqa.selenium.By.id(resourceId));
        if (found.isEmpty()) {
            System.out.println("[WARN] Horizontal scroll target not found: " + label
                    + " (" + resourceId + ")");
            return;
        }
        WebElement row = found.get(0);
        org.openqa.selenium.Rectangle r = row.getRect();
        try {
            ((JavascriptExecutor) driver).executeScript("mobile: scrollGesture", Map.of(
                    "left", r.getX() + 5,
                    "top", r.getY() + 5,
                    "width", Math.max(r.getWidth() - 10, 10),
                    "height", Math.max(r.getHeight() - 10, 10),
                    "direction", "right",
                    "percent", 0.8));
            System.out.println("[ACTION] Horizontally scrolled: " + label);
        } catch (Exception e) {
            System.out.println("[WARN] Horizontal scroll failed on " + label
                    + ": " + e.getMessage());
        }
    }

    // ================================================================
}