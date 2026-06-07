package org.rahulshettyacademy;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.WithoutLoginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Dogpack_WithoutLogin - Test class for guest/without-login functionality
 *
 * REORDERED Test Sequence (rationale: Lost Dog flow accumulates state -
 * memory pressure, event listeners, modal stacks - which can crash the
 * app/UiAutomator2 instrumentation. Language test was running AFTER Lost Dog
 * and crashing as a result. Moving Language to BEFORE Lost Dog ensures
 * fresh app state for language verification, and Lost Dog last means any
 * post-cleanup crash doesn't affect anything else):
 *
 *   1. Terms & Conditions navigation (WebView)
 *   2. Privacy Policy navigation (WebView)
 *   3. Change Language (independent - runs early on fresh app state)
 *   4. Report Lost Dog (creates the post - foundation for tests 5-8)
 *   5-8. Three-dot menu actions:
 *      - Report Dog Found (priority 5)
 *      - Copy URL (priority 6 - non-destructive)
 *      - Close popup (priority 7 - non-destructive)
 *      - Report or Delete (priority 8 - DESTRUCTIVE, last)
 *
 * IMPORTANT NOTES:
 *   - Tests 5-8 do NOT assume own post ownership. The first row in
 *     LostDogList may be ANY user's post (backend-sorted by location).
 *     handleReportOrDelete() adapts to whichever option is visible.
 *   - Test 3 (ChangeLanguage) RESTORES English at end - safe for tests 4+.
 *   - alwaysRun=true on @BeforeClass and tests for failure isolation.
 */
public class Dogpack_WithoutLogin extends AndroidBaseTest {

	WithoutLoginPage withoutLogin;

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		withoutLogin = new WithoutLoginPage(driver);
	}

	// ========================================================================
	// TEST 1: Navigate to Terms and Conditions (WebView)
	// ========================================================================
	@Test(priority = 1,
	      groups = { "Smoke", "Regression" },
	      alwaysRun = true,
	      description = "Verify Terms and Conditions opens in WebView, scrolls, returns to native")
	public void NavigatesToTermsAndCondition() throws InterruptedException {
		withoutLogin.navigatesToTermsAndCondition();
	}

	// ========================================================================
	// TEST 2: Navigate to Privacy Policy (WebView)
	// ========================================================================
	@Test(priority = 2,
	      groups = { "Smoke", "Regression" },
	      alwaysRun = true,
	      description = "Verify Privacy Policy opens in WebView, scrolls, returns to native")
	public void NavigatesToPrivacyPolicy() throws InterruptedException {
		withoutLogin.navigatesToPrivacyPolicy();
	}

	// ========================================================================
	// TEST 3: Change Language (MOVED EARLIER - runs on fresh app state)
	// ========================================================================
	// Tests all 13 languages + English restore. Runs BEFORE Lost Dog flow
	// because Lost Dog accumulates state (memory + listeners) which was
	// crashing the language test in previous run order.
	@Test(priority = 3,
	      groups = { "Smoke", "Regression" },
	      alwaysRun = true,
	      description = "Verify all 13 language options work correctly and restore English at end")
	public void ChangeLanguage() throws InterruptedException {
		withoutLogin.ChangeLanguage();
	}

	// ========================================================================
	// TEST 4: Create Lost Dog Post (Foundation for next tests)
	// ========================================================================
	@Test(priority = 4,
	      groups = { "Smoke", "Regression" },
	      alwaysRun = true,
	      description = "Create a lost dog post without login - end-to-end form flow (11 sub-screens)")
	public void ReportLostDogWithoutLogin() throws InterruptedException {
		withoutLogin.ReportLostDogWithoutLogin();
	}

	// ========================================================================
	// TEST 5: Report Dog as Found (3-dot menu action)
	// ========================================================================
	@Test(priority = 5,
	      groups = { "Smoke", "Regression" },
	      alwaysRun = true,
	      description = "Report the dog as found via 3-dot menu (tolerant if option missing)",
	      dependsOnMethods = "ReportLostDogWithoutLogin")
	public void reportDogFound() throws InterruptedException {
		withoutLogin.clickOnLostDogThreeDotAction();
		withoutLogin.reportDogFound();
	}

	// ========================================================================
	// TEST 6: Copy URL (3-dot menu action - non-destructive)
	// ========================================================================
	@Test(priority = 6,
	      groups = { "Smoke", "Regression" },
	      alwaysRun = true,
	      description = "Copy the lost dog post URL via 3-dot menu",
	      dependsOnMethods = "ReportLostDogWithoutLogin")
	public void copyURLFunctionality() throws InterruptedException {
		withoutLogin.clickOnLostDogThreeDotAction();
		withoutLogin.copyURLFunctionality();
	}

	// ========================================================================
	// TEST 7: Close Action Popup (3-dot menu action - non-destructive)
	// ========================================================================
	@Test(priority = 7,
	      groups = { "Smoke", "Regression" },
	      alwaysRun = true,
	      description = "Close the 3-dot action popup without selecting an action",
	      dependsOnMethods = "ReportLostDogWithoutLogin")
	public void CloseActionPopupFunctionality() throws InterruptedException {
		withoutLogin.clickOnLostDogThreeDotAction();
		withoutLogin.CloseActionPopupFunctionality();
	}

	// ========================================================================
	// TEST 8: Report or Delete (DESTRUCTIVE - LAST in 3-dot tests)
	// ========================================================================
	// First row may be own post (Delete shows) OR other user's post
	// (Report inappropriate shows). handleReportOrDelete() adapts.
	@Test(priority = 8,
	      groups = { "Smoke", "Regression" },
	      alwaysRun = true,
	      description = "Report post as inappropriate OR delete it (depending on ownership)",
	      dependsOnMethods = "ReportLostDogWithoutLogin")
	public void handleReportOrDelete() throws InterruptedException {
		withoutLogin.clickOnLostDogThreeDotAction();
		withoutLogin.handleReportOrDelete();
	}
}