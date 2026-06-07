package org.rahulshettyacademy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.rahulshettyacademy.TestUtils.AndroidBaseTest;
import org.rahulshettyacademy.pageObjects.android.LoginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class dogpack_Login extends AndroidBaseTest {

	LoginPage login;
	
	
	@BeforeClass(alwaysRun = true)
	public void setUpp() {
		login = new LoginPage(driver);
		
	}

	@Test(priority = 1, groups = { "Smoke", "Regression" })
	public void NavigtesToLoginScreen() {
		login.scrollToLogin();
		login.NavigateToLogin();
	}
	
	
	@Test(priority = 2, dataProvider = "getDataEmptyCredLogin", groups = { "Smoke","Regression" })
	public void EmptyCredLogin(HashMap<String, String> input) throws InterruptedException {
		login.setEmailPassword(input.get("email"), input.get("password"));
		login.clickOnLoginSubmit();
	
	}
	
	/*
	@Test(priority = 3, dataProvider = "getDataLoginInvalidCred", groups = { "Smoke","Regression" })
	public void LoginInvalidCred(HashMap<String, String> input) throws InterruptedException {
		
		login.setEmailPassword(input.get("email"), input.get("password"));
		login.clickOnLoginSubmit();
        login.pressBackWithKeyEvent();
        login.NavigateToLogin();
		
	}
	
	
	@Test(priority = 4, dataProvider = "getDataLoginInvalidUser", groups = { "Smoke","Regression" })
	public void LoginInvalidUser(HashMap<String, String> input) throws InterruptedException {
		
		login.setEmailPassword(input.get("email"), input.get("password"));
		login.clickOnLoginSubmit();
		login.pressBackWithKeyEvent();
	    login.NavigateToLogin();

	}
	*/
	@Test(priority = 5, dataProvider = "getDataSuccessfullLogin", groups = { "Smoke", "Regression" })
	public void SuccessfullLogin(HashMap<String, String> input) throws InterruptedException {
		
		login.setEmailPassword(input.get("email"), input.get("password"));
		login.clickOnLoginSubmit();
		login.CompleteLoginProccess();
	}
	
	@Test(priority = 6, groups = { "Smoke", "Regression" })
	public void HomePageHandlePopups() throws InterruptedException {

		login.HandleCustomDialog(384, 576);
	}

	
	
	@DataProvider
	public Object[][] getDataSuccessfullLogin() throws IOException {
		List<HashMap<String, String>> data = getJsonData(System.getProperty("user.dir")
				+ "//src//test//java//org//rahulshettyacademy//testData//LoginData.json");

		return new Object[][] { { data.get(0) } };
	}

	@DataProvider
	public Object[][] getDataEmptyCredLogin() throws IOException {
		List<HashMap<String, String>> data = getJsonData(System.getProperty("user.dir")
				+ "//src//test//java//org//rahulshettyacademy//testData//LoginData.json");

		return new Object[][] { { data.get(1) } };
	}
	
	@DataProvider
	public Object[][] getDataLoginInvalidCred() throws IOException {
		List<HashMap<String, String>> data = getJsonData(System.getProperty("user.dir")
				+ "//src//test//java//org//rahulshettyacademy//testData//LoginData.json");

		return new Object[][] { { data.get(2) } };
	}
	
	@DataProvider
	public Object[][] getDataLoginInvalidUser() throws IOException {
		List<HashMap<String, String>> data = getJsonData(System.getProperty("user.dir")
				+ "//src//test//java//org//rahulshettyacademy//testData//LoginData.json");

		return new Object[][] { { data.get(3) } };
	}
}
