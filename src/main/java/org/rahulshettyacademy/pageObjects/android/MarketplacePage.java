package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rahulshettyacademy.utils.AndroidActions;
import org.testng.Assert;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

/**
 * MarketplacePage - page object for the Marketplace feature.
 *
 * The Marketplace appears in the app when the user's location is set
 * to a marketplace-supported region (e.g., Canada). This page object
 * covers the LOCATION-CHANGE flow that surfaces the marketplace, NOT
 * the marketplace itself (storefront, cart, checkout - those would be
 * separate page objects added later).
 *
 * Flow covered:
 *   1. (Caller-handled) Tap Profile tab + open Settings hamburger
 *      Standard navigation - reuse ProfilePage / SettingsAndActivityPage
 *      methods from existing framework, not re-implemented here.
 *   2. Tap "Account and info" row in Settings
 *   3. Tap the location row on the Account-and-info screen
 *      (content-desc "my-profile-address")
 *   4. Tap the address-list locator icon
 *   5. Enter "Canada" in the location search textbox
 *   6. Tap the "Canada" suggestion
 *   7. Scroll to the bottom of the form
 *   8. Tap the "UPDATE" button
 *   9. Assert that "Shop" text appears (marketplace surfaced)
 *
 * After this flow, the marketplace UI is reachable and subsequent
 * marketplace-feature tests can navigate into it.
 *
 * Each step has on-failure dumpVisibleText() diagnostics for fast
 * debugging without re-running.
 */
public class MarketplacePage extends AndroidActions {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    public MarketplacePage(AndroidDriver driver) {
        super(driver);
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // ================================================================
    // ==========          VERIFIED XPATHS                       ======
    // ================================================================

    /** Settings menu row for "Account and info". */
    private static final String ACCOUNT_AND_INFO_ROW_XPATH =
        "//android.widget.TextView[@text=\"Account and info\"]";

    /**
     * Tappable row that opens the location-edit screen on the
     * Account-and-info page.
     *
     * Originally was //Button[@resource-id="android:id/button2"] (the
     * standard Android system AlertDialog 'negative' button id) on the
     * assumption that an AlertDialog was being shown. That XPath
     * timed out at 20s every run because no AlertDialog is actually
     * displayed; the location row is a regular app ViewGroup.
     *
     * Current locator is the app's content-desc 'my-profile-address',
     * verified via Appium Inspector.
     */
    private static final String LOCATION_BUTTON_XPATH =
        "//android.view.ViewGroup[@content-desc=\"my-profile-address\"]";

    /** Address-list locator icon on the location-edit screen. */
    private static final String ADDRESS_LIST_LOCATOR_XPATH =
        "//android.view.ViewGroup[@content-desc=\"my-profile-addresslist\"]"
        + "/android.widget.ImageView";

    /** "Enter location" search textbox. */
    private static final String LOCATION_SEARCH_BOX_XPATH =
        "//android.widget.EditText[@text=\"Enter location\"]";

    /** "Canada" suggestion in the location dropdown after typing. */
    private static final String CANADA_SUGGESTION_XPATH =
        "//android.widget.TextView[@text=\"Canada\"]";

    /** Final "UPDATE" button at the bottom of the form. */
    private static final String UPDATE_BTN_XPATH =
        "//android.widget.TextView[@text=\"UPDATE\"]";

    /**
     * "Shop" text - appears in the app navigation once a marketplace-
     * supported location is selected. Used as the post-update
     * assertion anchor.
     */
    private static final String SHOP_TEXT_XPATH =
        "//android.widget.TextView[@text=\"Shop\"]";

    /** Expected exact text value for the Shop assertion. */
    private static final String EXPECTED_SHOP_TEXT = "Shop";

    /** Search query to type into the location box. */
    private static final String LOCATION_SEARCH_QUERY = "Canada";

    // ================================================================
    // ==========    LOCATORS - MARKETPLACE BROWSE/CHECKOUT      ======
    // ================================================================
    // These cover the flow that runs AFTER AssertShopTextVisible:
    //   Shop tap -> See all -> Buy Now -> Checkout -> back/forth -> Place order

    /**
     * Shop entry point - tap to enter the marketplace view.
     *
     * Locator changed from descending to /android.widget.ImageView
     * to just the parent ViewGroup with the content-desc.
     * Reasoning: the inner ImageView's image asset loads
     * asynchronously, so visibility checks against the ImageView
     * routinely hit the 20s wait timeout. The parent ViewGroup is
     * structurally present immediately - much faster.
     */
    private static final String SHOP_ENTRY_XPATH =
        "//*[@content-desc=\"marketplace-view\"]";

    /** "See all" link on the marketplace landing screen. */
    private static final String SEE_ALL_XPATH =
        "//android.widget.TextView[@text=\"See all\"]";

    /**
     * "Buy Now" button - there are multiple on the product list screen,
     * indexed (//Buy Now)[1] for first, (//Buy Now)[2] for second. The
     * helper getBuyNowXpath(int) builds the indexed XPath at call time.
     */
    private String getBuyNowXpath(int index) {
        return "(//android.widget.TextView[@text=\"Buy Now\"])[" + index + "]";
    }

    /** "Checkout" button (appears after item added to cart). */
    private static final String CHECKOUT_BTN_XPATH =
        "//android.widget.TextView[@text=\"Checkout\"]";

    /**
     * Common header title TextView on Checkout / My Cart screens.
     * The text attribute holds the screen name, asserted by the test.
     */
    private static final String COMMON_HEADER_TITLE_XPATH =
        "//android.widget.TextView[@content-desc=\"common_header_title\"]";

    /**
     * "Powered by Stripe" image on the Checkout screen.
     * Positional XPath - no text or content-desc to anchor on.
     */
    private static final String POWERED_BY_STRIPE_XPATH =
        "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]"
        + "/android.widget.FrameLayout/android.view.ViewGroup"
        + "/android.view.ViewGroup/android.view.ViewGroup"
        + "/android.view.ViewGroup/android.widget.ScrollView"
        + "/android.view.ViewGroup/android.view.ViewGroup"
        + "/android.widget.ImageView";

    /** Back button on Checkout / My Cart common header. */
    /**
     * Back button on Checkout / My Cart common header.
     *
     * Targets the parent ViewGroup (the actual clickable element), not
     * the child ImageView. Earlier version descended to
     * .../android.widget.ImageView, which tapped but did NOT dispatch
     * the back action - the test reported green but the screen didn't
     * navigate. The downstream AssertTwoItemsTotal then failed because
     * we were still on the wrong screen.
     */
    private static final String COMMON_HEADER_BACK_BTN_XPATH =
        "//android.view.ViewGroup[@content-desc=\"common_header_back_button\"]";

    /**
     * "2 items..." total text on the My Cart screen after 2 items
     * have been added.
     *
     * Locator strategy: UiAutomator selector that anchors on
     * className="android.widget.TextView" and narrows via
     * textContains("2 items"). The price ($X.XX) is intentionally not
     * part of the locator - the cart total changes per run depending
     * on which products are selected, so any price-bound locator
     * would break on every catalog update.
     *
     * The assertion (in AssertTwoItemsTotalText below) ALSO checks
     * for the "2 items" substring on the fetched element's text -
     * defense in depth: even if UiAutomator's textContains is buggy
     * or finds a different TextView, the assertion confirms.
     */
    private static final String TWO_ITEMS_TOTAL_UIA_SELECTOR =
        "new UiSelector().className(\"android.widget.TextView\")"
        + ".textContains(\"2 items\")";
    private static final String EXPECTED_TWO_ITEMS_TOTAL = "2 items";

    /** "Place your order" final commit button on Checkout. */
    /**
     * "Place your order" button - locator updated to use the
     * test-place-order-button content-desc on the wrapping ViewGroup.
     * Previously used the TextView text-based locator, but the
     * content-desc approach is more reliable (taps the actual button
     * container, not its child text label which may not dispatch).
     */
    private static final String PLACE_YOUR_ORDER_XPATH =
        "//android.view.ViewGroup[@content-desc=\"test-place-order-button\"]";

    // ================================================================
    // ==========           PUBLIC METHODS                       ======
    // ================================================================

    /**
     * #1 - Tap "Account and info" row in Settings and activity.
     *      Caller is responsible for being on the Settings screen
     *      before invoking this (e.g., via the existing
     *      SettingsAndActivityPage.NavigatesToSettingAndActivityScreen()).
     */
    public void ClickAccountAndInfo() {
        log("===> ClickAccountAndInfo");
        WebElement row = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(ACCOUNT_AND_INFO_ROW_XPATH)));
        row.click();
        System.out.println("[ACTION] Clicked row");
        log("[OK]       Tapped Account and info");
        sleepQuiet(1500);
    }

    /**
     * #1.5 - Handle the Android system location-permission popup.
     *
     * After tapping "Account and info", Android shows the native OS
     * permission prompt ("Allow DogPack to access this device's
     * location?") with options 'Precise/Approximate', 'While using
     * the app', 'Only this time', "Don't allow".
     *
     * Per user spec we tap "While using the app" - granting permission.
     * If that exact text isn't present (Android version / OEM / locale
     * differences), a few common variants are tried. If no popup is
     * on screen at all (e.g., permission already granted on a prior
     * run), this method silently logs and returns without failing.
     *
     * NOTE: Per-candidate wait is 4s, so the worst-case latency when
     * NO popup is present is ~16s (4 candidates x 4s). On a run where
     * the popup IS present and the first candidate matches, latency
     * is just ~4s.
     */
    public void HandleLocationPermissionPopup() {
        log("===> HandleLocationPermissionPopup");
        String[] candidateTexts = {
            "While using the app",
            "While using app",
            "Allow only while using the app",
            "Allow"
        };
        WebDriverWait shortWait = new WebDriverWait(
                driver, Duration.ofSeconds(4));
        for (String text : candidateTexts) {
            String xpath = "//*[@text=\"" + text + "\"]";
            try {
                WebElement el = shortWait.until(
                        ExpectedConditions.elementToBeClickable(
                                AppiumBy.xpath(xpath)));
                el.click();
                System.out.println("[ACTION] Clicked el");
                log("[OK]       Tapped permission button: \"" + text
                        + "\" (granted)");
                sleepQuiet(2000);
                return;
            } catch (Exception ignore) {
                // try next candidate
            }
        }
        log("[INFO]     No location-permission popup detected on screen. "
                + "Either permission was already granted on a prior run, "
                + "or the popup hasn't appeared yet. Proceeding.");
    }

    /**
     * #2 - Tap the location row (content-desc 'my-profile-address') on
     *      the Account-and-info screen. Opens the location-edit screen.
     */
    public void ClickLocationDialogButton() {
        log("===> ClickLocationDialogButton");
        WebElement btn;
        try {
            btn = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(LOCATION_BUTTON_XPATH)));
        } catch (Exception e) {
            log("[FAIL]     Location row (my-profile-address) not "
                    + "clickable after 20s.");
            log("           If the row isn't on screen, the prior step "
                    + "may have landed on a different screen than "
                    + "Account-and-info.");
            dumpVisibleText();
            throw e;
        }
        btn.click();
        System.out.println("[ACTION] Clicked btn");
        log("[OK]       Tapped location row");
        sleepQuiet(1500);
    }

    /** #3 - Tap the address-list locator icon. */
    public void ClickAddressListLocator() {
        log("===> ClickAddressListLocator");
        WebElement icon = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(ADDRESS_LIST_LOCATOR_XPATH)));
        icon.click();
        System.out.println("[ACTION] Clicked icon");
        log("[OK]       Tapped address-list locator");
        sleepQuiet(1500);
    }

    /**
     * #4 - Tap the "Enter location" textbox and type the search query.
     *      Defaults to "Canada" (LOCATION_SEARCH_QUERY constant). The
     *      cleanup flow calls the (String) overload with "India".
     */
    public void EnterLocationSearchText() {
        EnterLocationSearchText(LOCATION_SEARCH_QUERY);
    }

    /**
     * Overload that accepts an arbitrary country name to type. Used by
     * the cleanup flow to type "India" instead of "Canada", reverting
     * the test user's location after the marketplace test completes.
     */
    public void EnterLocationSearchText(String query) {
        log("===> EnterLocationSearchText (\"" + query + "\")");

        log("[STEP 1/2] Tap 'Enter location' textbox");
        WebElement box = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(LOCATION_SEARCH_BOX_XPATH)));
        box.click();
        System.out.println("[ACTION] Clicked box");
        log("[OK]       Tapped textbox");
        sleepQuiet(500);

        log("[STEP 2/2] Type '" + query + "'");
        box.sendKeys(query);
        System.out.println("[ACTION] Entered text in box");
        log("[OK]       Sent keys");
        sleepQuiet(1500);
    }

    /**
     * #5 - Tap the "Canada" suggestion from the dropdown. Backward-compat
     *      thin wrapper around the parameterized SelectCountrySuggestion.
     */
    public void SelectCanadaSuggestion() {
        SelectCountrySuggestion("Canada");
    }

    /**
     * Parameterized country-suggestion tap. Used by the cleanup flow
     * to tap "India" instead of "Canada".
     */
    public void SelectCountrySuggestion(String country) {
        log("===> SelectCountrySuggestion (\"" + country + "\")");
        String xpath = "//android.widget.TextView[@text=\""
                + country + "\"]";
        WebElement el;
        try {
            el = new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.elementToBeClickable(
                            AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     '" + country + "' suggestion not visible after "
                    + "15s. Search query may not have produced a result.");
            dumpVisibleText();
            throw e;
        }
        el.click();
        System.out.println("[ACTION] Clicked el");
        log("[OK]       Selected " + country);
        sleepQuiet(1500);
    }

    /**
     * #6 - Scroll to the bottom of the form so the UPDATE button is
     *      in view. Uses UiAutomator's UiScrollable.scrollToEnd() which
     *      scrolls up to 10 times or until it can't scroll further.
     */
    public void ScrollToBottom() {
        log("===> ScrollToBottom (targeting UPDATE button)");

        // Primary attempt: scrollIntoView targeting UPDATE text directly.
        // This terminates at the right place regardless of which
        // scrollable container is the outer one (a previous green-but-
        // not-scrolling failure happened because scrollToEnd was
        // operating on an inner widget already at its end).
        boolean scrolledIn = false;
        try {
            driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))"
                + ".scrollIntoView(new UiSelector().text(\"UPDATE\"))"));
            scrolledIn = true;
            log("[OK]       scrollIntoView found UPDATE - scroll complete");
        } catch (Exception e) {
            log("[INFO]     scrollIntoView did not find UPDATE on the first "
                    + "scrollable. Falling back to manual scrollForward "
                    + "swipes.");
        }

        // Fallback: drive a few scrollForward swipes via UiScrollable.
        // Each scrollForward() advances roughly one viewport; setMaxSearchSwipes(1)
        // ensures each call does exactly one swipe and doesn't loop internally.
        if (!scrolledIn) {
            for (int i = 1; i <= 5; i++) {
                try {
                    driver.findElement(AppiumBy.androidUIAutomator(
                        "new UiScrollable(new UiSelector().scrollable(true))"
                        + ".setMaxSearchSwipes(1).scrollForward()"));
                    log("[INFO]     scrollForward swipe " + i + "/5 done");
                } catch (Exception ex) {
                    log("[INFO]     scrollForward swipe " + i + " hit end of "
                            + "scrollable - stopping");
                    break;
                }
                sleepQuiet(400);
            }
        }

        sleepQuiet(1000);
    }

    /** #7 - Tap the "UPDATE" button at the bottom of the form. */
    public void ClickUpdate() {
        log("===> ClickUpdate");
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(UPDATE_BTN_XPATH)));
        btn.click();
        System.out.println("[ACTION] Clicked btn");
        log("[OK]       Tapped UPDATE");
        // Generous settle - the update can trigger a few seconds of
        // async work (location save, UI refresh) before "Shop" appears.
        sleepQuiet(3000);
    }

    /**
     * #8 - Assert that "Shop" text appears anywhere in the visible UI,
     *      confirming the marketplace has been surfaced as a result of
     *      the location change.
     */
    public void AssertShopTextVisible() {
        log("[FLOW] AssertShopTextVisible: verifying Shop text");
        log("===> AssertShopTextVisible");
        WebElement shop;
        try {
            shop = new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            AppiumBy.xpath(SHOP_TEXT_XPATH)));
        } catch (Exception e) {
            log("[FAIL]     'Shop' text not visible after 20s.");
            log("           This means the marketplace did not surface "
                    + "after the location change to Canada.");
            dumpVisibleText();
            throw e;
        }
        String actual = shop.getAttribute("text");
        log("           Actual:   \"" + actual + "\"");
        log("           Expected: \"" + EXPECTED_SHOP_TEXT + "\"");
        Assert.assertEquals(actual, EXPECTED_SHOP_TEXT,
                "Shop text on the post-update screen does not match.");
        log("[PASS]     Shop text verified - marketplace surfaced");
    }

    // ================================================================
    // ==========  PUBLIC METHODS - MARKETPLACE BROWSE/CHECKOUT  ======
    // ================================================================

    /**
     * Tap the Shop / marketplace-view entry. Uses visibilityOfElementLocated
     * because the targeted child ImageView often has clickable=false;
     * the tap dispatches up to the clickable parent View.
     */
    public void ClickShop() {
        log("===> ClickShop");
        WebElement shop = wait.until(ExpectedConditions.visibilityOfElementLocated(
                AppiumBy.xpath(SHOP_ENTRY_XPATH)));
        shop.click();
        System.out.println("[ACTION] Clicked shop");
        log("[OK]       Tapped Shop");
        sleepQuiet(1000);
    }

    /**
     * Tap "Shop" via the marketplace view ImageView locator. This is
     * a DIFFERENT navigation than ClickShop() - it's used when
     * arriving at marketplace FROM the Profile screen (where the
     * Shop icon shows as a marketplace-view ImageView).
     *
     * Locator: //View[@content-desc="marketplace view"]/ImageView
     * (note: "marketplace view" with a space, lowercase 'v').
     */
    public void ClickShopFromProfileScreen() {
        log("===> ClickShopFromProfileScreen (marketplace view)");
        String xpath =
            "//android.view.View[@content-desc=\"marketplace view\"]"
            + "/android.widget.ImageView";
        WebElement el;
        try {
            el = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     marketplace view ImageView not clickable "
                    + "after 20s.");
            dumpVisibleText();
            throw e;
        }
        el.click();
        System.out.println("[ACTION] Clicked el");
        log("[OK]       Tapped marketplace view ImageView");
        sleepQuiet(2000);
    }

    // ================================================================
    // ==========    CART CLEANUP - delete all items             ======
    // ================================================================
    // Strategy:
    //   1. Extract item count N from the cart-total TextView. The
    //      text starts with "<N> items:" (or "<N> item:" for N=1)
    //      followed by a stable suffix "Total for selected items
    //      (excluding tax and shipping) ...". We use UiAutomator
    //      textContains on the suffix because it's the only stable
    //      part of the text - the leading count and trailing
    //      currency vary.
    //   2. Loop EXACTLY N times. Each iteration: find the FIRST
    //      element whose content-desc contains "test-delete-cart-item",
    //      tap it, sleep 1.5s for the list to re-render.
    //   3. If a mid-loop tap fails (element not found before N is
    //      reached), break with a warning rather than throwing.
    //      Defensive cleanup - partial deletion is better than no
    //      deletion.
    //
    // Edge cases:
    //   - Empty cart: count locator times out -> return 0 -> loop
    //     runs zero times -> no-op return.
    //   - Parse failure: return -1 -> loop runs zero times.
    // ================================================================

    /**
     * Orchestrate the deletion of all items from the cart screen.
     * Caller must already be on the CartView screen.
     *
     * Does NOT throw on mid-loop failure - partial cleanup is OK.
     * Throws only if the page-object scaffolding is broken (e.g.,
     * driver is null).
     */
    public void DeleteAllCartItems() {
        log("===> DeleteAllCartItems");
        int count = extractCartItemCount();
        log("[INFO]     Cart contains N=" + count + " item(s)");
        if (count <= 0) {
            log("[INFO]     Nothing to delete - returning");
            return;
        }

        for (int i = 1; i <= count; i++) {
            try {
                WebElement delBtn = findFirstDeleteButton();
                delBtn.click();
                System.out.println("[ACTION] Clicked delBtn");
                log("[OK]       Deleted item " + i + " of " + count);
                // 3s settle - list re-renders, animation, etc.
                sleepQuiet(3000);
            } catch (Exception e) {
                log("[WARN]     Stopped delete loop at iteration " + i
                        + " of " + count + ". Reason: "
                        + e.getClass().getSimpleName() + ": "
                        + e.getMessage());
                return;
            }
        }
        log("[OK]       Cart cleanup complete - removed N=" + count
                + " items");
    }

    /**
     * Internal: extract the item count from the cart-total TextView.
     * Returns:
     *   N (positive int): cart has N items
     *   0: empty cart (locator timeout)
     *   -1: matched but parse failed (data shape changed)
     *
     * Locator: UiAutomator textContains on the stable suffix
     * "Total for selected items (excluding tax and shipping)" -
     * present regardless of item count or currency.
     */
    private int extractCartItemCount() {
        log("===> extractCartItemCount (cart-total text)");
        String uia = "new UiSelector().className(\"android.widget.TextView\")"
                + ".textContains(\"Total for selected items "
                + "(excluding tax and shipping)\")";
        WebElement el;
        try {
            el = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            AppiumBy.androidUIAutomator(uia)));
        } catch (Exception e) {
            log("[INFO]     Cart-total text not found within 10s. "
                    + "Cart is likely empty.");
            return 0;
        }
        String text = el.getAttribute("text");
        log("           Cart total text: \"" + text + "\"");
        try {
            String[] parts = text.split("\\s+", 2);
            int n = Integer.parseInt(parts[0]);
            return n;
        } catch (Exception parseEx) {
            log("[WARN]     Could not parse leading int from text. "
                    + "Returning -1. Exception: "
                    + parseEx.getClass().getSimpleName());
            return -1;
        }
    }

    /**
     * Internal: locate the FIRST element on screen whose content-desc
     * contains the substring "test-delete-cart-item". Each delete
     * button in the cart has this attribute - tapping the first one
     * always works because the list re-renders after each deletion.
     *
     * 10s wait for visibility. Throws if no matching element found.
     */
    private WebElement findFirstDeleteButton() {
        String xpath =
            "(//*[contains(@content-desc, \"test-delete-cart-item\")])[1]";
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(
                        AppiumBy.xpath(xpath)));
    }

    /**
     * Tap the marketplace Header's search box to focus the input.
     *
     * Locator source: testID="mktplace_search_bar" - confirmed in the
     * React Native source at src/screen/Marketplace/components/SearchBar.tsx
     * (and wired into the marketplace Header via Header.tsx). Renders
     * inside the always-visible Header so available on Shop, StoreList,
     * CategoryView, etc.
     */
    public void ClickSearchBox() {
        log("===> ClickSearchBox (mktplace_search_bar)");
        String xpath = "//*[@content-desc=\"mktplace_search_bar\"]";
        WebElement box;
        try {
            box = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     Search box not clickable after 20s. "
                    + "Confirm the user is on a marketplace screen.");
            dumpVisibleText();
            throw e;
        }
        box.click();
        System.out.println("[ACTION] Clicked box");
        log("[OK]       Tapped search box");
        sleepQuiet(1000);
    }

    /**
     * Send the given query text into the (already-focused) search box.
     * Caller must have invoked ClickSearchBox() first so the input has
     * focus; we re-locate the element here because some keyboard pop-up
     * patterns invalidate the original WebElement reference.
     *
     * Parameterized (String) so different searches can be reused -
     * e.g. EnterSearchBoxText("card") for the payment-flow rebuild,
     * EnterSearchBoxText("greeting") for a different product line.
     */
    public void EnterSearchBoxText(String query) {
        log("===> EnterSearchBoxText (\"" + query + "\")");
        String xpath = "//*[@content-desc=\"mktplace_search_bar\"]";
        WebElement box;
        try {
            box = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     Search box not visible. Did ClickSearchBox "
                    + "succeed before this call?");
            dumpVisibleText();
            throw e;
        }
        box.sendKeys(query);
        System.out.println("[ACTION] Entered text in box");
        log("[OK]       Typed \"" + query + "\" into search box");
        sleepQuiet(1000);
    }

    /**
     * Submit the search by pressing the keyboard's Enter / Search key.
     *
     * The marketplace search bar uses returnKeyType="search" with an
     * onSubmitEditing handler that navigates to the category view with
     * the typed query as filter. There is NO visible search-submit
     * icon on the screen - pressing the Enter key on the soft
     * keyboard IS the submit action.
     *
     * Implementation: driver.pressKey(AndroidKey.ENTER) simulates a
     * hardware-style Enter keypress which the soft keyboard's IME
     * relays as an onSubmitEditing event to the React Native input.
     */
    public void PressEnterToSubmitSearch() {
        log("===> PressEnterToSubmitSearch (AndroidKey.ENTER)");
        try {
            driver.pressKey(new KeyEvent(AndroidKey.ENTER));
            System.out.println("[ACTION] Pressed device key");
            log("[OK]       Sent ENTER keypress - search submitted");
        } catch (Exception e) {
            log("[FAIL]     Could not send ENTER key. Exception: "
                    + e.getClass().getSimpleName() + ": "
                    + e.getMessage());
            throw e;
        }
        // Wait for search results / category view to navigate in
        sleepQuiet(2500);
    }

    // ================================================================
    // ==========  STRIPE PAYMENT SHEET - card form helpers      ======
    // ================================================================
    // The Stripe presentPaymentSheet() bottom-sheet renders three
    // EditText input slots (card number / expiry / CVC) inside a
    // ScrollView with a consistent structural pattern. Each input is
    // identified by its EditText index (1/2/3) plus a fixed
    // child-View path.
    //
    // Locator pattern (verified via Appium Inspector):
    //   //android.widget.ScrollView/android.view.View
    //   /android.view.View/android.view.View[1]
    //   /android.view.View/android.view.View
    //   /android.widget.EditText[N]
    // where N = 1 (card), 2 (MM/YY), 3 (CVC).
    //
    // NOTE: previous version descended one level further to
    // /android.view.View[2]. That worked for click() but caused
    // InvalidElementStateException on sendKeys because View[2] is
    // just a visual layer inside the compound input, not the actual
    // text-receiving EditText. Stripped to target the EditText
    // itself - works for both click and sendKeys.
    //
    // Brittle to Stripe SDK changes - if a future Stripe version
    // reorders its internal Views, the index-positional path will
    // break and we'll need to re-capture from Inspector.
    // ================================================================

    /**
     * Build the structural XPath for the N-th card-form EditText
     * inside Stripe's PaymentSheet. N = 1 (card number), 2 (MM/YY),
     * 3 (CVC).
     */
    private String buildCardFormFieldXPath(int editTextIndex) {
        return "//android.widget.ScrollView/android.view.View"
                + "/android.view.View/android.view.View[1]"
                + "/android.view.View/android.view.View"
                + "/android.widget.EditText[" + editTextIndex + "]";
    }

    /**
     * Tap (focus) the N-th card-form field on the Stripe PaymentSheet.
     * Public so test-class methods can invoke for specific fields.
     */
    public void ClickCardFormField(int editTextIndex) {
        log("===> ClickCardFormField (EditText[" + editTextIndex + "])");
        String xpath = buildCardFormFieldXPath(editTextIndex);
        WebElement el;
        try {
            el = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     Card-form field index " + editTextIndex
                    + " not clickable after 20s. Stripe SDK layout "
                    + "may have changed - re-capture XPath from "
                    + "Appium Inspector.");
            dumpVisibleText();
            throw e;
        }
        el.click();
        System.out.println("[ACTION] Clicked el");
        log("[OK]       Tapped card-form field index " + editTextIndex);
        sleepQuiet(1000);
    }

    /**
     * Re-locate the N-th card-form field and sendKeys the given
     * value into it. Re-locating (rather than caching the click
     * reference) avoids stale-element exceptions which are common
     * on Stripe's bridged native views.
     */
    public void TypeIntoCardFormField(int editTextIndex, String value) {
        log("===> TypeIntoCardFormField (EditText[" + editTextIndex
                + "], len=" + value.length() + ")");
        String xpath = buildCardFormFieldXPath(editTextIndex);
        WebElement el;
        try {
            el = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     Card-form field index " + editTextIndex
                    + " not visible. Did ClickCardFormField run first?");
            dumpVisibleText();
            throw e;
        }
        el.sendKeys(value);
        System.out.println("[ACTION] Entered text in el");
        log("[OK]       Sent " + value.length() + " chars to field index "
                + editTextIndex);
        sleepQuiet(1500);
    }

    /**
     * Tap the credit card number input (EditText[1]) on Stripe's
     * PaymentSheet. Backward-compat wrapper around ClickCardFormField(1).
     */
    public void ClickCardNumberBox() {
        log("===> ClickCardNumberBox");
        ClickCardFormField(1);
    }

    /**
     * Send the test card number into the focused card-number field.
     * Caller must have invoked ClickCardNumberBox() first.
     *
     * Send digits ONLY (no spaces). Stripe auto-formats the display
     * to "XXXX XXXX XXXX XXXX" as you type. Pre-formatting would
     * cause space characters to be rejected.
     *
     * Test card "4111111111111111" is a Visa test number that
     * Stripe's test environment accepts as a valid format.
     */
    public void EnterCardNumber(String digits) {
        log("===> EnterCardNumber");
        TypeIntoCardFormField(1, digits);
    }

    /**
     * Tap the MM/YY field, then type the expiry. Stripe auto-formats
     * the expiry display ("1230" -> "12/30"). The user's spec
     * specifies the value "12/30" which we pass literally - if
     * Stripe rejects the slash, this method may need adjustment
     * to strip it ("1230").
     */
    public void EnterMMYY(String mmyy) {
        log("===> EnterMMYY (\"" + mmyy + "\")");
        ClickCardFormField(2);
        TypeIntoCardFormField(2, mmyy);
    }

    /** Tap the CVC field, then type the 3-digit security code. */
    public void EnterCVC(String cvc) {
        log("===> EnterCVC (" + cvc.length() + " digits)");
        ClickCardFormField(3);
        TypeIntoCardFormField(3, cvc);
    }

    /**
     * Tap the primary Continue button on Stripe's PaymentSheet.
     *
     * Locator: resource-id="com.dogpack:id/primary_button" - this is
     * the native Android resource-id Stripe assigns to the main
     * action button. Resource-id locators are more reliable than
     * structural XPaths.
     */
    public void ClickContinueButtonOnPaymentSheet() {
        log("===> ClickContinueButtonOnPaymentSheet");
        String xpath =
            "//android.widget.Button[@resource-id="
            + "\"com.dogpack:id/primary_button\"]";
        WebElement btn;
        try {
            btn = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     Continue button (primary_button) not "
                    + "clickable after 20s.");
            dumpVisibleText();
            throw e;
        }
        btn.click();
        System.out.println("[ACTION] Clicked btn");
        log("[OK]       Tapped Continue button on PaymentSheet");
        sleepQuiet(3000);
    }

    /**
     * Assert the generic order-confirmation message is visible on the
     * post-order screen. The message is user-name-independent so this
     * works for any test user.
     *
     * Uses an EXACT-MATCH assertion (assertEquals). If the app source
     * ever changes the wording (even punctuation or a single word),
     * this will fail and the locator XPath needs updating.
     *
     * Note: the message contains an apostrophe in "You'll" / "you'll".
     * We use the ASCII straight apostrophe (U+0027). If the app
     * source uses a curly typographic apostrophe (U+2019) the exact
     * match will silently fail even though the strings look
     * identical. If that happens, the dump will show the actual text
     * and we can copy-paste the correct character.
     */
    public void AssertSuccessOrderMessage() {
        log("===> AssertSuccessOrderMessage");
        // Build the expected text via concatenation - kept under
        // editor-comfortable line widths.
        String expected =
            "You'll receive a confirmation email shortly, along "
            + "with updates about your order via email. You can "
            + "also track your order status anytime in your "
            + "profile, under the My Orders section.";
        String xpath =
            "//android.widget.TextView[@text=\"" + expected + "\"]";
        WebElement el;
        try {
            el = new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     Success-order confirmation TextView not "
                    + "visible after 20s. Possible causes: order "
                    + "didn't go through; app wording changed; or "
                    + "the apostrophe character in the source differs "
                    + "from our ASCII version.");
            dumpVisibleText();
            throw e;
        }
        String actual = el.getAttribute("text");
        log("           Actual:   \"" + actual + "\"");
        log("           Expected: \"" + expected + "\"");
        Assert.assertEquals(actual, expected,
                "Success-order message text does not match exactly.");
        log("[PASS]     Success-order confirmation verified (exact match)");
    }

    /**
     * Backward-compat wrapper - the test class calls
     * AssertThankYouMessageVisible() which now delegates to the
     * generic AssertSuccessOrderMessage(). Test method name kept the
     * same to avoid re-wiring the test class.
     */
    public void AssertThankYouMessageVisible() {
        log("[FLOW] AssertThankYouMessageVisible: verifying Thank You message");
        AssertSuccessOrderMessage();
    }

    /**
     * Tap "Continue Shopping" on the post-order confirmation screen.
     * Returns the user to the marketplace home (cart now empty,
     * order complete).
     */
    public void ClickContinueShopping() {
        log("===> ClickContinueShopping");
        String xpath =
            "//android.widget.TextView[@text=\"Continue Shopping\"]";
        WebElement btn;
        try {
            btn = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     Continue Shopping not clickable after 20s.");
            dumpVisibleText();
            throw e;
        }
        btn.click();
        System.out.println("[ACTION] Clicked btn");
        log("[OK]       Tapped Continue Shopping");
        sleepQuiet(2000);
    }

    /** Tap "See all" on the marketplace landing screen. */
    public void ClickSeeAll() {
        log("===> ClickSeeAll");
        WebElement seeAll = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(SEE_ALL_XPATH)));
        seeAll.click();
        System.out.println("[ACTION] Clicked seeAll");
        log("[OK]       Tapped See all");
        sleepQuiet(2000);
    }

    /**
     * Tap the N-th "Buy Now" button on the product list.
     *   ClickBuyNow(1) -> first product
     *   ClickBuyNow(2) -> second product
     *
     * Uses visibilityOfElementLocated because TextView "Buy Now" lives
     * inside a GradientLabels/MaskedView pattern where the TextView has
     * opacity=0 and may not be marked clickable; the click bubbles up
     * to the wrapping button.
     */
    public void ClickBuyNow(int index) {
        log("===> ClickBuyNow (index " + index + ")");
        String xpath = getBuyNowXpath(index);
        WebElement btn;
        try {
            btn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     Buy Now [" + index + "] not visible after 20s.");
            log("           Locator: " + xpath);
            dumpVisibleText();
            throw e;
        }
        btn.click();
        System.out.println("[ACTION] Clicked btn");
        log("[OK]       Tapped Buy Now [" + index + "]");
        sleepQuiet(2500);
    }

    /** Tap the "Checkout" button (used multiple times in the flow). */
    public void ClickCheckout() {
        log("===> ClickCheckout");
        WebElement checkout = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(CHECKOUT_BTN_XPATH)));
        checkout.click();
        System.out.println("[ACTION] Clicked checkout");
        log("[OK]       Tapped Checkout");
        sleepQuiet(2500);
    }

    /**
     * Assert the common header title text matches the expected value.
     * Used for both:
     *   - "Checkout" header (after tapping Checkout button)
     *   - "My Cart" header (after tapping back from Checkout)
     */
    public void AssertHeaderTitle(String expectedTitle) {
        log("===> AssertHeaderTitle (expected: \"" + expectedTitle + "\")");
        WebElement title;
        try {
            title = new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            AppiumBy.xpath(COMMON_HEADER_TITLE_XPATH)));
        } catch (Exception e) {
            log("[FAIL]     common_header_title not visible after 15s.");
            dumpVisibleText();
            throw e;
        }
        String actual = title.getAttribute("text");
        log("           Actual:        \"" + actual + "\"");
        log("           Expected (in): \"" + expectedTitle + "\"");
        Assert.assertTrue(
                actual != null && actual.contains(expectedTitle),
                "Common header title does not contain \"" + expectedTitle
                + "\". Actual: \"" + actual + "\"");
        log("[PASS]     Header title verified (substring match)");
    }

    /**
     * Assert the "Powered by Stripe" image is visible on the Checkout
     * screen. Positional XPath - this is a visibility check only; no
     * text or content-desc to compare against. If the Checkout screen's
     * UI is restructured, this XPath becomes fragile.
     */
    public void AssertPoweredByStripeImageVisible() {
        log("===> AssertPoweredByStripeImageVisible");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            AppiumBy.xpath(POWERED_BY_STRIPE_XPATH)));
            log("[PASS]     Powered by Stripe image visible at expected position");
        } catch (Exception e) {
            log("[FAIL]     Powered by Stripe image not visible after 15s.");
            log("           Positional XPath may be stale due to UI change.");
            dumpVisibleText();
            throw e;
        }
    }

    /**
     * Tap the back button in the common header. Used multiple times:
     *   - Checkout -> back to My Cart
     *   - My Cart -> back to product list
     */
    public void ClickBackButton() {
        log("===> ClickBackButton (common_header_back_button)");
        WebElement back = wait.until(ExpectedConditions.visibilityOfElementLocated(
                AppiumBy.xpath(COMMON_HEADER_BACK_BTN_XPATH)));
        back.click();
        System.out.println("[ACTION] Clicked back");
        log("[OK]       Tapped back button");
        sleepQuiet(2000);
    }

    /**
     * Assert the My Cart screen shows a TextView containing "2 items".
     * Locator is a UiAutomator selector (className + textContains) -
     * price-independent so cart total changes don't break the test.
     * The assertion verifies the fetched text contains "2 items" as
     * a second-layer check.
     */
    public void AssertTwoItemsTotalText() {
        log("===> AssertTwoItemsTotalText");
        WebElement totalEl;
        try {
            totalEl = new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            AppiumBy.androidUIAutomator(
                                    TWO_ITEMS_TOTAL_UIA_SELECTOR)));
        } catch (Exception e) {
            log("[FAIL]     No TextView containing '2 items' visible "
                    + "after 15s.");
            log("           Selector: " + TWO_ITEMS_TOTAL_UIA_SELECTOR);
            dumpVisibleText();
            throw e;
        }
        String actual = totalEl.getAttribute("text");
        log("           Actual:        \"" + actual + "\"");
        log("           Expected (in): \"" + EXPECTED_TWO_ITEMS_TOTAL + "\"");
        Assert.assertTrue(
                actual != null && actual.contains(EXPECTED_TWO_ITEMS_TOTAL),
                "Two-items-total text does not contain \""
                + EXPECTED_TWO_ITEMS_TOTAL + "\". Actual: \"" + actual
                + "\"");
        log("[PASS]     Two-items-total text verified (substring match)");
    }

    /** Tap the "Place your order" commit button on the final checkout. */
    public void ClickPlaceYourOrder() {
        log("===> ClickPlaceYourOrder");
        // 15s wait before the tap. Stripe's initPaymentSheet() is an
        // async call that runs in the background when the Checkout
        // screen loads. Tapping "Place your order" before init is
        // done causes presentPaymentSheet() to reject, which the app
        // surfaces as the "Invalid Payment Details" popup. Humans
        // wait naturally; automation needs an explicit pause. 15s is
        // generous - typical init completes in 2-4s but bumped up
        // per user request for added margin on slower test runs.
        log("[INFO]     Waiting 15s for Stripe initPaymentSheet() to "
                + "complete before tap...");
        sleepQuiet(15000);

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(PLACE_YOUR_ORDER_XPATH)));
        btn.click();
        System.out.println("[ACTION] Clicked btn");
        log("[OK]       Tapped Place your order");
        // No assertion after the click - user spec ends here. If a
        // confirmation screen check is needed in the future, add a
        // separate method.
        sleepQuiet(3000);
    }

    /**
     * "OK" button on the order-confirmation dialog that appears after
     * tapping "Place your order". Dismisses the dialog and returns to
     * a clean screen. Note: this is a generic "OK" - if any other OK
     * is on screen at this moment, findElement may grab the wrong
     * one. In practice the confirmation dialog dominates so collision
     * risk is low.
     */
    public void ClickOK() {
        log("===> ClickOK (order-confirmation dialog)");
        String xpath = "//android.widget.TextView[@text=\"OK\"]";
        WebElement btn;
        try {
            btn = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     OK button not clickable after 20s.");
            log("           Locator: " + xpath);
            dumpVisibleText();
            throw e;
        }
        btn.click();
        System.out.println("[ACTION] Clicked btn");
        log("[OK]       Tapped OK - order confirmation dismissed");
        sleepQuiet(2000);
    }

    /**
     * Tap the cart icon in the marketplace Header (top-right area).
     * Navigates from any marketplace screen (Shop, StoreList,
     * CategoryView, etc.) directly into the CartView screen.
     *
     * Locator source: confirmed in the React Native source code at
     *   src/screen/Marketplace/components/Header.tsx line 166:
     *   testID="mktplace_go_to_cart" on a TouchableOpacity that calls
     *   navigation.navigate("CartView"). The button renders
     *   unconditionally on the Header (not gated by visibility).
     */
    public void ClickCartIcon() {
        log("===> ClickCartIcon (mktplace_go_to_cart)");
        String xpath = "//*[@content-desc=\"mktplace_go_to_cart\"]";
        WebElement el;
        try {
            el = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     Cart icon not clickable after 20s. "
                    + "Make sure the user is on a marketplace screen "
                    + "(Shop / StoreList / CategoryView).");
            dumpVisibleText();
            throw e;
        }
        el.click();
        System.out.println("[ACTION] Clicked el");
        log("[OK]       Tapped cart icon - navigating to CartView");
        sleepQuiet(2000);
    }

    // ================================================================
    // ==========  CLEANUP-ONLY METHODS (revert to India)        ======
    // ================================================================
    // The following methods are used by the @AfterMethod cleanup flow
    // in Dogpack_Marketplace, NOT by the main test sequence. They use
    // different locators than the main flow's navigation methods on
    // purpose - the user specified specific cleanup locators and we
    // keep them self-contained to avoid disturbing the main-flow
    // methods (which are battle-tested across the 27-test chain).

    /**
     * Tap the Profile tab by its visible "Profile" TextView label.
     * Used by cleanup ONLY. Different from the main flow's
     * ProfilePage.navigateToProfileScreen() which uses a
     * content-desc="profile-view" descent into an ImageView.
     */
    public void ClickProfileTabByText() {
        log("===> ClickProfileTabByText (cleanup)");
        String xpath = "//android.widget.TextView[@text=\"Profile\"]";
        WebElement el;
        try {
            el = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     Profile TextView tab not clickable after 20s.");
            dumpVisibleText();
            throw e;
        }
        el.click();
        System.out.println("[ACTION] Clicked el");
        log("[OK]       Tapped Profile tab (by text)");
        sleepQuiet(2000);
    }

    /**
     * Tap the dog-profile hamburger menu icon. Used by cleanup ONLY.
     * Same XPath that the existing
     * SettingsAndActivityPage.NavigatesToSettingAndActivityScreen()
     * uses internally, but reimplemented here so the cleanup is
     * self-contained and doesn't drag in the larger method's
     * retry/modal-dismissal machinery.
     */
    public void ClickHamburgerMenu() {
        log("===> ClickHamburgerMenu (cleanup)");
        String xpath = "//android.view.ViewGroup[@content-desc="
                + "\"dog_profile_hamburger_menu\"]/android.widget.ImageView";
        WebElement el;
        try {
            el = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     Hamburger menu not clickable after 20s.");
            dumpVisibleText();
            throw e;
        }
        el.click();
        System.out.println("[ACTION] Clicked el");
        log("[OK]       Tapped hamburger menu");
        sleepQuiet(2000);
    }

    /**
     * Tap the "Account and info" row by its content-desc. Used by
     * cleanup ONLY. Different from the main flow's
     * ClickAccountAndInfo() which uses the TextView text-based locator.
     */
    public void ClickAccountAndInfoByDesc() {
        log("===> ClickAccountAndInfoByDesc (cleanup)");
        String xpath = "//android.view.ViewGroup[@content-desc="
                + "\"Account and info\"]";
        WebElement el;
        try {
            el = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     Account and info (by content-desc) not "
                    + "clickable after 20s.");
            dumpVisibleText();
            throw e;
        }
        el.click();
        System.out.println("[ACTION] Clicked el");
        log("[OK]       Tapped Account and info (by content-desc)");
        sleepQuiet(2000);
    }

    /**
     * Assert that a TextView with text "Search" is visible. Used by
     * cleanup ONLY - confirms the India revert took effect (Indian
     * users see a "Search" UI element where Canadian users would
     * see "Shop"). Uses substring contains() so any UI suffixes
     * (badges, counts) don't break the verification.
     *
     * Throws RuntimeException if the assertion fails - callers
     * (the @AfterMethod cleanup) catch+log+rethrow so TestNG records
     * the cleanup failure as a separate failure event in the report.
     */
    public void AssertSearchTextVisible() {
        log("===> AssertSearchTextVisible (cleanup verification)");
        String xpath = "//android.widget.TextView[@text=\"Search\"]";
        WebElement el;
        try {
            el = new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     'Search' TextView not visible after 20s. "
                    + "India revert may not have taken effect.");
            dumpVisibleText();
            throw e;
        }
        String actual = el.getAttribute("text");
        log("           Actual:        \"" + actual + "\"");
        log("           Expected (in): \"Search\"");
        Assert.assertTrue(
                actual != null && actual.contains("Search"),
                "Search text not found after India revert. "
                + "Cleanup did not surface the Indian-user UI. "
                + "Actual: \"" + actual + "\"");
        log("[PASS]     Search text verified - India revert confirmed");
    }

    // ================================================================
    // ==========           PRIVATE HELPERS                      ======
    // ================================================================

    private void log(String msg) {
        System.out.println(msg);
    }

    private void sleepQuiet(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }

    private void dumpVisibleText() {
        try {
            List<WebElement> textViews = driver.findElements(
                    AppiumBy.xpath("//android.widget.TextView"));
            log("           ---- visible TextViews ----");
            int n = 0;
            for (WebElement tv : textViews) {
                try {
                    if (!tv.isDisplayed()) continue;
                    String t = tv.getAttribute("text");
                    if (t == null || t.isEmpty()) continue;
                    log("           [TV] \"" + t + "\"");
                    if (++n >= 20) { log("           ...(truncated)"); break; }
                } catch (Exception ignore) { /* */ }
            }
        } catch (Exception ignore) { /* */ }
        try {
            List<WebElement> withDesc = driver.findElements(
                    AppiumBy.xpath("//*[@content-desc and string-length(@content-desc) > 0]"));
            log("           ---- visible elements with content-desc ----");
            int n = 0;
            for (WebElement el : withDesc) {
                try {
                    if (!el.isDisplayed()) continue;
                    String d = el.getAttribute("content-desc");
                    log("           [DESC] \"" + d + "\"");
                    if (++n >= 20) { log("           ...(truncated)"); break; }
                } catch (Exception ignore) { /* */ }
            }
        } catch (Exception ignore) { /* */ }
    }
}