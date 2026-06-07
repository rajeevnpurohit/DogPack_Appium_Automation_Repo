package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.BoostAccountPage;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.SettingsAndActivityPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_BoostAccount - MINIMAL test suite for the "Boost Your Account" flow.
 *
 * FLOW:
 *   1. Login + navigate to Profile
 *   2. Open Settings & activity
 *   3. Tap "Boost your account"
 *   4. Tap "Subscribe Monthly Plan for ₹6.00" -> tap "Got it" on the error dialog
 *   5. Tap close (X) and verify return to Settings
 *
 * Tests are chained via dependsOnMethods — when an upstream test
 * fails, downstream tests skip cleanly (no cascade noise).
 */
public class Dogpack_BoostAccount extends AndroidBaseTest {

    LoginPage login;
    ProfilePage profile;
    SettingsAndActivityPage settings;
    BoostAccountPage boost;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        profile = new ProfilePage(driver);
        settings = new SettingsAndActivityPage(driver);
        boost = new BoostAccountPage(driver);
    }

    /** #1 - Login + navigate to Profile screen. */
    @Test(priority = 1, dataProvider = "getDataSuccessfullLogin",
            groups = { "Smoke", "Regression" })
    public void PrerequestFunctionsforBoost(HashMap<String, String> input)
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
    @Test(priority = 2,
            dependsOnMethods = { "PrerequestFunctionsforBoost" },
            groups = { "Smoke", "Regression" })
    public void NavigatesSettingActivityScreen() throws InterruptedException {
        settings.NavigatesToSettingAndActivityScreen();
    }

    /** #3 - Tap "Boost Your Account" row. */
    @Test(priority = 3,
            dependsOnMethods = { "NavigatesSettingActivityScreen" },
            groups = { "Smoke", "Regression" })
    public void NavigateToBoostAccountFromMenu() {
        boost.NavigateToBoostAccountFromMenu();
    }

    /** #4 - Tap Subscribe Monthly Plan -> dismiss "Got it" dialog. */
    @Test(priority = 4,
            dependsOnMethods = { "NavigateToBoostAccountFromMenu" },
            groups = { "Smoke", "Regression" })
    public void ClickSubscribeMonthlyPlan() {
        boost.ClickSubscribeMonthlyPlan();
    }

    /** #5 - Tap close (X) and verify return to Settings. */
    @Test(priority = 5,
            dependsOnMethods = { "ClickSubscribeMonthlyPlan" },
            groups = { "Smoke", "Regression" })
    public void CloseBoostScreen() {
        boost.CloseBoostScreen();
    }

    // ================================================================
    // ==========    TEST CASE 2: Annual Plan flow              =======
    // ================================================================
    //
    // FLOW:
    //   #6 Re-open Boost screen (we're back at Settings after test #5)
    //   #7 Tap 'Annual Plan' card to switch selection from default Monthly
    //   #8 Tap 'Subscribe Annual Plan for ₹15.00' -> dismiss 'Got it'
    //   #9 Tap close (X) and verify return to Settings
    //
    // Chain anchor: dependsOnMethods = { "CloseBoostScreen" } so test
    // case 2 only runs if test case 1 finished cleanly (we're back at
    // Settings, not stuck on the Boost screen).
    // ================================================================

    /** #6 - Re-open Boost screen from Settings (entry for annual flow). */
    @Test(priority = 6,
            dependsOnMethods = { "CloseBoostScreen" },
            groups = { "Smoke", "Regression" })
    public void NavigateToBoostAccountFromMenu_Annual() {
        boost.NavigateToBoostAccountFromMenu();
    }

    /** #7 - Select the Annual Plan card. */
    @Test(priority = 7,
            dependsOnMethods = { "NavigateToBoostAccountFromMenu_Annual" },
            groups = { "Smoke", "Regression" })
    public void SelectAnnualPlanCard() {
        boost.SelectAnnualPlanCard();
    }

    /** #8 - Tap Subscribe Annual Plan -> dismiss 'Got it' dialog. */
    @Test(priority = 8,
            dependsOnMethods = { "SelectAnnualPlanCard" },
            groups = { "Smoke", "Regression" })
    public void ClickSubscribeAnnualPlan() {
        boost.ClickSubscribeAnnualPlan();
    }

    /** #9 - Tap close (X) and verify return to Settings (annual close). */
    @Test(priority = 9,
            dependsOnMethods = { "ClickSubscribeAnnualPlan" },
            groups = { "Smoke", "Regression" })
    public void CloseBoostScreen_Annual() {
        boost.CloseBoostScreen();
    }

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
