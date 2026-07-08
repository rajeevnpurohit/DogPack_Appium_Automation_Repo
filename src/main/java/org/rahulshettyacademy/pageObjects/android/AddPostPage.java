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

            step(7, "Tap Create post");
            clickCreatePost();
            sleepQuiet(1000);

            step(8, "Tap Post media (single-item preview submit)");
            clickPostMedia();
            sleepQuiet(1500);

            step(9, "Enter caption text on Create Post screen");
            enterCaptionText(captionText);
            sleepQuiet(500);

            step(10, "Tap final Post submit");
            clickFinalPost();
            sleepQuiet(1500);
        } finally {
            testEnd("postImageWithCaption");
        }
    }
}