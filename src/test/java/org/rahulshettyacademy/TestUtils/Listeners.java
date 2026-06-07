package org.rahulshettyacademy.TestUtils;

import java.util.HashMap;
import java.util.Map;

import org.rahulshettyacademy.utils.AppiumUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import io.appium.java_client.AppiumDriver;

/**
 * Listeners - HOTFIX for screenshot cascade-exception scenario.
 *
 * 🔴 CHANGES (May 21):
 *   - Wider exception catch around screenshot (catches WebDriverException too).
 *   - When UiAutomator2 instrumentation crashes mid-test, the test FAIL itself is
 *     logged correctly, but the FOLLOW-UP screenshot also fails with same error.
 *     Pehle wo cascade exception propagate hota tha. Ab gracefully handle.
 *
 * 🔴 CHANGES (Jun 1) - class-grouped Extent report:
 *   - Tests are now organized in a parent/child hierarchy in the
 *     ExtentReports output. Each TestNG class becomes a parent
 *     ExtentTest (e.g., "Dogpack_Marketplace") and each test method
 *     becomes a child node under that parent. The Spark theme renders
 *     this as collapsible class sections with their test methods
 *     nested underneath.
 *   - classTests map caches the parent ExtentTest per class name so
 *     the same parent is reused across all methods of a class.
 *   - onTestSkipped now defensively creates a child node if the
 *     "test" field is null (TestNG sometimes fires onTestSkipped
 *     without first calling onTestStart, for tests skipped via
 *     dependsOnMethods chain failure).
 *
 * NOTE: This is a minimal patch to the existing class.
 * For full logging + retry-awareness, use Batch 1's Listeners.java.
 */
public class Listeners extends AppiumUtils implements ITestListener {

	ExtentTest test;
	ExtentReports extent = ExtentReporterNG.getReporterObject();
	AppiumDriver driver;

	/**
	 * Cache of class-name -> parent ExtentTest. Populated on first
	 * test invocation of each class. Subsequent tests in the same
	 * class reuse the cached parent so all methods nest as children
	 * under one class-level row in the report.
	 */
	private static final Map<String, ExtentTest> classTests = new HashMap<>();

	/**
	 * Get the parent ExtentTest for the given simple class name,
	 * creating it on first request. Synchronized to be safe if
	 * TestNG ever runs tests in parallel - the current suite is
	 * sequential so this is defensive.
	 */
	private synchronized ExtentTest getOrCreateClassParent(String simpleClassName) {
		ExtentTest parent = classTests.get(simpleClassName);
		if (parent == null) {
			parent = extent.createTest(simpleClassName);
			classTests.put(simpleClassName, parent);
		}
		return parent;
	}

	/**
	 * Helper to extract the simple class name from an ITestResult.
	 * TestNG returns the fully-qualified name (e.g.,
	 * "org.rahulshettyacademy.Dogpack_Marketplace"); we keep just
	 * the part after the last dot, which matches how users refer
	 * to the class in the suite XML and conversation.
	 */
	private String getSimpleClassName(ITestResult result) {
		String fqn = result.getMethod().getTestClass().getName();
		int lastDot = fqn.lastIndexOf('.');
		return (lastDot >= 0) ? fqn.substring(lastDot + 1) : fqn;
	}

	@Override
	public void onTestStart(ITestResult result) {
		// Get or create the parent ExtentTest for this class, then
		// attach this test method as a child node under it.
		ExtentTest parent = getOrCreateClassParent(getSimpleClassName(result));
		test = parent.createNode(result.getMethod().getMethodName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		test.log(Status.PASS, "Test Passed");
		captureAndAttach(result);
	}

	@Override
	public void onTestFailure(ITestResult result) {
		test.fail(result.getThrowable());
		captureAndAttach(result);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		// Defensive: TestNG sometimes fires onTestSkipped for a test
		// that never had onTestStart called (e.g., skipped via
		// dependsOnMethods chain failure where the dependency failed
		// before reaching this test). In that case "test" still holds
		// the LAST started test's child node, so we'd misattribute
		// the skip. Detect and create a fresh child node bound to
		// the right parent + method name.
		boolean needFreshNode =
				(test == null)
				|| !result.getMethod().getMethodName().equals(
						getCurrentTestNodeName());
		if (needFreshNode) {
			ExtentTest parent = getOrCreateClassParent(getSimpleClassName(result));
			test = parent.createNode(result.getMethod().getMethodName());
		}

		Throwable t = result.getThrowable();
		String reason = (t != null && t.getMessage() != null)
				? t.getMessage().split("\n")[0] : "skipped";
		test.skip("SKIPPED: " + reason);
	}

	/**
	 * Best-effort: read the test node's name to compare against the
	 * incoming test method name. ExtentReports exposes the model name
	 * via getModel().getName(). If the API is unavailable or null,
	 * returns null and the caller treats that as "create fresh".
	 */
	private String getCurrentTestNodeName() {
		try {
			if (test != null && test.getModel() != null) {
				return test.getModel().getName();
			}
		} catch (Exception ignore) {
			// fall through
		}
		return null;
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// no-op
	}

	@Override
	public void onStart(ITestContext context) {
		// no-op
	}

	@Override
	public void onFinish(ITestContext context) {
		extent.flush();
	}

	// ====================================================================
	// 🔴 SAFE SCREENSHOT HELPER - replaces inline try/catch (IOException) pattern
	// ====================================================================
	/**
	 * Capture screenshot and attach to current Extent test entry.
	 * Catches ALL exceptions (including WebDriverException for crashed sessions).
	 * If screenshot fails, just logs a warning - never throws.
	 */
	private void captureAndAttach(ITestResult result) {
		try {
			driver = (AppiumDriver) result.getTestClass().getRealClass()
					.getField("driver").get(result.getInstance());
		} catch (Exception fieldEx) {
			System.out.println("[WARN] Listener: could not access driver field - "
					+ fieldEx.getClass().getSimpleName());
			if (test != null) {
				test.log(Status.WARNING, "Screenshot skipped - driver field unavailable");
			}
			return;
		}

		try {
			String screenshotPath = getScreenshotPath(
					result.getMethod().getMethodName(), driver);
			if (screenshotPath != null) {
				test.addScreenCaptureFromPath(screenshotPath,
						result.getMethod().getMethodName());
			} else {
				// getScreenshotPath returned null - session was dead
				test.log(Status.WARNING,
						"Screenshot skipped - driver session unavailable "
						+ "(probable instrumentation crash)");
			}
		} catch (Exception screenshotEx) {
			// Ye wo cascade exception thi - ab safe catch karte hain
			System.out.println("[WARN] Listener: screenshot attach failed - "
					+ screenshotEx.getClass().getSimpleName() + ": "
					+ (screenshotEx.getMessage() != null
							? screenshotEx.getMessage().split("\n")[0] : "no message"));
			test.log(Status.WARNING,
					"Screenshot capture failed: " + screenshotEx.getClass().getSimpleName());
		}
	}
}