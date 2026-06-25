package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.rahulshettyacademy.utils.AndroidActions;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

/**
 * SearchDiscoveryPage - page object for the Search & Discovery feature.
 * Holds the locators and actions for opening Search from the home screen,
 * handling the (conditional) location permission, typing a query, submitting
 * it, and reading the first result's profile name.
 *
 * <p>Follows the POM pattern used by the other page objects (extends
 * {@link AndroidActions}, own {@code wait}, PageFactory-initialised locators).
 */
public class SearchDiscoveryPage extends AndroidActions {

	AndroidDriver driver;
	WebDriverWait wait;

	public SearchDiscoveryPage(AndroidDriver driver) {
		super(driver);
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	// ================================================================
	// =====                    LOCATORS                         ======
	// ================================================================

	// 1 - Search button on the home screen.
	@AndroidFindBy(xpath = "//android.view.View[@content-desc=\"search-view\"]")
	private WebElement searchButton;

	// 2 - "While using the app" location permission (CONDITIONAL system dialog).
	@AndroidFindBy(xpath = "//android.widget.Button[@resource-id=\"com.android.permissioncontroller:id/permission_allow_foreground_only_button\"]")
	private WebElement whileUsingAppBtn;

	// 3 - Search text box.
	@AndroidFindBy(xpath = "//android.widget.EditText[@content-desc=\"search_dog_par_bus\"]")
	private WebElement searchTextBox;

	// 4 - Enter / submit search.
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"search_newSearch\"]/android.widget.ImageView")
	private WebElement enterButton;

	// 5 - First result's profile name.
	@AndroidFindBy(xpath = "//android.widget.TextView[@content-desc=\"dog_name\"]")
	private WebElement dogName;

	// 6 - Follow button on the first result (content-desc: follow-0).
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"follow-0\"]")
	private WebElement followButton;

	// 7 - Following/Unfollow button after following (content-desc: followUnfollow-0).
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"followUnfollow-0\"]")
	private WebElement followingButton;

	// 8 - Confirm dialog for unfollow.
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"onConfirm\"]")
	private WebElement confirmUnfollow;

	// ---- Park Search ----
	// 9 - Reset / clear search (the 'x').
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"search_recentx\"]")
	private WebElement resetSearchBtn;

	// 10 - PARKS tab.
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"PARKS\"]")
	private WebElement parksTab;

	// 11 - Park result name.
	@AndroidFindBy(xpath = "//android.widget.TextView[@content-desc=\"search_park_name\"]")
	private WebElement parkName;

	// 12 - Park Follow button (ViewGroup; getText returns 'Follow').
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"park_follo0\"]")
	private WebElement parkFollowBtn;

	// 13 - 'LATER' prompt shown after following a park.
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"LATER\"]")
	private WebElement parkLaterBtn;

	// 14 - Park 'FOLLOWING' label (read + click-to-unfollow).
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"FOLLOWING\"]")
	private WebElement parkFollowingText;

	// 15 - Park unfollow confirm.
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Confirm\"]")
	private WebElement parkConfirmBtn;

	// 16 - Park 'FOLLOW' label after unfollow.
	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"FOLLOW\"]")
	private WebElement parkFollowText;

	// ================================================================
	// =====                     ACTIONS                         ======
	// ================================================================

	/**
	 * 1 - open Search from the home screen, then handle the (conditional)
	 * location permission best-effort (folded in - see
	 * handleWhileUsingAppIfPresent).
	 */
	public void clickSearch() {
		wait.until(ExpectedConditions.visibilityOf(searchButton));
		wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
		System.out.println("[ACTION] Clicked Search");
		handleWhileUsingAppIfPresent();
	}

	/**
	 * 2 - allow location ("While using the app"). Best-effort: this system
	 * dialog only appears the first time location is requested, so it is
	 * skipped silently when already granted.
	 */
	private void handleWhileUsingAppIfPresent() {
		try {
			WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
			shortWait.until(ExpectedConditions.elementToBeClickable(whileUsingAppBtn)).click();
			System.out.println("[ACTION] Allowed location (While using the app)");
		} catch (Exception e) {
			System.out.println("[INFO] Location permission dialog not present - skipping");
		}
	}

	/** 3 - focus the search box and type the query. */
	public void enterSearchText(String text) {
		wait.until(ExpectedConditions.visibilityOf(searchTextBox));
		wait.until(ExpectedConditions.elementToBeClickable(searchTextBox)).click();
		try { searchTextBox.clear(); } catch (Exception ignored) {}
		searchTextBox.sendKeys(text);
		System.out.println("[INPUT] Entered search text: " + text);
	}

	/** 4 - submit the search. */
	public void clickEnter() {
		wait.until(ExpectedConditions.visibilityOf(enterButton));
		wait.until(ExpectedConditions.elementToBeClickable(enterButton)).click();
		System.out.println("[ACTION] Clicked search enter");
	}

	/** 5 - read the first result's profile name (dog_name). */
	public String getDogName() {
		WebElement el = wait.until(ExpectedConditions.visibilityOf(dogName));
		String txt = el.getText();
		System.out.println("[INFO] Search result dog_name: " + txt);
		return txt;
	}

	/** 6 - read the Follow button label (follow-0). */
	public String getFollowButtonText() {
		return readButtonText(followButton);
	}

	/** 7 - click Follow (follow-0). */
	public void clickFollow() {
		wait.until(ExpectedConditions.elementToBeClickable(followButton)).click();
		System.out.println("[ACTION] Clicked Follow");
	}

	/** 8 - read the Following button label (followUnfollow-0). */
	public String getFollowingButtonText() {
		return readButtonText(followingButton);
	}

	/** 9 - click Following to unfollow (followUnfollow-0). */
	public void clickUnfollow() {
		wait.until(ExpectedConditions.elementToBeClickable(followingButton)).click();
		System.out.println("[ACTION] Clicked Following (unfollow)");
	}

	/** 10 - confirm the unfollow. */
	public void clickConfirmUnfollow() {
		wait.until(ExpectedConditions.elementToBeClickable(confirmUnfollow)).click();
		System.out.println("[ACTION] Clicked Confirm (unfollow)");
	}

	/**
	 * Reads a button's label. The control is a ViewGroup, so getText() on it
	 * can be empty - fall back to the child TextView in that case.
	 */
	private String readButtonText(WebElement btn) {
		WebElement el = wait.until(ExpectedConditions.visibilityOf(btn));
		String t = el.getText();
		if (t == null || t.trim().isEmpty()) {
			try {
				t = el.findElement(By.xpath(".//android.widget.TextView")).getText();
			} catch (Exception ignored) {
			}
		}
		String out = (t == null) ? "" : t.trim();
		System.out.println("[INFO] Button text read: '" + out + "'");
		return out;
	}

	// ---- Park Search actions ----

	/** click the reset/clear-search button. */
	public void clickResetSearch() {
		wait.until(ExpectedConditions.elementToBeClickable(resetSearchBtn)).click();
		System.out.println("[ACTION] Reset search");
	}

	/** switch to the PARKS tab. */
	public void clickParksTab() {
		wait.until(ExpectedConditions.elementToBeClickable(parksTab)).click();
		System.out.println("[ACTION] Clicked PARKS tab");
	}

	/** read the park result name (search_park_name). */
	public String getParkName() {
		String t = wait.until(ExpectedConditions.visibilityOf(parkName)).getText();
		String out = (t == null) ? "" : t.trim();
		System.out.println("[INFO] search_park_name: '" + out + "'");
		return out;
	}

	/**
	 * read the park Follow button label directly from the ViewGroup
	 * (returns mixed-case 'Follow' - do NOT fall back to the child TextView,
	 * which is the uppercase 'FOLLOW').
	 */
	public String getParkFollowButtonText() {
		WebElement vg = wait.until(ExpectedConditions.visibilityOf(parkFollowBtn));
		String t = vg.getText();
		if (t == null || t.trim().isEmpty()) {
			// the ViewGroup has no text of its own - read the child TextView
			try {
				t = vg.findElement(By.xpath(".//android.widget.TextView")).getText();
			} catch (Exception ignored) {
			}
		}
		String out = (t == null) ? "" : t.trim();
		System.out.println("[INFO] park_follo0 text: '" + out + "'");
		return out;
	}

	/** click the park Follow button (park_follo0). */
	public void clickParkFollow() {
		wait.until(ExpectedConditions.elementToBeClickable(parkFollowBtn)).click();
		System.out.println("[ACTION] Clicked Park Follow");
	}

	/** click the 'LATER' prompt. */
	public void clickParkLater() {
		wait.until(ExpectedConditions.elementToBeClickable(parkLaterBtn)).click();
		System.out.println("[ACTION] Clicked LATER");
	}

	/** read the park 'FOLLOWING' label. */
	public String getParkFollowingText() {
		String t = wait.until(ExpectedConditions.visibilityOf(parkFollowingText)).getText();
		String out = (t == null) ? "" : t.trim();
		System.out.println("[INFO] park FOLLOWING text: '" + out + "'");
		return out;
	}

	/** click the park 'FOLLOWING' label to unfollow. */
	public void clickParkUnfollow() {
		wait.until(ExpectedConditions.elementToBeClickable(parkFollowingText)).click();
		System.out.println("[ACTION] Clicked Park FOLLOWING (unfollow)");
	}

	/** confirm the park unfollow. */
	public void clickParkConfirm() {
		wait.until(ExpectedConditions.elementToBeClickable(parkConfirmBtn)).click();
		System.out.println("[ACTION] Clicked Confirm (park unfollow)");
	}

	/** read the park 'FOLLOW' label after unfollow. */
	public String getParkFollowText() {
		String t = wait.until(ExpectedConditions.visibilityOf(parkFollowText)).getText();
		String out = (t == null) ? "" : t.trim();
		System.out.println("[INFO] park FOLLOW text: '" + out + "'");
		return out;
	}
}