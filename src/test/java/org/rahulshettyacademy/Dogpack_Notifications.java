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
 *   #1   Login as tip@yopmail.com (SmokeLoginData.json index 0)
 *   #2   Tap Feed tab
 *   #3   Tap notifications icon
 *   #4   Tap "All" filter chip
 *   #5   Tap "New Followers" filter chip
 *   #6   Tap "Likes" filter chip
 *   #7   Tap "Comments" filter chip
 *   #8   Tap "Mentions" filter chip
 *   --- Steps temporarily disabled (Engage link issue) ---
 *   (disabled) Tap "Engage" empty-state text
 *   (disabled) Tap "PROFILES" tab (search screen)
 *   (disabled) Tap "PARKS" tab (search screen)
 *   (disabled) Tap "BUSINESSES" tab (search screen)
 *   (disabled) Tap "HASHTAGS" tab (search screen)
 *   (disabled) Tap back button (search screen)
 *   --- Inbox tab flow continues normally ---
 *   #9   Tap "Inbox" top tab
 *   #10  Tap "All" filter chip (on Inbox)
 *   #11  Tap "Unread" filter chip
 *   #12  Tap "Groups" filter chip
 *   #13  Tap "Park Groups" filter chip
 *
 * Position in suite XML: between Dogpack_Subscription_Dog and
 * Dogpack_Marketplace. Notifications are read-only; marketplace
 * changes the device location (Canada) which could affect
 * notification content - safer to test notifications first.
 *
 * Note: this test does not modify state. The user follows nobody,
 * taps no notifications, and dismisses none. No cleanup needed.
 *
 * Onboarding handling: calls notifications.DismissAllOnboarding()
 * at two checkpoints (after login + before ClickFeed) to dismiss
 * first-time-user popups (Turn on Notifications screen, system
 * permission dialog, Feed tutorial 5-step walkthrough, units of
 * measurement modal, Profile tutorial 2-step walkthrough). These
 * popups otherwise block test progression when the app is in a
 * fresh-install / first-login state.
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
		// Dismiss any post-login onboarding popups before navigating
		// (Turn on Notifications screen, system permission dialog).
		notifications.DismissAllOnboarding();
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
		// Profile tutorial (2-step) may fire after navigateToProfileScreen.
		// Also dismisses Feed tutorial popups that auto-progress through
		// 5 steps and may still be partially visible at this point.
		notifications.DismissAllOnboarding();
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

	/** #5 - Tap the "New Followers" filter chip. */
	@Test(priority = 5,
			dependsOnMethods = { "ClickAllButton_Notifications" },
			groups = { "Smoke", "Regression" })
	public void ClickNewFollowers_Notifications() {
		notifications.ClickNewFollowers();
	}

	/** #6 - Tap the "Likes" filter chip. */
	@Test(priority = 6,
			dependsOnMethods = { "ClickNewFollowers_Notifications" },
			groups = { "Smoke", "Regression" })
	public void ClickLikes_Notifications() {
		notifications.ClickLikes();
	}

	/** #7 - Tap the "Comments" filter chip. */
	@Test(priority = 7,
			dependsOnMethods = { "ClickLikes_Notifications" },
			groups = { "Smoke", "Regression" })
	public void ClickComments_Notifications() {
		notifications.ClickComments();
	}

	/** #8 - Tap the "Mentions" filter chip. */
	@Test(priority = 8,
			dependsOnMethods = { "ClickComments_Notifications" },
			groups = { "Smoke", "Regression" })
	public void ClickMentions_Notifications() {
		notifications.ClickMentions();
	}

	// ================================================================
	// ==========  TEMPORARILY DISABLED - SEARCH/DISCOVERY FLOW  ======
	// ================================================================
	// The following 6 test steps (ClickEngage through ClickGoBack) are
	// commented out for now. The "Engage" link uses a React Native
	// ClickableSpan inside a SpannableString, which requires further
	// investigation to click reliably via Appium. Until that's
	// resolved, the entire search/discovery screen flow is skipped.
	// Re-enable as a batch when ClickEngage is fixed - and when doing
	// so, renumber priorities to fit between #8 and #9 (will need to
	// also bump the Inbox section's priorities accordingly).
	//
	// ClickInboxMenu_Notifications below depends on
	// ClickMentions_Notifications (the last passing step before the
	// disabled section) so the Inbox tab tests continue to run.
	// ----------------------------------------------------------------

//	/**
//	 * Tap the "Engage" empty-state text shown when the Mentions
//	 * filter has no entries.
//	 */
//	@Test(priority = 9,
//			dependsOnMethods = { "ClickMentions_Notifications" },
//			groups = { "Smoke", "Regression" })
//	public void ClickEngage_Notifications() {
//		notifications.ClickEngage();
//	}
//
//	/** Tap the "PROFILES" tab on the search/discovery screen. */
//	@Test(priority = 10,
//			dependsOnMethods = { "ClickEngage_Notifications" },
//			groups = { "Smoke", "Regression" })
//	public void ClickProfiles_Notifications() {
//		notifications.ClickProfiles();
//	}
//
//	/** Tap the "PARKS" tab on the search/discovery screen. */
//	@Test(priority = 11,
//			dependsOnMethods = { "ClickProfiles_Notifications" },
//			groups = { "Smoke", "Regression" })
//	public void ClickParks_Notifications() {
//		notifications.ClickParks();
//	}
//
//	/** Tap the "BUSINESSES" tab on the search/discovery screen. */
//	@Test(priority = 12,
//			dependsOnMethods = { "ClickParks_Notifications" },
//			groups = { "Smoke", "Regression" })
//	public void ClickBusinesses_Notifications() {
//		notifications.ClickBusinesses();
//	}
//
//	/** Tap the "HASHTAGS" tab on the search/discovery screen. */
//	@Test(priority = 13,
//			dependsOnMethods = { "ClickBusinesses_Notifications" },
//			groups = { "Smoke", "Regression" })
//	public void ClickHashtags_Notifications() {
//		notifications.ClickHashtags();
//	}
//
//	/**
//	 * Tap the back button on the search/discovery screen to return
//	 * to the Notifications screen.
//	 *
//	 * NOTE: also disabled because it depends on ClickHashtags above
//	 * AND can't physically function without the Search screen being
//	 * open. Re-enable together with the rest of the disabled batch.
//	 */
//	@Test(priority = 14,
//			dependsOnMethods = { "ClickHashtags_Notifications" },
//			groups = { "Smoke", "Regression" })
//	public void ClickGoBack_Notifications() {
//		notifications.ClickGoBack();
//	}

	// ================================================================
	// ==========    INBOX TAB FLOW (5 UI steps)                =======
	// ================================================================

	/** #9 - Tap the "Inbox" tab at the top of the Notifications screen. */
	@Test(priority = 9,
			dependsOnMethods = { "ClickMentions_Notifications" },
			groups = { "Smoke", "Regression" })
	public void ClickInboxMenu_Notifications() {
		notifications.ClickInbox();
	}

	/**
	 * #10 - Tap the "All" filter chip on the Inbox tab. Reuses the
	 * existing ClickAllButton method since the "All" chip XPath is
	 * identical between the Notifications and Inbox tabs - only the
	 * current screen context determines which one resolves.
	 */
	@Test(priority = 10,
			dependsOnMethods = { "ClickInboxMenu_Notifications" },
			groups = { "Smoke", "Regression" })
	public void ClickAllInInbox_Notifications() {
		notifications.ClickAllButton();
	}

	/** #11 - Tap the "Unread" filter chip on the Inbox tab. */
	@Test(priority = 11,
			dependsOnMethods = { "ClickAllInInbox_Notifications" },
			groups = { "Smoke", "Regression" })
	public void ClickUnread_Notifications() {
		notifications.ClickUnread();
	}

	/** #12 - Tap the "Groups" filter chip on the Inbox tab. */
	@Test(priority = 12,
			dependsOnMethods = { "ClickUnread_Notifications" },
			groups = { "Smoke", "Regression" })
	public void ClickGroups_Notifications() {
		notifications.ClickGroups();
	}

	/** #13 - Tap the "Park Groups" filter chip on the Inbox tab. */
	@Test(priority = 13,
			dependsOnMethods = { "ClickGroups_Notifications" },
			groups = { "Smoke", "Regression" })
	public void ClickParkGroups_Notifications() {
		notifications.ClickParkGroups();
	}

	/**
	 * Provides tip@yopmail.com credentials from SmokeLoginData.json
	 * (index 0). Same shared dog account used by every other dog
	 * test class in the suite.
	 */
	@DataProvider(name = "getNotificationsLogin")
	public Object[][] getNotificationsLogin() throws IOException {
		String jsonPath = Paths.get(
				System.getProperty("user.dir"),
				"src", "test", "java", "org", "rahulshettyacademy",
				"testData", "SmokeLoginData.json").toString();
		List<HashMap<String, String>> data = getJsonData(jsonPath);
		return new Object[][] { { data.get(0) } };
	}
}