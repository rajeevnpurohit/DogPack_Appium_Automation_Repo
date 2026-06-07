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
 * Dogpack_Marketplace_CartCleanup_Test - STANDALONE test for
 * validating the cart-cleanup deletion flow.
 *
 * Purpose:
 *   Development scaffold for the eventual dog-marketplace cleanup
 *   integration. Tests the cart-cleanup steps in isolation,
 *   without dragging in the full marketplace + payment chain.
 *
 *   Once these steps work reliably here, the DeleteAllCartItems()
 *   method gets composed into the @AfterMethod cleanup of
 *   Dogpack_Marketplace so the suite leaves a clean cart even when
 *   a test fails mid-flow with items already added.
 *
 * NOT INCLUDED IN THE SMOKE SUITE XML. Run on its own via:
 *   mvn test -Dtest=Dogpack_Marketplace_CartCleanup_Test
 *
 * PREREQUISITE: cart must be MANUALLY populated with 1+ items
 * before running. The test assumes cart is non-empty but handles
 * empty-cart gracefully (loop runs 0 times).
 *
 * Flow (14 priorities):
 *   #1  Login as shared account (tip@yopmail.com)
 *   #2  Tap Profile tab (existing ImageView locator)
 *
 *   #3-11  CHANGE LOCATION TO CANADA (mirrors main marketplace
 *          test's location-change chain exactly). Priority 6 is
 *          deliberately skipped to match the main class's gap.
 *
 *   #12 Tap Shop card via the OLD ClickShop() locator (NOT the
 *       new marketplace-view ImageView locator).
 *   #13 Tap cart icon (reuses ClickCartIcon from CartNav_Test work).
 *   #14 DeleteAllCartItems - extract count N, loop N times.
 *
 * No @AfterMethod cleanup - this scaffold validates the cleanup
 * mechanism itself.
 */
public class Dogpack_Marketplace_CartCleanup_Test extends AndroidBaseTest {

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

    // ================================================================
    // ==========    SETUP - login + profile + Canada           =======
    // ================================================================

    /** #1 - Login + navigate to Profile screen. */
    @Test(priority = 1, dataProvider = "getDogUserLogin",
            groups = { "CartCleanup" })
    public void Login_CartCleanup(HashMap<String, String> input)
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

    /**
     * #2 - Tap Profile tab via the existing ClickProfileTabByText
     * locator on MarketplacePage. Idempotent.
     */
    @Test(priority = 2,
            dependsOnMethods = { "Login_CartCleanup" },
            groups = { "CartCleanup" })
    public void ClickProfileTab_CartCleanup() {
        marketplace.ClickProfileTabByText();
    }

    /** #3 - Open Settings & Activity screen via hamburger menu. */
    @Test(priority = 3,
            dependsOnMethods = { "ClickProfileTab_CartCleanup" },
            groups = { "CartCleanup" })
    public void NavigatesSettingActivityScreen_CartCleanup()
            throws InterruptedException {
        settings.NavigatesToSettingAndActivityScreen();
    }

    /** #4 - Tap Account and info row. */
    @Test(priority = 4,
            dependsOnMethods = { "NavigatesSettingActivityScreen_CartCleanup" },
            groups = { "CartCleanup" })
    public void ClickAccountAndInfo_CartCleanup() {
        marketplace.ClickAccountAndInfo();
    }

    /**
     * #5 - Handle the Android system location-permission popup.
     * Mirrors main marketplace test priority 4.
     */
    @Test(priority = 5,
            dependsOnMethods = { "ClickAccountAndInfo_CartCleanup" },
            groups = { "CartCleanup" })
    public void HandleLocationPermissionPopup_CartCleanup() {
        marketplace.HandleLocationPermissionPopup();
    }

    /** #7 - Tap location edit button. */
    @Test(priority = 7,
            dependsOnMethods = { "HandleLocationPermissionPopup_CartCleanup" },
            groups = { "CartCleanup" })
    public void ClickLocationDialogButton_CartCleanup() {
        marketplace.ClickLocationDialogButton();
    }

    /** #8 - Type "Canada" in the location search box. */
    @Test(priority = 8,
            dependsOnMethods = { "ClickLocationDialogButton_CartCleanup" },
            groups = { "CartCleanup" })
    public void EnterLocationSearchText_CartCleanup() {
        marketplace.EnterLocationSearchText();
    }

    /** #9 - Select Canada from the suggestions list. */
    @Test(priority = 9,
            dependsOnMethods = { "EnterLocationSearchText_CartCleanup" },
            groups = { "CartCleanup" })
    public void SelectCanadaSuggestion_CartCleanup() {
        marketplace.SelectCanadaSuggestion();
    }

    /** #10 - Scroll to bottom to reveal UPDATE button. */
    @Test(priority = 10,
            dependsOnMethods = { "SelectCanadaSuggestion_CartCleanup" },
            groups = { "CartCleanup" })
    public void ScrollToBottom_CartCleanup() {
        marketplace.ScrollToBottom();
    }

    /** #11 - Tap UPDATE to commit Canada as the new location. */
    @Test(priority = 11,
            dependsOnMethods = { "ScrollToBottom_CartCleanup" },
            groups = { "CartCleanup" })
    public void ClickUpdate_CartCleanup() {
        marketplace.ClickUpdate();
    }

    /** #12 - Verify Shop card is visible (location-change worked). */
    @Test(priority = 12,
            dependsOnMethods = { "ClickUpdate_CartCleanup" },
            groups = { "CartCleanup" })
    public void AssertShopTextVisible_CartCleanup() {
        marketplace.AssertShopTextVisible();
    }

    // ================================================================
    // ==========    CART CLEANUP FLOW                           ======
    // ================================================================

    /**
     * #13 - Tap Shop card. Uses the OLD ClickShop() locator
     * (marketplace-Shop content-desc), NOT the newer marketplace-view
     * ImageView locator.
     */
    @Test(priority = 13,
            dependsOnMethods = { "AssertShopTextVisible_CartCleanup" },
            groups = { "CartCleanup" })
    public void ClickShop_CartCleanup() {
        marketplace.ClickShop();
    }

    /** #14 - Tap the cart icon to navigate to the cart screen. */
    @Test(priority = 14,
            dependsOnMethods = { "ClickShop_CartCleanup" },
            groups = { "CartCleanup" })
    public void ClickCartIcon_CartCleanup() {
        marketplace.ClickCartIcon();
    }

    /**
     * #15 - Delete all cart items via the N-derived loop. Extracts
     * count from cart-total text, loops exactly N times deleting
     * first available delete button each iteration.
     *
     * Empty cart handled gracefully (loop runs 0 times - passes).
     */
    @Test(priority = 15,
            dependsOnMethods = { "ClickCartIcon_CartCleanup" },
            groups = { "CartCleanup" })
    public void DeleteAllCartItems_CartCleanup() {
        marketplace.DeleteAllCartItems();
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