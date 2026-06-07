package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.NotificationsPage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_Notifications - Dog-profile notifications smoke test.
 *
 * Validates the basic notifications flow on the dog profile:
 *   #1  Login as tip@yopmail.com (LoginData.json index 0)
 *   #2  Tap Feed tab
 *   #3  Tap notifications icon
 *   #4  Tap "All" filter chip
 *   #5  Tap "New" section header
 *   #6  Extract + log + assert non-empty text from the first
 *       notification card area
 *
 * Position in suite XML: between Dogpack_Subscription_Dog and
 * Dogpack_Marketplace. Notifications are read-only; marketplace
 * changes the device location (Canada) which could affect
 * notification content - safer to test notifications first.
 *
 * Note: this test does not modify state. The user follows nobody,
 * taps no notifications, and dismisses none. No cleanup needed.
 */
public class Dogpack_Notifications extends AndroidBaseTest {

	LoginPage login;
	ProfilePage profile;
	NotificationsPage notifications;

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		login = new LoginPage(driver);
		profile = new ProfilePage(driver);
		notifications = new NotificationsPage(driver);
	}

	// ================================================================
	// ==========    SETUP - Login                              =======
	// ================================================================

	/** #1 - Login as tip@yopmail.com (shared dog test account). */
	@Test(priority = 1, dataProvider = "getNotificationsLogin",
			groups = { "Smoke", "Regression" })
	public void Login_Notifications(HashMap<String, String> input)
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

	// ================================================================
	// ==========    NOTIFICATIONS FLOW (5 UI steps)            =======
	// ================================================================

	/** #2 - Tap the Feed bottom-nav tab. */
	@Test(priority = 2,
			dependsOnMethods = { "Login_Notifications" },
			groups = { "Smoke", "Regression" })
	public void ClickFeed_Notifications() {
		notifications.ClickFeed();
	}

	/** #3 - Tap the notifications icon (bell/sport icon in header). */
	@Test(priority = 3,
			dependsOnMethods = { "ClickFeed_Notifications" },
			groups = { "Smoke", "Regression" })
	public void ClickNotificationsIcon_Notifications() {
		notifications.ClickNotificationsIcon();
	}

	/** #4 - Tap the "All" filter chip. */
	@Test(priority = 4,
			dependsOnMethods = { "ClickNotificationsIcon_Notifications" },
			groups = { "Smoke", "Regression" })
	public void ClickAllButton_Notifications() {
		notifications.ClickAllButton();
	}

	/** #5 - Tap the "New" section header. */
	@Test(priority = 5,
			dependsOnMethods = { "ClickAllButton_Notifications" },
			groups = { "Smoke", "Regression" })
	public void ClickNew_Notifications() {
		notifications.ClickNew();
	}

	/**
	 * #6 - Print + assert non-empty text from the first notification
	 * card area. Validates that the notifications listing has loaded
	 * with at least one card containing text content.
	 */
	@Test(priority = 6,
			dependsOnMethods = { "ClickNew_Notifications" },
			groups = { "Smoke", "Regression" })
	public void PrintNotificationText_Notifications() {
		notifications.PrintAndAssertNotificationCardText();
	}

	/**
	 * Provides tip@yopmail.com credentials from LoginData.json
	 * (index 0). Same shared dog account used by every other dog
	 * test class in the suite.
	 */
	@DataProvider(name = "getNotificationsLogin")
	public Object[][] getNotificationsLogin() throws IOException {
		String jsonPath = Paths.get(
				System.getProperty("user.dir"),
				"src", "test", "java", "org", "rahulshettyacademy",
				"testData", "LoginData.json").toString();
		List<HashMap<String, String>> data = getJsonData(jsonPath);
		return new Object[][] { { data.get(0) } };
	}
}
