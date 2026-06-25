package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;
import java.util.Arrays;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.rahulshettyacademy.utils.AndroidActions;
import io.appium.java_client.AppiumBy;
import java.util.List;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

/**
 * VideoFeedPage - page object for the Video Feed feature (center of the home
 * screen). Holds the locators and actions for the Video Feed tab and the
 * per-video controls: like, comment (+ close via swipe-down on the sheet
 * handle), share,
 * copy link, save video, and the three-dots / more menu.
 *
 * <p>Follows the POM pattern used by the other page objects (extends
 * {@link AndroidActions}, own {@code wait}, PageFactory-initialised locators).
 */
public class VideoFeedPage extends AndroidActions {

	AndroidDriver driver;
	WebDriverWait wait;

	// Username captured from the block-confirmation dialog (for later unblock in Settings).
	private static String blockedUsername;

	public VideoFeedPage(AndroidDriver driver) {
		super(driver);
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	// ================================================================
	// =====                    LOCATORS                         ======
	// ================================================================

	// 1 - Video Feed tab (center of the home screen).
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"feed-VideoFeed\"]/android.view.ViewGroup")
	private WebElement videoFeedTab;

	// 2 - Like (action rail child [1]).
	@AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[1]/android.view.ViewGroup/android.widget.ImageView")
	private WebElement likeButton;

	// 3 - Comment icon (action rail child [3]).
	@AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[3]/android.widget.ImageView")
	private WebElement commentIcon;

	// Comment composer (reused content-desc locators - same component as HomePage).
	@AndroidFindBy(xpath = "//android.widget.EditText[@content-desc=\"comment-reply-TextInput\"]")
	private WebElement commentTextBox;

	@AndroidFindBy(accessibility = "comment-reply-send")
	private WebElement commentSendBtn;

	// 4 - Bottom-sheet handle - long-pressed and dragged down to close the
	// comment box (see closeCommentBox()).
	@AndroidFindBy(xpath = "//android.widget.SeekBar[@content-desc=\"Bottom sheet handle\"]/android.view.ViewGroup")
	private WebElement sheetHandle;

	// 5 - Share (action rail child [4], direct ImageView - distinct from
	// commentIcon at child [3] and saveVideo at child [4]/ViewGroup).
	@AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[4]/android.widget.ImageView")
	private WebElement shareButton;

	// 6 - Copy link (share sheet) - real content-desc, robust locator.
	@AndroidFindBy(xpath = "//android.widget.FrameLayout[@content-desc=\"Copy\"]/android.widget.ImageView")
	private WebElement copyLink;

	// 7 - Save video (action rail child [5] / ViewGroup).
	@AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[5]/android.view.ViewGroup/android.widget.ImageView")
	private WebElement saveVideo;

	// 8 - Three dots / more (action rail child [5] / ImageView).
	@AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[6]/android.widget.ImageView")
	private WebElement threeDots;

	// Three-dots / more menu - report actions.
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Report Content as inappropriate\"]")
	private WebElement reportContentInappropriate;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Confirm\"]")
	private WebElement confirmReport;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Report User as inappropriate\"]")
	private WebElement reportUserInappropriate;

	@AndroidFindBy(xpath = "//android.widget.EditText[@text=\"Type a message\"]")
	private WebElement typeMessageBox;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Submit\"]")
	private WebElement submitReport;

	// Three-dots / more menu - Block User option.
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Block User\"]")
	private WebElement blockUserOption;

	// Block-confirmation dialog text (contains the dynamic @username).
	@AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, \"Are you sure you want to block\")]")
	private WebElement blockConfirmText;

	// Mute / unmute toggle - real content-desc (robust locator).
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"feed-video-mute\"]/android.widget.ImageView")
	private WebElement muteButton;

	// ================================================================
	// =====                     ACTIONS                         ======
	// ================================================================

	/**
	 * 1 - open the Video Feed (center of the home screen).
	 * The entry is gated by a remote-config flag and can render late, and the
	 * first tap on the TouchableOpacity sometimes doesn't register - so retry
	 * the click for up to 30s and confirm we actually landed on the feed
	 * (the video mute control appears only inside the Video Feed).
	 */
	public void clickVideoFeed() {
		By navMarker = AppiumBy.accessibilityId("feed-video-mute");
		long end = System.currentTimeMillis() + 30000;
		int attempts = 0;
		while (System.currentTimeMillis() < end) {
			// already on the Video Feed?
			if (!driver.findElements(navMarker).isEmpty()) {
				System.out.println("[ACTION] Video Feed opened after " + attempts + " click attempt(s)");
				return;
			}
			attempts++;
			try {
				new WebDriverWait(driver, Duration.ofSeconds(2))
						.until(ExpectedConditions.elementToBeClickable(videoFeedTab)).click();
				System.out.println("[ACTION] Clicked Video Feed (attempt " + attempts + ")");
			} catch (Exception e) {
				// not rendered / not clickable yet - keep waiting
			}
			try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
		}
		// final check in case the feed opened right at the deadline
		if (!driver.findElements(navMarker).isEmpty()) {
			System.out.println("[ACTION] Video Feed opened (late)");
			return;
		}
		throw new RuntimeException("Video Feed did not open within 30s after "
				+ attempts + " click attempts (icon may be hidden by the isShowReels remote-config flag).");
	}

	/** 2 - like the current video. */
	public void clickLike() {
		wait.until(ExpectedConditions.visibilityOf(likeButton));
		wait.until(ExpectedConditions.elementToBeClickable(likeButton)).click();
		System.out.println("[ACTION] Clicked Like button");
	}

	/** 3 - open the comment box, type a comment, and submit it. */
	public void clickComment() {
		// (1) open the comment box
		wait.until(ExpectedConditions.visibilityOf(commentIcon));
		wait.until(ExpectedConditions.elementToBeClickable(commentIcon)).click();
		System.out.println("[ACTION] Clicked Comment icon");

		// (2) focus the text box
		wait.until(ExpectedConditions.visibilityOf(commentTextBox));
		wait.until(ExpectedConditions.elementToBeClickable(commentTextBox)).click();

		// (3) enter the comment text
		commentTextBox.sendKeys("I like it!");
		System.out.println("[INPUT] Comment text typed: I like it!");

		// (4) submit the comment
		wait.until(ExpectedConditions.visibilityOf(commentSendBtn));
		wait.until(ExpectedConditions.elementToBeClickable(commentSendBtn)).click();
		System.out.println("[ACTION] Comment submitted");
	}

	/**
	 * 4 - close the comment box by long-pressing the bottom-sheet handle
	 * and swiping it down. W3C touch sequence: press on the handle, hold
	 * briefly (long press), drag down to near the bottom of the screen,
	 * then release.
	 */
	public void closeCommentBox() {
		WebElement handle = wait.until(ExpectedConditions.visibilityOf(sheetHandle));
		Point loc = handle.getLocation();
		Dimension sz = handle.getSize();
		int startX = loc.getX() + sz.getWidth() / 2;
		int startY = loc.getY() + sz.getHeight() / 2;
		int screenH = driver.manage().window().getSize().getHeight();
		int endY = (int) (screenH * 0.95);

		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence drag = new Sequence(finger, 1);
		drag.addAction(finger.createPointerMove(Duration.ZERO,
				PointerInput.Origin.viewport(), startX, startY));
		drag.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		drag.addAction(new Pause(finger, Duration.ofMillis(800)));   // long press
		drag.addAction(finger.createPointerMove(Duration.ofMillis(700),
				PointerInput.Origin.viewport(), startX, endY));         // swipe down
		drag.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		driver.perform(Arrays.asList(drag));
		System.out.println("[ACTION] Long-pressed sheet handle and swiped down (close comment box)");
	}

	/** 5 - open Share. */
	public void clickShare() {
		wait.until(ExpectedConditions.visibilityOf(shareButton));
		wait.until(ExpectedConditions.elementToBeClickable(shareButton)).click();
		System.out.println("[ACTION] Clicked Share");
	}

	/** 6 - copy the video link from the share sheet. */
	public void clickCopyLink() {
		wait.until(ExpectedConditions.visibilityOf(copyLink));
		wait.until(ExpectedConditions.elementToBeClickable(copyLink)).click();
		System.out.println("[ACTION] Clicked Copy link");
	}

	/** 7 - save the current video. */
	public void clickSaveVideo() {
		wait.until(ExpectedConditions.visibilityOf(saveVideo));
		wait.until(ExpectedConditions.elementToBeClickable(saveVideo)).click();
		System.out.println("[ACTION] Clicked Save video");
	}

	/** 7b - unsave the video: same toggle button as Save, clicked again. */
	public void clickUnsaveVideo() {
		wait.until(ExpectedConditions.visibilityOf(saveVideo));
		wait.until(ExpectedConditions.elementToBeClickable(saveVideo)).click();
		System.out.println("[ACTION] Clicked Save again (unsave video)");
	}

	/** 8 - open the three-dots / more menu and run the report actions. */
	public void clickThreeDots() {
		wait.until(ExpectedConditions.visibilityOf(threeDots));
		wait.until(ExpectedConditions.elementToBeClickable(threeDots)).click();
		System.out.println("[ACTION] Clicked three dots (more)");

		// (1) report content as inappropriate
		wait.until(ExpectedConditions.visibilityOf(reportContentInappropriate));
		wait.until(ExpectedConditions.elementToBeClickable(reportContentInappropriate)).click();
		System.out.println("[ACTION] Clicked 'Report Content as inappropriate'");

		// (2) confirm
		wait.until(ExpectedConditions.visibilityOf(confirmReport));
		wait.until(ExpectedConditions.elementToBeClickable(confirmReport)).click();
		System.out.println("[ACTION] Clicked Confirm");

		// reopen the three-dots menu before the next report action
		wait.until(ExpectedConditions.visibilityOf(threeDots));
		wait.until(ExpectedConditions.elementToBeClickable(threeDots)).click();
		System.out.println("[ACTION] Reopened three dots (more)");

		// (3) report user as inappropriate
		wait.until(ExpectedConditions.visibilityOf(reportUserInappropriate));
		wait.until(ExpectedConditions.elementToBeClickable(reportUserInappropriate)).click();
		System.out.println("[ACTION] Clicked 'Report User as inappropriate'");

		// (4) type a message
		wait.until(ExpectedConditions.visibilityOf(typeMessageBox));
		wait.until(ExpectedConditions.elementToBeClickable(typeMessageBox)).click();
		typeMessageBox.sendKeys("User inappropriate. Only a Test msg");
		System.out.println("[INPUT] Report message typed");

		// (5) submit
		wait.until(ExpectedConditions.visibilityOf(submitReport));
		wait.until(ExpectedConditions.elementToBeClickable(submitReport)).click();
		System.out.println("[ACTION] Clicked Submit");

		// (8) reopen the three-dots menu for the block flow
		wait.until(ExpectedConditions.visibilityOf(threeDots));
		wait.until(ExpectedConditions.elementToBeClickable(threeDots)).click();
		System.out.println("[ACTION] Reopened three dots (more) for block");

		// (9) click Block User
		wait.until(ExpectedConditions.visibilityOf(blockUserOption));
		wait.until(ExpectedConditions.elementToBeClickable(blockUserOption)).click();
		System.out.println("[ACTION] Clicked Block User");

		// (10) fetch the block-confirmation dialog text
		String blockText = wait.until(ExpectedConditions.visibilityOf(blockConfirmText)).getText();
		System.out.println("[INFO] Block dialog text: " + blockText);

		// (11) extract the @username (between '@' and '?') and save it for later unblock
		int at = blockText.indexOf("@");
		int q = blockText.indexOf("?");
		if (at >= 0 && q > at) {
			blockedUsername = blockText.substring(at + 1, q).trim();
			System.out.println("[INFO] Extracted blocked username: " + blockedUsername);
		} else {
			System.out.println("[WARN] Could not extract username from block dialog text");
		}
		// (12) confirm (completes the block, dismisses the dialog)
		wait.until(ExpectedConditions.visibilityOf(confirmReport));
		wait.until(ExpectedConditions.elementToBeClickable(confirmReport)).click();
		System.out.println("[ACTION] Clicked Confirm (block)");

	}

	/** Username captured from the block-confirmation dialog (for later unblock in Settings). */
	public static String getBlockedUsername() {
		return blockedUsername;
	}

	/** 9 - mute the video. */
	public void clickMute() {
		wait.until(ExpectedConditions.visibilityOf(muteButton));
		wait.until(ExpectedConditions.elementToBeClickable(muteButton)).click();
		System.out.println("[ACTION] Clicked Mute");
	}

	/** 10 - unmute the video: same toggle button as Mute, clicked again. */
	public void clickUnmute() {
		wait.until(ExpectedConditions.visibilityOf(muteButton));
		wait.until(ExpectedConditions.elementToBeClickable(muteButton)).click();
		System.out.println("[ACTION] Clicked Unmute");
	}
}