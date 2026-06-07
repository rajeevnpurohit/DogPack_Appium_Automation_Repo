package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.BoostAccountPage;
import org.rahulshettyacademy.pageObjects.android.BoostAccountPage.AccountType;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.ProfileSwitcherPage;
import org.rahulshettyacademy.pageObjects.android.SettingsAndActivityPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_BoostAccount_Business - Boost Your Account flow for the
 * BUSINESS entity (Tipper9Business).
 *
 * PURPOSE:
 *   Run AFTER the dog-profile feature tests AND after the entity switch
 *   to Tipper9Business. Exercises the same Boost flow but against the
 *   business entity:
 *     #1  Log in as shared account (tip@yopmail.com)
 *     #2  Switch entity to Tipper9Business (delegates to ProfileSwitcherPage)
 *     #3  Open Settings via hamburger
 *     #4  Tap "Boost your account"
 *     #5  Tap "Subscribe Monthly Plan for RUPEE520.00" (business price)
 *     #6  Tap close (X)
 *
 * The 520 vs 6 price difference is handled inside BoostAccountPage via
 * its AccountType parameter - this class instantiates the page with
 * AccountType.BUSINESS in setUpp().
 *
 * SESSION LIFECYCLE:
 *   Each TestNG class gets its own Appium session via @BeforeClass /
 *   @AfterClass in AndroidBaseTest. With noReset=false, this class
 *   starts on a clean splash screen, then logs in fresh - the entity
 *   switch is part of THIS class's flow, not inherited from any other.
 *
 *   The "depends on entity switch" constraint is met by chaining tests
 *   within this class via dependsOnMethods, not across classes.
 *
 * EXECUTION ORDER (in suite XML):
 *   1. Dogpack_BoostAccount             (dog Boost)
 *   2. Dogpack_AiPhotoGeneration        (dog AI)
 *   3. Dogpack_BusinessLogin            (transitional - just verifies
 *                                        entity switch works in isolation)
 *   4. Dogpack_BoostAccount_Business    (THIS CLASS - business Boost)
 */
public class Dogpack_BoostAccount_Business extends AndroidBaseTest {

    LoginPage login;
    ProfilePage profile;
    ProfileSwitcherPage profileSwitcher;
    SettingsAndActivityPage settings;
    BoostAccountPage boostBiz;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        profile = new ProfilePage(driver);
        profileSwitcher = new ProfileSwitcherPage(driver);
        settings = new SettingsAndActivityPage(driver);
        // Account-type aware page object - the Subscribe button XPath
        // inside this instance will resolve to the business price
        // (Rs.520.00) instead of the dog price (Rs.6.00).
        boostBiz = new BoostAccountPage(driver, AccountType.BUSINESS);
    }

    /** #1 - Login + navigate to Profile screen (defaults to dog entity). */
    @Test(priority = 1, dataProvider = "getBusinessUserLogin",
            groups = { "Smoke", "Regression" })
    public void LoginForBusinessBoost(HashMap<String, String> input)
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

    /**
     * #2 - Switch active entity from dog to Tipper9Business via the
     *      in-app profile switcher. Includes title-text assertion
     *      (asserts "Tipper9Business" exact match).
     */
    @Test(priority = 2,
            dependsOnMethods = { "LoginForBusinessBoost" },
            groups = { "Smoke", "Regression" })
    public void SwitchToFirstBusinessProfile() {
        profileSwitcher.SwitchToFirstBusinessProfile();
    }

    /** #3 - Open Settings and activity via hamburger. */
    @Test(priority = 3,
            dependsOnMethods = { "SwitchToFirstBusinessProfile" },
            groups = { "Smoke", "Regression" })
    public void NavigatesSettingActivityScreen_Business() throws InterruptedException {
        settings.NavigatesToSettingAndActivityScreen();
    }

    /** #4 - Tap "Boost your account" -> wait for Subscribe button. */
    @Test(priority = 4,
            dependsOnMethods = { "NavigatesSettingActivityScreen_Business" },
            groups = { "Smoke", "Regression" })
    public void NavigateToBoostAccountFromMenu_Business() {
        boostBiz.NavigateToBoostAccountFromMenu();
    }

    /**
     * #5 - Tap "Subscribe Monthly Plan for Rs.520.00" -> dismiss Got it.
     *      The 520 price resolves correctly because boostBiz was
     *      constructed with AccountType.BUSINESS.
     */
    @Test(priority = 5,
            dependsOnMethods = { "NavigateToBoostAccountFromMenu_Business" },
            groups = { "Smoke", "Regression" })
    public void ClickSubscribeMonthlyPlan_Business() {
        boostBiz.ClickSubscribeMonthlyPlan();
    }

    /** #6 - Tap close (X) and verify return to Settings. */
    @Test(priority = 6,
            dependsOnMethods = { "ClickSubscribeMonthlyPlan_Business" },
            groups = { "Smoke", "Regression" })
    public void CloseBoostScreen_Business() {
        boostBiz.CloseBoostScreen();
    }

    // ================================================================
    // ==========    TEST CASE 2: Business Annual Plan flow      ======
    // ================================================================
    //
    // FLOW (runs AFTER the Monthly close lands us back on Settings):
    //   #7  Re-open the Boost screen from Settings (same XPath as before)
    //   #8  Tap 'Annual Plan' card -> wait for Subscribe Annual button
    //   #9  Tap 'Subscribe Annual Plan for Rs.5,300.00' -> dismiss Got it
    //         (the page object's ClickSubscribeAnnualPlan() method
    //          internally taps Subscribe then waits for and taps Got it)
    //   #10 Tap close (X) and verify return to Settings
    //         (CloseBoostScreen() internally does a 3s defensive Got it
    //          dismissal before tapping Close - covers the user-requested
    //          'sleep 3s then close' sequence)
    //
    // Chain anchor: dependsOnMethods = { "CloseBoostScreen_Business" }
    // ================================================================

    /** #7 - Re-open Boost screen from Settings (entry to Annual flow). */
    @Test(priority = 7,
            dependsOnMethods = { "CloseBoostScreen_Business" },
            groups = { "Smoke", "Regression" })
    public void NavigateToBoostAccountFromMenu_Business_Annual() {
        boostBiz.NavigateToBoostAccountFromMenu();
    }

    /** #8 - Tap the 'Annual Plan' card. */
    @Test(priority = 8,
            dependsOnMethods = { "NavigateToBoostAccountFromMenu_Business_Annual" },
            groups = { "Smoke", "Regression" })
    public void SelectAnnualPlanCard_Business() {
        boostBiz.SelectAnnualPlanCard();
    }

    /**
     * #9 - Tap 'Subscribe Annual Plan for Rs.5,300.00' -> dismiss Got it.
     *      Price resolves to 5,300.00 (not 15.00) because boostBiz was
     *      constructed with AccountType.BUSINESS.
     */
    @Test(priority = 9,
            dependsOnMethods = { "SelectAnnualPlanCard_Business" },
            groups = { "Smoke", "Regression" })
    public void ClickSubscribeAnnualPlan_Business() {
        boostBiz.ClickSubscribeAnnualPlan();
    }

    /**
     * #10 - Tap close (X) and verify return to Settings.
     *       CloseBoostScreen() includes a 3s defensive Got it dismissal
     *       at the start - which covers the user-requested 'Click Got it
     *       + sleep 3s + click close' three-step sequence in one call.
     */
    @Test(priority = 10,
            dependsOnMethods = { "ClickSubscribeAnnualPlan_Business" },
            groups = { "Smoke", "Regression" })
    public void CloseBoostScreen_Business_Annual() {
        boostBiz.CloseBoostScreen();
    }

    /**
     * Returns the shared tip@yopmail.com credentials from
     * LoginData.json index [0] - same login as the dog tests use, but
     * after step #2 the active entity will be Tipper9Business.
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
