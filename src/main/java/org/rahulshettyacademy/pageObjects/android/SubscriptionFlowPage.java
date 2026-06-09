package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rahulshettyacademy.utils.AndroidActions;
import org.testng.Assert;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

/**
 * SubscriptionFlowPage - page object for the Subscription feature flow
 * (Verification + Magic image credits cards -> View Plans -> close -> back).
 *
 * NAMED 'SubscriptionFlowPage' instead of 'SubscriptionPage' because a
 * pre-existing SubscriptionPage.java (749 lines) already lives in this
 * package and covers an earlier tab-based interaction pattern. This
 * new class is for the card-based flow specified by the user, kept
 * separate to avoid risk of breaking the legacy code path.
 *
 * Reached from Settings and activity -> "Subscription" hamburger row.
 * Screen layout differs slightly between dog and business profiles
 * (header text on the Verification details screen changes - "Boost
 * Your Account" vs "Boost Your Business Account"). The business
 * variant is implemented first; a dog variant or AccountType
 * parameterization can follow.
 *
 * Flow covered (business user variant):
 *   #1  Tap "Subscription" row in Settings
 *   #2  Wait for screen load (anchor: "View Plans" text visible)
 *   #3  Tap "Verification" card
 *   #4  Tap "View Plans"
 *   #5  Assert "Boost Your Business Account" header is visible
 *   #6  Tap close (X)
 *   #7  Tap "Magic image credits" card
 *   #8  Tap "View Plans"
 *   #9  Assert "Unlock more magic credits" header is visible
 *   #10 Tap close (X)
 *   #11 Tap back arrow (first ImageView on screen via UiAutomator)
 *   #12 Assert "Settings and activity" header is visible
 *
 * On any step failure, dumpVisibleText() prints visible TextViews and
 * content-descs so the cause is obvious without re-running.
 */
public class SubscriptionFlowPage extends AndroidActions {

    /**
     * Account-type variants for the Subscription feature flow.
     * Only the Verification details screen's header text differs:
     *   DOG      -> "Boost Your Account"
     *   BUSINESS -> "Boost Your Business Account"
     * Everything else (Subscription row, Verification card, View Plans,
     * Magic image credits card, Unlock more magic credits header, Close,
     * back arrow, Settings header) is identical between account types.
     */
    public enum AccountType { DOG, BUSINESS }

    private final AndroidDriver driver;
    private final WebDriverWait wait;
    private final AccountType accountType;

    /**
     * Default constructor - account type defaults to DOG (matches the
     * framework convention used in BoostAccountPage and
     * AiPhotoGenerationPage).
     */
    public SubscriptionFlowPage(AndroidDriver driver) {
        this(driver, AccountType.DOG);
    }

    /**
     * Parameterized constructor. Pass AccountType.BUSINESS from the
     * business test class so the Verification assertion expects
     * "Boost Your Business Account" instead of "Boost Your Account".
     */
    public SubscriptionFlowPage(AndroidDriver driver, AccountType accountType) {
        super(driver);
        this.driver = driver;
        this.accountType = accountType;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // ================================================================
    // ==========          VERIFIED LOCATORS                     ======
    // ================================================================

    /** Settings menu row for "Subscription". */
    private static final String SUBSCRIPTION_ROW_XPATH =
        "//android.view.ViewGroup[@content-desc=\"Subscription\"]";

    /**
     * "View Plans" text - used as screen-loaded anchor AND as click
     * target after each card is selected.
     */
    private static final String VIEW_PLANS_XPATH =
        "//android.widget.TextView[@text=\"View Plans\"]";

    /** "Verification" subscription card. */
    private static final String VERIFICATION_CARD_XPATH =
        "//android.widget.TextView[@text=\"Verification\"]";

    /** "Magic image credits" subscription card. */
    private static final String MAGIC_IMAGE_CREDITS_CARD_XPATH =
        "//android.widget.TextView[@text=\"Magic image credits\"]";

    /**
     * Header text on the Verification-View-Plans screen:
     *   DOG      -> "Boost Your Account"
     *   BUSINESS -> "Boost Your Business Account"
     * Both XPath and expected-text string are resolved at call time
     * via the helpers below.
     */
    private String getBoostHeaderExpectedText() {
        return (accountType == AccountType.BUSINESS)
                ? "Boost Your Business Account"
                : "Boost Your Account";
    }

    private String getBoostHeaderXpath() {
        return "//android.widget.TextView[@text=\""
                + getBoostHeaderExpectedText() + "\"]";
    }

    /** Header text on the AI-credits-View-Plans screen. */
    private static final String UNLOCK_AI_CREDITS_HEADER_XPATH =
        "//android.widget.TextView[@text=\"Unlock more magic credits\"]";

    /** Close (X) button on the per-card details screens. */
    private static final String CLOSE_BTN_XPATH =
        "//android.widget.Button[@content-desc=\"Close\"]"
        + "/android.widget.ImageView";

    /**
     * Back arrow on the Subscription overview screen.
     * User-specified: match by className 'android.widget.ImageView'.
     * findElement returns the first such ImageView in document order,
     * which should be the toolbar back arrow on this screen.
     */
    private static final String BACK_ARROW_UIA_SELECTOR =
        "new UiSelector().className(\"android.widget.ImageView\")";

    /** Settings screen header - used to verify back navigation landed. */
    private static final String SETTINGS_HEADER_XPATH =
        "//android.widget.TextView[@text=\"Settings and activity\"]";

    // Expected header text values for assertions
    // (Boost header expected text is account-aware - see helper above.)
    private static final String EXPECTED_UNLOCK_AI_CREDITS_HEADER =
        "Unlock more magic credits";

    // ================================================================
    // ==========           PUBLIC METHODS                       ======
    // ================================================================

    /**
     * #1 + #2 - Tap "Subscription" row in Settings, wait for overview
     * screen to load by anchoring on "View Plans" visibility.
     */
    public void NavigateToSubscriptionFromMenu() {
        log("===> NavigateToSubscriptionFromMenu");

        log("[STEP 1/2] Tap Subscription row");
        WebElement row = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(SUBSCRIPTION_ROW_XPATH)));
        row.click();
        log("[OK]       Tapped Subscription row");

        log("[STEP 2/2] Wait for 'View Plans' (screen-loaded anchor, 30s)");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            AppiumBy.xpath(VIEW_PLANS_XPATH)));
            log("[PASS]     Subscription overview screen loaded");
        } catch (Exception e) {
            log("[FAIL]     'View Plans' not visible after 30s.");
            dumpVisibleText();
            throw e;
        }
    }

    /** #3 - Tap the "Verification" card. */
    public void ClickVerificationCard() {
        log("===> ClickVerificationCard");
        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(VERIFICATION_CARD_XPATH)));
        card.click();
        log("[OK]       Tapped Verification card");
    }

    /**
     * #4 + #8 - Tap the "View Plans" button. Used twice - once after
     * Verification is selected, once after Magic image credits is selected.
     */
    public void ClickViewPlans() {
        log("===> ClickViewPlans");
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(VIEW_PLANS_XPATH)));
        btn.click();
        log("[OK]       Tapped View Plans");
    }

    /**
     * #5 - Assert the Verification details screen's header text equals
     *      "Boost Your Business Account" (business variant).
     *
     * For the dog variant this header would be "Boost Your Account" -
     * when a dog version of this flow is written, parameterize via
     * AccountType (same pattern as Boost / AI) or create a separate
     * assertion method.
     */
    /**
     * #5 - Assert the Verification details screen's header text matches
     *      the value expected for the configured AccountType:
     *        DOG      -> "Boost Your Account"
     *        BUSINESS -> "Boost Your Business Account"
     *
     * Both XPath and expected-text resolve from the AccountType enum,
     * so a single method serves both account variants. The dog test
     * class calls it via the same method name as the business one.
     */
    public void AssertVerificationHeader() {
        log("===> AssertVerificationHeader");
        String expected = getBoostHeaderExpectedText();
        WebElement header;
        try {
            header = new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            AppiumBy.xpath(getBoostHeaderXpath())));
        } catch (Exception e) {
            log("[FAIL]     Verification header not visible after 15s.");
            log("           Expected text: \"" + expected + "\"");
            dumpVisibleText();
            throw e;
        }
        String actual = header.getAttribute("text");
        log("           Actual:   \"" + actual + "\"");
        log("           Expected: \"" + expected + "\"");
        Assert.assertEquals(actual, expected,
                "Verification details screen header does not match the "
                + "expected value for AccountType=" + accountType + ".");
        log("[PASS]     Verification header verified ("
                + accountType + " variant)");
    }

    /**
     * Backward-compat alias for AssertVerificationHeader().
     * Kept so the existing Dogpack_Subscription_Business test class
     * (which references AssertBoostBusinessHeader) continues to
     * compile and pass without modification.
     */
    public void AssertBoostBusinessHeader() {
        AssertVerificationHeader();
    }

    /**
     * #6 + #10 - Tap close (X) on the per-card details screen. Returns
     * to the Subscription overview screen. Used after both Verification
     * and Magic image credits details screens.
     */
    public void CloseCardScreen() {
        log("===> CloseCardScreen");
        WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(CLOSE_BTN_XPATH)));
        closeBtn.click();
        log("[OK]       Tapped Close");
        // brief settle so the next step's wait can fire reliably
        sleepQuiet(1500);
    }

    /** #7 - Tap the "Magic image credits" card. */
    public void ClickMagicImageCreditsCard() {
        log("===> ClickMagicImageCreditsCard");
        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(MAGIC_IMAGE_CREDITS_CARD_XPATH)));
        card.click();
        log("[OK]       Tapped Magic image credits card");
    }

    /**
     * #9 - Assert the AI-credits details screen's header text equals
     *      "Unlock more magic credits" (same for both account types).
     */
    public void AssertUnlockAiCreditsHeader() {
        log("===> AssertUnlockAiCreditsHeader");
        WebElement header;
        try {
            header = new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            AppiumBy.xpath(UNLOCK_AI_CREDITS_HEADER_XPATH)));
        } catch (Exception e) {
            log("[FAIL]     'Unlock more magic credits' header not visible after 15s.");
            dumpVisibleText();
            throw e;
        }
        String actual = header.getAttribute("text");
        log("           Actual:   \"" + actual + "\"");
        log("           Expected: \"" + EXPECTED_UNLOCK_AI_CREDITS_HEADER + "\"");
        Assert.assertEquals(actual, EXPECTED_UNLOCK_AI_CREDITS_HEADER,
                "Magic image credits details screen header does not match.");
        log("[PASS]     Unlock more magic credits header verified");
    }

    /**
     * #11 - Tap the back arrow (toolbar back navigation).
     * Uses UiAutomator selector matching the first ImageView in document
     * order. If the wrong ImageView is matched and the click lands
     * somewhere unexpected, the next step's assertion will fail and the
     * diagnostic dump will show what's on screen.
     */
    public void ClickBackArrow() {
        log("===> ClickBackArrow");
        WebElement backArrow;
        try {
            backArrow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.androidUIAutomator(BACK_ARROW_UIA_SELECTOR)));
        } catch (Exception e) {
            log("[FAIL]     Back arrow (first ImageView) not visible after 20s.");
            log("           Selector: " + BACK_ARROW_UIA_SELECTOR);
            dumpVisibleText();
            throw e;
        }
        backArrow.click();
        log("[OK]       Tapped back arrow");
        sleepQuiet(1500);
    }

    /**
     * #12 - Assert we are back on Settings and activity screen by
     *       waiting for its header text to be visible.
     */
    public void AssertOnSettingsScreen() {
        log("===> AssertOnSettingsScreen");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            AppiumBy.xpath(SETTINGS_HEADER_XPATH)));
            log("[PASS]     Back on Settings and activity screen");
        } catch (Exception e) {
            log("[FAIL]     Settings header not visible after 15s.");
            dumpVisibleText();
            throw e;
        }
    }

    // ================================================================
    // ==========           PRIVATE HELPERS                      ======
    // ================================================================

    private void log(String msg) {
        System.out.println(msg);
    }

    private void sleepQuiet(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }

    private void dumpVisibleText() {
        try {
            List<WebElement> textViews = driver.findElements(
                    AppiumBy.xpath("//android.widget.TextView"));
            log("           ---- visible TextViews ----");
            int n = 0;
            for (WebElement tv : textViews) {
                try {
                    if (!tv.isDisplayed()) continue;
                    String t = tv.getAttribute("text");
                    if (t == null || t.isEmpty()) continue;
                    log("           [TV] \"" + t + "\"");
                    if (++n >= 20) { log("           ...(truncated)"); break; }
                } catch (Exception ignore) { /* */ }
            }
        } catch (Exception ignore) { /* */ }
        try {
            List<WebElement> withDesc = driver.findElements(
                    AppiumBy.xpath("//*[@content-desc and string-length(@content-desc) > 0]"));
            log("           ---- visible elements with content-desc ----");
            int n = 0;
            for (WebElement el : withDesc) {
                try {
                    if (!el.isDisplayed()) continue;
                    String d = el.getAttribute("content-desc");
                    log("           [DESC] \"" + d + "\"");
                    if (++n >= 20) { log("           ...(truncated)"); break; }
                } catch (Exception ignore) { /* */ }
            }
        } catch (Exception ignore) { /* */ }
    }
}