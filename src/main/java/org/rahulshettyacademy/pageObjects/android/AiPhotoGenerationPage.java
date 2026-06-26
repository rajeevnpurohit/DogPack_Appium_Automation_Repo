package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rahulshettyacademy.utils.AndroidActions;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

/**
 * AiPhotoGenerationPage - MINIMAL page object for the "AI photo
 * generations" feature. Uses USER-VERIFIED XPATHS extracted via Appium
 * Inspector.
 *
 * Flow covered (Test Case 1 - Monthly subscription):
 *   Settings and activity
 *     -> tap "AI photo generations"
 *     -> tap "Subscribe" tab
 *     -> tap "Monthly" plan card
 *     -> tap "Subscribe Monthly for <RUPEE>420.00"
 *     -> tap "Got it" (error dialog dismissal)
 *     -> tap close (X)
 *
 * IMPORTANT - Unicode handling for the Indian Rupee Sign (U+20B9):
 *   This source file contains NO literal Indian Rupee characters
 *   anywhere - not in code, not in comments. The character is built
 *   at runtime from its numeric code point via:
 *     RUPEE = String.valueOf((char) 0x20B9)
 *   This is the same defensive pattern that made the Boost feature
 *   tests pass on Windows toolchains.
 */
public class AiPhotoGenerationPage extends AndroidActions {

    /**
     * Account-type variants for the AI Photo Generation flow.
     *   DOG      -> Monthly Subscribe button = "Subscribe Monthly for <RUPEE>420.00"
     *   BUSINESS -> Monthly Subscribe button = "Subscribe Monthly for <RUPEE>430.00"
     *
     * All other prices (Annual <RUPEE>4,800, 10 Pack <RUPEE>110, 50 Pack <RUPEE>420,
     * 200 Pack <RUPEE>1,600, 1000 Pack <RUPEE>5,900) are IDENTICAL between dog
     * and business per the user's verified Inspector extraction - only
     * the Monthly Subscribe price differs.
     */
    public enum AccountType { DOG, BUSINESS }

    private final AndroidDriver driver;
    private final WebDriverWait wait;
    private final AccountType accountType;

    /**
     * Default constructor - account type defaults to DOG. Preserves the
     * existing Dogpack_AiPhotoGeneration test class behavior with zero
     * code change there.
     */
    public AiPhotoGenerationPage(AndroidDriver driver) {
        this(driver, AccountType.DOG);
    }

    /**
     * Parameterized constructor for account-type-aware tests.
     * Used by Dogpack_AiPhotoGeneration_Business with AccountType.BUSINESS.
     */
    public AiPhotoGenerationPage(AndroidDriver driver, AccountType accountType) {
        super(driver);
        this.driver = driver;
        this.accountType = accountType;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // ================================================================
    // ==========          VERIFIED XPATHS                       ======
    // ================================================================
    // Extracted from Appium Inspector on the actual test device.

    /**
     * Indian Rupee Sign (U+20B9). Built from numeric code point at
     * runtime so no rupee character appears in this source file.
     */
    private static final String RUPEE = String.valueOf((char) 0x20B9);

    /** Settings menu row for "AI photo generations" (positional XPath). */
    private static final String AI_PHOTO_GEN_ROW_XPATH =
        "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]"
        + "/android.widget.FrameLayout/android.view.ViewGroup"
        + "/android.view.ViewGroup/android.view.ViewGroup"
        + "/android.view.ViewGroup/android.widget.ScrollView"
        + "/android.view.ViewGroup/android.view.ViewGroup"
        + "/android.view.ViewGroup[1]/android.widget.FrameLayout"
        + "/android.view.ViewGroup/android.view.ViewGroup"
        + "/android.view.ViewGroup/android.widget.ScrollView"
        + "/android.view.ViewGroup/android.view.ViewGroup"
        + "/android.widget.ScrollView/android.view.ViewGroup"
        + "/android.view.ViewGroup[3]/android.view.ViewGroup"
        + "/android.view.View";

    /**
     * "Subscribe" tab on the AI screen (positional - on the AI screen,
     * not on Settings).
     */
    private static final String SUBSCRIBE_TAB_XPATH =
        "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]"
        + "/android.widget.FrameLayout/android.view.ViewGroup"
        + "/android.view.ViewGroup/android.view.ViewGroup"
        + "/android.view.ViewGroup/android.widget.ScrollView"
        + "/android.view.ViewGroup/android.view.ViewGroup"
        + "/android.view.ViewGroup[1]/android.view.ViewGroup[2]";

    /**
     * "Monthly" plan card. Anchored on the localized content-desc that
     * Android composes from the card's accessibility labels. The
     * content-desc embeds the price, so this differs by account type:
     *   DOG      -> "...Cancel anytime., <RUPEE>420.00"
     *   BUSINESS -> "...Cancel anytime., <RUPEE>430.00"
     *
     * Resolved at call time via getMonthlyCardXpath() helper, mirroring
     * the same pattern used for Subscribe Monthly button XPath.
     */
    private String getMonthlyCardXpath() {
        String price = (accountType == AccountType.BUSINESS) ? "430.00" : "420.00";
        return "//android.view.ViewGroup[@content-desc=\"Monthly, 50 AI generations "
                + "per month with access to all premium filters. Cancel anytime., "
                + RUPEE + price + "\"]";
    }

    /**
     * "Subscribe Monthly for <RUPEE>X.00" button label.
     *   DOG      -> 420.00
     *   BUSINESS -> 430.00
     * Only this AI price differs between account types - everything else
     * (Annual + all four Pack prices) is identical and stays as static
     * final constants below.
     */
    private String getSubscribeMonthlyBtnXpath() {
        String price = (accountType == AccountType.BUSINESS) ? "430.00" : "420.00";
        return "//android.widget.TextView[@text=\"Subscribe Monthly for "
                + RUPEE + price + "\"]";
    }

    /** Error dialog "Got it" button (same as Boost flow). */
    private static final String GOT_IT_BTN_XPATH =
        "//android.widget.Button[@text=\"Got it\"]";

    /** Wrapper close (X) button (same as Boost flow). */
    private static final String CLOSE_BTN_XPATH =
        "//android.widget.Button[@content-desc=\"Close\"]";

    /** Settings screen header - used to verify return after close. */
    private static final String SETTINGS_HEADER_XPATH =
        "//android.widget.TextView[@text=\"Settings and activity\"]";

    // ---------- Annual-plan flow (Test Case 2) ----------------------

    /**
     * "Annual" plan card label. Tapping the TextView dispatches the
     * click up to its parent clickable card (same behavior as the
     * "Annual Plan" card in the Boost flow).
     */
    private static final String ANNUAL_CARD_XPATH =
        "//android.widget.TextView[@text=\"Annual\"]";

    /**
     * "Subscribe Annual for <RUPEE>4,800.00" button label. Note the
     * COMMA between 4 and 800 - the actual rendered price uses Indian
     * digit grouping (4,800.00) not unformatted (4800.00). Built via
     * concatenation with the runtime RUPEE so the source file stays
     * pure ASCII.
     */
    private static final String SUBSCRIBE_ANNUAL_BTN_XPATH =
        "//android.widget.TextView[@text=\"Subscribe Annual for "
        + RUPEE + "4,800.00\"]";

    // ---------- Purchase flow (Test Case 3) -------------------------

    /**
     * "Purchase" tab on the AI screen. Unlike the Subscribe tab which
     * is anchored on a deep positional XPath, the Purchase tab exposes
     * a clean content-desc, so we use that.
     */
    private static final String PURCHASE_TAB_XPATH =
        "//android.view.ViewGroup[@content-desc=\"Purchase\"]";

    /**
     * "10 Pack" plan card label. Tapping the TextView dispatches the
     * click up to the parent clickable card.
     */
    private static final String TEN_PACK_CARD_XPATH =
        "//android.widget.TextView[@text=\"10 Pack\"]";

    /**
     * "Purchase 10 Pack for <RUPEE>110.00" button label. No digit
     * grouping at this magnitude (only 3 digits before the decimal).
     */
    private static final String PURCHASE_10_PACK_BTN_XPATH =
        "//android.widget.TextView[@text=\"Purchase 10 Pack for "
        + RUPEE + "110.00\"]";

    // ---------- Purchase 50 Pack flow (Test Case 4) -----------------

    /**
     * "50 Pack" plan card label. Tapping the TextView dispatches the
     * click up to the parent clickable card (same as 10 Pack).
     */
    private static final String FIFTY_PACK_CARD_XPATH =
        "//android.widget.TextView[@text=\"50 Pack\"]";

    /**
     * "Purchase 50 Pack for <RUPEE>420.00" button label. Same RUPEE
     * value (420.00) as the Monthly subscription button - they are
     * disambiguated by the "Purchase 50 Pack" vs "Subscribe Monthly"
     * prefix in the text attribute.
     */
    private static final String PURCHASE_50_PACK_BTN_XPATH =
        "//android.widget.TextView[@text=\"Purchase 50 Pack for "
        + RUPEE + "420.00\"]";

    // ---------- Purchase 200 Pack flow (Test Case 5) ----------------

    /**
     * "200 Pack" plan card label. Tapping the TextView dispatches the
     * click up to the parent clickable card (same as other packs).
     */
    private static final String TWO_HUNDRED_PACK_CARD_XPATH =
        "//android.widget.TextView[@text=\"200 Pack\"]";

    /**
     * "Purchase 200 Pack for <RUPEE>1,600.00" button label. Note the
     * comma between 1 and 600 - Indian digit grouping is used at this
     * magnitude.
     */
    private static final String PURCHASE_200_PACK_BTN_XPATH =
        "//android.widget.TextView[@text=\"Purchase 200 Pack for "
        + RUPEE + "1,600.00\"]";

    // ---------- Purchase 1000 Pack flow (Test Case 6) ---------------

    /**
     * "1000 Pack" plan card label. Tapping the TextView dispatches the
     * click up to the parent clickable card (same as other packs).
     */
    private static final String THOUSAND_PACK_CARD_XPATH =
        "//android.widget.TextView[@text=\"1000 Pack\"]";

    /**
     * "Purchase 1000 Pack for <RUPEE>5,900.00" button label. Comma
     * between 5 and 900 (Indian digit grouping).
     */
    private static final String PURCHASE_1000_PACK_BTN_XPATH =
        "//android.widget.TextView[@text=\"Purchase 1000 Pack for "
        + RUPEE + "5,900.00\"]";

    // ================================================================
    // ==========           PUBLIC METHODS                       ======
    // ================================================================

    /**
     * #1 - Tap "AI photo generations" in Settings. Verifies the AI
     *      screen opened by waiting for the Subscribe tab to appear.
     */
    public void NavigateToAIPhotoGenFromMenu() {
        log("===> NavigateToAIPhotoGenFromMenu");
        log("[ENV]      RUPEE char code = U+"
                + String.format("%04X", (int) RUPEE.charAt(0))
                + " (" + RUPEE + ")");

        log("[STEP 1/2] Tap 'AI photo generations' row");
        WebElement row = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(AI_PHOTO_GEN_ROW_XPATH)));
        row.click();
        log("[OK]       Tapped AI photo generations row");

        log("[STEP 2/2] Wait for Subscribe tab to appear (40s)");
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(40));
        try {
            longWait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath(SUBSCRIBE_TAB_XPATH)));
            log("[PASS]     AI screen opened (Subscribe tab clickable)");
        } catch (Exception e) {
            log("[FAIL]     Subscribe tab not clickable after 40s.");
            dumpVisibleText();
            throw e;
        }
    }

    /**
     * #2 - Tap the "Subscribe" tab on the AI screen. By default the
     *      "Purchase" tab is selected (because the screen prefers the
     *      one-time-purchase plans when both are available); tapping
     *      Subscribe switches to the recurring-subscription cards.
     */
    public void ClickSubscribeTab() {
        log("===> ClickSubscribeTab");

        log("[STEP 1/2] Tap 'Subscribe' tab");
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(SUBSCRIBE_TAB_XPATH)));
        tab.click();
        log("[OK]       Tapped Subscribe tab");

        log("[STEP 2/2] Wait for Subscribe tab to be active (40s)");
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(40));
        try {
            longWait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"Subscribe\"]")));
            log("[PASS]     Subscribe tab active ('Subscribe' visible)");
        } catch (Exception e) {
            log("[FAIL]     Subscribe tab not active after 40s.");
            dumpVisibleText();
            throw e;
        }
    }

    /**
     * #3 - Tap the "Monthly" plan card to select it. The selection is
     *      confirmed by waiting for the Subscribe button text to update.
     */
    public void SelectMonthlyAIPlanCard() {
        log("===> SelectMonthlyAIPlanCard");

        log("[STEP 1/2] Tap 'Monthly' plan card");
        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath("//android.widget.TextView[@text=\"Monthly\"]")));
        card.click();
        log("[OK]       Tapped Monthly card");

        log("[STEP 2/2] Wait for 'Subscribe Monthly for <RUPEE>420.00' "
                + "button (40s)");
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(40));
        try {
            longWait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.xpath(getSubscribeMonthlyBtnXpath())));
            log("[PASS]     Monthly selected (Subscribe button reflects price)");
        } catch (Exception e) {
            log("[FAIL]     Subscribe Monthly button not visible after 40s.");
            dumpVisibleText();
            throw e;
        }
    }

    /**
     * #4 - Tap "Subscribe Monthly for <RUPEE>420.00" -> dismiss the
     *      "Got it" error dialog that follows (same as Boost flow).
     */
    public void ClickSubscribeMonthlyAI() {
        log("===> ClickSubscribeMonthlyAI");

        log("[STEP 1/3] Tap Subscribe button");
        WebElement subBtn = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(getSubscribeMonthlyBtnXpath())));
        subBtn.click();
        log("[OK]       Tapped Subscribe button");

        log("[STEP 2/3] Wait for 'Got it' error dialog (up to 15s)");
        WebElement gotItBtn = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.elementToBeClickable(
                        AppiumBy.xpath(GOT_IT_BTN_XPATH)));
        log("[OK]       'Got it' dialog appeared");

        log("[STEP 3/3] Tap 'Got it' to dismiss");
        gotItBtn.click();
        try { Thread.sleep(1500); } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
        log("[PASS]     Subscribe -> Got it complete");
    }

    /**
     * #5 - Tap the close (X) button. Verifies return to Settings.
     */
    public void CloseAIPhotoGen() {
        log("===> CloseAIPhotoGen");

        // Settle first - after the previous step's "Got it" dismissal,
        // a "Payment could not be processed" toast appears for ~3s.
        // If the close button is tapped while the toast is still on
        // screen, Android sometimes dispatches the click to the toast
        // layer instead of the underlying button - click "succeeds" but
        // no navigation happens. A 2s wait lets the toast clear so the
        // click reliably reaches the close button.
        sleepQuiet(2000);

        log("[STEP 1/2] Tap close (X) button");
        WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(CLOSE_BTN_XPATH)));
        closeBtn.click();
        log("[OK]       Tapped Close");

        log("[STEP 2/2] Verify return to Settings (25s)");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(25))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            AppiumBy.xpath(SETTINGS_HEADER_XPATH)));
            log("[PASS]     Back on Settings and activity screen");
        } catch (Exception e) {
            log("[FAIL]     Settings header not visible after close.");
            dumpVisibleText();
            throw e;
        }
    }

    /**
     * Quiet sleep helper - swallow InterruptedException so the test
     * method does not need to declare it. Used for animation/toast
     * settling delays.
     */
    private void sleepQuiet(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }

    // ---------- Annual-plan flow (Test Case 2) ----------------------

    /**
     * #6 - Tap the "Annual" plan card to select it. Selection is
     *      confirmed by waiting for the "Subscribe Annual for
     *      <RUPEE>4,800.00" button text to appear.
     */
    public void SelectAnnualAIPlanCard() {
        log("===> SelectAnnualAIPlanCard");

        log("[STEP 1/2] Tap 'Annual' plan card");
        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(ANNUAL_CARD_XPATH)));
        card.click();
        log("[OK]       Tapped Annual card");

        log("[STEP 2/2] Wait for 'Subscribe Annual for <RUPEE>4,800.00' "
                + "button (40s)");
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(40));
        try {
            longWait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.xpath(SUBSCRIBE_ANNUAL_BTN_XPATH)));
            log("[PASS]     Annual selected (Subscribe button reflects "
                    + "annual price)");
        } catch (Exception e) {
            log("[FAIL]     Subscribe Annual button not visible after 40s.");
            dumpVisibleText();
            throw e;
        }
    }

    /**
     * #7 - Tap "Subscribe Annual for <RUPEE>4,800.00" -> dismiss the
     *      "Got it" error dialog that follows (same dialog as the
     *      Monthly flow).
     */
    public void ClickSubscribeAnnualAI() {
        log("===> ClickSubscribeAnnualAI");

        log("[STEP 1/3] Tap Subscribe Annual button");
        WebElement subBtn = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(SUBSCRIBE_ANNUAL_BTN_XPATH)));
        subBtn.click();
        log("[OK]       Tapped Subscribe Annual button");

        log("[STEP 2/3] Wait for 'Got it' error dialog (up to 15s)");
        WebElement gotItBtn = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.elementToBeClickable(
                        AppiumBy.xpath(GOT_IT_BTN_XPATH)));
        log("[OK]       'Got it' dialog appeared");

        log("[STEP 3/3] Tap 'Got it' to dismiss");
        gotItBtn.click();
        try { Thread.sleep(1500); } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
        log("[PASS]     Annual Subscribe -> Got it complete");
    }

    // ---------- Purchase flow (Test Case 3) -------------------------

    /**
     * #8 - Tap the "Purchase" tab on the AI screen. This switches from
     *      the Subscribe tab (recurring plans) to the Purchase tab
     *      (one-time credit packs).
     */
    public void ClickPurchaseTab() {
        log("===> ClickPurchaseTab");

        By purchaseTab = AppiumBy.xpath(PURCHASE_TAB_XPATH);
        By tenPack = AppiumBy.xpath(TEN_PACK_CARD_XPATH);
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // The paywall opens on the Subscribe tab; the first Purchase tap can be
        // swallowed if it lands before the sheet settles, leaving the tab unswitched.
        // Tap, verify the switch (10 Pack card), and re-tap if it did not take.
        for (int attempt = 1; attempt <= 3; attempt++) {
            log("[STEP] Tap 'Purchase' tab (attempt " + attempt + ")");
            try {
                wait.until(ExpectedConditions.elementToBeClickable(purchaseTab)).click();
                log("[OK]       Tapped Purchase tab");
            } catch (Exception e) {
                log("[WARN] Purchase tab not clickable on attempt " + attempt);
            }
            try {
                shortWait.until(ExpectedConditions.visibilityOfElementLocated(tenPack));
                log("[PASS]     Purchase tab active (10 Pack card visible)");
                return;
            } catch (Exception e) {
                log("[WARN] 10 Pack not visible after tap attempt " + attempt
                        + " - tab may not have switched, retrying");
            }
        }

        log("[STEP] Final wait for '10 Pack' card (40s)");
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(40));
        try {
            longWait.until(ExpectedConditions.visibilityOfElementLocated(tenPack));
            log("[PASS]     Purchase tab active (10 Pack card visible)");
        } catch (Exception e) {
            log("[FAIL]     10 Pack card not visible after retries.");
            dumpVisibleText();
            throw e;
        }
    }

    /**
     * #9 - Tap the "10 Pack" plan card to select it. Selection is
     *      confirmed by waiting for the "Purchase 10 Pack for
     *      <RUPEE>110.00" button text to appear.
     */
    public void SelectTenPackCard() {
        log("===> SelectTenPackCard");

        log("[STEP 1/2] Tap '10 Pack' card");
        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(TEN_PACK_CARD_XPATH)));
        card.click();
        log("[OK]       Tapped 10 Pack card");

        log("[STEP 2/2] Wait for 'Purchase 10 Pack for <RUPEE>110.00' "
                + "button (40s)");
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(40));
        try {
            longWait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.xpath(PURCHASE_10_PACK_BTN_XPATH)));
            log("[PASS]     10 Pack selected (Purchase button reflects price)");
        } catch (Exception e) {
            log("[FAIL]     Purchase 10 Pack button not visible after 40s.");
            dumpVisibleText();
            throw e;
        }
    }

    /**
     * #10 - Tap "Purchase 10 Pack for <RUPEE>110.00" -> dismiss the
     *       "Got it" error dialog that follows (same dialog as the
     *       subscription flows).
     */
    public void ClickPurchase10Pack() {
        log("===> ClickPurchase10Pack");

        log("[STEP 1/3] Tap Purchase 10 Pack button");
        WebElement purBtn = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(PURCHASE_10_PACK_BTN_XPATH)));
        purBtn.click();
        log("[OK]       Tapped Purchase 10 Pack button");

        log("[STEP 2/3] Wait for 'Got it' error dialog (up to 15s)");
        WebElement gotItBtn = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.elementToBeClickable(
                        AppiumBy.xpath(GOT_IT_BTN_XPATH)));
        log("[OK]       'Got it' dialog appeared");

        log("[STEP 3/3] Tap 'Got it' to dismiss");
        gotItBtn.click();
        try { Thread.sleep(1500); } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
        log("[PASS]     Purchase 10 Pack -> Got it complete");
    }

    // ---------- Purchase 50 Pack flow (Test Case 4) -----------------

    /**
     * #11 - Tap the "50 Pack" plan card to select it. Selection is
     *       confirmed by waiting for the "Purchase 50 Pack for
     *       <RUPEE>420.00" button text to appear.
     */
    public void SelectFiftyPackCard() {
        log("===> SelectFiftyPackCard");

        log("[STEP 1/2] Tap '50 Pack' card");
        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(FIFTY_PACK_CARD_XPATH)));
        card.click();
        log("[OK]       Tapped 50 Pack card");

        log("[STEP 2/2] Wait for 'Purchase 50 Pack for <RUPEE>420.00' "
                + "button (40s)");
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(40));
        try {
            longWait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.xpath(PURCHASE_50_PACK_BTN_XPATH)));
            log("[PASS]     50 Pack selected (Purchase button reflects price)");
        } catch (Exception e) {
            log("[FAIL]     Purchase 50 Pack button not visible after 40s.");
            dumpVisibleText();
            throw e;
        }
    }

    /**
     * #12 - Tap "Purchase 50 Pack for <RUPEE>420.00" -> dismiss the
     *       "Got it" error dialog (same dialog as the other flows).
     */
    public void ClickPurchase50Pack() {
        log("===> ClickPurchase50Pack");

        log("[STEP 1/3] Tap Purchase 50 Pack button");
        WebElement purBtn = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(PURCHASE_50_PACK_BTN_XPATH)));
        purBtn.click();
        log("[OK]       Tapped Purchase 50 Pack button");

        log("[STEP 2/3] Wait for 'Got it' error dialog (up to 15s)");
        WebElement gotItBtn = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.elementToBeClickable(
                        AppiumBy.xpath(GOT_IT_BTN_XPATH)));
        log("[OK]       'Got it' dialog appeared");

        log("[STEP 3/3] Tap 'Got it' to dismiss");
        gotItBtn.click();
        try { Thread.sleep(1500); } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
        log("[PASS]     Purchase 50 Pack -> Got it complete");
    }

    // ---------- Purchase 200 Pack flow (Test Case 5) ----------------

    /**
     * #13 - Tap the "200 Pack" plan card to select it. Selection is
     *       confirmed by waiting for the "Purchase 200 Pack for
     *       <RUPEE>1,600.00" button text to appear.
     */
    public void SelectTwoHundredPackCard() {
        log("===> SelectTwoHundredPackCard");

        log("[STEP 1/2] Tap '200 Pack' card");
        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(TWO_HUNDRED_PACK_CARD_XPATH)));
        card.click();
        log("[OK]       Tapped 200 Pack card");

        log("[STEP 2/2] Wait for 'Purchase 200 Pack for <RUPEE>1,600.00' "
                + "button (40s)");
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(40));
        try {
            longWait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.xpath(PURCHASE_200_PACK_BTN_XPATH)));
            log("[PASS]     200 Pack selected (Purchase button reflects price)");
        } catch (Exception e) {
            log("[FAIL]     Purchase 200 Pack button not visible after 40s.");
            dumpVisibleText();
            throw e;
        }
    }

    /**
     * #14 - Tap "Purchase 200 Pack for <RUPEE>1,600.00" -> dismiss the
     *       "Got it" error dialog (same dialog as other flows).
     */
    public void ClickPurchase200Pack() {
        log("===> ClickPurchase200Pack");

        log("[STEP 1/3] Tap Purchase 200 Pack button");
        WebElement purBtn = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(PURCHASE_200_PACK_BTN_XPATH)));
        purBtn.click();
        log("[OK]       Tapped Purchase 200 Pack button");

        log("[STEP 2/3] Wait for 'Got it' error dialog (up to 15s)");
        WebElement gotItBtn = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.elementToBeClickable(
                        AppiumBy.xpath(GOT_IT_BTN_XPATH)));
        log("[OK]       'Got it' dialog appeared");

        log("[STEP 3/3] Tap 'Got it' to dismiss");
        gotItBtn.click();
        try { Thread.sleep(1500); } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
        log("[PASS]     Purchase 200 Pack -> Got it complete");
    }

    // ---------- Purchase 1000 Pack flow (Test Case 6) ---------------

    /**
     * #15 - Tap the "1000 Pack" plan card to select it. Selection is
     *       confirmed by waiting for the "Purchase 1000 Pack for
     *       <RUPEE>5,900.00" button text to appear.
     *
     * Why this method scrolls before clicking (unlike 10/50/200 Pack):
     *   The card list is lazily mounted. 10/50/200 Pack are above the
     *   fold and rendered immediately when the Purchase tab opens, but
     *   1000 Pack lives below the fold and is NOT in the accessibility
     *   tree until the list scrolls. Any wait.until() will time out
     *   because the node literally does not exist yet. We use
     *   UiAutomator's UiScrollable.scrollIntoView() to scroll the
     *   container until "1000 Pack" mounts, then proceed with the
     *   normal click + verify sequence.
     */
    public void SelectThousandPackCard() {
        log("===> SelectThousandPackCard");

        log("[STEP 1/3] Scroll '1000 Pack' card into view (lazy-mounted "
                + "below the fold)");
        try {
            driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))"
                + ".scrollIntoView(new UiSelector().text(\"1000 Pack\"))"));
            log("[OK]       Scrolled to 1000 Pack");
        } catch (Exception e) {
            log("[WARN]     UiScrollable failed - " + e.getMessage().split("\n")[0]
                    + ". Continuing - the wait below may still succeed if the "
                    + "card became mounted via the partial scroll.");
        }

        log("[STEP 2/3] Tap '1000 Pack' card");
        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(THOUSAND_PACK_CARD_XPATH)));
        card.click();
        log("[OK]       Tapped 1000 Pack card");

        log("[STEP 3/3] Wait for 'Purchase 1000 Pack for <RUPEE>5,900.00' "
                + "button (40s)");
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(40));
        try {
            longWait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.xpath(PURCHASE_1000_PACK_BTN_XPATH)));
            log("[PASS]     1000 Pack selected (Purchase button reflects price)");
        } catch (Exception e) {
            log("[FAIL]     Purchase 1000 Pack button not visible after 40s.");
            dumpVisibleText();
            throw e;
        }
    }

    /**
     * #16 - Tap "Purchase 1000 Pack for <RUPEE>5,900.00" -> dismiss the
     *       "Got it" error dialog.
     */
    public void ClickPurchase1000Pack() {
        log("===> ClickPurchase1000Pack");

        log("[STEP 1/3] Tap Purchase 1000 Pack button");
        WebElement purBtn = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(PURCHASE_1000_PACK_BTN_XPATH)));
        purBtn.click();
        log("[OK]       Tapped Purchase 1000 Pack button");

        log("[STEP 2/3] Wait for 'Got it' error dialog (up to 15s)");
        WebElement gotItBtn = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.elementToBeClickable(
                        AppiumBy.xpath(GOT_IT_BTN_XPATH)));
        log("[OK]       'Got it' dialog appeared");

        log("[STEP 3/3] Tap 'Got it' to dismiss");
        gotItBtn.click();
        try { Thread.sleep(1500); } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
        log("[PASS]     Purchase 1000 Pack -> Got it complete");
    }

    // ================================================================
    // ==========           PRIVATE HELPERS                      ======
    // ================================================================

    private void log(String msg) {
        System.out.println(msg);
    }

    /**
     * Print currently-visible TextViews and Buttons (with content-desc)
     * to stdout. Called only on failure - lets you see exactly what is
     * on screen at the moment the assertion failed. Same helper as
     * BoostAccountPage uses; duplicated here to keep this page object
     * self-contained.
     */
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
        } catch (Exception e) {
            log("           dumpVisibleText TextView error: "
                    + e.getMessage().split("\n")[0]);
        }
        try {
            List<WebElement> buttons = driver.findElements(
                    AppiumBy.xpath("//android.widget.Button"));
            log("           ---- visible Buttons (content-desc) ----");
            int n = 0;
            for (WebElement b : buttons) {
                try {
                    if (!b.isDisplayed()) continue;
                    String desc = b.getAttribute("content-desc");
                    String text = b.getAttribute("text");
                    if ((desc == null || desc.isEmpty())
                            && (text == null || text.isEmpty())) continue;
                    log("           [BTN] desc=\""
                            + (desc == null ? "" : desc) + "\" text=\""
                            + (text == null ? "" : text) + "\"");
                    if (++n >= 15) { log("           ...(truncated)"); break; }
                } catch (Exception ignore) { /* */ }
            }
        } catch (Exception e) {
            log("           dumpVisibleText Button error: "
                    + e.getMessage().split("\n")[0]);
        }
    }
}