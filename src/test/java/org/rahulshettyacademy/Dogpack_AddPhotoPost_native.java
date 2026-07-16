package org.rahulshettyacademy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.AddPhotoPostPage;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * NEW native Photo capture module - camera-controls smoke TC.
 *
 * Targets the native Kotlin media-editor camera (com.dogpack.mediaediting /
 * CameraFragment), NOT yet released to the client. Fully separate from:
 *   - the old React Native Add Post TCs (Dogpack_PostingExperience,
 *     Dogpack_PostingExperienceTextPost) which run the obsolete composer, and
 *   - the native text-post TC (Dogpack_AddTextPost_native).
 * No code is shared with any of them; AddPhotoPostPage is self-contained.
 *
 * This first slice stops before capture - it exercises the camera controls:
 *   + -> No Effect -> assert credits view -> tap timer -> assert timer = 3s
 *
 * Greppable log markers: grep -E "===>|===<|FAIL|WARN|ASSERT" run.log
 */
public class Dogpack_AddPhotoPost_native extends AndroidBaseTest {

    LoginPage login;
    AddPhotoPostPage photoPost;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        photoPost = new AddPhotoPostPage(driver);
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

    /** #2 - Native Photo camera-controls smoke (stops before capture). */
    @Test(priority = 2, groups = { "Smoke", "Regression" })
    public void CameraTopControls() {
        photoPost.runCameraTopControls();
    }

    /** #3 - Native Photo side toolbar: Beauty + full filter panel sweep. */
    @Test(priority = 3, groups = { "Smoke", "Regression" })
    public void CameraSideControls_BeautyAndFilters() {
        photoPost.runCameraSideControls_BeautyAndFilters();
    }

    /** #4 - Native Photo side toolbar: Background (BG) filter flow. */
    @Test(priority = 4, groups = { "Smoke", "Regression" })
    public void CameraSideControls_BackgroundFilter() {
        photoPost.runCameraSideControls_BackgroundFilter();
    }

    /** #5 - Native Photo side toolbar: Layout (collage) + FX tools. */
    @Test(priority = 5, groups = { "Smoke", "Regression" })
    public void CameraSideControls_Layout_FX() {
        photoPost.runCameraSideControls_Layout_FX();
    }

    /**
     * #6 - Native Photo full posting flow (CONTINUATION of Layout_FX). Runs only
     * after CameraSideControls_Layout_FX in priority order, which leaves the FX
     * panel open; this starts at "Tap Fire" and posts to the tip@yopmail.com
     * feed. Not independently runnable.
     */
    @Test(priority = 6, groups = { "Smoke", "Regression" })
    public void CameraSideControls_Posting() {
        photoPost.runCameraSideControls_Posting();
    }

    @DataProvider
    public Object[][] getDataSuccessfullLogin() throws IOException {
        // Log in as the shared account tip@yopmail.com, which lives at
        // SmokeLoginData.json index 0 (same source the other tip@yopmail.com
        // TCs use - e.g. Dogpack_VideoFeed, Dogpack_Notifications). Deliberately
        // NOT LoginData.json, whose index 0 is a different account (iamkiara02)
        // shared by ~29 other TCs - changing that file would switch every one
        // of them.
        List<HashMap<String, String>> data = getJsonData(
                System.getProperty("user.dir")
                        + "//src//test//java//org//rahulshettyacademy"
                        + "//testData//SmokeLoginData.json");
        return new Object[][] { { data.get(0) } };
    }
}