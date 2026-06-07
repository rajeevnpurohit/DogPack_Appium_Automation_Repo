package org.rahulshettyacademy.utils;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import com.google.common.collect.ImmutableMap;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class AndroidActions extends AppiumUtils{
	
	AndroidDriver driver;
	
	public AndroidActions(AndroidDriver driver)
	{
	
		this.driver = driver;
	}
	
	public void longPressAction(WebElement ele)
	{
		((JavascriptExecutor)driver).executeScript("mobile: longClickGesture",
				ImmutableMap.of("elementId",((RemoteWebElement)ele).getId(),
						"duration",2000));
	}
	
	public void swipeDownToRefresh() {
	    ((JavascriptExecutor) driver).executeScript("mobile: swipeGesture", Map.of(
	        "left", 100,
	        "top", 300,
	        "width", 800,
	        "height", 1200,
	        "direction", "down",
	        "percent", 0.85
	    ));

	    System.out.println("🔄 Performed swipe-down to refresh.");
	}

	public void scrollToEndAction()
	{
		boolean canScrollMore;
		do
		{
		 canScrollMore = (Boolean) ((JavascriptExecutor) driver).executeScript("mobile: scrollGesture", ImmutableMap.of(
			    "left", 100, "top", 100, "width", 200, "height", 200,
			    "direction", "down",
			    "percent", 3.0
			    
			));
		}while(canScrollMore);
	}
	
	public void scrollDownTwice() {
	    for (int i = 0; i < 2; i++) {
	        ((JavascriptExecutor) driver).executeScript("mobile: scrollGesture", ImmutableMap.of(
	            "left", 100, 
	            "top", 100, 
	            "width", 200, 
	            "height", 200,
	            "direction", "down",
	            "percent", 3.0
	        ));
	    }
	}
	
	public void scrollToSearchUserName(String text) {
	    driver.findElement(AppiumBy.androidUIAutomator(
	        "new UiScrollable(new UiSelector().scrollable(true)).setAsVerticalList()" +
	        ".scrollIntoView(new UiSelector().text(\"" + text + "\"));"
	    ));
	}
	
	public void scrollToText(String text)
	{
		
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\""+text+"\"));"));
	
	}
	
	public void scrollToText2(String text) {
	    for (int i = 0; i < 7; i++) { // Max 7 scroll attempts

	        try {
	            WebElement el = driver.findElement(
	                By.xpath("//android.widget.TextView[@text='" + text + "']")
	            );

	            if (el.isDisplayed()) {
	                System.out.println("✅ Found text: " + text);
	                return;
	            }

	        } catch (Exception e) {
	            // ignore - element not found, continue scrolling
	        }

	        // Perform scroll gesture
	        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
	            "mobile: scrollGesture",
	            java.util.Map.of(
	                "left", 100,
	                "top", 300,
	                "width", 800,
	                "height", 1200,
	                "direction", "down",
	                "percent", 0.85
	            )
	        );

	        System.out.println("↕ Scrolling to find: " + text);
	    }

	    throw new RuntimeException("❌ Could not find text: " + text);
	}

	
	public void scrollableToText(String text)
	{
		driver.findElement(
  			    AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().textContains(\""+text+"\"));"));
	}
	
	
	public void swipeAction(WebElement ele,String direction)
	{
		((JavascriptExecutor) driver).executeScript("mobile: swipeGesture", ImmutableMap.of(
				"elementId", ((RemoteWebElement)ele).getId(),
			 
			    "direction", direction,
			    "percent", 0.75
			));
		
		
	}

}
