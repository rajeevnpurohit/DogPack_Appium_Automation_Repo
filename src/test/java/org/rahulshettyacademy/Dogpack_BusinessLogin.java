package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.ProfileSwitcherPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_BusinessLogin - TRANSITIONAL test class.
 *
 * PURPOSE:
 *   Run AFTER the dog-profile feature tests (Boost + AI Photo Generation).
 *   Performs ONLY:
 *     #1  Log in AGAIN as the same shared account (LoginData.json index
 *         [0] - tip@yopmail.com). After login the account defaults to the
 *         dog entity ('Tipper9'), exactly like the Phase 1 / Phase 2 tests.
 *     #2  Switch the active entity to the BUSINESS profile ('Tipper9Business')
 *         using the in-app profile switcher (Profile tab -> username
 *         dropdown -> Business Profile section -> first business entry).
 *
 *   Does NOT execute any feature tests (Boost / AI / Subscription)
 *   against the business profile yet - that's a future iteration once
 *   business-side XPaths are captured via Appium Inspector.
 *
 * ABOUT THE ACCOUNT MODEL:
 *   tip@yopmail.com is ONE login that contains TWO entities:
 *     - Dog profile     = 'Tipper9'         (default active entity on login)
 *     - Business profile = 'Tipper9Business' (selectable via in-app switcher)
 *   So there is no "separate business login" - the same email/password is
 *   used for both phases, and the business view is reached by switching
 *   entities inside the app.
 *
 * SESSION LIFECYCLE (important):
 *   The framework's AndroidBaseTest creates the driver in @BeforeClass and
 *   tears it down in @AfterClass. So each TestNG class gets its OWN Appium
 *   session. With noReset=false in data.properties, every new session
 *   starts with cleared app data - i.e., on the unauthenticated splash /
 *   login screen.
 *
 *   This is what gives us the "logout" semantic in the user-described flow:
 *     Phase 2 ends -> session torn down -> app data cleared
 *     Phase 3 starts -> new session -> app on splash (effectively logged out)
 *     Phase 3 logs back in as tip@yopmail.com (same credentials)
 *     Phase 3 switches entity to business
 *   No explicit UI logout step is needed.
 *
 * EXECUTION ORDER:
 *   This class runs AFTER Dogpack_BoostAccount and Dogpack_AiPhotoGeneration
 *   because the suite XML lists it third under <test preserve-order="true">.
 *
 * WHEN THIS FILE WILL BE REMOVED:
 *   Once we wire up TestNG @Factory to run the same Boost/AI tests under
 *   both entity types (dog default and business via switcher), this class
 *   becomes redundant and can be deleted - the entity switch will happen
 *   as a parameterized setup step inside the feature test classes
 *   themselves.
 */
public class Dogpack_BusinessLogin extends AndroidBaseTest {

    LoginPage login;
    ProfilePage profile;
    ProfileSwitcherPage profileSwitcher;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        profile = new ProfilePage(driver);
        profileSwitcher = new ProfileSwitcherPage(driver);
    }

    /**
     * #1 - Log in as the BUSINESS user.
     *
     * Reuses the exact same login flow that the dog tests use - the steps
     * are identical for both account types, only the credentials differ.
     * Credentials come from LoginData.json index [5] via the data provider
     * below.
     *
     * The session starts unauthenticated (noReset=false ensures the
     * @BeforeClass session has fresh, cleared app state). So we go
     * straight from the splash screen into login without any logout step.
     *
     * Post-condition: app is signed in as the business user and the
     * Profile screen has loaded (verified by ProfilePage.navigateToProfileScreen,
     * which checks for the edit-profile button as its success anchor).
     */
    @Test(priority = 1,
            dataProvider = "getBusinessUserLogin",
            groups = { "Smoke", "Regression" })
    public void LoginAsBusinessUser(HashMap<String, String> input)
            throws InterruptedException {
        System.out.println("[INFO]   Re-logging in as shared account: "
                + input.get("email") + " (will default to dog entity, "
                + "then switch to business in the next test)");
        login.scrollToLogin();
        login.NavigateToLogin();
        login.setEmailPassword(input.get("email"), input.get("password"));
        login.clickOnLoginSubmit();
        login.CompleteLoginProccess();
        login.HandleCustomDialog(0, 0);
        profile.navigateToProfileScreen();
        System.out.println("[PASS]   Logged in successfully. Profile screen "
                + "loaded with the default dog entity active.");
    }

    /**
     * #2 - Switch the active entity to the first BUSINESS profile attached
     *      to this account.
     *
     * Flow (delegated to ProfileSwitcherPage):
     *   - Tap Profile tab (ensure we are on profile-view)
     *   - Tap username dropdown arrow
     *   - Tap "Business Profile" section header
     *   - Tap the first business profile entry
     *   - Verify business_edPro is now visible (proof the active entity
     *     is a business profile)
     *
     * Pre-condition: LoginAsBusinessUser succeeded and the Profile screen
     *      is currently displayed (the default entity is usually a dog
     *      profile, even if the account also has business profiles).
     *
     * Post-condition: The active entity is now the first business profile.
     *      The Profile screen has reloaded to show the business-flavoured
     *      view (business_edPro visible instead of dog edit-profile).
     */
    @Test(priority = 2,
            dependsOnMethods = { "LoginAsBusinessUser" },
            groups = { "Smoke", "Regression" })
    public void SwitchToFirstBusinessProfile() {
        profileSwitcher.SwitchToFirstBusinessProfile();
        System.out.println("[PASS]   Switched to first business profile. "
                + "Smoke suite ends here - no business-side feature tests "
                + "run in this iteration.");
    }

    /**
     * Returns the shared tip@yopmail.com credentials from LoginData.json
     * index [0]. This is the SAME entry used by Dogpack_BoostAccount and
     * Dogpack_AiPhotoGeneration - because the same login serves both the
     * dog entity (Phases 1/2) and the business entity (Phase 3, after the
     * in-app switch in test #2 of this class).
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
