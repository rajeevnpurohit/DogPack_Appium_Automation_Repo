package org.rahulshettyacademy.pageObjects.android;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rahulshettyacademy.utils.AndroidActions;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

/**
 * BoostAccountPage - MINIMAL page object for the "Boost Your Account"
 * feature. Uses USER-VERIFIED XPATHS extracted via Appium Inspector.
 *
 * Flow covered:
 *   Settings & activity
 *     -> tap "Boost your account"
 *     -> tap "Subscribe Monthly Plan for <RUPEE>6.00"
 *     -> tap "Got it" (error dialog dismissal)
 *     -> tap close (X)
 *
 * IMPORTANT - Unicode handling for the Indian Rupee Sign (U+20B9):
 *   This source file contains NO literal Indian Rupee characters
 *   anywhere - not in code, not in comments. The character is built
 *   at runtime from its numeric code point via:
 *     RUPEE = String.valueOf((char) 0x20B9)
 *   This is the most defensive approach against Windows toolchain
 *   encoding bugs that have been observed to mangle even \u20B9
 *   escape sequences to '?' under some conditions.
 */
public class BoostAccountPage extends AndroidActions {

    /**
     * Account-type variants for the Boost flow. The Subscribe button
     * text differs between dog and business accounts (different prices);
     * everything else on the screen is identical.
     *   DOG      -> "Subscribe Monthly Plan for <RUPEE>6.00"
     *   BUSINESS -> "Subscribe Monthly Plan for <RUPEE>520.00"
     */
    public enum AccountType { DOG, BUSINESS }

    private final AndroidDriver driver;
    private final WebDriverWait wait;
    private final AccountType accountType;

    /**
     * Default constructor - account type defaults to DOG. This keeps
     * the existing Dogpack_BoostAccount test class compatible without
     * any changes (the existing 9 green tests still call new BoostAccountPage(driver)).
     */
    public BoostAccountPage(AndroidDriver driver) {
        this(driver, AccountType.DOG);
    }

    /**
     * Parameterized constructor for account-type-aware tests.
     * Used by Dogpack_BoostAccount_Business with AccountType.BUSINESS.
     */
    public BoostAccountPage(AndroidDriver driver, AccountType accountType) {
        super(driver);
        this.driver = driver;
        this.accountType = accountType;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // ================================================================
    // ==========          VERIFIED XPATHS                       ======
    // ================================================================
    // Extracted from your Appium Inspector session on the actual test
    // device. Do NOT replace these unless you re-extract via Inspector
    // after a UI change.

    /** Settings menu row for "Boost your account" (positional XPath). */
    private static final String BOOST_ROW_XPATH =
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
        + "/android.view.ViewGroup[2]";

    /**
     * Indian Rupee Sign (<RUPEE>). Constructed from numeric code point at runtime
     * - no rupee character appears in this source file. This is the most
     * defensive approach against encoding mangling (some Windows toolchains
     * have been observed to convert literal <RUPEE> to '?' even when \u20B9
     * escapes appear in the source). Building it from (char) 0x20B9
     * eliminates any source-file or compile-time encoding interaction.
     */
    private static final String RUPEE = String.valueOf((char) 0x20B9);

    /**
     * Subscribe button text - varies by account type:
     *   DOG      -> "Subscribe Monthly Plan for <RUPEE>6.00"
     *   BUSINESS -> "Subscribe Monthly Plan for <RUPEE>520.00"
     *
     * Resolved dynamically based on the AccountType passed to the
     * constructor (defaults to DOG). Use the helper getSubscribeBtnXpath()
     * inside method bodies instead of referencing a constant.
     */
    private String getSubscribeBtnXpath() {
        String price = (accountType == AccountType.BUSINESS) ? "520.00" : "6.00";
        return "//android.widget.TextView[@text=\"Subscribe Monthly Plan for "
                + RUPEE + price + "\"]";
    }

    /** Error dialog "Got it" button. */
    private static final String GOT_IT_BTN_XPATH =
        "//android.widget.Button[@text=\"Got it\"]";

    /**
     * Wrapper close (X) button. Descends into the inner ImageView -
     * Android dispatches the tap up to the clickable parent Button, so
     * behavior is identical to targeting the Button directly, but this
     * variant has been found necessary for some screens where the
     * parent-Button matcher resolves to an off-screen ancestor.
     */
    private static final String CLOSE_BTN_XPATH =
        "//android.widget.Button[@content-desc=\"Close\"]"
        + "/android.widget.ImageView";

    /** Settings screen header - used to verify return after close. */
    private static final String SETTINGS_HEADER_XPATH =
        "//android.widget.TextView[@text=\"Settings and activity\"]";

    // ---------- Annual-plan flow (Test Case 2) ----------------------

    /** "Annual Plan" card label. Tapping the TextView forwards to the
     *  parent clickable card. */
    private static final String ANNUAL_PLAN_CARD_XPATH =
        "//android.widget.TextView[@text=\"Annual Plan\"]";

    /**
     * Annual Subscribe button - varies by account type:
     *   DOG      -> "Subscribe Annual Plan for <RUPEE>15.00"
     *   BUSINESS -> "Subscribe Annual Plan for <RUPEE>5,300.00"
     *
     * NOTE 1: Element type is TextView (matching the Monthly button on
     * the same screen), not Button as a previous version of this code
     * assumed. The user provided the TextView locator for business and
     * it has worked for dog Monthly all along; standardizing here.
     *
     * NOTE 2: Business price includes a comma between 5 and 300 (Indian
     * digit grouping kicks in at 4-digit-and-above figures).
     */
    private String getSubscribeAnnualBtnXpath() {
        String price = (accountType == AccountType.BUSINESS) ? "5,300.00" : "15.00";
        return "//android.widget.TextView[@text=\"Subscribe Annual Plan for "
                + RUPEE + price + "\"]";
    }

    // ================================================================
    // ==========           PUBLIC METHODS                       ======
    // ================================================================

    /**
     * #1 - Tap the "Boost Your Account" row in Settings.
     *      Verifies the SubscribeDog screen opened by waiting for the
     *      Subscribe button to appear.
     */
    public void NavigateToBoostAccountFromMenu() {
        log("===> NavigateToBoostAccountFromMenu");
        log("[ENV]      RUPEE char code = U+"
                + String.format("%04X", (int) RUPEE.charAt(0))
                + " (" + RUPEE + ")");
        log("[ENV]      Subscribe XPath = " + getSubscribeBtnXpath());

        log("[STEP 1/2] Tap 'Boost your account' row");
        WebElement row = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(BOOST_ROW_XPATH)));
        row.click();
        log("[OK]       Tapped Boost row");

        log("[STEP 2/2] Wait for Subscribe button to appear (40s)");
        WebDriverWait longerWait = new WebDriverWait(driver, Duration.ofSeconds(40));
        try {
            longerWait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.xpath(getSubscribeBtnXpath())));
            log("[PASS]     Boost screen opened (Subscribe button visible)");
        } catch (Exception e) {
            log("[FAIL]     Subscribe button not visible after 40s.");
            log("[FAIL]     Dumping currently visible text for diagnosis:");
            dumpVisibleText();
            throw e;
        }
    }

    /**
     * #2 - Tap "Subscribe Monthly Plan for \u20B96.00".
     *      Dismisses the "Got it" error dialog that follows.
     */
    public void ClickSubscribeMonthlyPlan() {
        log("===> ClickSubscribeMonthlyPlan");

        log("[STEP 1/3] Tap Subscribe button");
        WebElement subBtn = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(getSubscribeBtnXpath())));
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
     * #3 - Tap the close (X) button. Verifies return to Settings.
     */
    public void CloseBoostScreen() {
        log("===> CloseBoostScreen");

        // ---- STEP 1: Defensive 'Got it' dismissal ----
        // On the business flow, a second/lingering 'Got it' dialog has
        // been observed overlaying the screen when CloseBoostScreen is
        // called. If we tap Close while that dialog is showing, the tap
        // is intercepted by the dialog instead of reaching the Close
        // button - the test then fails with "Settings header not visible".
        // Short 3s wait: if no Got it is present, we move on quickly
        // (no latency cost for the dog flow where Got it isn't there).
        log("[STEP 1/3] Defensive 'Got it' dismissal (3s wait, optional)");
        try {
            WebElement gotItBtn = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.elementToBeClickable(
                            AppiumBy.xpath(GOT_IT_BTN_XPATH)));
            gotItBtn.click();
            log("[OK]       Got it dialog found and dismissed");
            try { Thread.sleep(1000); } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        } catch (Exception e) {
            log("[INFO]     No Got it dialog present - continuing to Close");
        }

        // ---- STEP 2: Tap Close ----
        log("[STEP 2/3] Tap close (X) button");
        WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(CLOSE_BTN_XPATH)));
        closeBtn.click();
        log("[OK]       Tapped Close");

        // ---- STEP 3: Verify return to Settings ----
        log("[STEP 3/3] Verify return to Settings");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(
                        AppiumBy.xpath(SETTINGS_HEADER_XPATH)));
        log("[PASS]     Back on Settings and activity screen");
    }

    // ---------- Annual-plan flow (Test Case 2) ----------------------

    /**
     * #4 - Tap the "Annual Plan" card to select it. The Monthly Plan is
     *      selected by default; this tap switches the selection so the
     *      Subscribe button text updates to "Subscribe Annual Plan for
     *      \u20B915.00".
     */
    public void SelectAnnualPlanCard() {
        log("===> SelectAnnualPlanCard");

        log("[STEP 1/2] Tap 'Annual Plan' card");
        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(ANNUAL_PLAN_CARD_XPATH)));
        card.click();
        log("[OK]       Tapped Annual Plan card");

        log("[STEP 2/2] Wait for 'Subscribe Annual Plan for \u20B915.00' "
                + "button to appear (selection confirmed by button text change)");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                AppiumBy.xpath(getSubscribeAnnualBtnXpath())));
        log("[PASS]     Annual plan selected (Subscribe button reflects "
                + "annual price)");
    }

    /**
     * #5 - Tap "Subscribe Annual Plan for \u20B915.00".
     *      Dismisses the "Got it" error dialog that follows
     *      (same dialog as the Monthly flow).
     */
    public void ClickSubscribeAnnualPlan() {
        log("===> ClickSubscribeAnnualPlan");

        log("[STEP 1/3] Tap Subscribe Annual button");
        WebElement subBtn = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath(getSubscribeAnnualBtnXpath())));
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

    private void log(String msg) {
        System.out.println(msg);
    }

    /**
     * Print currently-visible TextViews and Buttons (with content-desc)
     * to stdout. Called only on failure - lets you see exactly what is
     * on screen at the moment the assertion failed, without writing a
     * huge file dump. Each line shows what the XPath would match against.
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
