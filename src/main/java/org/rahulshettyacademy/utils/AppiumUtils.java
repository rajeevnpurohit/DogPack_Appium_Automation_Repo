package org.rahulshettyacademy.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

/**
 * AppiumUtils - HOTFIX for Appium 3.x compatibility.
 *
 * 🔴 CRITICAL CHANGES (May 21):
 *   1. --allow-insecure flag format updated for Appium 3.x:
 *      OLD: "chromedriver_autodownload"
 *      NEW: "uiautomator2:chromedriver_autodownload"  (driver:feature)
 *
 *   2. Real health check after service.start():
 *      Pehle service.start() return hote hi "successfully started" print hota tha
 *      EVEN IF Node process async crash ho. Ab actual isRunning() verify karte hain.
 *
 *   3. getScreenshotPath now catches WebDriverException too:
 *      Agar UiAutomator2 crash ho gaya to screenshot bhi fail hota hai - return null
 *      instead of throwing cascade exception.
 */
public abstract class AppiumUtils {

	public AppiumDriverLocalService service;

	/**
	 * Format amount string by removing currency symbol
	 */
	public Double getFormattedAmount(String amount) {
		Double price = Double.parseDouble(amount.substring(1));
		return price;
	}

	/**
	 * Read JSON test data file
	 */
	public List<HashMap<String, String>> getJsonData(String jsonFilePath) throws IOException {
		String jsonContent = FileUtils.readFileToString(new File(jsonFilePath), StandardCharsets.UTF_8);
		ObjectMapper mapper = new ObjectMapper();
		List<HashMap<String, String>> data = mapper.readValue(jsonContent,
				new TypeReference<List<HashMap<String, String>>>() {
				});
		return data;
	}

	/**
	 * Start Appium Server programmatically — APPIUM 3.x COMPATIBLE.
	 *
	 * @param ipAddress   - usually 127.0.0.1
	 * @param port        - usually 4723
	 * @param appiumJsPath - full path to appium main.js
	 * @return AppiumDriverLocalService
	 */
	public AppiumDriverLocalService startAppiumServer(String ipAddress, int port, String appiumJsPath) {
		File appiumJs = new File(appiumJsPath);

		if (!appiumJs.exists()) {
			throw new RuntimeException("❌ Appium main.js not found at: " + appiumJsPath
					+ "\nPlease check 'appiumServerPath' in data.properties");
		}

		System.out.println("🚀 Starting Appium server at " + ipAddress + ":" + port);
		System.out.println("📍 Using Appium JS: " + appiumJsPath);

		// ================================================================
		// 🔴 APPIUM 3.x BREAKING CHANGE FIX
		// ================================================================
		// Pehle  (Appium 2.x): --allow-insecure chromedriver_autodownload
		// Ab     (Appium 3.x): --allow-insecure <driver>:<feature>
		//                   OR --allow-insecure *:<feature>  (all drivers)
		//
		// Reference: Appium 3.0 release notes - insecure features ko ab
		// explicitly per-driver namespace karna padta hai.
		// ================================================================
		service = new AppiumServiceBuilder()
				.withAppiumJS(appiumJs)
				.withIPAddress(ipAddress)
				.usingPort(port)
				// ⬇️ THE ACTUAL FIX
				.withArgument(() -> "--allow-insecure", "uiautomator2:chromedriver_autodownload")
				.build();

		service.start();

		// ================================================================
		// 🔴 REAL HEALTH CHECK
		// ================================================================
		// service.start() returns BEFORE Node process is actually ready/dead.
		// Pehle invalid args pe Node async crash hota tha but humara code
		// "✅ successfully started" print karta tha. Ab actually verify.
		// ================================================================
		try {
			Thread.sleep(2500); // Node ko fully boot/crash hone do
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		if (!service.isRunning()) {
			throw new RuntimeException(
					"❌ Appium server FAILED to start (Node process not running).\n"
					+ "Most common reasons:\n"
					+ "  1. --allow-insecure format wrong (Appium 3.x needs 'driver:feature')\n"
					+ "  2. Port " + port + " already in use - run: lsof -i:" + port + " | grep LISTEN\n"
					+ "  3. Node version incompatible - Appium 3.x needs Node 18+\n"
					+ "  4. Existing manual Appium server running - kill it: pkill -f appium\n"
					+ "Check the console output above for the exact Node error.");
		}

		System.out.println("✅ Appium server started successfully at " + service.getUrl());
		return service;
	}

	/**
	 * Wait for element to appear
	 */
	public void waitForElementToAppear(WebElement ele, AppiumDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.visibilityOf(ele));
	}

	/**
	 * Take screenshot - works on both Mac and Windows.
	 *
	 * 🔴 HOTFIX: Catches WebDriverException too (UiAutomator2 crash scenario).
	 * Returns null path on failure instead of cascade exception.
	 */
	public String getScreenshotPath(String testCaseName, AppiumDriver driver) throws IOException {
		try {
			File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

			// Cross-platform path using Paths.get()
			String reportsDir = Paths.get(System.getProperty("user.dir"), "reports").toString();
			File reportsFolder = new File(reportsDir);

			// Create reports folder if it doesn't exist
			if (!reportsFolder.exists()) {
				reportsFolder.mkdirs();
			}

			String destinationFile = Paths.get(reportsDir, testCaseName + ".png").toString();
			FileUtils.copyFile(source, new File(destinationFile));
			return destinationFile;
		} catch (WebDriverException wde) {
			// UiAutomator2 instrumentation crash scenario - session is dead
			System.out.println("[WARN] Screenshot skipped - driver session/instrumentation unavailable: "
					+ wde.getMessage().split("\n")[0]);
			return null;
		} catch (Exception e) {
			System.out.println("[WARN] Screenshot failed unexpectedly: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Helper method to build cross-platform paths from project root
	 * Usage: getProjectPath("src", "test", "java", "data.json")
	 */
	public static String getProjectPath(String... pathParts) {
		return Paths.get(System.getProperty("user.dir"), pathParts).toString();
	}
}