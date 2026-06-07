package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rahulshettyacademy.utils.AndroidActions;
import org.testng.Assert;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

/**
 * NotificationsPage - Page object for the in-app notifications
 * feature. Covers the 5-step navigation from Feed tab to the
 * notifications listing + first-card text extraction.
 *
 * Method overview:
 *   ClickFeed()                       - tap the Feed bottom-nav tab
 *   ClickNotificationsIcon()          - tap the bell/sport icon
 *   ClickAllButton()                  - tap the "All" filter chip
 *   ClickNew()                        - tap the "New" section header
 *   PrintAndAssertNotificationCardText() - extract + log + assert
 *                                       non-empty text from the
 *                                       first notification card area
 */
public class NotificationsPage extends AndroidActions {

	AndroidDriver driver;
	WebDriverWait wait;

	public NotificationsPage(AndroidDriver driver) {
		super(driver);
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	// ================================================================
	// =====           LOCATORS (XPATH STRINGS)                  ======
	// ================================================================

	/** Bottom-nav tab labeled "Feed". */
	private static final String FEED_TAB_XPATH =
			"//android.widget.TextView[@text=\"Feed\"]";

	/**
	 * Notifications icon in the header (when Feed tab is active).
	 * NOTE: descends to /ImageView per user spec; the parent
	 * ViewGroup carries content-desc="feed_image_sport".
	 */
	private static final String NOTIFICATIONS_ICON_XPATH =
			"//android.view.ViewGroup[@content-desc=\"feed_image_sport\"]"
			+ "/android.widget.ImageView";

	/** "All" filter chip on the Notifications screen. */
	private static final String ALL_BUTTON_XPATH =
			"//android.widget.TextView[@text=\"All\"]";

	/** "New" section header on the Notifications screen. */
	private static final String NEW_SECTION_XPATH =
			"//android.widget.TextView[@text=\"New\"]";

	/**
	 * The notification cards container - first card area inside the
	 * RecyclerView. Deeply-nested positional path; expected to
	 * resolve to a ViewGroup whose descendants are TextViews carrying
	 * the notification text content.
	 *
	 * FRAGILITY: this XPath uses positional indices (ViewGroup[3])
	 * and is sensitive to changes in either the app's view
	 * hierarchy or the order of notifications in the feed.
	 */
	private static final String NOTIFICATION_CARD_CONTAINER_XPATH =
			"//androidx.recyclerview.widget.RecyclerView"
			+ "/android.widget.FrameLayout"
			+ "/android.view.ViewGroup"
			+ "/android.view.ViewGroup"
			+ "/android.view.ViewGroup[3]"
			+ "/android.widget.ScrollView"
			+ "/android.view.ViewGroup";

	// ================================================================
	// =====           PUBLIC METHODS                            ======
	// ================================================================

	/** Step 1 - Tap the Feed bottom-nav tab. */
	public void ClickFeed() {
		System.out.println("===> ClickFeed");
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
				AppiumBy.xpath(FEED_TAB_XPATH)));
		el.click();
		System.out.println("[OK]       Tapped Feed tab");
		sleepShort();
	}

	/** Step 2 - Tap the notifications icon (bell/sport icon). */
	public void ClickNotificationsIcon() {
		System.out.println("===> ClickNotificationsIcon");
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
				AppiumBy.xpath(NOTIFICATIONS_ICON_XPATH)));
		el.click();
		System.out.println("[OK]       Tapped notifications icon");
		sleepShort();
	}

	/** Step 3 - Tap the "All" filter chip. */
	public void ClickAllButton() {
		System.out.println("===> ClickAllButton");
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
				AppiumBy.xpath(ALL_BUTTON_XPATH)));
		el.click();
		System.out.println("[OK]       Tapped 'All' filter chip");
		sleepShort();
	}

	/** Step 4 - Tap the "New" section header. */
	public void ClickNew() {
		System.out.println("===> ClickNew");
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
				AppiumBy.xpath(NEW_SECTION_XPATH)));
		el.click();
		System.out.println("[OK]       Tapped 'New' section");
		sleepShort();
	}

	/**
	 * Step 5 - Extract text from the first notification card area,
	 * log it to the console, and assert it is non-empty.
	 *
	 * Strategy: locate the container ViewGroup via the user-provided
	 * XPath, then iterate ALL descendant TextViews and concatenate
	 * their text. Aggregating descendants is more reliable than
	 * calling getText() on the container ViewGroup directly because
	 * Android's accessibility APIs don't consistently expose
	 * concatenated text on ViewGroup nodes.
	 *
	 * Assertion: the aggregated text must be non-null and non-empty.
	 * Per the test's intent, this validates that the notification
	 * card area has some textual content rendered.
	 */
	public void PrintAndAssertNotificationCardText() {
		System.out.println("===> PrintAndAssertNotificationCardText");
		WebElement container = wait.until(
				ExpectedConditions.visibilityOfElementLocated(
						AppiumBy.xpath(NOTIFICATION_CARD_CONTAINER_XPATH)));

		// Aggregate text content from all descendant TextViews.
		List<WebElement> textViews = container.findElements(
				AppiumBy.xpath(".//android.widget.TextView"));
		StringBuilder aggregate = new StringBuilder();
		for (WebElement tv : textViews) {
			String t = tv.getAttribute("text");
			if (t != null && !t.isEmpty()) {
				aggregate.append(t).append("\n");
			}
		}
		String allText = aggregate.toString().trim();

		System.out.println("==== NOTIFICATION CARD TEXT CONTENT ====");
		System.out.println(allText);
		System.out.println("==== END NOTIFICATION CARD TEXT ========");

		Assert.assertNotNull(allText,
				"Notification card text aggregation returned null - "
				+ "container found but no text could be extracted.");
		Assert.assertFalse(allText.isEmpty(),
				"Notification card text is empty - expected at least "
				+ "one notification card with text content. Container "
				+ "had " + textViews.size() + " TextView descendant(s).");
		System.out.println("[PASS]     Notification text is non-empty ("
				+ textViews.size() + " TextView(s) aggregated)");
	}

	// ================================================================
	// =====           PRIVATE HELPERS                           ======
	// ================================================================

	/** Brief settle delay after each tap to let UI transitions complete. */
	private void sleepShort() {
		try {
			Thread.sleep(1200);
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}
	}
}
