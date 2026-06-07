package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.MarketplacePage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.ProfileSwitcherPage;
import org.rahulshettyacademy.pageObjects.android.SettingsAndActivityPage;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_Marketplace_Business - Marketplace location-change + checkout
 * flow for the BUSINESS entity (Tipper9Business).
 *
 * Mirrors the dog-entity marketplace flow exactly (Dogpack_Marketplace),
 * just adapted to run for the business user after switching entities.
 *
 * SETUP CHAIN (priorities 1-2):
 *     #1  Login as shared account (tip@yopmail.com) - defaults to dog
 *     #2  Switch entity to Tipper9Business via profile switcher
 *
 * FEATURE CHAIN (priorities 3-28): mirrors the 25 active marketplace
 * tests from Dogpack_Marketplace. All test methods use the
 * _MarketplaceBiz suffix to prevent any name collisions with the
 * dog-entity marketplace tests.
 *
 * COMMENTED-OUT TESTS (mirrors dog class):
 *   - ClickAddressListLocator_MarketplaceBiz: the my-profile-address
 *     XPath lands directly on the search-textbox screen, skipping the
 *     intermediate address-list step entirely.
 *   - AssertTwoItemsTotal_MarketplaceBiz: temporarily disabled while
 *     we work on other features. The downstream
 *     ClickCheckout_Final_MarketplaceBiz dependency was redirected to
 *     ClickBack_FromCheckout2_MarketplaceBiz so the chain continues.
 *
 * REUSE:
 *   - MarketplacePage methods are entity-agnostic and reused 1:1
 *   - ProfileSwitcherPage.SwitchToFirstBusinessProfile() handles the
 *     entity switch (battle-tested across all other _Business classes)
 *   - Page object methods, locators, scroll strategy - all unchanged
 *
 * CLEANUP:
 *   Mirrors the dog-marketplace @AfterMethod cleanup pattern:
 *     - Trigger: priority >= 12 test failure OR successful completion of
 *       ClickOK_MarketplaceBiz (the final test, now priority 28)
 *     - Action: terminateApp + activateApp + re-login + switch to
 *       business entity + revert location to India
 *     - Guard: cleanupAlreadyRan flag ensures one-shot execution
 *
 * EXECUTION ORDER (suite XML):
 *   ... existing 8 classes including Dogpack_Marketplace ...
 *   9. Dogpack_Marketplace_Business    (THIS CLASS - business Marketplace)
 */
public class Dogpack_Marketplace_Business extends AndroidBaseTest {

    LoginPage login;
    ProfilePage profile;
    ProfileSwitcherPage profileSwitcher;
    SettingsAndActivityPage settings;
    MarketplacePage marketplace;

    /**
     * Cleanup state: guards against multiple cleanup invocations.
     * @AfterMethod fires after every test, but cleanup should run
     * only once per class execution.
     */
    private boolean cleanupAlreadyRan = false;

    /**
     * Credentials captured during LoginForMarketplace_Business so the
     * cleanup flow can re-login after the terminate+activate app
     * relaunch.
     */
    private String savedEmail;
    private String savedPassword;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        profile = new ProfilePage(driver);
        profileSwitcher = new ProfileSwitcherPage(driver);
        settings = new SettingsAndActivityPage(driver);
        marketplace = new MarketplacePage(driver);
    }

    // ================================================================
    // ==========    SETUP CHAIN (login + switch entity)        =======
    // ================================================================

    /** #1 - Login + navigate to Profile screen (defaults to dog entity). */
    @Test(priority = 1, dataProvider = "getBusinessUserLogin",
            groups = { "Smoke", "Regression" })
    public void LoginForMarketplace_Business(HashMap<String, String> input)
            throws InterruptedException {
        System.out.println("[INFO]   Logging in as shared account (will "
                + "switch to business entity next): " + input.get("email"));
        // Capture credentials for the @AfterMethod cleanup flow, so it
        // can re-login after killing+relaunching the app without
        // re-reading the JSON test-data file.
        this.savedEmail = input.get("email");
        this.savedPassword = input.get("password");
        login.scrollToLogin();
        login.NavigateToLogin();
        login.setEmailPassword(input.get("email"), input.get("password"));
        login.clickOnLoginSubmit();
        login.CompleteLoginProccess();
        login.HandleCustomDialog(0, 0);
        profile.navigateToProfileScreen();
    }

    /**
     * #2 - Switch active entity from dog to Tipper9Business via the
     *      in-app profile switcher. Delegated to ProfileSwitcherPage
     *      (the same method other _Business classes use).
     */
    @Test(priority = 2,
            dependsOnMethods = { "LoginForMarketplace_Business" },
            groups = { "Smoke", "Regression" })
    public void SwitchToFirstBusinessProfile_MarketplaceBiz() {
        profileSwitcher.SwitchToFirstBusinessProfile();
    }

    // ================================================================
    // ==========    LOCATION-CHANGE FLOW (Canada)              =======
    // ================================================================

    /** #3 - Open Settings and activity via hamburger. */
    @Test(priority = 3,
            dependsOnMethods = { "SwitchToFirstBusinessProfile_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void NavigatesSettingActivityScreen_MarketplaceBiz()
            throws InterruptedException {
        settings.NavigatesToSettingAndActivityScreen();
    }

    /** #4 - Tap "Account and info" row. */
    @Test(priority = 4,
            dependsOnMethods = { "NavigatesSettingActivityScreen_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickAccountAndInfo_MarketplaceBiz() {
        marketplace.ClickAccountAndInfo();
    }

    /** #5 - Handle the Android location permission popup if present. */
    @Test(priority = 5,
            dependsOnMethods = { "ClickAccountAndInfo_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void HandleLocationPermissionPopup_MarketplaceBiz() {
        marketplace.HandleLocationPermissionPopup();
    }

    /** #6 - Tap the location dialog button (opens edit location row). */
    @Test(priority = 6,
            dependsOnMethods = { "HandleLocationPermissionPopup_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickLocationDialogButton_MarketplaceBiz() {
        marketplace.ClickLocationDialogButton();
    }

    /*
     * COMMENTED OUT - same as dog-class. The my-profile-address XPath
     * used by ClickLocationDialogButton lands directly on the search
     * textbox screen, skipping the address-list intermediate.
     *
    @Test(priority = 7,
            dependsOnMethods = { "ClickLocationDialogButton_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickAddressListLocator_MarketplaceBiz() {
        marketplace.ClickAddressListLocator();
    }
    */

    /** #8 - Type "Canada" into the location search textbox. */
    @Test(priority = 8,
            dependsOnMethods = { "ClickLocationDialogButton_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void EnterLocationSearchText_MarketplaceBiz() {
        marketplace.EnterLocationSearchText();
    }

    /** #9 - Tap the "Canada" suggestion in the dropdown. */
    @Test(priority = 9,
            dependsOnMethods = { "EnterLocationSearchText_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void SelectCanadaSuggestion_MarketplaceBiz() {
        marketplace.SelectCanadaSuggestion();
    }

    /** #10 - Scroll to the bottom of the location form (UPDATE button). */
    @Test(priority = 10,
            dependsOnMethods = { "SelectCanadaSuggestion_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ScrollToBottom_MarketplaceBiz() {
        marketplace.ScrollToBottom();
    }

    /** #11 - Tap the UPDATE button to commit the location change. */
    @Test(priority = 11,
            dependsOnMethods = { "ScrollToBottom_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickUpdate_MarketplaceBiz() {
        marketplace.ClickUpdate();
    }

    /** #12 - Assert "Shop" text is visible (marketplace surfaced). */
    @Test(priority = 12,
            dependsOnMethods = { "ClickUpdate_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void AssertShopTextVisible_MarketplaceBiz() {
        marketplace.AssertShopTextVisible();
    }

    // ================================================================
    // ==========    BROWSE + CHECKOUT CHAIN                    =======
    // ================================================================

    /** #13 - Tap Shop card to enter the marketplace home. */
    @Test(priority = 13,
            dependsOnMethods = { "AssertShopTextVisible_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickShop_MarketplaceBiz() {
        marketplace.ClickShop();
    }

    /*
     * =================================================================
     * LEGACY BUSINESS FLOW (priorities 14-28) - COMMENTED OUT
     * =================================================================
     * The original browse + simple-checkout flow for business has
     * been disabled to match the dog-class rebuild. The new flow
     * (search-by-keyword + full payment + Thank You confirmation)
     * lives below starting at priority 14.
     *
     * If the new flow needs to be abandoned and the legacy flow
     * restored, simply delete the surrounding block-comment
     * delimiters (the slash-star above and star-slash below).
     *
     * Tests inside this block:
     *   14. ClickSeeAll_MarketplaceBiz
     *   15. ClickBuyNowFirst_MarketplaceBiz
     *   16. ClickCheckout_AfterFirstBuy_MarketplaceBiz
     *   17. AssertCheckoutHeader_MarketplaceBiz
     *   18. AssertPoweredByStripe_MarketplaceBiz
     *   19. ClickBack_FromCheckout1_MarketplaceBiz
     *   20. AssertMyCartHeader_MarketplaceBiz
     *   21. ClickBack_FromCart_MarketplaceBiz
     *   22. ClickBuyNowSecond_MarketplaceBiz
     *   23. ClickCheckout_AfterSecondBuy_MarketplaceBiz
     *   24. ClickBack_FromCheckout2_MarketplaceBiz
     *   25. AssertTwoItemsTotal_MarketplaceBiz (already was disabled)
     *   26. ClickCheckout_Final_MarketplaceBiz
     *   27. ClickPlaceYourOrder_MarketplaceBiz
     *   28. ClickOK_MarketplaceBiz
     * =================================================================

    @Test(priority = 14,
            dependsOnMethods = { "ClickShop_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickSeeAll_MarketplaceBiz() {
        marketplace.ClickSeeAll();
    }

    @Test(priority = 15,
            dependsOnMethods = { "ClickSeeAll_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickBuyNowFirst_MarketplaceBiz() {
        marketplace.ClickBuyNow(1);
    }

    @Test(priority = 16,
            dependsOnMethods = { "ClickBuyNowFirst_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickCheckout_AfterFirstBuy_MarketplaceBiz() {
        marketplace.ClickCheckout();
    }

    @Test(priority = 17,
            dependsOnMethods = { "ClickCheckout_AfterFirstBuy_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void AssertCheckoutHeader_MarketplaceBiz() {
        marketplace.AssertHeaderTitle("Checkout");
    }

    @Test(priority = 18,
            dependsOnMethods = { "AssertCheckoutHeader_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void AssertPoweredByStripe_MarketplaceBiz() {
        marketplace.AssertPoweredByStripeImageVisible();
    }

    @Test(priority = 19,
            dependsOnMethods = { "AssertPoweredByStripe_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickBack_FromCheckout1_MarketplaceBiz() {
        marketplace.ClickBackButton();
    }

    @Test(priority = 20,
            dependsOnMethods = { "ClickBack_FromCheckout1_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void AssertMyCartHeader_MarketplaceBiz() {
        // Note: actual header text is "My cart" (lowercase 'c').
        marketplace.AssertHeaderTitle("My cart");
    }

    @Test(priority = 21,
            dependsOnMethods = { "AssertMyCartHeader_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickBack_FromCart_MarketplaceBiz() {
        marketplace.ClickBackButton();
    }

    @Test(priority = 22,
            dependsOnMethods = { "ClickBack_FromCart_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickBuyNowSecond_MarketplaceBiz() {
        marketplace.ClickBuyNow(2);
    }

    @Test(priority = 23,
            dependsOnMethods = { "ClickBuyNowSecond_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickCheckout_AfterSecondBuy_MarketplaceBiz() {
        marketplace.ClickCheckout();
    }

    @Test(priority = 24,
            dependsOnMethods = { "ClickCheckout_AfterSecondBuy_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickBack_FromCheckout2_MarketplaceBiz() throws InterruptedException {
        // 3s settle before the back tap. Mirrors dog-class fix - the
        // back button is in the tree but not yet responsive
        // immediately after ClickCheckout_AfterSecondBuy lands.
        Thread.sleep(3000);
        marketplace.ClickBackButton();
    }

    // COMMENTED OUT - AssertTwoItemsTotal_MarketplaceBiz (legacy)
    // Now part of the larger legacy-block comment-out below.
    @Test(priority = 25,
            dependsOnMethods = { "ClickBack_FromCheckout2_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void AssertTwoItemsTotal_MarketplaceBiz() {
        marketplace.AssertTwoItemsTotalText();
    }

    @Test(priority = 26,
            dependsOnMethods = { "ClickBack_FromCheckout2_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickCheckout_Final_MarketplaceBiz() {
        marketplace.ClickCheckout();
    }

    @Test(priority = 27,
            dependsOnMethods = { "ClickCheckout_Final_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickPlaceYourOrder_MarketplaceBiz() {
        marketplace.ClickPlaceYourOrder();
    }

    @Test(priority = 28,
            dependsOnMethods = { "ClickPlaceYourOrder_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickOK_MarketplaceBiz() {
        marketplace.ClickOK();
    }

     * =================================================================
     * END OF LEGACY BUSINESS FLOW BLOCK
     * =================================================================
     */

    // ================================================================
    // ==========    NEW BUSINESS MARKETPLACE FLOW              =======
    // ================================================================
    // Mirrors the dog-class new payment flow exactly, just with the
    // _MarketplaceBiz suffix on every test method name. All page-
    // object methods (ClickSearchBox, EnterCardNumber, etc.) are
    // reused from MarketplacePage with no changes - they're entity-
    // agnostic and work identically for dog and business contexts.
    // ================================================================

    /** #14 (new flow #2) - Tap the search box in the marketplace Header. */
    @Test(priority = 14,
            dependsOnMethods = { "ClickShop_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickSearchBox_MarketplaceBiz() {
        marketplace.ClickSearchBox();
    }

    /** #15 (new flow #3) - Type "card" into the search box. */
    @Test(priority = 15,
            dependsOnMethods = { "ClickSearchBox_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void EnterSearchText_MarketplaceBiz() {
        marketplace.EnterSearchBoxText("card");
    }

    /** #16 (new flow #4) - Press Enter to submit the search. */
    @Test(priority = 16,
            dependsOnMethods = { "EnterSearchText_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void PressEnterToSubmitSearch_MarketplaceBiz() {
        marketplace.PressEnterToSubmitSearch();
    }

    /** #17 (new flow #5) - Tap Buy Now on the first product in results. */
    @Test(priority = 17,
            dependsOnMethods = { "PressEnterToSubmitSearch_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickBuyNow_MarketplaceBiz() {
        marketplace.ClickBuyNow(1);
    }

    /** #18 (new flow #6) - Tap Checkout on the cart screen. */
    @Test(priority = 18,
            dependsOnMethods = { "ClickBuyNow_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickCheckout_MarketplaceBiz() {
        marketplace.ClickCheckout();
    }

    /**
     * #19 (new flow #7) - Tap "Place your order" - navigates to the
     * Stripe PaymentSheet card form.
     */
    @Test(priority = 19,
            dependsOnMethods = { "ClickCheckout_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickPlaceYourOrder1_MarketplaceBiz() {
        marketplace.ClickPlaceYourOrder();
    }

    /** #20 (new flow #8) - Tap card number field on Stripe PaymentSheet. */
    @Test(priority = 20,
            dependsOnMethods = { "ClickPlaceYourOrder1_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickCardNumberBox_MarketplaceBiz() {
        marketplace.ClickCardNumberBox();
    }

    /** #21 (new flow #9) - Type Visa test card "4111111111111111". */
    @Test(priority = 21,
            dependsOnMethods = { "ClickCardNumberBox_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void EnterCardNumber_MarketplaceBiz() {
        marketplace.EnterCardNumber("4111111111111111");
    }

    /** #22 (new flow #10) - Type expiry "12/30". */
    @Test(priority = 22,
            dependsOnMethods = { "EnterCardNumber_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void EnterMMYY_MarketplaceBiz() {
        marketplace.EnterMMYY("12/30");
    }

    /** #23 (new flow #11) - Type CVC "123". */
    @Test(priority = 23,
            dependsOnMethods = { "EnterMMYY_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void EnterCVC_MarketplaceBiz() {
        marketplace.EnterCVC("123");
    }

    /**
     * #24 (new flow #12) - Tap the Continue button on Stripe
     * PaymentSheet, submitting card details and closing the sheet.
     */
    @Test(priority = 24,
            dependsOnMethods = { "EnterCVC_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickContinueButton_MarketplaceBiz() {
        marketplace.ClickContinueButtonOnPaymentSheet();
    }

    /**
     * #25 (new flow #13) - Tap "Place your order" a second time to
     * submit the order with the now-saved payment method.
     */
    @Test(priority = 25,
            dependsOnMethods = { "ClickContinueButton_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickPlaceYourOrder2_MarketplaceBiz() {
        marketplace.ClickPlaceYourOrder();
    }

    /**
     * #26 (new flow #14) - Assert generic success-order confirmation
     * message visible. Same text as dog class.
     */
    @Test(priority = 26,
            dependsOnMethods = { "ClickPlaceYourOrder2_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void AssertThankYouMessage_MarketplaceBiz() {
        marketplace.AssertThankYouMessageVisible();
    }

    /**
     * #27 (new flow #15) - Tap "Continue Shopping" to return to the
     * marketplace home. Final test in the new business flow.
     */
    @Test(priority = 27,
            dependsOnMethods = { "AssertThankYouMessage_MarketplaceBiz" },
            groups = { "Smoke", "Regression" })
    public void ClickContinueShopping_MarketplaceBiz() {
        marketplace.ClickContinueShopping();
    }

    // ================================================================
    // ==========    CLEANUP - REVERT LOCATION TO INDIA         =======
    // ================================================================
    // Mirrors dog-class cleanup with these adaptations for business:
    //   - Trigger priority threshold is 12 (AssertShopTextVisible_MarketplaceBiz)
    //     instead of 11. This is because we have an extra setup test
    //     (SwitchToFirstBusinessProfile_MarketplaceBiz at priority 2)
    //     which shifts all subsequent priorities by 1.
    //   - Trigger method-name for success path is ClickOK_MarketplaceBiz
    //     (priority 28) instead of ClickOK_Marketplace.
    //   - The cleanup flow itself still ends in India (the same India
    //     for both dog and business - reverts the shared account's
    //     location, regardless of which entity was active).
    //   - After the app relaunch + login, we walk through the same
    //     Profile -> hamburger -> Account -> India revert sequence as
    //     the dog cleanup. We do NOT switch back to business entity
    //     during cleanup because the location revert works against
    //     the shared user's location setting, not the per-entity one.
    // ================================================================

    @AfterMethod(alwaysRun = true)
    public void cleanupIfNeeded(ITestResult result) {
        if (cleanupAlreadyRan) {
            return;
        }

        int priority = result.getMethod().getPriority();
        String methodName = result.getMethod().getMethodName();
        int status = result.getStatus();
        boolean failed = (status == ITestResult.FAILURE);
        boolean isFinalTestPassed = "ClickContinueShopping_MarketplaceBiz"
                .equals(methodName)
                && (status == ITestResult.SUCCESS);

        // Threshold is priority >= 12 (was 11 for dog) because business
        // class adds an extra setup test at priority 2.
        boolean shouldCleanup =
                (failed && priority >= 12) || isFinalTestPassed;

        if (!shouldCleanup) {
            return;
        }

        cleanupAlreadyRan = true;

        if (failed) {
            System.out.println("[CLEANUP] Triggered by FAILURE of '"
                    + methodName + "' (priority " + priority
                    + "). Will revert location to India.");
        } else {
            System.out.println("[CLEANUP] Triggered by SUCCESSFUL "
                    + "completion of final test '" + methodName
                    + "'. Will revert location to India.");
        }

        try {
            runIndiaRevertCleanup();
            System.out.println("[CLEANUP] India revert complete.");
        } catch (RuntimeException re) {
            System.out.println("[CLEANUP] !!! REVERT FAILED !!! "
                    + "Manual intervention may be required to restore "
                    + "the user's location to India before re-running "
                    + "dependent tests. Exception: " + re.getMessage());
            re.printStackTrace(System.out);
            throw re;
        } catch (Exception e) {
            System.out.println("[CLEANUP] !!! REVERT FAILED !!! "
                    + "(non-runtime exception) " + e.getMessage());
            e.printStackTrace(System.out);
            throw new RuntimeException(
                    "Cleanup failed (wrapped non-runtime exception)", e);
        }
    }

    /**
     * Executes the India-revert sequence (mirrors dog-class cleanup):
     *   1. Terminate + activate com.dogpack
     *   2. Re-login with saved credentials (defensive try-catch in case
     *      app retains session after relaunch)
     *   3-9. Profile -> hamburger -> Account and info -> location row ->
     *        type "India" -> select India -> scroll -> UPDATE
     *  10. Assert "Search" text is visible (confirms revert took effect)
     *
     * Note: we do NOT switch back to business entity during cleanup
     * because the user-level location setting is shared across both
     * entities for this account. Reverting once is sufficient.
     */
    private void runIndiaRevertCleanup() throws InterruptedException {
        // Step 1: app relaunch for known clean state
        System.out.println("[CLEANUP] Terminating com.dogpack...");
        driver.terminateApp("com.dogpack");
        Thread.sleep(2000);
        System.out.println("[CLEANUP] Activating com.dogpack...");
        driver.activateApp("com.dogpack");
        Thread.sleep(4000);

        // Step 2 (conditional): re-login if the app is showing the
        // login screen. After terminateApp+activateApp some Android
        // versions/configs DROP the user's session while others
        // PRESERVE it. Try the login sequence; if any step throws
        // (because there's no login screen), assume already logged in.
        System.out.println("[CLEANUP] Probing for login screen...");
        try {
            login.scrollToLogin();
            login.NavigateToLogin();
            login.setEmailPassword(savedEmail, savedPassword);
            login.clickOnLoginSubmit();
            login.CompleteLoginProccess();
            login.HandleCustomDialog(0, 0);
            System.out.println("[CLEANUP] Re-login successful (app was "
                    + "logged out after relaunch).");
        } catch (Exception loginEx) {
            System.out.println("[CLEANUP] Login sequence skipped - app "
                    + "appears to be already logged in (session "
                    + "survived app relaunch). Proceeding to Profile "
                    + "tab. Underlying exception (informational only): "
                    + loginEx.getClass().getSimpleName() + ": "
                    + loginEx.getMessage());
        }

        // Steps 3-9 from cleanup spec (Profile -> ... -> UPDATE):
        marketplace.ClickProfileTabByText();
        marketplace.ClickHamburgerMenu();
        marketplace.ClickAccountAndInfoByDesc();

        // Defensive: location permission popup might re-appear after
        // app relaunch. HandleLocationPermissionPopup is silent no-op
        // if popup not present.
        marketplace.HandleLocationPermissionPopup();

        marketplace.ClickLocationDialogButton();
        marketplace.EnterLocationSearchText("India");
        marketplace.SelectCountrySuggestion("India");
        marketplace.ScrollToBottom();
        marketplace.ClickUpdate();

        // Step 10: assert Search text visible. Will throw if not.
        marketplace.AssertSearchTextVisible();

        // -----------------------------------------------------------
        // DOG PROFILE REVERT (steps 11-14) - business -> dog switch
        //
        // After India revert succeeds, switch the active entity back
        // to dog. This is defensive cleanup - if the suite is
        // re-run, or if someone opens the app manually right after,
        // they shouldn't inherit business state.
        //
        // Wrapped in try-catch so a failure here is LOGGED ONLY and
        // does NOT throw. The location revert (the more important
        // server-side state) has already succeeded by this point.
        // Profile-revert failures are visible in the console + the
        // Surefire output, but don't fail the @AfterMethod cleanup.
        //
        // Uses the 4 verified methods on ProfileSwitcherPage that
        // were validated in Dogpack_Marketplace_ProfileSwitch_Test.
        // -----------------------------------------------------------
        try {
            System.out.println("[CLEANUP] === Dog profile revert "
                    + "(business -> dog switch) ===");
            profileSwitcher.ClickProfileTabByTextView();
            profileSwitcher.ClickArrowDownForProfileSwitching();
            profileSwitcher.ClickDogProfileSwitcher();
            profileSwitcher.ClickSelectProfileForDog();
            System.out.println("[CLEANUP] Dog profile switch complete.");
        } catch (Exception dogSwitchEx) {
            System.out.println("[CLEANUP] !!! Dog profile revert FAILED "
                    + "(non-fatal, location already reverted): "
                    + dogSwitchEx.getClass().getSimpleName() + ": "
                    + dogSwitchEx.getMessage()
                    + ". User may be left on business profile - "
                    + "manual intervention recommended before next run.");
            dogSwitchEx.printStackTrace(System.out);
        }
    }

    /**
     * Provides login credentials. Returns the SAME shared account
     * (tip@yopmail.com) that all _Business classes use. After step
     * #2 the active entity will be Tipper9Business.
     */
    @DataProvider(name = "getBusinessUserLogin")
    public Object[][] getBusinessUserLogin() throws IOException {
        String jsonPath = Paths.get(
                System.getProperty("user.dir"),
                "src", "test", "java", "org", "rahulshettyacademy",
                "testData", "LoginData.json").toString();
        List<HashMap<String, String>> data = getJsonData(jsonPath);
        return new Object[][] { { data.get(0) } };
    }
}