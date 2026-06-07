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
 * DogPack_ManualBusinessSignupUnclaimed - Claim an existing UNCLAIMED business
 * via search and OTP verification.
 *
 * Path: SignUp Welcome (email/password) -> UserType (Business) -> UserName ->
 *       Permission popup (location) -> Search unclaimed business -> Claim ->
 *       Yes confirmation modal -> OTP -> "Great!" -> optional "Rate Us" ->
 *       NewNotification (no Distance modal in this flow)
 *
 * Removed unused page object instantiations (only Manual page needed).
 */
public class DogPack_ManualBusinessSignupUnclaimed extends AndroidBaseTest {

	private ManualSignupPage manual;

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		manual = new ManualSignupPage(driver);
	}

	@Test(priority = 1,
	      groups = { "Smoke", "Regression" },
	      description = "Claim an existing unclaimed business via search and OTP",
	      dataProvider = "getDataUniqueSignupUnclaimedBusiness")
	public void CreateUnclaimedBusinessAccountManual(HashMap<String, String> input)
			throws InterruptedException {
		// JSON value should be "AUTO" or "AUTO:unclaim" - generates fresh email each run
		String email = manual.resolveSignupEmail(input.get("email"));
		manual.SignupWithEmailPassword(email, input.get("password"));
		manual.CreateUnclaimedBusinessAccount();
	}

	@DataProvider
	public Object[][] getDataUniqueSignupUnclaimedBusiness() throws IOException {
		List<HashMap<String, String>> data = getJsonData(System.getProperty("user.dir")
				+ "//src//test//java//org//rahulshettyacademy//testData//SignupData.json");
		return new Object[][] { { data.get(5) } };
	}
}