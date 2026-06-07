package org.rahulshettyacademy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.HomePage;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_Homepage - Test class for the home/feed module.
 *
 * Refactor approach: Identical structure to original, only changes:
 *   - All original test methods preserved (same names, same priorities)
 *   - alwaysRun=true added so failures in one test don't skip later tests
 *   - dependsOnMethods NOT used (each test independent)
 */
public class Dogpack_Homepage extends AndroidBaseTest {

	LoginPage login;
	HomePage home;
	ProfilePage profile;

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		login = new LoginPage(driver);
		home = new HomePage(driver);
		profile = new ProfilePage(driver);
	}

	@Test(priority = 1, dataProvider = "getDataSuccessfullLogin",
	      groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void PrerequestFunctionsforProfile(HashMap<String, String> input) throws InterruptedException {
		login.scrollToLogin();
		login.NavigateToLogin();
		login.setEmailPassword(input.get("email"), input.get("password"));
		login.clickOnLoginSubmit();
		login.CompleteLoginProccess();
		login.HandleCustomDialog(384, 576);
	}

	@Test(priority = 2, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void NavigatesToNearByFeed() throws InterruptedException {
		home.ClickOnDogPackDropdownFeedOption();
		home.SelectNearByFeedOption();
	}

	@Test(priority = 3, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void NavigatesToForYouFeed() throws InterruptedException {
		home.ClickOnDogPackDropdownFeedOption();
		home.SelectForYouFeedOption();
	}

	@Test(priority = 4, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void NavigatesMostRecentFeed() throws InterruptedException {
		home.ClickOnDogPackDropdownFeedOption();
		home.SelectMostRecentFeedOption();
	}

	@Test(priority = 5, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void OtherUserProfileNavigation() throws InterruptedException {
		home.OtherUserProfileNavigation();
	}

	@Test(priority = 6, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void FollowOrUnfollowFromFeedScreen() throws InterruptedException {
		home.FollowOrUnfollowFromFeedScreen();
	}

	@Test(priority = 7, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void blockUser() throws InterruptedException {
		home.blockUser();
		
	}
	
	@Test(priority = 8, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void SaveDownloadDeleteImagePost() throws InterruptedException {
		home.SaveDownloadDeleteImagePost();
	}

	@Test(priority = 9, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void ReportContent() throws InterruptedException {
		home.ReportContent();
	}

	@Test(priority = 10, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void reportUser() throws InterruptedException {
		home.reportUser();
	}

	@Test(priority = 11, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void SuperDogFeature() throws InterruptedException {
		home.SuperDogFeature();
	}

	@Test(priority = 12, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void ApplyFilterOnFeed() throws InterruptedException {
		home.ApplyFilterOnFeed();
	}

	@Test(priority = 17, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void LikeOrUnlikePost() throws InterruptedException {
		home.LikeOrUnlikeFirstVisiblePost();
	}

	@Test(priority = 18, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void CommentOnPost() throws InterruptedException {
		home.CommentOnPost();
	}

	@Test(priority = 19, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void LikeFirstVisibleComment() throws InterruptedException {
		home.LikeFirstVisibleComment();
	}

	@Test(priority = 20, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void ClickFirstVisibleReplyIfPresent() throws InterruptedException {
		home.ClickFirstVisibleReplyIfPresent();
	}

	@Test(priority = 21, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void DeleteCommentOnPost() throws InterruptedException {
		home.DeleteCommentOnPost();
	}

//	@Test(priority = 22, groups = { "Smoke", "Regression" })
//	public void CreatePost() throws InterruptedException {
//		home.AddImagePost();
//	}
//
//	@Test(priority = 23, groups = { "Smoke", "Regression" })
//	public void DeletePost() throws InterruptedException {
//		profile.navigateToProfileScreen();
//		home.DeletePost();
//	}

	@DataProvider
	public Object[][] getDataSuccessfullLogin() throws IOException {
		List<HashMap<String, String>> data = getJsonData(System.getProperty("user.dir")
				+ "//src//test//java//org//rahulshettyacademy//testData//LoginData.json");
		return new Object[][] { { data.get(0) } };
	}
}