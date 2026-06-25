package org.rahulshettyacademy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.NotificationsPage;
import org.rahulshettyacademy.pageObjects.android.SearchDiscoveryPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Dogpack_SearchAndDiscovery - Search &amp; Discovery TC.
 *
 * <p>Logs in as usual, opens Search from the home screen, handles the
 * (conditional) location permission, searches for "Nyvanne45", and verifies the
 * first result's profile name matches the query.
 *
 * <p>Purely additive - reuses LoginPage / NotificationsPage and the new
 * SearchDiscoveryPage.
 */
public class Dogpack_SearchAndDiscovery extends AndroidBaseTest {

	LoginPage login;
	NotificationsPage notifications; // reused for DismissAllOnboarding()
	SearchDiscoveryPage search;

	private static final String QUERY = "Nyvanne45";

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		login = new LoginPage(driver);
		notifications = new NotificationsPage(driver);
		search = new SearchDiscoveryPage(driver);
	}

	// ================================================================
	// ==========    SETUP - Login (Dog profile)               =======
	// ================================================================

	/** #1 - Login and land on the home screen. */
	@Test(priority = 1, dataProvider = "getSearchLogin",
			groups = { "Smoke", "Regression" })
	public void Login_Search(HashMap<String, String> input)
			throws InterruptedException {
		System.out.println("[INFO]   Logging in: " + input.get("email"));
		login.scrollToLogin();
		login.NavigateToLogin();
		login.setEmailPassword(input.get("email"), input.get("password"));
		login.clickOnLoginSubmit();
		login.CompleteLoginProccess();
		login.HandleCustomDialog(0, 0);
		notifications.DismissAllOnboarding();
		System.out.println("[FLOW]   Logged in - on home screen");
	}

	// ================================================================
	// ==========    SEARCH & DISCOVERY FLOW                    =======
	// ================================================================

	/** #2 - PROFILE SEARCH: search, verify name, follow + unfollow (clubbed). */
	@Test(priority = 2, dependsOnMethods = { "Login_Search" },
			groups = { "Smoke", "Regression" })
	public void ProfileSearch() {
		// open Search (handles conditional location permission)
		search.clickSearch();
		// type the query and submit
		search.enterSearchText(QUERY);
		search.clickEnter();
		// verify the first result's profile name
		String name = search.getDogName();
		Assert.assertEquals(name == null ? null : name.trim(), QUERY,
				"Search result name did not match the query.");
		System.out.println("[ASSERT PASS] Search returned profile '" + QUERY + "'");
		// follow: Follow -> Following
		Assert.assertEquals(search.getFollowButtonText(), "Follow",
				"Initial button text should be 'Follow'.");
		search.clickFollow();
		Assert.assertEquals(search.getFollowingButtonText(), "Following",
				"After Follow, button should read 'Following'.");
		System.out.println("[ASSERT PASS] Follow -> Following");
		// unfollow (with confirm): Following -> Follow
		search.clickUnfollow();
		search.clickConfirmUnfollow();
		Assert.assertEquals(search.getFollowButtonText(), "Follow",
				"After Unfollow, button should read 'Follow' again.");
		System.out.println("[ASSERT PASS] Following -> Follow");
	}

	/** #3 - PARK SEARCH: reset, PARKS tab, search, verify name, follow + unfollow. */
	@Test(priority = 3, dependsOnMethods = { "ProfileSearch" },
			groups = { "Smoke", "Regression" })
	public void ParkSearch() {
		// reset the previous search, then switch to the PARKS tab
		search.clickResetSearch();
		search.clickParksTab();
		// search the same query under PARKS
		search.enterSearchText(QUERY);
		// verify the park name (query + "Park")
		String parkName = search.getParkName();
		Assert.assertEquals(parkName == null ? null : parkName.trim(), QUERY + "Park",
				"Park result name did not match.");
		System.out.println("[ASSERT PASS] Park found: '" + QUERY + "Park'");
		// follow the park: Follow -> (LATER prompt) -> FOLLOWING
		Assert.assertEquals(search.getParkFollowButtonText(), "FOLLOW",
				"Initial park button text should be 'FOLLOW'.");
		search.clickParkFollow();
		search.clickParkLater();
		Assert.assertEquals(search.getParkFollowingText(), "FOLLOWING",
				"After Follow, park button should read 'FOLLOWING'.");
		System.out.println("[ASSERT PASS] Park Follow -> FOLLOWING");
		// unfollow the park (with confirm): FOLLOWING -> FOLLOW
		search.clickParkUnfollow();
		search.clickParkConfirm();
		Assert.assertEquals(search.getParkFollowText(), "FOLLOW",
				"After Unfollow, park button should read 'FOLLOW'.");
		System.out.println("[ASSERT PASS] Park FOLLOWING -> FOLLOW");
	}

	// ================================================================
	// ==========    DATA PROVIDER                              =======
	// ================================================================

	@DataProvider(name = "getSearchLogin")
	public Object[][] getSearchLogin() throws IOException {
		String jsonPath = Paths.get(
				System.getProperty("user.dir"),
				"src", "test", "java", "org", "rahulshettyacademy",
				"testData", "SmokeLoginData.json").toString();
		List<HashMap<String, String>> data = getJsonData(jsonPath);
		return new Object[][] { { data.get(0) } };
	}
}