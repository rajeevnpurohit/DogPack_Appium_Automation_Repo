package org.rahulshettyacademy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.SearchPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_Search - refactored 2025-05-17.
 *
 * Drives 17 tests across 3 phases via the refactored SearchPage:
 *   Phase 1 (Profile): tests 2-6  (5 tests)
 *   Phase 2 (Park):    tests 7-10 (4 tests)
 *   Phase 3 (Business): tests 11-17 (7 tests)
 *
 * Test 1 is the prerequisite login (PrerequestFunctionsforProfile).
 *
 * NOTE: BusinessBlockUser (test 15) is best-effort tolerant due to
 * source-level changes (businessUserdetails screen has no hamburger
 * testID; only Share + Report icons remain).
 */
public class Dogpack_Search extends AndroidBaseTest {

    LoginPage login;
    SearchPage search;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        search = new SearchPage(driver);
    }

    // ============================================================
    // Phase 0: Login prerequisite
    // ============================================================

    @Test(priority = 1, dataProvider = "getDataSuccessfullLogin",
            groups = {"Smoke", "Regression"})
    public void PrerequestFunctionsforProfile(HashMap<String, String> input)
            throws InterruptedException {
        login.scrollToLogin();
        login.NavigateToLogin();
        login.setEmailPassword(input.get("email"), input.get("password"));
        login.clickOnLoginSubmit();
        login.CompleteLoginProccess();
        login.HandleCustomDialog(384, 576);
    }

    // ============================================================
    // Phase 1: Profile (5 tests)
    // ============================================================

    @Test(priority = 2, groups = {"Smoke", "Regression"})
    public void NavigatesToSearchPage() throws InterruptedException {
        search.NavigatesToSearchPage();
    }

    @Test(priority = 3, groups = {"Smoke", "Regression"})
    public void ProfileTabSearchUser() throws InterruptedException {
        search.ProfileTabSearchUser();
    }

    @Test(priority = 4, groups = {"Smoke", "Regression"})
    public void ProfileTabFollowUser() throws InterruptedException {
        search.ProfileTabFollowUser();
    }

    @Test(priority = 5, groups = {"Smoke", "Regression"})
    public void ProfileTabMessageUser() throws InterruptedException {
        search.ProfileTabMessageUser();
    }

    @Test(priority = 6, groups = {"Smoke", "Regression"})
    public void ProfileBlockUser() throws InterruptedException {
        search.ProfileBlockUser();
    }

    // ============================================================
    // Phase 2: Park (4 tests)
    // ============================================================

    @Test(priority = 7, groups = {"Smoke", "Regression"})
    public void SearchParkTab() throws InterruptedException {
        search.SearchParkTab();
    }

    @Test(priority = 8, groups = {"Smoke", "Regression"})
    public void ViewParkGallery() throws InterruptedException {
        search.ViewParkGallery();
    }

    @Test(priority = 9, groups = {"Regression"})
    public void SearchParkDetailScreen() throws InterruptedException {
        search.SearchParkDetailScreen();
    }

    @Test(priority = 10, groups = {"Smoke", "Regression"})
    public void SearchParkDetailAmenities() throws InterruptedException {
        search.SearchParkDetailAmenities();
    }

    // ============================================================
    // Phase 3: Business (7 tests)
    // ============================================================

    @Test(priority = 11, groups = {"Smoke", "Regression"})
    public void SearchBusinessTab() throws InterruptedException {
        search.SearchBusinessTab();
    }

    @Test(priority = 12, groups = {"Smoke", "Regression"})
    public void navigatesToAllTabsInProfile() throws InterruptedException {
        search.navigatesToAllTabsInProfile();
    }

    @Test(priority = 13, groups = {"Smoke", "Regression"})
    public void BusinessAddress() throws InterruptedException {
        search.BusinessAddress();
    }

    @Test(priority = 14, groups = {"Smoke", "Regression"})
    public void BusinessTabMessageUser() throws InterruptedException {
        search.BusinessTabMessageUser();
    }

    @Test(priority = 15, groups = {"Smoke", "Regression"})
    public void BusinessBlockUser() throws InterruptedException {
        search.BusinessBlockUser();
    }

    @Test(priority = 16, groups = {"Smoke", "Regression"})
    public void ReportBusiness() throws InterruptedException {
        search.ReportBusiness();
    }

    @Test(priority = 17, groups = {"Smoke", "Regression"})
    public void ClickOnSubTabsInProfile() throws InterruptedException {
        search.ClickOnSubTabsInProfile();
    }

    @DataProvider
    public Object[][] getDataSuccessfullLogin() throws IOException {
        List<HashMap<String, String>> data = getJsonData(
                System.getProperty("user.dir")
                        + "//src//test//java//org//rahulshettyacademy"
                        + "//testData//LoginData.json");
        return new Object[][]{{data.get(0)}};
    }
}