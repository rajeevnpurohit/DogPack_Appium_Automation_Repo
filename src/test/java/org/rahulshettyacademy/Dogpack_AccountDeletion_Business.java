package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.MarketplacePage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.ProfileSwitcherPage;
import org.rahulshettyacademy.pageObjects.android.SettingsAndActivityPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_AccountDeletion_Business - business-profile variant of the
 * account-deletion smoke test.
 *
 * Identical to Dogpack_AccountDeletion except for ONE extra step at
 * the start: SwitchToFirstBusinessProfile to activate the business
 * entity on the dpdelete@yopmail.com account. Account deletion is
 * account-level (not profile-level), so deleting from business
 * context destroys the same underlying user account that
 * Dogpack_AccountDeletion deletes from dog context.
 *
 * Account: dpdelete@yopmail.com / Test@123 (SmokeLoginData.json index 1).
 * PREREQUISITE: a business entity must be provisioned on this
 * account before the first run. Re-submitting the delete request
 * (from a previous test run) is idempotent on the server, so the
 * test is safely repeatable like the dog version.
 *
 * Position in suite XML: 11th (LAST) class - after
 * Dogpack_AccountDeletion. Both deletion tests are destructive so
 * they run at the very end of the suite.
 *
 * Flow (14 priorities):
 *   #1  Login as dpdelete@yopmail.com (SmokeLoginData.json index 1)
 *   #2  Switch to first business profile (NEW for business)
 *   #3  Tap Profile tab
 *   #4  Open hamburger / Settings & Activity
 *   #5  Tap Account and info
 *   #6  Handle Android location-permission popup ("While using app")
 *   #7  Scroll to "More settings"
 *   #8  Tap "More settings"
 *   #9  Tap "Manage profiles"
 *   #10 Tap "Delete User Account"
 *   #11 Tap "Delete" (first confirmation)
 *   #12 Tap "Confirm" (second confirmation - opens feedback modal)
 *   #13 Tap first feedback reason checkbox
 *   #14 Dismiss keyboard + tap SUBMIT
 */
public class Dogpack_AccountDeletion_Business extends AndroidBaseTest {

	LoginPage login;
	ProfilePage profile;
	ProfileSwitcherPage profileSwitcher;
	SettingsAndActivityPage settings;
	MarketplacePage marketplace;

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		login = new LoginPage(driver);
		profile = new ProfilePage(driver);
		profileSwitcher = new ProfileSwitcherPage(driver);
		settings = new SettingsAndActivityPage(driver);
		marketplace = new MarketplacePage(driver);
	}

	// ================================================================
	// ==========    SETUP - Login + switch to business         =======
	// ================================================================

	/** #1 - Login as the dedicated deletion-test account (index 1). */
	@Test(priority = 1, dataProvider = "getDpDeleteLogin",
			groups = { "Smoke", "Regression" })
	public void Login_AccountDeletionBiz(HashMap<String, String> input)
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

	/**
	 * #2 - Switch to first business profile. PREREQUISITE: a business
	 * entity must exist on dpdelete@yopmail.com before the first run.
	 * Uses the parameterized SwitchToFirstBusinessProfile(String)
	 * overload because dpdelete's business profile shows title text
	 * "Dp delete" (per actual app behavior - see assertion below).
	 *
	 * NOTE: The string "Dp delete" looks more like a user's display
	 * name (first + last) than a typical business entity name. If
	 * downstream steps behave unexpectedly (e.g., the deletion flow
	 * doesn't appear to be running on a business profile), this is
	 * the first place to check - the switch may not be landing on
	 * the business entity as intended.
	 *
	 * SLEEP: 10s wait at the start to let the app fully settle after
	 * login before the switcher UI is exercised. Without this, the
	 * dropdown / business-section taps were intermittently flaky.
	 * Hardcoded delay is a known code smell - replace with a proper
	 * "wait-until-condition" check if a stable readiness indicator
	 * can be identified.
	 */
	@Test(priority = 2,
			dependsOnMethods = { "Login_AccountDeletionBiz" },
			groups = { "Smoke", "Regression" })
	public void SwitchToFirstBusinessProfile_AccountDeletionBiz()
			throws InterruptedException {
		Thread.sleep(10000);
		profileSwitcher.SwitchToFirstBusinessProfile("Dp delete");
	}

	/** #3 - Tap Profile tab. */
	@Test(priority = 3,
			dependsOnMethods = { "SwitchToFirstBusinessProfile_AccountDeletionBiz" },
			groups = { "Smoke", "Regression" })
	public void ClickProfileTab_AccountDeletionBiz() {
		marketplace.ClickProfileTabByText();
	}

	/** #4 - Open hamburger / Settings & Activity screen. */
	@Test(priority = 4,
			dependsOnMethods = { "ClickProfileTab_AccountDeletionBiz" },
			groups = { "Smoke", "Regression" })
	public void NavigatesSettingActivityScreen_AccountDeletionBiz()
			throws InterruptedException {
		settings.NavigatesToSettingAndActivityScreen();
	}

	/** #5 - Tap Account and info. */
	@Test(priority = 5,
			dependsOnMethods = { "NavigatesSettingActivityScreen_AccountDeletionBiz" },
			groups = { "Smoke", "Regression" })
	public void ClickAccountAndInfo_AccountDeletionBiz() {
		marketplace.ClickAccountAndInfo();
	}

	/**
	 * #6 - Handle the Android system location-permission popup that
	 * appears after tapping Account and info. Taps "While using the
	 * app". Silent no-op if the popup doesn't appear (cached grant).
	 */
	@Test(priority = 6,
			dependsOnMethods = { "ClickAccountAndInfo_AccountDeletionBiz" },
			groups = { "Smoke", "Regression" })
	public void HandleLocationPermissionPopup_AccountDeletionBiz() {
		marketplace.HandleLocationPermissionPopup();
	}

	// ================================================================
	// ==========    ACCOUNT DELETION FLOW                       ======
	// ================================================================

	/** #7 - Scroll down to bring "More settings" into view. */
	@Test(priority = 7,
			dependsOnMethods = { "HandleLocationPermissionPopup_AccountDeletionBiz" },
			groups = { "Smoke", "Regression" })
	public void ScrollToMoreSettings_AccountDeletionBiz() {
		profile.ScrollToMoreSettings();
	}

	/** #8 - Tap "More settings". */
	@Test(priority = 8,
			dependsOnMethods = { "ScrollToMoreSettings_AccountDeletionBiz" },
			groups = { "Smoke", "Regression" })
	public void ClickMoreSettings_AccountDeletionBiz() {
		profile.ClickMoreSettings();
	}

	/** #9 - Tap "Manage profiles". */
	@Test(priority = 9,
			dependsOnMethods = { "ClickMoreSettings_AccountDeletionBiz" },
			groups = { "Smoke", "Regression" })
	public void ClickManageProfiles_AccountDeletionBiz() {
		profile.ClickManageProfiles();
	}

	/** #10 - Tap "Delete User Account" link. */
	@Test(priority = 10,
			dependsOnMethods = { "ClickManageProfiles_AccountDeletionBiz" },
			groups = { "Smoke", "Regression" })
	public void ClickDeleteUserAccount_AccountDeletionBiz() {
		profile.ClickDeleteUserAccount();
	}

	/** #11 - Tap "Delete" on first confirmation. */
	@Test(priority = 11,
			dependsOnMethods = { "ClickDeleteUserAccount_AccountDeletionBiz" },
			groups = { "Smoke", "Regression" })
	public void ClickDeleteConfirmation_AccountDeletionBiz() {
		profile.ClickDeleteConfirmation();
	}

	/** #12 - Tap "Confirm" - opens the feedback modal. */
	@Test(priority = 12,
			dependsOnMethods = { "ClickDeleteConfirmation_AccountDeletionBiz" },
			groups = { "Smoke", "Regression" })
	public void ClickConfirmDeletion_AccountDeletionBiz() {
		profile.ClickConfirmDeletion();
	}

	/** #13 - Tap first feedback reason checkbox. */
	@Test(priority = 13,
			dependsOnMethods = { "ClickConfirmDeletion_AccountDeletionBiz" },
			groups = { "Smoke", "Regression" })
	public void ClickFirstFeedbackReason_AccountDeletionBiz() {
		profile.ClickFirstFeedbackReason();
	}

	/**
	 * #14 - Dismiss keyboard and tap SUBMIT. After this, the deletion
	 * request is submitted (30-day grace begins, or refreshed if a
	 * prior submission already exists from Dogpack_AccountDeletion).
	 */
	@Test(priority = 14,
			dependsOnMethods = { "ClickFirstFeedbackReason_AccountDeletionBiz" },
			groups = { "Smoke", "Regression" })
	public void SubmitFeedback_AccountDeletionBiz() {
		profile.ScrollAndClickFeedbackSubmit();
	}

	/**
	 * Provides dpdelete credentials from SmokeLoginData.json (index 1).
	 * Same JSON-loading pattern + same credentials as the dog version.
	 */
	@DataProvider(name = "getDpDeleteLogin")
	public Object[][] getDpDeleteLogin() throws IOException {
		String jsonPath = Paths.get(
				System.getProperty("user.dir"),
				"src", "test", "java", "org", "rahulshettyacademy",
				"testData", "SmokeLoginData.json").toString();
		List<HashMap<String, String>> data = getJsonData(jsonPath);
		return new Object[][] { { data.get(1) } };
	}
}
