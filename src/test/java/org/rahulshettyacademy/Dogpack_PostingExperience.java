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
 * Photo/Gallery post regression suite.
 *
 * Test order (TestNG priority):
 *   1. PrerequestFunctionsforProfile - login + clear any onboarding dialog
 *   2. NavigatesToAddPostScreen      - open the post creation entry screen
 *   3. CompletesGalleryPostFlow      - photo post end-to-end
 *
 * Each test triggers recovery (recoverToFeed) so a partial failure in
 * one test does NOT cascade-fail the next. Logs use the same greppable
 * markers as Settings:
 *
 *   grep -E "===>|===<|FAIL|RECOVERY|ASSERT" run.log
 */
public class Dogpack_PostingExperience extends AndroidBaseTest {

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

    /** #2 - Tap the bottom + tab to open the post-type chooser. */
    @Test(priority = 2, groups = { "Smoke", "Regression" })
    public void NavigatesToAddPostScreen() throws InterruptedException {
        try {
            posting.NavigatesToAddPostScreen();
        } finally {
            // Don't recoverToFeed here - we want to STAY on the post
            // chooser so test 3 can pick up from this screen.
        }
    }

    /**
     * #3 - End-to-end photo post:
     *   gallery pick -> tag park -> add location -> submit
     *
     * NOTE: relies on test #2 having opened the post chooser. If the
     * suite is run in isolation, NavigatesToAddPostScreen must run first.
     */
    @Test(priority = 3, groups = { "Smoke", "Regression" })
    public void CompletesGalleryPostFlow() throws InterruptedException {
        try {
            posting.doGalleryPost();
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