package org.rahulshettyacademy.pageObjects.android;

import static java.util.Collections.singletonList;

import java.io.FileInputStream;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rahulshettyacademy.utils.AndroidActions;
import org.testng.Assert;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class MapPage extends AndroidActions {

	AndroidDriver driver;
	Properties testDataProp = new Properties();

	public MapPage(AndroidDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(new AppiumFieldDecorator(driver), this); //

		try {
			FileInputStream fis = new FileInputStream(System.getProperty("user.dir")
					+ "//src//main//java//org//rahulshettyacademy//resources//TestData.properties");
			testDataProp.load(fis);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_foreground_only_button")
	private WebElement whileUsingAppPermission;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_message")
	private WebElement locationPermissionMsg;

	@AndroidFindBy(accessibility = "map-view")
	private WebElement mapViewTab;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"maptype\"]/android.widget.ImageView")
	private WebElement mapType;

	@AndroidFindBy(accessibility = "mapSearchLocation")
	private WebElement mapSearchBar;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Get Current Location\"]")
	private WebElement getCurrentLocation;

	@AndroidFindBy(xpath = "//android.widget.EditText[@text=\"Enter a Location Here \"]")
	private WebElement enterLocationHere;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Montreal, QC, Canada\"]")
	private WebElement MontrealLocation;

	@AndroidFindBy(accessibility = "Default")
	private WebElement mapTypeDefault;

	@AndroidFindBy(accessibility = "Satellite")
	private WebElement mapTypeSatellite;

	@AndroidFindBy(accessibility = "Terrain")
	private WebElement mapTypeTerrain;

	@AndroidFindBy(accessibility = "Traffic")
	private WebElement mapTypeTraffic;

	@AndroidFindBy(accessibility = "lView")
	private WebElement listView;

	@AndroidFindBy(accessibility = "onlyb")
	private WebElement listBusinessBtn;

	@AndroidFindBy(accessibility = "dogbus-action-dog")
	private WebElement labelDogProfile;

	@AndroidFindBy(accessibility = "dogbus-action-dogBusiness")
	private WebElement labelBusinessProfile;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"SuggestPark\"]/android.widget.ImageView")
	private WebElement SuggestBtn;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"markerpin\"]/android.widget.ImageView")
	private WebElement suggestPinMarker;

	@AndroidFindBy(accessibility = "enna")
	private WebElement enterSuggestedName;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"square0\"]/android.widget.ImageView")
	private WebElement dogParkOption;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"square1\"]/android.widget.ImageView")
	private WebElement dogFriendlyAreaOption;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"square2\"]/android.widget.ImageView")
	private WebElement dogBusinessOption;

	@AndroidFindBy(accessibility = "upimsave")
	private WebElement suggestSave;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Let other park goers know which amenities are at the park!\"]")
	private WebElement parkLabel;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"CONFIRM\"]")
	private WebElement parkConfirm;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"LATER\"]")
	private WebElement parkLater;

	@AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"park_right\"])[1]/android.widget.ImageView")
	private WebElement parkChecked;

	@AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"park_close\"])[2]/android.widget.ImageView")
	private WebElement parkCross;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Thank you we are reviewing your suggestion.\"]")
	private WebElement parkSuggestSuccessMessage;

	@AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,\"What type of business\")]")
	private WebElement businessLabel;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"LATER\"]")
	private WebElement businessLater;

	@AndroidFindBy(accessibility = "CONFIRM")
	private WebElement businessConfirm;

	@AndroidFindBy(accessibility = "Booking.com")
	private WebElement mapLodgings;

	@AndroidFindBy(accessibility = "Dog Business")
	private WebElement mapBusinessBtn;

	@AndroidFindBy(accessibility = "Dog Park")
	private WebElement mapParkBtn;

	@AndroidFindBy(accessibility = "Dog-Friendly Area")
	private WebElement mapFriendlyAreaBtn;

	@AndroidFindBy(xpath = "//android.widget.HorizontalScrollView/android.view.ViewGroup")
	private WebElement filterBarContainer;

	@AndroidFindBy(accessibility = "Reserve")
	private WebElement lodgingReserveBtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"View Business\"]")
	private WebElement viewBusiness;

	@AndroidFindBy(accessibility = "business_messag")
	private WebElement businessMessageBtn;

	@AndroidFindBy(accessibility = "business_UserFollow")
	private WebElement followerTab;

	@AndroidFindBy(accessibility = "business_following")
	private WebElement followeringTab;

	@AndroidFindBy(accessibility = "business_badge")
	private WebElement badgeTab;

	@AndroidFindBy(xpath = "//android.widget.EditText[@text=\"Search\"]")
	private WebElement searchField;

	@AndroidFindBy(accessibility = "business_nearByBusiness")
	private WebElement businessAddress;

	@AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"Follow\"])[1]")
	private WebElement businessAddressFollowMulti;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Follow\"]")
	private WebElement businessAddressFollow;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Following\"]")
	private WebElement businessAddressFollowing;

	@AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"Following\"])[1]")
	private WebElement businessAddressFollowingMulti;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"chat-gallery\"]/android.widget.ImageView")
	private WebElement chatGallery;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_all_button")
	private WebElement allowBtn;

	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_one_time_button")
	private WebElement allowOneBtn;

	@AndroidFindBy(id = "com.dogpack:id/ps_tv_title")
	private WebElement cameraRollTitle;

	@AndroidFindBy(xpath = "(//android.widget.ImageView[@resource-id=\"com.dogpack:id/ivPicture\"])[1]")
	private WebElement selectFirstImage;

	@AndroidFindBy(id = "com.dogpack:id/ps_tv_complete")
	private WebElement cameraRollDone;

	@AndroidFindBy(accessibility = "Crop")
	private WebElement lostDogCrop;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"chat-imgComp\"]/android.widget.ImageView")
	private WebElement chatGalleryEnterBtn;

	@AndroidFindBy(accessibility = "chat-input")
	private WebElement messageTextbox;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"chat-imgComp\"]/android.widget.ImageView")
	private WebElement messageEnterbtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"You have exceeded the max amount of new chats you can start for today, please try again in 24 hours. Contact supportdog@dogpackapp.com for more information!\"]")
	private WebElement exceedMessageLimitPopup;

	@AndroidFindBy(accessibility = "hambugar-menu")
	private WebElement profileHambugarMenu;

	@AndroidFindBy(accessibility = "Block user")
	private WebElement profileBlockBtn;

	@AndroidFindBy(accessibility = "modal_block")
	private WebElement businessBlockBtn;

	@AndroidFindBy(accessibility = "onCancel")
	private WebElement profileCancelBtn;

	@AndroidFindBy(accessibility = "onConfirm")
	private WebElement profileConfirmBtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Report User as inappropriate\"]")
	private WebElement reportUserInappropriateBtn;

	private By appropriateOptionBtnBy = AppiumBy
			.androidUIAutomator("new UiSelector().text(\"The Pin is in the wrong location\")");

	private By approSubmitBtnBy = AppiumBy.androidUIAutomator("new UiSelector().text(\"SUBMIT\")");

	@AndroidFindBy(accessibility = "item1")
	private WebElement profilePost;
	
	@AndroidFindBy(accessibility = "item2")
	private WebElement profileInfo;
	
	@AndroidFindBy(accessibility = "item3")
	private WebElement profileQuestion;
	
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"feed-dot-menu-0\"]/android.widget.ImageView")
	private WebElement profileFeedThreeDot;

	@AndroidFindBy(accessibility = "review_addview")
	private WebElement business_addview;
	
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"review_Unlike\"]/android.widget.ImageView")
	private WebElement reviewLike;
	
	@AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"review_Unlike\"])[1]/android.widget.ImageView")
	private WebElement reviewLikeMulti;
	
	private By parkTabRatingLaterBtnBy = AppiumBy.androidUIAutomator("new UiSelector().text(\"LATER\")");
	
	@AndroidFindBy(accessibility = "Be there at")
	private WebElement beThereAt;
	
	@AndroidFindBy(accessibility = "Check-In")
	private WebElement checkIn;
	
	private final By businessMessageBtnBy = AppiumBy.accessibilityId("business_messag");

	
	// Map Functions

	public void ParkMap() throws InterruptedException {
		System.out.println("[FLOW] ParkMap: filtering Dog Park markers");
		dismissDogProfileSheetIfPresent();
		ensureOnMapScreen();
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

	    wait.until(ExpectedConditions.visibilityOf(mapParkBtn));
	    wait.until(ExpectedConditions.elementToBeClickable(mapParkBtn)).click();
	    System.out.println("[ACTION] Clicked mapParkBtn");

	    Thread.sleep(600); // let map settle

	    List<WebElement> parkMarkers =
	        driver.findElements(By.xpath("//android.view.View[@content-desc='Map Marker']"));

	    if (parkMarkers.isEmpty()) {
	        System.out.println("[ACTION] No park markers found. Tapping Dog Park filter again.");
	        if (!driver.findElements(AppiumBy.accessibilityId("Dog Park")).isEmpty()) {
	            mapParkBtn.click();
	        } else {
	            System.out.println("[WARN] Dog Park filter not present for re-toggle - skipping");
	        }
	        Thread.sleep(1200);
	        return;
	    }

	    for (WebElement marker : parkMarkers) {
	        try {
	            if (!marker.isDisplayed() || !marker.isEnabled()) continue;
	            if (!isAboveListView(marker, listView)) continue; // avoid overlap with lView

	            tapByCenter(marker);              // coordinate tap (more reliable than .click)
	            Thread.sleep(500);                // let sheet open

	            wait.until(ExpectedConditions.or(
	                    ExpectedConditions.visibilityOf(beThereAt),
	                    ExpectedConditions.visibilityOf(checkIn)
	            ));
	            wait.until(ExpectedConditions.elementToBeClickable(viewBusiness)).click();
	            System.out.println("[ACTION] Clicked viewBusiness");

	            performedActionAccordingToBusinessType();
	            
	            wait.until(ExpectedConditions.visibilityOf(viewBusiness));
				wait.until(ExpectedConditions.elementToBeClickable(mapParkBtn)).click();
				System.out.println("[ACTION] Clicked mapParkBtn");
	            Thread.sleep(400);
	            return;

	        } catch (Exception ignored) {
	            // try next marker
	        }
	    }

	    System.out.println("[ACTION] No safe marker clickable. Re-toggling Dog Business filter.");
	    mapParkBtn.click();
	    Thread.sleep(1200);
	}
	
	private void tapByCenter(WebElement el) {
	    Rectangle rect = el.getRect();
	    int cx = rect.getX() + rect.getWidth() / 2;
	    int cy = rect.getY() + rect.getHeight() / 2;

	    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
	    Sequence tap = new Sequence(finger, 1);
	    tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), cx, cy));
	    tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
	    tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
	    driver.perform(singletonList(tap));
	    System.out.println("[ACTION] Performed touch gesture");
	}

	private boolean isAboveListView(WebElement el, WebElement listViewToggle) {
	    Rectangle r = el.getRect();
	    int elCenterY = r.getY() + r.getHeight() / 2;

	    Rectangle lv = listViewToggle.getRect();
	    int listTopY = lv.getY();

	    return elCenterY < (listTopY - 8); // small safety margin
	}
	
	public void safeBack() {
	    try {
	        // 1) Agar keyboard open hai to close
	        try { driver.hideKeyboard(); } catch (Exception ignored) {}

	        // 2) Context check
	        if (!"NATIVE_APP".equals(driver.getContext())) {
	            // WEBVIEW back
	            try { driver.executeScript("history.back()"); } catch (Exception ignored) {}
	            try { driver.navigate().back(); } catch (Exception ignored) {}
	        } else {
	            // NATIVE back
	            try { driver.pressKey(new KeyEvent(AndroidKey.BACK)); } catch (Exception ignored) {}
	        }

	        // 3) Thoda wait (optional)
	        Thread.sleep(500);

	    } catch (Exception e) {
	        System.out.println("[WARN] ⚠️ smartBack failed: " + e.getMessage());
	        try { driver.context("NATIVE_APP"); } catch (Exception ignored) {}
	    }
	}
	
	public void performedActionAccordingToBusinessType() throws InterruptedException {

	    // 1) Is it present at all?
	    List<WebElement> els = driver.findElements(businessMessageBtnBy);

	    if (els.isEmpty()) {
	        System.out.println("[FLOW] Business Message button NOT in DOM. Doing fallback actions...");
	        safeBack();
	        return;
	    }

	    // 2) Present: check visibility safely
	    WebElement btn = els.get(0);
	    boolean visible = false;
	    try {
	        visible = btn.isDisplayed();
	    } catch (Exception ignored) { /* stale/invisible */ }

	    if (visible) {
	        System.out.println("[INFO] Business Message button is displayed. Performing first set of actions...");
	        navigatesToAllTabsInProfile();
	        BusinessAddress();
	        BusinessTabMessageUser();
	        BusinessBlockUser();
	        ReportBusiness();
	        ClickOnSubTabsInProfile();
	        safeBack();

	    } else {
	        System.out.println("[WARN] Business Message button present but NOT displayed. Doing fallback...");
	        safeBack();
	    }
	}

	public void ClickOnSubTabsInProfile() throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		
		wait.until(ExpectedConditions.visibilityOf(profilePost));
		wait.until(ExpectedConditions.elementToBeClickable(profilePost)).click();
		System.out.println("[ACTION] Clicked profilePost");
		try {
			wait.until(ExpectedConditions.visibilityOf(profileFeedThreeDot));
		} catch (Exception e) {
			Thread.sleep(3000);
		}
		
		wait.until(ExpectedConditions.visibilityOf(profileInfo));
		wait.until(ExpectedConditions.elementToBeClickable(profileInfo)).click();
		System.out.println("[ACTION] Clicked profileInfo");
		
		
		try {
			wait.until(ExpectedConditions.visibilityOf(business_addview));
			wait.until(ExpectedConditions.elementToBeClickable(business_addview)).click();
			System.out.println("[ACTION] Clicked business_addview");
			WebElement laterBtn = wait
					.until(ExpectedConditions.elementToBeClickable(parkTabRatingLaterBtnBy));
			laterBtn.click();
			System.out.println("[ACTION] ✅ 'Later' clicked.");
			
		} catch (Exception e) {
			// TODO: handle exception
			if (reviewLike.isDisplayed()) {
				wait.until(ExpectedConditions.visibilityOf(reviewLike));
				wait.until(ExpectedConditions.elementToBeClickable(reviewLike)).click();
				System.out.println("[ACTION] ✅ 'Like' clicked.");
			}else {
				wait.until(ExpectedConditions.visibilityOf(reviewLikeMulti));
				wait.until(ExpectedConditions.elementToBeClickable(reviewLikeMulti)).click();
				System.out.println("[ACTION] ✅ 'Multi-Like' clicked.");
			}
			
		}
		
		wait.until(ExpectedConditions.visibilityOf(profileQuestion));
		wait.until(ExpectedConditions.elementToBeClickable(profileQuestion)).click();
		System.out.println("[ACTION] Clicked profileQuestion");
		try {
			wait.until(ExpectedConditions.visibilityOf(profileFeedThreeDot));
		} catch (Exception e) {
			Thread.sleep(3000);
		}
	}
	
	public void ReportBusiness() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(profileHambugarMenu));
		wait.until(ExpectedConditions.elementToBeClickable(profileHambugarMenu)).click();
		System.out.println("[ACTION] Clicked profileHambugarMenu");
		wait.until(ExpectedConditions.visibilityOf(reportUserInappropriateBtn));
		wait.until(ExpectedConditions.elementToBeClickable(reportUserInappropriateBtn)).click();
		System.out.println("[ACTION] Clicked reportUserInappropriateBtn");

		WebElement inapproBtn = wait.until(ExpectedConditions.elementToBeClickable(appropriateOptionBtnBy));
		inapproBtn.click();
		System.out.println("[ACTION] ✅ 'Inappropriate option' clicked.");
		driver.hideKeyboard();
		WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(approSubmitBtnBy));
		submitBtn.click();
		System.out.println("[ACTION] ✅ 'Submit' clicked.");
	}

	public void handleProfilePopup(String action) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		if (action.equalsIgnoreCase("Confirm")) {
			wait.until(ExpectedConditions.visibilityOf(profileConfirmBtn));
			wait.until(ExpectedConditions.elementToBeClickable(profileConfirmBtn)).click();
			System.out.println("[ACTION] ✅ Confirm button clicked.");
		} else if (action.equalsIgnoreCase("Cancel")) {
			wait.until(ExpectedConditions.visibilityOf(profileCancelBtn));
			wait.until(ExpectedConditions.elementToBeClickable(profileCancelBtn)).click();
			System.out.println("[ACTION] ✅ Cancel button clicked.");
			driver.pressKey(new KeyEvent(AndroidKey.BACK));
		} else {
			System.out.println("[INFO] ❌ Invalid action. Use 'Confirm' or 'Cancel'.");
		}

	}

	public void BusinessBlockUser() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(profileHambugarMenu));
		wait.until(ExpectedConditions.elementToBeClickable(profileHambugarMenu)).click();
		System.out.println("[ACTION] Clicked profileHambugarMenu");
		wait.until(ExpectedConditions.visibilityOf(businessBlockBtn));
		wait.until(ExpectedConditions.elementToBeClickable(businessBlockBtn)).click();
		System.out.println("[ACTION] Clicked businessBlockBtn");
		handleProfilePopup("Cancel");
		wait.until(ExpectedConditions.visibilityOf(businessMessageBtn));
	}

	public void BusinessTabMessageUser() throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(businessMessageBtn));
		wait.until(ExpectedConditions.elementToBeClickable(businessMessageBtn)).click();
		System.out.println("[ACTION] Clicked businessMessageBtn");
		wait.until(ExpectedConditions.visibilityOf(messageTextbox));
		wait.until(ExpectedConditions.elementToBeClickable(messageTextbox)).sendKeys("Hey brother");
		System.out.println("[ACTION] Entered text in messageTextbox");
		driver.hideKeyboard();
		System.out.println("[ACTION] Hid keyboard");
		wait.until(ExpectedConditions.visibilityOf(messageEnterbtn));
		wait.until(ExpectedConditions.elementToBeClickable(messageEnterbtn)).click();
		System.out.println("[ACTION] Clicked messageEnterbtn");
		try {
			if (exceedMessageLimitPopup.isDisplayed()) {
				System.out.println("[INFO] ⚠️ Message limit reached.");
				wait.until(ExpectedConditions.invisibilityOf(exceedMessageLimitPopup));
				return;
			}
		} catch (Exception ignored) {
			System.out.println("[INFO] ⚠️ Message limit is not reached yet reached.");
		}
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		System.out.println("[ACTION] Pressed device Back");

		wait.until(ExpectedConditions.visibilityOf(businessMessageBtn));
		wait.until(ExpectedConditions.elementToBeClickable(businessMessageBtn)).click();
		System.out.println("[ACTION] Clicked businessMessageBtn");

		// chat gallery
		wait.until(ExpectedConditions.visibilityOf(chatGallery));
		wait.until(ExpectedConditions.elementToBeClickable(chatGallery)).click();
		System.out.println("[ACTION] Clicked chatGallery");

		try {
			if (allowBtn.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(allowBtn)).click();
				System.out.println("[ACTION] Clicked allowBtn");
			}
		} catch (Exception e) {
		}

		try {
			if (allowOneBtn.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(allowOneBtn)).click();
				System.out.println("[ACTION] Clicked allowOneBtn");
			}
		} catch (Exception e) {
		}

		wait.until(ExpectedConditions.elementToBeClickable(selectFirstImage)).click();
		System.out.println("[ACTION] Clicked selectFirstImage");
		wait.until(ExpectedConditions.elementToBeClickable(cameraRollDone)).click();
		System.out.println("[ACTION] Clicked cameraRollDone");
		wait.until(ExpectedConditions.elementToBeClickable(lostDogCrop)).click();
		System.out.println("[ACTION] Clicked lostDogCrop");
		wait.until(ExpectedConditions.elementToBeClickable(chatGalleryEnterBtn)).click();
		System.out.println("[ACTION] Clicked chatGalleryEnterBtn");
		Thread.sleep(3000);
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		System.out.println("[ACTION] Pressed device Back");

		wait.until(ExpectedConditions.visibilityOf(businessMessageBtn));
	}

	public void BusinessAddress() throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(businessAddress));
		wait.until(ExpectedConditions.elementToBeClickable(businessAddress)).click();
		System.out.println("[ACTION] Clicked businessAddress");

//		wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOf(businessAddressFollow),
//				ExpectedConditions.visibilityOf(businessAddressFollowMulti),
//				ExpectedConditions.visibilityOf(businessAddressFollowing),
//				ExpectedConditions.visibilityOf(businessAddressFollowingMulti)));
		//driver.pressKey(new KeyEvent(AndroidKey.BACK));
		Thread.sleep(5000);		
		safeBack();
	}

	public void navigatesToAllTabsInProfile() throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(followerTab));
		wait.until(ExpectedConditions.elementToBeClickable(followerTab)).click();
		System.out.println("[ACTION] Clicked followerTab");
		wait.until(ExpectedConditions.visibilityOf(searchField));
		wait.until(ExpectedConditions.elementToBeClickable(searchField)).sendKeys("xyz");
		System.out.println("[ACTION] Entered text in searchField");
		driver.hideKeyboard();
		System.out.println("[ACTION] Hid keyboard");
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		System.out.println("[ACTION] Pressed device Back");

		wait.until(ExpectedConditions.visibilityOf(followeringTab));
		wait.until(ExpectedConditions.elementToBeClickable(followeringTab)).click();
		System.out.println("[ACTION] Clicked followeringTab");
		wait.until(ExpectedConditions.visibilityOf(searchField));
		wait.until(ExpectedConditions.elementToBeClickable(searchField)).sendKeys("xyz");
		System.out.println("[ACTION] Entered text in searchField");
		driver.hideKeyboard();
		System.out.println("[ACTION] Hid keyboard");
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		System.out.println("[ACTION] Pressed device Back");
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		System.out.println("[ACTION] Pressed device Back");

		wait.until(ExpectedConditions.visibilityOf(badgeTab));
		wait.until(ExpectedConditions.elementToBeClickable(badgeTab)).click();
		System.out.println("[ACTION] Clicked badgeTab");
		Thread.sleep(3000);
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		System.out.println("[ACTION] Pressed device Back");
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		System.out.println("[ACTION] Pressed device Back");
		wait.until(ExpectedConditions.visibilityOf(businessMessageBtn));

	}

	public void DogBusiness() throws InterruptedException {
		System.out.println("[FLOW] DogBusiness: filtering Dog Business markers");
		dismissDogProfileSheetIfPresent();
		ensureOnMapScreen();
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

	    wait.until(ExpectedConditions.visibilityOf(mapBusinessBtn));
	    wait.until(ExpectedConditions.elementToBeClickable(mapBusinessBtn)).click();
	    System.out.println("[ACTION] Clicked mapBusinessBtn");

	    Thread.sleep(600); // let map settle

	    List<WebElement> businessMarkers =
	        driver.findElements(By.xpath("//android.view.View[@content-desc='Map Marker']"));

	    if (businessMarkers.isEmpty()) {
	        System.out.println("[ACTION] No business markers found. Tapping Dog Business filter again.");
	        if (!driver.findElements(AppiumBy.accessibilityId("Dog Business")).isEmpty()) {
	            mapBusinessBtn.click();
	        } else {
	            System.out.println("[WARN] Dog Business filter not present for re-toggle - skipping");
	        }
	        Thread.sleep(1200);
	        return;
	    }

	    for (WebElement marker : businessMarkers) {
	        try {
	            if (!marker.isDisplayed() || !marker.isEnabled()) continue;
	            if (!isAboveListView(marker, listView)) continue; // avoid overlap with lView

	            tapByCenter(marker);              // coordinate tap (more reliable than .click)
	            Thread.sleep(500);                // let sheet open

	            wait.until(ExpectedConditions.visibilityOf(viewBusiness));
	            wait.until(ExpectedConditions.elementToBeClickable(viewBusiness)).click();
	            System.out.println("[ACTION] Clicked viewBusiness");

	            performedActionAccordingToBusinessType();
	            
	            wait.until(ExpectedConditions.visibilityOf(viewBusiness));
				wait.until(ExpectedConditions.elementToBeClickable(mapBusinessBtn)).click();
				System.out.println("[ACTION] Clicked mapBusinessBtn");
	            Thread.sleep(400);
	            return;

	        } catch (Exception ignored) {
	            // try next marker
	        }
	    }

	    System.out.println("[ACTION] No safe marker clickable. Re-toggling Dog Business filter.");
	    mapBusinessBtn.click();
	    Thread.sleep(1200);
	}

	/**
	 * Click the Booking.com (lodgings) filter chip robustly. The marker ->
	 * Reserve -> Booking.com flow can leave the app in List View or a detail
	 * screen where the map filter chips are absent; in that case return to the
	 * Map tab first, then click the chip.
	 */
	private void clickBookingChip() {
		dismissDogProfileSheetIfPresent();
		try {
			new WebDriverWait(driver, Duration.ofSeconds(3))
					.until(ExpectedConditions.elementToBeClickable(mapLodgings)).click();
					System.out.println("[ACTION] Clicked mapLodgings");
			return;
		} catch (Exception notOnMap) {
			WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(15));
			try { driver.context("NATIVE_APP"); } catch (Exception ignored) {}
			w.until(ExpectedConditions.elementToBeClickable(mapViewTab)).click();
			System.out.println("[ACTION] Clicked mapViewTab");
			w.until(ExpectedConditions.visibilityOf(mapType));
			w.until(ExpectedConditions.elementToBeClickable(mapLodgings)).click();
			System.out.println("[ACTION] Clicked mapLodgings");
		}
	}

	/**
	 * Close the "Dog Profile (10)" selector bottom sheet if it is stuck open over
	 * the map. Primary gesture: tap the map (confirmed working on device); device
	 * Back is a verified fallback. Self-verifies after each attempt so it never
	 * over-acts. Best-effort recovery helper - never throws.
	 */
	/**
	 * Guarantees the app is back on the native map screen before map-filter
	 * actions run. After the lodging/Booking.com detour the app can be left in
	 * a WebView, list view, or detail screen where the search bar and filter
	 * chips (Dog Business / Dog Park) are absent. Switches back to native
	 * context if needed, then presses device Back (up to 4 times) until the map
	 * search bar (mapSearchLocation) is visible. Best-effort: never throws.
	 */
	public void ensureOnMapScreen() {
		try {
			if (!"NATIVE_APP".equals(driver.getContext())) {
				driver.context("NATIVE_APP");
				System.out.println("[FLOW] ensureOnMapScreen: switched back to NATIVE_APP context");
			}
		} catch (Exception ignored) { }

		By searchBar = AppiumBy.accessibilityId("mapSearchLocation");
		driver.manage().timeouts().implicitlyWait(Duration.ZERO);
		try {
			for (int attempt = 0; attempt <= 4; attempt++) {
				if (!driver.findElements(searchBar).isEmpty()) {
					if (attempt > 0) {
						System.out.println("[FLOW] ensureOnMapScreen: map visible after " + attempt + " Back press(es)");
					}
					return;
				}
				try { driver.pressKey(new KeyEvent(AndroidKey.BACK)); } catch (Exception ignored) { }
				sleepQuiet(700);
			}
			if (driver.findElements(searchBar).isEmpty()) {
				System.out.println("[WARN] ensureOnMapScreen: map search bar still not visible after 4 Back presses");
			}
		} catch (Exception e) {
			System.out.println("[WARN] ensureOnMapScreen error: " + e.getMessage());
		} finally {
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		}
	}

	public void dismissDogProfileSheetIfPresent() {
		if (!isDogProfileSheetPresent()) {
			return;
		}
		System.out.println("[FLOW] Dog Profile sheet detected over the map - dismissing (tap map)");
		for (int attempt = 1; attempt <= 2 && isDogProfileSheetPresent(); attempt++) {
			try { tapMapToDismissSheet(); } catch (Exception ignored) {}
			sleepQuiet(700);
		}
		if (isDogProfileSheetPresent()) {
			System.out.println("[FLOW] Sheet still open after tap - using device Back fallback");
			try { driver.pressKey(new KeyEvent(AndroidKey.BACK)); } catch (Exception ignored) {}
			sleepQuiet(700);
		}
		if (isDogProfileSheetPresent()) {
			System.out.println("[WARN] Dog Profile sheet still present after dismiss attempts");
		} else {
			System.out.println("[FLOW] Dog Profile sheet dismissed");
		}
	}

	/** True if the Dog Profile selector bottom sheet is currently showing. */
	private boolean isDogProfileSheetPresent() {
		try {
			for (WebElement e : driver.findElements(By.xpath(
				"//*[contains(@text,\"Add a new dog or business profile\") or contains(@text,\"Dog Profile\")]"))) {
				try {
					if (e.isDisplayed()) {
						return true;
					}
				} catch (Exception ignored) {
				}
			}
		} catch (Exception ignored) {
		}
		return false;
	}

	/** Tap the map (scrim above the sheet) to dismiss the bottom sheet. */
	private void tapMapToDismissSheet() {
		org.openqa.selenium.Dimension sz = driver.manage().window().getSize();
		int x = sz.getWidth() / 2;
		int y = (int) (sz.getHeight() * 0.40);   // map area: below the filter chips, above the sheet
		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence tap = new Sequence(finger, 1);
		tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
		tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		driver.perform(singletonList(tap));
		System.out.println("[ACTION] Performed touch gesture");
	}

	private void sleepQuiet(long ms) {
		try { Thread.sleep(ms); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
	}

	public void clickFirstMarkerOrFallbackToLodgings() throws InterruptedException {
		System.out.println("[FLOW] clickFirstMarkerOrFallbackToLodgings: tapping a lodging marker / Booking chip");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		List<WebElement> lodgingMarkers = driver
				.findElements(By.xpath("//android.view.View[@content-desc='Map Marker']"));

		if (lodgingMarkers.isEmpty()) {
			System.out.println("[ACTION] No lodging markers found. Returning to map and clicking Booking.com chip.");
			clickBookingChip();
			Thread.sleep(2000); // optional wait
			return;
		}

		for (WebElement marker : lodgingMarkers) {
			try {
				if (marker.isDisplayed() && marker.isEnabled()) {
					marker.click();
					System.out.println("[ACTION] Clicked on first visible lodging marker.");
					wait.until(ExpectedConditions.visibilityOf(lodgingReserveBtn));
					wait.until(ExpectedConditions.elementToBeClickable(lodgingReserveBtn)).click();
					System.out.println("[ACTION] Clicked lodgingReserveBtn");
					Set<String> contextNames = driver.getContextHandles();
					int attempts = 0;
					while (contextNames.size() < 2 && attempts < 10) {
						Thread.sleep(1000);
						contextNames = driver.getContextHandles();
						attempts++;
					}
					for (String contextName : contextNames) {
						System.out.println("[INFO] Available Context: " + contextName);
					}
					
					try {
						driver.context("WEBVIEW_com.dogpack");
						scrollDownTwice();
						driver.pressKey(new KeyEvent(AndroidKey.BACK));
						System.out.println("[ACTION] Pressed device Back");
						//safeBack();
						driver.context("NATIVE_APP");

						wait.until(ExpectedConditions.visibilityOf(lodgingReserveBtn));
						clickBookingChip();
					} catch (Exception e) {
						driver.pressKey(new KeyEvent(AndroidKey.BACK));
						System.out.println("[ACTION] Pressed device Back");
						wait.until(ExpectedConditions.visibilityOf(lodgingReserveBtn));
						clickBookingChip();
					}
					
					
					return;
				}
			} catch (Exception ignored) {
				// Continue to next marker if current is stale or not clickable
			}
		}

		// Fallback if none of the markers were clickable
		System.out.println("[ACTION] No visible lodging marker clickable. Returning to map and clicking Booking.com chip.");
		clickBookingChip();
		Thread.sleep(2000); // optional wait
	}

	public void swipeRightInsideFilterBarContainer() {
		int startX = filterBarContainer.getLocation().getX() + (int) (filterBarContainer.getSize().width * 0.2);
		int endX = filterBarContainer.getLocation().getX() + (int) (filterBarContainer.getSize().width * 0.8);
		int y = filterBarContainer.getLocation().getY() + (filterBarContainer.getSize().height / 2);

		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence swipe = new Sequence(finger, 1);

		swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, y));
		swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		swipe.addAction(finger.createPointerMove(Duration.ofMillis(400), PointerInput.Origin.viewport(), endX, y));
		swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

		driver.perform(singletonList(swipe));
		System.out.println("[ACTION] Performed touch gesture");
	}

	public void clickOnLodgingsAfterSwipeRight() throws InterruptedException {
		System.out.println("[FLOW] clickOnLodgingsAfterSwipeRight");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		int attempts = 0;
		boolean found = false;

		// Swipe right until Lodgings button is found
		while (attempts < 4) {
			try {
				if (mapLodgings.isDisplayed()) {
					found = true;
					break;
				}
			} catch (Exception ignored) {
			}

			swipeRightInsideFilterBarContainer();
			Thread.sleep(500);
			attempts++;
		}

		if (!found) {
			throw new RuntimeException("Lodgings button not found after swiping right.");
		}

		wait.until(ExpectedConditions.elementToBeClickable(mapLodgings)).click();
		System.out.println("[ACTION] Clicked mapLodgings");
		Thread.sleep(2000);
	}

	public void swipeLeftInsideFilterBarContainer() {
		int startX = filterBarContainer.getLocation().getX() + (int) (filterBarContainer.getSize().width * 0.8);
		int endX = filterBarContainer.getLocation().getX() + (int) (filterBarContainer.getSize().width * 0.2);
		int y = filterBarContainer.getLocation().getY() + (filterBarContainer.getSize().height / 2);

		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence swipe = new Sequence(finger, 1);

		swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, y));
		swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		swipe.addAction(finger.createPointerMove(Duration.ofMillis(400), PointerInput.Origin.viewport(), endX, y));
		swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

		driver.perform(singletonList(swipe));
		System.out.println("[ACTION] Performed touch gesture");
	}

	public void UnSelectDogFriendlyArea() throws InterruptedException {
		System.out.println("[FLOW] UnSelectDogFriendlyArea");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		int attempts = 0;
		boolean found = false;

		// Try swiping until button is visible
		while (attempts < 4) {
			try {
				if (mapFriendlyAreaBtn.isDisplayed()) {
					found = true;
					break;
				}
			} catch (Exception ignored) {
			}

			swipeLeftInsideFilterBarContainer();
			Thread.sleep(500);
			attempts++;
		}

		if (!found) {
			throw new RuntimeException("Dog-Friendly Area button not found after swiping.");
		}

		wait.until(ExpectedConditions.elementToBeClickable(mapFriendlyAreaBtn)).click();
		System.out.println("[ACTION] Clicked mapFriendlyAreaBtn");
		Thread.sleep(2000);
	}

	public void UnSelectLodgings() throws InterruptedException {
		System.out.println("[FLOW] UnSelectLodgings");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(mapLodgings));
		wait.until(ExpectedConditions.elementToBeClickable(mapLodgings)).click();
		System.out.println("[ACTION] Clicked mapLodgings");
		Thread.sleep(2000);
	}

	public void UnSelectBusiness() throws InterruptedException {
		System.out.println("[FLOW] UnSelectBusiness");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(mapBusinessBtn));
		wait.until(ExpectedConditions.elementToBeClickable(mapBusinessBtn)).click();
		System.out.println("[ACTION] Clicked mapBusinessBtn");
		Thread.sleep(2000);
	}

	public void UnSelectPark() throws InterruptedException {
		System.out.println("[FLOW] UnSelectPark");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(mapParkBtn));
		wait.until(ExpectedConditions.elementToBeClickable(mapParkBtn)).click();
		System.out.println("[ACTION] Clicked mapParkBtn");
		Thread.sleep(2000);
	}

	public void SuggestBusiness() {
		System.out.println("[FLOW] SuggestBusiness: suggesting a business pin");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(SuggestBtn));
		wait.until(ExpectedConditions.elementToBeClickable(SuggestBtn)).click();
		System.out.println("[ACTION] Clicked SuggestBtn");

		// Handle location permissions if prompted
		try {
			if (locationPermissionMsg.isDisplayed()) {
				Assert.assertEquals(locationPermissionMsg.getText(), "Allow DogPack to access this device’s location?");
			}
		} catch (Exception ignored) {
			System.out.println("[WARN] permission message not displayed");
		}

		try {
			if (whileUsingAppPermission.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(whileUsingAppPermission)).click();
				System.out.println("[ACTION] Clicked whileUsingAppPermission");
			}
		} catch (Exception ignored) {
			System.out.println("[WARN] while using app button not displayed");
		}

		wait.until(ExpectedConditions.visibilityOf(suggestPinMarker));
		wait.until(ExpectedConditions.elementToBeClickable(suggestPinMarker)).click();
		System.out.println("[ACTION] Clicked suggestPinMarker");

		wait.until(ExpectedConditions.visibilityOf(enterSuggestedName));
		wait.until(ExpectedConditions.elementToBeClickable(enterSuggestedName)).sendKeys("AutomationTestingBusiness01");
		System.out.println("[ACTION] Entered text in enterSuggestedName");
		driver.hideKeyboard();
		System.out.println("[ACTION] Hid keyboard");

		wait.until(ExpectedConditions.elementToBeClickable(dogBusinessOption)).click();
		System.out.println("[ACTION] Clicked dogBusinessOption");
		wait.until(ExpectedConditions.elementToBeClickable(suggestSave)).click();
		System.out.println("[ACTION] Clicked suggestSave");

		try {
			wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOf(businessLabel),
					ExpectedConditions.visibilityOf(businessLater), ExpectedConditions.visibilityOf(businessConfirm)));

			wait.until(ExpectedConditions.elementToBeClickable(businessLater)).click();
			System.out.println("[ACTION] Clicked businessLater");

			String expected = "Thank you we are reviewing your suggestion.";
			String actual = parkSuggestSuccessMessage.getText();
			Assert.assertEquals(actual, expected, "Success message text doesn't match!");

			wait.until(ExpectedConditions.visibilityOf(listView));
		} finally {
			// Never leave the "What type of business" modal open - a stray modal
			// would cover the map and break every subsequent Map test.
			dismissSuggestBusinessModalIfPresent();
		}
	}

	/**
	 * Best-effort: close the suggest-business "What type of business" modal if
	 * it is still showing (tap its LATER button), so it cannot block later tests.
	 */
	public void dismissSuggestBusinessModalIfPresent() {
		try {
			java.util.List<WebElement> later = driver.findElements(
					org.openqa.selenium.By.xpath("//android.widget.TextView[@text=\"LATER\"]"));
			if (!later.isEmpty() && later.get(0).isDisplayed()) {
				later.get(0).click();
				System.out.println("[FLOW] Closed stray suggest-business modal (LATER)");
			}
		} catch (Exception ignored) {
		}
	}

	public void SuggestPark() {
		System.out.println("[FLOW] SuggestPark: suggesting a park pin");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(SuggestBtn));
		wait.until(ExpectedConditions.elementToBeClickable(SuggestBtn)).click();
		System.out.println("[ACTION] Clicked SuggestBtn");

		// Handle location permissions if prompted
		try {
			if (locationPermissionMsg.isDisplayed()) {
				Assert.assertEquals(locationPermissionMsg.getText(), "Allow DogPack to access this device’s location?");
			}
		} catch (Exception ignored) {
			System.out.println("[WARN] permission message not displayed");
		}

		try {
			if (whileUsingAppPermission.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(whileUsingAppPermission)).click();
				System.out.println("[ACTION] Clicked whileUsingAppPermission");
			}
		} catch (Exception ignored) {
			System.out.println("[WARN] while using app button not displayed");
		}

		wait.until(ExpectedConditions.visibilityOf(suggestPinMarker));
		wait.until(ExpectedConditions.elementToBeClickable(suggestPinMarker)).click();
		System.out.println("[ACTION] Clicked suggestPinMarker");

		wait.until(ExpectedConditions.visibilityOf(enterSuggestedName));
		wait.until(ExpectedConditions.elementToBeClickable(enterSuggestedName)).sendKeys("AutomationTestingPark01");
		System.out.println("[ACTION] Entered text in enterSuggestedName");
		driver.hideKeyboard();
		System.out.println("[ACTION] Hid keyboard");

		wait.until(ExpectedConditions.elementToBeClickable(dogParkOption)).click();
		System.out.println("[ACTION] Clicked dogParkOption");
		wait.until(ExpectedConditions.elementToBeClickable(suggestSave)).click();
		System.out.println("[ACTION] Clicked suggestSave");

		wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOf(parkLabel),
				ExpectedConditions.visibilityOf(parkConfirm), ExpectedConditions.visibilityOf(parkLater)));

		wait.until(ExpectedConditions.elementToBeClickable(parkChecked)).click();
		System.out.println("[ACTION] Clicked parkChecked");
		wait.until(ExpectedConditions.elementToBeClickable(parkCross)).click();
		System.out.println("[ACTION] Clicked parkCross");
		wait.until(ExpectedConditions.elementToBeClickable(parkConfirm)).click();
		System.out.println("[ACTION] Clicked parkConfirm");

		String expected = "Thank you we are reviewing your suggestion.";
		String actual = parkSuggestSuccessMessage.getText();
		Assert.assertEquals(actual, expected, "Success message text doesn't match!");

		wait.until(ExpectedConditions.visibilityOf(listView));

	}

	public void ListYourBusiness() {
		System.out.println("[FLOW] ListYourBusiness: opening the List Your Business flow");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(listBusinessBtn));
		wait.until(ExpectedConditions.elementToBeClickable(listBusinessBtn)).click();
		System.out.println("[ACTION] Clicked listBusinessBtn");

		wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOf(labelDogProfile),
				ExpectedConditions.visibilityOf(labelBusinessProfile)));

		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		System.out.println("[ACTION] Pressed device Back");
		wait.until(ExpectedConditions.visibilityOf(listView));

	}

	public void NavigtesToMap() {
		System.out.println("[FLOW] NavigtesToMap: opening the Map screen");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(mapViewTab));
		wait.until(ExpectedConditions.elementToBeClickable(mapViewTab)).click();
		System.out.println("[ACTION] Clicked mapViewTab");

		// Handle location permissions if prompted
		try {
			if (locationPermissionMsg.isDisplayed()) {
				Assert.assertEquals(locationPermissionMsg.getText(), "Allow DogPack to access this device’s location?");
			}
		} catch (Exception ignored) {
			System.out.println("[WARN] permission message not displayed");
		}

		try {
			if (whileUsingAppPermission.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(whileUsingAppPermission)).click();
				System.out.println("[ACTION] Clicked whileUsingAppPermission");
			}
		} catch (Exception ignored) {
			System.out.println("[WARN] while using app button not displayed");
		}

	//	driver.pressKey(new KeyEvent(AndroidKey.BACK));
		safeBack();
		wait.until(ExpectedConditions.visibilityOf(mapType));
	}

	public void searchByCurrentLocation() {
		System.out.println("[FLOW] searchByCurrentLocation: searching by current location");
		dismissDogProfileSheetIfPresent();
		ensureOnMapScreen();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(mapSearchBar));
		wait.until(ExpectedConditions.elementToBeClickable(mapSearchBar)).click();
		System.out.println("[ACTION] Clicked mapSearchBar");

		wait.until(ExpectedConditions.visibilityOf(getCurrentLocation));
		wait.until(ExpectedConditions.elementToBeClickable(getCurrentLocation)).click();
		System.out.println("[ACTION] Clicked getCurrentLocation");
		wait.until(ExpectedConditions.visibilityOf(mapType));

	}

	public void searchByDynamicLocation() {
		System.out.println("[FLOW] searchByDynamicLocation: searching by a typed location");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(mapSearchBar));
		wait.until(ExpectedConditions.elementToBeClickable(mapSearchBar)).click();
		System.out.println("[ACTION] Clicked mapSearchBar");

		wait.until(ExpectedConditions.elementToBeClickable(enterLocationHere)).sendKeys("montreal");
		System.out.println("[ACTION] Entered text in enterLocationHere");
		wait.until(ExpectedConditions.visibilityOf(MontrealLocation));
		wait.until(ExpectedConditions.elementToBeClickable(MontrealLocation)).click();
		System.out.println("[ACTION] Clicked MontrealLocation");

		wait.until(ExpectedConditions.visibilityOf(mapType));

	}

	public void changeMapTypeToTraffic() {
		System.out.println("[FLOW] changeMapTypeToTraffic");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(mapType));
		wait.until(ExpectedConditions.elementToBeClickable(mapType)).click();
		System.out.println("[ACTION] Clicked mapType");
		wait.until(ExpectedConditions.visibilityOf(mapTypeDefault));

		wait.until(ExpectedConditions.elementToBeClickable(mapTypeTraffic)).click();
		System.out.println("[ACTION] Clicked mapTypeTraffic");
		wait.until(ExpectedConditions.visibilityOf(listView));

	}

	public void changeMapTypeToTerrain() {
		System.out.println("[FLOW] changeMapTypeToTerrain");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(mapType));
		wait.until(ExpectedConditions.elementToBeClickable(mapType)).click();
		System.out.println("[ACTION] Clicked mapType");
		wait.until(ExpectedConditions.visibilityOf(mapTypeDefault));

		wait.until(ExpectedConditions.elementToBeClickable(mapTypeTerrain)).click();
		System.out.println("[ACTION] Clicked mapTypeTerrain");
		wait.until(ExpectedConditions.visibilityOf(listView));

	}

	public void changeMapTypeToSatellite() {
		System.out.println("[FLOW] changeMapTypeToSatellite");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(mapType));
		wait.until(ExpectedConditions.elementToBeClickable(mapType)).click();
		System.out.println("[ACTION] Clicked mapType");
		wait.until(ExpectedConditions.visibilityOf(mapTypeDefault));

		wait.until(ExpectedConditions.elementToBeClickable(mapTypeSatellite)).click();
		System.out.println("[ACTION] Clicked mapTypeSatellite");
		wait.until(ExpectedConditions.visibilityOf(listView));

	}

	public void changeMapTypeToDefault() {
		System.out.println("[FLOW] changeMapTypeToDefault");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(mapType));
		wait.until(ExpectedConditions.elementToBeClickable(mapType)).click();
		System.out.println("[ACTION] Clicked mapType");
		wait.until(ExpectedConditions.visibilityOf(mapTypeDefault));

		wait.until(ExpectedConditions.elementToBeClickable(mapTypeDefault)).click();
		System.out.println("[ACTION] Clicked mapTypeDefault");
		wait.until(ExpectedConditions.visibilityOf(listView));

	}

}