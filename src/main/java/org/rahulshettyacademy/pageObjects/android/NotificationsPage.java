package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rahulshettyacademy.utils.AndroidActions;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

/**
 * NotificationsPage - Page object for the in-app notifications
 * feature. Covers navigation from Feed tab to the notifications
 * listing + tapping through the filter chips on both the
 * Notifications and Inbox tabs.
 *
 * Method overview:
 *   ClickFeed()                       - tap the Feed bottom-nav tab
 *   ClickNotificationsIcon()          - tap the bell/sport icon
 *   ClickAllButton()                  - tap "All" filter chip (Notif or Inbox)
 *   ClickNewFollowers()               - tap "New Followers" chip (Notif)
 *   ClickLikes()                      - tap "Likes" chip (Notif)
 *   ClickComments()                   - tap "Comments" chip (Notif)
 *   ClickMentions()                   - tap "Mentions" chip (Notif, scrolls)
 *   --- The following 6 methods are temporarily disabled ---
 *   ClickEngage()                     - (disabled) tap empty-state Engage text
 *   ClickProfiles()                   - (disabled) tap "PROFILES" tab
 *   ClickParks()                      - (disabled) tap "PARKS" tab
 *   ClickBusinesses()                 - (disabled) tap "BUSINESSES" tab
 *   ClickHashtags()                   - (disabled) tap "HASHTAGS" tab
 *   ClickGoBack()                     - (disabled) tap back button
 *   --- End of disabled section ---
 *   ClickInbox()                      - tap the "Inbox" top tab
 *   ClickUnread()                     - tap "Unread" chip (Inbox)
 *   ClickGroups()                     - tap "Groups" chip (Inbox)
 *   ClickParkGroups()                 - tap "Park Groups" chip (Inbox, scrolls)
 *   ClickBackButton()                 - tap top-left back button (cleanup helper)
 *   DismissAllOnboarding()            - dismiss first-time-user popups
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
	 * Targets the TouchableOpacity carrying content-desc=
	 * "feed_image_sport" DIRECTLY - does NOT descend to the inner
	 * /android.widget.ImageView.
	 *
	 * Why no descent: the inner Image is async-loaded (prop.imageSport
	 * in the React Native source - see common/Header.js lines 235-258).
	 * Descending to /android.widget.ImageView would cause the
	 * visibility/clickability wait to hit the 30s timeout because
	 * the ImageView isn't reliably exposed in the accessibility tree
	 * until the image asset finishes loading.
	 *
	 * Tap propagates correctly because the parent TouchableOpacity
	 * is what owns the onPress handler (-> prop.middleRightClick()).
	 */
	private static final String NOTIFICATIONS_ICON_XPATH =
			"//*[@content-desc=\"feed_image_sport\"]";

	/** "All" filter chip on the Notifications screen. */
	private static final String ALL_BUTTON_XPATH =
			"//android.widget.TextView[@text=\"All\"]";

	/** "New Followers" filter chip on the Notifications screen. */
	private static final String NEW_FOLLOWERS_XPATH =
			"//android.widget.TextView[@text=\"New Followers\"]";

	/** "Likes" filter chip on the Notifications screen. */
	private static final String LIKES_XPATH =
			"//android.widget.TextView[@text=\"Likes\"]";

	/** "Comments" filter chip on the Notifications screen. */
	private static final String COMMENTS_XPATH =
			"//android.widget.TextView[@text=\"Comments\"]";

	/** "Mentions" filter chip on the Notifications screen. */
	private static final String MENTIONS_XPATH =
			"//android.widget.TextView[@text=\"Mentions\"]";

	/*
	 * NOTE on the "Engage" link: there is no constant for it because
	 * the locator strategy is too complex to express as a single
	 * static XPath. The element is a ClickableSpan inside a parent
	 * TextView's SpannableString, and Appium's exposure of this varies
	 * by RN version + Android accessibility settings. See ClickEngage()
	 * for the multi-strategy approach.
	 */

	/**
	 * "PROFILES" tab on the search/discovery screen.
	 *
	 * IMPORTANT: the visible text is "PROFILES" (all caps) but the
	 * underlying text attribute is "Profiles" (mixed case). The Search
	 * screen applies textTransform: "uppercase" as a CSS-style visual
	 * effect, which doesn't alter the actual text stored in the view.
	 * Appium reads the source text from the accessibility tree, so we
	 * must match the mixed-case form.
	 *
	 * Source: Search.js line 163 - tabBarLabel = translate("Profiles"),
	 * which returns "Profiles" per en.json. Surrounding Text has
	 * textTransform: "uppercase" (line 160) - visual only.
	 *
	 * The other three tabs (PARKS, BUSINESSES, HASHTAGS) have their
	 * translation values already stored in uppercase in en.json, so
	 * those XPaths can use the visible form directly.
	 */
	private static final String PROFILES_XPATH =
			"//android.widget.TextView[@text=\"Profiles\"]";

	/** "PARKS" tab on the search/discovery screen. */
	private static final String PARKS_XPATH =
			"//android.widget.TextView[@text=\"PARKS\"]";

	/** "BUSINESSES" tab on the search/discovery screen. */
	private static final String BUSINESSES_XPATH =
			"//android.widget.TextView[@text=\"BUSINESSES\"]";

	/** "HASHTAGS" tab on the search/discovery screen. */
	private static final String HASHTAGS_XPATH =
			"//android.widget.TextView[@text=\"HASHTAGS\"]";

	/**
	 * Back button on the search/discovery screen.
	 *
	 * Targets the wrapping ViewGroup carrying content-desc=
	 * "search_isGoBack" DIRECTLY - does NOT descend to the inner
	 * /android.widget.ImageView. Same async-image-load fix we applied
	 * to feed_image_sport (the inner Image is rendered after a network
	 * fetch, so /ImageView isn't reliably in the accessibility tree).
	 * The tap propagates correctly because the parent TouchableOpacity
	 * owns the onPress handler.
	 */
	private static final String GO_BACK_BUTTON_XPATH =
			"//*[@content-desc=\"search_isGoBack\"]";

	/** "Inbox" tab at the top of the Notifications screen. */
	private static final String INBOX_TAB_XPATH =
			"//android.widget.TextView[@text=\"Inbox\"]";

	/** "Unread" filter chip on the Inbox tab. */
	private static final String UNREAD_XPATH =
			"//android.widget.TextView[@text=\"Unread\"]";

	/** "Groups" filter chip on the Inbox tab. */
	private static final String GROUPS_XPATH =
			"//android.widget.TextView[@text=\"Groups\"]";

	/** "Park Groups" filter chip on the Inbox tab (off-screen right). */
	private static final String PARK_GROUPS_XPATH =
			"//android.widget.TextView[@text=\"Park Groups\"]";

	/**
	 * Top-left back button on the Notifications / Inbox screen.
	 * Carries content-desc="left_click_back". This is the screen-level
	 * back button - tapping it exits the Notifications screen and
	 * returns to the screen that opened it (typically Feed). Used by
	 * test classes for cleanup, since the bottom-nav (Profile tab,
	 * etc.) is hidden while the Notifications screen is active.
	 *
	 * Distinct from GO_BACK_BUTTON_XPATH above, which targets the
	 * search/discovery screen's back button (content-desc=
	 * "search_isGoBack") - a completely different element.
	 */
	private static final String BACK_BUTTON_XPATH =
			"//android.view.ViewGroup[@content-desc=\"left_click_back\"]"
			+ "/android.widget.ImageView";

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

	/** Step 4 - Tap the "New Followers" filter chip. */
	public void ClickNewFollowers() {
		System.out.println("===> ClickNewFollowers");
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
				AppiumBy.xpath(NEW_FOLLOWERS_XPATH)));
		el.click();
		System.out.println("[OK]       Tapped 'New Followers' filter chip");
		sleepShort();
	}

	/** Step 5 - Tap the "Likes" filter chip. */
	public void ClickLikes() {
		System.out.println("===> ClickLikes");
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
				AppiumBy.xpath(LIKES_XPATH)));
		el.click();
		System.out.println("[OK]       Tapped 'Likes' filter chip");
		sleepShort();
	}

	/** Step 6 - Tap the "Comments" filter chip. */
	public void ClickComments() {
		System.out.println("===> ClickComments");
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
				AppiumBy.xpath(COMMENTS_XPATH)));
		el.click();
		System.out.println("[OK]       Tapped 'Comments' filter chip");
		sleepShort();
	}

	/**
	 * Step 7 - Tap the "Mentions" filter chip.
	 *
	 * Mentions sits off-screen to the right when "All" is the
	 * leftmost chip, and is NOT in the rendered accessibility tree
	 * until the chip row is scrolled left. Direct touch swipes
	 * don't work here because the parent Notifications/Inbox
	 * ViewPager keeps intercepting horizontal touch gestures and
	 * switching tabs - confirmed across multiple swipe-gesture
	 * attempts (W3C PointerInput, mobile:swipeGesture with
	 * coordinate rectangle, mobile:swipeGesture with elementId).
	 *
	 * Solution: explicit UiScrollable.scrollForward() via the
	 * androidUIAutomator locator strategy. This goes through the
	 * Android Accessibility Service - no synthetic touch events
	 * are dispatched - so the parent ViewPager can't see the
	 * scroll. For a horizontal list, scrollForward() swipes from
	 * right to left in screen space, shifting content leftward
	 * and revealing items at the END of the list (Mentions).
	 *
	 * Why scrollForward() and not scrollIntoView(): scrollIntoView
	 * tries to be smart by scrolling BACKWARD first (toward the
	 * start of the list), then forward. When it scrolled backward
	 * we observed motion in the wrong direction and Mentions never
	 * came into view. scrollForward() is one-shot, one-direction,
	 * unambiguous.
	 */
	public void ClickMentions() {
		System.out.println("===> ClickMentions");
		scrollChipRowForward();
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
				AppiumBy.xpath(MENTIONS_XPATH)));
		el.click();
		System.out.println("[OK]       Tapped 'Mentions' filter chip");
		sleepShort();
	}

	// ================================================================
	// ==========  TEMPORARILY DISABLED - SEARCH/DISCOVERY      =======
	// ================================================================
	// The 6 page object methods below (ClickEngage through ClickGoBack)
	// are commented out to mirror the disabled state of their
	// corresponding @Test methods in Dogpack_Notifications.java.
	// The "Engage" link uses a React Native ClickableSpan inside a
	// SpannableString, which requires further investigation to click
	// reliably via Appium. Re-enable this whole block when ClickEngage
	// is fixed.
	// ----------------------------------------------------------------

//	/**
//	 * Step 9 - Tap the "Engage" link inside the empty-state sentence.
//	 *
//	 * "Engage" is a ClickableSpan inside the parent TextView's
//	 * SpannableString. The inner Text isn't exposed as a separate
//	 * accessibility node, so we locate the parent (full sentence)
//	 * and tap by coordinates at the position where "Engage" is
//	 * rendered: right side of line 1 of the wrapped text.
//	 *
//	 * NOTE: this implementation is currently not reliable across all
//	 * runs - the tap registers but doesn't always fire handlePress
//	 * ("engage"). The corresponding test (ClickEngage_Notifications)
//	 * is temporarily disabled in Dogpack_Notifications.java pending
//	 * further investigation.
//	 */
//	public void ClickEngage() {
//		System.out.println("===> ClickEngage");
//
//		WebElement parent = wait.until(
//				ExpectedConditions.presenceOfElementLocated(
//						AppiumBy.androidUIAutomator(
//								"new UiSelector().textContains(\"No mentions yet\")")));
//
//		int parentLeft = parent.getLocation().getX();
//		int parentTop = parent.getLocation().getY();
//		int parentWidth = parent.getSize().getWidth();
//		int parentHeight = parent.getSize().getHeight();
//
//		// "Engage" sits at the right end of line 1 of the wrapped text.
//		int tapX = parentLeft + (int) (parentWidth * 0.65);
//		int tapY = parentTop + (int) (parentHeight * 0.20);
//
//		Map<String, Object> args = new HashMap<>();
//		args.put("x", tapX);
//		args.put("y", tapY);
//		driver.executeScript("mobile: clickGesture", args);
//
//		System.out.println("[OK]       Tapped 'Engage' at coords (" + tapX
//				+ ", " + tapY + ")");
//		sleepShort();
//	}
//
//	/** Step 10 - Tap the "PROFILES" tab on the search/discovery screen. */
//	public void ClickProfiles() {
//		System.out.println("===> ClickProfiles");
//		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
//				AppiumBy.xpath(PROFILES_XPATH)));
//		el.click();
//		System.out.println("[OK]       Tapped 'PROFILES' tab");
//		sleepShort();
//	}
//
//	/** Step 11 - Tap the "PARKS" tab on the search/discovery screen. */
//	public void ClickParks() {
//		System.out.println("===> ClickParks");
//		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
//				AppiumBy.xpath(PARKS_XPATH)));
//		el.click();
//		System.out.println("[OK]       Tapped 'PARKS' tab");
//		sleepShort();
//	}
//
//	/** Step 12 - Tap the "BUSINESSES" tab on the search/discovery screen. */
//	public void ClickBusinesses() {
//		System.out.println("===> ClickBusinesses");
//		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
//				AppiumBy.xpath(BUSINESSES_XPATH)));
//		el.click();
//		System.out.println("[OK]       Tapped 'BUSINESSES' tab");
//		sleepShort();
//	}
//
//	/** Step 13 - Tap the "HASHTAGS" tab on the search/discovery screen. */
//	public void ClickHashtags() {
//		System.out.println("===> ClickHashtags");
//		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
//				AppiumBy.xpath(HASHTAGS_XPATH)));
//		el.click();
//		System.out.println("[OK]       Tapped 'HASHTAGS' tab");
//		sleepShort();
//	}
//
//	/**
//	 * Step 14 - Tap the back button on the search/discovery screen to
//	 * return to the Notifications screen.
//	 */
//	public void ClickGoBack() {
//		System.out.println("===> ClickGoBack");
//		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
//				AppiumBy.xpath(GO_BACK_BUTTON_XPATH)));
//		el.click();
//		System.out.println("[OK]       Tapped back button");
//		sleepShort();
//	}

	/** Step 8 - Tap the "Inbox" tab at the top of the Notifications screen. */
	public void ClickInbox() {
		System.out.println("===> ClickInbox");
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
				AppiumBy.xpath(INBOX_TAB_XPATH)));
		el.click();
		System.out.println("[OK]       Tapped 'Inbox' tab");
		sleepShort();
	}

	/** Step 9 - Tap the "Unread" filter chip on the Inbox tab. */
	public void ClickUnread() {
		System.out.println("===> ClickUnread");
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
				AppiumBy.xpath(UNREAD_XPATH)));
		el.click();
		System.out.println("[OK]       Tapped 'Unread' filter chip");
		sleepShort();
	}

	/** Step 10 - Tap the "Groups" filter chip on the Inbox tab. */
	public void ClickGroups() {
		System.out.println("===> ClickGroups");
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
				AppiumBy.xpath(GROUPS_XPATH)));
		el.click();
		System.out.println("[OK]       Tapped 'Groups' filter chip");
		sleepShort();
	}

	/**
	 * Step 11 - Tap the "Park Groups" filter chip on the Inbox tab.
	 *
	 * Like Mentions on the Notifications tab, Park Groups sits at the
	 * far right of the chip row and may be off-screen / outside the
	 * rendered accessibility tree. Same scrollForward() treatment.
	 */
	public void ClickParkGroups() {
		System.out.println("===> ClickParkGroups");
		scrollChipRowForward();
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
				AppiumBy.xpath(PARK_GROUPS_XPATH)));
		el.click();
		System.out.println("[OK]       Tapped 'Park Groups' filter chip");
		sleepShort();
	}

	/**
	 * Step 12 - Tap the top-left back button on the Notifications /
	 * Inbox screen, returning to the screen that opened Notifications
	 * (typically Feed).
	 *
	 * Used by test class cleanup (@AfterClass) when reverting from
	 * business profile back to dog profile. The dog-switch sequence
	 * starts by tapping the bottom-nav Profile tab, which is hidden
	 * while the Notifications screen is active - so we must exit the
	 * Notifications screen first.
	 */
	public void ClickBackButton() {
		System.out.println("===> ClickBackButton");
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
				AppiumBy.xpath(BACK_BUTTON_XPATH)));
		el.click();
		System.out.println("[OK]       Tapped back button "
				+ "(exited Notifications screen)");
		sleepShort();
	}

	// ================================================================
	// =====      ONBOARDING POPUP DISMISSAL                     ======
	// ================================================================

	/**
	 * Dismiss any first-time-user onboarding popups that may be
	 * blocking the test. Handles, in order:
	 *  1. "Turn on Notifications" onboarding screen - taps SKIP
	 *  2. System notification permission dialog - taps "Don't allow"
	 *  3. Feed tutorial walkthrough (5 steps) - taps Skip
	 *  4. Units of measurement modal - taps Submit (accepts defaults)
	 *  5. Profile tutorial walkthrough (2 steps) - taps Skip
	 *
	 * Idempotent: each popup check uses a short 2-second wait and
	 * silently moves on if not present. Safe to call multiple times
	 * throughout the test.
	 *
	 * Why this exists: on fresh-install / first-login state, the
	 * app shows a chain of onboarding overlays after login. The
	 * 5-step Feed tutorial auto-progresses and lands the user on
	 * the Profile screen with another tutorial running on top. By
	 * the time the test calls ClickNotificationsIcon, the app is
	 * sitting on Profile (where feed_image_sport doesn't exist).
	 * Without dismissing these, the test times out chasing an
	 * element that's structurally absent from the current screen.
	 *
	 * Recommended call sites in test classes:
	 *  - Right after login (before profile.navigateToProfileScreen)
	 *  - Right before ClickFeed (catches Profile tutorial that fires
	 *    after navigateToProfileScreen)
	 */
	public void DismissAllOnboarding() {
		System.out.println("===> DismissAllOnboarding");
		// 1. Post-login "Turn on Notifications" SKIP (uppercase variant)
		tapIfVisible("//android.widget.TextView[@text=\"SKIP\"]",
				"'Turn on Notifications' SKIP");
		// 2. Android system permission dialog (Don't allow)
		//    Try both straight + curly apostrophe variants since Samsung
		//    builds sometimes substitute one for the other in this dialog.
		tapIfVisible("//android.widget.Button[@text=\"Don\u2019t allow\"]",
				"system permission 'Don't allow' (curly apostrophe)");
		tapIfVisible("//android.widget.Button[@text=\"Don't allow\"]",
				"system permission 'Don't allow' (straight apostrophe)");
		// 3. Tutorial walkthrough first Skip (Feed tutorial 5-step or
		//    Profile tutorial 2-step - same button text)
		tapIfVisible("//android.widget.TextView[@text=\"Skip\"]",
				"tutorial Skip (1st pass)");
		// 4. Units of measurement modal - accept default selections
		tapIfVisible("//android.widget.TextView[@text=\"Submit\"]",
				"units of measurement Submit");
		// 5. Any remaining tutorial Skip (Profile tutorial may fire
		//    AFTER Feed tutorial was dismissed)
		tapIfVisible("//android.widget.TextView[@text=\"Skip\"]",
				"tutorial Skip (2nd pass)");
		System.out.println("[OK]       DismissAllOnboarding completed");
	}

	// ================================================================
	// =====           PRIVATE HELPERS                           ======
	// ================================================================

	/**
	 * Tap element matching xpath if it appears within a short wait.
	 * Silently ignores if not present. Logs every successful tap so
	 * we can see in the report exactly which popups fired on this
	 * device/state.
	 */
	private void tapIfVisible(String xpath, String description) {
		try {
			WebDriverWait shortWait = new WebDriverWait(
					driver, Duration.ofSeconds(2));
			WebElement el = shortWait.until(
					ExpectedConditions.elementToBeClickable(
							AppiumBy.xpath(xpath)));
			el.click();
			System.out.println("[OK]       Dismissed: " + description);
			Thread.sleep(1200);  // brief settle for the transition animation
		} catch (Exception ignored) {
			// element not present - expected for popups that don't fire
		}
	}

	/**
	 * Scroll the chip row's HorizontalScrollView forward (gesture
	 * moves right-to-left in screen space, content shifts left,
	 * revealing items at the END of the chip list - e.g., "Mentions"
	 * on the Notifications tab or "Park Groups" on the Inbox tab).
	 *
	 * Uses UiAutomator2's setAsHorizontalList().scrollForward() via
	 * the androidUIAutomator strategy. This goes through the Android
	 * Accessibility Service - no synthetic touch events - so the
	 * parent Notifications/Inbox ViewPager can't intercept it.
	 *
	 * Note on findElements: scrollForward() returns boolean, not a
	 * UI element. findElement would throw because the chain doesn't
	 * end with an element-producing call. findElements (plural)
	 * returns an empty list instead, while the scroll action still
	 * executes on the device.
	 */
	private void scrollChipRowForward() {
		String scrollSelector =
				"new UiScrollable("
				+ "new UiSelector().className(\"android.widget.HorizontalScrollView\")"
				+ ").setAsHorizontalList()"
				+ ".scrollForward()";
		driver.findElements(AppiumBy.androidUIAutomator(scrollSelector));
		System.out.println("[OK]       Scrolled chip row left (forward)");
	}

	/** Brief settle delay after each tap to let UI transitions complete. */
	private void sleepShort() {
		try {
			Thread.sleep(1200);
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}
	}
}