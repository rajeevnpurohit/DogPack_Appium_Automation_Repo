package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.ProfileSwitcherPage;
import org.rahulshettyacademy.pageObjects.android.SettingsAndActivityPage;
import org.rahulshettyacademy.pageObjects.android.SubscriptionFlowPage;
import org.rahulshettyacademy.pageObjects.android.SubscriptionFlowPage.AccountType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_Subscription_Business - Subscription feature flow for the
 * BUSINESS entity (Tipper9Business).
 *
 * FLOW (15 total tests = 3 setup + 12 feature steps):
 *
 *   SETUP CHAIN:
 *     #1  Login as shared account (tip@yopmail.com)
 *     #2  Switch entity to Tipper9Business
 *     #3  Open Settings and activity via hamburger
 *
 *   FEATURE CHAIN:
 *     #4  Tap Subscription row in Settings (waits for "View Plans"
 *         anchor inside the page-object method)
 *     #5  Tap Verification card
 *     #6  Tap View Plans
 *     #7  Assert "Boost Your Business Account" header
 *     #8  Tap close (X)
 *     #9  Tap Magic image credits card
 *     #10 Tap View Plans
 *     #11 Assert "Unlock more magic credits" header
 *     #12 Tap close (X)
 *     #13 Tap back arrow (first ImageView on screen)
 *     #14 Assert "Settings and activity" header (verifies back nav)
 *
 * Note: steps 4 and 14 in the user's 1-12 spec are folded into the
 * page-object methods (Subscription tap also waits for View Plans;
 * back-arrow tap also waits for Settings header), so the test count
 * doesn't grow beyond 15.
 *
 * SESSION LIFECYCLE:
 *   Per-class driver via AndroidBaseTest @BeforeClass/@AfterClass.
 *   noReset=false ensures a fresh splash-screen session start.
 *
 * EXECUTION ORDER (in suite XML):
 *   1. Dogpack_BoostAccount
 *   2. Dogpack_AiPhotoGeneration
 *   3. Dogpack_BusinessLogin
 *   4. Dogpack_BoostAccount_Business
 *   5. Dogpack_AiPhotoGeneration_Business
 *   6. Dogpack_Subscription_Business    (THIS CLASS - business Subscription)
 */
public class Dogpack_Subscription_Business extends AndroidBaseTest {

    LoginPage login;
    ProfilePage profile;
    ProfileSwitcherPage profileSwitcher;
    SettingsAndActivityPage settings;
    SubscriptionFlowPage subFlow;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        profile = new ProfilePage(driver);
        profileSwitcher = new ProfileSwitcherPage(driver);
        settings = new SettingsAndActivityPage(driver);
        // AccountType.BUSINESS -> Verification header assertion
        // resolves to "Boost Your Business Account" (not the dog
        // default of "Boost Your Account").
        subFlow = new SubscriptionFlowPage(driver, AccountType.BUSINESS);
    }

    // ================================================================
    // ==========    SETUP CHAIN (login + switch + settings)    =======
    // ================================================================

    @Test(priority = 1, dataProvider = "getBusinessUserLogin",
            groups = { "Smoke", "Regression" })
    public void LoginForBusinessSubscription(HashMap<String, String> input)
            throws InterruptedException {
        System.out.println("[INFO]   Logging in as shared account: "
                + input.get("email"));
        login.scrollToLogin();
        login.NavigateToLogin();
        login.setEmailPassword(input.get("email"), input.get("password"));
        login.clickOnLoginSubmit();
        login.CompleteLoginProccess();
        login.HandleCustomDialog(0, 0);
        profile.navigateToProfileScreen();
    }

    @Test(priority = 2, dependsOnMethods = { "LoginForBusinessSubscription" },
            groups = { "Smoke", "Regression" })
    public void SwitchToBusinessForSubscription() {
        profileSwitcher.SwitchToFirstBusinessProfile();
    }

    @Test(priority = 3, dependsOnMethods = { "SwitchToBusinessForSubscription" },
            groups = { "Smoke", "Regression" })
    public void NavigatesSettingActivityScreen_Subscription_Business()
            throws InterruptedException {
        settings.NavigatesToSettingAndActivityScreen();
    }

    // ================================================================
    // ==========    FEATURE CHAIN (Subscription flow)          =======
    // ================================================================

    /** #4 - Tap Subscription row (and wait for View Plans inside). */
    @Test(priority = 4,
            dependsOnMethods = { "NavigatesSettingActivityScreen_Subscription_Business" },
            groups = { "Smoke", "Regression" })
    public void NavigateToSubscriptionFromMenu_Business() {
        subFlow.NavigateToSubscriptionFromMenu();
    }

    /** #5 - Tap Verification card. */
    @Test(priority = 5,
            dependsOnMethods = { "NavigateToSubscriptionFromMenu_Business" },
            groups = { "Smoke", "Regression" })
    public void ClickVerificationCard_Business() {
        subFlow.ClickVerificationCard();
    }

    /** #6 - Tap View Plans (Verification details screen opens). */
    @Test(priority = 6,
            dependsOnMethods = { "ClickVerificationCard_Business" },
            groups = { "Smoke", "Regression" })
    public void ClickViewPlans_Verification_Business() {
        subFlow.ClickViewPlans();
    }

    /** #7 - Assert "Boost Your Business Account" header. */
    @Test(priority = 7,
            dependsOnMethods = { "ClickViewPlans_Verification_Business" },
            groups = { "Smoke", "Regression" })
    public void AssertBoostBusinessHeader_Business() {
        subFlow.AssertBoostBusinessHeader();
    }

    /** #8 - Close Verification details screen. */
    @Test(priority = 8,
            dependsOnMethods = { "AssertBoostBusinessHeader_Business" },
            groups = { "Smoke", "Regression" })
    public void CloseVerificationCard_Business() {
        subFlow.CloseCardScreen();
    }

    /** #9 - Tap Magic image credits card. */
    @Test(priority = 9,
            dependsOnMethods = { "CloseVerificationCard_Business" },
            groups = { "Smoke", "Regression" })
    public void ClickMagicImageCreditsCard_Business() {
        subFlow.ClickMagicImageCreditsCard();
    }

    /** #10 - Tap View Plans (AI credits details screen opens). */
    @Test(priority = 10,
            dependsOnMethods = { "ClickMagicImageCreditsCard_Business" },
            groups = { "Smoke", "Regression" })
    public void ClickViewPlans_AiCredits_Business() {
        subFlow.ClickViewPlans();
    }

    /** #11 - Assert "Unlock more magic credits" header. */
    @Test(priority = 11,
            dependsOnMethods = { "ClickViewPlans_AiCredits_Business" },
            groups = { "Smoke", "Regression" })
    public void AssertUnlockAiCreditsHeader_Business() {
        subFlow.AssertUnlockAiCreditsHeader();
    }

    /** #12 - Close AI credits details screen. */
    @Test(priority = 12,
            dependsOnMethods = { "AssertUnlockAiCreditsHeader_Business" },
            groups = { "Smoke", "Regression" })
    public void CloseAiCreditsCard_Business() {
        subFlow.CloseCardScreen();
    }

    /** #13 - Tap back arrow on Subscription overview screen. */
    @Test(priority = 13,
            dependsOnMethods = { "CloseAiCreditsCard_Business" },
            groups = { "Smoke", "Regression" })
    public void ClickBackArrow_Subscription_Business() {
        subFlow.ClickBackArrow();
    }

    /** #14 - Verify we're back on Settings and activity. */
    @Test(priority = 14,
            dependsOnMethods = { "ClickBackArrow_Subscription_Business" },
            groups = { "Smoke", "Regression" })
    public void AssertOnSettingsScreen_Subscription_Business() {
        subFlow.AssertOnSettingsScreen();
    }

    @DataProvider(name = "getBusinessUserLogin")
    public Object[][] getBusinessUserLogin() throws IOException {
        String jsonPath = Paths.get(
                System.getProperty("user.dir"),
                "src", "test", "java", "org", "rahulshettyacademy",
                "testData", "SmokeLoginData.json").toString();
        List<HashMap<String, String>> data = getJsonData(jsonPath);
        return new Object[][] { { data.get(0) } };
    }
}