package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.MarketplacePage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.SettingsAndActivityPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_AccountDeletion - PROMOTED from dev scaffold to smoke
 * suite. Validates the full account-deletion UI flow on the dog
 * profile.
 *
 * Account deletion triggers a 30-day grace period server-side -
 * the account remains usable, and a successful login REACTIVATES
 * it. This makes the test safely repeatable.
 *
 * Account: dpdelete@yopmail.com / Test@123 (LoginData.json index 5).
 * Dedicated account - not shared with any other test class.
 *
 * Position in suite XML: LAST class. Destructive nature means it
 * runs after every other class has finished, so no other test
 * inherits its end state.
 *
 * Flow (13 priorities):
 *   #1  Login as dpdelete@yopmail.com (LoginData.json index 5)
 *   #2  Tap Profile tab
 *   #3  Open hamburger / Settings & Activity
 *   #4  Tap Account and info
 *   #5  Handle Android location-permission popup ("While using app")
 *   #6  Scroll to "More settings"
 *   #7  Tap "More settings"
 *   #8  Tap "Manage profiles"
 *   #9  Tap "Delete User Account"
 *   #10 Tap "Delete" (first confirmation)
 *   #11 Tap "Confirm" (second confirmation - opens feedback modal)
 *   #12 Tap first feedback reason checkbox
 *   #13 Dismiss keyboard + tap SUBMIT
 */
public class Dogpack_AccountDeletion extends AndroidBaseTest {

	LoginPage login;
	ProfilePage profile;
	SettingsAndActivityPage settings;
	MarketplacePage marketplace;

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		login = new LoginPage(driver);
		profile = new ProfilePage(driver);
		settings = new SettingsAndActivityPage(driver);
		marketplace = new MarketplacePage(driver);
	}

	// ================================================================
	// ==========    SETUP - Login + reach Account screen        ======
	// ================================================================

	/** #1 - Login as the dedicated deletion-test account (index 5). */
	@Test(priority = 1, dataProvider = "getDpDeleteLogin",
			groups = { "Smoke", "Regression" })
	public void Login_AccountDeletion(HashMap<String, String> input)
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

	/** #2 - Tap Profile tab. */
	@Test(priority = 2,
			dependsOnMethods = { "Login_AccountDeletion" },
			groups = { "Smoke", "Regression" })
	public void ClickProfileTab_AccountDeletion() {
		marketplace.ClickProfileTabByText();
	}

	/** #3 - Open hamburger / Settings & Activity screen. */
	@Test(priority = 3,
			dependsOnMethods = { "ClickProfileTab_AccountDeletion" },
			groups = { "Smoke", "Regression" })
	public void NavigatesSettingActivityScreen_AccountDeletion()
			throws InterruptedException {
		settings.NavigatesToSettingAndActivityScreen();
	}

	/** #4 - Tap Account and info. */
	@Test(priority = 4,
			dependsOnMethods = { "NavigatesSettingActivityScreen_AccountDeletion" },
			groups = { "Smoke", "Regression" })
	public void ClickAccountAndInfo_AccountDeletion() {
		marketplace.ClickAccountAndInfo();
	}

	/**
	 * #5 - Handle the Android system location-permission popup that
	 * appears after tapping Account and info. Taps "While using the
	 * app". Silent no-op if the popup doesn't appear (cached grant).
	 */
	@Test(priority = 5,
			dependsOnMethods = { "ClickAccountAndInfo_AccountDeletion" },
			groups = { "Smoke", "Regression" })
	public void HandleLocationPermissionPopup_AccountDeletion() {
		marketplace.HandleLocationPermissionPopup();
	}

	// ================================================================
	// ==========    ACCOUNT DELETION FLOW (13-step)             ======
	// ================================================================

	/** #6 - Scroll down to bring "More settings" into view. */
	@Test(priority = 6,
			dependsOnMethods = { "HandleLocationPermissionPopup_AccountDeletion" },
			groups = { "Smoke", "Regression" })
	public void ScrollToMoreSettings_AccountDeletion() {
		profile.ScrollToMoreSettings();
	}

	/** #7 - Tap "More settings". */
	@Test(priority = 7,
			dependsOnMethods = { "ScrollToMoreSettings_AccountDeletion" },
			groups = { "Smoke", "Regression" })
	public void ClickMoreSettings_AccountDeletion() {
		profile.ClickMoreSettings();
	}

	/** #8 - Tap "Manage profiles". */
	@Test(priority = 8,
			dependsOnMethods = { "ClickMoreSettings_AccountDeletion" },
			groups = { "Smoke", "Regression" })
	public void ClickManageProfiles_AccountDeletion() {
		profile.ClickManageProfiles();
	}

	/** #9 - Tap "Delete User Account" link. */
	@Test(priority = 9,
			dependsOnMethods = { "ClickManageProfiles_AccountDeletion" },
			groups = { "Smoke", "Regression" })
	public void ClickDeleteUserAccount_AccountDeletion() {
		profile.ClickDeleteUserAccount();
	}

	/** #10 - Tap "Delete" on first confirmation. */
	@Test(priority = 10,
			dependsOnMethods = { "ClickDeleteUserAccount_AccountDeletion" },
			groups = { "Smoke", "Regression" })
	public void ClickDeleteConfirmation_AccountDeletion() {
		profile.ClickDeleteConfirmation();
	}

	/** #11 - Tap "Confirm" - opens the feedback modal. */
	@Test(priority = 11,
			dependsOnMethods = { "ClickDeleteConfirmation_AccountDeletion" },
			groups = { "Smoke", "Regression" })
	public void ClickConfirmDeletion_AccountDeletion() {
		profile.ClickConfirmDeletion();
	}

	/** #12 - Tap first feedback reason checkbox. */
	@Test(priority = 12,
			dependsOnMethods = { "ClickConfirmDeletion_AccountDeletion" },
			groups = { "Smoke", "Regression" })
	public void ClickFirstFeedbackReason_AccountDeletion() {
		profile.ClickFirstFeedbackReason();
	}

	/**
	 * #13 - Dismiss keyboard and tap SUBMIT. After this, the deletion
	 * request is submitted (30-day grace begins).
	 */
	@Test(priority = 13,
			dependsOnMethods = { "ClickFirstFeedbackReason_AccountDeletion" },
			groups = { "Smoke", "Regression" })
	public void SubmitFeedback_AccountDeletion() {
		profile.ScrollAndClickFeedbackSubmit();
	}

	/**
	 * Provides dpdelete credentials from LoginData.json (index 5).
	 * Same JSON-loading pattern used by other suite test classes.
	 */
	@DataProvider(name = "getDpDeleteLogin")
	public Object[][] getDpDeleteLogin() throws IOException {
		String jsonPath = Paths.get(
				System.getProperty("user.dir"),
				"src", "test", "java", "org", "rahulshettyacademy",
				"testData", "LoginData.json").toString();
		List<HashMap<String, String>> data = getJsonData(jsonPath);
		return new Object[][] { { data.get(5) } };
	}
}
