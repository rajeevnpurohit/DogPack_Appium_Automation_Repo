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

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"What type of business is this? Please check all that apply.\"]")
	private WebElement businessLabel;

	@AndroidFindBy(accessibility = "LATER")
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
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

	    wait.until(ExpectedConditions.visibilityOf(mapParkBtn));
	    wait.until(ExpectedConditions.elementToBeClickable(mapParkBtn)).click();

	    Thread.sleep(600); // let map settle

	    List<WebElement> parkMarkers =
	        driver.findElements(By.xpath("//android.view.View[@content-desc='Map Marker']"));

	    if (parkMarkers.isEmpty()) {
	        System.out.println("No business markers found. Tapping Dog Business filter again.");
	        mapParkBtn.click();
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

	            performedActionAccordingToBusinessType();
	            
	            wait.until(ExpectedConditions.visibilityOf(viewBusiness));
				wait.until(ExpectedConditions.elementToBeClickable(mapParkBtn)).click();
	            Thread.sleep(400);
	            return;

	        } catch (Exception ignored) {
	            // try next marker
	        }
	    }

	    System.out.println("No safe marker clickable. Re-toggling Dog Business filter.");
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
	        System.out.println("⚠️ smartBack failed: " + e.getMessage());
	        try { driver.context("NATIVE_APP"); } catch (Exception ignored) {}
	    }
	}
	
	public void performedActionAccordingToBusinessType() throws InterruptedException {

	    // 1) Is it present at all?
	    List<WebElement> els = driver.findElements(businessMessageBtnBy);

	    if (els.isEmpty()) {
	        System.out.println("Business Message button NOT in DOM. Doing fallback actions...");
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
	        System.out.println("Business Message button is displayed. Performing first set of actions...");
	        navigatesToAllTabsInProfile();
	        BusinessAddress();
	        BusinessTabMessageUser();
	        BusinessBlockUser();
	        ReportBusiness();
	        ClickOnSubTabsInProfile();
	        safeBack();

	    } else {
	        System.out.println("Business Message button present but NOT displayed. Doing fallback...");
	        safeBack();
	    }
	}

	public void ClickOnSubTabsInProfile() throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		
		wait.until(ExpectedConditions.visibilityOf(profilePost));
		wait.until(ExpectedConditions.elementToBeClickable(profilePost)).click();
		try {
			wait.until(ExpectedConditions.visibilityOf(profileFeedThreeDot));
		} catch (Exception e) {
			Thread.sleep(3000);
		}
		
		wait.until(ExpectedConditions.visibilityOf(profileInfo));
		wait.until(ExpectedConditions.elementToBeClickable(profileInfo)).click();
		
		
		try {
			wait.until(ExpectedConditions.visibilityOf(business_addview));
			wait.until(ExpectedConditions.elementToBeClickable(business_addview)).click();
			WebElement laterBtn = wait
					.until(ExpectedConditions.elementToBeClickable(parkTabRatingLaterBtnBy));
			laterBtn.click();
			System.out.println("✅ 'Later' clicked.");
			
		} catch (Exception e) {
			// TODO: handle exception
			if (reviewLike.isDisplayed()) {
				wait.until(ExpectedConditions.visibilityOf(reviewLike));
				wait.until(ExpectedConditions.elementToBeClickable(reviewLike)).click();
				System.out.println("✅ 'Like' clicked.");
			}else {
				wait.until(ExpectedConditions.visibilityOf(reviewLikeMulti));
				wait.until(ExpectedConditions.elementToBeClickable(reviewLikeMulti)).click();
				System.out.println("✅ 'Multi-Like' clicked.");
			}
			
		}
		
		wait.until(ExpectedConditions.visibilityOf(profileQuestion));
		wait.until(ExpectedConditions.elementToBeClickable(profileQuestion)).click();
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
		wait.until(ExpectedConditions.visibilityOf(reportUserInappropriateBtn));
		wait.until(ExpectedConditions.elementToBeClickable(reportUserInappropriateBtn)).click();

		WebElement inapproBtn = wait.until(ExpectedConditions.elementToBeClickable(appropriateOptionBtnBy));
		inapproBtn.click();
		System.out.println("✅ 'Inappropriate option' clicked.");
		driver.hideKeyboard();
		WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(approSubmitBtnBy));
		submitBtn.click();
		System.out.println("✅ 'Submit' clicked.");
	}

	public void handleProfilePopup(String action) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		if (action.equalsIgnoreCase("Confirm")) {
			wait.until(ExpectedConditions.visibilityOf(profileConfirmBtn));
			wait.until(ExpectedConditions.elementToBeClickable(profileConfirmBtn)).click();
			System.out.println("✅ Confirm button clicked.");
		} else if (action.equalsIgnoreCase("Cancel")) {
			wait.until(ExpectedConditions.visibilityOf(profileCancelBtn));
			wait.until(ExpectedConditions.elementToBeClickable(profileCancelBtn)).click();
			System.out.println("✅ Cancel button clicked.");
			driver.pressKey(new KeyEvent(AndroidKey.BACK));
		} else {
			System.out.println("❌ Invalid action. Use 'Confirm' or 'Cancel'.");
		}

	}

	public void BusinessBlockUser() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(profileHambugarMenu));
		wait.until(ExpectedConditions.elementToBeClickable(profileHambugarMenu)).click();
		wait.until(ExpectedConditions.visibilityOf(businessBlockBtn));
		wait.until(ExpectedConditions.elementToBeClickable(businessBlockBtn)).click();
		handleProfilePopup("Cancel");
		wait.until(ExpectedConditions.visibilityOf(businessMessageBtn));
	}

	public void BusinessTabMessageUser() throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(businessMessageBtn));
		wait.until(ExpectedConditions.elementToBeClickable(businessMessageBtn)).click();
		wait.until(ExpectedConditions.visibilityOf(messageTextbox));
		wait.until(ExpectedConditions.elementToBeClickable(messageTextbox)).sendKeys("Hey brother");
		driver.hideKeyboard();
		wait.until(ExpectedConditions.visibilityOf(messageEnterbtn));
		wait.until(ExpectedConditions.elementToBeClickable(messageEnterbtn)).click();
		try {
			if (exceedMessageLimitPopup.isDisplayed()) {
				System.out.println("⚠️ Message limit reached.");
				wait.until(ExpectedConditions.invisibilityOf(exceedMessageLimitPopup));
				return;
			}
		} catch (Exception ignored) {
			System.out.println("⚠️ Message limit is not reached yet reached.");
		}
		driver.pressKey(new KeyEvent(AndroidKey.BACK));

		wait.until(ExpectedConditions.visibilityOf(businessMessageBtn));
		wait.until(ExpectedConditions.elementToBeClickable(businessMessageBtn)).click();

		// chat gallery
		wait.until(ExpectedConditions.visibilityOf(chatGallery));
		wait.until(ExpectedConditions.elementToBeClickable(chatGallery)).click();

		try {
			if (allowBtn.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(allowBtn)).click();
			}
		} catch (Exception e) {
		}

		try {
			if (allowOneBtn.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(allowOneBtn)).click();
			}
		} catch (Exception e) {
		}

		wait.until(ExpectedConditions.elementToBeClickable(selectFirstImage)).click();
		wait.until(ExpectedConditions.elementToBeClickable(cameraRollDone)).click();
		wait.until(ExpectedConditions.elementToBeClickable(lostDogCrop)).click();
		wait.until(ExpectedConditions.elementToBeClickable(chatGalleryEnterBtn)).click();
		Thread.sleep(3000);
		driver.pressKey(new KeyEvent(AndroidKey.BACK));

		wait.until(ExpectedConditions.visibilityOf(businessMessageBtn));
	}

	public void BusinessAddress() throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(businessAddress));
		wait.until(ExpectedConditions.elementToBeClickable(businessAddress)).click();

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
		wait.until(ExpectedConditions.visibilityOf(searchField));
		wait.until(ExpectedConditions.elementToBeClickable(searchField)).sendKeys("xyz");
		driver.hideKeyboard();
		driver.pressKey(new KeyEvent(AndroidKey.BACK));

		wait.until(ExpectedConditions.visibilityOf(followeringTab));
		wait.until(ExpectedConditions.elementToBeClickable(followeringTab)).click();
		wait.until(ExpectedConditions.visibilityOf(searchField));
		wait.until(ExpectedConditions.elementToBeClickable(searchField)).sendKeys("xyz");
		driver.hideKeyboard();
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		driver.pressKey(new KeyEvent(AndroidKey.BACK));

		wait.until(ExpectedConditions.visibilityOf(badgeTab));
		wait.until(ExpectedConditions.elementToBeClickable(badgeTab)).click();
		Thread.sleep(3000);
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		wait.until(ExpectedConditions.visibilityOf(businessMessageBtn));

	}

	public void DogBusiness() throws InterruptedException {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

	    wait.until(ExpectedConditions.visibilityOf(mapBusinessBtn));
	    wait.until(ExpectedConditions.elementToBeClickable(mapBusinessBtn)).click();

	    Thread.sleep(600); // let map settle

	    List<WebElement> businessMarkers =
	        driver.findElements(By.xpath("//android.view.View[@content-desc='Map Marker']"));

	    if (businessMarkers.isEmpty()) {
	        System.out.println("No business markers found. Tapping Dog Business filter again.");
	        mapBusinessBtn.click();
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

	            performedActionAccordingToBusinessType();
	            
	            wait.until(ExpectedConditions.visibilityOf(viewBusiness));
				wait.until(ExpectedConditions.elementToBeClickable(mapBusinessBtn)).click();
	            Thread.sleep(400);
	            return;

	        } catch (Exception ignored) {
	            // try next marker
	        }
	    }

	    System.out.println("No safe marker clickable. Re-toggling Dog Business filter.");
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
		try {
			new WebDriverWait(driver, Duration.ofSeconds(3))
					.until(ExpectedConditions.elementToBeClickable(mapLodgings)).click();
			return;
		} catch (Exception notOnMap) {
			WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(15));
			try { driver.context("NATIVE_APP"); } catch (Exception ignored) {}
			w.until(ExpectedConditions.elementToBeClickable(mapViewTab)).click();
			w.until(ExpectedConditions.visibilityOf(mapType));
			w.until(ExpectedConditions.elementToBeClickable(mapLodgings)).click();
		}
	}

	public void clickFirstMarkerOrFallbackToLodgings() throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		List<WebElement> lodgingMarkers = driver
				.findElements(By.xpath("//android.view.View[@content-desc='Map Marker']"));

		if (lodgingMarkers.isEmpty()) {
			System.out.println("No lodging markers found. Returning to map and clicking Booking.com chip.");
			clickBookingChip();
			Thread.sleep(2000); // optional wait
			return;
		}

		for (WebElement marker : lodgingMarkers) {
			try {
				if (marker.isDisplayed() && marker.isEnabled()) {
					marker.click();
					System.out.println("Clicked on first visible lodging marker.");
					wait.until(ExpectedConditions.visibilityOf(lodgingReserveBtn));
					wait.until(ExpectedConditions.elementToBeClickable(lodgingReserveBtn)).click();
					Set<String> contextNames = driver.getContextHandles();
					int attempts = 0;
					while (contextNames.size() < 2 && attempts < 10) {
						Thread.sleep(1000);
						contextNames = driver.getContextHandles();
						attempts++;
					}
					for (String contextName : contextNames) {
						System.out.println("Available Context: " + contextName);
					}
					
					try {
						driver.context("WEBVIEW_com.dogpack");
						scrollDownTwice();
						driver.pressKey(new KeyEvent(AndroidKey.BACK));
						//safeBack();
						driver.context("NATIVE_APP");

						wait.until(ExpectedConditions.visibilityOf(lodgingReserveBtn));
						clickBookingChip();
					} catch (Exception e) {
						driver.pressKey(new KeyEvent(AndroidKey.BACK));
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
		System.out.println("No visible lodging marker clickable. Returning to map and clicking Booking.com chip.");
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
	}

	public void clickOnLodgingsAfterSwipeRight() throws InterruptedException {
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
	}

	public void UnSelectDogFriendlyArea() throws InterruptedException {
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
		Thread.sleep(2000);
	}

	public void UnSelectLodgings() throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(mapLodgings));
		wait.until(ExpectedConditions.elementToBeClickable(mapLodgings)).click();
		Thread.sleep(2000);
	}

	public void UnSelectBusiness() throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(mapBusinessBtn));
		wait.until(ExpectedConditions.elementToBeClickable(mapBusinessBtn)).click();
		Thread.sleep(2000);
	}

	public void UnSelectPark() throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(mapParkBtn));
		wait.until(ExpectedConditions.elementToBeClickable(mapParkBtn)).click();
		Thread.sleep(2000);
	}

	public void SuggestBusiness() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(SuggestBtn));
		wait.until(ExpectedConditions.elementToBeClickable(SuggestBtn)).click();

		// Handle location permissions if prompted
		try {
			if (locationPermissionMsg.isDisplayed()) {
				Assert.assertEquals(locationPermissionMsg.getText(), "Allow DogPack to access this device’s location?");
			}
		} catch (Exception ignored) {
			System.out.println("permission message not displayed");
		}

		try {
			if (whileUsingAppPermission.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(whileUsingAppPermission)).click();
			}
		} catch (Exception ignored) {
			System.out.println("while using app button not displayed");
		}

		wait.until(ExpectedConditions.visibilityOf(suggestPinMarker));
		wait.until(ExpectedConditions.elementToBeClickable(suggestPinMarker)).click();

		wait.until(ExpectedConditions.visibilityOf(enterSuggestedName));
		wait.until(ExpectedConditions.elementToBeClickable(enterSuggestedName)).sendKeys("AutomationTestingBusiness01");
		driver.hideKeyboard();

		wait.until(ExpectedConditions.elementToBeClickable(dogBusinessOption)).click();
		wait.until(ExpectedConditions.elementToBeClickable(suggestSave)).click();

		wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOf(businessLabel),
				ExpectedConditions.visibilityOf(businessLater), ExpectedConditions.visibilityOf(businessConfirm)));

		wait.until(ExpectedConditions.elementToBeClickable(businessLater)).click();

		String expected = "Thank you we are reviewing your suggestion.";
		String actual = parkSuggestSuccessMessage.getText();
		Assert.assertEquals(actual, expected, "Success message text doesn't match!");

		wait.until(ExpectedConditions.visibilityOf(listView));
	}

	public void SuggestPark() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(SuggestBtn));
		wait.until(ExpectedConditions.elementToBeClickable(SuggestBtn)).click();

		// Handle location permissions if prompted
		try {
			if (locationPermissionMsg.isDisplayed()) {
				Assert.assertEquals(locationPermissionMsg.getText(), "Allow DogPack to access this device’s location?");
			}
		} catch (Exception ignored) {
			System.out.println("permission message not displayed");
		}

		try {
			if (whileUsingAppPermission.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(whileUsingAppPermission)).click();
			}
		} catch (Exception ignored) {
			System.out.println("while using app button not displayed");
		}

		wait.until(ExpectedConditions.visibilityOf(suggestPinMarker));
		wait.until(ExpectedConditions.elementToBeClickable(suggestPinMarker)).click();

		wait.until(ExpectedConditions.visibilityOf(enterSuggestedName));
		wait.until(ExpectedConditions.elementToBeClickable(enterSuggestedName)).sendKeys("AutomationTestingPark01");
		driver.hideKeyboard();

		wait.until(ExpectedConditions.elementToBeClickable(dogParkOption)).click();
		wait.until(ExpectedConditions.elementToBeClickable(suggestSave)).click();

		wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOf(parkLabel),
				ExpectedConditions.visibilityOf(parkConfirm), ExpectedConditions.visibilityOf(parkLater)));

		wait.until(ExpectedConditions.elementToBeClickable(parkChecked)).click();
		wait.until(ExpectedConditions.elementToBeClickable(parkCross)).click();
		wait.until(ExpectedConditions.elementToBeClickable(parkConfirm)).click();

		String expected = "Thank you we are reviewing your suggestion.";
		String actual = parkSuggestSuccessMessage.getText();
		Assert.assertEquals(actual, expected, "Success message text doesn't match!");

		wait.until(ExpectedConditions.visibilityOf(listView));

	}

	public void ListYourBusiness() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(listBusinessBtn));
		wait.until(ExpectedConditions.elementToBeClickable(listBusinessBtn)).click();

		wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOf(labelDogProfile),
				ExpectedConditions.visibilityOf(labelBusinessProfile)));

		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		wait.until(ExpectedConditions.visibilityOf(listView));

	}

	public void NavigtesToMap() {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(mapViewTab));
		wait.until(ExpectedConditions.elementToBeClickable(mapViewTab)).click();

		// Handle location permissions if prompted
		try {
			if (locationPermissionMsg.isDisplayed()) {
				Assert.assertEquals(locationPermissionMsg.getText(), "Allow DogPack to access this device’s location?");
			}
		} catch (Exception ignored) {
			System.out.println("permission message not displayed");
		}

		try {
			if (whileUsingAppPermission.isDisplayed()) {
				wait.until(ExpectedConditions.elementToBeClickable(whileUsingAppPermission)).click();
			}
		} catch (Exception ignored) {
			System.out.println("while using app button not displayed");
		}

	//	driver.pressKey(new KeyEvent(AndroidKey.BACK));
		safeBack();
		wait.until(ExpectedConditions.visibilityOf(mapType));
	}

	public void searchByCurrentLocation() {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(mapSearchBar));
		wait.until(ExpectedConditions.elementToBeClickable(mapSearchBar)).click();

		wait.until(ExpectedConditions.visibilityOf(getCurrentLocation));
		wait.until(ExpectedConditions.elementToBeClickable(getCurrentLocation)).click();
		wait.until(ExpectedConditions.visibilityOf(mapType));

	}

	public void searchByDynamicLocation() {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(mapSearchBar));
		wait.until(ExpectedConditions.elementToBeClickable(mapSearchBar)).click();

		wait.until(ExpectedConditions.elementToBeClickable(enterLocationHere)).sendKeys("montreal");
		wait.until(ExpectedConditions.visibilityOf(MontrealLocation));
		wait.until(ExpectedConditions.elementToBeClickable(MontrealLocation)).click();

		wait.until(ExpectedConditions.visibilityOf(mapType));

	}

	public void changeMapTypeToTraffic() {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(mapType));
		wait.until(ExpectedConditions.elementToBeClickable(mapType)).click();
		wait.until(ExpectedConditions.visibilityOf(mapTypeDefault));

		wait.until(ExpectedConditions.elementToBeClickable(mapTypeTraffic)).click();
		wait.until(ExpectedConditions.visibilityOf(listView));

	}

	public void changeMapTypeToTerrain() {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(mapType));
		wait.until(ExpectedConditions.elementToBeClickable(mapType)).click();
		wait.until(ExpectedConditions.visibilityOf(mapTypeDefault));

		wait.until(ExpectedConditions.elementToBeClickable(mapTypeTerrain)).click();
		wait.until(ExpectedConditions.visibilityOf(listView));

	}

	public void changeMapTypeToSatellite() {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(mapType));
		wait.until(ExpectedConditions.elementToBeClickable(mapType)).click();
		wait.until(ExpectedConditions.visibilityOf(mapTypeDefault));

		wait.until(ExpectedConditions.elementToBeClickable(mapTypeSatellite)).click();
		wait.until(ExpectedConditions.visibilityOf(listView));

	}

	public void changeMapTypeToDefault() {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		wait.until(ExpectedConditions.visibilityOf(mapType));
		wait.until(ExpectedConditions.elementToBeClickable(mapType)).click();
		wait.until(ExpectedConditions.visibilityOf(mapTypeDefault));

		wait.until(ExpectedConditions.elementToBeClickable(mapTypeDefault)).click();
		wait.until(ExpectedConditions.visibilityOf(listView));

	}

}