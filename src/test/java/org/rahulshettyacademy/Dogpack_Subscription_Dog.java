package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.SettingsAndActivityPage;
import org.rahulshettyacademy.pageObjects.android.SubscriptionFlowPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_Subscription_Dog - Subscription feature flow for the DOG
 * entity (Tipper9 - default entity on login).
 *
 * Named with the _Dog suffix to parallel Dogpack_Subscription_Business
 * AND to avoid colliding with the pre-existing legacy
 * Dogpack_Subscription class (178 lines) that goes with the legacy
 * SubscriptionPage page object.
 *
 * FLOW (13 total tests = 2 setup + 11 feature steps):
 *
 *   SETUP CHAIN:
 *     #1  Login as shared account (tip@yopmail.com) -> defaults to dog
 *     #2  Open Settings and activity via hamburger
 *
 *   FEATURE CHAIN:
 *     #3  Tap Subscription row (waits for "View Plans" anchor)
 *     #4  Tap Verification card
 *     #5  Tap View Plans
 *     #6  Assert "Boost Your Account" header (dog variant)
 *     #7  Tap close (X)
 *     #8  Tap Magic image credits card
 *     #9  Tap View Plans
 *     #10 Assert "Unlock more magic credits" header
 *     #11 Tap close (X)
 *     #12 Tap back arrow (first ImageView on screen)
 *     #13 Assert "Settings and activity" header (verifies back nav)
 *
 * No entity switch in this class - the default entity on login is the
 * dog profile (Tipper9). The page object is constructed with the
 * 1-arg constructor which defaults to AccountType.DOG, so the
 * Verification header assertion expects "Boost Your Account"
 * (not "Boost Your Business Account").
 *
 * METHOD NAMING:
 *   Test method names use a _Dog suffix to avoid collision with the
 *   business class's _Business suffix and any legacy method names.
 *
 * EXECUTION ORDER (in suite XML):
 *   1. Dogpack_BoostAccount
 *   2. Dogpack_AiPhotoGeneration
 *   3. Dogpack_BusinessLogin
 *   4. Dogpack_BoostAccount_Business
 *   5. Dogpack_AiPhotoGeneration_Business
 *   6. Dogpack_Subscription_Business
 *   7. Dogpack_Subscription_Dog            (THIS CLASS - dog Subscription)
 */
public class Dogpack_Subscription_Dog extends AndroidBaseTest {

    LoginPage login;
    ProfilePage profile;
    SettingsAndActivityPage settings;
    SubscriptionFlowPage subFlow;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        profile = new ProfilePage(driver);
        settings = new SettingsAndActivityPage(driver);
        // 1-arg constructor -> defaults to AccountType.DOG, so the
        // Verification assertion expects "Boost Your Account"
        // (not "Boost Your Business Account").
        subFlow = new SubscriptionFlowPage(driver);
    }

    // ================================================================
    // ==========    SETUP CHAIN (login + settings)             =======
    // ================================================================

    @Test(priority = 1, dataProvider = "getDogUserLogin",
            groups = { "Smoke", "Regression" })
    public void LoginForDogSubscription(HashMap<String, String> input)
            throws InterruptedException {
        System.out.println("[INFO]   Logging in as shared account (defaults "
                + "to dog entity): " + input.get("email"));
        login.scrollToLogin();
        login.NavigateToLogin();
        login.setEmailPassword(input.get("email"), input.get("password"));
        login.clickOnLoginSubmit();
        login.CompleteLoginProccess();
        login.HandleCustomDialog(0, 0);
        profile.navigateToProfileScreen();
    }

    @Test(priority = 2, dependsOnMethods = { "LoginForDogSubscription" },
            groups = { "Smoke", "Regression" })
    public void NavigatesSettingActivityScreen_Subscription_Dog()
            throws InterruptedException {
        settings.NavigatesToSettingAndActivityScreen();
    }

    // ================================================================
    // ==========    FEATURE CHAIN (Subscription flow)          =======
    // ================================================================

    /** #3 - Tap Subscription row (waits for View Plans anchor). */
    @Test(priority = 3,
            dependsOnMethods = { "NavigatesSettingActivityScreen_Subscription_Dog" },
            groups = { "Smoke", "Regression" })
    public void NavigateToSubscriptionFromMenu_Dog() {
        subFlow.NavigateToSubscriptionFromMenu();
    }

    /** #4 - Tap Verification card. */
    @Test(priority = 4,
            dependsOnMethods = { "NavigateToSubscriptionFromMenu_Dog" },
            groups = { "Smoke", "Regression" })
    public void ClickVerificationCard_Dog() {
        subFlow.ClickVerificationCard();
    }

    /** #5 - Tap View Plans under Verification. */
    @Test(priority = 5,
            dependsOnMethods = { "ClickVerificationCard_Dog" },
            groups = { "Smoke", "Regression" })
    public void ClickViewPlans_Verification_Dog() {
        subFlow.ClickViewPlans();
    }

    /**
     * #6 - Assert "Boost Your Account" header (dog variant).
     *      AssertVerificationHeader resolves the expected text from the
     *      AccountType enum: DOG -> "Boost Your Account".
     */
    @Test(priority = 6,
            dependsOnMethods = { "ClickViewPlans_Verification_Dog" },
            groups = { "Smoke", "Regression" })
    public void AssertBoostHeader_Dog() {
        subFlow.AssertVerificationHeader();
    }

    /** #7 - Close Verification details screen. */
    @Test(priority = 7,
            dependsOnMethods = { "AssertBoostHeader_Dog" },
            groups = { "Smoke", "Regression" })
    public void CloseVerificationCard_Dog() {
        subFlow.CloseCardScreen();
    }

    /** #8 - Tap Magic image credits card. */
    @Test(priority = 8,
            dependsOnMethods = { "CloseVerificationCard_Dog" },
            groups = { "Smoke", "Regression" })
    public void ClickMagicImageCreditsCard_Dog() {
        subFlow.ClickMagicImageCreditsCard();
    }

    /** #9 - Tap View Plans (AI credits details screen opens). */
    @Test(priority = 9,
            dependsOnMethods = { "ClickMagicImageCreditsCard_Dog" },
            groups = { "Smoke", "Regression" })
    public void ClickViewPlans_AiCredits_Dog() {
        subFlow.ClickViewPlans();
    }

    /** #10 - Assert "Unlock more magic credits" header. */
    @Test(priority = 10,
            dependsOnMethods = { "ClickViewPlans_AiCredits_Dog" },
            groups = { "Smoke", "Regression" })
    public void AssertUnlockAiCreditsHeader_Dog() {
        subFlow.AssertUnlockAiCreditsHeader();
    }

    /** #11 - Close AI credits details screen. */
    @Test(priority = 11,
            dependsOnMethods = { "AssertUnlockAiCreditsHeader_Dog" },
            groups = { "Smoke", "Regression" })
    public void CloseAiCreditsCard_Dog() {
        subFlow.CloseCardScreen();
    }

    /** #12 - Tap back arrow on Subscription overview screen. */
    @Test(priority = 12,
            dependsOnMethods = { "CloseAiCreditsCard_Dog" },
            groups = { "Smoke", "Regression" })
    public void ClickBackArrow_Subscription_Dog() {
        subFlow.ClickBackArrow();
    }

    /** #13 - Verify we're back on Settings and activity. */
    @Test(priority = 13,
            dependsOnMethods = { "ClickBackArrow_Subscription_Dog" },
            groups = { "Smoke", "Regression" })
    public void AssertOnSettingsScreen_Subscription_Dog() {
        subFlow.AssertOnSettingsScreen();
    }

    @DataProvider(name = "getDogUserLogin")
    public Object[][] getDogUserLogin() throws IOException {
        String jsonPath = Paths.get(
                System.getProperty("user.dir"),
                "src", "test", "java", "org", "rahulshettyacademy",
                "testData", "SmokeLoginData.json").toString();
        List<HashMap<String, String>> data = getJsonData(jsonPath);
        return new Object[][] { { data.get(0) } };
    }
}