package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.SettingsAndActivityPage;
import org.rahulshettyacademy.pageObjects.android.SubscriptionPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_Subscription - Test suite for the "Subscription" overview
 * screen (NOT the SubscribeDog purchase pages — the screen that shows
 *  the user's currently active plan or upsells from an empty state).
 *
 * APP SOURCE COVERED:
 *   - screen/MenuScreen.js (entry row "subn" -> "Subscription" navigation)
 *   - screen/Subscription/Subscription.tsx (entire screen)
 *
 * ================================================================
 *  PRIORITY -> METHOD mapping:
 * ================================================================
 *   Priority   Method                                  Status     Notes
 *   --------   --------------------------------------- ---------- --------------------
 *      1       PrerequestFunctionsforSubscription      ACTIVE     login + profile
 *      2       NavigatesSettingActivityScreen          ACTIVE     opens settings
 *      3       NavigateToSubscriptionFromMenu          ACTIVE     menu row tap
 *      4       VerifyTabsRenderedFunctionality         ACTIVE     both tabs present
 *      5       SwitchToAIImageCreditsTabFunctionality  ACTIVE     tab switch
 *      6       SwitchBackToVerificationTabFunctionality ACTIVE    tab switch
 *      7       VerifyActivePlanOrEmptyState            ACTIVE     state-based assert
 *      8       TapViewPlansFromEmptyState              ACTIVE     navigates to SubscribeDog
 *                                                                 (SKIPS if user has active plan)
 *      9       VerifyChangePlanButtonForMonthly        ACTIVE     monthly-plan users only
 *     10       VerifyFooterText                        ACTIVE     active-plan users only
 *     11       CloseSubscriptionScreen                 ACTIVE     return to Settings
 *
 *  Tests 8 and 9 are SCENARIO-DEPENDENT and will SKIP cleanly when the
 *  user's plan state doesn't match (empty/active). To exercise both
 *  paths, run the suite once with an unsubscribed user and once with
 *  a monthly-subscribed user.
 *
 *  PRE-REQUISITES FOR THIS SUITE:
 *   - Test user MAY be any verification state — the Subscription row
 *     is NOT gated by is_verified or isArSupported.
 *   - For business users: business account MUST be approved.
 */
public class Dogpack_Subscription extends AndroidBaseTest {

    LoginPage login;
    ProfilePage profile;
    SettingsAndActivityPage settings;
    SubscriptionPage subscription;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        profile = new ProfilePage(driver);
        settings = new SettingsAndActivityPage(driver);
        subscription = new SubscriptionPage(driver);
    }

    // ================================================================
    // ==========    LOGIN + ENTRY  (priorities 1-2)            =======
    // ================================================================

    /** #1 - Pre-requisite: log in and land on the Profile screen. */
    @Test(priority = 1, dataProvider = "getDataSuccessfullLogin",
            groups = { "Smoke", "Regression" })
    public void PrerequestFunctionsforSubscription(HashMap<String, String> input)
            throws InterruptedException {
        login.scrollToLogin();
        login.NavigateToLogin();
        login.setEmailPassword(input.get("email"), input.get("password"));
        login.clickOnLoginSubmit();
        login.CompleteLoginProccess();
        login.HandleCustomDialog(0, 0);
        profile.navigateToProfileScreen();
    }

    /** #2 - Open Settings and activity via hamburger. */
    @Test(priority = 2, groups = { "Smoke", "Regression" })
    public void NavigatesSettingActivityScreen() throws InterruptedException {
        settings.NavigatesToSettingAndActivityScreen();
    }

    // ================================================================
    // ==========   SUBSCRIPTION FEATURE TESTS (priorities 3-11) ======
    // ================================================================

    /** #3 - Tap "Subscription" in the Settings menu. */
    @Test(priority = 3, groups = { "Smoke", "Regression" })
    public void NavigateToSubscriptionFromMenu() throws InterruptedException {
        subscription.NavigateToSubscriptionFromMenu();
    }

    /** #4 - Both tabs (Verification + AI image credits) are rendered. */
    @Test(priority = 4, groups = { "Smoke", "Regression" })
    public void VerifyTabsRenderedFunctionality() throws InterruptedException {
        subscription.VerifyTabsRendered();
    }

    /** #5 - Switch to "AI image credits" tab and verify content. */
    @Test(priority = 5, groups = { "Regression" })
    public void SwitchToAIImageCreditsTabFunctionality() throws InterruptedException {
        subscription.SwitchToAIImageCreditsTab();
    }

    /** #6 - Switch back to "Verification" tab. */
    @Test(priority = 6, groups = { "Regression" })
    public void SwitchBackToVerificationTabFunctionality() throws InterruptedException {
        subscription.SwitchBackToVerificationTab();
    }

    /**
     * #7 - Verify EITHER the active plan banner OR the empty-state View
     *      Plans button is rendered (mutually exclusive paths).
     */
    @Test(priority = 7, groups = { "Smoke", "Regression" })
    public void VerifyActivePlanOrEmptyState() throws InterruptedException {
        subscription.VerifyActivePlanOrEmptyState();
    }

    /**
     * #8 - From empty state, tap "View Plans" and verify navigation to
     *      SubscribeDog. SKIPS when user has an active plan.
     */
    @Test(priority = 8, groups = { "Regression" })
    public void TapViewPlansFromEmptyState() throws InterruptedException {
        subscription.TapViewPlansFromEmptyState();
    }

    /**
     * #9 - Verify "Change plan" button for monthly-subscribed users on
     *      the Verification tab. SKIPS when user is not on monthly plan.
     */
    @Test(priority = 9, groups = { "Regression" })
    public void VerifyChangePlanButtonForMonthly() throws InterruptedException {
        subscription.VerifyChangePlanButtonForMonthly();
    }

    /**
     * #10 - Verify the footer store-management text. SKIPS in the empty
     *       state (footer is only rendered on the eligible path).
     */
    @Test(priority = 10, groups = { "Regression" })
    public void VerifyFooterText() throws InterruptedException {
        subscription.VerifyFooterText();
    }

    /** #11 - Press back arrow and verify return to Settings. */
    @Test(priority = 11, groups = { "Smoke", "Regression" })
    public void CloseSubscriptionScreen() throws InterruptedException {
        subscription.CloseSubscriptionScreen();
    }

    // ================================================================
    // ==========           DATA PROVIDERS                       ======
    // ================================================================

    @DataProvider
    public Object[][] getDataSuccessfullLogin() throws IOException {
        String jsonPath = Paths.get(
                System.getProperty("user.dir"),
                "src", "test", "java", "org", "rahulshettyacademy",
                "testData", "LoginData.json").toString();

        List<HashMap<String, String>> data = getJsonData(jsonPath);

        return new Object[][] { { data.get(0) } };
    }
}
