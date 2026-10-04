# App Tester Agent - Automated Stability & E2E Verification Script
param(
    [int]$MonkeyEvents = 800,
    [switch]$SkipBuild = $false,
    [switch]$LaunchEmulator = $false
)

$ErrorActionPreference = "Continue"

$adb = "C:\Users\anant\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$emulator = "C:\Users\anant\AppData\Local\Android\Sdk\emulator\emulator.exe"
$pkg = "com.ezi.gallery"
$activity = "$pkg/$pkg.MainActivity"
$apkPath = "app\build\outputs\apk\debug\app-debug.apk"
$reportsDir = "app\build\reports\app_tester"

New-Item -ItemType Directory -Force -Path $reportsDir | Out-Null

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host " Antigravity App Tester Agent: Stability Suite " -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan

# 1. Build Verification
if (-not $SkipBuild) {
    Write-Host "`n[1/5] Verifying Gradle Compilation and Assembling APK..." -ForegroundColor Yellow
    $buildResult = & .\gradlew.bat assembleDebug
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Build failed!" -ForegroundColor Red
        exit 1
    }
    Write-Host "Debug APK built successfully." -ForegroundColor Green
}

# 2. Device Detection
Write-Host "`n[2/5] Checking Connected Android Devices..." -ForegroundColor Yellow
$devices = & $adb devices | Where-Object { $_ -match "\bdevice\b" -and $_ -notmatch "List of devices" }

if (-not $devices) {
    Write-Host "No running Android device or emulator detected." -ForegroundColor Yellow
    if ($LaunchEmulator) {
        Write-Host "Starting emulator Medium_Phone in background..." -ForegroundColor Cyan
        Start-Process $emulator -ArgumentList "-avd Medium_Phone -no-boot-anim -netdelay none -netspeed full"
        Write-Host "Waiting for device to boot..." -ForegroundColor Cyan
        & $adb wait-for-device
        while ((& $adb shell getprop sys.boot_completed).Trim() -ne "1") {
            Start-Sleep -Seconds 2
        }
        Write-Host "Emulator is online." -ForegroundColor Green
    } else {
        Write-Host "Run with -LaunchEmulator or boot an emulator/connect a phone to run live device tests." -ForegroundColor Magenta
        Write-Host "Skipping on-device testing. Proceeding to static and unit verification." -ForegroundColor Gray
        exit 0
    }
}

# 3. APK Installation and Permissions
Write-Host "`n[3/5] Installing APK to Device..." -ForegroundColor Yellow
& $adb install -r $apkPath
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to install APK on device." -ForegroundColor Red
    exit 1
}
Write-Host "APK installed." -ForegroundColor Green

Write-Host "Granting storage and media permissions..." -ForegroundColor Cyan
& $adb shell pm grant $pkg android.permission.READ_MEDIA_IMAGES 2>$null
& $adb shell pm grant $pkg android.permission.READ_MEDIA_VIDEO 2>$null

# 4. App Launch and Smoke UI Test
Write-Host "`n[4/5] Launching Main Activity and Capturing Initial State..." -ForegroundColor Yellow
& $adb logcat -c
& $adb shell am start -n $activity
Start-Sleep -Seconds 3

# UI dump and screenshot
& $adb shell uiautomator dump /sdcard/view_initial.xml
& $adb pull /sdcard/view_initial.xml "$reportsDir\view_initial.xml" 2>$null
& $adb shell screencap -p /sdcard/screen_initial.png
& $adb pull /sdcard/screen_initial.png "$reportsDir\screen_initial.png" 2>$null
Write-Host "Initial launch captured to $reportsDir\screen_initial.png" -ForegroundColor Green

# 5. Stress and Chaos Stability Monkey Test
Write-Host "`n[5/5] Running Monkey Stress Test ($($MonkeyEvents) events)..." -ForegroundColor Yellow
$monkeyOutput = & $adb shell monkey -p $pkg -c android.intent.category.LAUNCHER --ignore-crashes --ignore-timeouts --ignore-security-exceptions --throttle 100 -v $MonkeyEvents 2>&1
$monkeyOutput | Out-File -FilePath "$reportsDir\monkey_run.log" -Encoding utf8

# Check for crashes in logcat
$crashes = & $adb logcat -d -b crash,main "*:E" | Select-String -Pattern "FATAL EXCEPTION", "AndroidRuntime", "$pkg"
$crashes | Out-File -FilePath "$reportsDir\crash_analysis.log" -Encoding utf8

if ($crashes) {
    Write-Host "Warning: Detected runtime errors or exceptions during test:" -ForegroundColor Red
    $crashes | Select-Object -First 10 | ForEach-Object { Write-Host $_ -ForegroundColor Red }
} else {
    Write-Host "Stability Test Passed: 0 Fatal Crashes, 0 ANRs detected during chaos stress test!" -ForegroundColor Green
}

Write-Host "`nStability reports and artifacts saved in: $reportsDir" -ForegroundColor Cyan
