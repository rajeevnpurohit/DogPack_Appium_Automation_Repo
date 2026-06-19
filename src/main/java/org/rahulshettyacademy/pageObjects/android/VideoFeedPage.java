package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;
import java.util.Arrays;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.rahulshettyacademy.utils.AndroidActions;

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
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Video Feed\"]")
	private WebElement videoFeedTab;

	// 2 - Like (action rail child [1]).
	@AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[1]/android.view.ViewGroup/android.widget.ImageView")
	private WebElement likeButton;

	// 3 - Comment icon (action rail child [3]).
	@AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[3]/android.widget.ImageView")
	private WebElement commentIcon;

	// 4 - Bottom-sheet handle - long-pressed and dragged down to close the
	// comment box (see closeCommentBox()).
	@AndroidFindBy(xpath = "//android.widget.SeekBar[@content-desc=\"Bottom sheet handle\"]/android.view.ViewGroup")
	private WebElement sheetHandle;

	// 5 - Share (action rail child [4], direct ImageView - distinct from
	// commentIcon at child [3] and saveVideo at child [4]/ViewGroup).
	@AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[4]/android.widget.ImageView")
	private WebElement shareButton;

	// 6 - Copy link (share sheet) - real content-desc, robust locator.
	@AndroidFindBy(xpath = "//android.widget.FrameLayout[@content-desc=\"Copy link\"]/android.widget.ImageView")
	private WebElement copyLink;

	// 7 - Save video (action rail child [5] / ViewGroup).
	@AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[5]/android.view.ViewGroup/android.widget.ImageView")
	private WebElement saveVideo;

	// 8 - Three dots / more (action rail child [5] / ImageView).
	@AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[5]/android.widget.ImageView")
	private WebElement threeDots;

	// Mute / unmute toggle - real content-desc (robust locator).
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"feed-video-mute\"]/android.widget.ImageView")
	private WebElement muteButton;

	// ================================================================
	// =====                     ACTIONS                         ======
	// ================================================================

	/** 1 - open the Video Feed (center of the home screen). */
	public void clickVideoFeed() {
		wait.until(ExpectedConditions.visibilityOf(videoFeedTab));
		wait.until(ExpectedConditions.elementToBeClickable(videoFeedTab)).click();
		System.out.println("[ACTION] Clicked Video Feed");
	}

	/** 2 - like the current video. */
	public void clickLike() {
		wait.until(ExpectedConditions.visibilityOf(likeButton));
		wait.until(ExpectedConditions.elementToBeClickable(likeButton)).click();
		System.out.println("[ACTION] Clicked Like button");
	}

	/** 3 - open the comment box. */
	public void clickComment() {
		wait.until(ExpectedConditions.visibilityOf(commentIcon));
		wait.until(ExpectedConditions.elementToBeClickable(commentIcon)).click();
		System.out.println("[ACTION] Clicked Comment icon");
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

	/** 8 - open the three-dots / more menu. */
	public void clickThreeDots() {
		wait.until(ExpectedConditions.visibilityOf(threeDots));
		wait.until(ExpectedConditions.elementToBeClickable(threeDots)).click();
		System.out.println("[ACTION] Clicked three dots (more)");
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