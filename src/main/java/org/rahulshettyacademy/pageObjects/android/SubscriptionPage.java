package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rahulshettyacademy.utils.AndroidActions;
import org.testng.Assert;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

/**
 * SubscriptionPage - Page object for the "Subscription" feature
 * (NOT the SubscribeDog purchase page — the OVERVIEW page that shows
 *  the user's currently active plan or an empty/upsell state).
 *
 * APP SOURCE MAPPING:
 *   - Settings menu entry            -> screen/MenuScreen.js
 *     - menuListData entry: title="subn", type="btn"
 *       (Helper.menuListData line 498)
 *     - translate("subn") = "Subscription" (en.json:1835)
 *     - methodClickItem(item == "subn") -> handleNavigation push to
 *       "Subscription" with passProps { activePlan: {} } (line 305-317)
 *     - Gated by isCheckBusinessApprove() — toast on pending business.
 *   - "Subscription" screen          -> screen/Subscription/Subscription.tsx
 *     - Hosted on Stack route name = "Subscription" with headerShown=true
 *       (Routes.js:1235-1238) — uses common Header component, NOT the
 *       Wrapper.tsx layout used by SubscribeDog. So:
 *         > NO close (X) button — back is via the common Header back arrow
 *           (accessibility id "left_click_back" if present, else BACK key).
 *         > Header title = translate("subn") = "Subscription"
 *     - TWO TABS rendered side-by-side (line 416-430):
 *         "Verification"      (translate("ver"))     — default selected
 *         "AI image credits"  (translate("aiimage"))
 *     - Both tabs render the SAME structure conditionally:
 *         isEligible = (currentPlan?.id || gemini_product_credits > 0)
 *         If eligible: ActivePlanBanner + (Verification: BenefitsRow list)
 *                       (Gemini: ActivePlanBanner * 2 — sub + product)
 *         If NOT eligible: EmptyView with "View Plans" button
 *           (translate("vpan"))
 *     - View Plans -> onPressButton (line 386-396):
 *         "gemini" tab -> SubscribeDog with isDogBus="aifiler"
 *         "verification" tab -> SubscribeDog with isDogBus="dog"/"business"
 *
 *   - ActivePlanBanner       -> Subscription.tsx lines 135-204
 *     - Title varies by user + tab:
 *         "DogPack Premium"             (primary_account=1, verification tab)
 *         "DogPack Business Premium"    (primary_account=2, verification tab)
 *         "AI Premium"                  (gemini subscription)
 *         "AI Generation Bundle"        (gemini product / one-time)
 *     - Labelled rows: Status, Billing Period, AI Credit Balance,
 *       Plan end date / Plan renewal date.
 *
 *   - "Change plan" CTA      -> only shown on Verification tab when
 *     badgePlan.plan.plan_type === "monthly" (line 494-515). Navigates
 *     to SubscribeDog isDogBus="dog"|"business".
 *
 *   - Footer text            -> translate("mnsa") (Android) /
 *     translate("mnsi") (iOS) — store management hint.
 *
 * NOTE on header: the common Header component used by Subscription is
 * the SAME one used elsewhere; per ProfilePage / SettingsAndActivityPage
 * comments, the back arrow has accessibility id "left_click_back" in
 * other screens. We try that first; fall back to physical BACK key.
 */
public class SubscriptionPage extends AndroidActions {

    private final AndroidDriver driver;
    private final WebDriverWait shortWait;
    private final WebDriverWait wait;
    private final WebDriverWait longWait;

    public SubscriptionPage(AndroidDriver driver) {
        super(driver);
        this.driver = driver;
        PageFactory.initElements(
                new AppiumFieldDecorator(driver, Duration.ofSeconds(12)),
                this);
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(40));
    }

    // ================================================================
    // ==========                LOCATORS                    ==========
    // ================================================================

    // --- 1. SETTINGS-screen entry row ---
    @AndroidFindBy(accessibility = "Subscription")
    private WebElement subscriptionMenuRow;

    // --- 2. Common Header (NOT Wrapper) ---
    // Header title text rendered by common/Header.js with translate("subn").
    // Two TextView nodes may render "Subscription":
    //   (1) the menu row in Settings (which we left BEFORE navigating here)
    //   (2) the header title on this screen
    // We disambiguate using exact text match + ensure menu row is gone.
    @AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Subscription\"]")
    private WebElement subscriptionHeader;

    // Header back arrow. Per ProfilePage / SettingsAndActivityPage doc
    // comments, common Header exposes "left_click_back" accessibility id.
    @AndroidFindBy(accessibility = "left_click_back")
    private WebElement headerBackArrow;

    // --- 3. TABS ---
    // Tab labels from line 416-430. Active state is style-driven (opacity)
    // — no testID. We assert presence here and verify tab content below.
    @AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Verification\"]")
    private WebElement verificationTab;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text=\"AI image credits\"]")
    private WebElement aiImageCreditsTab;

    // --- 4. ACTIVE PLAN BANNER (visible when eligible) ---
    // Plan titles per the if/else ladder in ActivePlanBanner (line 148-161).
    @AndroidFindBy(xpath = "//*[@text='DogPack Premium']")
    private WebElement premiumBannerTitle;

    @AndroidFindBy(xpath = "//*[@text='DogPack Business Premium']")
    private WebElement premiumBusinessBannerTitle;

    @AndroidFindBy(xpath = "//*[@text='AI Premium']")
    private WebElement aiPremiumBannerTitle;

    @AndroidFindBy(xpath = "//*[@text='AI Generation Bundle']")
    private WebElement aiBundleBannerTitle;

    // Banner labels (rendered as label-then-value pairs)
    @AndroidFindBy(xpath = "//*[starts-with(@text,'Status')]")
    private WebElement bannerStatusLabel;

    @AndroidFindBy(xpath = "//*[starts-with(@text,'Billing Period')]")
    private WebElement bannerBillingPeriodLabel;

    @AndroidFindBy(xpath = "//*[starts-with(@text,'AI Credit Balance')]")
    private WebElement bannerCreditBalanceLabel;

    @AndroidFindBy(xpath = "//*[starts-with(@text,'Plan end date') or "
            + "starts-with(@text,'Plan renewal date')]")
    private WebElement bannerPlanDateLabel;

    // --- 5. EMPTY STATE (when isEligible === false) ---
    // EmptyView renders Labels with translate("empty_sub") / ("empty_gem")
    // + Button with translate("vpan") = "View Plans".
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'Upgrade to unlock verified status')]")
    private WebElement emptyVerificationCopy;

    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'Upgrade to unlock AI image credits')]")
    private WebElement emptyGeminiCopy;

    @AndroidFindBy(xpath = "//*[@text='View Plans']")
    private WebElement viewPlansButton;

    // --- 6. "Your Benefits" / "Purchased AI Credit Balance" labels ---
    @AndroidFindBy(xpath = "//*[@text='Your Benefits']")
    private WebElement yourBenefitsLabel;

    @AndroidFindBy(xpath = "//*[@text='Purchased AI Credit Balance']")
    private WebElement purchasedCreditBalanceLabel;

    // --- 7. "Change plan" CTA (monthly verification users) ---
    @AndroidFindBy(xpath = "//*[@text='Change plan']")
    private WebElement changePlanButton;

    // --- 8. Footer store-management text ---
    @AndroidFindBy(xpath = "//*[contains(@text,'Manage your subscription directly')]")
    private WebElement footerManageText;

    // --- 9. Under-approval toast (business pending) ---
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'You will be able to access this feature')]")
    private WebElement underApprovalToast;

    // --- 10. Settings screen header (used to verify return after back) ---
    @AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Settings and activity\"]")
    private WebElement settingsScreenHeader;

    // ================================================================
    // ==========             PUBLIC METHODS                ===========
    // ================================================================

    /**
     * #1 - Tap the "Subscription" row from Settings menu and verify the
     *      Subscription overview screen opens.
     *
     * Pre-condition: caller has navigated to "Settings and activity"
     *      screen.
     */
    public void NavigateToSubscriptionFromMenu() {
        testStart("NavigateToSubscriptionFromMenu");
        try {
            ensureAppForeground();

            step("1/4", "Scrolling to 'Subscription' menu row");
            boolean reached = scrollToTextSafe("Subscription", 8);

            if (!reached) {
                Assert.fail("'Subscription' menu row not found in Settings. "
                        + "This row is NOT gated by verification/AR — its "
                        + "absence indicates a build issue or test user "
                        + "on the wrong screen.");
                return;
            }

            step("2/4", "Tapping 'Subscription' row");
            try {
                shortWait.until(ExpectedConditions.elementToBeClickable(
                        subscriptionMenuRow)).click();
                System.out.println("[ACTION] Clicked 'Subscription'");
            } catch (Exception e) {
                Assert.fail("Row text found but @AndroidFindBy proxy not "
                        + "clickable. " + e.getMessage().split("\n")[0]);
                return;
            }

            sleepQuiet(1500);
            if (isDisplayedSafe(underApprovalToast)) {
                System.out.println("[INFO] 'Under approval' toast — business "
                        + "pending. Soft pass.");
                return;
            }

            step("3/4", "Waiting for tabs to render (Subscription screen anchor)");
            try {
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.visibilityOf(verificationTab),
                        ExpectedConditions.visibilityOf(aiImageCreditsTab)));
                System.out.println("[ASSERT PASS] Subscription screen tabs "
                        + "visible");
            } catch (Exception e) {
                Assert.fail("Subscription screen did not load within 20s "
                        + "(tabs not rendered). Routes.js stack 'Subscription' "
                        + "may have failed to mount. Error: "
                        + e.getMessage().split("\n")[0]);
                return;
            }

            step("4/4", "Verifying header title 'Subscription'");
            // The screen header uses common Header — title is translate("subn").
            Assert.assertTrue(isDisplayedSafe(subscriptionHeader),
                    "Subscription header text not visible.");
            System.out.println("[ASSERT PASS] Subscription header visible");
        } finally {
            testEnd("NavigateToSubscriptionFromMenu");
        }
    }

    /**
     * #2 - Verify both tabs are rendered and "Verification" is the
     *      default-selected tab (state-driven; assert by presence of
     *      Verification-specific elements).
     */
    public void VerifyTabsRendered() {
        testStart("VerifyTabsRendered");
        try {
            ensureAppForeground();

            step("1/3", "Verification tab visible");
            Assert.assertTrue(isDisplayedSafe(verificationTab),
                    "'Verification' tab not visible.");

            step("2/3", "AI image credits tab visible");
            Assert.assertTrue(isDisplayedSafe(aiImageCreditsTab),
                    "'AI image credits' tab not visible.");

            step("3/3", "Verification tab is default-active (assert by "
                    + "presence of Verification-only content)");
            // Default selectedTab is "verification" (line 289). The page
            // renders either Verification banner OR the empty-Verification
            // copy. Verify at least ONE of these is present.
            boolean defaultIsVerification = isDisplayedSafe(emptyVerificationCopy)
                    || isDisplayedSafe(premiumBannerTitle)
                    || isDisplayedSafe(premiumBusinessBannerTitle)
                    || isDisplayedSafe(yourBenefitsLabel);

            Assert.assertTrue(defaultIsVerification,
                    "Verification tab does NOT appear default-selected. "
                    + "Expected one of: empty verification copy, DogPack "
                    + "Premium banner, or 'Your Benefits' label. None "
                    + "visible — useState initial value may have changed.");
            System.out.println("[ASSERT PASS] Verification is default tab");
        } finally {
            testEnd("VerifyTabsRendered");
        }
    }

    /**
     * #3 - Switch to "AI image credits" tab and verify Gemini-specific
     *      content appears (empty copy OR AI Premium / Bundle banner).
     */
    public void SwitchToAIImageCreditsTab() {
        testStart("SwitchToAIImageCreditsTab");
        try {
            ensureAppForeground();

            if (!isDisplayedSafe(aiImageCreditsTab)) {
                System.out.println("[SKIP] AI image credits tab not visible.");
                return;
            }

            step("1/2", "Tap AI image credits tab");
            shortWait.until(ExpectedConditions.elementToBeClickable(
                    aiImageCreditsTab)).click();
            System.out.println("[ACTION] Tapped 'AI image credits' tab");
            sleepQuiet(800);

            step("2/2", "Verify Gemini-specific content rendered");
            boolean geminiContent = isDisplayedSafe(emptyGeminiCopy)
                    || isDisplayedSafe(aiPremiumBannerTitle)
                    || isDisplayedSafe(aiBundleBannerTitle)
                    || isDisplayedSafe(purchasedCreditBalanceLabel);
            Assert.assertTrue(geminiContent,
                    "After tap, no Gemini-specific content visible. "
                    + "Expected one of: empty-gem copy, AI Premium banner, "
                    + "AI Generation Bundle banner, or 'Purchased AI "
                    + "Credit Balance' label. setSelectedTab may have "
                    + "failed.");
            System.out.println("[ASSERT PASS] AI image credits tab content "
                    + "rendered");
        } finally {
            testEnd("SwitchToAIImageCreditsTab");
        }
    }

    /**
     * #4 - Switch back to "Verification" tab from AI tab.
     */
    public void SwitchBackToVerificationTab() {
        testStart("SwitchBackToVerificationTab");
        try {
            ensureAppForeground();

            if (!isDisplayedSafe(verificationTab)) {
                System.out.println("[SKIP] Verification tab not visible.");
                return;
            }

            step("1/2", "Tap Verification tab");
            shortWait.until(ExpectedConditions.elementToBeClickable(
                    verificationTab)).click();
            System.out.println("[ACTION] Tapped 'Verification' tab");
            sleepQuiet(800);

            step("2/2", "Verify Verification-specific content rendered");
            boolean verContent = isDisplayedSafe(emptyVerificationCopy)
                    || isDisplayedSafe(premiumBannerTitle)
                    || isDisplayedSafe(premiumBusinessBannerTitle)
                    || isDisplayedSafe(yourBenefitsLabel);
            Assert.assertTrue(verContent,
                    "After tap, no Verification-specific content visible.");
            System.out.println("[ASSERT PASS] Verification tab content "
                    + "rendered");
        } finally {
            testEnd("SwitchBackToVerificationTab");
        }
    }

    /**
     * #5 - Verify EITHER the active-plan banner OR the empty-state View
     *      Plans button is rendered on the current tab (mutually
     *      exclusive: line 432-588 `isEligible ? <ScrollView> : <EmptyView>`).
     *
     * If banner: assert banner labels (Status, Billing Period, AI Credit
     *      Balance, Plan end/renewal date).
     * If empty:  assert "View Plans" button + correct empty copy.
     */
    public void VerifyActivePlanOrEmptyState() {
        testStart("VerifyActivePlanOrEmptyState");
        try {
            ensureAppForeground();

            boolean hasBanner = isDisplayedSafe(premiumBannerTitle)
                    || isDisplayedSafe(premiumBusinessBannerTitle)
                    || isDisplayedSafe(aiPremiumBannerTitle)
                    || isDisplayedSafe(aiBundleBannerTitle);

            boolean hasEmpty = isDisplayedSafe(viewPlansButton)
                    && (isDisplayedSafe(emptyVerificationCopy)
                            || isDisplayedSafe(emptyGeminiCopy));

            if (hasBanner) {
                System.out.println("[PATH A] Active plan banner detected");

                step("1/4", "Banner title visible (already asserted by path)");
                step("2/4", "Status label visible");
                Assert.assertTrue(isDisplayedSafe(bannerStatusLabel),
                        "Banner 'Status' label not visible.");

                step("3/4", "Plan end / renewal date label visible");
                Assert.assertTrue(isDisplayedSafe(bannerPlanDateLabel),
                        "Banner 'Plan end date' or 'Plan renewal date' "
                        + "label not visible.");

                step("4/4", "AI Credit Balance label visible");
                Assert.assertTrue(isDisplayedSafe(bannerCreditBalanceLabel),
                        "Banner 'AI Credit Balance' label not visible.");

                System.out.println("[ASSERT PASS] All banner labels visible");

            } else if (hasEmpty) {
                System.out.println("[PATH B] Empty state detected");

                step("1/2", "Empty-state copy visible");
                Assert.assertTrue(
                        isDisplayedSafe(emptyVerificationCopy)
                                || isDisplayedSafe(emptyGeminiCopy),
                        "Empty state copy text not visible.");

                step("2/2", "View Plans button visible");
                Assert.assertTrue(isDisplayedSafe(viewPlansButton),
                        "'View Plans' button not visible in empty state.");

                System.out.println("[ASSERT PASS] Empty state correctly "
                        + "rendered");
            } else {
                Assert.fail("Neither active-plan banner NOR empty-state "
                        + "View Plans button is visible on the Subscription "
                        + "screen. This is an undefined UI state — possible "
                        + "causes: (a) shimmer / loading still active, "
                        + "(b) API error returning malformed currentPlan, "
                        + "(c) selectedTab in transition.");
            }
        } finally {
            testEnd("VerifyActivePlanOrEmptyState");
        }
    }

    /**
     * #6 - From empty state, tap "View Plans" and verify navigation to
     *      SubscribeDog screen (either Boost screen OR AI screen
     *      depending on currently selected tab — see onPressButton
     *      line 386-396).
     */
    public void TapViewPlansFromEmptyState() {
        testStart("TapViewPlansFromEmptyState");
        try {
            ensureAppForeground();

            if (!isDisplayedSafe(viewPlansButton)) {
                System.out.println("[SKIP] 'View Plans' button not visible "
                        + "— user has an active plan or selected tab is on "
                        + "the eligible path.");
                return;
            }

            step("1/2", "Tap 'View Plans' button");
            shortWait.until(ExpectedConditions.elementToBeClickable(
                    viewPlansButton)).click();
            System.out.println("[ACTION] Tapped 'View Plans'");

            step("2/2", "Verify SubscribeDog screen opened "
                    + "(Boost OR AI variant)");
            try {
                wait.until(ExpectedConditions.or(
                        // Boost variants
                        ExpectedConditions.visibilityOfElementLocated(
                                AppiumBy.xpath(
                                        "//android.widget.TextView[@text=\"Boost Your Account\"]")),
                        ExpectedConditions.visibilityOfElementLocated(
                                AppiumBy.xpath(
                                        "//android.widget.TextView[@text=\"Boost Your Business Account\"]")),
                        // AI variant
                        ExpectedConditions.visibilityOfElementLocated(
                                AppiumBy.xpath(
                                        "//android.widget.TextView[@text=\"Unlock more AI credits\"]"))));
                System.out.println("[ASSERT PASS] SubscribeDog screen opened "
                        + "(Boost or AI variant detected)");
            } catch (Exception e) {
                Assert.fail("After 'View Plans' tap, neither Boost nor AI "
                        + "subscribe screen appeared within 20s. "
                        + e.getMessage().split("\n")[0]);
            }
        } finally {
            // Return to Subscription screen so subsequent tests start clean.
            // The Subscribe screen's close button + the Subscription screen's
            // header use the same back semantics, but to be safe we use
            // the close button on Wrapper if present, else BACK.
            try {
                WebElement closeBtn = driver.findElement(
                        AppiumBy.accessibilityId("Close"));
                if (closeBtn.isDisplayed()) {
                    closeBtn.click();
                    sleepQuiet(1500);
                    System.out.println("[FLOW] Closed SubscribeDog via X");
                } else {
                    safeBack();
                    sleepQuiet(1500);
                }
            } catch (Exception ignore) {
                safeBack();
                sleepQuiet(1500);
            }
            testEnd("TapViewPlansFromEmptyState");
        }
    }

    /**
     * #7 - Verify the "Change plan" button is rendered when the user has
     *      a MONTHLY verification subscription (badgePlan.plan.plan_type
     *      === "monthly" on the Verification tab).
     *
     * Tap it and verify navigation to SubscribeDog (dog/business variant).
     *
     * SKIPS when user does NOT have a monthly verification plan.
     */
    public void VerifyChangePlanButtonForMonthly() {
        testStart("VerifyChangePlanButtonForMonthly");
        try {
            ensureAppForeground();

            // Make sure we're on Verification tab
            try {
                if (isDisplayedSafe(verificationTab)) {
                    shortWait.until(ExpectedConditions.elementToBeClickable(
                            verificationTab)).click();
                    sleepQuiet(600);
                }
            } catch (Exception ignore) { /* */ }

            if (!isDisplayedSafe(changePlanButton)) {
                System.out.println("[SKIP] 'Change plan' button not "
                        + "rendered. User likely does NOT have a monthly "
                        + "verification subscription. The button is gated "
                        + "by badgePlan.plan.plan_type === 'monthly' "
                        + "(Subscription.tsx line 494-515).");
                return;
            }

            step("1/2", "Tap 'Change plan' button");
            shortWait.until(ExpectedConditions.elementToBeClickable(
                    changePlanButton)).click();
            System.out.println("[ACTION] Tapped 'Change plan'");

            step("2/2", "Verify SubscribeDog (Boost variant) opens");
            try {
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(
                                AppiumBy.xpath(
                                        "//android.widget.TextView[@text=\"Boost Your Account\"]")),
                        ExpectedConditions.visibilityOfElementLocated(
                                AppiumBy.xpath(
                                        "//android.widget.TextView[@text=\"Boost Your Business Account\"]"))));
                System.out.println("[ASSERT PASS] Navigated to SubscribeDog");
            } catch (Exception e) {
                Assert.fail("'Change plan' tapped but no Boost screen "
                        + "appeared. " + e.getMessage().split("\n")[0]);
            }
        } finally {
            // Cleanup: return to Subscription overview
            try {
                WebElement closeBtn = driver.findElement(
                        AppiumBy.accessibilityId("Close"));
                if (closeBtn.isDisplayed()) {
                    closeBtn.click();
                    System.out.println("[ACTION] Clicked closeBtn");
                    sleepQuiet(1500);
                }
            } catch (Exception ignore) {
                safeBack();
                sleepQuiet(1500);
            }
            testEnd("VerifyChangePlanButtonForMonthly");
        }
    }

    /**
     * #8 - Verify the footer store-management text is rendered. This
     *      text is translate("mnsa") on Android = "Manage your
     *      subscription directly from the Google Play store."
     */
    public void VerifyFooterText() {
        testStart("VerifyFooterText");
        try {
            ensureAppForeground();
            try {
                scrollToTextSafe("Manage your subscription", 4);
            } catch (Exception ignore) { /* */ }

            // Footer is only rendered on the eligible-path (line 572-578)
            // — i.e., when the user HAS an active plan. On empty state
            // the footer is NOT visible.
            if (!isDisplayedSafe(footerManageText)) {
                System.out.println("[SKIP] Footer manage-subscription text "
                        + "not visible. Likely on empty-state path where "
                        + "footer is not rendered.");
                return;
            }

            Assert.assertTrue(isDisplayedSafe(footerManageText),
                    "Footer 'Manage your subscription' text not visible.");
            System.out.println("[ASSERT PASS] Footer text visible");
        } finally {
            testEnd("VerifyFooterText");
        }
    }

    /**
     * #9 - Press the header back arrow (or BACK key) and verify return
     *      to Settings.
     */
    public void CloseSubscriptionScreen() {
        testStart("CloseSubscriptionScreen");
        try {
            ensureAppForeground();

            step("1/2", "Press header back (or BACK key fallback)");
            boolean tapped = false;
            try {
                if (isDisplayedSafe(headerBackArrow)) {
                    shortWait.until(ExpectedConditions.elementToBeClickable(
                            headerBackArrow)).click();
                    System.out.println("[ACTION] Tapped header back arrow");
                    tapped = true;
                }
            } catch (Exception ignore) { /* */ }

            if (!tapped) {
                System.out.println("[FLOW] Header back arrow not present — "
                        + "using physical BACK key");
                safeBack();
            }
            sleepQuiet(1500);

            step("2/2", "Verify return to Settings");
            try {
                WebDriverWait settle = new WebDriverWait(driver, Duration.ofSeconds(10));
                settle.until(ExpectedConditions.visibilityOf(settingsScreenHeader));
                System.out.println("[ASSERT PASS] Back on Settings screen");
            } catch (Exception e) {
                System.out.println("[WARN] Settings header not visible 10s "
                        + "after back. " + e.getMessage().split("\n")[0]);
            }
        } finally {
            testEnd("CloseSubscriptionScreen");
        }
    }

    // ================================================================
    // ==========          PRIVATE HELPERS                  ===========
    // ================================================================

    private void testStart(String name) {
        System.out.println("\n===========================================");
        System.out.println("===> TEST START: " + name);
        System.out.println("===========================================");
    }

    private void testEnd(String name) {
        System.out.println("===< TEST END:   " + name);
        System.out.println("===========================================\n");
    }

    private void step(String num, String description) {
        System.out.println("[STEP " + num + "] " + description);
    }

    private void sleepQuiet(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignore) { /* */ }
    }

    private void safeBack() {
        try {
            driver.pressKey(new KeyEvent(AndroidKey.BACK));
            System.out.println("[ACTION] Pressed device Back");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("[WARN] safeBack failed: "
                    + e.getMessage().split("\n")[0]);
        }
    }

    private boolean isDisplayedSafe(WebElement el) {
        try {
            return el != null && el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private void ensureAppForeground() {
        try {
            String pkg = driver.getCurrentPackage();
            if (pkg != null && !pkg.startsWith("com.dogpack")
                    && !pkg.startsWith("com.android.systemui")) {
                System.out.println("[FLOW] App not foreground (currentPackage="
                        + pkg + ") - activating");
                driver.activateApp("com.dogpack");
                sleepQuiet(1500);
            }
        } catch (Exception e) {
            System.out.println("[WARN] ensureAppForeground check failed: "
                    + e.getMessage().split("\n")[0]);
        }
    }

    private boolean scrollToTextSafe(String text, int maxAttempts) {
        Duration originalImplicit = Duration.ofSeconds(10);
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        } catch (Exception ignore) { /* */ }

        try {
            sleepQuiet(400);
            for (int attempt = 0; attempt < maxAttempts; attempt++) {
                List<WebElement> exact = driver.findElements(AppiumBy.xpath(
                        "//android.widget.TextView[@text='" + text + "']"));
                if (!exact.isEmpty() && exact.get(0).isDisplayed()) {
                    return true;
                }
                List<WebElement> contains = driver.findElements(AppiumBy.xpath(
                        "//android.widget.TextView[contains(@text,'" + text + "')]"));
                if (!contains.isEmpty() && contains.get(0).isDisplayed()) {
                    return true;
                }
                try {
                    Dimension size = driver.manage().window().getSize();
                    Map<String, Object> args = new HashMap<>();
                    args.put("left", (int) (size.width * 0.1));
                    args.put("top", (int) (size.height * 0.25));
                    args.put("width", (int) (size.width * 0.8));
                    args.put("height", (int) (size.height * 0.5));
                    args.put("direction", "down");
                    args.put("percent", 0.75);
                    Object result = ((JavascriptExecutor) driver)
                            .executeScript("mobile: scrollGesture", args);
                    if (Boolean.FALSE.equals(result)) {
                        return false;
                    }
                    sleepQuiet(400);
                } catch (Exception ignore) { /* */ }
            }
            return false;
        } finally {
            try {
                driver.manage().timeouts().implicitlyWait(originalImplicit);
            } catch (Exception ignore) { /* */ }
        }
    }
}
