package org.rahulshettyacademy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.rahulshettyacademy.pageObjects.android.MapPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class Dogpack_Map extends AndroidBaseTest {

	LoginPage login;
	MapPage map;

	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		login = new LoginPage(driver);
		map = new MapPage(driver);
	}

	@Test(priority = 1, dataProvider = "getDataSuccessfullLogin", groups = { "Smoke", "Regression" })
	public void PrerequestFunctionsforProfile(HashMap<String, String> input) throws InterruptedException {
		login.scrollToLogin();
		login.NavigateToLogin();
		login.setEmailPassword(input.get("email"), input.get("password"));
		login.clickOnLoginSubmit();
		login.CompleteLoginProccess();
		login.HandleCustomDialog(384, 576);
	}
	
	@Test(priority = 2, groups = { "Smoke", "Regression" })
	public void NavigtesToMap() throws InterruptedException {
		map.NavigtesToMap();
	}
	
	@Test(priority = 3, groups = { "Smoke","Regression" })
	public void searchByDynamicLocation() throws InterruptedException {
		map.searchByDynamicLocation();
	}
	
	
	@Test(priority = 4, groups = { "Smoke","Regression" })
	public void searchByCurrentLocation() throws InterruptedException {
		map.searchByCurrentLocation();
	}
	

	@Test(priority = 5, groups = { "Smoke","Regression" })
	public void changeMapTypeToTraffic() throws InterruptedException {
		map.changeMapTypeToTraffic();
	}
	
	@Test(priority = 6, groups = {"Smoke","Regression" })
	public void changeMapTypeToTerrain() throws InterruptedException {
		map.changeMapTypeToTerrain();
	}
	
	@Test(priority = 7, groups = { "Smoke","Regression" })
	public void changeMapTypeToSatellite() throws InterruptedException {
		map.changeMapTypeToSatellite();
	}
	
	@Test(priority = 8, groups = { "Smoke","Regression" })
	public void changeMapTypeToDefault() throws InterruptedException {
		map.changeMapTypeToDefault();
	}
	
	@Test(priority = 9, groups = {  "Regression" })
	public void ListYourBusiness() throws InterruptedException {
		map.ListYourBusiness();
	}
	
	@Test(priority = 10, groups = { "Smoke","Regression" })
	public void SuggestPark() throws InterruptedException {
		map.SuggestPark();
	}
	
	@Test(priority = 11, groups = {"Smoke","Regression" })
	public void SuggestBusiness() throws InterruptedException {
		map.SuggestBusiness();
	}
	
	@Test(priority = 12, groups = { "Smoke", "Regression" })
	public void UnSelectLodgings() throws InterruptedException {
		map.UnSelectLodgings();
	}
	
	
	@Test(priority = 13, groups = { "Smoke", "Regression" })
	public void UnSelectBusiness() throws InterruptedException {
		map.UnSelectBusiness();
	}
	
	
	@Test(priority = 14, groups = { "Smoke", "Regression" })
	public void UnSelectPark() throws InterruptedException {
		map.UnSelectPark();
	}
	
	
	@Test(priority = 15, groups = { "Smoke", "Regression" })
	public void UnSelectDogFriendlyArea() throws InterruptedException {
		map.UnSelectDogFriendlyArea();
	}
	
	@Test(priority = 16, groups = { "Smoke", "Regression" })
	public void clickOnLodgingsAfterSwipeRight() throws InterruptedException {
		map.clickOnLodgingsAfterSwipeRight();
	}
	
	@Test(priority = 17, groups = {  "Smoke", "Regression" })
	public void clickFirstMarkerOrFallbackToLodgings() throws InterruptedException {
		map.clickFirstMarkerOrFallbackToLodgings();
	}
	
	@Test(priority = 18, groups = { "Smoke", "Regression" })
	public void closePopupOfLodginginMap() throws InterruptedException {
		map.searchByCurrentLocation();
		map.searchByCurrentLocation();
	}
	
	@Test(priority = 19, groups = {   "Regression" })
	public void DogBusiness() throws InterruptedException {
		map.DogBusiness();
		
	}
	
	@Test(priority = 20, groups = {  "Smoke","Regression" })
	public void closePopupOfBusinessinMap() throws InterruptedException {
		map.searchByCurrentLocation();
		map.searchByCurrentLocation();
	}
	
	@Test(priority = 21, groups = {  "Regression" })
	public void ParkMap() throws InterruptedException {
		map.ParkMap();
		
	}
	
	@DataProvider
	public Object[][] getDataSuccessfullLogin() throws IOException {
		List<HashMap<String, String>> data = getJsonData(System.getProperty("user.dir")
				+ "//src//test//java//org//rahulshettyacademy//testData//LoginData.json");

		return new Object[][] { { data.get(0) } };
	}

}
