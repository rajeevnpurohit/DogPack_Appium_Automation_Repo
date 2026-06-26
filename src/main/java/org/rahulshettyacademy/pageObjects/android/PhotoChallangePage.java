package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rahulshettyacademy.utils.AndroidActions;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

public class PhotoChallangePage extends AndroidActions {

	AndroidDriver driver;

	public PhotoChallangePage(AndroidDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
	}

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"feed-challenge\"]/android.view.ViewGroup/android.widget.ImageView")
	private WebElement feedChallengeBtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Vote on yesterday’s challenge:\"]")
	private WebElement yesterdayChallengeLabel;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Today’s challenge:\"]")
	private WebElement todayChallengeLabel;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Vote\"]")
	private WebElement voteBtnChallenge;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Enter\"]")
	private WebElement enterBtnChallenge;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_all_button")
	private WebElement allowBtn;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_one_time_button")
	private WebElement allowOneBtn;

	@AndroidFindBy(accessibility = "Join the challenge")
	private WebElement joinTheChallenge;

	@AndroidFindBy(xpath = "(//android.widget.ImageView[@resource-id=\"com.dogpack:id/ivPicture\"])[1]")
	private WebElement selectFirstImage;

	@AndroidFindBy(id = "com.dogpack:id/ps_tv_complete")
	private WebElement cameraRollDone;

	@AndroidFindBy(accessibility = "Crop")
	private WebElement lostDogCrop;

	@AndroidFindBy(accessibility = "Post this photo to the Feed")
	private WebElement postThisPhotoTotheFeed;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Submit photo\"]")
	private WebElement submitPhoto;

	@AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'You have successfully entered the challenge')]")
	private WebElement successMsg;

	@AndroidFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.widget.ImageView")
	private WebElement Change_deleteBtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Cancel\"]")
	private WebElement cancelPhoto;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Delete this photo?\"]")
	private WebElement deleteThisPhotoLabel;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Delete\"]")
	private WebElement deletePhoto;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Back to challenges\"]")
	private WebElement backToChallengesBtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"View entry\"]")
	private WebElement viewEntry;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"View all\"]")
	private WebElement viewAllSingle;

	@AndroidFindBy(xpath = "(//android.widget.TextView[@text=\"View all\"])[1]")
	private WebElement viewAllMultipleOption1;

	@AndroidFindBy(xpath = "(//android.widget.TextView[@text=\"View all\"])[2]")
	private WebElement viewAllMultipleOption2;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Upcoming challenges\"]")
	private WebElement upcomingChallengesLabel;

	public void navigatesToChallengeScreen() {
		System.out.println("[FLOW] navigatesToChallengeScreen: opening the Photo Challenge screen");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(feedChallengeBtn));
		wait.until(ExpectedConditions.elementToBeClickable(feedChallengeBtn)).click();

		wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOf(yesterdayChallengeLabel),
				ExpectedConditions.visibilityOf(todayChallengeLabel)));

	}

	public void scrollToEnter() {
		try {
			driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
					+ ".setAsVerticalList()" + ".scrollTextIntoView(\"Enter\")"));
		} catch (Exception e) {
			// fallback (kuch UIs me yeh better kaam karta hai)
			driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
					+ ".scrollIntoView(new UiSelector().text(\"Enter\"))"));
		}
	}

	public void joinChallenge() {
		System.out.println("[FLOW] joinChallenge: entering today's challenge and submitting a photo");
		scrollToEnter();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement enter = wait.until(ExpectedConditions
				.elementToBeClickable(AppiumBy.androidUIAutomator("new UiSelector().text(\"Enter\")")));
		System.out.println("[ACTION] Tapping 'Enter' to enter the challenge");
		enter.click();

		// 1. Open the Join screen and tap 'Join the challenge'
		wait.until(ExpectedConditions.visibilityOf(joinTheChallenge));
		wait.until(ExpectedConditions.elementToBeClickable(joinTheChallenge)).click();

		WebDriverWait permWait = new WebDriverWait(driver, Duration.ofSeconds(5));
		// 2. camera / record-video permission -> 'While using the app'
		try {
			permWait.until(ExpectedConditions.elementToBeClickable(whileUsingAppBtn)).click();
			System.out.println("[ACTION] Camera/record permission -> 'While using the app'");
		} catch (Exception e) {
			System.out.println("[INFO] Camera/record permission dialog not shown - skipping");
		}
		// 3. photos & videos permission -> 'Allow all'
		try {
			permWait.until(ExpectedConditions.elementToBeClickable(allowBtn)).click();
			System.out.println("[ACTION] Photos/videos permission -> 'Allow all'");
		} catch (Exception e) {
			System.out.println("[INFO] Photos/videos permission dialog not shown - skipping");
		}

		wait.until(ExpectedConditions.elementToBeClickable(selectFirstImage)).click();
		wait.until(ExpectedConditions.elementToBeClickable(cameraRollDone)).click();
		wait.until(ExpectedConditions.elementToBeClickable(lostDogCrop)).click();

		wait.until(ExpectedConditions.visibilityOf(postThisPhotoTotheFeed));
		wait.until(ExpectedConditions.elementToBeClickable(postThisPhotoTotheFeed)).click();
		wait.until(ExpectedConditions.elementToBeClickable(submitPhoto)).click();

	}

	public void DeleteChallengePhotoCancelOption() {
		System.out.println("[FLOW] DeleteChallengePhotoCancelOption: opening delete dialog then Cancel");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			// wait.until(ExpectedConditions.visibilityOf(Change_deleteBtn));
			// wait.until(ExpectedConditions.elementToBeClickable(Change_deleteBtn)).click();
			// Open change/delete sheet
			wait.ignoring(org.openqa.selenium.StaleElementReferenceException.class)
					.until(ExpectedConditions.elementToBeClickable(Change_deleteBtn)).click();

			wait.until(ExpectedConditions.visibilityOf(deleteThisPhotoLabel));

			wait.until(ExpectedConditions.visibilityOf(cancelPhoto));
			wait.until(ExpectedConditions.elementToBeClickable(cancelPhoto)).click();
		} catch (Exception e) {
			System.out.println("[WARN]  Delete Challenge Photo Cancel Option Not Performed");
		}

	}

	public void DeleteChallengePhotoDeleteOption() {
		System.out.println("[FLOW] DeleteChallengePhotoDeleteOption: opening delete dialog then Delete");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		wait.until(ExpectedConditions.visibilityOf(Change_deleteBtn));
		wait.until(ExpectedConditions.elementToBeClickable(Change_deleteBtn)).click();

		wait.until(ExpectedConditions.visibilityOf(deleteThisPhotoLabel));
		wait.until(ExpectedConditions.visibilityOf(deletePhoto));
		wait.until(ExpectedConditions.elementToBeClickable(deletePhoto)).click();

		wait.until(ExpectedConditions.visibilityOf(joinTheChallenge)); // back to join screen
	}

	public void joinChallengeAfterDeletePhoto() {
		System.out.println("[FLOW] joinChallengeAfterDeletePhoto: re-joining after deleting the photo");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		wait.until(ExpectedConditions.visibilityOf(joinTheChallenge));
		wait.until(ExpectedConditions.elementToBeClickable(joinTheChallenge)).click();

		try {
			if (allowBtn.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(allowBtn)).click();
			}
		} catch (Exception e) {
			System.out.println("[WARN] Not Asked for permission");
		}

		try {
			if (allowOneBtn.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(allowOneBtn)).click();
			}
		} catch (Exception e) {
			System.out.println("[WARN] Not Asked for permission");
		}

		wait.until(ExpectedConditions.elementToBeClickable(selectFirstImage)).click();
		wait.until(ExpectedConditions.elementToBeClickable(cameraRollDone)).click();
		wait.until(ExpectedConditions.elementToBeClickable(lostDogCrop)).click();

		wait.until(ExpectedConditions.visibilityOf(postThisPhotoTotheFeed));
		wait.until(ExpectedConditions.elementToBeClickable(postThisPhotoTotheFeed)).click();
		wait.until(ExpectedConditions.elementToBeClickable(submitPhoto)).click();
		wait.until(ExpectedConditions.visibilityOf(backToChallengesBtn));
		wait.until(ExpectedConditions.elementToBeClickable(backToChallengesBtn)).click();
		wait.until(ExpectedConditions.visibilityOf(viewEntry));

	}

	public void scrollToPreviousChallenges() {
		try {
			driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
					+ ".setAsVerticalList()" + ".scrollTextIntoView(\"Previous challenges and winners\")"));
		} catch (Exception e) {
			// fallback (kuch UIs me yeh better kaam karta hai)
			driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
					+ ".scrollIntoView(new UiSelector().text(\"Previous challenges and winners\"))"));
		}
	}

	public void clickViewAllFirstOption() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		// Count how many "View all" dikh rahe hain
		int count = driver.findElements(AppiumBy.xpath("//android.widget.TextView[@text='View all']")).size();

		if (count == 1) {
			// Sirf ek button hai -> single wala click
			wait.until(ExpectedConditions.elementToBeClickable(viewAllSingle)).click();
		} else if (count > 1) {
			// Multiple buttons hain -> pehla wala click
			wait.until(ExpectedConditions.elementToBeClickable(viewAllMultipleOption1)).click();
		} else {
			// Screen par abhi dikh hi nahi raha -> scroll karke retry
			try {
				driver.findElement(AppiumBy.androidUIAutomator(
						"new UiScrollable(new UiSelector().scrollable(true))" + ".scrollTextIntoView(\"View all\")"));
			} catch (Exception ignore) {
			}

			// Retry after scroll
			count = driver.findElements(AppiumBy.xpath("//android.widget.TextView[@text='View all']")).size();

			if (count == 0) {
				throw new RuntimeException("'View all' not found after scroll.");
			} else if (count == 1) {
				wait.until(ExpectedConditions.elementToBeClickable(viewAllSingle)).click();
			} else {
				wait.until(ExpectedConditions.elementToBeClickable(viewAllMultipleOption1)).click();
			}
		}
	}

	private final By upcomingChallengesLabelBy = AppiumBy
			.xpath("//android.widget.TextView[@text='Upcoming challenges']");

	public void ViewAllUpcomingChallenges() {
		System.out.println("[FLOW] ViewAllUpcomingChallenges: View all -> upcoming challenges");
		scrollToPreviousChallenges();
		clickViewAllFirstOption();
		scrollDownTwice();
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(upcomingChallengesLabelBy));
		} catch (Exception e) {
			// off-screen hua toh scroll karke dubara try
			driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
					+ ".scrollTextIntoView(\"Upcoming challenges\")"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(upcomingChallengesLabelBy));
		}

	}

	public void clickViewAllSecondOption() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));

		By allViewAll = AppiumBy.xpath("//android.widget.TextView[@text='View all']");
		By secondViewAll = AppiumBy.xpath("(//android.widget.TextView[@text='View all'])[2]");

		int count = driver.findElements(allViewAll).size();

		// agar screen par na mile to ek scroll try
		if (count == 0) {
			try {
				driver.findElement(AppiumBy.androidUIAutomator(
						"new UiScrollable(new UiSelector().scrollable(true))" + ".scrollTextIntoView(\"View all\")"));
			} catch (Exception ignore) {
			}
			count = driver.findElements(allViewAll).size();
			if (count == 0)
				throw new RuntimeException("'View all' not found after scroll.");
		}

		if (count >= 2) {
			// multiple hain -> specifically SECOND pe click
			try {
				wait.until(ExpectedConditions.elementToBeClickable(viewAllMultipleOption2)).click();
			} catch (StaleElementReferenceException | TimeoutException e) {
				// fallback: By se dubara locate karke click
				wait.until(ExpectedConditions.elementToBeClickable(secondViewAll)).click();
			}
		} else {
			// sirf ek hai -> single pe click
			wait.until(ExpectedConditions.elementToBeClickable(viewAllSingle)).click();
		}
	}

	private final By PreviousChallengesLabelBy = AppiumBy
			.xpath("//android.widget.TextView[@text='Previous challenges and winners']");

	public void ViewAllPreviousChallenges() {
		System.out.println("[FLOW] ViewAllPreviousChallenges: View all -> previous challenges");
		scrollToPreviousChallenges();
		clickViewAllSecondOption();
		scrollDownTwice();
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(PreviousChallengesLabelBy));
		} catch (Exception e) {
			// off-screen hua toh scroll karke dubara try
			driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
					+ ".scrollTextIntoView(\"Previous challenges and winners\")"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(PreviousChallengesLabelBy));
		}

	}

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"upcomming-0\"]/android.view.ViewGroup[1]/android.widget.ImageView")
	private WebElement upComingOption1;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Go to settings\"]")
	private WebElement goToSettings;

	@AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"notification-toggleKey\"])[12]/android.widget.ImageView")
	private WebElement notificationToggleBtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Got it\"]")
	private WebElement gotIt;

	@AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"notification-toggleKey\"])[1]/android.widget.ImageView")
	private WebElement notificationToggle1;

	public void TryToEnterInUpcomingChallenge() {
		System.out.println("[FLOW] TryToEnterInUpcomingChallenge: tapping an upcoming challenge");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			// ✅ Case 1: Check if upComingOption1 exists and click
			if (upComingOption1.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(upComingOption1)).click();
				System.out.println("[ACTION] Clicked on Upcoming Option");

				// ✅ Case 2: Handle popup after clicking
				try {
					if (driver.findElements(By.xpath("//android.widget.TextView[@text='Go to settings']")).size() > 0) {
						wait.until(ExpectedConditions.elementToBeClickable(goToSettings)).click();
						System.out.println("[ACTION] Clicked on 'Go to settings'");

						wait.until(ExpectedConditions.visibilityOf(notificationToggle1));

						driver.findElement(
								AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
										+ ".scrollIntoView(new UiSelector().text(\"Challenge Notification\"))"));
						System.out.println("[ACTION] Scrolled to 'Challenge Notification'");

						wait.until(ExpectedConditions.elementToBeClickable(notificationToggleBtn)).click();
						System.out.println("[ACTION] Toggled 'Challenge Notification'");

						driver.pressKey(new KeyEvent(AndroidKey.BACK));
						System.out.println("[FLOW] Navigated back");

						wait.until(ExpectedConditions.visibilityOfElementLocated(PreviousChallengesLabelBy));

					} else if (driver.findElements(By.xpath("//android.widget.TextView[@text='Got it']")).size() > 0) {
						wait.until(ExpectedConditions.elementToBeClickable(gotIt)).click();
						System.out.println("[ACTION] Clicked on 'Got it'");

						wait.until(ExpectedConditions.visibilityOfElementLocated(PreviousChallengesLabelBy));
					} else {
						System.out.println("[INFO] No known popup option found");
					}
				} catch (Exception e) {
					System.out.println("[WARN] Popup handling failed: " + e.getMessage());
				}

			} else {
				System.out.println("[WARN] upComingOption1 does not exist");
			}

		} catch (Exception e) {
			System.out.println("[WARN] upComingOption1 value does not exist or click failed: " + e.getMessage());
		}
	}

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"previous-0\"]/android.view.ViewGroup[1]/android.widget.ImageView")
	private WebElement previous0;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"View winners\"]")
	private WebElement viewWinners;

	@AndroidFindBy(accessibility = "profile_user1")
	private WebElement winnerOne;

	public void ViewWinnersOfPreviousChallenge() {
		System.out.println("[FLOW] ViewWinnersOfPreviousChallenge: opening winners of a previous challenge");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			// ✅ Step 1: Check if previous0 is visible and click
			if (previous0.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(previous0)).click();
				System.out.println("[ACTION] Clicked on Previous Challenge Option");

				// ✅ Step 2: Wait for "View winners" button and click
				wait.until(ExpectedConditions.visibilityOf(viewWinners));
				wait.until(ExpectedConditions.elementToBeClickable(viewWinners)).click();
				System.out.println("[ACTION] Clicked on 'View winners'");

				// ✅ Step 3: Wait for winner profile and click
				wait.until(ExpectedConditions.visibilityOf(winnerOne));
				wait.until(ExpectedConditions.elementToBeClickable(winnerOne)).click();
				System.out.println("[ACTION] Clicked on Winner Profile");

				// ✅ Step 4: Press back to close modal window
				driver.pressKey(new KeyEvent(AndroidKey.BACK));
				System.out.println("[FLOW] Closed modal window");

				// ✅ Step 5: Wait for winner profile to reappear
				wait.until(ExpectedConditions.visibilityOf(winnerOne));
				System.out.println("[INFO] Winner profile visible again");

				// ✅ Step 6: Press back to return to challenge screen
				driver.pressKey(new KeyEvent(AndroidKey.BACK));
				System.out.println("[FLOW] Navigated back to challenge screen");

				// ✅ Step 7: Wait for PreviousChallengesLabelBy to confirm screen
				wait.until(ExpectedConditions.visibilityOfElementLocated(PreviousChallengesLabelBy));
				System.out.println("[INFO] Challenge screen loaded");

			} else {
				System.out.println("[WARN] Previous option not displayed");
			}

		} catch (Exception e) {
			System.out.println("[WARN] Error in viewing winners: " + e.getMessage());
		}
	}

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Winner0\"]")
	private WebElement current;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Winner1\"]")
	private WebElement Last7Days;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Winner2\"]")
	private WebElement Last30Days;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Winner3\"]")
	private WebElement allTime;

	public void navigatesToLeaderShipBoard() {
		System.out.println("[FLOW] navigatesToLeaderShipBoard: opening the Leaderboard");
		// Step 1: Click on Leaderboard tab using UIAutomator
		driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().text(\"Leaderboard\")")).click();

		// Step 2: Wait until any one of the winner elements is visible
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOf(current),
				ExpectedConditions.visibilityOf(Last7Days), ExpectedConditions.visibilityOf(Last30Days),
				ExpectedConditions.visibilityOf(allTime)));

		System.out.println("[FLOW] Navigated to Leaderboard screen successfully.");
	}

	public void navigatesToLast7Days() {
		System.out.println("[FLOW] navigatesToLast7Days: switching Last 7 / 30 days / All-time tabs");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			// Wait for visibility and clickability of Last7Days tab
			wait.until(ExpectedConditions.visibilityOf(Last7Days));
			wait.until(ExpectedConditions.elementToBeClickable(Last7Days)).click();
			System.out.println("[ACTION] Clicked on 'Last 7 Days' tab");

			// Wait for winnerOne to be visible as confirmation
			wait.until(ExpectedConditions.visibilityOf(winnerOne));
			System.out.println("[FLOW] 'winnerOne' element is visible. Navigation successful.");

			// Wait for visibility and clickability of Last30Days tab
			wait.until(ExpectedConditions.visibilityOf(Last30Days));
			wait.until(ExpectedConditions.elementToBeClickable(Last30Days)).click();
			System.out.println("[ACTION] Clicked on 'Last 30 Days' tab");

			// Wait for winnerOne to be visible as confirmation
			wait.until(ExpectedConditions.visibilityOf(winnerOne));
			System.out.println("[FLOW] 'winnerOne' element is visible. Navigation successful.");
			
			// Wait for visibility and clickability of AllTime tab
			wait.until(ExpectedConditions.visibilityOf(allTime));
			wait.until(ExpectedConditions.elementToBeClickable(allTime)).click();
			System.out.println("[ACTION] Clicked on 'AllTime' tab");
			
			// Wait for winnerOne to be visible as confirmation
			wait.until(ExpectedConditions.visibilityOf(winnerOne));
			System.out.println("[FLOW] 'winnerOne' element is visible. Navigation successful.");

		} catch (TimeoutException e) {
			System.out.println("[WARN] Timeout while navigating to 'Last 7 Days' tab: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("[WARN] Unexpected error: " + e.getMessage());
		}
	}

	
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"other_messag\"]")
	private WebElement winnerMessageBtn;

	public void navigatesToWinnerProfile() {
		System.out.println("[FLOW] navigatesToWinnerProfile: opening a winner profile");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			// Step 1: Wait for winnerOne to be visible and clickable
			wait.until(ExpectedConditions.visibilityOf(winnerOne));
			wait.until(ExpectedConditions.elementToBeClickable(winnerOne)).click();
			System.out.println("[ACTION] Clicked on winner profile");

			// Step 2: Wait for winnerMessageBtn to confirm navigation
			wait.until(ExpectedConditions.visibilityOf(winnerMessageBtn));
			System.out.println("[FLOW] Winner message button is visible. Navigation successful.");

		} catch (TimeoutException e) {
			System.out.println("[WARN] Timeout while navigating to winner profile: " + e.getMessage());
			// Optionally take screenshot or log to report
		} catch (Exception e) {
			System.out.println("[WARN] Unexpected error during winner profile navigation: " + e.getMessage());
		}
	}

	@AndroidFindBy(accessibility = "chat-input")
	private WebElement chatInput;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"chat-imgComp\"]/android.widget.ImageView")
	private WebElement chatSendBtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@resource-id=\"com.android.permissioncontroller:id/permission_message\"]")
	private WebElement audioPermissionMessage;

	@AndroidFindBy(xpath = "//android.widget.Button[@resource-id=\"com.android.permissioncontroller:id/permission_allow_foreground_only_button\"]")
	private WebElement audioPermissionAllowBtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@resource-id=\"com.android.permissioncontroller:id/permission_message\"]")
	private WebElement photoPermissionMessage;

	@AndroidFindBy(xpath = "//android.widget.Button[@resource-id=\"com.android.permissioncontroller:id/permission_allow_all_button\"]")
	private WebElement photoPermissionAllowBtn;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"chat-gallery\"]/android.widget.ImageView")
	private WebElement chatGallery;

	@AndroidFindBy(xpath = "(//android.widget.TextView[@resource-id=\"com.dogpack:id/tvCheck\"])[1]")
	private WebElement photoSelect;

	@AndroidFindBy(xpath = "//android.widget.TextView[@resource-id=\"com.dogpack:id/ps_tv_complete\"]")
	private WebElement donePhoto;

	@AndroidFindBy(xpath = "//android.widget.Button[@content-desc=\"Crop\"]")
	private WebElement cropPhoto;

	public void MessageWinnerUser() {
		System.out.println("[FLOW] MessageWinnerUser: sending a chat message to the winner");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.visibilityOf(winnerMessageBtn));
		wait.until(ExpectedConditions.elementToBeClickable(winnerMessageBtn)).click();
		wait.until(ExpectedConditions.visibilityOf(chatInput));
		wait.until(ExpectedConditions.elementToBeClickable(chatInput)).sendKeys("Congratulations");
		driver.hideKeyboard();
		wait.until(ExpectedConditions.elementToBeClickable(chatSendBtn)).click();

		try {
			if (audioPermissionMessage.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(audioPermissionAllowBtn)).click();
			}
		} catch (Exception e) {
			System.out.println("[WARN] Audio Permission Popup not displayed.");
		}

		try {
			if (photoPermissionMessage.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(photoPermissionAllowBtn)).click();
			}
		} catch (Exception e) {
			System.out.println("[WARN] photo Permission Popup not displayed.");
		}

//		// Hide Recoding feature using back button
//		driver.pressKey(new KeyEvent(AndroidKey.BACK));
//		try {
//			Thread.sleep(3000);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		wait.until(ExpectedConditions.visibilityOf(winnerMessageBtn));

	}
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"go_to_chat\"]/android.widget.ImageView")
	private WebElement threeDot;	

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Message\"]")
	private WebElement messageAction;
	
	
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Copy Profile URL\"]")
	private WebElement copyURLAction;
	
	
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Report this user as inappropriate\"]")
	private WebElement reportUserInapproAction;
	

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Block user\"]")
	private WebElement blockUser;
	
	
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Cancel\"]")
	private WebElement cancelAction;
	
	@AndroidFindBy(accessibility = "onConfirm")
	private WebElement onConfirmAction;
	
	@AndroidFindBy(accessibility = "onCancel")
	private WebElement onCancelAction;
	
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"User reported successfully.\"]")
	private WebElement reportMessage;

	// --- Report flow: message + image attachment + submit ---
	@AndroidFindBy(xpath = "//android.widget.EditText[@text=\"Type a message\"]")
	private WebElement typeMessageBox;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Upload Image\"]")
	private WebElement uploadImageOption;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_foreground_only_button")
	private WebElement whileUsingAppBtn;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_all_button")
	private WebElement allowAllBtn;

	@AndroidFindBy(xpath = "(//android.widget.TextView[@resource-id=\"com.dogpack:id/tvCheck\"])[4]")
	private WebElement selectImage;

	@AndroidFindBy(xpath = "//android.widget.TextView[@resource-id=\"com.dogpack:id/ps_tv_complete\"]")
	private WebElement doneBtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Submit\"]")
	private WebElement submitReportBtn;

	/**
	 * Click that tolerates StaleElementReferenceException: the action menu
	 * re-renders between locate and click, which can stale the reference.
	 * Re-locates and retries up to 3 times before giving up.
	 */
	private void clickStaleSafe(WebElement el) {
		WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(10));
		for (int attempt = 1; attempt <= 3; attempt++) {
			try {
				w.until(ExpectedConditions.visibilityOf(el));
				w.until(ExpectedConditions.elementToBeClickable(el)).click();
				return;
			} catch (StaleElementReferenceException e) {
				if (attempt == 3) {
					throw e;
				}
				try {
					Thread.sleep(300);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	/**
	 * Best-effort click for conditional UI such as system permission
	 * dialogs. Clicks the element if it appears within a short window;
	 * if it never shows (e.g. permission already granted on a prior run),
	 * it is skipped instead of failing the test.
	 */
	private void clickIfPresent(WebElement el) {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(ExpectedConditions.elementToBeClickable(el)).click();
		} catch (TimeoutException e) {
			System.out.println("[INFO] Optional element not present, skipping.");
		}
	}

	public void ThreeDotActionPerformed() 
	{
		System.out.println("[FLOW] ThreeDotActionPerformed: winner 3-dot menu (message/copy URL/report/block)");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.visibilityOf(threeDot));
		wait.until(ExpectedConditions.elementToBeClickable(threeDot)).click();
		
		clickStaleSafe(messageAction);
		wait.until(ExpectedConditions.visibilityOf(chatInput));
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		
		wait.until(ExpectedConditions.visibilityOf(threeDot));
		wait.until(ExpectedConditions.elementToBeClickable(threeDot)).click();
		wait.until(ExpectedConditions.visibilityOf(copyURLAction));
		wait.until(ExpectedConditions.elementToBeClickable(copyURLAction)).click();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		wait.until(ExpectedConditions.visibilityOf(threeDot));
		wait.until(ExpectedConditions.elementToBeClickable(threeDot)).click();
		wait.until(ExpectedConditions.visibilityOf(reportUserInapproAction));
		wait.until(ExpectedConditions.elementToBeClickable(reportUserInapproAction)).click();

		// Enter a reason in the "Type a message" box
		wait.until(ExpectedConditions.visibilityOf(typeMessageBox));
		typeMessageBox.click();
		typeMessageBox.sendKeys("Reporting user for Automatoin Testing");

		// Attach an image: Upload Image -> grant permission -> pick -> Done
		wait.until(ExpectedConditions.elementToBeClickable(uploadImageOption)).click();
		clickIfPresent(whileUsingAppBtn);   // system permission dialog (best-effort)
		clickIfPresent(allowAllBtn);        // system permission dialog (best-effort)
		wait.until(ExpectedConditions.elementToBeClickable(selectImage)).click();
		wait.until(ExpectedConditions.elementToBeClickable(doneBtn)).click();

		// Submit the report
		wait.until(ExpectedConditions.elementToBeClickable(submitReportBtn)).click();

		// onConfirmAction click DISABLED for now:
		// wait.until(ExpectedConditions.elementToBeClickable(onConfirmAction)).click();
		wait.until(ExpectedConditions.invisibilityOf(reportMessage));
		
		wait.until(ExpectedConditions.visibilityOf(threeDot));
		wait.until(ExpectedConditions.elementToBeClickable(threeDot)).click();
		wait.until(ExpectedConditions.visibilityOf(blockUser));
		wait.until(ExpectedConditions.elementToBeClickable(blockUser)).click();
		wait.until(ExpectedConditions.elementToBeClickable(onCancelAction)).click();
		
		wait.until(ExpectedConditions.visibilityOf(threeDot));
		wait.until(ExpectedConditions.elementToBeClickable(threeDot)).click();
		wait.until(ExpectedConditions.visibilityOf(cancelAction));
		wait.until(ExpectedConditions.elementToBeClickable(cancelAction)).click();
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		wait.until(ExpectedConditions.visibilityOf(winnerOne));
	}
	
	public void navigatesToResultBoard() {
		System.out.println("[FLOW] navigatesToResultBoard: opening My Results");
		// Step 1: Click on the "My Results" tab using xpath
		driver.findElement(AppiumBy.xpath("//android.widget.TextView[@text=\"My Results\"]")).click();

		// Step 2: Wait until any one of the winner elements is visible
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("[FLOW] Navigated to Result screen successfully.");
	}

	public void votingInYesterdayChallenge() {
		System.out.println("[FLOW] votingInYesterdayChallenge: voting on yesterday's challenge");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(voteBtnChallenge));
		wait.until(ExpectedConditions.elementToBeClickable(voteBtnChallenge)).click();

	}

}