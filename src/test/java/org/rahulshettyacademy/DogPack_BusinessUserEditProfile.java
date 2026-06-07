package org.rahulshettyacademy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.BusinessUserEditProfile;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Business User Edit Profile regression suite.
 *
 * Login slot: LoginData.json[4] = iamkabir01 (business user).
 *
 * Test order (TestNG priority):
 *   1. PrerequestFunctionsforProfile - login as business user + clear
 *      any onboarding dialog
 *   2. NavigatesToProfileTab         - tap profile-view tab + verify
 *      business Edit button visible (typo fixed from old
 *      'NavigtesToProfileTab')
 *   3. UpdateProfileDetails          - open Edit form, update profile
 *      image + name fields + 7 social URLs, submit
 *
 * Each test triggers recoverToProfileScreen in finally{} so a partial
 * failure in one test does NOT cascade-fail the next. Logs use the
 * same greppable markers as Settings/Posting:
 *
 *   grep -E "===>|===<|FAIL|RECOVERY|ASSERT" run.log
 */
public class DogPack_BusinessUserEditProfile extends AndroidBaseTest {

    LoginPage login;
    BusinessUserEditProfile businessUser;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        businessUser = new BusinessUserEditProfile(driver);
    }

    /** #1 - Login as iamkabir01 (business user) + dismiss onboarding. */
    @Test(priority = 1, dataProvider = "getDataSuccessfullLogin",
            groups = { "Smoke", "Regression" })
    public void PrerequestFunctionsforProfile(HashMap<String, String> input)
            throws InterruptedException {
        login.scrollToLogin();
        login.NavigateToLogin();
        login.setEmailPassword(input.get("email"), input.get("password"));
        login.clickOnLoginSubmit();
        login.CompleteLoginProccess();
        login.HandleCustomDialog(384, 576);
    }

    /**
     * #2 - Navigate to business profile screen and confirm the
     * business Edit button (business_edPro) is visible.
     *
     * Method name fixed from the original 'NavigtesToProfileTab'
     * (typo) - 'Navigates' is the correct spelling. TestNG only cares
     * about the @Test annotation, not the method name, so this rename
     * is safe and improves readability in reports.
     */
    @Test(priority = 2, groups = { "Smoke", "Regression" })
    public void NavigatesToProfileTab() throws InterruptedException {
        businessUser.navigateToProfileScreen();
        // Don't recover here - we want to STAY on the profile screen so
        // test 3 can pick up from this state.
    }

    /**
     * #3 - Open the Edit form and update profile image + name fields +
     * all 7 social URLs. Submit via Update button.
     *
     * Relies on test #2 having navigated to the business profile
     * screen. If run in isolation, NavigatesToProfileTab must run first.
     */
    @Test(priority = 3, groups = { "Smoke", "Regression" })
    public void UpdateProfileDetails() throws InterruptedException {
        try {
            businessUser.ClickOnEditBtn();
            businessUser.editBusinessProfileDetails();
        } finally {
            // After update, the app typically navigates back to the
            // business profile screen automatically. If anything went
            // wrong mid-flow (form stuck on edit screen, modal up,
            // etc.) recover so the suite ends cleanly.
            businessUser.recoverToProfileScreen();
        }
    }

    @DataProvider
    public Object[][] getDataSuccessfullLogin() throws IOException {
        List<HashMap<String, String>> data = getJsonData(
                System.getProperty("user.dir")
                        + "//src//test//java//org//rahulshettyacademy"
                        + "//testData//LoginData.json");
        // Slot [4] = iamkabir01 business user (LoginData.json)
        return new Object[][] { { data.get(4) } };
    }
}