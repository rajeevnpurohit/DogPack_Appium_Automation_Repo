package org.rahulshettyacademy;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.SocialLoginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Dogpack_FacebookLogin - Facebook signup/login test.
 *
 * Single test that exercises the full Facebook sign-in flow:
 *   1. Click facebook login on welcome screen
 *   2. Click "Continue as <name>" (FB native sheet, locale-tolerant)
 *   3. New user: complete dog profile + notification + distance
 *   4. Returning user: directly to home
 */
public class Dogpack_FacebookLogin extends AndroidBaseTest {

	SocialLoginPage socialLogin;

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		socialLogin = new SocialLoginPage(driver);
	}

	@Test(priority = 1, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void SignupWithFacebook() throws InterruptedException {
		socialLogin.SignupWithFacebook();
	}
}