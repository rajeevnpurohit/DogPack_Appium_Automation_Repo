package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.AiPhotoGenerationPage;
import org.rahulshettyacademy.pageObjects.android.AiPhotoGenerationPage.AccountType;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.ProfileSwitcherPage;
import org.rahulshettyacademy.pageObjects.android.SettingsAndActivityPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_AiPhotoGeneration_Business - AI Photo Generation flow for the
 * BUSINESS entity (Tipper9Business).
 *
 * FLOWS (2 chained scenarios):
 *
 *   SUBSCRIBE CHAIN (no close in the middle):
 *     #4  Open AI screen from Settings (lands on Subscribe view directly,
 *         no separate tab tap needed for business)
 *     #5  Tap Monthly card -> wait for Subscribe Monthly button
 *     #6  Tap "Subscribe Monthly for Rs.430.00" -> dismiss Got it
 *     #7  Tap Annual card -> wait for Subscribe Annual button
 *     #8  Tap "Subscribe Annual for Rs.4,800.00" -> dismiss Got it
 *     #9  Tap close -> verify return to Settings
 *
 *   PURCHASE CHAIN (no close in the middle):
 *     #10 Re-open AI screen from Settings
 *     #11 Tap "Purchase" tab
 *     #12 Tap 10 Pack card -> wait for Purchase 10 Pack button
 *     #13 Tap "Purchase 10 Pack for Rs.110.00" -> dismiss Got it
 *     #14 Tap 50 Pack card -> wait for Purchase 50 Pack button
 *     #15 Tap "Purchase 50 Pack for Rs.420.00" -> dismiss Got it
 *     #16 Tap 200 Pack card -> wait for Purchase 200 Pack button
 *     #17 Tap "Purchase 200 Pack for Rs.1,600.00" -> dismiss Got it
 *     #18 Tap 1000 Pack card (UiScrollable.scrollIntoView inside the
 *         method handles the lazy-mounted card)
 *     #19 Tap "Purchase 1000 Pack for Rs.5,900.00" -> dismiss Got it
 *     #20 Tap close -> verify return to Settings
 *
 * PRICE DIFFERENCES vs DOG:
 *   Only the Monthly Subscribe button price differs (Rs.420 dog / Rs.430
 *   business). All other prices (Annual + four Pack prices) are
 *   IDENTICAL between dog and business. The dog Monthly is left in the
 *   page object's getSubscribeMonthlyBtnXpath() helper, which resolves
 *   based on the AccountType passed at construction time.
 *
 * EXECUTION ORDER (in suite XML):
 *   1. Dogpack_BoostAccount                       (dog Boost)
 *   2. Dogpack_AiPhotoGeneration                  (dog AI)
 *   3. Dogpack_BusinessLogin                      (transitional)
 *   4. Dogpack_BoostAccount_Business              (business Boost)
 *   5. Dogpack_AiPhotoGeneration_Business         (THIS CLASS - business AI)
 */
public class Dogpack_AiPhotoGeneration_Business extends AndroidBaseTest {

    LoginPage login;
    ProfilePage profile;
    ProfileSwitcherPage profileSwitcher;
    SettingsAndActivityPage settings;
    AiPhotoGenerationPage aiBiz;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        profile = new ProfilePage(driver);
        profileSwitcher = new ProfileSwitcherPage(driver);
        settings = new SettingsAndActivityPage(driver);
        // AccountType.BUSINESS -> Monthly Subscribe button XPath
        // resolves to "Subscribe Monthly for Rs.430.00" (not 420)
        aiBiz = new AiPhotoGenerationPage(driver, AccountType.BUSINESS);
    }

    // ================================================================
    // ==========    SETUP CHAIN (login + switch + settings)    =======
    // ================================================================

    @Test(priority = 1, dataProvider = "getBusinessUserLogin",
            groups = { "Smoke", "Regression" })
    public void LoginForBusinessAI(HashMap<String, String> input)
            throws InterruptedException {
        System.out.println("[INFO]   Logging in as shared account: "
                + input.get("email"));
        login.scrollToLogin();
        login.NavigateToLogin();
        login.setEmailPassword(input.get("email"), input.get("password"));
        login.clickOnLoginSubmit();
        login.CompleteLoginProccess();
        login.HandleCustomDialog(0, 0);
        profile.navigateToProfileScreen();
    }

    @Test(priority = 2, dependsOnMethods = { "LoginForBusinessAI" },
            groups = { "Smoke", "Regression" })
    public void SwitchToBusinessForAI() {
        profileSwitcher.SwitchToFirstBusinessProfile();
    }

    @Test(priority = 3, dependsOnMethods = { "SwitchToBusinessForAI" },
            groups = { "Smoke", "Regression" })
    public void NavigatesSettingActivityScreen_AI_Business()
            throws InterruptedException {
        settings.NavigatesToSettingAndActivityScreen();
    }

    // ================================================================
    // ==========    SUBSCRIBE CHAIN (Monthly + Annual)         =======
    // ================================================================

    /** #4 - Open AI screen from Settings. The screen opens directly on
     *       the Subscribe view for business; no separate tab tap is
     *       needed (unlike the dog flow). */
    @Test(priority = 4,
            dependsOnMethods = { "NavigatesSettingActivityScreen_AI_Business" },
            groups = { "Smoke", "Regression" })
    public void NavigateToAIPhotoGenFromMenu_Business() {
        aiBiz.NavigateToAIPhotoGenFromMenu();
    }

    /** #5 - Tap Monthly card -> wait for Subscribe Monthly button. */
    @Test(priority = 5,
            dependsOnMethods = { "NavigateToAIPhotoGenFromMenu_Business" },
            groups = { "Smoke", "Regression" })
    public void SelectMonthlyAIPlanCard_Business() {
        aiBiz.SelectMonthlyAIPlanCard();
    }

    /**
     * #6 - Tap 'Subscribe Monthly for Rs.430.00' -> dismiss Got it.
     *      Price resolves to 430 (not 420) because aiBiz was constructed
     *      with AccountType.BUSINESS.
     */
    @Test(priority = 6,
            dependsOnMethods = { "SelectMonthlyAIPlanCard_Business" },
            groups = { "Smoke", "Regression" })
    public void ClickSubscribeMonthlyAI_Business() {
        aiBiz.ClickSubscribeMonthlyAI();
    }

    /** #7 - Tap Annual card -> wait for Subscribe Annual button. */
    @Test(priority = 7,
            dependsOnMethods = { "ClickSubscribeMonthlyAI_Business" },
            groups = { "Smoke", "Regression" })
    public void SelectAnnualAIPlanCard_Business() {
        aiBiz.SelectAnnualAIPlanCard();
    }

    /** #8 - Tap 'Subscribe Annual for Rs.4,800.00' -> dismiss Got it. */
    @Test(priority = 8,
            dependsOnMethods = { "SelectAnnualAIPlanCard_Business" },
            groups = { "Smoke", "Regression" })
    public void ClickSubscribeAnnualAI_Business() {
        aiBiz.ClickSubscribeAnnualAI();
    }

    /** #9 - Tap close -> verify return to Settings (after Subscribe chain). */
    @Test(priority = 9,
            dependsOnMethods = { "ClickSubscribeAnnualAI_Business" },
            groups = { "Smoke", "Regression" })
    public void CloseAIPhotoGen_Business_Subscribe() {
        aiBiz.CloseAIPhotoGen();
    }

    // ================================================================
    // ==========    PURCHASE CHAIN (10/50/200/1000 Pack)       =======
    // ================================================================

    /** #10 - Re-open AI screen for the Purchase chain. */
    @Test(priority = 10,
            dependsOnMethods = { "CloseAIPhotoGen_Business_Subscribe" },
            groups = { "Smoke", "Regression" })
    public void NavigateToAIPhotoGenFromMenu_Business_Purchase() {
        aiBiz.NavigateToAIPhotoGenFromMenu();
    }

    /** #11 - Tap 'Purchase' tab. */
    @Test(priority = 11,
            dependsOnMethods = { "NavigateToAIPhotoGenFromMenu_Business_Purchase" },
            groups = { "Smoke", "Regression" })
    public void ClickPurchaseTab_Business() {
        aiBiz.ClickPurchaseTab();
    }

    /** #12 - Select 10 Pack card. */
    @Test(priority = 12,
            dependsOnMethods = { "ClickPurchaseTab_Business" },
            groups = { "Smoke", "Regression" })
    public void SelectTenPackCard_Business() {
        aiBiz.SelectTenPackCard();
    }

    /** #13 - Tap 'Purchase 10 Pack for Rs.110.00' -> dismiss Got it. */
    @Test(priority = 13,
            dependsOnMethods = { "SelectTenPackCard_Business" },
            groups = { "Smoke", "Regression" })
    public void ClickPurchase10Pack_Business() {
        aiBiz.ClickPurchase10Pack();
    }

    /** #14 - Select 50 Pack card. */
    @Test(priority = 14,
            dependsOnMethods = { "ClickPurchase10Pack_Business" },
            groups = { "Smoke", "Regression" })
    public void SelectFiftyPackCard_Business() {
        aiBiz.SelectFiftyPackCard();
    }

    /** #15 - Tap 'Purchase 50 Pack for Rs.420.00' -> dismiss Got it. */
    @Test(priority = 15,
            dependsOnMethods = { "SelectFiftyPackCard_Business" },
            groups = { "Smoke", "Regression" })
    public void ClickPurchase50Pack_Business() {
        aiBiz.ClickPurchase50Pack();
    }

    /** #16 - Select 200 Pack card. */
    @Test(priority = 16,
            dependsOnMethods = { "ClickPurchase50Pack_Business" },
            groups = { "Smoke", "Regression" })
    public void SelectTwoHundredPackCard_Business() {
        aiBiz.SelectTwoHundredPackCard();
    }

    /** #17 - Tap 'Purchase 200 Pack for Rs.1,600.00' -> dismiss Got it. */
    @Test(priority = 17,
            dependsOnMethods = { "SelectTwoHundredPackCard_Business" },
            groups = { "Smoke", "Regression" })
    public void ClickPurchase200Pack_Business() {
        aiBiz.ClickPurchase200Pack();
    }

    /**
     * #18 - Select 1000 Pack card. The page-object method internally
     *       uses UiScrollable.scrollIntoView() because the 1000 Pack
     *       card is lazily mounted below the fold.
     */
    @Test(priority = 18,
            dependsOnMethods = { "ClickPurchase200Pack_Business" },
            groups = { "Smoke", "Regression" })
    public void SelectThousandPackCard_Business() {
        aiBiz.SelectThousandPackCard();
    }

    /** #19 - Tap 'Purchase 1000 Pack for Rs.5,900.00' -> dismiss Got it. */
    @Test(priority = 19,
            dependsOnMethods = { "SelectThousandPackCard_Business" },
            groups = { "Smoke", "Regression" })
    public void ClickPurchase1000Pack_Business() {
        aiBiz.ClickPurchase1000Pack();
    }

    /** #20 - Tap close -> verify return to Settings (after Purchase chain). */
    @Test(priority = 20,
            dependsOnMethods = { "ClickPurchase1000Pack_Business" },
            groups = { "Smoke", "Regression" })
    public void CloseAIPhotoGen_Business_Purchase() {
        aiBiz.CloseAIPhotoGen();
    }

    /**
     * Returns the shared tip@yopmail.com credentials from LoginData.json
     * index [0] - same login as all other classes, with the active
     * entity switched to Tipper9Business by SwitchToBusinessForAI.
     */
    @DataProvider(name = "getBusinessUserLogin")
    public Object[][] getBusinessUserLogin() throws IOException {
        String jsonPath = Paths.get(
                System.getProperty("user.dir"),
                "src", "test", "java", "org", "rahulshettyacademy",
                "testData", "LoginData.json").toString();
        List<HashMap<String, String>> data = getJsonData(jsonPath);
        return new Object[][] { { data.get(0) } };
    }
}