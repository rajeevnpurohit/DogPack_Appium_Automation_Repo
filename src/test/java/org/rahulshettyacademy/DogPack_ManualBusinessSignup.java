package org.rahulshettyacademy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.ManualSignupPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * DogPack_ManualBusinessSignup - Manual signup creating a NEW business account.
 *
 * Path: SignUp Welcome (email/password) -> UserType (Business) -> UserName ->
 *       businessAlready (Continue without claim) -> businessRegisterDetails form ->
 *       OTP -> 2nd screen (location/service/image/description) ->
 *       3rd screen (phone) -> Submit -> NewNotification -> Home
 *
 * Removed unused page object instantiations (was creating Login + Profile + Settings
 * + ForgotPassword for no reason).
 */
public class DogPack_ManualBusinessSignup extends AndroidBaseTest {

	private ManualSignupPage manual;

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		manual = new ManualSignupPage(driver);
	}

	@Test(priority = 1,
	      groups = { "Smoke", "Regression" },
	      description = "Create a new business account end-to-end via manual signup",
	      dataProvider = "getDataUniqueSignupBusiness")
	public void CreateBusinessAccountManual(HashMap<String, String> input) {
		System.out.println(">>> TEST START: CreateBusinessAccountManual");
		// JSON value should be "AUTO" or "AUTO:biz" - generates fresh email each run
		String email = manual.resolveSignupEmail(input.get("email"));
		manual.SignupWithEmailPassword(email, input.get("password"));
		manual.CreateBusinessAccount();
		System.out.println(">>> TEST END: CreateBusinessAccountManual");
	}

	@DataProvider
	public Object[][] getDataUniqueSignupBusiness() throws IOException {
		List<HashMap<String, String>> data = getJsonData(System.getProperty("user.dir")
				+ "//src//test//java//org//rahulshettyacademy//testData//SignupData.json");
		return new Object[][] { { data.get(3) } };
	}
}