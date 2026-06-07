package org.rahulshettyacademy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.ManualSignupPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * DogPack_ManualSignupSkipCase - Signup flow where the user skips creating
 * a Dog profile entirely.
 *
 * Path: SignUp Welcome -> UserType chooser -> "Skip for now" -> NewNotification -> Home
 *
 * Removed unused page-object instantiations (only Manual + Login needed).
 */
public class DogPack_ManualSignupSkipCase extends AndroidBaseTest {

	private LoginPage login;
	private ManualSignupPage manual;

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		login = new LoginPage(driver);
		manual = new ManualSignupPage(driver);
	}

	@Test(priority = 1,
	      groups = { "Smoke", "Regression" },
	      description = "Signup with unique email but skip dog profile creation",
	      dataProvider = "getDataUniqueUserWithNoInformation")
	public void UniqueUserWithNoInformation(HashMap<String, String> input) {
		// JSON value should be "AUTO" or "AUTO:noinfo" - generates fresh email each run
		String email = manual.resolveSignupEmail(input.get("email"));
		manual.SignupWithEmailPassword(email, input.get("password"));
		manual.CreateNewDogwithNoInformation();
		login.HandleCustomDialog(384, 576);
	}

	@DataProvider
	public Object[][] getDataUniqueUserWithNoInformation() throws IOException {
		List<HashMap<String, String>> data = getJsonData(System.getProperty("user.dir")
				+ "//src//test//java//org//rahulshettyacademy//testData//SignupData.json");
		return new Object[][] { { data.get(4) } };
	}
}