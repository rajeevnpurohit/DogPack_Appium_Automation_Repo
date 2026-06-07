package org.rahulshettyacademy;

import java.util.HashMap;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.MarketplacePage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.SettingsAndActivityPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * DogPack_AccountDeletion_Test - STANDALONE test for validating
 * the full account-deletion UI flow on the dog profile.
 *
 * Purpose:
 *   Development scaffold to validate the 12-step account-deletion
 *   flow (Login -> Profile -> hamburger -> Account -> scroll ->
 *   More settings -> Manage profiles -> Delete User Account ->
 *   Delete -> Confirm -> first feedback reason -> Submit).
 *
 * IMPORTANT:
 *   Account deletion triggers a 30-day grace period server-side -
 *   the account remains usable, and a successful login REACTIVATES
 *   it. The test is therefore safely repeatable on the same
 *   credentials.
 *
 *   Hardcoded credentials: dpdelete@yopmail.com / Test@123.
 *   These are deliberately NOT added to LoginData.json - keeping
 *   the scaffold self-contained.
 *
 * NOT INCLUDED IN THE SMOKE SUITE XML. Run on its own via:
 *   mvn test -Dtest=DogPack_AccountDeletion_Test
 *
 * Flow (12 priorities):
 *   #1  Login as dpdelete@yopmail.com
 *   #2  Tap Profile tab
 *   #3  Open hamburger / settings
 *   #4  Tap Account and info
 *   #5  Scroll to More settings
 *   #6  Tap More settings
 *   #7  Tap Manage profiles
 *   #8  Tap Delete User Account
 *   #9  Tap Delete (first confirmation)
 *   #10 Tap Confirm (second confirmation)
 *   #11 Tap first feedback reason checkbox
 *   #12 Scroll + tap SUBMIT
 *
 * No @AfterMethod cleanup - account is destined for deletion
 * anyway, and the 30-day grace makes the test repeatable.
 */
public class DogPack_AccountDeletion_Test extends AndroidBaseTest {

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

	/** #1 - Login as the dedicated deletion-test account. */
	@Test(priority = 1, dataProvider = "getDpDeleteLogin",
			groups = { "AccountDeletion" })
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
			groups = { "AccountDeletion" })
	public void ClickProfileTab_AccountDeletion() {
		marketplace.ClickProfileTabByText();
	}

	/** #3 - Open hamburger / Settings & Activity screen. */
	@Test(priority = 3,
			dependsOnMethods = { "ClickProfileTab_AccountDeletion" },
			groups = { "AccountDeletion" })
	public void NavigatesSettingActivityScreen_AccountDeletion()
			throws InterruptedException {
		settings.NavigatesToSettingAndActivityScreen();
	}

	/** #4 - Tap Account and info. */
	@Test(priority = 4,
			dependsOnMethods = { "NavigatesSettingActivityScreen_AccountDeletion" },
			groups = { "AccountDeletion" })
	public void ClickAccountAndInfo_AccountDeletion() {
		marketplace.ClickAccountAndInfo();
	}

	/**
	 * #5 - Handle the Android system location-permission popup that
	 * appears after tapping Account and info. Taps "While using the
	 * app". The HandleLocationPermissionPopup() method is a silent
	 * no-op if the popup doesn't appear (some Android versions cache
	 * the grant after first time).
	 */
	@Test(priority = 5,
			dependsOnMethods = { "ClickAccountAndInfo_AccountDeletion" },
			groups = { "AccountDeletion" })
	public void HandleLocationPermissionPopup_AccountDeletion() {
		marketplace.HandleLocationPermissionPopup();
	}

	// ================================================================
	// ==========    ACCOUNT DELETION FLOW (12-step)             ======
	// ================================================================

	/** #6 - Scroll down to bring "More settings" into view. */
	@Test(priority = 6,
			dependsOnMethods = { "HandleLocationPermissionPopup_AccountDeletion" },
			groups = { "AccountDeletion" })
	public void ScrollToMoreSettings_AccountDeletion() {
		profile.ScrollToMoreSettings();
	}

	/** #7 - Tap "More settings". */
	@Test(priority = 7,
			dependsOnMethods = { "ScrollToMoreSettings_AccountDeletion" },
			groups = { "AccountDeletion" })
	public void ClickMoreSettings_AccountDeletion() {
		profile.ClickMoreSettings();
	}

	/** #8 - Tap "Manage profiles". */
	@Test(priority = 8,
			dependsOnMethods = { "ClickMoreSettings_AccountDeletion" },
			groups = { "AccountDeletion" })
	public void ClickManageProfiles_AccountDeletion() {
		profile.ClickManageProfiles();
	}

	/** #9 - Tap "Delete User Account" link. */
	@Test(priority = 9,
			dependsOnMethods = { "ClickManageProfiles_AccountDeletion" },
			groups = { "AccountDeletion" })
	public void ClickDeleteUserAccount_AccountDeletion() {
		profile.ClickDeleteUserAccount();
	}

	/** #10 - Tap "Delete" on first confirmation. */
	@Test(priority = 10,
			dependsOnMethods = { "ClickDeleteUserAccount_AccountDeletion" },
			groups = { "AccountDeletion" })
	public void ClickDeleteConfirmation_AccountDeletion() {
		profile.ClickDeleteConfirmation();
	}

	/** #11 - Tap "Confirm" - opens the feedback modal. */
	@Test(priority = 11,
			dependsOnMethods = { "ClickDeleteConfirmation_AccountDeletion" },
			groups = { "AccountDeletion" })
	public void ClickConfirmDeletion_AccountDeletion() {
		profile.ClickConfirmDeletion();
	}

	/** #12 - Tap first feedback reason checkbox. */
	@Test(priority = 12,
			dependsOnMethods = { "ClickConfirmDeletion_AccountDeletion" },
			groups = { "AccountDeletion" })
	public void ClickFirstFeedbackReason_AccountDeletion() {
		profile.ClickFirstFeedbackReason();
	}

	/**
	 * #13 - Scroll and tap SUBMIT. After this, the account deletion
	 * request is submitted (30-day grace begins).
	 */
	@Test(priority = 13,
			dependsOnMethods = { "ClickFirstFeedbackReason_AccountDeletion" },
			groups = { "AccountDeletion" })
	public void SubmitFeedback_AccountDeletion() {
		profile.ScrollAndClickFeedbackSubmit();
	}

	/**
	 * Provides hardcoded dpdelete credentials. Kept inline (not in
	 * LoginData.json) since the scaffold isn't in the smoke suite
	 * and shouldn't pollute the shared data file.
	 */
	@DataProvider(name = "getDpDeleteLogin")
	public Object[][] getDpDeleteLogin() {
		HashMap<String, String> creds = new HashMap<>();
		creds.put("email", "dpdelete@yopmail.com");
		creds.put("password", "Test@123");
		return new Object[][] { { creds } };
	}
}