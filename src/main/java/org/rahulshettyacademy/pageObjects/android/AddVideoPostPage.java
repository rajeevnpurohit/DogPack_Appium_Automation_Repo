package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rahulshettyacademy.utils.AndroidActions;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

/**
 * Page Object for the NEW native (com.dogpack.mediaediting) VIDEO capture
 * screen (CameraFragment / fragment_camera.xml, Video mode).
 *
 * SCOPE: Self-contained, standalone page object for the native Video module. It
 * does NOT import, extend, share, or copy code from the old React Native Add
 * Post automation (PostingExperiencePage / Dogpack_PostingExperience*), and it
 * does not couple to AddPostPage (text) or AddPhotoPostPage (photo) - any helper
 * it needs is copied in-file so this TC can be run, broken, and fixed in
 * complete isolation.
 *
 * Flow (Inspector-verified locators):
 *   runSwitchToVideoMode():
 *     2. Tap "+" (add-feed) to open the camera
 *     3. Dismiss the record-audio permission (While using the app)
 *     4. Tap "Switch to Video mode"
 *     5. Tap "No Effect"
 *   runVideoSideControls() (left editing toolbar in Video mode):
 *     6.  Open Labels carousel  -> (Editing tool)[7]  (expand chevron)
 *     7.  Beauty                -> (Editing tool)[1]
 *     8.  Filter                -> (Editing tool)[2]
 *     9.  Loop                  -> (Editing tool)[3]
 *     10. Loop (re-tap)         -> (Editing tool)[3]
 *     11. Zoom                  -> (Editing tool)[4]
 *     12. Rewind                -> (Editing tool)[5]
 *     13. FX                    -> (Editing tool)[6]
 *
 * NOTE: the (Editing tool)[N] indices are config/mode dependent. In Video mode
 * the expand chevron is [7] (7 tools before it), vs [6] in Photo mode. Captured
 * live from Inspector; re-verify if the video toolbar composition changes.
 */
public class AddVideoPostPage extends AndroidActions {

    AndroidDriver driver;
    WebDriverWait wait;

    public AddVideoPostPage(AndroidDriver driver) {
        super(driver);
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    // ================================================================
    // ===================  LOCATORS  =================================
    // ================================================================

    /** Home bottom-tab "+" (add-feed) that launches the camera. */
    @AndroidFindBy(xpath = "//android.view.View[@content-desc=\"add-feed\"]")
    private WebElement addFeedBtn;

    /** Mode tab: Switch to Video mode. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@content-desc=\"Switch to Video mode\"]")
    private WebElement switchToVideoModeTab;

    /** "No Effect" item in the bottom effect carousel. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text=\"No Effect\"]")
    private WebElement noEffectItem;

    // ================================================================
    // ===================  DIAGNOSTIC LOGGING  =======================
    // ================================================================

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
    // Video capture fires camera + record-audio permission prompts on a fresh
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
                "com.android.permissioncontroller:id/permission_allow_foreground_only_button"),
                "While using the app");
        if (!clicked) {
            clicked = clickRawByLocator(org.openqa.selenium.By.id(
                    "com.android.permissioncontroller:id/permission_allow_all_button"),
                    "Allow All");
        }
        if (!clicked) {
            clicked = clickRawByLocator(org.openqa.selenium.By.id(
                    "com.android.permissioncontroller:id/permission_allow_button"),
                    "Allow");
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
    // ===================  GENERIC HELPERS  ==========================
    // ================================================================

    public void clickAddFeedIcon() {
        wait.until(ExpectedConditions.elementToBeClickable(addFeedBtn)).click();
        System.out.println("[ACTION] Tapped + (add-feed)");
    }

    public void clickSwitchToVideoMode() {
        wait.until(ExpectedConditions.elementToBeClickable(switchToVideoModeTab)).click();
        System.out.println("[ACTION] Tapped Switch to Video mode");
    }

    public void clickNoEffect() {
        wait.until(ExpectedConditions.elementToBeClickable(noEffectItem)).click();
        System.out.println("[ACTION] Tapped No Effect carousel item");
    }

    /** Tap an "Editing tool" icon by its 1-based index in the video toolbar. */
    public void clickEditingTool(int index, String label) {
        wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath("(//android.widget.ImageView[@content-desc="
                        + "\"Editing tool\"])[" + index + "]"))).click();
        System.out.println("[ACTION] Tapped " + label + " (Editing tool [" + index + "])");
    }

    // ================================================================
    // ===================  FLOW STEPS  ===============================
    // ================================================================

    /**
     * Steps 2-5: open the camera, clear the record-audio permission, switch to
     * Video mode, and select No Effect. Assumes login is done and the feed is
     * showing.
     */
    public void runSwitchToVideoMode() {
        testStart("runSwitchToVideoMode");
        try {
            step(2, "Tap + (add-feed) to open the camera");
            clickAddFeedIcon();
            sleepQuiet(1000);

            step(3, "Dismiss record-audio (and any camera) permission dialog");
            dismissAllPermissionDialogs(3);

            step(4, "Tap Switch to Video mode");
            clickSwitchToVideoMode();
            sleepQuiet(800);

            step(5, "Tap No Effect");
            clickNoEffect();
            sleepQuiet(600);
        } finally {
            testEnd("runSwitchToVideoMode");
        }
    }

    /**
     * Steps 6-13: exercise the left editing toolbar in Video mode. CONTINUATION
     * of runSwitchToVideoMode (priority order) - it does not open the camera or
     * switch modes; it assumes Video mode is already active with No Effect
     * selected.
     */
    public void runVideoSideControls() {
        testStart("runVideoSideControls");
        try {
            step(6, "Open Labels carousel (expand)");
            clickEditingTool(7, "Open Labels carousel");
            sleepQuiet(600);

            step(7, "Tap Beauty");
            clickEditingTool(1, "Beauty");
            sleepQuiet(600);

            step(8, "Tap Filter");
            clickEditingTool(2, "Filter");
            sleepQuiet(600);

            step(9, "Tap Loop");
            clickEditingTool(3, "Loop");
            sleepQuiet(600);

            step(10, "Re-tap Loop");
            clickEditingTool(3, "Loop (re-tap)");
            sleepQuiet(600);

            step(11, "Tap Zoom");
            clickEditingTool(4, "Zoom");
            sleepQuiet(600);

            step(12, "Tap Rewind");
            clickEditingTool(5, "Rewind");
            sleepQuiet(600);

            step(13, "Tap FX");
            clickEditingTool(6, "FX");
            sleepQuiet(600);
        } finally {
            testEnd("runVideoSideControls");
        }
    }
}
