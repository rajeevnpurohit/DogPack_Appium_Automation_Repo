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
 * Dogpack_Marketplace_ProfileSwitch_Test - STANDALONE test for
 * validating the business-to-dog profile switch flow.
 *
 * Purpose:
 *   Development scaffold for the eventual business-marketplace
 *   cleanup integration. This class exists to test ONLY the four
 *   dog-switch steps in isolation, without dragging in the full
 *   marketplace + payment + India-revert cleanup chain.
 *
 *   Once these 4 steps are proven to work reliably, the same
 *   ProfileSwitcherPage methods will be invoked from within
 *   Dogpack_Marketplace_Business's @AfterMethod cleanup so the
 *   suite always ends with the user back on dog profile.
 *
 * NOT INCLUDED IN THE SMOKE SUITE XML by default. Run on its own
 * via:
 *   mvn test -Dtest=Dogpack_Marketplace_ProfileSwitch_Test
 *
 * Flow (6 priorities):
 *   #1  Login as shared account (tip@yopmail.com)
 *   #2  Switch to first business profile (so we have a state to
 *       revert from). Reuses the battle-tested
 *       SwitchToFirstBusinessProfile() method.
 *   #3-6  The four new dog-switch steps:
 *         3. Click Profile tab (TextView[2] locator)
 *         4. Click arrow_down_click chevron
 *         5. Click dogbus-action-dog tile
 *         6. Click dogbus-SwitchProfile commit
 *
 * No @AfterMethod cleanup here - this test is just for the switch
 * flow validation.
 */
public class Dogpack_Marketplace_ProfileSwitch_Test extends AndroidBaseTest {

    LoginPage login;
    ProfilePage profile;
    ProfileSwitcherPage profileSwitcher;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        profile = new ProfilePage(driver);
        profileSwitcher = new ProfileSwitcherPage(driver);
    }

    // ================================================================
    // ==========    SETUP - login + switch to business         =======
    // ================================================================

    /** #1 - Login + navigate to Profile screen. */
    @Test(priority = 1, dataProvider = "getBusinessUserLogin",
            groups = { "ProfileSwitch" })
    public void Login_ProfileSwitch(HashMap<String, String> input)
            throws InterruptedException {
        System.out.println("[INFO]   Logging in: " + input.get("email"));
        login.scrollToLogin();
        login.NavigateToLogin();
        login.setEmailPassword(input.get("email"), input.get("password"));
        login.clickOnLoginSubmit();
        login.CompleteLoginProccess();
        login.HandleCustomDialog(0, 0);
        profile.navigateToProfileScreen();
    }

    /**
     * #2 - Switch to first business profile via the existing
     * battle-tested SwitchToFirstBusinessProfile() method. After
     * this, the active entity is Tipper9Business - giving us a
     * non-dog state to revert from.
     */
    @Test(priority = 2,
            dependsOnMethods = { "Login_ProfileSwitch" },
            groups = { "ProfileSwitch" })
    public void SwitchToBusiness_ProfileSwitch() {
        profileSwitcher.SwitchToFirstBusinessProfile();
    }

    // ================================================================
    // ==========    NEW FLOW - business -> dog (4 steps)       =======
    // ================================================================

    /** #3 (new step 1) - Tap Profile tab via TextView[2] locator. */
    @Test(priority = 3,
            dependsOnMethods = { "SwitchToBusiness_ProfileSwitch" },
            groups = { "ProfileSwitch" })
    public void ClickProfileTab_ProfileSwitch() {
        profileSwitcher.ClickProfileTabByTextView();
    }

    /** #4 (new step 2) - Tap arrow_down_click chevron. */
    @Test(priority = 4,
            dependsOnMethods = { "ClickProfileTab_ProfileSwitch" },
            groups = { "ProfileSwitch" })
    public void ClickArrowDown_ProfileSwitch() {
        profileSwitcher.ClickArrowDownForProfileSwitching();
    }

    /** #5 (new step 3) - Tap dog profile tile (dogbus-action-dog). */
    @Test(priority = 5,
            dependsOnMethods = { "ClickArrowDown_ProfileSwitch" },
            groups = { "ProfileSwitch" })
    public void ClickDogTile_ProfileSwitch() {
        profileSwitcher.ClickDogProfileSwitcher();
    }

    /** #6 (new step 4) - Tap SwitchProfile commit button. */
    @Test(priority = 6,
            dependsOnMethods = { "ClickDogTile_ProfileSwitch" },
            groups = { "ProfileSwitch" })
    public void ClickSelectProfile_ProfileSwitch() {
        profileSwitcher.ClickSelectProfileForDog();
    }

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
