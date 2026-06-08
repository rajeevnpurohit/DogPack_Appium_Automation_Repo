package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.AiPhotoGenerationPage;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.SettingsAndActivityPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_AiPhotoGeneration - MINIMAL test suite for the
 * "AI photo generations" feature.
 *
 * TEST CASE 1 - Monthly subscription flow:
 *   1. Login -> Profile screen
 *   2. Open Settings and activity
 *   3. Tap "AI photo generations"
 *   4. Tap "Subscribe" tab
 *   5. Tap "Monthly" plan card
 *   6. Tap "Subscribe Monthly for <RUPEE>420.00" -> dismiss "Got it"
 *   7. Tap close (X) and verify return to Settings
 *
 * Tests are chained via dependsOnMethods so a mid-flow failure produces
 * "Failures: 1, Skipped: N" instead of cascade noise.
 */
public class Dogpack_AiPhotoGeneration extends AndroidBaseTest {

    LoginPage login;
    ProfilePage profile;
    SettingsAndActivityPage settings;
    AiPhotoGenerationPage ai;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        profile = new ProfilePage(driver);
        settings = new SettingsAndActivityPage(driver);
        ai = new AiPhotoGenerationPage(driver);
    }

    /** #1 - Login + navigate to Profile screen. */
    @Test(priority = 1, dataProvider = "getDataSuccessfullLogin",
            groups = { "Smoke", "Regression" })
    public void PrerequestFunctionsforAIPhotoGen(HashMap<String, String> input)
            throws InterruptedException {
        login.scrollToLogin();
        login.NavigateToLogin();
        login.setEmailPassword(input.get("email"), input.get("password"));
        login.clickOnLoginSubmit();
        login.CompleteLoginProccess();
        login.HandleCustomDialog(0, 0);
        profile.navigateToProfileScreen();
    }

    /** #2 - Open Settings and activity via hamburger. */
    @Test(priority = 2,
            dependsOnMethods = { "PrerequestFunctionsforAIPhotoGen" },
            groups = { "Smoke", "Regression" })
    public void NavigatesSettingActivityScreen() throws InterruptedException {
        settings.NavigatesToSettingAndActivityScreen();
    }

    /** #3 - Tap "AI photo generations" row. */
    @Test(priority = 3,
            dependsOnMethods = { "NavigatesSettingActivityScreen" },
            groups = { "Smoke", "Regression" })
    public void NavigateToAIPhotoGenFromMenu() {
        ai.NavigateToAIPhotoGenFromMenu();
    }

    /** #4 - Tap the "Subscribe" tab on the AI screen. */
    @Test(priority = 4,
            dependsOnMethods = { "NavigateToAIPhotoGenFromMenu" },
            groups = { "Smoke", "Regression" })
    public void ClickSubscribeTab() {
        ai.ClickSubscribeTab();
    }

    /** #5 - Select the "Monthly" plan card. */
    @Test(priority = 5,
            dependsOnMethods = { "ClickSubscribeTab" },
            groups = { "Smoke", "Regression" })
    public void SelectMonthlyAIPlanCard() {
        ai.SelectMonthlyAIPlanCard();
    }

    /** #6 - Tap Subscribe Monthly for <RUPEE>420 -> dismiss 'Got it'. */
    @Test(priority = 6,
            dependsOnMethods = { "SelectMonthlyAIPlanCard" },
            groups = { "Smoke", "Regression" })
    public void ClickSubscribeMonthlyAI() {
        ai.ClickSubscribeMonthlyAI();
    }

    /** #7 - Tap close (X) and verify return to Settings. */
    @Test(priority = 7,
            dependsOnMethods = { "ClickSubscribeMonthlyAI" },
            groups = { "Smoke", "Regression" })
    public void CloseAIPhotoGen() {
        ai.CloseAIPhotoGen();
    }

    // ================================================================
    // ==========    TEST CASE 2: Annual subscription flow      =======
    // ================================================================
    //
    // FLOW:
    //   #8  Re-open AI screen (we are back at Settings after test #7)
    //   #9  Tap 'Subscribe' tab again (re-open lands on default Purchase)
    //   #10 Tap 'Annual' card -> wait for Subscribe Annual button
    //   #11 Tap 'Subscribe Annual for RUPEE4800.00' -> dismiss 'Got it'
    //   #12 Tap close (X) and verify return to Settings
    //
    // Chain anchor: dependsOnMethods = { "CloseAIPhotoGen" } so test
    // case 2 only runs if test case 1 finished cleanly (we are back at
    // Settings, not stuck somewhere on the AI screen).
    // ================================================================

    /** #8 - Re-open AI screen from Settings (entry for Annual flow). */
    @Test(priority = 8,
            dependsOnMethods = { "CloseAIPhotoGen" },
            groups = { "Smoke", "Regression" })
    public void NavigateToAIPhotoGenFromMenu_Annual() {
        ai.NavigateToAIPhotoGenFromMenu();
    }

    /** #9 - Tap the 'Subscribe' tab again (AI screen re-opens on Purchase). */
    @Test(priority = 9,
            dependsOnMethods = { "NavigateToAIPhotoGenFromMenu_Annual" },
            groups = { "Smoke", "Regression" })
    public void ClickSubscribeTab_Annual() {
        ai.ClickSubscribeTab();
    }

    /** #10 - Select the 'Annual' plan card. */
    @Test(priority = 10,
            dependsOnMethods = { "ClickSubscribeTab_Annual" },
            groups = { "Smoke", "Regression" })
    public void SelectAnnualAIPlanCard() {
        ai.SelectAnnualAIPlanCard();
    }

    /** #11 - Tap Subscribe Annual for RUPEE4800.00 -> dismiss 'Got it'. */
    @Test(priority = 11,
            dependsOnMethods = { "SelectAnnualAIPlanCard" },
            groups = { "Smoke", "Regression" })
    public void ClickSubscribeAnnualAI() {
        ai.ClickSubscribeAnnualAI();
    }

    /** #12 - Tap close (X) and verify return to Settings (annual close). */
    @Test(priority = 12,
            dependsOnMethods = { "ClickSubscribeAnnualAI" },
            groups = { "Smoke", "Regression" })
    public void CloseAIPhotoGen_Annual() {
        ai.CloseAIPhotoGen();
    }

    // ================================================================
    // ==========    TEST CASE 3: Purchase 10 Pack flow         =======
    // ================================================================
    //
    // FLOW:
    //   #13 Re-open AI screen (we are back at Settings after test #12)
    //   #14 Tap 'Purchase' tab to switch from Subscribe to Purchase
    //   #15 Tap '10 Pack' card -> wait for Purchase 10 Pack button
    //   #16 Tap 'Purchase 10 Pack for RUPEE110.00' -> dismiss 'Got it'
    //   #17 Tap close (X) and verify return to Settings
    //
    // Chain anchor: dependsOnMethods = { "CloseAIPhotoGen_Annual" } so
    // test case 3 only runs if test case 2 (Annual) finished cleanly.
    // ================================================================

    /** #13 - Re-open AI screen from Settings (entry for Purchase flow). */
    @Test(priority = 13,
            dependsOnMethods = { "CloseAIPhotoGen_Annual" },
            groups = { "Smoke", "Regression" })
    public void NavigateToAIPhotoGenFromMenu_Purchase() {
        ai.NavigateToAIPhotoGenFromMenu();
    }

    /** #14 - Tap the 'Purchase' tab on the AI screen. */
    @Test(priority = 14,
            dependsOnMethods = { "NavigateToAIPhotoGenFromMenu_Purchase" },
            groups = { "Smoke", "Regression" })
    public void ClickPurchaseTab() {
        ai.ClickPurchaseTab();
    }

    /** #15 - Select the '10 Pack' card. */
    @Test(priority = 15,
            dependsOnMethods = { "ClickPurchaseTab" },
            groups = { "Smoke", "Regression" })
    public void SelectTenPackCard() {
        ai.SelectTenPackCard();
    }

    /** #16 - Tap Purchase 10 Pack for RUPEE110.00 -> dismiss 'Got it'. */
    @Test(priority = 16,
            dependsOnMethods = { "SelectTenPackCard" },
            groups = { "Smoke", "Regression" })
    public void ClickPurchase10Pack() {
        ai.ClickPurchase10Pack();
    }

    /** #17 - Tap close (X) and verify return to Settings (purchase close). */
    @Test(priority = 17,
            dependsOnMethods = { "ClickPurchase10Pack" },
            groups = { "Smoke", "Regression" })
    public void CloseAIPhotoGen_Purchase() {
        ai.CloseAIPhotoGen();
    }

    // ================================================================
    // ==========    TEST CASE 4: Purchase 50 Pack flow         =======
    // ================================================================
    //
    // FLOW:
    //   #18 Re-open AI screen (back at Settings after test #17)
    //   #19 Tap 'Purchase' tab again
    //   #20 Tap '50 Pack' card -> wait for Purchase 50 Pack button
    //   #21 Tap 'Purchase 50 Pack for RUPEE420.00' -> dismiss 'Got it'
    //   #22 Tap close (X) and verify return to Settings
    //
    // Chain anchor: dependsOnMethods = { "CloseAIPhotoGen_Purchase" }
    // so test case 4 only runs if test case 3 (10 Pack) finished
    // cleanly.
    // ================================================================

    /** #18 - Re-open AI screen from Settings (entry for 50 Pack flow). */
    @Test(priority = 18,
            dependsOnMethods = { "CloseAIPhotoGen_Purchase" },
            groups = { "Smoke", "Regression" })
    public void NavigateToAIPhotoGenFromMenu_50Pack() {
        ai.NavigateToAIPhotoGenFromMenu();
    }

    /** #19 - Tap the 'Purchase' tab again. */
    @Test(priority = 19,
            dependsOnMethods = { "NavigateToAIPhotoGenFromMenu_50Pack" },
            groups = { "Smoke", "Regression" })
    public void ClickPurchaseTab_50Pack() {
        ai.ClickPurchaseTab();
    }

    /** #20 - Select the '50 Pack' card. */
    @Test(priority = 20,
            dependsOnMethods = { "ClickPurchaseTab_50Pack" },
            groups = { "Smoke", "Regression" })
    public void SelectFiftyPackCard() {
        ai.SelectFiftyPackCard();
    }

    /** #21 - Tap Purchase 50 Pack for RUPEE420.00 -> dismiss 'Got it'. */
    @Test(priority = 21,
            dependsOnMethods = { "SelectFiftyPackCard" },
            groups = { "Smoke", "Regression" })
    public void ClickPurchase50Pack() {
        ai.ClickPurchase50Pack();
    }

    /** #22 - Tap close (X) and verify return to Settings (50 Pack close). */
    @Test(priority = 22,
            dependsOnMethods = { "ClickPurchase50Pack" },
            groups = { "Smoke", "Regression" })
    public void CloseAIPhotoGen_50Pack() {
        ai.CloseAIPhotoGen();
    }

    // ================================================================
    // ==========    TEST CASE 5: Purchase 200 Pack flow        =======
    // ================================================================
    //
    // FLOW:
    //   #23 Re-open AI screen (back at Settings after test #22)
    //   #24 Tap 'Purchase' tab again
    //   #25 Tap '200 Pack' card -> wait for Purchase 200 Pack button
    //   #26 Tap 'Purchase 200 Pack for RUPEE1,600.00' -> dismiss 'Got it'
    //   #27 Tap close (X) and verify return to Settings
    //
    // Chain anchor: dependsOnMethods = { "CloseAIPhotoGen_50Pack" }
    // ================================================================

    /** #23 - Re-open AI screen from Settings (entry for 200 Pack flow). */
    @Test(priority = 23,
            dependsOnMethods = { "CloseAIPhotoGen_50Pack" },
            groups = { "Smoke", "Regression" })
    public void NavigateToAIPhotoGenFromMenu_200Pack() {
        ai.NavigateToAIPhotoGenFromMenu();
    }

    /** #24 - Tap the 'Purchase' tab again. */
    @Test(priority = 24,
            dependsOnMethods = { "NavigateToAIPhotoGenFromMenu_200Pack" },
            groups = { "Smoke", "Regression" })
    public void ClickPurchaseTab_200Pack() {
        ai.ClickPurchaseTab();
    }

    /** #25 - Select the '200 Pack' card. */
    @Test(priority = 25,
            dependsOnMethods = { "ClickPurchaseTab_200Pack" },
            groups = { "Smoke", "Regression" })
    public void SelectTwoHundredPackCard() {
        ai.SelectTwoHundredPackCard();
    }

    /** #26 - Tap Purchase 200 Pack for RUPEE1,600.00 -> dismiss 'Got it'. */
    @Test(priority = 26,
            dependsOnMethods = { "SelectTwoHundredPackCard" },
            groups = { "Smoke", "Regression" })
    public void ClickPurchase200Pack() {
        ai.ClickPurchase200Pack();
    }

    /** #27 - Tap close (X) and verify return to Settings (200 Pack close). */
    @Test(priority = 27,
            dependsOnMethods = { "ClickPurchase200Pack" },
            groups = { "Smoke", "Regression" })
    public void CloseAIPhotoGen_200Pack() {
        ai.CloseAIPhotoGen();
    }

    // ================================================================
    // ==========    TEST CASE 6: Purchase 1000 Pack flow       =======
    // ================================================================
    //
    // FLOW:
    //   #28 Re-open AI screen (back at Settings after test #27)
    //   #29 Tap 'Purchase' tab again
    //   #30 Tap '1000 Pack' card -> wait for Purchase 1000 Pack button
    //   #31 Tap 'Purchase 1000 Pack for RUPEE5,900.00' -> dismiss 'Got it'
    //   #32 Tap close (X) and verify return to Settings
    //
    // Chain anchor: dependsOnMethods = { "CloseAIPhotoGen_200Pack" }
    // ================================================================

    /** #28 - Re-open AI screen from Settings (entry for 1000 Pack flow). */
    @Test(priority = 28,
            dependsOnMethods = { "CloseAIPhotoGen_200Pack" },
            groups = { "Smoke", "Regression" })
    public void NavigateToAIPhotoGenFromMenu_1000Pack() {
        ai.NavigateToAIPhotoGenFromMenu();
    }

    /** #29 - Tap the 'Purchase' tab again. */
    @Test(priority = 29,
            dependsOnMethods = { "NavigateToAIPhotoGenFromMenu_1000Pack" },
            groups = { "Smoke", "Regression" })
    public void ClickPurchaseTab_1000Pack() {
        ai.ClickPurchaseTab();
    }

    /** #30 - Select the '1000 Pack' card. */
    @Test(priority = 30,
            dependsOnMethods = { "ClickPurchaseTab_1000Pack" },
            groups = { "Smoke", "Regression" })
    public void SelectThousandPackCard() {
        ai.SelectThousandPackCard();
    }

    /** #31 - Tap Purchase 1000 Pack for RUPEE5,900.00 -> dismiss 'Got it'. */
    @Test(priority = 31,
            dependsOnMethods = { "SelectThousandPackCard" },
            groups = { "Smoke", "Regression" })
    public void ClickPurchase1000Pack() {
        ai.ClickPurchase1000Pack();
    }

    /** #32 - Tap close (X) and verify return to Settings (1000 Pack close). */
    @Test(priority = 32,
            dependsOnMethods = { "ClickPurchase1000Pack" },
            groups = { "Smoke", "Regression" })
    public void CloseAIPhotoGen_1000Pack() {
        ai.CloseAIPhotoGen();
    }

    // ================================================================
    @DataProvider
    public Object[][] getDataSuccessfullLogin() throws IOException {
        String jsonPath = Paths.get(
                System.getProperty("user.dir"),
                "src", "test", "java", "org", "rahulshettyacademy",
                "testData", "SmokeLoginData.json").toString();
        List<HashMap<String, String>> data = getJsonData(jsonPath);
        return new Object[][] { { data.get(0) } };
    }
}
