package org.rahulshettyacademy;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.SocialLoginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Dogpack_GmailLogin - Gmail signup/login test.
 *
 * Single test that exercises the full Google sign-in flow:
 *   1. Click google login on welcome screen
 *   2. Pick account from system picker (if shown)
 *   3. New user: complete dog profile + notification + distance
 *   4. Returning user: directly to home (or via notify screen)
 *
 * NOTE: alwaysRun=true added for failure isolation. The test is self-contained
 * (single method) so isolation matters less here, but consistency with other
 * refactored modules.
 */
public class Dogpack_GmailLogin extends AndroidBaseTest {

	SocialLoginPage socialLogin;

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		socialLogin = new SocialLoginPage(driver);
	}

	@Test(priority = 1, groups = { "Smoke", "Regression" }, alwaysRun = true)
	public void SignupWithGmail() throws InterruptedException {
		socialLogin.SignupWithGmail();
	}
}