package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rahulshettyacademy.utils.AndroidActions;
import org.testng.Assert;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class LoginPage extends AndroidActions {

	AndroidDriver driver;

	public LoginPage(AndroidDriver driver) {
		super(driver);
		this.driver = driver;
		// Increase PageFactory locator timeout (e.g., 10s)
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	@AndroidFindBy(accessibility = "signup_login")
	private WebElement loginBtn;

	@AndroidFindBy(accessibility = "login_username")
	private WebElement usernameField;

	@AndroidFindBy(accessibility = "login_password")
	private WebElement passwordField;

	@AndroidFindBy(accessibility = "login_button")
	private WebElement loginSubmit;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Invalid credentials.\"]")
	private WebElement popupLocator;

	// IMPORTANT: Text is "Yes, Notify Me" (capitalized) because:
	// - JSON has: "Yes, notify me" (lowercase)
	// - PrimaryButton component applies textTransform: 'capitalize' CSS
	// - Result on screen: "Yes, Notify Me" (this is what Appium sees)
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Yes, Notify Me\"]")
	private WebElement notifyMe;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_button")
	public WebElement allowPermission;

	@AndroidFindBy(accessibility = "feed-distance-submit")
	private WebElement homePageSubmit;

	public void scrollToLogin() {
		System.out.println("[FLOW] scrollToLogin: scrolling to the Login entry");
		scrollToText("Log In");
	}

	public void NavigateToLogin() {
		System.out.println("[ACTION] NavigateToLogin: opening the login form");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(loginBtn));
		wait.until(ExpectedConditions.elementToBeClickable(loginBtn)).click();
		System.out.println("[ACTION] Clicked loginBtn");
	}

	public void pressBackWithKeyEvent() {
		System.out.println("[FLOW] pressBackWithKeyEvent: pressing device Back");
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
	}

	public void setEmailPassword(String name, String password) {
		System.out.println("[INPUT] setEmailPassword: entering email and password");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(usernameField));
		wait.until(ExpectedConditions.elementToBeClickable(usernameField)).sendKeys(name);
		System.out.println("[ACTION] Entered text in usernameField");
		wait.until(ExpectedConditions.visibilityOf(passwordField));
		wait.until(ExpectedConditions.elementToBeClickable(passwordField)).sendKeys(password);
		System.out.println("[ACTION] Entered text in passwordField");
		driver.hideKeyboard();
		System.out.println("[ACTION] Hid keyboard");
	}

	public void clickOnLoginSubmit() {
		System.out.println("[ACTION] clickOnLoginSubmit: submitting the login form");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(loginSubmit));
		wait.until(ExpectedConditions.elementToBeClickable(loginSubmit)).click();
		System.out.println("[ACTION] Clicked loginSubmit");
	}

	/**
	 * Complete the login process after credentials are submitted.
	 * 
	 * SMART HANDLING: This method handles MULTIPLE post-login scenarios:
	 * 
	 * SCENARIO 1: First login (or notification permission not granted yet)
	 *   - "Yes, Notify Me" screen appears -> click it
	 *   - System permission dialog appears -> Allow
	 *   - feed-distance-submit appears on home -> click it
	 * 
	 * SCENARIO 2: Repeat login (notification permission already granted at app level)
	 *   - NewNotification screen is SKIPPED by app
	 *   - System dialog also doesn't appear
	 *   - Distance modal STILL appears (first-time configuration) -> click it
	 * 
	 * SCENARIO 3: Returning user (notification AND distance already configured)
	 *   - All conditional screens are SKIPPED by app
	 *   - Direct land on home feed
	 *   - NO modal to dismiss - just verify feed loaded
	 * 
	 * App logic reference: src/screen/Login.js line 200-230
	 * Distance modal logic: src/screen/Feeds.js (Helper.getData("distancePermission"))
	 */
	public void CompleteLoginProccess() {
		System.out.println("[FLOW] CompleteLoginProccess: handling post-login dialogs");
		// Short wait for optional elements (conditional screens)
		WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
		// Very short wait for distance modal (only first-time)
		WebDriverWait quickWait = new WebDriverWait(driver, Duration.ofSeconds(8));
		
		// ========================================
		// STEP 1: Try to handle "Yes, Notify Me" screen (OPTIONAL)
		// ========================================
		try {
			System.out.println("[FLOW] Checking if 'Yes, Notify Me' screen appears...");
			shortWait.until(ExpectedConditions.elementToBeClickable(notifyMe)).click();
			System.out.println("[ACTION] [Scenario 1] NewNotification screen appeared - clicked 'Yes, Notify Me'");
			
			// ========================================
			// STEP 2: Handle Android system permission dialog (OPTIONAL)
			// ========================================
			try {
				WebElement notificationPopup = shortWait.until(ExpectedConditions.presenceOfElementLocated(
						By.xpath("//android.widget.TextView[@resource-id=\"com.android.permissioncontroller:id/permission_message\"]")));
				String ExpectednotificationPopup = notificationPopup.getText();
				System.out.println("[INFO] Permission dialog text: " + ExpectednotificationPopup);
				
				if (!ExpectednotificationPopup.toLowerCase().contains("notification")) {
					System.out.println("[WARN] WARNING: Permission text mismatch - expected text containing 'notification'");
				}
				
				shortWait.until(ExpectedConditions.visibilityOf(allowPermission));
				shortWait.until(ExpectedConditions.elementToBeClickable(allowPermission)).click();
				System.out.println("[ACTION] System permission dialog - clicked Allow");
				
			} catch (Exception permissionEx) {
				System.out.println("[INFO] System permission dialog skipped (may already be granted): " 
						+ permissionEx.getClass().getSimpleName());
			}
			
		} catch (Exception notifyMeEx) {
			System.out.println("[INFO] [Scenario 2/3] NewNotification screen SKIPPED - notification already granted at app level");
		}
		
		// ========================================
		// STEP 3: feed-distance-submit (CONDITIONAL - only first-time setup)
		// ========================================
		// Previously this was REQUIRED with 30s wait, causing test failures for
		// returning users. Now made truly conditional with 8s timeout.
		try {
			System.out.println("[ACTION] Checking for feed-distance-submit modal (first-time only)...");
			quickWait.until(ExpectedConditions.visibilityOf(homePageSubmit));
			quickWait.until(ExpectedConditions.elementToBeClickable(homePageSubmit)).click();
			System.out.println("[ACTION] [Scenario 1/2] Clicked feed-distance-submit - distance modal dismissed");
		} catch (Exception distanceEx) {
			System.out.println("[INFO] [Scenario 3] Distance modal SKIPPED - already configured for this user");
		}
		
		System.out.println("[OK] Login flow completed!");
	}

	public void HandleCustomDialog(int xc, int yc) {
		System.out.println("[FLOW] HandleCustomDialog: dismissing the custom dialog");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		Assert.assertTrue(wait
				.until(ExpectedConditions
						.presenceOfElementLocated(By.xpath("//android.view.View[@content-desc=\"profile-view\"]")))
				.isDisplayed(), "'Search' button is not visible on the HomeScreen screen.");

		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		System.out.println("[ACTION] Pressed device Back");
	}
}