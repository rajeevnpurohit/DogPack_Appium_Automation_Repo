package org.rahulshettyacademy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.PostingExperiencePage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Text-only post regression suite.
 *
 * Test order (TestNG priority):
 *   1. PrerequestFunctionsforProfile - login + clear any onboarding dialog
 *   2. CompletesTextPostFlow         - text post end-to-end
 *
 * The text post flow is self-contained in one test (vs. the photo flow's
 * 2-step) because the text composer is harder to leave half-finished
 * gracefully - we want it to run start-to-finish or fail visibly.
 *
 * Logs use the same greppable markers as Settings/photo Posting:
 *   grep -E "===>|===<|FAIL|RECOVERY|ASSERT" run.log
 */
public class Dogpack_PostingExperienceTextPost extends AndroidBaseTest {

    LoginPage login;
    PostingExperiencePage posting;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        posting = new PostingExperiencePage(driver);
    }

    /** #1 - Login + dismiss any first-run dialog so the feed is interactive. */
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
     * #2 - End-to-end text post:
     *   open chooser -> Text tile -> compose -> tag park -> add location
     *   -> submit
     */
    @Test(priority = 2, groups = { "Smoke", "Regression" })
    public void CompletesTextPostFlow() throws InterruptedException {
        try {
            posting.NavigatesToAddPostScreen();
            posting.doTextPost();
            posting.tagParkInPost();
            posting.AddLocation();
            posting.clickSecondPostButton();
        } finally {
            posting.recoverToFeed();
        }
    }

    @DataProvider
    public Object[][] getDataSuccessfullLogin() throws IOException {
        List<HashMap<String, String>> data = getJsonData(
                System.getProperty("user.dir")
                        + "//src//test//java//org//rahulshettyacademy"
                        + "//testData//LoginData.json");
        return new Object[][] { { data.get(0) } };
    }
}