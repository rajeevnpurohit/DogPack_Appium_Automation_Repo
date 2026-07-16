package org.rahulshettyacademy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.AddPostPage;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * NEW native Add Post module regression - image + caption text post.
 *
 * Targets the native Kotlin media-editor rewrite of Add Post
 * (com.dogpack.mediaediting), NOT yet released to the client as of this
 * writing. Separate from the legacy {@code Dogpack_PostingExperience*}
 * classes, which remain parked for the old React Native composer.
 *
 * Test order (TestNG priority):
 *   1. PrerequestFunctionsforProfile - login + dismiss onboarding dialog
 *   2. CompletesImagePostWithCaption - full add-post flow, image + caption,
 *      ends with an assertion on the "Post uploaded successfully" toast
 *
 * Logs use the same greppable markers as the rest of the suite:
 *   grep -E "===>|===<|FAIL|WARN|ASSERT" run.log
 */
public class Dogpack_AddTextPost_native extends AndroidBaseTest {

    LoginPage login;
    AddPostPage addPost;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        addPost = new AddPostPage(driver);
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
     * #2 - End-to-end native Add Post flow:
     *   + -> Text mode -> attach image -> Done -> Create post -> Post media
     *   -> caption -> Post -> verify success toast
     */
    @Test(priority = 2, groups = { "Smoke", "Regression" })
    public void CompletesImagePostWithCaption() throws InterruptedException {
        addPost.postImageWithCaption("Test image post");
    }

    @DataProvider
    public Object[][] getDataSuccessfullLogin() throws IOException {
        // Log in as the shared account tip@yopmail.com, which lives at
        // SmokeLoginData.json index 0 (same source the other tip@yopmail.com
        // TCs use - e.g. Dogpack_VideoFeed, Dogpack_Notifications, and the
        // native Dogpack_AddPhotoPost_native). Deliberately NOT LoginData.json,
        // whose index 0 is a different account (iamkiara02) shared by ~29 other
        // TCs - changing that file would switch every one of them.
        List<HashMap<String, String>> data = getJsonData(
                System.getProperty("user.dir")
                        + "//src//test//java//org//rahulshettyacademy"
                        + "//testData//SmokeLoginData.json");
        return new Object[][] { { data.get(0) } };
    }
}