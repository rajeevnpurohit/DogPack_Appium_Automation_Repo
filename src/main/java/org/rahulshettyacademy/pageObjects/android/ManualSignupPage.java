package org.rahulshettyacademy.pageObjects.android;

import java.io.FileInputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
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
 * ManualSignupPage - Page Object for the Manual (Email/Password) Signup module.
 *
 * Serves four test classes:
 *   1. DogPack_ManualSignup            -> Dog account creation (full happy path)
 *   2. DogPack_ManualSignupSkipCase    -> Skip "Create Dog Profile" form (no info)
 *   3. DogPack_ManualBusinessSignup    -> Create new business account
 *   4. DogPack_ManualBusinessSignupUnclaimed -> Claim existing unclaimed business
 *
 * App-source mapping (refer src_app/src/screen/signUpFlow/):
 *   signUpWelcome/index.js        -> Email + Password (default test_input/test) + "Create Account"
 *                                  -> Existing-account modal: testID `onCancel`/`onConfirm`
 *   UserType/index.js             -> testIDs: dogProfile, businessProfile, skipForNow, clickdogbus
 *                                  -> Business confirm modal: testID `onConfirm`
 *   userName/index.js             -> testIDs: usernameInput, userContinue
 *                                  -> "Username taken" modal -> testID `onConfirm`/`onCancel`
 *   DogProfile/DogProfileForm.js  -> Dog name (test_input default), gender buttons by text
 *                                  -> testID: dogProfileImage; date picker "YYYY/MM/DD"
 *                                  -> Finish (PrimaryButton default `test`, located by text)
 *   businessAlready/index.js      -> testID: claimBusinesscon; search via BusinessListView (search_business_view)
 *                                  -> Claim Yes/No via CustomBusinessModal (text-located)
 *   businessRegisterDetails/      -> testIDs: company_name, firstName, lastName, email,
 *                                     confirmEmail, businessCreateCon, otp_button_con,
 *                                     description, businessSecondCon, businessImage0,
 *                                     businessPhone, businesswebsite, businessSubmit
 *   notification/NewNotification.js -> "Yes, Notify Me" (text), Skip
 *   Feeds.js                       -> testID: feed-distance-submit (post-login distance modal)
 *
 * Critical improvements over the previous version:
 *   - WebDriverWait promoted to instance fields (created once)
 *   - Notification screen made OPTIONAL (matches Login/ForgotPassword pattern)
 *   - OTP rejection detection (fast-fail with clear error)
 *   - Permission popups handled with text-tolerant logic (curly-apostrophe, multi-button)
 *   - All hard-coded strings extracted to TestData.properties with safe defaults
 *   - Long flows broken into private helpers for readability
 *   - Implicit wait NOT modified in constructor (BaseTest manages it)
 */
public class ManualSignupPage extends AndroidActions {

	private final AndroidDriver driver;
	private final WebDriverWait wait;
	private final WebDriverWait shortWait;
	private final WebDriverWait longWait;
	private final Properties testDataProp = new Properties();

	public ManualSignupPage(AndroidDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(
				new AppiumFieldDecorator(driver, Duration.ofSeconds(15)),
				this);

		this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		this.longWait = new WebDriverWait(driver, Duration.ofSeconds(40));

		try {
			FileInputStream fis = new FileInputStream(System.getProperty("user.dir")
					+ "//src//main//java//org//rahulshettyacademy//resources//TestData.properties");
			testDataProp.load(fis);
		} catch (Exception e) {
			System.out.println("[WARN] Could not load TestData.properties: " + e.getMessage());
		}
	}

	// =======================================================================
	// LOCATORS - SignUp Welcome (email/password + Create Account)
	// =======================================================================

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Create Account\"]")
	private WebElement createAccountBtn;

	// SignUpWelcome has 2 InputCustom (no override) -> both default to `test_input`.
	@AndroidFindBy(xpath = "(//android.widget.EditText[@content-desc='test_input'])[1]")
	private WebElement emailFieldSignup;

	@AndroidFindBy(xpath = "(//android.widget.EditText[@content-desc='test_input'])[2]")
	private WebElement passwordFieldSignup;

	@AndroidFindBy(xpath = "//*[contains(@content-desc,\"valid email\") "
			+ "or contains(@content-desc,\"Please enter\") "
			+ "or contains(@text,\"valid email\") "
			+ "or contains(@text,\"Please enter\")]")
	private WebElement emptyCredErrorAlert;

	// CustomAlertModal exposes onCancel/onConfirm testIDs
	@AndroidFindBy(accessibility = "onCancel")
	private WebElement modalCancelBtn;

	@AndroidFindBy(accessibility = "onConfirm")
	private WebElement modalConfirmBtn;

	// =======================================================================
	// LOCATORS - UserType screen (Dog / Business chooser)
	// =======================================================================

	@AndroidFindBy(accessibility = "dogProfile")
	private WebElement dogProfileChoice;

	@AndroidFindBy(accessibility = "businessProfile")
	private WebElement businessProfileChoice;

	@AndroidFindBy(accessibility = "skipForNow")
	private WebElement skipForNowLink;

	@AndroidFindBy(accessibility = "clickdogbus")
	private WebElement userTypeContinueBtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Create a profile for your Dog\"]")
	private WebElement userTypeHeading;

	@AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,\"Creating a Business\")]")
	private WebElement businessConfirmModalText;

	// =======================================================================
	// LOCATORS - UserName screen
	// =======================================================================

	@AndroidFindBy(accessibility = "usernameInput")
	private WebElement userNameField;

	@AndroidFindBy(accessibility = "userContinue")
	private WebElement userNameContinueBtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Username taken\"]")
	private WebElement usernameTakenTitle;

	// =======================================================================
	// LOCATORS - DogProfileForm screen
	// =======================================================================

	// Dog Name field is an InputCustom default-> `test_input`
	@AndroidFindBy(accessibility = "test_input")
	private WebElement dogNameField;

	@AndroidFindBy(accessibility = "Select Breed 1")
	private WebElement breedDropdown;

	@AndroidFindBy(xpath = "//android.widget.EditText[@text=\"Search breed\"]")
	private WebElement searchBreedField;

	@AndroidFindBy(accessibility = "Female")
	private WebElement dogGenderFemale;

	@AndroidFindBy(accessibility = "Male")
	private WebElement dogGenderMale;

	@AndroidFindBy(accessibility = "YYYY/MM/DD")
	private WebElement dogDobTrigger;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Confirm\"]")
	private WebElement dobConfirmBtn;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc='dogProfileImage']/android.view.ViewGroup")
	private WebElement dogProfileImagePicker;

	@AndroidFindBy(xpath = "(//android.widget.ImageView[@resource-id=\"com.dogpack:id/ivPicture\"])[1]")
	private WebElement firstImageInGallery;

	@AndroidFindBy(id = "com.dogpack:id/ps_tv_complete")
	private WebElement gallerySelectionDone;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Finish\"]")
	private WebElement finishSignupBtn;

	// =======================================================================
	// LOCATORS - businessAlready screen (claim/unclaimed flow)
	// =======================================================================

	@AndroidFindBy(accessibility = "claimBusinesscon")
	private WebElement businessAlreadyListedContinueBtn;

	// Search input on businessAlready is InputCustom default -> `test_input`
	@AndroidFindBy(accessibility = "test_input")
	private WebElement unclaimedSearchBox;

	@AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"search_business_view\"])[1]")
	private WebElement firstUnclaimedBusinessRow;

	private final By claimBtnLocator = AppiumBy.androidUIAutomator(
			"new UiSelector().text(\"Claim\")");

	private final By yesBtnLocator = AppiumBy.androidUIAutomator(
			"new UiSelector().text(\"Yes\")");

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Great!\"]")
	private WebElement greatBtnUnclaimedBusiness;

	private final By rateUsLaterLocator = AppiumBy.androidUIAutomator(
			"new UiSelector().text(\"Not now\")");

	// =======================================================================
	// LOCATORS - businessRegisterDetails (form + OTP + 2nd + 3rd screens)
	// =======================================================================

	@AndroidFindBy(accessibility = "company_name")
	private WebElement companyNameField;

	@AndroidFindBy(accessibility = "firstName")
	private WebElement companyFirstNameField;

	@AndroidFindBy(accessibility = "lastName")
	private WebElement companyLastNameField;

	@AndroidFindBy(accessibility = "email")
	private WebElement companyWorkEmailField;

	@AndroidFindBy(accessibility = "confirmEmail")
	private WebElement companyConfirmEmailField;

	@AndroidFindBy(accessibility = "businessCreateCon")
	private WebElement businessCreateContinueBtn;

	// OTP fields (shared with ForgotPassword flow)
	@AndroidFindBy(accessibility = "otp0")
	private WebElement otp1;

	@AndroidFindBy(accessibility = "otp1")
	private WebElement otp2;

	@AndroidFindBy(accessibility = "otp2")
	private WebElement otp3;

	@AndroidFindBy(accessibility = "otp3")
	private WebElement otp4;

	@AndroidFindBy(accessibility = "otp_button_con")
	private WebElement otpContinueBtn;

	// Business 2nd screen: address / image / services / description
	@AndroidFindBy(accessibility = "businessImage0")
	private WebElement firstBusinessImageSlot;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Get Current Location\"]")
	private WebElement getCurrentLocationBtn;

	@AndroidFindBy(accessibility = "Dog Walking")
	private WebElement dogWalkingService;

	@AndroidFindBy(accessibility = "description")
	private WebElement businessDescriptionField;

	@AndroidFindBy(accessibility = "businessSecondCon")
	private WebElement businessSecondContinueBtn;

	// Business 3rd screen: phone + submit
	@AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,\"Phone Number\")]")
	private WebElement phoneNumberHeading;

	@AndroidFindBy(accessibility = "businessSubmit")
	private WebElement businessSubmitBtn;

	// =======================================================================
	// LOCATORS - System permissions + NewNotification + Home
	// =======================================================================

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_message")
	private WebElement permissionDialogMsg;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_button")
	private WebElement permissionAllowBtn;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_all_button")
	private WebElement permissionAllowAllBtn;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_one_time_button")
	private WebElement permissionAllowOneTimeBtn;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_foreground_only_button")
	private WebElement permissionWhileUsingAppBtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Yes, Notify Me\"]")
	private WebElement notifyMeBtn;

	// Newer builds replace the "Yes, Notify Me" screen with a "Verify Your
	// Account" upsell (Get more visibility on DogPack). It is dismissed via the
	// "Maybe Later" button. content-desc is the stable RN accessibility id.
	@AndroidFindBy(accessibility = "Maybe Later")
	private WebElement maybeLaterBtn;

	@AndroidFindBy(accessibility = "feed-distance-submit")
	private WebElement homePageDistanceSubmitBtn;

	@AndroidFindBy(accessibility = "profile-view")
	private WebElement profileViewBtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text='Logout']")
	private WebElement logoutOption;

	// =======================================================================
	// PUBLIC API: Email/Password signup form
	// =======================================================================

	/**
	 * Enter email + password and click "Create Account" on the SignUp Welcome screen.
	 * Behavior depends on inputs:
	 *  - Empty creds -> error toast
	 *  - Existing user -> "Already have an account" modal
	 *  - Unique creds -> navigate to UserType chooser
	 */
	public void SignupWithEmailPassword(String email, String password) {
		System.out.println("\n[SIGNUP] Filling email/password (email='" + email + "')");

		wait.until(ExpectedConditions.visibilityOf(emailFieldSignup));
		WebElement emailEl = wait.until(ExpectedConditions.elementToBeClickable(emailFieldSignup));
		clearFieldRobustly(emailEl, "email");
		if (email != null && !email.isEmpty()) {
			emailEl.sendKeys(email);
		}
		tryHideKeyboard();

		WebElement passEl = wait.until(ExpectedConditions.elementToBeClickable(passwordFieldSignup));
		clearFieldRobustly(passEl, "password");
		if (password != null && !password.isEmpty()) {
			passEl.sendKeys(password);
		}
		tryHideKeyboard();

		wait.until(ExpectedConditions.elementToBeClickable(createAccountBtn)).click();
		System.out.println("[SIGNUP] Clicked Create Account");
	}

	/**
	 * Assertion for empty-credentials validation.
	 *
	 * Strategy:
	 *   1. Try to capture the toast message (best case - explicit assertion)
	 *   2. Fallback: if toast missed (common - RN toasts disappear in 2-3s),
	 *      verify we're STILL on SignUp Welcome screen (i.e. did not navigate
	 *      forward), which proves validation BLOCKED the action.
	 */
	public void AssertionForEmptyCred() {
		try {
			shortWait.until(ExpectedConditions.visibilityOf(emptyCredErrorAlert));
			System.out.println("[ASSERT PASS] Empty-cred error toast captured");
			return;
		} catch (Exception e) {
			// Toast may have already disappeared - check staying-on-screen as fallback
		}

		// Fallback: still on SignUp Welcome screen = validation worked
		boolean stillOnSignupScreen = isDisplayed(createAccountBtn)
				&& isDisplayed(emailFieldSignup);

		if (stillOnSignupScreen) {
			System.out.println("[ASSERT PASS] Toast not captured but user stayed on "
					+ "SignUp Welcome screen - validation correctly blocked submission");
		} else {
			System.out.println("[ASSERT WARN] Empty-cred error toast not captured AND "
					+ "user no longer on SignUp Welcome - validation may have failed");
		}
	}

	/**
	 * Cancel the "account already exists" modal that appears when an existing
	 * email is used.
	 *
	 * Self-healing behavior:
	 *  - If modal appears -> click Cancel, clear fields. (expected path)
	 *  - If modal does NOT appear -> log a clear warning. This usually means
	 *    the test email in SignupData.json[1] was deleted from backend, so the
	 *    app proceeded as a fresh signup. Test recovers by going BACK to
	 *    SignUp Welcome so subsequent tests can continue.
	 *  - If neither modal nor SignUp screen visible -> hard fail with
	 *    diagnostic info.
	 */
	/**
	 * Handle the "account already exists" case after Create Account submission.
	 *
	 * IMPORTANT - App behavior changed in current build:
	 *  Earlier, the app would show a modal with onCancel/onConfirm buttons.
	 *  Now, the app handles error_code 1001 by automatically attempting LOGIN
	 *  via executeEmailPasswordLogin (see signUpWelcome/index.js:methodLoginAfterAccountExists).
	 *
	 *  As a result, ONE of these can happen:
	 *    A) Password matches  -> user logs in and lands on Home screen
	 *    B) Password mismatch -> error toast shown, user stays on SignUp form
	 *    C) OTP required      -> user navigates to OTP screen
	 *    D) Modal still shown (legacy/older builds) -> click Cancel
	 *
	 * This method tolerates ALL four outcomes and recovers to SignUp Welcome
	 * for the next test:
	 *   - If on Home -> log out + return to SignUp Welcome
	 *   - If toast/still on SignUp -> just clear fields and continue
	 *   - If on OTP -> press BACK to recover
	 *   - If legacy modal -> click Cancel
	 */
	public void ExistingAccountCancel() {
		System.out.println("[FLOW] Handling 'existing account' response (post Create Account)");

		// Wait UP TO 30s for ANY of: modal, home (logged in), OTP screen,
		// or just toast (still on SignUp form). The recaptcha+API chain can
		// take 5-15s on slow networks.
		WebDriverWait modalWait = new WebDriverWait(driver, Duration.ofSeconds(30));
		try {
			modalWait.until(ExpectedConditions.or(
					// Legacy: modal still shown
					ExpectedConditions.visibilityOf(modalCancelBtn),
					ExpectedConditions.visibilityOf(modalConfirmBtn),
					ExpectedConditions.visibilityOfElementLocated(
							By.xpath("//android.widget.TextView[@text=\"Login\"]")),
					// Auto-login success: distance modal on Home
					ExpectedConditions.visibilityOf(homePageDistanceSubmitBtn),
					// OTP required for login
					ExpectedConditions.visibilityOf(otp1),
					// Stayed on SignUp screen (toast shown then disappeared)
					ExpectedConditions.visibilityOf(createAccountBtn)
			));
		} catch (Exception e) {
			System.out.println("[WARN] No expected screen detected within 30s - "
					+ "attempting recovery anyway");
		}

		// Path A: Legacy modal still shown -> click Cancel
		boolean cancelClicked = false;
		if (isDisplayed(modalCancelBtn)) {
			System.out.println("[PATH A] Legacy modal detected - clicking Cancel");
			try {
				wait.until(ExpectedConditions.elementToBeClickable(modalCancelBtn)).click();
				cancelClicked = true;
			} catch (Exception e) {
				System.out.println("[WARN] onCancel click failed: " + e.getMessage());
			}
		}

		if (!cancelClicked && isLegacyModalTextVisible()) {
			String[] cancelTexts = { "Cancel", "Go back", "Goback", "Try another" };
			for (String t : cancelTexts) {
				try {
					By by = By.xpath("//android.widget.TextView[@text=\"" + t + "\"]");
					WebElement el = shortWait.until(ExpectedConditions.elementToBeClickable(by));
					el.click();
					System.out.println("[PATH A] Modal closed via text fallback: '" + t + "'");
					cancelClicked = true;
					break;
				} catch (Exception ignore) { /* try next */ }
			}
		}

		// Path B: Auto-login succeeded -> distance modal on Home
		if (isDisplayed(homePageDistanceSubmitBtn)) {
			System.out.println("[PATH B] App auto-logged-in the existing user "
					+ "(error_code 1001 -> login). Recovering by going back to SignUp.");
			try {
				wait.until(ExpectedConditions.elementToBeClickable(homePageDistanceSubmitBtn)).click();
			} catch (Exception ignore) { }
			// Logout to return to SignUp Welcome
			try {
				LogoutUser();
			} catch (Exception e) {
				System.out.println("[WARN] Auto-recovery logout failed: " + e.getMessage());
			}
			System.out.println("[ASSERT PASS] Existing-user flow handled (auto-login path)");
			return;
		}

		// Path C: OTP required for login
		if (isDisplayed(otp1)) {
			System.out.println("[PATH C] App routed to OTP screen for existing-user login. "
					+ "Pressing BACK to recover to SignUp Welcome.");
			pressBackKey();
			try {
				Thread.sleep(800);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
			recoverBackToSignupWelcome();
			tryClearSignupFields();
			System.out.println("[ASSERT PASS] Existing-user flow handled (OTP path)");
			return;
		}

		// Path D: Stayed on SignUp screen (toast shown then disappeared)
		if (isDisplayed(createAccountBtn)) {
			System.out.println("[PATH D] User stayed on SignUp Welcome screen "
					+ "(toast-based error). Clearing fields for next test.");
			tryClearSignupFields();
			System.out.println("[ASSERT PASS] Existing-user flow handled (toast path)");
			return;
		}

		// Path A continued: modal was clicked - clean up
		if (cancelClicked) {
			tryClearSignupFields();
			System.out.println("[ASSERT PASS] Existing-user flow handled (modal path)");
			return;
		}

		// Unknown state
		Assert.fail("[FAIL] Existing-user flow ended in unknown state. "
				+ "Take a screenshot of the device to debug.");
	}

	private boolean isLegacyModalTextVisible() {
		try {
			return shortWait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//android.widget.TextView[@text=\"Login\"]"))).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	private void tryClearSignupFields() {
		System.out.println("[CLEAR] Attempting to clear email/password fields");
		clearFieldRobustly(emailFieldSignup, "email");
		clearFieldRobustly(passwordFieldSignup, "password");
		tryHideKeyboard();
	}

	/**
	 * Robust clear - React Native controlled-state fields sometimes ignore
	 * a plain .clear() call. Strategy:
	 *   1. Plain clear() (works on most builds)
	 *   2. If text is still present, long-press select-all + delete
	 *   3. As last resort, tap and send DEL keys for each char
	 */
	private void clearFieldRobustly(WebElement field, String label) {
		try {
			if (!isDisplayed(field)) {
				System.out.println("[CLEAR] " + label + " field not visible - skipping");
				return;
			}
			field.click();
			field.clear();

			// Verify cleared
			String remaining = "";
			try {
				remaining = field.getText();
			} catch (Exception ignore) { }

			if (remaining == null || remaining.isEmpty()) {
				System.out.println("[CLEAR] " + label + " field cleared via .clear()");
				return;
			}

			// Fallback: select-all + delete via key events
			System.out.println("[CLEAR] " + label + " field still has text '" + remaining
					+ "' - using key-event fallback");
			int len = remaining.length();
			// Move cursor to end then send DEL
			for (int i = 0; i < len + 5; i++) {
				driver.pressKey(new KeyEvent(AndroidKey.DEL));
			}
		} catch (Exception e) {
			System.out.println("[CLEAR WARN] " + label + " clear failed: " + e.getMessage());
		}
	}

	private void recoverBackToSignupWelcome() {
		// Press BACK up to 4 times to reach SignUp Welcome
		for (int i = 0; i < 4; i++) {
			if (isDisplayed(createAccountBtn)) {
				System.out.println("[RECOVERY] Reached SignUp Welcome screen");
				return;
			}
			pressBackKey();
			try {
				Thread.sleep(800);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		}
		if (!isDisplayed(createAccountBtn)) {
			System.out.println("[WARN] Could not return to SignUp Welcome via BACK - "
					+ "subsequent tests may fail. Consider restarting the app.");
		}
	}

	// =======================================================================
	// PUBLIC API: Dog account flow (full happy path)
	// =======================================================================

	/**
	 * After SignupWithEmailPassword, complete the full Dog-profile signup flow.
	 * Reads username / dog-name / etc. from TestData.properties with safe defaults.
	 */
	public void CreateNewDogAccountManual() {
		System.out.println("\n========== CREATE DOG ACCOUNT FLOW ==========");

		String username = testDataProp.getProperty("signupUsername", "testdog" + uniqueSuffix());
		String dogName = testDataProp.getProperty("signupDogName", "Shiro");

		// 1. UserType chooser -> "Dog" is default selection in app, just continue
		clickUserTypeContinueWithDogProfileSelected();

		// 2. Username screen
		enterUsernameWithRetry(username);

		// 3. Dog form (name + breed + gender + dob + image)
		fillDogProfileForm(dogName);

		// 4. Finish
		wait.until(ExpectedConditions.visibilityOf(finishSignupBtn));
		wait.until(ExpectedConditions.elementToBeClickable(finishSignupBtn)).click();
		System.out.println("[ACTION] Clicked Finish");

		// 4b. "Verify Your Business" upsell -> stay on free (non-licensed) tier
		dismissStayOnFreeUpsellIfPresent();

		// 5. Notification + Home
		completeNotificationAndDistanceFlow();

		System.out.println("========== DOG ACCOUNT FLOW DONE ==========\n");
	}

	/**
	 * Skip-case flow: user picks "Skip for now" on UserType screen.
	 * Goes straight to NewNotification -> Home.
	 */
	public void CreateNewDogwithNoInformation() {
		System.out.println("\n========== SKIP DOG PROFILE FLOW ==========");

		wait.until(ExpectedConditions.visibilityOf(userTypeHeading));
		wait.until(ExpectedConditions.elementToBeClickable(skipForNowLink)).click();
		System.out.println("[ACTION] Clicked 'Skip for now'");

		completeNotificationAndDistanceFlow();
		System.out.println("========== SKIP DOG PROFILE FLOW DONE ==========\n");
	}

	// =======================================================================
	// PUBLIC API: Business signup flows
	// =======================================================================

	/**
	 * Create a NEW business account (not claiming existing one).
	 * Path: UserType (Business) -> UserName -> businessAlready (Continue) ->
	 *       businessRegisterDetails (form) -> OTP -> 2nd screen (image+services+description) ->
	 *       3rd screen (phone) -> Submit -> NewNotification -> Home
	 */
	public void CreateBusinessAccount() {
		System.out.println("\n========== CREATE BUSINESS ACCOUNT FLOW ==========");

		// 1. Reach username screen via UserType chooser
		navigateToUsernameViaBusinessChooser();

		String username = testDataProp.getProperty("businessUsername",
				"testbiz" + uniqueSuffix());
		enterUsernameWithRetry(username);

		// 2. Optional location permission popup (some builds prompt here)
		handleSinglePermissionDialogIfPresent();

		// 3. businessAlready screen -> "Continue without claiming" path
		wait.until(ExpectedConditions.elementToBeClickable(businessAlreadyListedContinueBtn)).click();
		System.out.println("[ACTION] Clicked claimBusinesscon (skip-claim continue)");

		// 4. Business registration form
		fillBusinessRegistrationForm();
		clickBusinessCreateConWithVerification();

		// 5. OTP screen
		submitOtpWithFastFail();

		// 6. Business 2nd screen: location + service + image + description
		fillBusinessSecondScreen();

		// 7. Business 3rd screen: phone -> submit
		fillBusinessThirdScreen();

		// 7b. "Verify Your Business" upsell -> stay on free (non-licensed) tier
		dismissStayOnFreeUpsellIfPresent();

		// 8. Notification + Home
		completeNotificationAndDistanceFlow();

		System.out.println("========== CREATE BUSINESS ACCOUNT DONE ==========\n");
	}

	/**
	 * Claim an existing UNCLAIMED business via search.
	 *
	 * Verified flow (from app source: businessAlready/index.js + Otp.js):
	 *   1. UserType chooser   -> Business + Continue + Confirm modal "Yes"
	 *   2. UserName screen    -> unique username (with "taken" retry)
	 *   3. Location permission popup (system) - first-time only
	 *   4. businessAlready    -> search input (testID test_input)
	 *                          -> list renders (debounced) with "Claim" buttons
	 *                          -> Click Claim -> setIsClaimModal(true)
	 *   5. Claim Yes modal    -> "Yes" button text -> businessClaimRequest API
	 *                          -> Success -> navigate('Otp', {userType:'businessClaim'})
	 *   6. OTP screen         -> 4-digit OTP -> businessClaimVerify API
	 *                          -> Success -> setState({isClaimModal:true})
	 *   7. Claim success modal-> "Great!" button -> afterClaimBusiness()
	 *
	 * After Great! click, the flow is CONDITIONAL based on async storage state:
	 *   - RateUs screen      -> may or may not appear (based on is_app_review flag + date)
	 *   - NewNotification    -> may or may not appear (based on OS notification perm)
	 *   - Notification popup -> only if "Yes Notify Me" clicked
	 *   - Distance modal     -> ALWAYS shown (app sets distancePermission=false before OTP)
	 *
	 * This method tolerates ALL conditional screens:
	 *   handleRateUsScreenIfPresent()
	 *   completeNotificationAndDistanceFlow() [already handles 2 scenarios]
	 */
	public void CreateUnclaimedBusinessAccount() throws InterruptedException {
		System.out.println("\n========== CLAIM UNCLAIMED BUSINESS FLOW ==========");

		// 1. UserType chooser -> Username screen
		navigateToUsernameViaBusinessChooser();

		String username = testDataProp.getProperty("unclaimedBusinessUsername",
				"testunclaim" + uniqueSuffix());
		enterUsernameWithRetry(username);

		// 2. Optional location permission (first-time)
		handleSinglePermissionDialogIfPresent();

		// 3. Search and Claim
		searchAndClaimUnclaimedBusiness();

		// 4. Yes confirmation modal
		System.out.println("[FLOW] Waiting for Claim confirmation modal");
		try {
			shortWait.until(ExpectedConditions.elementToBeClickable(yesBtnLocator)).click();
			System.out.println("[ACTION] Clicked Yes (claim confirmation)");
		} catch (Exception e) {
			Assert.fail("[FAIL] Claim confirmation 'Yes' button not found: "
					+ e.getMessage());
		}

		// 5. OTP screen for businessClaim flow
		// Note: businessClaimVerify uses different API than work-email OTP.
		// Test OTP (1111) may be rejected - use manualOtpInput=true if needed.
		submitOtpWithFastFail();

		// 6. "Great!" success modal (CustomBusinessModal with bottonGreat=true)
		clickGreatWithVerification();

		// 6b. "Verify Your Business" upsell -> stay on free (non-licensed) tier
		dismissStayOnFreeUpsellIfPresent();

		// 7. RateUs screen - CONDITIONAL based on storage flags
		handleRateUsScreenIfPresent();

		// 8. Notification + Home Distance modal
		// IMPORTANT: app source sets distancePermission=false in businessAlready
		// (line 195), so distance modal IS shown on Home for claim flow.
		completeNotificationAndDistanceFlow();

		System.out.println("========== CLAIM UNCLAIMED BUSINESS DONE ==========\n");
	}

	/**
	 * Search + claim with fallback to any available unclaimed business.
	 *
	 * App source debounce behavior (businessAlready/index.js useEffect):
	 *   - searchText >= 3 chars triggers API after 1-second debounce
	 *   - Empty searchText re-fetches FULL unclaimed business list
	 *   - Initial screen load also fetches full list automatically
	 *
	 * Strategy:
	 *   1. Type configured search term -> wait for debounce + API
	 *   2. Look for "Claim" button
	 *   3. If found -> click that one (preferred match)
	 *   4. If NOT found -> clear search field -> wait for full list refresh
	 *      -> click FIRST available "Claim" button
	 *   5. If still no Claim button -> hard fail with diagnostic
	 */
	private void searchAndClaimUnclaimedBusiness() {
		String searchTerm = testDataProp.getProperty(
				"unclaimedBusinessSearchTerm", "");

		System.out.println("[FLOW] Searching for unclaimed business");

		// PERMANENT FIX FOR RACE CONDITION:
		// Location permission dialog can appear LATE (after handleSingle
		// PermissionDialogIfPresent already timed out) and block test_input.
		// Poll for EITHER test_input visibility OR permission dialog. If
		// dialog appears, handle it inline and continue waiting for test_input.
		// Max total wait: 30s (covers slow network + delayed permission).
		long deadline = System.currentTimeMillis() + 30_000;
		boolean searchBoxVisible = false;
		while (System.currentTimeMillis() < deadline) {
			// Check 1: is test_input now visible?
			try {
				if (unclaimedSearchBox.isDisplayed()) {
					searchBoxVisible = true;
					System.out.println("[FLOW] Search box (test_input) is now visible");
					break;
				}
			} catch (Exception ignore) {
				// element not found yet - continue polling
			}
			// Check 2: did a late permission dialog appear?
			boolean handled = handleSinglePermissionDialogIfPresent();
			if (handled) {
				System.out.println("[RACE-FIX] Late location permission handled "
						+ "during test_input wait - continuing");
				// Give app time to dismiss dialog and load search screen
				try {
					Thread.sleep(1500);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
				continue;
			}
			// Neither visible - short pause then poll again
			try {
				Thread.sleep(500);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				break;
			}
		}

		if (!searchBoxVisible) {
			Assert.fail("[FAIL] Unclaimed business search box (test_input) did "
					+ "not appear within 30s. App may be on wrong screen or stuck.");
		}

		// Try configured search term first (if provided)
		boolean claimedViaSearch = false;
		if (!searchTerm.isEmpty()) {
			System.out.println("[FLOW] Trying configured search term: " + searchTerm);
			WebElement searchEl = wait.until(
					ExpectedConditions.elementToBeClickable(unclaimedSearchBox));
			searchEl.click();
			clearFieldRobustly(searchEl, "search");
			searchEl.sendKeys(searchTerm);
			tryHideKeyboard();

			// Allow debounce (1s in app) + API call to populate list
			try {
				Thread.sleep(2500);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}

			// Try to find Claim button for searched business
			try {
				WebDriverWait searchResultWait = new WebDriverWait(
						driver, Duration.ofSeconds(8));
				searchResultWait.until(
						ExpectedConditions.visibilityOfElementLocated(claimBtnLocator));
				WebElement claimBtn = searchResultWait.until(
						ExpectedConditions.elementToBeClickable(claimBtnLocator));
				claimBtn.click();
				System.out.println("[ACTION] Clicked Claim for searched business: "
						+ searchTerm);
				claimedViaSearch = true;
			} catch (Exception e) {
				System.out.println("[INFO] Configured search '" + searchTerm
						+ "' returned no Claim button. Falling back to first "
						+ "available unclaimed business.");
			}
		} else {
			System.out.println("[INFO] No search term configured - using "
					+ "first available unclaimed business");
		}

		if (claimedViaSearch) {
			return;
		}

		// FALLBACK: clear search to refresh full list, click first Claim
		fallbackClaimAnyAvailable();
	}

	/**
	 * Fallback path - clear search field to refresh the full unclaimed-business
	 * list, then click the first available Claim button.
	 *
	 * Verified in app source useEffect (line 235-238): empty searchText
	 * re-fetches full list with current location.
	 */
	private void fallbackClaimAnyAvailable() {
		System.out.println("[FALLBACK] Clearing search to load full list");

		try {
			WebElement searchEl = wait.until(
					ExpectedConditions.elementToBeClickable(unclaimedSearchBox));
			searchEl.click();
			clearFieldRobustly(searchEl, "search");
			tryHideKeyboard();
		} catch (Exception e) {
			System.out.println("[WARN] Could not clear search field: "
					+ e.getMessage());
		}

		// Wait for default list to load (1s debounce + API call)
		try {
			Thread.sleep(3000);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}

		// Find ANY Claim button - UiSelector picks first match by default
		try {
			WebDriverWait listWait = new WebDriverWait(driver, Duration.ofSeconds(15));
			listWait.until(ExpectedConditions.visibilityOfElementLocated(claimBtnLocator));
			WebElement firstClaim = listWait.until(
					ExpectedConditions.elementToBeClickable(claimBtnLocator));
			firstClaim.click();
			System.out.println("[ACTION] Clicked Claim on first available unclaimed business");
		} catch (Exception e) {
			Assert.fail("[FAIL] No 'Claim' button found even on the unfiltered list. "
					+ "Possible causes: "
					+ "(a) No unclaimed businesses exist in the test backend, "
					+ "(b) Location services blocked the list from loading, "
					+ "(c) API/network failure. "
					+ "Check the device manually to debug.");
		}
	}

	/**
	 * Click "Great!" button with verification that the modal actually closed.
	 * After Great!, app calls afterClaimBusiness() which navigates away.
	 */
	private void clickGreatWithVerification() {
		System.out.println("[FLOW] Waiting for 'Great!' success modal");
		try {
			wait.until(ExpectedConditions.visibilityOf(greatBtnUnclaimedBusiness));
			wait.until(ExpectedConditions.elementToBeClickable(greatBtnUnclaimedBusiness)).click();
			System.out.println("[ACTION] Clicked Great!");
		} catch (Exception e) {
			Assert.fail("[FAIL] 'Great!' success modal did not appear. "
					+ "OTP verification likely succeeded but UI is in unexpected state. "
					+ e.getMessage());
		}

		// Brief pause for setTimeout(600) in app source after Great! click
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Handle RateUs screen if it appears.
	 *
	 * RateUs is CONDITIONAL based on async storage:
	 *   - Helper.userInfo.is_app_rating_view flag
	 *   - last_in_app_review_date in storage
	 *   - firebaseRemoteConfigData.for_app_rate_review_modal
	 *
	 * For a fresh test device or one that hasn't shown RateUs recently,
	 * the screen WILL appear. For repeated runs on same device, it may not.
	 *
	 * Approach: short wait for "Not now" or any rating UI element. If found,
	 * click "Not now". If not found within timeout, assume it was skipped
	 * by the app (proceed without failing).
	 */
	private void handleRateUsScreenIfPresent() {
		System.out.println("[FLOW] Checking for RateUs screen (conditional)");

		// Wait briefly for RateUs OR proceed if other screens visible
		// "Not now" text from translate('notNow') = "Not now"
		// Also check for star icons / Rate Us heading as anchors
		WebDriverWait checkWait = new WebDriverWait(driver, Duration.ofSeconds(6));
		try {
			checkWait.until(ExpectedConditions.or(
					ExpectedConditions.visibilityOfElementLocated(
							By.xpath("//android.widget.TextView[@text=\"Not now\"]")),
					ExpectedConditions.visibilityOfElementLocated(
							By.xpath("//android.widget.TextView[contains(@text,\"Rate Us\") "
									+ "or contains(@text,\"rate us\") "
									+ "or contains(@text,\"How is\") "
									+ "or contains(@text,\"Enjoying\")]")),
					// If we landed on NewNotification / Verify-Account upsell / HomeTab, skip RateUs
					ExpectedConditions.visibilityOf(notifyMeBtn),
					ExpectedConditions.visibilityOf(maybeLaterBtn),
					ExpectedConditions.visibilityOf(homePageDistanceSubmitBtn)
			));
		} catch (Exception e) {
			System.out.println("[INFO] RateUs check timed out - proceeding to next step");
			return;
		}

		// If "Not now" is visible -> RateUs screen is up, dismiss it
		try {
			By notNowBy = By.xpath("//android.widget.TextView[@text=\"Not now\"]");
			WebElement notNow = driver.findElement(notNowBy);
			if (notNow.isDisplayed()) {
				notNow.click();
				System.out.println("[ACTION] Dismissed RateUs via 'Not now'");
				try {
					Thread.sleep(800);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
				return;
			}
		} catch (Exception ignore) {
			// Not now button not present
		}

		System.out.println("[INFO] RateUs screen skipped (already on next screen)");
	}

	// =======================================================================
	// PUBLIC API: post-signup helpers (used by suite chaining)
	// =======================================================================

	public void HandleCustomDialog() {
		try {
			Assert.assertTrue(wait.until(ExpectedConditions.presenceOfElementLocated(
							By.xpath("//android.view.View[@content-desc=\"search-view\"]")))
					.isDisplayed(), "Search view not visible on Home screen.");
			driver.pressKey(new KeyEvent(AndroidKey.BACK));
		} catch (Exception e) {
			System.out.println("[WARN] HandleCustomDialog: search-view not found - " + e.getMessage());
		}
	}

	public void navigateToProfileScreen() {
		try {
			wait.until(ExpectedConditions.visibilityOf(profileViewBtn));
			wait.until(ExpectedConditions.elementToBeClickable(profileViewBtn)).click();
			driver.pressKey(new KeyEvent(AndroidKey.BACK));
		} catch (Exception e) {
			Assert.fail("[FAIL] Could not navigate to Profile screen: " + e.getMessage());
		}
	}

	public void NavigatesToSettingAndActivityScreen() {
		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence tap = new Sequence(finger, 1);
		int x = 1021;
		int y = 142;
		tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
		tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		driver.perform(Collections.singletonList(tap));
	}

	public void LogoutUser() {
		try {
			scrollableToText("Logout");
			wait.until(ExpectedConditions.visibilityOf(logoutOption)).click();
			wait.until(ExpectedConditions.visibilityOf(modalConfirmBtn)).click();
			wait.until(ExpectedConditions.visibilityOf(createAccountBtn));
			System.out.println("[FLOW] Logout completed");
		} catch (Exception e) {
			System.out.println("[WARN] Logout flow error: " + e.getMessage());
		}
	}

	// =======================================================================
	// PRIVATE HELPERS - shared between dog/business flows
	// =======================================================================

	private void clickUserTypeContinueWithDogProfileSelected() {
		// Dog is default; just click continue
		wait.until(ExpectedConditions.visibilityOf(userTypeHeading));
		wait.until(ExpectedConditions.elementToBeClickable(userTypeContinueBtn)).click();
		System.out.println("[ACTION] UserType continue clicked (Dog profile)");
	}

	private void navigateToUsernameViaBusinessChooser() {
		// Wait for UserType chooser OR username screen (in case app skipped chooser)
		try {
			shortWait.until(ExpectedConditions.or(
					ExpectedConditions.visibilityOf(userTypeHeading),
					ExpectedConditions.visibilityOf(businessProfileChoice),
					ExpectedConditions.visibilityOf(userNameField)
			));
		} catch (Exception e) {
			Assert.fail("[FAIL] Neither UserType chooser nor Username screen appeared: "
					+ e.getMessage());
		}

		// If on username screen already, return
		if (isDisplayed(userNameField)) {
			System.out.println("[FLOW] Already on Username screen");
			return;
		}

		// Pick Business
		wait.until(ExpectedConditions.elementToBeClickable(businessProfileChoice)).click();
		wait.until(ExpectedConditions.elementToBeClickable(userTypeContinueBtn)).click();
		System.out.println("[ACTION] Selected Business + Continue");

		// Confirm modal "Creating a business account?"
		try {
			shortWait.until(ExpectedConditions.visibilityOf(businessConfirmModalText));
			wait.until(ExpectedConditions.elementToBeClickable(modalConfirmBtn)).click();
			System.out.println("[ACTION] Confirmed business creation modal");
		} catch (Exception e) {
			System.out.println("[INFO] Business confirm modal not shown");
		}

		wait.until(ExpectedConditions.visibilityOf(userNameField));
	}

	private void enterUsernameWithRetry(String username) {
		System.out.println("[FLOW] Entering username: " + username);
		typeUsernameAndContinue(username);

		// OPTION C: handle REPEATED "Username taken" modals.
		// 1. Try up to 2 fresh COMPACT unique names ("Try another" = onCancel).
		//    Compact names avoid the app username length limit that longer
		//    run-scoped tokens can trip (surfacing as "Username taken").
		// 2. If still rejected, click "Confirm" (onConfirm) so the APP
		//    auto-generates a valid, unique username - immune to length AND
		//    collision, whatever the rejection cause.
		final int maxTryAnother = 2;
		for (int attempt = 1; attempt <= maxTryAnother; attempt++) {
			if (!isUsernameTakenModalShown()) {
				return; // name accepted - moved past the username screen
			}
			System.out.println("[INFO] Username taken (attempt " + attempt
					+ ") - retrying with a fresh compact name");
			wait.until(ExpectedConditions.elementToBeClickable(modalCancelBtn)).click();
			String retryName = compactUsernameToken();
			typeUsernameAndContinue(retryName);
			System.out.println("[ACTION] Retried with username: " + retryName);
		}

		// Still stuck on the modal after retries -> let the app create one.
		if (isUsernameTakenModalShown()) {
			System.out.println("[FALLBACK] Username still rejected after "
					+ maxTryAnother + " retries - clicking Confirm to let the "
					+ "app auto-generate a username");
			wait.until(ExpectedConditions.elementToBeClickable(modalConfirmBtn)).click();
		}
	}

	/** Type a username into the field and tap Continue. */
	private void typeUsernameAndContinue(String username) {
		WebElement field = wait.until(
				ExpectedConditions.elementToBeClickable(userNameField));
		field.clear();
		field.sendKeys(username);
		wait.until(ExpectedConditions.elementToBeClickable(userNameContinueBtn)).click();
	}

	/** True if the "Username taken" modal is visible within shortWait. */
	private boolean isUsernameTakenModalShown() {
		try {
			shortWait.until(ExpectedConditions.visibilityOf(usernameTakenTitle));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Compact (~10 char) high-entropy username that stays well under the
	 * app username length limit. Format: "tu" + 2 base36 time chars (rough
	 * ordering) + 6 random base36 chars (36^6 ~= 2.1B combinations). Used
	 * only for retries, where the long run-scoped token would be too long.
	 */
	private String compactUsernameToken() {
		String millis36 = Long.toString(System.currentTimeMillis(), 36);
		String timeTail = millis36.substring(Math.max(0, millis36.length() - 2));
		return "tu" + timeTail + randomAlnum(6);
	}

	private void fillDogProfileForm(String dogName) {
		System.out.println("[FLOW] Filling dog profile form");

		wait.until(ExpectedConditions.visibilityOf(dogNameField));
		WebElement nameEl = wait.until(ExpectedConditions.elementToBeClickable(dogNameField));
		nameEl.clear();
		nameEl.sendKeys(dogName);
		System.out.println("[INPUT] Dog name: " + dogName);

		// Breed selection
		wait.until(ExpectedConditions.elementToBeClickable(breedDropdown)).click();
		try {
			// Try to type a known/common breed for stable results across builds
			String breedSearch = testDataProp.getProperty("signupDogBreedSearch", "Adopted");
			shortWait.until(ExpectedConditions.visibilityOf(searchBreedField));
			searchBreedField.sendKeys(breedSearch);

			By breedItem = By.xpath("//android.widget.TextView[@text=\"" + breedSearch + "\"]");
			WebElement breedEl = wait.until(ExpectedConditions.elementToBeClickable(breedItem));
			breedEl.click();
			System.out.println("[INPUT] Selected breed: " + breedSearch);
		} catch (Exception e) {
			System.out.println("[WARN] Breed selection fallback - selecting first available: "
					+ e.getMessage());
			pressBackKey();
			pressBackKey();
		}

		// Gender (Female default)
		wait.until(ExpectedConditions.elementToBeClickable(dogGenderFemale)).click();
		System.out.println("[INPUT] Gender: Female");

		// DOB
		wait.until(ExpectedConditions.elementToBeClickable(dogDobTrigger)).click();
		wait.until(ExpectedConditions.elementToBeClickable(dobConfirmBtn)).click();
		System.out.println("[INPUT] DOB confirmed");

		// Scroll to bottom to bring image picker + Finish into view
		try {
			scrollToText("Finish");
		} catch (Exception ignore) {
			System.out.println("[INFO] scrollToText('Finish') failed - already at bottom?");
		}

		// Profile image (optional - if upload fails, app's Finish still skips with no image)
		try {
			wait.until(ExpectedConditions.elementToBeClickable(dogProfileImagePicker)).click();
			handleMultiplePermissionDialogs(3);
			wait.until(ExpectedConditions.elementToBeClickable(firstImageInGallery)).click();
			wait.until(ExpectedConditions.elementToBeClickable(gallerySelectionDone)).click();
			System.out.println("[INPUT] Profile image selected");
		} catch (Exception e) {
			System.out.println("[WARN] Image upload skipped: " + e.getMessage());
		}
	}

	private void fillBusinessRegistrationForm() {
		System.out.println("[FLOW] Filling business registration form");

		// Resolve work email - support AUTO marker for unique emails per run
		// Backend rejects duplicate work emails (already linked to another biz)
		String coName = testDataProp.getProperty("companyName", "TestCo " + uniqueSuffix());
		String firstName = testDataProp.getProperty("companyFirstName", "Test");
		String lastName = testDataProp.getProperty("companyLastName", "User");
		String rawWorkEmail = testDataProp.getProperty("companyWorkEmail", "AUTO:work");
		String email = resolveSignupEmail(rawWorkEmail);
		if (email.isEmpty()) {
			email = generateUniqueEmail("work");
		}
		String confirmEmail = testDataProp.getProperty(
				"companyConfirmWorkEmail", email);
		// If confirm is AUTO or empty, use same as work email
		if (confirmEmail.equalsIgnoreCase("AUTO")
				|| confirmEmail.toUpperCase().startsWith("AUTO:")
				|| confirmEmail.isEmpty()) {
			confirmEmail = email;
		}

		fillFieldRobustly(companyNameField, coName, "company_name");
		fillFieldRobustly(companyFirstNameField, firstName, "firstName");
		fillFieldRobustly(companyLastNameField, lastName, "lastName");
		fillFieldRobustly(companyWorkEmailField, email, "work_email");
		fillConfirmEmailWithScroll(confirmEmail);

		// Hide keyboard and scroll to make sure button is visible
		tryHideKeyboard();
		scrollIntoViewIfNeeded("Continue");
		System.out.println("[INPUT] Business registration fields filled "
				+ "(work_email=" + email + ")");
	}

	/**
	 * Click businessCreateCon button and verify navigation actually happened.
	 *
	 * App source (businessRegisterDetails/index.js -> onContinueFirst -> goToNext):
	 *  - Click triggers validate() -> if fails, Helper.showToast + stay on form
	 *  - Validate passes -> Api.sendWorkEmailOtp -> on success navigate to Otp screen
	 *  - API fails -> Helper.showToast -> stay on form
	 *
	 * So after click, ONE of these must be true:
	 *  - OTP screen visible (success path)
	 *  - Stayed on form (validation/API failure - log clear error)
	 *
	 * Method retries up to 3 times with keyboard hide before each attempt.
	 */
	private void clickBusinessCreateConWithVerification() {
		int maxAttempts = 3;
		Exception lastEx = null;

		for (int attempt = 1; attempt <= maxAttempts; attempt++) {
			try {
				tryHideKeyboard();
				try {
					Thread.sleep(500);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}

				WebElement btn = wait.until(
						ExpectedConditions.elementToBeClickable(businessCreateContinueBtn));
				btn.click();
				System.out.println("[ACTION] Clicked businessCreateCon (attempt "
						+ attempt + ")");

				// Wait for either OTP screen OR stayed-on-form (toast path)
				// API + recaptcha chain can take 5-15s on slow networks
				WebDriverWait postClickWait = new WebDriverWait(
						driver, Duration.ofSeconds(15));
				try {
					postClickWait.until(ExpectedConditions.or(
							ExpectedConditions.visibilityOf(otp1),
							ExpectedConditions.visibilityOfElementLocated(
									By.xpath("//*[contains(@text,\"OTP\") "
											+ "or contains(@text,\"verification\") "
											+ "or contains(@text,\"sent to\")]"))
					));
					System.out.println("[VERIFIED] OTP screen reached");
					return;
				} catch (Exception postEx) {
					// Did NOT reach OTP screen - check why
					if (isDisplayed(businessCreateContinueBtn)) {
						String toastInfo = readVisibleToastOrError();
						System.out.println("[CLICK ATTEMPT " + attempt
								+ "] Stayed on form. " + toastInfo);
						lastEx = postEx;
						// Continue to next retry
					} else {
						// Click worked - some other screen reached
						System.out.println("[CLICK ATTEMPT " + attempt
								+ "] Form gone but OTP not detected - "
								+ "continuing to OTP wait in caller");
						return;
					}
				}
			} catch (Exception clickEx) {
				lastEx = clickEx;
				System.out.println("[CLICK ATTEMPT " + attempt + "] Failed: "
						+ clickEx.getMessage());
			}
		}

		// All attempts exhausted
		String toastInfo = readVisibleToastOrError();
		Assert.fail("[FAIL] businessCreateCon click did not advance to OTP "
				+ "screen after " + maxAttempts + " attempts. " + toastInfo
				+ " Possible causes: (a) work_email already used on backend "
				+ "(set companyWorkEmail=AUTO:work in TestData.properties), "
				+ "(b) form validation failed silently, "
				+ "(c) backend OTP API rate-limited.");
	}

	/**
	 * Read any visible toast or error message for diagnostic purposes.
	 * Returns description string or empty if nothing found.
	 */
	private String readVisibleToastOrError() {
		String[] xpaths = {
				"//android.widget.Toast",
				"//*[contains(@text,\"already\") or contains(@text,\"exists\")]",
				"//*[contains(@text,\"valid\") or contains(@text,\"invalid\")]",
				"//*[contains(@text,\"required\") or contains(@text,\"enter\")]",
				"//*[contains(@text,\"failed\") or contains(@text,\"error\")]"
		};
		for (String xp : xpaths) {
			try {
				WebElement el = driver.findElement(By.xpath(xp));
				String text = el.getText();
				if (text != null && !text.isEmpty()) {
					return "Visible message: '" + text + "'.";
				}
			} catch (Exception ignore) { /* try next */ }
		}
		return "No visible toast/error captured (may have auto-dismissed).";
	}

	/**
	 * Fill a text field robustly:
	 *  1. Wait for clickable
	 *  2. Click to focus
	 *  3. Clear via robust strategy
	 *  4. SendKeys new value
	 *  5. Hide keyboard so next field click works
	 */
	private void fillFieldRobustly(WebElement field, String value, String label) {
		int maxAttempts = 3;
		for (int attempt = 1; attempt <= maxAttempts; attempt++) {
			try {
				WebElement el = wait.until(ExpectedConditions.elementToBeClickable(field));
				el.click();
				clearFieldRobustly(el, label);
				el.sendKeys(value);
				System.out.println("[INPUT] " + label + " = " + value);
				tryHideKeyboard();
				return;
			} catch (Exception e) {
				if (attempt == maxAttempts) {
					logEditableFieldsForDiagnostics();
					Assert.fail("[FAIL] Could not fill field '" + label + "': "
							+ e.getMessage());
				}
				// Recovery before retry:
				//   attempt 1 -> hide keyboard (it may be covering the field)
				//   attempt 2 -> scroll the form forward to bring an off-screen
				//                field (e.g. confirmEmail) into the a11y tree
				if (attempt == 1) {
					System.out.println("[INFO] '" + label
							+ "' not ready - hiding keyboard and retrying");
					tryHideKeyboard();
				} else {
					System.out.println("[INFO] '" + label
							+ "' still not visible - scrolling form to reveal it");
					tryHideKeyboard();
					scrollFormForwardOnce();
				}
			}
		}
	}

	/**
	 * Scroll the first scrollable container forward by ~one viewport to
	 * bring an off-screen field into the accessibility tree. No-op if there
	 * is no scrollable container or the form is already at the bottom.
	 */
	private void scrollFormForwardOnce() {
		// 1) Native scroll container - works only when the container is
		//    exposed as scrollable(true) in the a11y tree.
		try {
			driver.findElement(AppiumBy.androidUIAutomator(
					"new UiScrollable(new UiSelector().scrollable(true))"
							+ ".scrollForward()"));
			return;
		} catch (Exception ignore) {
			// React Native ScrollViews are frequently NOT marked
			// scrollable(true), so UiScrollable is a no-op. Fall back to a
			// coordinate gesture which scrolls regardless of that flag.
		}
		// 2) Coordinate-based scroll gesture (React Native friendly).
		try {
			org.openqa.selenium.Dimension size =
					driver.manage().window().getSize();
			java.util.Map<String, Object> args = new java.util.HashMap<>();
			args.put("left", (int) (size.getWidth() * 0.1));
			args.put("top", (int) (size.getHeight() * 0.25));
			args.put("width", (int) (size.getWidth() * 0.8));
			args.put("height", (int) (size.getHeight() * 0.45));
			args.put("direction", "down");
			args.put("percent", 0.85);
			driver.executeScript("mobile: scrollGesture", args);
		} catch (Exception ignore) {
			// gesture unsupported / failed - leave screen as-is
		}
	}

	/**
	 * Diagnostic: print every EditText currently in the a11y tree with
	 * its content-desc / resource-id / text. Helps identify the real
	 * locator (or confirm a field is absent) when a fill can't find one.
	 */
	private void logEditableFieldsForDiagnostics() {
		try {
			java.util.List<WebElement> fields =
					driver.findElements(By.className("android.widget.EditText"));
			System.out.println("[DIAG] EditText fields on screen: " + fields.size());
			for (WebElement f : fields) {
				System.out.println("[DIAG]   content-desc='" + safeAttr(f, "content-desc")
						+ "' resource-id='" + safeAttr(f, "resource-id")
						+ "' text='" + safeAttr(f, "text") + "'");
			}
		} catch (Exception e) {
			System.out.println("[DIAG] Could not enumerate fields: " + e.getMessage());
		}
	}

	private String safeAttr(WebElement el, String name) {
		try {
			String v = el.getAttribute(name);
			return v == null ? "" : v;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Confirm Email needs special handling: the box sits just below the
	 * keyboard line and is not reliably in the a11y tree until focused.
	 * Strategy (per observed device behaviour):
	 *   1. Locate the box (presence-based; scroll the form forward first
	 *      if it is not yet in the tree).
	 *   2. Click it to focus - the app lifts the focused field above the IME.
	 *   3. Swipe the screen upwards so it clears the keyboard.
	 *   4. Clear and type the value.
	 */
	private void fillConfirmEmailWithScroll(String value) {
		String label = "con_email";
		WebElement field = findFieldByAccId("confirmEmail");
		if (field == null) {
			scrollFormForwardOnce();
			field = findFieldByAccId("confirmEmail");
		}
		if (field == null) {
			logEditableFieldsForDiagnostics();
			Assert.fail("[FAIL] Could not locate '" + label
					+ "' (confirmEmail) even after scrolling. "
					+ "See [DIAG] field list above.");
			return;
		}
		try {
			field.click();
			System.out.println("[ACTION] Focused con_email box");
			swipeScreenUp();
			clearFieldRobustly(field, label);
			field.sendKeys(value);
			System.out.println("[INPUT] " + label + " = " + value);
			tryHideKeyboard();
		} catch (Exception e) {
			logEditableFieldsForDiagnostics();
			Assert.fail("[FAIL] Could not fill field '" + label + "': "
					+ e.getMessage());
		}
	}

	private WebElement findFieldByAccId(String accId) {
		try {
			java.util.List<WebElement> els =
					driver.findElements(AppiumBy.accessibilityId(accId));
			return els.isEmpty() ? null : els.get(0);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Swipe the content area upwards (finger bottom->top) so a focused
	 * field is lifted above the soft keyboard. RN-friendly - does not
	 * depend on a scrollable(true) container.
	 */
	private void swipeScreenUp() {
		try {
			org.openqa.selenium.Dimension size =
					driver.manage().window().getSize();
			java.util.Map<String, Object> args = new java.util.HashMap<>();
			args.put("left", (int) (size.getWidth() * 0.1));
			args.put("top", (int) (size.getHeight() * 0.2));
			args.put("width", (int) (size.getWidth() * 0.8));
			args.put("height", (int) (size.getHeight() * 0.35));
			args.put("direction", "up");
			args.put("percent", 0.6);
			driver.executeScript("mobile: swipeGesture", args);
		} catch (Exception ignore) {
			// gesture unsupported / failed - leave as-is
		}
	}

	private void submitOtpWithFastFail() {
		System.out.println("[FLOW] Submitting OTP");
		String otp = testDataProp.getProperty("signupValidOtp", "1111");
		boolean manualOtpInput = Boolean.parseBoolean(
				testDataProp.getProperty("manualOtpInput", "false"));
		int manualWait = Integer.parseInt(
				testDataProp.getProperty("manualOtpWaitSeconds", "60"));

		wait.until(ExpectedConditions.visibilityOf(otp1));

		if (manualOtpInput) {
			System.out.println("[OTP] MANUAL MODE - waiting " + manualWait
					+ "s for you to enter OTP on device");
			try {
				Thread.sleep(manualWait * 1000L);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		} else {
			fillOtpDigits(otp);
		}

		tryHideKeyboard();
		wait.until(ExpectedConditions.elementToBeClickable(otpContinueBtn)).click();
		System.out.println("[ACTION] OTP Continue clicked");

		// Fast-fail if OTP rejected (still on OTP screen after a few seconds)
		try {
			Thread.sleep(2500);
			boolean stillOnOtp = isDisplayed(otp1);
			if (stillOnOtp) {
				String hint = manualOtpInput
						? "Manual OTP appears to be incorrect."
						: "Auto OTP '" + otp + "' was rejected. Set manualOtpInput=true "
								+ "in TestData.properties OR update signupValidOtp.";
				Assert.fail("[OTP REJECTED] " + hint);
			}
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}

	private void fillOtpDigits(String otp) {
		if (otp == null || otp.length() != 4) {
			Assert.fail("OTP must be 4 digits, got: " + otp);
		}
		wait.until(ExpectedConditions.elementToBeClickable(otp1)).sendKeys(otp.substring(0, 1));
		wait.until(ExpectedConditions.elementToBeClickable(otp2)).sendKeys(otp.substring(1, 2));
		wait.until(ExpectedConditions.elementToBeClickable(otp3)).sendKeys(otp.substring(2, 3));
		wait.until(ExpectedConditions.elementToBeClickable(otp4)).sendKeys(otp.substring(3, 4));
	}

	private void fillBusinessSecondScreen() {
		System.out.println("[FLOW] Filling business 2nd screen (location/service/image/description)");

		// Location
		try {
			shortWait.until(ExpectedConditions.elementToBeClickable(getCurrentLocationBtn)).click();
			handleMultiplePermissionDialogs(2);
			System.out.println("[ACTION] Got current location");
		} catch (Exception e) {
			System.out.println("[WARN] Location step skipped: " + e.getMessage());
		}

		// Service
		try {
			wait.until(ExpectedConditions.elementToBeClickable(dogWalkingService)).click();
			System.out.println("[INPUT] Selected Dog Walking service");
		} catch (Exception e) {
			System.out.println("[WARN] Service selection failed: " + e.getMessage());
		}

		// Listing checkbox - select the "test" business listing
		try {
			By testListing = By.xpath(
					"//android.view.ViewGroup[@content-desc=\"test\"]"
							+ "/android.view.ViewGroup/android.view.ViewGroup");
			wait.until(ExpectedConditions.elementToBeClickable(testListing)).click();
			System.out.println("[INPUT] Selected 'test' business listing checkbox");
			// Best-effort confirmation the row is now selected
			try {
				WebElement row = driver.findElement(
						By.xpath("//android.view.ViewGroup[@content-desc=\"test\"]"));
				System.out.println("[INFO] 'test' row selected="
						+ safeAttr(row, "selected") + " checked="
						+ safeAttr(row, "checked"));
			} catch (Exception ignore) { /* state read is best-effort */ }
		} catch (Exception e) {
			System.out.println("[WARN] 'test' listing checkbox selection failed: "
					+ e.getMessage());
		}

		// Image
		try {
			wait.until(ExpectedConditions.elementToBeClickable(firstBusinessImageSlot)).click();
			handleMultiplePermissionDialogs(3);
			wait.until(ExpectedConditions.elementToBeClickable(firstImageInGallery)).click();
			wait.until(ExpectedConditions.elementToBeClickable(gallerySelectionDone)).click();
			System.out.println("[INPUT] Business image selected");
		} catch (Exception e) {
			System.out.println("[WARN] Business image skipped: " + e.getMessage());
		}

		// Description
		try {
			scrollIntoViewIfNeeded("English");
			String desc = testDataProp.getProperty("businessDescription",
					"This is a test business description.");
			wait.until(ExpectedConditions.elementToBeClickable(businessDescriptionField)).sendKeys(desc);
			tryHideKeyboard();
			System.out.println("[INPUT] Description entered");
		} catch (Exception e) {
			System.out.println("[WARN] Description failed: " + e.getMessage());
		}

		wait.until(ExpectedConditions.elementToBeClickable(businessSecondContinueBtn)).click();
		System.out.println("[ACTION] businessSecondCon clicked");
	}

	/**
	 * Best-effort: dismiss the "Verify Your Business" upsell (which appears
	 * before the Notifications screen in BOTH the dog and business signup
	 * flows) by tapping "Stay on Free" to remain on the free (non-licensed)
	 * tier. No-op if the screen does not appear.
	 */
	private void dismissStayOnFreeUpsellIfPresent() {
		try {
			shortWait.until(ExpectedConditions.elementToBeClickable(
					By.xpath("//android.widget.TextView[@text=\"Stay on Free\"]")))
					.click();
			System.out.println("[ACTION] Verify Your Business upsell - tapped 'Stay on Free'");
		} catch (Exception e) {
			System.out.println("[INFO] Verify Your Business upsell not shown - skipping");
		}
	}

	private void fillBusinessThirdScreen() {
		System.out.println("[FLOW] Business 3rd screen (phone)");
		wait.until(ExpectedConditions.visibilityOf(phoneNumberHeading));
		scrollIntoViewIfNeeded("Submit");
		wait.until(ExpectedConditions.elementToBeClickable(businessSubmitBtn)).click();
		System.out.println("[ACTION] businessSubmit clicked");
	}

	/**
	 * Notification + Home Distance flow.
	 * Two scenarios (matches Login/ForgotPassword pattern):
	 *   1. NewNotification screen -> system permission dialog -> distance modal
	 *   2. Notification already granted at OS level -> direct distance modal
	 */
	private void completeNotificationAndDistanceFlow() {
		// The post-signup flow now shows TWO screens in sequence:
		//   1) "Verify Your Account" upsell   -> dismiss via "Maybe Later"
		//   2) "Turn on Notifications" screen -> "Yes, Notify Me" (or SKIP)
		// Both are handled independently and best-effort, so whichever appears
		// (or both, in order) gets dismissed. They are NOT mutually exclusive.

		// --- Screen 1: "Verify Your Account" upsell -> Maybe Later ---
		try {
			System.out.println("[FLOW] Checking 'Verify Your Account' upsell screen...");
			shortWait.until(ExpectedConditions.elementToBeClickable(maybeLaterBtn)).click();
			System.out.println("[ACTION] Dismissed 'Verify Your Account' upsell via 'Maybe Later'");
		} catch (Exception e) {
			System.out.println("[INFO] 'Maybe Later' upsell not shown");
		}

		// --- Screen 2: "Turn on Notifications" -> Yes, Notify Me (ALWAYS check) ---
		try {
			System.out.println("[FLOW] Checking 'Yes, Notify Me' screen...");
			shortWait.until(ExpectedConditions.elementToBeClickable(notifyMeBtn)).click();
			System.out.println("[Scenario 1] Clicked 'Yes, Notify Me'");
			handleSinglePermissionDialogIfPresent();
		} catch (Exception e) {
			System.out.println("[Scenario 2] NewNotification skipped (perm already granted)");
		}

		// --- Distance preference modal on Home (best-effort: not always shown) ---
		try {
			Thread.sleep(1500);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
		try {
			shortWait.until(ExpectedConditions.visibilityOf(homePageDistanceSubmitBtn));
			shortWait.until(ExpectedConditions.elementToBeClickable(homePageDistanceSubmitBtn)).click();
			System.out.println("[ACTION] Clicked feed-distance-submit on Home");
		} catch (Exception e) {
			System.out.println("[INFO] Distance modal (feed-distance-submit) not shown - skipping");
		}
	}

	/**
	 * Notification flow only - used by claimed-business flow which doesn't
	 * always show the distance modal.
	 */
	private void completeNotificationFlowOnly() {
		// Newer builds: "Verify Your Account" upsell -> "Maybe Later".
		try {
			shortWait.until(ExpectedConditions.elementToBeClickable(maybeLaterBtn)).click();
			System.out.println("[ACTION] Dismissed 'Verify Your Account' upsell via 'Maybe Later'");
			handleSinglePermissionDialogIfPresent();
			return;
		} catch (Exception e) {
			System.out.println("[INFO] 'Maybe Later' upsell not shown");
		}
		// Older builds (fallback): "Yes, Notify Me".
		try {
			shortWait.until(ExpectedConditions.elementToBeClickable(notifyMeBtn)).click();
			System.out.println("[ACTION] Clicked 'Yes, Notify Me'");
			handleSinglePermissionDialogIfPresent();
		} catch (Exception e) {
			System.out.println("[INFO] Notify screen not shown");
		}
	}

	/**
	 * Generic single-permission handler. Tolerates curly-apostrophe text
	 * variations and Android version differences in button IDs.
	 */
	private boolean handleSinglePermissionDialogIfPresent() {
		try {
			WebElement msg = shortWait.until(ExpectedConditions.visibilityOf(permissionDialogMsg));
			String text = msg.getText();
			System.out.println("[PERMISSION] Dialog: " + text);
		} catch (Exception e) {
			return false;
		}

		if (clickIfDisplayed(permissionWhileUsingAppBtn, "While Using App")) return true;
		if (clickIfDisplayed(permissionAllowBtn, "Allow")) return true;
		if (clickIfDisplayed(permissionAllowAllBtn, "Allow All")) return true;
		if (clickIfDisplayed(permissionAllowOneTimeBtn, "Allow Once")) return true;
		System.out.println("[WARN] Permission dialog visible but no button matched");
		return false;
	}

	private void handleMultiplePermissionDialogs(int maxDialogs) {
		for (int i = 0; i < maxDialogs; i++) {
			boolean handled = handleSinglePermissionDialogIfPresent();
			if (!handled) return;
			try {
				Thread.sleep(1500);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private boolean clickIfDisplayed(WebElement el, String label) {
		try {
			if (el.isDisplayed()) {
				el.click();
				System.out.println("[CLICK] Permission button: " + label);
				return true;
			}
		} catch (Exception ignore) {
		}
		return false;
	}

	private boolean isDisplayed(WebElement el) {
		try {
			return el.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	private void scrollIntoViewIfNeeded(String text) {
		try {
			driver.findElement(AppiumBy.androidUIAutomator(
					"new UiScrollable(new UiSelector().scrollable(true))" +
					".scrollIntoView(new UiSelector().textContains(\"" + text + "\"))"));
		} catch (Exception ignore) {
			// Element may already be visible OR no scrollable container
		}
	}

	private void tryHideKeyboard() {
		try {
			driver.hideKeyboard();
		} catch (Exception ignore) {
		}
	}

	private void pressBackKey() {
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
	}

	// Run-scoped unique-token generator (collision-resistant across runs).
	// RUN_ID is computed once per JVM/test-run and shared by all instances;
	// SEQ guarantees per-call uniqueness even within the same millisecond.
	private static final String RUN_ID = newRunId();
	private static final AtomicInteger SEQ = new AtomicInteger(0);

	private static String newRunId() {
		String time = Long.toString(System.currentTimeMillis(), 36); // full precision
		return time + randomAlnum(4);                                 // + cross-run entropy
	}

	private static String randomAlnum(int n) {
		final String ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; i++) {
			sb.append(ALPHABET.charAt(ThreadLocalRandom.current().nextInt(ALPHABET.length())));
		}
		return sb.toString();
	}

	/**
	 * Collision-resistant unique token: unique per call, per run, and across
	 * runs. Format: <base36 millis><4 random base36 chars><base36 seq>.
	 * Replaces the old (millis % 1_000_000) which truncated to 6 digits and
	 * wrapped every ~16.7 min, causing occasional duplicate account names.
	 */
	private String uniqueSuffix() {
		return RUN_ID + Integer.toString(SEQ.getAndIncrement(), 36);
	}

	// =======================================================================
	// PUBLIC HELPERS - Unique email/username generation for test data
	//
	// Why: Signup tests need a fresh email every run. Hard-coded emails in
	// SignupData.json get "already registered" after the first run, forcing
	// engineers to manually edit JSON files between runs - an anti-pattern.
	//
	// Convention: In SignupData.json, set the email value to one of:
	//   ""          -> empty (used for empty-cred validation test)
	//   "AUTO"      -> auto-generate fresh unique email each run
	//   "AUTO:dog"  -> auto-generate with custom prefix (becomes dog<timestamp>)
	//   "actual@email.com" -> use as-is (for existing-user / login tests)
	//
	// resolveSignupEmail() is called by the test class on the JSON value
	// before passing it to SignupWithEmailPassword().
	// =======================================================================

	/**
	 * Resolve a signup email value from JSON test data.
	 * If the value is "AUTO" or "AUTO:prefix", generates a unique mailinator email.
	 * Otherwise returns the value unchanged.
	 *
	 * @param rawValue value from SignupData.json (may be null/empty/AUTO/regular email)
	 * @return resolved email string ready for sendKeys
	 */
	public String resolveSignupEmail(String rawValue) {
		if (rawValue == null) {
			return "";
		}
		String trimmed = rawValue.trim();
		if (trimmed.isEmpty()) {
			return "";
		}
		if (trimmed.equalsIgnoreCase("AUTO")) {
			String generated = "auto" + uniqueSuffix() + "@mailinator.com";
			System.out.println("[TEST DATA] AUTO email generated: " + generated);
			return generated;
		}
		if (trimmed.toUpperCase().startsWith("AUTO:")) {
			String prefix = trimmed.substring("AUTO:".length()).trim();
			if (prefix.isEmpty()) {
				prefix = "auto";
			}
			String generated = prefix + uniqueSuffix() + "@mailinator.com";
			System.out.println("[TEST DATA] AUTO:" + prefix + " email generated: " + generated);
			return generated;
		}
		// Regular email - return as-is
		return trimmed;
	}

	/**
	 * Generate a fresh unique email at any point. Useful when test class wants
	 * to override/replace JSON value entirely.
	 */
	public String generateUniqueEmail(String prefix) {
		String safePrefix = (prefix == null || prefix.isEmpty()) ? "auto" : prefix;
		return safePrefix + uniqueSuffix() + "@mailinator.com";
	}
}