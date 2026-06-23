package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.NotificationsPage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.SettingsAndActivityPage;
import org.rahulshettyacademy.pageObjects.android.VideoFeedPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_VideoFeed - Smoke test for the Video Feed feature (center of the
 * home screen), Dog profile.
 *
 * <p>Logs in as the shared dog account tip@yopmail.com (SmokeLoginData.json
 * index 0) using the existing login module, lands on the home screen, then
 * walks the Video Feed controls: open feed, like, comment, close comment
 * box, share, copy link, save video, three-dots.
 *
 * <p>Purely additive - reuses LoginPage / NotificationsPage and the new
 * VideoFeedPage. Business-profile coverage will follow separately.
 */
public class Dogpack_VideoFeed extends AndroidBaseTest {

	LoginPage login;
	NotificationsPage notifications; // reused for DismissAllOnboarding()
	VideoFeedPage videoFeed;
	ProfilePage profile;
	SettingsAndActivityPage setting;

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		login = new LoginPage(driver);
		notifications = new NotificationsPage(driver);
		videoFeed = new VideoFeedPage(driver);
		profile = new ProfilePage(driver);
		setting = new SettingsAndActivityPage(driver);
	}

	// ================================================================
	// ==========    SETUP - Login (Dog profile)               =======
	// ================================================================

	/** #1 - Login as tip@yopmail.com (dog) and land on the home screen. */
	@Test(priority = 1, dataProvider = "getVideoFeedLogin",
			groups = { "Smoke", "Regression" })
	public void Login_VideoFeed(HashMap<String, String> input)
			throws InterruptedException {
		System.out.println("[INFO]   Logging in: " + input.get("email"));
		login.scrollToLogin();
		login.NavigateToLogin();
		login.setEmailPassword(input.get("email"), input.get("password"));
		login.clickOnLoginSubmit();
		login.CompleteLoginProccess();
		login.HandleCustomDialog(0, 0);
		notifications.DismissAllOnboarding();
		System.out.println("[FLOW]   Logged in - on home screen (Video Feed area)");
	}

	// ================================================================
	// ==========    VIDEO FEED FLOW (Dog profile)             =======
	// ================================================================

	/** #2 - Open the Video Feed. */
	@Test(priority = 2, dependsOnMethods = { "Login_VideoFeed" },
			groups = { "Smoke", "Regression" })
	public void ClickVideoFeed_VideoFeed() {
		videoFeed.clickVideoFeed();
	}

	/** #3 - Like the current video. */
	@Test(priority = 3, dependsOnMethods = { "ClickVideoFeed_VideoFeed" },
			groups = { "Smoke", "Regression" })
	public void ClickLike_VideoFeed() {
		videoFeed.clickLike();
	}

	/** #4 - Open the comment box. */
	@Test(priority = 4, dependsOnMethods = { "ClickLike_VideoFeed" },
			groups = { "Smoke", "Regression" })
	public void ClickComment_VideoFeed() {
		videoFeed.clickComment();
	}

	/** #5 - Close the comment box (long-press the handle and swipe down). */
	@Test(priority = 5, dependsOnMethods = { "ClickComment_VideoFeed" },
			groups = { "Smoke", "Regression" })
	public void CloseCommentBox_VideoFeed() {
		videoFeed.closeCommentBox();
	}

	/** #6 - Open Share. */
	@Test(priority = 6, dependsOnMethods = { "CloseCommentBox_VideoFeed" },
			groups = { "Smoke", "Regression" })
	public void ClickShare_VideoFeed() {
		videoFeed.clickShare();
	}

	/** #7 - Copy the video link. */
	@Test(priority = 7, dependsOnMethods = { "ClickShare_VideoFeed" },
			groups = { "Smoke", "Regression" })
	public void ClickCopyLink_VideoFeed() {
		videoFeed.clickCopyLink();
	}

	/** #8 - Save the video. */
	@Test(priority = 8, dependsOnMethods = { "ClickCopyLink_VideoFeed" },
			groups = { "Smoke", "Regression" })
	public void ClickSaveVideo_VideoFeed() {
		videoFeed.clickSaveVideo();
	}

	/** #9 - Unsave the video (toggle the Save button off). */
	@Test(priority = 9, dependsOnMethods = { "ClickSaveVideo_VideoFeed" },
			groups = { "Smoke", "Regression" })
	public void ClickUnsaveVideo_VideoFeed() {
		videoFeed.clickUnsaveVideo();
	}

	/** #10 - Open the three-dots / more menu. (DISABLED for now) */
	@Test(priority = 10, dependsOnMethods = { "ClickUnsaveVideo_VideoFeed" },
			groups = { "Smoke", "Regression" })
	public void ClickThreeDots_VideoFeed() {
		videoFeed.clickThreeDots();
	}

	/** #11 - Mute the video. */
	@Test(priority = 11, dependsOnMethods = { "ClickUnsaveVideo_VideoFeed" },
			groups = { "Smoke", "Regression" })
	public void ClickMute_VideoFeed() {
		videoFeed.clickMute();
	}

	/** #12 - Unmute the video (toggle mute off). */
	@Test(priority = 12, dependsOnMethods = { "ClickMute_VideoFeed" },
			groups = { "Smoke", "Regression" })
	public void ClickUnmute_VideoFeed() {
		videoFeed.clickUnmute();
	}

	/** #13 - Unblock the user that was blocked in the three-dots flow. */
	@Test(priority = 13, dependsOnMethods = { "ClickThreeDots_VideoFeed" },
			groups = { "Smoke", "Regression" })
	public void UnblockUser_VideoFeed() throws InterruptedException {
		String username = VideoFeedPage.getBlockedUsername();
		if (username == null || username.trim().isEmpty()) {
			throw new RuntimeException(
					"No blocked username captured - the block flow in "
					+ "ClickThreeDots_VideoFeed must run first.");
		}
		profile.navigateToProfileScreen();   // (1) Profile icon
		setting.unblockUserByUsername(username); // (2)-(7) hamburger -> Blocked Users -> Unblock -> Confirm
	}

	/** tip@yopmail.com credentials from SmokeLoginData.json (index 0). */
	@DataProvider(name = "getVideoFeedLogin")
	public Object[][] getVideoFeedLogin() throws IOException {
		String jsonPath = Paths.get(
				System.getProperty("user.dir"),
				"src", "test", "java", "org", "rahulshettyacademy",
				"testData", "SmokeLoginData.json").toString();
		List<HashMap<String, String>> data = getJsonData(jsonPath);
		return new Object[][] { { data.get(0) } };
	}
}