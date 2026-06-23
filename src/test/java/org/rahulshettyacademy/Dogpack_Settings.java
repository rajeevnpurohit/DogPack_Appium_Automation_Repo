package org.rahulshettyacademy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.SettingsAndActivityPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_Settings - Test suite for the "Settings and activity" screen.
 *
 * ================================================================
 *  CURRENT MODE: FAST ITERATION (May 23)
 * ================================================================
 *  Target this run: validate BadgesYouCanEarn (#10) fix + verify
 *  batch 2 (#11-15) work end-to-end. Previously proven PASS tests
 *  (#3-#9) are TEMPORARILY DISABLED to cut iteration time from
 *  ~30 minutes to ~5-7 minutes.
 *
 *  ACTIVE: 1, 2, 10, 11, 12, 13, 14, 15  (8 tests)
 *  SKIPPED THIS RUN: 3, 4, 5, 6, 7, 8, 9
 *  PERMANENTLY DISABLED: 16-34, 99 (will enable batch-by-batch)
 *
 *  Independence verified for active set:
 *   - #10 (Badges) - opens its own screen, returns via header back
 *   - #11 (Refer)  - opens Refer, share sheet, returns via BACK
 *   - #12 (ChangePassword) - opens screen, fills fields, submits,
 *                            app auto-returns on success toast
 *   - #13 (blockUser) - opens BlockedUser screen, returns via BACK
 *   - #14 (NotifDisable) - scrolls to Notifications row (yes, in
 *                          PREFERENCES section below the fold -
 *                          scrollToTextSafe handles it)
 *   - #15 (NotifEnable) - same row, just flips back ON
 *
 *  None of #11-15 depend on state from #3-9. All start from Settings
 *  screen root after their own navigation reset via ensureOnSettings.
 *
 *  TO RESTORE FULL BATCH 1: flip `enabled = false` to `enabled = true`
 *  on priorities 3-9 (search for "FAST-ITER:DISABLED" comments).
 *
 * ================================================================
 *  PRIORITY -> METHOD mapping (35 + terminal):
 * ================================================================
 *   Priority   Method                                   Status
 *   --------   --------------------------------------   ----------
 *      1       PrerequestFunctionsforSettings           ACTIVE
 *      2       NavigatesSettingActivityScreen           ACTIVE
 *      3       AccountInfoFunctionality                 SKIPPED (proven PASS)
 *      4       MyParkFunctionality                      SKIPPED (proven PASS)
 *      5       SaveMediaFunctionality                   SKIPPED (proven PASS)
 *      6       BusinessIfollowFunctionality             SKIPPED (proven PASS)
 *      7       myReviews                                SKIPPED (proven PASS)
 *      8       LoveDogPackRateUsFunctionality           SKIPPED (proven PASS)
 *      9       SearchFeedByLocationFunctionality        SKIPPED (proven PASS)
 *     10       BadgesYouCanEarnFunctionality            ACTIVE (fix verification)
 *     11       ReferAndEarnFunctionality                ACTIVE
 *     12       ChangePassFunctionality                  ACTIVE
 *     13       blockUser                                ACTIVE
 *     14       NotificationSettingDesibledFunctionality ACTIVE
 *     15       NotificationSettingEnabledFunctionality  ACTIVE
 *     16       DarkModeOnFunctionality                  DISABLED (debug LostDog)
 *     17       DarkModeOFFFunctionality                 DISABLED (debug LostDog)
 *     18       AutoPlayVideoFunctionality               DISABLED (debug LostDog)
 *     19       HapiticsFunctionality                    DISABLED (debug LostDog)
 *     20       UnitsFunctionality                       DISABLED (debug LostDog)
 *     21       ChangeLanguage                           DISABLED (debug LostDog)
 *     22       SuggestPark                              DISABLED (deferred)
 *     23       shopGearFunctionality                    DISABLED (debug LostDog)
 *     24       LostDogFunctionality                     ACTIVE (debugging)
 *     25       RedeemFunctionality                      DISABLED (debug LostDog)
 *     26       NavigatesToBlog                          DISABLED (debug LostDog)
 *     27       NavigatesToDogBreeds                     DISABLED (debug LostDog)
 *     28-34    FAQ / Terms / Privacy / utils / createDog DISABLED (batch 4B+)
 *     99       LogoutFunctionality                      DISABLED (terminal)
 */
public class Dogpack_Settings extends AndroidBaseTest {

    LoginPage login;
    ProfilePage profile;
    SettingsAndActivityPage Setting;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        // CRITICAL (May 23 v4): Clear stale screenshots from previous
        // runs BEFORE the listener generates fresh ones. Without this,
        // disabled tests' .png files from earlier full-suite runs stay
        // in reports/ folder. The Extent Report HTML might still list
        // those disabled tests as having screenshots even though they
        // didn't run this iteration - confusing the tester about which
        // tests actually executed.
        //
        // Behavior:
        //   - Deletes all *.png files in reports/ folder
        //   - Keeps index.html (will be overwritten by extent.flush())
        //   - Safe if reports/ folder doesn't exist (no-op)
        cleanReportsFolder();

        login = new LoginPage(driver);
        profile = new ProfilePage(driver);
        Setting = new SettingsAndActivityPage(driver);
    }

    /**
     * Clears stale .png screenshots from reports/ folder so the
     * Extent Report only shows screenshots from THIS run.
     */
    private void cleanReportsFolder() {
        try {
            String reportsDir = Paths.get(System.getProperty("user.dir"),
                    "reports").toString();
            File dir = new File(reportsDir);
            if (!dir.exists() || !dir.isDirectory()) {
                System.out.println("[FLOW] No reports/ folder to clean "
                        + "(will be created on first screenshot)");
                return;
            }
            File[] pngs = dir.listFiles((d, name) -> name.endsWith(".png"));
            int count = 0;
            if (pngs != null) {
                for (File png : pngs) {
                    if (png.delete()) count++;
                }
            }
            System.out.println("[FLOW] Cleaned " + count + " stale "
                    + ".png screenshot(s) from previous run(s)");
        } catch (Exception e) {
            System.out.println("[WARN] Could not clean reports folder: "
                    + e.getMessage().split("\n")[0]);
        }
    }

    // ================================================================
    // ==========    LOGIN + ENTRY  (priorities 1-2)            =======
    // ================================================================

    /** #1 - Pre-requisite: log in and land on the Profile screen. */
    @Test(priority = 1, dataProvider = "getDataSuccessfullLogin",
            groups = { "Smoke", "Regression" })
    public void PrerequestFunctionsforSettings(HashMap<String, String> input)
            throws InterruptedException {
        login.scrollToLogin();
        login.NavigateToLogin();
        login.setEmailPassword(input.get("email"), input.get("password"));
        login.clickOnLoginSubmit();
        login.CompleteLoginProccess();
        login.HandleCustomDialog(0, 0);
        profile.navigateToProfileScreen();
    }

    /** #2 - Entry: open Settings and activity screen via hamburger. */
    @Test(priority = 2, groups = { "Smoke", "Regression" })
    public void NavigatesSettingActivityScreen() throws InterruptedException {
        Setting.NavigatesToSettingAndActivityScreen();
    }

    // ================================================================
    // ==========  FAST-ITER:DISABLED  (priorities 3-9)         =======
    // ==========  Already PASS on prior runs. Skip for speed   =======
    // ==========  during batch 2 verification. To re-enable    =======
    // ==========  flip enabled = false -> true.                =======
    // ================================================================

    /** #3 (UI #1) - "Your account" -> Account and info */
    @Test(priority = 3,  groups = { "Smoke", "Regression" })
    public void AccountInfoFunctionality() throws InterruptedException {
        Setting.AccountInfo();
    }

    /** #4 (UI #5) - "Your account" -> My Parks */
    @Test(priority = 4,  groups = { "Regression" })
    public void MyParkFunctionality() throws InterruptedException {
        Setting.MyPark();
    }

    /** #5 (UI #7) - "Your account" -> Saved media */
    @Test(priority = 5,  groups = { "Regression" })
    public void SaveMediaFunctionality() throws InterruptedException {
        Setting.SaveMedia();
    }

    /** #6 (UI #8) - "Your account" -> Businesses I Follow */
    @Test(priority = 6,  groups = { "Regression" })
    public void BusinessIfollowFunctionality() throws InterruptedException {
        Setting.BusinessIfollow();
    }

    /** #7 (UI #9) - "Your account" -> My Reviews */
    @Test(priority = 7,  groups = { "Regression" })
    public void myReviews() throws InterruptedException {
        Setting.myReview();
    }

    /** #8 (UI #10) - "Your account" -> Love DogPack? Rate us */
    @Test(priority = 8,  groups = { "Regression" })
    public void LoveDogPackRateUsFunctionality() throws InterruptedException {
        Setting.LoveDogPackRateUs();
    }

    /** #9 (UI #11) - "Your account" -> Search the Feed by Location */
    @Test(priority = 9,  groups = { "Regression" })
    public void SearchFeedByLocationFunctionality() throws InterruptedException {
        Setting.SearchFeedByLocation();
    }

    // ================================================================
    // ==========  ACTIVE TARGETS  (priorities 10-15)           =======
    // ================================================================

    /**
     * #10 (UI #12) - "Your account" -> Badges You Can Earn.
     *
     * DISABLED (May 23): User observed app going to background on this
     * test. Root cause analysis (in conversation history) identified
     * upstream issues - possible Badges API failure for this test
     * account causing screen to render in unstable state. Re-enable
     * after debugging Badges screen rendering.
     */
    @Test(priority = 10,  groups = { "Smoke", "Regression" })
    public void BadgesYouCanEarnFunctionality() throws InterruptedException {
        Setting.BadgesYouCanEarn();
    }

    /** #11 (UI #13) - "Your account" -> Refer Friends and Earn Treats */
    @Test(priority = 11,  groups = { "Regression" })
    public void ReferAndEarnFunctionality() throws InterruptedException {
        Setting.ReferAndEarn();
    }

    /**
     * #12 (UI #14) - "Your account" -> Change Password.
     * Moved FROM top TO position #14 in new UI build.
     */
    @Test(priority = 12,  groups = { "Regression" })
    public void ChangePassFunctionality() throws InterruptedException {
        Setting.ChangePassword();
    }

    /** #13 (UI #15) - "Your account" -> Blocked Users */
    @Test(priority = 13,  groups = { "Smoke", "Regression" })
    public void blockUser() throws InterruptedException {
        Setting.blockUser();
    }

    /**
     * #14 (UI #16) - Preferences -> Notifications (disable phase).
     * Requires scrollToSettingsItem("Notifications") since the row
     * is below the fold in PREFERENCES section. Independent of #3-9.
     */
    @Test(priority = 14,  groups = { "Smoke", "Regression" })
    public void NotificationSettingDesibledFunctionality() throws InterruptedException {
        Setting.NotificationSettingDesibled();
    }

    /** #15 (UI #16) - Preferences -> Notifications (re-enable phase) */
    @Test(priority = 15,  groups = { "Smoke", "Regression" })
    public void NotificationSettingEnabledFunctionality() throws InterruptedException {
        Setting.NotificationSettingEnabled();
    }

    // ================================================================
    // ==========    BATCH 3 - DISABLED (priorities 16-21)      =======
    // ==========    Enable after batch 2 is green              =======
    // ================================================================

    /** #16 (UI #17) - Dark Mode ON */
    @Test(priority = 16,  groups = { "Smoke", "Regression" })
    public void DarkModeOnFunctionality() throws InterruptedException {
        Setting.DarkModeOn();
    }

    /** #17 (UI #17) - Dark Mode OFF */
    @Test(priority = 17,  groups = { "Smoke", "Regression" })
    public void DarkModeOFFFunctionality() throws InterruptedException {
        Setting.DarkModeOFF();
    }

    /** #18 (UI #18) - Auto-Play Videos (flip + restore) */
    @Test(priority = 18,  groups = { "Smoke", "Regression" })
    public void AutoPlayVideoFunctionality() throws InterruptedException {
        Setting.AutoPlayVideo();
        Setting.AutoPlayVideo();
    }

    /** #19 (UI #19) - Haptics (double-tap inside method) */
    @Test(priority = 19,  groups = { "Smoke", "Regression" })
    public void HapiticsFunctionality() throws InterruptedException {
        Setting.Hapitics();
    }

    /** #20 (UI #20) - Units (Km->Mi then Mi->Km) */
    @Test(priority = 20,  groups = { "Smoke", "Regression" })
    public void UnitsFunctionality() throws InterruptedException {
        Setting.UnitsKmtoMiles();
        Setting.UnitsMilestoKm();
    }

    /** #21 (UI #21) - Language (cycles 8 locales) */
    @Test(priority = 21,  groups = { "Smoke", "Regression" })
    public void ChangeLanguage() throws InterruptedException {
        Setting.ChangeLanguage();
    }

    // ================================================================
    // ==========    BATCH 4 - DISABLED (priorities 22-34)      =======
    // ================================================================

    /** #22 (UI #22) - Suggest a pin */
    @Test(priority = 22,  groups = { "Regression" })
    public void SuggestPark() throws InterruptedException {
        Setting.SuggestPark();
    }

    /** #23 (UI #23) - Shop DogPack Marketplace (WebView) */
    @Test(priority = 23,  groups = { "Regression" })
    public void shopGearFunctionality() throws InterruptedException {
        Setting.shopGearFunctionality();
    }

    /** #24 (UI #24) - Lost & Found -> Report A Lost Dog */
    @Test(priority = 24,  groups = { "Regression" })
    public void LostDogFunctionality() throws InterruptedException {
        Setting.LostDog();
    }

    /** #25 (UI #25) - Redeem */
    @Test(priority = 25, groups = { "Regression" })
    public void RedeemFunctionality() throws InterruptedException {
        Setting.Redeem();
    }

    /** #26 (UI #26) - Blog (WebView) */
    @Test(priority = 26, groups = { "Regression" })
    public void NavigatesToBlog() throws InterruptedException {
        Setting.NavigatesToBlog();
    }

    /** #27 (UI #27) - Dog Breeds (WebView) */
    @Test(priority = 27, groups = { "Regression" })
    public void NavigatesToDogBreeds() throws InterruptedException {
        Setting.NavigatesToDogBreeds();
    }

    /** #28 (UI #28) - FAQ (WebView) */
    @Test(priority = 28, groups = { "Regression" })
    public void FAQFunctionality() throws InterruptedException {
        Setting.FAQ();
    }

    /** #29 (UI #30) - Terms And Conditions (WebView) */
    @Test(priority = 29, groups = { "Regression" })
    public void NavigatesToTermsAndCondition() throws InterruptedException {
        Setting.NavigatesToTermsAndCondition();
    }

    /** #30 (UI #31) - Privacy Policy (WebView) */
    @Test(priority = 30, groups = { "Regression" })
    public void NavigatesToPrivacyPolicy() throws InterruptedException {
        Setting.NavigatesToPrivacyPolicy();
    }

    /** #31 - Auto-Play info icon (menu-ap_vi). */
    @Test(priority = 31, groups = { "Regression" })
    public void clickOniIcon() throws InterruptedException {
        Setting.clickOniIcon();
    }

    /** #32 - Utility: scroll to Language row. */
    @Test(priority = 32, groups = { "Regression" })
    public void scrollToLanguageFunctionality() throws InterruptedException {
        Setting.scrollToLanguage();
    }

    /** #33 - Utility: scroll to Logout footer. */
    @Test(priority = 33, groups = { "Regression" })
    public void scrollToLogoutFunctionality() throws InterruptedException {
        Setting.scrollToLogout();
    }

    /** #34 - Footer "Add new account" -> add a new dog profile. */
    @Test(priority = 34, groups = { "Regression" })
    public void createNewDogProfile() throws InterruptedException {
        Setting.createNewDogProfile();
    }

    // ================================================================
    // ==========       TERMINAL TEST  -  DISABLED              =======
    // ================================================================
    /**
     * WARNING: enabled = false BY DESIGN. Terminates session - all
     * subsequent tests would fail. Only enable for full e2e suite.
     */
    @Test(priority = 99, enabled = false, groups = { "Regression" })
    public void LogoutFunctionality() throws InterruptedException {
        Setting.Logout();
    }

    // ================================================================
    // ==========           DATA PROVIDERS                       ======
    // ================================================================

    @DataProvider
    public Object[][] getDataSuccessfullLogin() throws IOException {
        String jsonPath = Paths.get(
                System.getProperty("user.dir"),
                "src", "test", "java", "org", "rahulshettyacademy",
                "testData", "LoginData.json").toString();

        List<HashMap<String, String>> data = getJsonData(jsonPath);

        return new Object[][] { { data.get(0) } };
    }
}