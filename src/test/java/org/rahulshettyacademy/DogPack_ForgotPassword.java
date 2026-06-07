package org.rahulshettyacademy;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.ForgotPassword;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * DogPack_ForgotPassword - Test class for the Forgot Password module.
 *
 * App source mapped:
 *  - signUpWelcome -> Login -> ForgotPassword -> OTP -> ChangePassword
 *      -> NewNotification -> System dialog -> Home (distance modal)
 *
 * Test sequence (priorities are STRICT, dependsOnMethods enforces correct order):
 *   1. NavigateToLoginScreen        - intro -> login (signup_login)
 *   2. NavigateToForgotPasswordScr  - login -> forgot password (login_forgot)
 *   3. ForgotPasswordWithInvalidUsr - asserts "User not found"
 *   4. ForgotPasswordHappyPath      - email -> OTP -> change password -> success toast
 *   5. CompletePasswordResetLogin   - notify screen + system dialog + home distance modal
 *
 * IMPROVEMENTS FROM ORIGINAL:
 *  - Two tests had priority=3 in original. Now strict priorities + dependsOnMethods.
 *  - Missing navigation step (Login -> ForgotPassword) added as a separate test.
 *  - Resend OTP flow extracted - is OPTIONAL because of 119s app cooldown timer.
 *  - All tests in Smoke + Regression groups for selective execution.
 *  - Method names match Java conventions (PascalCase for tests).
 */
public class DogPack_ForgotPassword extends AndroidBaseTest {

	private ForgotPassword forgot;

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		forgot = new ForgotPassword(driver);
	}

	// =======================================================================
	// TEST 1: SignUp Welcome -> Login screen
	// =======================================================================
	@Test(priority = 1,
	      groups = { "Smoke", "Regression" },
	      description = "Navigate from SignUp Welcome screen to Login screen")
	public void NavigateToLoginScreen() {
		forgot.scrollToLogin();
		forgot.NavigateToLogin();
	}

	// =======================================================================
	// TEST 2: Login screen -> ForgotPassword screen
	// =======================================================================
	@Test(priority = 2,
	      groups = { "Smoke", "Regression" },
	      description = "Navigate from Login screen to Forgot Password screen via 'login_forgot'",
	      dependsOnMethods = "NavigateToLoginScreen")
	public void NavigateToForgotPasswordScreen() {
		forgot.navigateToForgotPasswordScreen();
	}

	// =======================================================================
	// TEST 3: Invalid user submission
	// =======================================================================
	@Test(priority = 3,
	      groups = { "Smoke", "Regression" },
	      description = "Submit invalid user/email and verify 'User not found' error",
	      dependsOnMethods = "NavigateToForgotPasswordScreen")
	public void ForgotPasswordWithInvalidUserFunctionality() {
		forgot.ForgotPasswordWithInvalidUserFunctionality();
	}

	// =======================================================================
	// TEST 4: Happy-path forgot password
	// =======================================================================
	@Test(priority = 4,
	      groups = { "Smoke", "Regression" },
	      description = "Full happy path: email -> OTP -> new password -> success",
	      dependsOnMethods = "ForgotPasswordWithInvalidUserFunctionality")
	public void ForgotPasswordHappyPath() throws InterruptedException {
		forgot.ForgotPasswordFunctionality();
	}

	// =======================================================================
	// TEST 5: Post-reset login completion
	// =======================================================================
	@Test(priority = 5,
	      groups = { "Smoke", "Regression" },
	      description = "After password reset: notify screen + permission + distance modal",
	      dependsOnMethods = "ForgotPasswordHappyPath")
	public void CompletePasswordResetLoginProcess() {
		forgot.completePasswordResetLoginProcess();
	}

	/*
	 * NOTE on Resend OTP test:
	 *  App's ResendOTP component has a hard-coded 119-second countdown before the button
	 *  becomes clickable. Including a Resend test in CI adds ~2 minutes of wait time per run.
	 *  Uncomment below if you want to include it in the Regression group only.
	 *
	 * @Test(priority = 4,
	 *       groups = { "Regression" },
	 *       description = "Resend OTP after countdown elapses (~120s wait)",
	 *       dependsOnMethods = "ForgotPasswordWithInvalidUserFunctionality")
	 * public void ResendOtpFlow() {
	 *     forgot.resendOtpFlow();
	 * }
	 */
}