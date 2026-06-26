package org.rahulshettyacademy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.PhotoChallangePage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class dogpack_PhotoChallange extends AndroidBaseTest {

	LoginPage login;
	PhotoChallangePage photo;
	
	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		login = new LoginPage(driver);
		photo = new PhotoChallangePage(driver);
	}

	@Test(priority = 1, dataProvider = "getDataSuccessfullLogin", groups = { "Smoke", "Regression" })
	public void PrerequestFunctionsforProfile(HashMap<String, String> input) throws InterruptedException {
		login.scrollToLogin();
		login.NavigateToLogin();
		login.setEmailPassword(input.get("email"), input.get("password"));
		login.clickOnLoginSubmit();
		login.CompleteLoginProccess();
		login.HandleCustomDialog(384, 576);
	}
	
	@Test(priority = 2, groups = { "Smoke","Regression" })
	public void navigatesToChallengeScreen() throws InterruptedException {
		photo.navigatesToChallengeScreen();
	}
	
	@Test(priority = 3, groups = { "Smoke","Regression" })
	public void joinChallenge() throws InterruptedException {
		photo.joinChallenge();
	}
	
	@Test(priority = 4, groups = { "Smoke","Regression" })
	public void DeleteChallengePhotoCancelOption() throws InterruptedException {
		photo.DeleteChallengePhotoCancelOption();
	}
	
	@Test(priority = 5, groups = { "Smoke","Regression" })
	public void DeleteChallengePhotoDeleteOption() throws InterruptedException {
		photo.DeleteChallengePhotoDeleteOption();
	}
	
	@Test(priority = 6, groups = { "Smoke","Regression" })
	public void joinChallengeAfterDeletePhoto() throws InterruptedException {
		photo.joinChallengeAfterDeletePhoto();
	}
	
	@Test(priority = 7, groups = { "Smoke","Regression" })
	public void ViewAllUpcomingChallenges() throws InterruptedException {
		photo.ViewAllUpcomingChallenges();
	}
	
	@Test(priority = 8, groups = { "Smoke","Regression" })
	public void ViewAllPreviousChallenges() throws InterruptedException {
		photo.ViewAllPreviousChallenges();
	}
	
	@Test(priority = 9, groups = { "Smoke","Regression" })
	public void TryToEnterInUpcomingChallenge() throws InterruptedException {
		photo.TryToEnterInUpcomingChallenge();
	}
	
	@Test(priority = 10, groups = { "Smoke","Regression" })
	public void ViewWinnersOfPreviousChallenge() throws InterruptedException {
		photo.ViewWinnersOfPreviousChallenge();
	}
	
	@Test(priority = 11, groups = { "Smoke","Regression" })
	public void navigatesToLeaderShipBoard() throws InterruptedException {
		photo.navigatesToLeaderShipBoard();
	}
	
	
	@Test(priority = 12, groups = { "Smoke","Regression" })
	public void navigatesToLast7Days() throws InterruptedException {
		photo.navigatesToLast7Days();
	}
	
	@Test(priority = 13, groups = { "Smoke","Regression" })
	public void navigatesToWinnerProfile() throws InterruptedException {
		photo.navigatesToWinnerProfile();
	}
	
	@Test(priority = 14, groups = { "Smoke","Regression" })
	public void MessageWinnerUser() throws InterruptedException {
		photo.MessageWinnerUser();
	}
	
	@Test(priority = 15, groups = { "Smoke","Regression" })
	public void ThreeDotActionPerformed() throws InterruptedException {
		photo.ThreeDotActionPerformed();
	}
	
	@Test(priority = 16, groups = { "Smoke","Regression" })
	public void navigatesToResultBoard() throws InterruptedException {
		photo.navigatesToResultBoard();
	}
	
	@DataProvider
	public Object[][] getDataSuccessfullLogin() throws IOException {
		List<HashMap<String, String>> data = getJsonData(System.getProperty("user.dir")
				+ "//src//test//java//org//rahulshettyacademy//testData//LoginData.json");

		return new Object[][] { { data.get(0) } };
	}

	
}
