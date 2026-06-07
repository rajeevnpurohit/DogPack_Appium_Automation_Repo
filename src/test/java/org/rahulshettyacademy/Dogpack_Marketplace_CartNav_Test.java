package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.MarketplacePage;
import org.rahulshettyacademy.pageObjects.android.ProfilePage;
import org.rahulshettyacademy.pageObjects.android.SettingsAndActivityPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_Marketplace_CartNav_Test - STANDALONE test for navigating to
 * the cart screen.
 *
 * Purpose:
 *   Development scaffold for the cart-cleanup feature. This class
 *   exists to test ONLY the navigate-to-cart step in isolation,
 *   without dragging in the full marketplace browse+checkout chain
 *   or the @AfterMethod India-revert cleanup.
 *
 *   Once cart-cleanup is fully implemented and merged into the main
 *   cleanup function, this standalone test class can be deleted or
 *   left in place as a regression sanity check.
 *
 * NOT INCLUDED IN THE SMOKE SUITE XML by default. Run on its own via:
 *   mvn test -Dtest=Dogpack_Marketplace_CartNav_Test
 *
 * Flow (5 priorities):
 *   #1  Login as shared account (tip@yopmail.com)
 *   #2  Navigate Profile -> Settings via hamburger
 *   #3  Account and info -> location row -> "Canada" -> UPDATE
 *   #4  Assert Shop visible, tap Shop, tap cart icon
 *   #5  Assert we landed on My cart screen
 *
 * Locator source for cart icon:
 *   testID="mktplace_go_to_cart" - confirmed in React Native source at
 *   src/screen/Marketplace/components/Header.tsx
 */
public class Dogpack_Marketplace_CartNav_Test extends AndroidBaseTest {

    LoginPage login;
    ProfilePage profile;
    SettingsAndActivityPage settings;
    MarketplacePage marketplace;

    @BeforeClass(alwaysRun = true)
    public void setUpp() {
        login = new LoginPage(driver);
        profile = new ProfilePage(driver);
        settings = new SettingsAndActivityPage(driver);
        marketplace = new MarketplacePage(driver);
    }

    /** #1 - Login + navigate to Profile screen. */
    @Test(priority = 1, dataProvider = "getDogUserLogin",
            groups = { "CartNav" })
    public void Login_CartNav(HashMap<String, String> input)
            throws InterruptedException {
        System.out.println("[INFO]   Logging in: " + input.get("email"));
        login.scrollToLogin();
        login.NavigateToLogin();
        login.setEmailPassword(input.get("email"), input.get("password"));
        login.clickOnLoginSubmit();
        login.CompleteLoginProccess();
        login.HandleCustomDialog(0, 0);
        profile.navigateToProfileScreen();
    }

    /** #2 - Open Settings via hamburger. */
    @Test(priority = 2,
            dependsOnMethods = { "Login_CartNav" },
            groups = { "CartNav" })
    public void NavigateToSettings_CartNav() throws InterruptedException {
        settings.NavigatesToSettingAndActivityScreen();
    }

    /**
     * #3 - Full location-change-to-Canada flow folded into one test
     * for brevity. Failures here surface as this test failing
     * instead of cascading - the cart-icon test below doesn't run
     * if Canada-setup fails.
     */
    @Test(priority = 3,
            dependsOnMethods = { "NavigateToSettings_CartNav" },
            groups = { "CartNav" })
    public void ChangeLocationToCanada_CartNav() {
        marketplace.ClickAccountAndInfo();
        marketplace.HandleLocationPermissionPopup();
        marketplace.ClickLocationDialogButton();
        marketplace.EnterLocationSearchText();   // default "Canada"
        marketplace.SelectCanadaSuggestion();
        marketplace.ScrollToBottom();
        marketplace.ClickUpdate();
    }

    /** #4 - Assert Shop visible, tap Shop, tap cart icon. */
    @Test(priority = 4,
            dependsOnMethods = { "ChangeLocationToCanada_CartNav" },
            groups = { "CartNav" })
    public void TapShopAndCartIcon_CartNav() {
        marketplace.AssertShopTextVisible();
        marketplace.ClickShop();
        marketplace.ClickCartIcon();
    }

    /**
     * #5 - Assert the My cart screen header is visible after tapping
     * the cart icon. Uses the existing AssertHeaderTitle("My cart")
     * which we know is the substring-contains pattern.
     */
    @Test(priority = 5,
            dependsOnMethods = { "TapShopAndCartIcon_CartNav" },
            groups = { "CartNav" })
    public void AssertOnCartScreen_CartNav() {
        marketplace.AssertHeaderTitle("My cart");
    }

    @DataProvider(name = "getDogUserLogin")
    public Object[][] getDogUserLogin() throws IOException {
        String jsonPath = Paths.get(
                System.getProperty("user.dir"),
                "src", "test", "java", "org", "rahulshettyacademy",
                "testData", "LoginData.json").toString();
        List<HashMap<String, String>> data = getJsonData(jsonPath);
        return new Object[][] { { data.get(0) } };
    }
}
