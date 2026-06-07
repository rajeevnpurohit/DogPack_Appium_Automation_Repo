package org.rahulshettyacademy.TestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;

import org.rahulshettyacademy.utils.AppiumUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;

/**
 * AndroidBaseTest - HOTFIX for Android 14+/16 instrumentation stability.
 *
 * 🔴 NEW CAPABILITIES ADDED (May 21):
 *
 * Background: Android 16 (API 36) device pe heavy flows (Lost Dog 11-screen,
 * MyProfile hamburger flows etc) ke beech mein UiAutomator2 instrumentation
 * process kill ho jata hai memory pressure ke karan. Error:
 *   "'POST /element' cannot be proxied to UiAutomator2 server because
 *    the instrumentation process is not running (probably crashed)"
 *
 * Ye capabilities add ki hain to prevent / recover from this:
 *
 *   disableSuppressAccessibilityService  - Accessibility service ko disable
 *                                          karne ki koshish nahi karte (Android 14+ pe
 *                                          ye explicit permission deni padti hai)
 *   ignoreHiddenApiPolicyError           - Android 14+ hidden API enforcement ko bypass
 *   adbExecTimeout                       - ADB commands ke liye longer timeout (60s)
 *   uiautomator2ServerInstallTimeout     - UiAutomator2 server install timeout
 *   uiautomator2ServerLaunchTimeout      - UiAutomator2 server launch timeout
 *   enforceAppInstall=false              - Same version hai to reinstall mat kar
 *                                          (reduces test setup time + memory pressure)
 *   mjpegServerPort                      - Alternative screenshot channel agar primary fail ho
 */
public class AndroidBaseTest extends AppiumUtils {

	public AndroidDriver driver;
	public AppiumDriverLocalService service;
	public static Properties testDataProp;

	@BeforeClass(alwaysRun = true)
	public void ConfigureAppium() throws IOException, InterruptedException {
		System.out.println("=== STARTING ConfigureAppium ===");

		// ========================================
		// 1. Load Properties File (cross-platform path)
		// ========================================
		Properties prop = new Properties();
		String propsPath = Paths.get(
				System.getProperty("user.dir"),
				"src", "main", "java", "org", "rahulshettyacademy", "resources",
				"data.properties"
		).toString();

		System.out.println("Loading properties from: " + propsPath);
		FileInputStream fis = new FileInputStream(propsPath);
		prop.load(fis);
		fis.close();

		// ========================================
		// 2. Read All Configuration from Properties
		// ========================================
		String ipAddress = System.getProperty("ipAddress") != null
				? System.getProperty("ipAddress")
				: prop.getProperty("ipAddress");
		String port = prop.getProperty("port");
		String deviceName = prop.getProperty("AndroidDeviceName");
		String appiumServerPath = prop.getProperty("appiumServerPath");
		String appPackage = prop.getProperty("appPackage");
		String appActivity = prop.getProperty("appActivity");
		String useApk = prop.getProperty("useApk", "false");
		String noResetStr = prop.getProperty("noReset", "false");
		boolean noReset = Boolean.parseBoolean(noResetStr);

		// ========================================
		// 3. Start Appium Server (Appium 3.x compatible - see AppiumUtils.java)
		// ========================================
		service = startAppiumServer(ipAddress, Integer.parseInt(port), appiumServerPath);

		// ========================================
		// 4. Set Capabilities (Modern - UiAutomator2)
		// ========================================
		UiAutomator2Options options = new UiAutomator2Options();
		options.setDeviceName(deviceName);
		options.setCapability("chromedriver_autodownload", true);

		// Common stability capabilities
		options.setNewCommandTimeout(Duration.ofSeconds(300));
		options.setNoReset(noReset);

		// ================================================================
		// 🔴 NEW: ANDROID 14+/16 INSTRUMENTATION STABILITY CAPABILITIES
		// ================================================================
		// Without these, UiAutomator2 process crashes mid-test on Android 16
		// during heavy flows (Lost Dog form, MyProfile hamburger taps, etc).
		// ================================================================

		// Accessibility service stability (Android 14+)
		options.setCapability("disableSuppressAccessibilityService", true);

		// Bypass Android 14+ hidden API enforcement (memory pressure trigger)
		options.setCapability("ignoreHiddenApiPolicyError", true);

		// Longer ADB timeout for slow operations (image upload, large form submit)
		options.setAdbExecTimeout(Duration.ofSeconds(60));

		// UiAutomator2 server install/launch (slower on Android 16 first boot)
		options.setUiautomator2ServerInstallTimeout(Duration.ofSeconds(60));
		options.setUiautomator2ServerLaunchTimeout(Duration.ofSeconds(60));

		// Don't reinstall app if same version already there - saves memory + time
		options.setEnforceAppInstall(false);

		// Alternative MJPEG stream (fallback if primary command channel chokes)
		options.setCapability("mjpegServerPort", 8484);

		// Force app launch (clean start, prevents state-bleed across tests)
		options.setCapability("forceAppLaunch", true);

		// Skip the app launch wait if already foregrounded (faster)
		options.setCapability("autoLaunch", true);

		// ========================================
		// IMPORTANT: setAutoGrantPermissions REMOVED
		// ========================================
		// Reason: App ka login flow notification permission screen
		// (NewNotification + system popup) ke through jata hai.
		// Agar auto-grant kiya toh wo popups aayenge hi nahi
		// aur test fail ho jayega.
		// Hum manually handle karenge LoginPage.CompleteLoginProccess() me
		// ========================================

		// ========================================
		// 5. APK vs Installed App Decision
		// ========================================
		if ("true".equalsIgnoreCase(useApk)) {
			String apkRelativePath = prop.getProperty("apkRelativePath");
			String[] apkPathParts = apkRelativePath.split("/");
			String apkFullPath = Paths.get(System.getProperty("user.dir"), apkPathParts).toString();

			File apkFile = new File(apkFullPath);
			if (!apkFile.exists()) {
				throw new RuntimeException("APK file not found at: " + apkFullPath
						+ "\nPlease verify 'apkRelativePath' in data.properties");
			}
			System.out.println("APK Mode - Installing from: " + apkFullPath);
			options.setApp(apkFullPath);

		} else {
			System.out.println("Installed App Mode - Launching: " + appPackage);
			System.out.println("Activity: " + appActivity);
			options.setAppPackage(appPackage);
			options.setAppActivity(appActivity);
		}

		// ========================================
		// 6. Initialize Driver
		// ========================================
		System.out.println("Initializing AndroidDriver for device: " + deviceName);
		driver = new AndroidDriver(service.getUrl(), options);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

		System.out.println("=== ConfigureAppium COMPLETED ===");
		System.out.println("Device: " + deviceName + " | Android API: "
				+ driver.getCapabilities().getCapability("deviceApiLevel"));
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() {
		try {
			if (driver != null) {
				driver.quit();
				System.out.println("Driver quit successfully");
			}
		} catch (Exception e) {
			System.out.println("Error quitting driver: " + e.getMessage());
		}

		try {
			if (service != null) {
				service.stop();
				System.out.println("Appium service stopped");
			}
		} catch (Exception e) {
			System.out.println("Error stopping Appium service: " + e.getMessage());
		}
	}
}