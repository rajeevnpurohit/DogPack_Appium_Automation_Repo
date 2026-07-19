package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rahulshettyacademy.utils.AndroidActions;
import org.testng.Assert;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

/**
 * SocialLoginPage - Page object for Gmail + Facebook social login.
 *
 * SAFE VERSION (May 20):
 *   - All locators verified against app source (NO changes)
 *   - ensureAppForeground() at start of both public methods + after auth
 *   - safeBackPress() replaces all direct BACK calls (3 instances)
 *   - Goal: each test PASS or FAIL cleanly - NO app background after social auth,
 *     NO cascade hangs from auth provider context switch
 *
 * App source verified (May 20):
 *   - login_google: Login.js line 999, signUpWelcome/index.js line 1027
 *   - login_fbook:  Login.js line 1014, signUpWelcome/index.js line 1044
 *   - clickdogbus:  signUpFlow/UserType/index.js line 426
 *   - usernameInput / userContinue: signUpFlow/userName/index.js line 674, 722
 *   - dogProfileImage: DogProfileForm.js line 964
 *   - feed-distance-submit: Feeds.js (verified)
 *   - All testIDs UNCHANGED in current build (logic refactor only)
 */
public class SocialLoginPage extends AndroidActions {

	AndroidDriver driver;
	WebDriverWait wait;
	WebDriverWait shortWait;

	public SocialLoginPage(AndroidDriver driver) {
		super(driver);
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	// ============================================================
	// LOCATORS - Social login buttons
	// ============================================================

	@AndroidFindBy(accessibility = "login_google")
	private WebElement googleSocialLogin;

	@AndroidFindBy(accessibility = "login_fbook")
	private WebElement facebookSocialLogin;

	// Google account picker (system UI - not app's testID)
	@AndroidFindBy(xpath = "(//android.widget.LinearLayout[@resource-id=\"com.google.android.gms:id/container\"])[1]/android.widget.LinearLayout")
	private WebElement selectFirstGmailAccount;

	@AndroidFindBy(xpath = "//android.widget.Button[contains(@text, 'Continue as')]")
	private WebElement facebookContinueBtn;

	// ============================================================
	// LOCATORS - UserType chooser
	// ============================================================

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Create a profile for your Dog\"]")
	private WebElement textOfDogProfileType;

	@AndroidFindBy(accessibility = "dogProfile")
	private WebElement dogProfileChoice;

	@AndroidFindBy(accessibility = "businessProfile")
	private WebElement businessProfileChoice;

	@AndroidFindBy(accessibility = "skipForNow")
	private WebElement skipForNowLink;

	@AndroidFindBy(accessibility = "clickdogbus")
	private WebElement userTypeContinueBtn;

	// ============================================================
	// LOCATORS - Username screen
	// ============================================================

	@AndroidFindBy(accessibility = "usernameInput")
	private WebElement userNameField;

	@AndroidFindBy(accessibility = "userContinue")
	private WebElement userNameScreenContinueBtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Username taken\"]")
	private WebElement usernameTakenPopup;

	@AndroidFindBy(accessibility = "onConfirm")
	private WebElement usernameTakenPopupConfirmBtn;

	@AndroidFindBy(accessibility = "onCancel")
	private WebElement usernameTakenPopupCancelBtn;

	// ============================================================
	// LOCATORS - Dog profile form
	// ============================================================

	@AndroidFindBy(accessibility = "test_input")
	private WebElement dogNameField;

	@AndroidFindBy(accessibility = "Select Breed 1")
	private WebElement breedDropdown;

	@AndroidFindBy(xpath = "//android.widget.EditText[@text=\"Search breed\"]")
	private WebElement searchBreedField;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Adopted\"]")
	private WebElement breedName;

	@AndroidFindBy(accessibility = "Female")
	private WebElement dogGender;

	@AndroidFindBy(accessibility = "YYYY/MM/DD")
	private WebElement dogDOB;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Confirm\"]")
	private WebElement dogDobConfirmBtn;

	@AndroidFindBy(accessibility = "dogProfileImage")
	private WebElement dogProfileImagePicker;

	@AndroidFindBy(xpath = "(//android.widget.ImageView[@resource-id=\"com.dogpack:id/ivPicture\"])[1]")
	private WebElement firstImageInGallery;

	@AndroidFindBy(id = "com.dogpack:id/ps_tv_complete")
	private WebElement gallerySelectionDone;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Finish\"]")
	private WebElement finishBtn;

	// ============================================================
	// LOCATORS - Notification permission
	// ============================================================

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Yes, Notify Me\"]")
	private WebElement notifyMe;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_message")
	private WebElement permissionDialogMsg;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_button")
	private WebElement allowPermission;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_foreground_only_button")
	private WebElement allowForegroundBtn;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_one_time_button")
	private WebElement allowOneTimeBtn;

	// ============================================================
	// LOCATORS - Distance modal on Home
	// ============================================================

	@AndroidFindBy(accessibility = "feed-distance-submit")
	private WebElement homePageSubmit;

	// ============================================================
	// =====  SAFETY HELPERS (NEW - May 20)  ======================
	// ============================================================

	/**
	 * Ensure DogPack is in foreground. Reactivate if not.
	 * Especially critical for social auth flows because:
	 *   - Google account picker is a SYSTEM UI (com.google.android.gms)
	 *   - Facebook auth may briefly switch to FB app/browser
	 *   - After auth callback, focus should return to DogPack but
	 *     sometimes Android leaves the system UI on top
	 */
	private void ensureAppForeground() {
		try {
			String currentPkg = driver.getCurrentPackage();
			if (currentPkg == null || !currentPkg.contains("dogpack")) {
				System.out.println("[RECOVERY] App not in foreground (current: " + currentPkg + "), reactivating");
				driver.activateApp("com.dogpack");
				Thread.sleep(2500);
			}
		} catch (Exception e) {
			System.out.println("[WARN] ensureAppForeground: " + e.getMessage().split("\n")[0]);
		}
	}

	/**
	 * Press BACK then verify app stayed in foreground.
	 * SAFE replacement for direct pressKey(BACK).
	 */
	private void safeBackPress() {
		try {
			String beforePkg = driver.getCurrentPackage();
			driver.pressKey(new KeyEvent(AndroidKey.BACK));
			System.out.println("[ACTION] Pressed device Back");
			Thread.sleep(1000);

			String afterPkg = driver.getCurrentPackage();
			if (afterPkg == null || !afterPkg.contains("dogpack")) {
				if (beforePkg != null && beforePkg.contains("dogpack")) {
					System.out.println("[RECOVERY] BACK pushed app to: " + afterPkg + " - reactivating DogPack");
					driver.activateApp("com.dogpack");
					Thread.sleep(2500);
				}
			}
		} catch (Exception e) {
			System.out.println("[WARN] safeBackPress: " + e.getMessage().split("\n")[0]);
			try {
				driver.activateApp("com.dogpack");
				Thread.sleep(2000);
			} catch (Exception ignore) { /* */ }
		}
	}

	/**
	 * After auth provider (Google/Facebook) completes, wait for DogPack to
	 * come back to foreground. The auth flow temporarily switches to
	 * system UI / FB SDK - we must wait for control to return.
	 */
	private void waitForAppForegroundAfterAuth() {
		System.out.println("[FLOW] Waiting for DogPack to return to foreground after auth...");
		long deadline = System.currentTimeMillis() + 20000;  // 20s budget
		while (System.currentTimeMillis() < deadline) {
			try {
				String pkg = driver.getCurrentPackage();
				if (pkg != null && pkg.contains("dogpack")) {
					System.out.println("[FLOW] DogPack is foreground");
					return;
				}
			} catch (Exception ignore) { /* */ }
			try { Thread.sleep(500); } catch (InterruptedException ignore) { /* */ }
		}
		// Forced reactivate if still not back
		System.out.println("[RECOVERY] Auth-callback timeout - forcing DogPack to foreground");
		try {
			driver.activateApp("com.dogpack");
			Thread.sleep(3000);
		} catch (Exception e) {
			System.out.println("[WARN] Force activation failed: " + e.getMessage().split("\n")[0]);
		}
	}

	// ============================================================
	// =====  PUBLIC METHODS  =====================================
	// ============================================================

	public void SignupWithGmail() throws InterruptedException {
		System.out.println("\n========== GMAIL SIGNUP/LOGIN FLOW ==========");
		ensureAppForeground();

		wait.until(ExpectedConditions.visibilityOf(googleSocialLogin));
		wait.until(ExpectedConditions.elementToBeClickable(googleSocialLogin)).click();
		System.out.println("[ACTION] Clicked Google login button");

		// Account picker - shown by GoogleSignin.signOut() + signIn() (system UI)
		if (isElementPresentSafe(selectFirstGmailAccount, 12)) {
			try {
				wait.until(ExpectedConditions.elementToBeClickable(selectFirstGmailAccount)).click();
				System.out.println("[ACTION] Selected first Google account");
			} catch (Exception e) {
				System.out.println("[WARN] Account picker click failed: " + e.getMessage().split("\n")[0]);
			}
		} else {
			System.out.println("[INFO] No account picker shown - app may be auto-using last account");
		}

		// CRITICAL: Wait for DogPack to come back from system UI
		waitForAppForegroundAfterAuth();

		if (!isSessionAlive()) {
			Assert.fail("APP CRASH detected after Google sign-in. "
					+ "Possible app-side issue with GoogleSignIn or methodSocialSignup.");
		}

		completeSocialPostAuthFlow();

		System.out.println("========== GMAIL FLOW DONE ==========\n");
	}

	public void SignupWithFacebook() throws InterruptedException {
		System.out.println("\n========== FACEBOOK SIGNUP/LOGIN FLOW ==========");
		ensureAppForeground();

		wait.until(ExpectedConditions.visibilityOf(facebookSocialLogin));
		wait.until(ExpectedConditions.elementToBeClickable(facebookSocialLogin)).click();
		System.out.println("[ACTION] Clicked Facebook login button");

		clickFacebookContinueButton();

		// CRITICAL: Wait for DogPack to come back from FB SDK
		waitForAppForegroundAfterAuth();

		if (!isSessionAlive()) {
			Assert.fail("APP CRASH detected after Facebook sign-in. "
					+ "Possible app-side issue with FBSDK or methodSocialSignup.");
		}

		completeSocialPostAuthFlow();

		System.out.println("========== FACEBOOK FLOW DONE ==========\n");
	}

	// ============================================================
	// =====  PRIVATE HELPERS - Shared post-auth flow  ============
	// ============================================================

	private void completeSocialPostAuthFlow() throws InterruptedException {
		System.out.println("[FLOW] Determining post-auth path...");
		ensureAppForeground();

		WebDriverWait branchWait = new WebDriverWait(driver, Duration.ofSeconds(25));
		try {
			branchWait.until(ExpectedConditions.or(
					ExpectedConditions.visibilityOf(textOfDogProfileType),
					ExpectedConditions.visibilityOf(notifyMe),
					ExpectedConditions.visibilityOf(homePageSubmit),
					// NEW (May 20 fix): HOME screen indicators for returning users
					// who skip all onboarding and land directly on feed
					ExpectedConditions.presenceOfElementLocated(
							AppiumBy.accessibilityId("profile-view")),
					ExpectedConditions.presenceOfElementLocated(
							AppiumBy.accessibilityId("feed-challenge")),
					ExpectedConditions.presenceOfElementLocated(
							AppiumBy.accessibilityId("feed-support-chat"))));
		} catch (Exception e) {
			// One last try - reactivate and re-check
			ensureAppForeground();
			try {
				WebDriverWait retry = new WebDriverWait(driver, Duration.ofSeconds(10));
				retry.until(ExpectedConditions.or(
						ExpectedConditions.visibilityOf(textOfDogProfileType),
						ExpectedConditions.visibilityOf(notifyMe),
						ExpectedConditions.visibilityOf(homePageSubmit),
						ExpectedConditions.presenceOfElementLocated(
								AppiumBy.accessibilityId("profile-view")),
						ExpectedConditions.presenceOfElementLocated(
								AppiumBy.accessibilityId("feed-challenge"))));
			} catch (Exception ex) {
				Assert.fail("Post-auth: no expected screen appeared within 35s total");
			}
		}

		// PATH D: Already on HOME screen (NEW - returning user, fully onboarded)
		// This is the case where user has previously completed Gmail/FB signup,
		// granted all permissions, and set distance. They go DIRECTLY to feed.
		if (isOnHomeScreenAfterAuth()) {
			System.out.println("[PATH D] Already on HOME screen - returning user fully onboarded");
			System.out.println("[ASSERT PASS] Social login successful - user is on Home feed");
			return;
		}

		if (isDisplayedSafe(textOfDogProfileType)) {
			System.out.println("[PATH A] New user - full dog profile signup flow");
			handleNewUserSignupFlow();
			return;
		}

		if (isDisplayedSafe(notifyMe)) {
			System.out.println("[PATH B] Returning user - notify screen");
			handleNotificationFlow();
			handleDistanceModalIfPresent();
			return;
		}

		if (isDisplayedSafe(homePageSubmit)) {
			System.out.println("[PATH C] Returning user - direct to home");
			handleDistanceModalIfPresent();
			return;
		}

		System.out.println("[WARN] Post-auth flow branched but no path matched - user may already be on Home");
	}

	/**
	 * NEW (May 20 fix): Detect if app is on the HOME/feed screen.
	 * Used as 4th branch in post-auth flow for fully-onboarded returning users
	 * who skip UserType/NotifyMe/Distance screens entirely.
	 */
	private boolean isOnHomeScreenAfterAuth() {
		try {
			if (!driver.findElements(AppiumBy.accessibilityId("profile-view")).isEmpty()) {
				return true;
			}
			if (!driver.findElements(AppiumBy.accessibilityId("feed-challenge")).isEmpty()) {
				return true;
			}
			if (!driver.findElements(AppiumBy.accessibilityId("feed-support-chat")).isEmpty()) {
				return true;
			}
		} catch (Exception ignore) { /* */ }
		return false;
	}

	private void handleNewUserSignupFlow() throws InterruptedException {
		wait.until(ExpectedConditions.visibilityOf(textOfDogProfileType));
		wait.until(ExpectedConditions.elementToBeClickable(userTypeContinueBtn)).click();
		System.out.println("[ACTION] UserType continue clicked (Dog default)");

		wait.until(ExpectedConditions.visibilityOf(userNameField));
		String username = "testsocialuser" + System.currentTimeMillis() % 100000;
		WebElement field = wait.until(ExpectedConditions.elementToBeClickable(userNameField));
		field.clear();
		field.sendKeys(username);
		System.out.println("[INPUT] Username: " + username);

		wait.until(ExpectedConditions.elementToBeClickable(userNameScreenContinueBtn)).click();
		System.out.println("[ACTION] Username continue clicked");

		try {
			shortWait.until(ExpectedConditions.visibilityOf(usernameTakenPopup));
			System.out.println("[INFO] 'Username taken' popup - dismissing and retrying");
			wait.until(ExpectedConditions.elementToBeClickable(usernameTakenPopupCancelBtn)).click();

			String retryName = username + "x" + (System.currentTimeMillis() % 1000);
			WebElement field2 = wait.until(ExpectedConditions.elementToBeClickable(userNameField));
			field2.clear();
			System.out.println("[ACTION] Cleared field2");
			field2.sendKeys(retryName);
			wait.until(ExpectedConditions.elementToBeClickable(userNameScreenContinueBtn)).click();
			System.out.println("[ACTION] Retried with: " + retryName);
		} catch (Exception ignore) {
			// no popup - moved past
		}

		fillDogProfileForm();

		try {
			scrollToText("Finish");
		} catch (Exception ignore) { /* may already be visible */ }

		wait.until(ExpectedConditions.visibilityOf(finishBtn));
		wait.until(ExpectedConditions.elementToBeClickable(finishBtn)).click();
		System.out.println("[ACTION] Clicked Finish");

		handleNotificationFlow();
		handleDistanceModalIfPresent();
	}

	private void fillDogProfileForm() {
		System.out.println("[FLOW] Filling dog profile form");

		wait.until(ExpectedConditions.visibilityOf(dogNameField));
		WebElement nameEl = wait.until(ExpectedConditions.elementToBeClickable(dogNameField));
		nameEl.clear();
		nameEl.sendKeys("Entertainment");
		System.out.println("[INPUT] Dog name: Entertainment");

		wait.until(ExpectedConditions.elementToBeClickable(breedDropdown)).click();
		System.out.println("[ACTION] Opened breed dropdown");

		try {
			shortWait.until(ExpectedConditions.visibilityOf(searchBreedField));
			searchBreedField.sendKeys("Adopted");
			shortWait.until(ExpectedConditions.elementToBeClickable(breedName)).click();
			System.out.println("[INPUT] Selected breed: Adopted");
		} catch (Exception e) {
			System.out.println("[WARN] 'Adopted' not found, trying first breed");
			try {
				List<WebElement> items = driver.findElements(
						By.xpath("//android.widget.TextView[@resource-id and string-length(@text)>2]"));
				if (!items.isEmpty()) {
					items.get(0).click();
					System.out.println("[INPUT] Selected fallback breed");
				} else {
					// SAFE BACK - was driver.pressKey(BACK) directly
					safeBackPress();
				}
			} catch (Exception ex) {
				// SAFE BACK - was driver.pressKey(BACK) directly
				safeBackPress();
			}
		}

		wait.until(ExpectedConditions.elementToBeClickable(dogGender)).click();
		System.out.println("[INPUT] Gender: Female");

		wait.until(ExpectedConditions.elementToBeClickable(dogDOB)).click();
		wait.until(ExpectedConditions.elementToBeClickable(dogDobConfirmBtn)).click();
		System.out.println("[INPUT] DOB confirmed");

		try {
			scrollToText("Finish");
		} catch (Exception ignore) { /* */ }

		// Image upload - OPTIONAL
		try {
			if (isDisplayedSafe(dogProfileImagePicker)) {
				wait.until(ExpectedConditions.elementToBeClickable(dogProfileImagePicker)).click();
				System.out.println("[ACTION] Tapped dog profile image picker");

				handlePhotoPermissionsIfPresent();

				if (!isSessionAlive()) {
					System.out.println("[WARN] Session died after photo permission - skipping image");
					return;
				}

				try {
					shortWait.until(ExpectedConditions.elementToBeClickable(firstImageInGallery)).click();
					shortWait.until(ExpectedConditions.elementToBeClickable(gallerySelectionDone)).click();
					System.out.println("[INPUT] Profile image selected");
				} catch (Exception ex) {
					System.out.println("[WARN] Image gallery interaction failed");
					// Re-check foreground after gallery interaction
					ensureAppForeground();
				}
			}
		} catch (Exception e) {
			System.out.println("[INFO] Image upload step skipped: " + e.getMessage().split("\n")[0]);
		}
	}

	private void handleNotificationFlow() {
		try {
			shortWait.until(ExpectedConditions.elementToBeClickable(notifyMe)).click();
			System.out.println("[ACTION] Clicked 'Yes, Notify Me'");

			try {
				WebElement msg = shortWait.until(ExpectedConditions.visibilityOf(permissionDialogMsg));
				String text = msg.getText();
				System.out.println("[PERMISSION] Dialog: " + text);
				Assert.assertTrue(text.toLowerCase().contains("notification"),
						"Expected notification permission dialog, got: " + text);
				System.out.println("[ASSERT PASS] Notification permission dialog text verified");
			} catch (Exception e) {
				System.out.println("[INFO] Permission dialog message not captured");
			}

			if (clickIfDisplayed(allowPermission, "Allow")) return;
			if (clickIfDisplayed(allowForegroundBtn, "While Using App")) return;
			if (clickIfDisplayed(allowOneTimeBtn, "Allow Once")) return;
			System.out.println("[WARN] No Allow button matched");

		} catch (Exception e) {
			System.out.println("[INFO] NewNotification screen not shown - perm may already be granted");
		}
	}

	private void handleDistanceModalIfPresent() throws InterruptedException {
		Thread.sleep(1500);
		ensureAppForeground();
		try {
			WebDriverWait distWait = new WebDriverWait(driver, Duration.ofSeconds(15));
			distWait.until(ExpectedConditions.visibilityOf(homePageSubmit));
			distWait.until(ExpectedConditions.elementToBeClickable(homePageSubmit)).click();
			System.out.println("[ACTION] Clicked feed-distance-submit on Home");
			System.out.println("[ASSERT PASS] User logged in successfully");
		} catch (Exception e) {
			System.out.println("[INFO] Distance modal not shown - user may already be on feed");
		}
	}

	private void clickFacebookContinueButton() {
		try {
			List<WebElement> continueButtons = driver.findElements(By.xpath(
					"//android.widget.Button[contains(@text, 'Continue') "
					+ "or contains(@text, 'जारी')]"));

			if (!continueButtons.isEmpty()) {
				wait.until(ExpectedConditions.elementToBeClickable(continueButtons.get(0))).click();
				System.out.println("[ACTION] Clicked Facebook 'Continue' button");
				return;
			}

			WebElement anyButton = wait.until(ExpectedConditions.elementToBeClickable(
					By.className("android.widget.Button")));
			anyButton.click();
			System.out.println("[ACTION] Clicked first available Button (FB fallback)");
		} catch (Exception e) {
			System.out.println("[WARN] Facebook Continue not found: " + e.getMessage().split("\n")[0]);
			// SAFE BACK - was driver.pressKey(BACK) directly
			safeBackPress();
		}
	}

	private void handlePhotoPermissionsIfPresent() {
		try {
			java.util.Map<String, Object> args = new java.util.HashMap<>();
			args.put("appPackage", "com.dogpack");
			args.put("action", "grant");
			args.put("permissions", new String[] {
					"android.permission.READ_MEDIA_IMAGES",
					"android.permission.READ_MEDIA_VIDEO",
					"android.permission.READ_MEDIA_VISUAL_USER_SELECTED",
					"android.permission.READ_EXTERNAL_STORAGE"
			});
			args.put("target", "pm");
			driver.executeScript("mobile: changePermissions", args);
			System.out.println("[FLOW] mobile:changePermissions - photo permissions granted");
			return;
		} catch (Exception ignore) { /* fall through */ }

		WebElement permBtn = findFirstClickable(8, new By[] {
				AppiumBy.id("com.android.permissioncontroller:id/permission_allow_one_time_button"),
				AppiumBy.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button"),
				AppiumBy.androidUIAutomator("new UiSelector().textMatches(\"(?i)Allow only this time\")"),
				AppiumBy.androidUIAutomator("new UiSelector().textMatches(\"(?i)While using the app\")"),
				AppiumBy.id("com.android.permissioncontroller:id/permission_allow_all_button"),
				AppiumBy.androidUIAutomator("new UiSelector().textMatches(\"(?i)Allow all\")"),
				AppiumBy.androidUIAutomator("new UiSelector().textMatches(\"(?i)Allow\")")
		});

		if (permBtn == null) {
			System.out.println("[FLOW] No photo permission popup (likely pre-granted)");
			return;
		}

		try {
			String btnText = "";
			try { btnText = permBtn.getText(); } catch (Exception ignore) { /* */ }
			permBtn.click();
			System.out.println("[FLOW] Photo permission - clicked: '"
					+ (btnText.isEmpty() ? "[resource-id]" : btnText) + "'");
		} catch (Exception e) {
			System.out.println("[WARN] Permission button click failed");
		}
	}

	// ============================================================
	// =====  GENERIC HELPERS  ====================================
	// ============================================================

	private boolean isElementPresentSafe(WebElement el, int timeoutSec) {
		try {
			WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
			w.until(ExpectedConditions.visibilityOf(el));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isDisplayedSafe(WebElement el) {
		try {
			return el.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	private boolean clickIfDisplayed(WebElement el, String label) {
		try {
			if (el.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(el)).click();
				System.out.println("[ACTION] Clicked: " + label);
				return true;
			}
		} catch (Exception ignore) { /* */ }
		return false;
	}

	private boolean isSessionAlive() {
		try {
			driver.getCurrentPackage();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private WebElement findFirstClickable(int totalTimeoutSec, By[] candidates) {
		long deadline = System.currentTimeMillis() + (totalTimeoutSec * 1000L);
		while (System.currentTimeMillis() < deadline) {
			for (By locator : candidates) {
				try {
					List<WebElement> els = driver.findElements(locator);
					if (!els.isEmpty()) {
						WebElement el = els.get(0);
						if (el.isDisplayed() && el.isEnabled()) {
							return el;
						}
					}
				} catch (Exception ignore) { /* */ }
			}
			try { Thread.sleep(300); } catch (InterruptedException ignore) { /* */ }
		}
		return null;
	}
}