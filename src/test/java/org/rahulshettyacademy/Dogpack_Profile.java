package org.rahulshettyacademy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_Profile - Test suite for profile module.
 *
 * NOTE: alwaysRun=true is set on all tests so that a failure in one test
 * does not cascade-skip subsequent tests. Each test independently navigates
 * to its required starting state where possible.
 *
 * Known app-side limitation: DeleteDogProfile (priority 8) is expected to
 * fail in the current build because the "Delete Profile" button has been
 * removed from the regular Edit screen (only accessible via ManageProfile
 * flow which is unreachable in main UI). See ProfilePage.DeleteDogProfile()
 * for full details.
 */
public class Dogpack_Profile extends AndroidBaseTest {

	LoginPage login;
	ProfilePage profile;

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		login = new LoginPage(driver);
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
	public void NavigtesToProfileTab() throws InterruptedException {
		profile.navigateToProfileScreen();
	}

	@Test(priority = 3, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void navigatesToAllTabsInProfile() throws InterruptedException {
		profile.navigatesToAllTabsInProfile();
	}

	@Test(priority = 4, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void shareProfile() throws InterruptedException {
		profile.shareProfile();
	}

	@Test(priority = 5, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void ClickOnSubTabsInProfile() throws InterruptedException {
		profile.ClickOnSubTabsInProfile();
	}

	@Test(priority = 6, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void UpdateProfileDetails() throws InterruptedException {
		profile.ClickOnEditBtn();
		profile.EditProfileDetails();
		// profile.verifyProfileUpdateMessage();  // now handled inside EditProfileDetails
	}

	@Test(priority = 7, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void createNewDogProfile() throws InterruptedException {
		profile.createNewDogProfile();
	}


	@DataProvider
	public Object[][] getDataSuccessfullLogin() throws IOException {
		List<HashMap<String, String>> data = getJsonData(System.getProperty("user.dir")
				+ "//src//test//java//org//rahulshettyacademy//testData//LoginData.json");

		return new Object[][] { { data.get(0) } };
	}
}