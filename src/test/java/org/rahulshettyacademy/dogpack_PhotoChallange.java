package org.rahulshettyacademy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.PhotoChallangePage;
import org.testng.annotations.BeforeClass;
import java.time.Duration;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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

	

	/**
	 * Cleanup: re-login to get a guaranteed clean home screen, then check if a
	 * challenge photo was submitted during this TC run and delete it.
	 * This ensures joinChallenge always sees the 'Enter' button on the next run
	 * (which only appears when no photo has been submitted today).
	 *
	 * Flow: terminateApp + activateApp (clean slate) → re-login → open Photo
	 * Challenge → scroll to Today's Challenge → if 'View entry' present → delete.
	 * Every action is best-effort (try/catch) so cleanup NEVER fails the suite.
	 */
	@Test(priority = 99, alwaysRun = true)
	public void cleanupChallengeEntry() {
		System.out.println("[FLOW] cleanupChallengeEntry: re-login + checking for leftover challenge photo");
		try {
			// --- Step 1: terminate + relaunch app for a guaranteed clean state ---
			try {
				driver.terminateApp("com.dogpack");
				Thread.sleep(1500);
				driver.activateApp("com.dogpack");
				Thread.sleep(2000);
				System.out.println("[ACTION] cleanup: app relaunched");
			} catch (Exception e) {
				System.out.println("[WARN] cleanup: app relaunch failed: " + e.getMessage());
				return;
			}
			// --- Step 2: re-login with credentials from LoginData.json ---
			try {
				java.util.List<java.util.HashMap<String, String>> data = getJsonData(
					System.getProperty("user.dir")
					+ "//src//test//java//org//rahulshettyacademy//testData//LoginData.json");
				String email    = data.get(0).get("email");
				String password = data.get(0).get("password");
				login.scrollToLogin();
				login.NavigateToLogin();
				login.setEmailPassword(email, password);
				login.clickOnLoginSubmit();
				login.CompleteLoginProccess();
				System.out.println("[ACTION] cleanup: re-login complete");
			} catch (Exception e) {
				System.out.println("[WARN] cleanup: re-login failed: " + e.getMessage());
				return;
			}
			Thread.sleep(1500); // let home feed settle
			// --- Step 3: open Photo Challenge ---
			try {
				new WebDriverWait(driver, Duration.ofSeconds(10))
					.until(ExpectedConditions.elementToBeClickable(
						AppiumBy.accessibilityId("feed-challenge"))).click();
				System.out.println("[ACTION] cleanup: opened Photo Challenge screen");
			} catch (Exception e) {
				System.out.println("[INFO] cleanup: could not open Photo Challenge - skipping: " + e.getMessage());
				return;
			}
			Thread.sleep(1500); // let challenge screen settle
			// --- Step 4: scroll to find 'View entry' (today's submitted photo) ---
			boolean viewEntryFound = false;
			driver.manage().timeouts().implicitlyWait(Duration.ZERO);
			try {
				for (int i = 0; i < 6; i++) {
					if (!driver.findElements(AppiumBy.xpath(
							"//android.widget.TextView[@text='View entry']")).isEmpty()) {
						viewEntryFound = true; break;
					}
					photo.scrollDownTwice();
					Thread.sleep(400);
				}
			} catch (Exception ignore) {
			} finally {
				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			}
			if (!viewEntryFound) {
				System.out.println("[INFO] cleanup: no 'View entry' found - nothing to delete");
				return;
			}
			System.out.println("[ACTION] cleanup: 'View entry' found - deleting submitted photo");
			// --- Step 5: open the delete sheet ---
			try {
				new WebDriverWait(driver, Duration.ofSeconds(8))
					.until(ExpectedConditions.elementToBeClickable(AppiumBy.xpath(
						"//android.widget.ScrollView/android.view.ViewGroup"
						+ "/android.view.ViewGroup/android.view.ViewGroup[3]/android.widget.ImageView"))).click();
				System.out.println("[ACTION] cleanup: opened delete sheet");
			} catch (Exception e) {
				System.out.println("[WARN] cleanup: could not open delete sheet: " + e.getMessage());
				return;
			}
			// --- Step 6: tap Delete on the confirmation dialog ---
			try {
				new WebDriverWait(driver, Duration.ofSeconds(8))
					.until(ExpectedConditions.elementToBeClickable(
						AppiumBy.xpath("//android.widget.TextView[@text='Delete']"))).click();
				System.out.println("[ACTION] cleanup: photo deleted - TC is clean for next run");
			} catch (Exception e) {
				System.out.println("[WARN] cleanup: Delete confirm failed: " + e.getMessage());
			}
		} catch (Exception e) {
			System.out.println("[WARN] cleanupChallengeEntry unexpected error: " + e.getMessage());
		}
	}

}