# Subscription Features Test Suites — Boost / AI Photo Gen / Subscription

This README documents the three test classes added for the subscription-related
features and the two TestNG suite files that let you run them in isolation
from the rest of the regression suite.

---

## What's been added

### Page objects (in `src/main/java/org/rahulshettyacademy/pageObjects/android/`)

| File | Feature | App screens covered |
|---|---|---|
| `BoostAccountPage.java` | Boost Your Account | `SubscribeDog.tsx` → `SubscribeDogRender.tsx` / `SubscribeBusinessRender.tsx` |
| `AiPhotoGenerationPage.java` | AI Photo Generations | `SubscribeDog.tsx` → `AiFilterPurchasesRender.tsx` |
| `SubscriptionPage.java` | Subscription overview | `Subscription.tsx` (Verification + AI image credits tabs, ActivePlanBanner, empty state) |

### Test classes (in `src/test/java/org/rahulshettyacademy/`)

| File | Tests | Smoke-tagged |
|---|---|---|
| `Dogpack_BoostAccount.java` | 10 (#1 login, #2 settings, #3–10 feature) | #1, #2, #3, #4, #10 |
| `Dogpack_AiPhotoGeneration.java` | 9 (#1 login, #2 settings, #3–9 feature) | #1, #2, #3, #4, #9 |
| `Dogpack_Subscription.java` | 11 (#1 login, #2 settings, #3–11 feature) | #1, #2, #3, #4, #7, #11 |

**Total: 30 test methods (16 also tagged Smoke).**

### Suite files (in `testNGSuites/`)

| File | Tests | Estimated runtime |
|---|---|---|
| `testng_Boost_AI_Subscription.xml` | All 30 | ~18–22 min |
| `testng_Boost_AI_Subscription_Smoke.xml` | 16 (Smoke group filter) | ~9–12 min |

---

## How to run

### Option A — direct (no pom changes)

```bash
# Full suite
mvn test -DsuiteXmlFile=testNGSuites/testng_Boost_AI_Subscription.xml

# Smoke-only suite
mvn test -DsuiteXmlFile=testNGSuites/testng_Boost_AI_Subscription_Smoke.xml
```

### Option B — via Maven profile (after pasting `pom_profiles_snippet.xml`)

Open `pom.xml`, find the existing `<profiles>` block (it already contains
`Login` / `Regression` / `Smoke`), and paste the two `<profile>` blocks
from `testNGSuites/pom_profiles_snippet.xml` into it. Then:

```bash
mvn test -P SubscriptionFeatures        # full 30-test suite
mvn test -P SubscriptionFeaturesSmoke   # smoke-only 16-test suite
```

### Option C — single test class via TestNG

```bash
mvn test -Dtest=Dogpack_BoostAccount
mvn test -Dtest=Dogpack_AiPhotoGeneration
mvn test -Dtest=Dogpack_Subscription
```

---

## Test user pre-requisites

These tests assume the test user defined in `LoginData.json[0]` is available:

```json
{ "email": "iamkiara02", "password": "Test@123" }
```

Different feature areas have different visibility gates baked into the app
itself — the tests will **skip with a clear `[SKIP]` log line** when the
gate is closed, rather than failing. To exercise everything, pick a
test user that matches the table below for each suite:

| Suite | Required user state | Why |
|---|---|---|
| `Dogpack_BoostAccount` | `is_verified != 1` AND `isArSupported == true` | Boost row hidden in `MenuScreen.js:113-117` when verified or non-AR device |
| `Dogpack_AiPhotoGeneration` | `isArSupported == true` (AR-capable device) | AI Photo Gen row hidden in `MenuScreen.js:107-111` on non-AR devices |
| `Dogpack_Subscription` | Any user | Run TWICE — once with an unsubscribed user (exercises empty state + View Plans), once with a monthly-subscribed user (exercises active banner + Change Plan) |

For business-account users, the business must be **admin-approved** —
unapproved business accounts hit the `underAproval` toast on row tap and
the navigation is short-circuited. The tests detect that toast and soft-pass.

---

## In-app purchase behavior

The IAP-engagement tests (`#7 ClickSubscribeBoostPlan`, `#8 ClickSubscribeOrPurchaseAI`)
**do NOT complete a real purchase**. They tap the Subscribe button and
assert one of:

1. The native Google Play purchase sheet opens (verified via
   `driver.getCurrentPackage()` containing `com.android.vending`)
2. A `ConfirmModal` ("Verification Required") appears post-tap
3. The "Payment could not be processed" toast appears (sandbox account
   without a payment method)

If your test device is configured with a **Google Play sandbox account**
and you want to drive the purchase to completion, extend the page object
methods to also tap through the Play sheet ("1-tap buy" or "Buy" button)
and handle the in-app `ConfirmModal` afterwards.

---

## Locator strategy notes

These pages have **no `testID`s** on key elements as of the current build,
so the locators rely on:

- **`@AndroidFindBy(accessibility = "<English label>")`** for menu rows —
  works because UiAutomator2 maps the child `<Text>` to the parent
  `TouchableOpacity`'s `content-desc` (documented in
  `SettingsAndActivityPage.java` lines 58–63).
- **XPath against visible text** (`//android.widget.TextView[@text="…"]`)
  for screen titles, plan card anchors, buttons, footer links.

**Both approaches assume English locale.** When the app source eventually
gets `testID` attributes on the subscription screens, swap the XPath
locators for `@AndroidFindBy(accessibility = "testid_value")` and the
tests will become locale-robust.

---

## What each test exercises (quick reference)

### `Dogpack_BoostAccount`

| # | Method | What it verifies |
|---|---|---|
| 1 | `PrerequestFunctionsforBoost` | Login + navigate to Profile |
| 2 | `NavigatesSettingActivityScreen` | Open Settings via hamburger |
| 3 | `NavigateToBoostAccountFromMenu` | Tap "Boost Your Account" row, screen opens |
| 4 | `VerifyBoostScreenElements` | Title, close, plan cards, button, Terms/Privacy all rendered |
| 5 | `SelectMonthlyBoostPlanFunctionality` | Tap monthly card, verify button text |
| 6 | `SelectAnnualBoostPlanFunctionality` | Tap annual card, verify button text |
| 7 | `ClickSubscribeBoostPlan` | Tap Subscribe, IAP flow engages |
| 8 | `NavigateToTermsFromBoost` | Tap T&C link, leaves screen |
| 9 | `NavigateToPrivacyFromBoost` | Tap Privacy link, leaves screen |
| 10 | `CloseBoostAccount` | Tap X, returns to Settings |

### `Dogpack_AiPhotoGeneration`

| # | Method | What it verifies |
|---|---|---|
| 1 | `PrerequestFunctionsforAIPhotoGen` | Login + navigate to Profile |
| 2 | `NavigatesSettingActivityScreen` | Open Settings via hamburger |
| 3 | `NavigateToAIPhotoGenFromMenu` | Tap "AI photo generations" row, screen opens |
| 4 | `VerifyAIScreenElements` | Title, close, tabs, plan card, footer rendered |
| 5 | `SwitchToSubscribeTabFunctionality` | Tap Subscribe tab, subscription cards visible |
| 6 | `SwitchToPurchaseTabFunctionality` | Tap Purchase tab, one-time cards visible |
| 7 | `SelectFirstAIPlanFunctionality` | Tap first plan card |
| 8 | `ClickSubscribeOrPurchaseAI` | Tap button, IAP flow engages |
| 9 | `CloseAIPhotoGen` | Tap X, returns to Settings |

### `Dogpack_Subscription`

| # | Method | What it verifies |
|---|---|---|
| 1 | `PrerequestFunctionsforSubscription` | Login + navigate to Profile |
| 2 | `NavigatesSettingActivityScreen` | Open Settings via hamburger |
| 3 | `NavigateToSubscriptionFromMenu` | Tap "Subscription" row, screen opens |
| 4 | `VerifyTabsRenderedFunctionality` | Verification + AI image credits tabs present |
| 5 | `SwitchToAIImageCreditsTabFunctionality` | Tap AI tab, Gemini content shows |
| 6 | `SwitchBackToVerificationTabFunctionality` | Tap Verification tab, content shows |
| 7 | `VerifyActivePlanOrEmptyState` | Either active-plan banner OR View Plans empty state |
| 8 | `TapViewPlansFromEmptyState` | Tap View Plans, lands on SubscribeDog (skips if active plan) |
| 9 | `VerifyChangePlanButtonForMonthly` | Change Plan button for monthly users (skips otherwise) |
| 10 | `VerifyFooterText` | Manage-subscription footer text (skips on empty state) |
| 11 | `CloseSubscriptionScreen` | Press back, returns to Settings |
