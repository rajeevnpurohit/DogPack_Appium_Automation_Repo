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

/**
 * ProfileSwitcherPage - page object for the in-app profile switcher.
 *
 * Used when a single logged-in account has MULTIPLE entities attached -
 * e.g., one dog profile AND one or more business profiles. The switcher
 * lives inside the Profile screen header.
 *
 * Full flow (7 steps):
 *   1. Tap Profile (bottom-nav tab)
 *   2. Tap username dropdown chevron
 *   3. Tap "Business Profile" expand chevron
 *   4. Tap 4th ImageView on screen (the business entity icon - first
 *      working selector landed via .instance(3) UiAutomator strategy)
 *   5. Tap the SwitchProfile ViewGroup to commit the entity selection
 *   6. Tap Profile (bottom-nav tab) again - re-renders the profile
 *      screen so the new entity's title text is visible
 *   7. Assert the title text equals "Tipper9Business" (replaces the
 *      generic business_edPro presence check with an exact-name
 *      assertion against the title TextView)
 *
 * USAGE:
 *   Call SwitchToFirstBusinessProfile() AFTER a successful login and
 *   after ProfilePage.navigateToProfileScreen() has confirmed the
 *   Profile screen is loaded.
 *
 * After this method returns, the active entity is "Tipper9Business" and
 * the profile screen header reflects that name.
 *
 * LOCATOR MIX:
 *   - Steps 1, 2, 3, 6: XPath via AppiumBy.xpath
 *   - Step 4: UiAutomator selector via AppiumBy.androidUIAutomator
 *             (XPath was ambiguous / fragile here)
 *   - Step 5: XPath
 *   - Step 7: XPath for the TextView, then TestNG Assert on its text attribute
 */
public class ProfileSwitcherPage extends AndroidActions {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    public ProfileSwitcherPage(AndroidDriver driver) {
        super(driver);
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // ================================================================
    // ==========          VERIFIED LOCATORS                     ======
    // ================================================================

    /** STEP 1 + STEP 6 - Profile tab icon in bottom navigation. */
    private static final String PROFILE_TAB_XPATH =
        "//android.view.View[@content-desc=\"profile-view\"]"
        + "/android.view.ViewGroup/android.widget.ImageView";

    /** STEP 2 - Username dropdown arrow on the Profile screen header. */
    private static final String USERNAME_DROPDOWN_XPATH =
        "//android.view.ViewGroup[@content-desc=\"arrow_down_click\"]"
        + "/android.widget.ImageView";

    /** STEP 3 - "Business Profile" section expand chevron. */
    private static final String BUSINESS_PROFILE_SECTION_XPATH =
        "//android.view.ViewGroup[@content-desc=\"arrow_down_click\"]";

    /**
     * STEP 4 - First business profile entry's ImageView.
     * UiAutomator selector (NOT XPath): the 4th ImageView on screen.
     * Must be passed to AppiumBy.androidUIAutomator(), not .xpath().
     */
    private static final String FIRST_BUSINESS_PROFILE_UIA_SELECTOR =
        "new UiSelector().className(\"android.widget.ImageView\").instance(3)";

    /**
     * STEP 5 - The SwitchProfile ViewGroup that commits the entity
     * selection. Targets the inner ViewGroup child of the container
     * that carries content-desc='dogbus-business-SwitchProfile'.
     */
    private static final String PROFILE_SELECTION_XPATH =
        "//android.view.ViewGroup[@content-desc=\"dogbus-business-SwitchProfile\"]"
        + "/android.view.ViewGroup";

    /**
     * STEP 7 - Title text TextView on the (now business-flavoured)
     * Profile screen. Carries content-desc='profile_title_text'. We
     * locate the element via this XPath, then read its 'text' attribute
     * and assert it equals "Tipper9Business" (exact match).
     */
    private static final String PROFILE_TITLE_TEXT_XPATH =
        "//android.widget.TextView[@content-desc=\"profile_title_text\"]";

    /** Expected title text after the switch lands. */
    private static final String EXPECTED_BUSINESS_TITLE = "Tipper9Business";

    // ================================================================
    // ==========           PUBLIC METHODS                       ======
    // ================================================================

    /**
     * Walk through the 7-step profile-switcher flow to activate the
     * "Tipper9Business" entity, then assert by exact title-text match.
     *
     * On any step failure, diagnostic dump (visible TextViews +
     * elements with content-desc) prints to stdout so the next
     * iteration knows ground truth.
     */
    /**
     * Walk through the 7-step profile-switcher flow to activate the
     * first business profile attached to the current user account.
     * Verifies success by asserting the resulting Profile screen
     * title text equals the default EXPECTED_BUSINESS_TITLE
     * ("Tipper9Business").
     *
     * This no-arg overload preserves backward-compatibility for the
     * 4+ existing test classes that operate on tip@yopmail.com (whose
     * business entity is "Tipper9Business"). For accounts with a
     * different business display name (e.g., dpdelete@yopmail.com's
     * "Dpdeletebusiness"), call the parameterized overload below.
     */
    public void SwitchToFirstBusinessProfile() {
        SwitchToFirstBusinessProfile(EXPECTED_BUSINESS_TITLE);
    }

    /**
     * Parameterized version - allows callers to specify the expected
     * business display name. The 6 navigation steps are unchanged
     * (they don't depend on the name); only step 7's title assertion
     * uses the parameter.
     *
     * @param expectedBusinessName the exact text the Profile screen
     *        title should display after the switch lands (e.g.,
     *        "Tipper9Business" for tip@, "Dpdeletebusiness" for
     *        dpdelete@). Case-sensitive exact match.
     */
    public void SwitchToFirstBusinessProfile(String expectedBusinessName) {
        log("===> SwitchToFirstBusinessProfile (expecting \""
                + expectedBusinessName + "\")");

        // ---- STEP 1 ----
        log("[STEP 1/7] Tap Profile tab");
        WebElement profileTab = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(PROFILE_TAB_XPATH)));
        profileTab.click();
        log("[OK]       Tapped Profile tab");
        sleepQuiet(1000);

        // ---- STEP 2 ----
        log("[STEP 2/7] Tap username dropdown arrow");
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(USERNAME_DROPDOWN_XPATH)));
        dropdown.click();
        log("[OK]       Tapped username dropdown");
        sleepQuiet(1500);

        // ---- STEP 3 ----
        log("[STEP 3/7] Tap 'Business Profile' section header");
        WebElement bizSection = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(BUSINESS_PROFILE_SECTION_XPATH)));
        bizSection.click();
        log("[OK]       Tapped Business Profile section");
        sleepQuiet(1500);

        // ---- STEP 4 ----
        log("[STEP 4/7] Tap the first business profile entry (.instance(3))");
        WebElement firstBiz;
        try {
            firstBiz = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.androidUIAutomator(FIRST_BUSINESS_PROFILE_UIA_SELECTOR)));
        } catch (Exception e) {
            log("[FAIL]     Step 4 - First business profile entry not visible after 20s.");
            log("           Locator: " + FIRST_BUSINESS_PROFILE_UIA_SELECTOR);
            dumpVisibleText();
            throw e;
        }
        firstBiz.click();
        log("[OK]       Tapped first business profile entry");
        sleepQuiet(1500);

        // ---- STEP 5 ----
        log("[STEP 5/7] Tap ProfileSelection (commits entity switch)");
        WebElement profileSelection;
        try {
            profileSelection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.xpath(PROFILE_SELECTION_XPATH)));
        } catch (Exception e) {
            log("[FAIL]     Step 5 - ProfileSelection not visible after 20s.");
            log("           Locator: " + PROFILE_SELECTION_XPATH);
            dumpVisibleText();
            throw e;
        }
        profileSelection.click();
        log("[OK]       Tapped ProfileSelection");
        sleepQuiet(2000);

        // ---- STEP 6 ----
        log("[STEP 6/7] Tap Profile tab again (re-render with new entity)");

        // Fresh-app-data runs (data cleared each @BeforeClass) show first-run
        // onboarding overlays (units popup, profile coach-mark tooltips) that sit
        // on top of the UI and intercept taps - which is why the navigation tap
        // was landing on an overlay and bouncing back to Feed instead of opening
        // the profile. Clear them first, then do the single Profile-tab tap.
        dismissOnboardingCoachMarks(4);

        WebElement profileTabAgain = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(PROFILE_TAB_XPATH)));
        profileTabAgain.click();
        log("[OK]       Tapped Profile tab again");
        sleepQuiet(2000);

        // ---- STEP 7 ----
        log("[STEP 7/7] Assert title text equals \""
                + expectedBusinessName + "\"");

        // Settle pause: let the profile-screen transition begin to render before
        // we start polling. The profile-view container (React Native) can mount a
        // beat after the tab tap, especially under full-suite load.
        sleepQuiet(1500);

        // The profile screen itself shows first-run coach-marks ("hold down the
        // profile icon...", "access your settings...") on a fresh-data run; clear
        // them so they don't cover profile-view or intercept the read.
        dismissOnboardingCoachMarks(4);

        // Bounded polling on PRESENCE (not visibility). A React Native container
        // View often reports displayed=false to Selenium's visibility algorithm
        // even when it is on screen (its pixels are drawn by a child), which makes
        // visibilityOfElementLocated time out on exactly the element that Appium
        // Inspector - which taps by coordinate - can see and act on. Polling on
        // presenceOfElementLocated only requires the node to exist in the UI tree,
        // matching what Inspector sees. WebDriverWait polls every 500ms and returns
        // the instant the node appears, so this is fast when profile-view loads
        // quickly and patient (up to 30s) when it is slow - not a flat sleep.
        WebElement titleEl;
        try {
            WebDriverWait presenceWait =
                    new WebDriverWait(driver, Duration.ofSeconds(30));
            titleEl = presenceWait.until(ExpectedConditions.presenceOfElementLocated(
                    AppiumBy.xpath(PROFILE_TITLE_TEXT_XPATH)));
        } catch (Exception e) {
            log("[FAIL]     Step 7 - profile_title_text not present in UI tree after 30s.");
            log("           Locator: " + PROFILE_TITLE_TEXT_XPATH);
            dumpVisibleText();
            throw e;
        }

        // Post-presence settle: if the parent View appeared first, give RN a
        // moment to finish mounting its children before we read from it.
        sleepQuiet(500);

        String actualTitle = titleEl.getAttribute("text");
        log("           Actual title text:   \"" + actualTitle + "\"");
        log("           Expected title text: \"" + expectedBusinessName + "\"");
        Assert.assertEquals(actualTitle, expectedBusinessName,
                "Profile title text does not match the expected business "
                + "entity name. Switch may not have landed on the correct "
                + "profile.");
        log("[PASS]     Title matches - switched to "
                + expectedBusinessName + " successfully");
    }

    // ================================================================
    // ==========    ONBOARDING / COACH-MARK DISMISSAL          ======
    // ================================================================

    /**
     * Best-effort dismissal of first-run onboarding overlays that appear on a
     * fresh-app-data run (app data is cleared each @BeforeClass in this suite).
     * These overlays sit on top of the real UI and intercept taps, which is why
     * the post-switch navigation tap was landing on an overlay and bouncing back
     * to Feed instead of opening the profile screen.
     *
     * Overlays observed on a device recording (2026-07-10):
     *   - "Set your preferred units of measurement" popup   -> "Submit"
     *   - Profile coach-mark tooltips ("hold down the profile
     *     icon...", "access your settings...") + feed
     *     customization coach-mark                           -> "Skip" / "Done"
     *   - "Turn on Notifications" screen                     -> "SKIP"
     *
     * Uses raw findElements (no PageFactory proxy / implicit-wait burn) so a
     * missing overlay costs about half a second, not a full wait timeout. Loops
     * a few rounds because several overlays can be queued back-to-back, and
     * stops early as soon as a round finds nothing to dismiss.
     *
     * NOTE: these are TEXT-based best-effort locators taken from the recording,
     * not Inspector-verified resource-ids. "Skip"/"Done"/"Submit" dismiss an
     * overlay outright; "Next" (which only advances a coach-mark) is deliberately
     * NOT tapped, to avoid advancing-without-dismissing loops - the round loop
     * plus a present "Done"/"Skip" handles multi-step tooltips. If a future build
     * changes the button copy, update the text list here.
     */
    private void dismissOnboardingCoachMarks(int maxRounds) {
        String[] dismissTexts = { "Skip", "SKIP", "Done", "Submit" };
        for (int round = 0; round < maxRounds; round++) {
            boolean dismissedSomething = false;
            for (String t : dismissTexts) {
                if (clickRawByExactText(t)) {
                    dismissedSomething = true;
                    sleepQuiet(500);
                }
            }
            if (!dismissedSomething) {
                break; // nothing left to dismiss this round
            }
        }
    }

    /**
     * Raw exact-text tap helper. Returns false instantly if no visible element
     * with that exact text is present - no implicit-wait timeout is incurred.
     */
    private boolean clickRawByExactText(String text) {
        try {
            java.util.List<WebElement> found = driver.findElements(
                    AppiumBy.androidUIAutomator(
                            "new UiSelector().text(\"" + text + "\")"));
            if (!found.isEmpty() && found.get(0).isDisplayed()) {
                found.get(0).click();
                log("[COACHMARK] Dismissed overlay via text: \"" + text + "\"");
                return true;
            }
        } catch (Exception ignore) {
            // overlay not present / already gone - nothing to do
        }
        return false;
    }

    // ================================================================
    // ==========    DOG PROFILE SWITCH (business -> dog)        ======
    // ================================================================
    // The 4 methods below implement the BUSINESS-TO-DOG reverse path.
    // Used by the business-marketplace cleanup to ensure the test
    // user ends up back on the dog entity. Locators verified via
    // Appium Inspector.
    //
    // NOTE: deliberately granular (4 separate methods) so each can
    // be a distinct test step in the dev-scaffold test class. Once
    // proven, a single umbrella SwitchToFirstDogProfile() method can
    // compose them for use inside cleanup.
    // ================================================================

    /**
     * STEP 1 of business-to-dog switch - tap the Profile tab by its
     * 2nd TextView descendant under profile-view. Different locator
     * than PROFILE_TAB_XPATH (which descends to an ImageView).
     */
    public void ClickProfileTabByTextView() {
        log("===> ClickProfileTabByTextView (business-to-dog step 1)");
        String xpath =
            "//android.view.View[@content-desc=\"profile-view\"]"
            + "/android.widget.TextView[2]";
        WebElement el;
        try {
            el = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     Profile tab (TextView[2]) not clickable "
                    + "after 20s.");
            dumpVisibleText();
            throw e;
        }
        el.click();
        log("[OK]       Tapped Profile tab (TextView[2])");
        sleepQuiet(2000);
    }

    /**
     * STEP 2 of business-to-dog switch - tap the arrow-down chevron
     * on the Profile screen header to open the profile-switcher
     * picker. Reuses the same arrow_down_click content-desc the
     * business switch uses for its dropdown chevron.
     */
    public void ClickArrowDownForProfileSwitching() {
        log("===> ClickArrowDownForProfileSwitching (business-to-dog step 2)");
        String xpath =
            "//android.view.ViewGroup[@content-desc=\"arrow_down_click\"]"
            + "/android.widget.ImageView";
        WebElement el;
        try {
            el = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     arrow_down_click chevron not clickable "
                    + "after 20s.");
            dumpVisibleText();
            throw e;
        }
        el.click();
        log("[OK]       Tapped arrow_down_click chevron");
        sleepQuiet(2000);
    }

    /**
     * STEP 3 of business-to-dog switch - tap the dog profile tile
     * via its dogbus-action-dog content-desc. The trailing ImageView
     * is the visible tile icon inside the picker.
     */
    public void ClickDogProfileSwitcher() {
        log("===> ClickDogProfileSwitcher (business-to-dog step 3)");
        String xpath =
            "//android.view.ViewGroup[@content-desc=\"dogbus-action-dog\"]"
            + "/android.view.ViewGroup/android.widget.ImageView";
        WebElement el;
        try {
            el = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     dogbus-action-dog tile not clickable "
                    + "after 20s.");
            dumpVisibleText();
            throw e;
        }
        el.click();
        log("[OK]       Tapped dog profile tile (dogbus-action-dog)");
        sleepQuiet(2000);
    }

    /**
     * STEP 4 of business-to-dog switch - tap the SwitchProfile commit
     * button to confirm the dog selection and complete the switch.
     */
    public void ClickSelectProfileForDog() {
        log("===> ClickSelectProfileForDog (business-to-dog step 4)");
        String xpath =
            "//android.view.ViewGroup[@content-desc=\"dogbus-SwitchProfile\"]"
            + "/android.view.ViewGroup";
        WebElement el;
        try {
            el = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(xpath)));
        } catch (Exception e) {
            log("[FAIL]     dogbus-SwitchProfile commit button not "
                    + "clickable after 20s.");
            dumpVisibleText();
            throw e;
        }
        el.click();
        log("[OK]       Tapped SwitchProfile - dog selection committed");
        sleepQuiet(2500);
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