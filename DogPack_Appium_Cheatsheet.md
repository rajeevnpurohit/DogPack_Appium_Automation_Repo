# DogPack Appium Automation — Windows Cheatsheet

Common commands for running, debugging, and managing the automation framework.
Project: `AppiumFramework_Dogpack` · Java + Maven + TestNG + Appium · Samsung SM-A156E · `com.dogpack`

---

## 1. Test Run Commands (most used)

### Recommended — via Maven profile (correct class ordering)

```cmd
mvn test -P SubscriptionFeaturesSmoke
```
Runs the smoke suite (`testng_Boost_AI_Subscription_Smoke.xml`). Tip@yopmail logs in first. **~25-35 min**.

```cmd
mvn test -P FullRun
```

```cmd
mvn test -P FullRun -Dtester="Rajeev Purohit"
```


Runs smoke + regression combined (`testng_Full_Run.xml`). **~50-90 min**.

```cmd
mvn test -P Regression
```
Runs regression only (`testng.xml`). **~25-55 min**.

### Direct XML invocation (use correct property name)

```cmd
mvn clean test -Dsurefire.suiteXmlFiles=testNGSuites/testng_Boost_AI_Subscription_Smoke.xml
mvn clean test -Dsurefire.suiteXmlFiles=testNGSuites/testng_Full_Run.xml
mvn clean test -Dsurefire.suiteXmlFiles=testNGSuites/testng.xml
```
**IMPORTANT:** Use `surefire.suiteXmlFiles` (plural, with `surefire.` prefix). The shorter `-DsuiteXmlFile=...` form alphabetizes classes and breaks ordering.

### Run a single test class

```cmd
mvn test -Dtest=Dogpack_BoostAccount
```

### Run a single test method

```cmd
mvn test -Dtest=Dogpack_BoostAccount#LoginForBoost
```

### Other useful flags

```cmd
mvn clean test -P SubscriptionFeaturesSmoke -DskipTests=false   :: explicit run
mvn test -P SubscriptionFeaturesSmoke -X                         :: verbose debug output
mvn test -P SubscriptionFeaturesSmoke -o                         :: offline (no dep download)
mvn dependency:tree                                              :: see dependency tree
```

---

## 2. Appium Server

### Start Appium server (default port 4723)

```cmd
appium
```

### Start with explicit port + log level

```cmd
appium --port 4723 --log-level info
appium --port 4723 --log-level debug   :: more detail when debugging
```

### Start with output piped to log file

```cmd
appium --log appium.log
```

### Check if Appium is running

```cmd
curl http://127.0.0.1:4723/status
```

### Kill Appium server on Windows

```cmd
:: Find process on port 4723
netstat -ano | findstr :4723

:: Kill by PID (replace 12345 with the PID from above)
taskkill /F /PID 12345

:: Or kill ALL Node processes (nuclear option)
taskkill /F /IM node.exe
```

---

## 3. ADB (Android Debug Bridge)

### List connected devices

```cmd
adb devices
adb devices -l                                  :: with model info
```

### Target a specific device (when multiple connected)

```cmd
adb -s RZCXB11TMJJ shell ...                    :: prefix any adb command
```

### App management

```cmd
adb install path\to\app.apk                     :: install/update
adb install -r path\to\app.apk                  :: replace existing
adb uninstall com.dogpack                       :: uninstall app
adb shell pm list packages | findstr dogpack    :: confirm installed
```

### App control

```cmd
adb shell am force-stop com.dogpack             :: kill app
adb shell pm clear com.dogpack                  :: clear app data (LOGOUT + RESET)
adb shell monkey -p com.dogpack 1               :: launch app (any activity)
```

### Get current activity (useful for adding to page objects)

```cmd
adb shell dumpsys activity activities | findstr "mResumedActivity"
adb shell dumpsys activity top | findstr ACTIVITY
```

### Live device log (logcat)

```cmd
adb logcat                                       :: all logs (overwhelming)
adb logcat -c                                    :: clear log buffer
adb logcat | findstr dogpack                     :: filter for app
adb logcat *:E                                   :: errors only
adb logcat -d > log.txt                          :: dump log to file (snapshot)
```

### Screenshots and screen recording

```cmd
adb shell screencap -p /sdcard/screen.png        :: capture
adb pull /sdcard/screen.png .                    :: download to current dir
adb shell rm /sdcard/screen.png                  :: cleanup

adb shell screenrecord /sdcard/video.mp4         :: record (Ctrl+C to stop)
adb pull /sdcard/video.mp4 .
```

### Useful device state

```cmd
adb shell getprop ro.product.model               :: device model
adb shell getprop ro.build.version.release       :: Android version
adb shell wm size                                :: screen resolution
adb shell input keyevent KEYCODE_HOME            :: press home button
adb shell input keyevent KEYCODE_BACK            :: press back button
```

---

## 4. Reports

### Open the latest ExtentReport (after a test run)

```cmd
start reports\index.html
```

The report is at `<project_root>\reports\index.html` (or wherever `ExtentReporterNG.java` writes it).

### Maven Surefire reports (raw TestNG output)

```cmd
start target\surefire-reports\emailable-report.html
```

---

## 5. Pre-Test Setup (do these before any run)

### Verify environment

```cmd
java -version                                    :: should be 11+
mvn -version                                     :: confirm Maven is installed
adb devices                                      :: device must be listed
appium --version                                 :: Appium version (should be 2.x)
```

### Reset device to clean state (before a fresh run)

```cmd
adb shell am force-stop com.dogpack
adb shell pm clear com.dogpack
```
Then re-launch and login fresh.

### Disable screen lock during long runs

```cmd
adb shell settings put global stay_on_while_plugged_in 7
adb shell settings put system screen_off_timeout 1800000   :: 30 min
```

### Restore defaults after testing

```cmd
adb shell settings put global stay_on_while_plugged_in 0
adb shell settings put system screen_off_timeout 30000
```

---

## 6. Common Troubleshooting

| Symptom | Quick fix |
|---|---|
| "device unauthorized" | On phone: tap **Allow** in the USB debugging prompt |
| Appium can't connect | `adb kill-server && adb start-server` |
| Tests skip immediately (0 ran) | Check `-Dsurefire.suiteXmlFiles=...` syntax (NOT `-DsuiteXmlFile=`) |
| `dpdelete` logs in first instead of `tip@` | Use a profile (`-P SubscriptionFeaturesSmoke`) or the correct `surefire.suiteXmlFiles` property |
| "session not created" | Restart Appium server; verify only one device is connected |
| App crashes mid-test | Check logcat (`adb logcat -d > crash.log`) right after the crash |
| Stale `target/` causing weird errors | `mvn clean` |
| OutOfMemory | `set MAVEN_OPTS=-Xmx2048m` before running |

---

## 7. Test Account Quick Reference

| Email | Password | Used by |
|---|---|---|
| `tip@yopmail.com` | `9Hertz#40` | Most smoke + regression tests (shared dog/business account) |
| `dpdelete@yopmail.com` | `Test@123` | Account-deletion tests only |
| `iamkiara01` / `iamkiara555` / `iamkabir01` | (in `LoginData.json`) | Regression-only flows |

Test card (marketplace payment): `4111 1111 1111 1111` · `12/30` · CVC `123`

---

## 8. Project Folder Quick Reference

```
AppiumFramework_Dogpack\
├── pom.xml                                 :: Maven build + 6 profiles
├── src\
│   ├── main\java\org\rahulshettyacademy\
│   │   ├── pageObjects\android\            :: page objects
│   │   ├── utils\                          :: AppiumUtils, AndroidActions
│   │   └── resources\
│   │       ├── data.properties             :: device + Appium config
│   │       └── log4j2.properties
│   └── test\java\org\rahulshettyacademy\
│       ├── *.java                          :: test classes
│       ├── TestUtils\                      :: AndroidBaseTest, Listeners
│       └── testData\
│           ├── LoginData.json              :: regression accounts (6 entries)
│           └── SmokeLoginData.json         :: smoke accounts (2 entries)
└── testNGSuites\
    ├── testng.xml                          :: regression
    ├── testng_Boost_AI_Subscription_Smoke.xml  :: smoke
    └── testng_Full_Run.xml                 :: smoke + regression
```

---

## 9. Quick Daily Workflow

```cmd
:: 1. Start Appium (in a separate terminal)
appium

:: 2. Verify device
adb devices

:: 3. Reset app to clean state
adb shell am force-stop com.dogpack
adb shell pm clear com.dogpack

:: 4. Run smoke
mvn test -P SubscriptionFeaturesSmoke

:: 5. View report
start reports\index.html
```
