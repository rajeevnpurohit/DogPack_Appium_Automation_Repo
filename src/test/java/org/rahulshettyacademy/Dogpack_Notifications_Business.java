package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.NotificationsPage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.ProfileSwitcherPage;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_Notifications_Business - Business-profile notifications
 * smoke test. Mirrors Dogpack_Notifications.java but operates on
 * the BUSINESS entity (Tipper9Business) instead of the default dog
 * profile.
 *
 * Validates the basic notifications flow on the business profile:
 *   #1   Login as tip@yopmail.com (shared dog+business account)
 *   #2   Switch entity to Tipper9Business
 *   #3   Tap Feed tab
 *   #4   Tap notifications icon
 *   #5   Tap "All" filter chip
 *   #6   Tap "New Followers" filter chip
 *   #7   Tap "Likes" filter chip
 *   #8   Tap "Comments" filter chip
 *   #9   Tap "Mentions" filter chip
 *   --- Steps temporarily disabled (Engage link issue) ---
 *   (disabled) Tap "Engage" empty-state text
 *   (disabled) Tap "PROFILES" tab (search screen)
 *   (disabled) Tap "PARKS" tab (search screen)
 *   (disabled) Tap "BUSINESSES" tab (search screen)
 *   (disabled) Tap "HASHTAGS" tab (search screen)
 *   (disabled) Tap back button (search screen)
 *   --- Inbox tab flow continues normally ---
 *   #10  Tap "Inbox" top tab
 *   #11  Tap "All" filter chip (on Inbox)
 *   #12  Tap "Unread" filter chip
 *   #13  Tap "Groups" filter chip
 *   #14  Tap "Park Groups" filter chip
 *
 * Position in suite XML: AFTER Dogpack_Notifications (dog version),
 * between the dog Subscription test class and the business
 * Marketplace test class. Both notifications classes share the same
 * NotificationsPage object - filter chip XPaths and the
 * notifications icon target are app-wide, not profile-specific.
 *
 * Credentials: same tip@yopmail.com used by every other dog/business
 * test (the account owns both the dog profile Tipper9 and the
 * business profile Tipper9Business). The profile switch in #2 puts
 * the session on the business side before the notifications flow
 * begins.
 *
 * Note: this test does not modify state. The user follows nobody,
 * taps no notifications, and dismisses none. No cleanup needed.
 *
 * Onboarding handling: calls notifications.DismissAllOnboarding()
 * at two checkpoints (after profile switch and before ClickFeed) to
 * dismiss any first-time-user popups that fire after entering the
 * business profile context. Idempotent, so extra calls are harmless.
 * The post-login popup chain is handled inside login.HandleCustomDialog
 * and login.CompleteLoginProccess - we deliberately do NOT call
 * DismissAllOnboarding before navigateToProfileScreen here, matching
 * the proven setup pattern from Dogpack_Subscription_Business.
 *
 * Cleanup: @AfterClass(alwaysRun = true) reverts the active entity
 * back to the dog profile after all tests complete (pass or fail).
 * Hygiene-only since Notifications is read-only - the revert just
 * prevents subsequent suite re-runs or manual app access from
 * inheriting business-entity state. Failures inside cleanup are
 * logged but never thrown, so they cannot mask real test results.
 * See revertToDogProfile() at the bottom of this class.
 */
public class Dogpack_Notifications_Business extends AndroidBaseTest {

	LoginPage login;
	ProfilePage profile;
	ProfileSwitcherPage profileSwitcher;
	NotificationsPage notifications;

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		login = new LoginPage(driver);
		profile = new ProfilePage(driver);
		profileSwitcher = new ProfileSwitcherPage(driver);
		notifications = new NotificationsPage(driver);
	}

	// ================================================================
	// ==========    SETUP CHAIN (login + profile switch)       =======
	// ================================================================

	/** #1 - Login as tip@yopmail.com (shared dog+business account). */
	@Test(priority = 1, dataProvider = "getBusinessUserLogin",
			groups = { "Smoke", "Regression" })
	public void LoginForBusinessNotifications(HashMap<String, String> input)
			throws InterruptedException {
		System.out.println("[INFO]   Logging in as shared account: "
				+ input.get("email"));
		login.scrollToLogin();
		login.NavigateToLogin();
		login.setEmailPassword(input.get("email"), input.get("password"));
		login.clickOnLoginSubmit();
		login.CompleteLoginProccess();
		login.HandleCustomDialog(0, 0);
		profile.navigateToProfileScreen();
	}

	/** #2 - Switch entity to the first business profile (Tipper9Business). */
	@Test(priority = 2,
			dependsOnMethods = { "LoginForBusinessNotifications" },
			groups = { "Smoke", "Regression" })
	public void SwitchToBusinessForNotifications() {
		profileSwitcher.SwitchToFirstBusinessProfile();
		// Some business profiles trigger their own onboarding popups
		// the first time they're entered (business tutorial overlays,
		// fresh permission prompts). Dismiss any that fire here.
		notifications.DismissAllOnboarding();
	}

	// ================================================================
	// ==========    NOTIFICATIONS FLOW (7 UI steps)            =======
	// ================================================================

	/** #3 - Tap the Feed bottom-nav tab. */
	@Test(priority = 3,
			dependsOnMethods = { "SwitchToBusinessForNotifications" },
			groups = { "Smoke", "Regression" })
	public void ClickFeed_Business() {
		// Catch any remaining onboarding popups (Profile tutorial 2-step,
		// Feed tutorial 5-step) that may still be partially visible.
		notifications.DismissAllOnboarding();
		notifications.ClickFeed();
	}

	/** #4 - Tap the notifications icon (bell/sport icon in header). */
	@Test(priority = 4,
			dependsOnMethods = { "ClickFeed_Business" },
			groups = { "Smoke", "Regression" })
	public void ClickNotificationsIcon_Business() {
		notifications.ClickNotificationsIcon();
	}

	/** #5 - Tap the "All" filter chip. */
	@Test(priority = 5,
			dependsOnMethods = { "ClickNotificationsIcon_Business" },
			groups = { "Smoke", "Regression" })
	public void ClickAllButton_Business() {
		notifications.ClickAllButton();
	}

	/** #6 - Tap the "New Followers" filter chip. */
	@Test(priority = 6,
			dependsOnMethods = { "ClickAllButton_Business" },
			groups = { "Smoke", "Regression" })
	public void ClickNewFollowers_Business() {
		notifications.ClickNewFollowers();
	}

	/** #7 - Tap the "Likes" filter chip. */
	@Test(priority = 7,
			dependsOnMethods = { "ClickNewFollowers_Business" },
			groups = { "Smoke", "Regression" })
	public void ClickLikes_Business() {
		notifications.ClickLikes();
	}

	/** #8 - Tap the "Comments" filter chip. */
	@Test(priority = 8,
			dependsOnMethods = { "ClickLikes_Business" },
			groups = { "Smoke", "Regression" })
	public void ClickComments_Business() {
		notifications.ClickComments();
	}

	/** #9 - Tap the "Mentions" filter chip. */
	@Test(priority = 9,
			dependsOnMethods = { "ClickComments_Business" },
			groups = { "Smoke", "Regression" })
	public void ClickMentions_Business() {
		notifications.ClickMentions();
	}

	// ================================================================
	// ==========  TEMPORARILY DISABLED - SEARCH/DISCOVERY FLOW  ======
	// ================================================================
	// The following 6 test steps (ClickEngage through ClickGoBack) are
	// commented out for now, mirroring the dog-profile version. The
	// "Engage" link uses a React Native ClickableSpan inside a
	// SpannableString, which requires further investigation to click
	// reliably via Appium. Re-enable as a batch when ClickEngage is
	// fixed in the page object.
	//
	// ClickInboxMenu_Business below depends on ClickMentions_Business
	// (the last passing step before the disabled section) so the
	// Inbox tab tests continue to run.
	// ----------------------------------------------------------------

//	/**
//	 * Tap the "Engage" empty-state text shown when the Mentions
//	 * filter has no entries.
//	 */
//	@Test(priority = 10,
//			dependsOnMethods = { "ClickMentions_Business" },
//			groups = { "Smoke", "Regression" })
//	public void ClickEngage_Business() {
//		notifications.ClickEngage();
//	}
//
//	/** Tap the "PROFILES" tab on the search/discovery screen. */
//	@Test(priority = 11,
//			dependsOnMethods = { "ClickEngage_Business" },
//			groups = { "Smoke", "Regression" })
//	public void ClickProfiles_Business() {
//		notifications.ClickProfiles();
//	}
//
//	/** Tap the "PARKS" tab on the search/discovery screen. */
//	@Test(priority = 12,
//			dependsOnMethods = { "ClickProfiles_Business" },
//			groups = { "Smoke", "Regression" })
//	public void ClickParks_Business() {
//		notifications.ClickParks();
//	}
//
//	/** Tap the "BUSINESSES" tab on the search/discovery screen. */
//	@Test(priority = 13,
//			dependsOnMethods = { "ClickParks_Business" },
//			groups = { "Smoke", "Regression" })
//	public void ClickBusinesses_Business() {
//		notifications.ClickBusinesses();
//	}
//
//	/** Tap the "HASHTAGS" tab on the search/discovery screen. */
//	@Test(priority = 14,
//			dependsOnMethods = { "ClickBusinesses_Business" },
//			groups = { "Smoke", "Regression" })
//	public void ClickHashtags_Business() {
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
//	@Test(priority = 15,
//			dependsOnMethods = { "ClickHashtags_Business" },
//			groups = { "Smoke", "Regression" })
//	public void ClickGoBack_Business() {
//		notifications.ClickGoBack();
//	}

	// ================================================================
	// ==========    INBOX TAB FLOW (5 UI steps)                =======
	// ================================================================

	/** #10 - Tap the "Inbox" tab at the top of the Notifications screen. */
	@Test(priority = 10,
			dependsOnMethods = { "ClickMentions_Business" },
			groups = { "Smoke", "Regression" })
	public void ClickInboxMenu_Business() {
		notifications.ClickInbox();
	}

	/**
	 * #11 - Tap the "All" filter chip on the Inbox tab. Reuses the
	 * existing ClickAllButton method since the "All" chip XPath is
	 * identical between the Notifications and Inbox tabs - only the
	 * current screen context determines which one resolves.
	 */
	@Test(priority = 11,
			dependsOnMethods = { "ClickInboxMenu_Business" },
			groups = { "Smoke", "Regression" })
	public void ClickAllInInbox_Business() {
		notifications.ClickAllButton();
	}

	/** #12 - Tap the "Unread" filter chip on the Inbox tab. */
	@Test(priority = 12,
			dependsOnMethods = { "ClickAllInInbox_Business" },
			groups = { "Smoke", "Regression" })
	public void ClickUnread_Business() {
		notifications.ClickUnread();
	}

	/** #13 - Tap the "Groups" filter chip on the Inbox tab. */
	@Test(priority = 13,
			dependsOnMethods = { "ClickUnread_Business" },
			groups = { "Smoke", "Regression" })
	public void ClickGroups_Business() {
		notifications.ClickGroups();
	}

	/** #14 - Tap the "Park Groups" filter chip on the Inbox tab. */
	@Test(priority = 14,
			dependsOnMethods = { "ClickGroups_Business" },
			groups = { "Smoke", "Regression" })
	public void ClickParkGroups_Business() {
		notifications.ClickParkGroups();
	}

	// ================================================================
	// ==========    CLEANUP (revert business -> dog profile)   =======
	// ================================================================

	/**
	 * Revert the active entity back to the dog profile after all tests
	 * in this class complete. Runs unconditionally (alwaysRun = true)
	 * so the user is not left on the business profile if any test
	 * failed mid-flow.
	 *
	 * This is hygiene-only - Notifications doesn't mutate any
	 * server-side or device state. The cleanup ensures that
	 * re-running the suite or opening the app manually right after
	 * this class doesn't inherit business-entity state.
	 *
	 * Sequence (5 steps):
	 *   0. ClickBackButton - exit the Notifications/Inbox screen
	 *      first, since the bottom-nav Profile tab is hidden while
	 *      the notifications screen is active. Without this step
	 *      the dog-switch's step 1 (tap Profile tab) cannot find
	 *      its target.
	 *   1-4. Reuses the 4 validated business->dog methods on
	 *      ProfileSwitcherPage (same sequence used inside
	 *      Dogpack_Marketplace_Business's @AfterMethod cleanup):
	 *        1. ClickProfileTabByTextView
	 *        2. ClickArrowDownForProfileSwitching
	 *        3. ClickDogProfileSwitcher
	 *        4. ClickSelectProfileForDog
	 *
	 * Wrapped in try/catch so a failure here only LOGS - it does
	 * NOT throw. Cleanup must never mask a real test failure;
	 * if the revert breaks, the test report still reflects the
	 * actual test outcomes accurately.
	 *
	 * TestNG lifecycle: subclass @AfterClass runs BEFORE the
	 * superclass (AndroidBaseTest) @AfterClass, so we get a chance
	 * to switch profiles while the driver is still alive and the
	 * app is still running.
	 */
	@AfterClass(alwaysRun = true)
	public void revertToDogProfile() {
		System.out.println(
				"[CLEANUP] === Reverting business -> dog profile ===");
		try {
			// Step 0: exit the Notifications/Inbox screen so the
			// bottom-nav Profile tab becomes accessible again.
			notifications.ClickBackButton();
			// Steps 1-4: standard business -> dog switch sequence.
			profileSwitcher.ClickProfileTabByTextView();
			profileSwitcher.ClickArrowDownForProfileSwitching();
			profileSwitcher.ClickDogProfileSwitcher();
			profileSwitcher.ClickSelectProfileForDog();
			System.out.println("[CLEANUP] Dog profile switch complete.");
		} catch (Exception e) {
			System.out.println("[CLEANUP] !!! Dog profile revert FAILED "
					+ "(non-fatal): " + e.getClass().getSimpleName()
					+ ": " + e.getMessage()
					+ ". User may be left on business profile - "
					+ "manual intervention recommended before next run.");
			e.printStackTrace(System.out);
		}
	}

	/**
	 * Provides tip@yopmail.com credentials from SmokeLoginData.json
	 * (index 0). Same shared account used by every other business
	 * test class in the suite (Subscription_Business, BoostAccount_
	 * Business, etc.) - the account owns both the dog profile and
	 * the business profile, and step #2 switches the session to the
	 * business side before the notifications flow runs.
	 */
	@DataProvider(name = "getBusinessUserLogin")
	public Object[][] getBusinessUserLogin() throws IOException {
		String jsonPath = Paths.get(
				System.getProperty("user.dir"),
				"src", "test", "java", "org", "rahulshettyacademy",
				"testData", "SmokeLoginData.json").toString();
		List<HashMap<String, String>> data = getJsonData(jsonPath);
		return new Object[][] { { data.get(0) } };
	}
}