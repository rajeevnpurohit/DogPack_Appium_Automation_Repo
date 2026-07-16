package org.rahulshettyacademy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.AddVideoPostPage;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * NEW native Video capture module TC.
 *
 * Targets the native Kotlin media-editor camera in Video mode
 * (com.dogpack.mediaediting / CameraFragment), NOT yet released to the client.
 * Fully separate from:
 *   - the old React Native Add Post TCs (Dogpack_PostingExperience,
 *     Dogpack_PostingExperienceTextPost), and
 *   - the native text/photo TCs (Dogpack_AddTextPost_native,
 *     Dogpack_AddPhotoPost_native).
 * No code is shared with any of them; AddVideoPostPage is self-contained.
 *
 * Logs in as tip@yopmail.com (SmokeLoginData.json index 0), matching the other
 * native TCs.
 *
 * Greppable log markers: grep -E "===>|===<|FAIL|WARN|ASSERT" run.log
 */
public class Dogpack_AddVideoPost_native extends AndroidBaseTest {

    LoginPage login;
    AddVideoPostPage videoPost;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        videoPost = new AddVideoPostPage(driver);
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

    /** #2 - Open camera, clear permission, switch to Video mode, No Effect. */
    @Test(priority = 2, groups = { "Smoke", "Regression" })
    public void SwitchToVideoMode() {
        videoPost.runSwitchToVideoMode();
    }

    /**
     * #3 - Video side toolbar (Beauty/Filter/Loop/Zoom/Rewind/FX).
     * CONTINUATION of SwitchToVideoMode (priority order) - assumes Video mode is
     * active with No Effect selected. Not independently runnable.
     */
    @Test(priority = 3, groups = { "Smoke", "Regression" })
    public void VideoSideControls() {
        videoPost.runVideoSideControls();
    }

    @DataProvider
    public Object[][] getDataSuccessfullLogin() throws IOException {
        // Log in as tip@yopmail.com (SmokeLoginData.json index 0), same source
        // the other native TCs use. NOT LoginData.json (whose index 0 is a
        // different account shared by many TCs).
        List<HashMap<String, String>> data = getJsonData(
                System.getProperty("user.dir")
                        + "//src//test//java//org//rahulshettyacademy"
                        + "//testData//SmokeLoginData.json");
        return new Object[][] { { data.get(0) } };
    }
}
