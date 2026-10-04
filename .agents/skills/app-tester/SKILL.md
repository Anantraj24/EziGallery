---
name: app-tester
description: Autonomous Android App Tester & Stability Evaluation Agent for Antigravity. Performs end-to-end UI testing, ADB Monkey stress/chaos testing, memory leak analysis, crash/ANR detection, and test suite execution.
risk: safe
version: 1.0.0
---

# 🤖 Antigravity App Tester Agent (Android & Jetpack Compose)

The **App Tester Agent** is an autonomous QA, reliability, and stability engineer tailored for Android applications built with Jetpack Compose, Kotlin Coroutines, Room, and MediaStore.

## Core Capabilities

1. **Compilation & Build Health**:
   - Validates JDK, Android SDK, and Gradle daemon toolchain consistency.
   - Enforces clean build passes (`assembleDebug`, `checkDebugAarMetadata`).
   - Verifies Proguard/R8 rules, manifest permissions, and resource integrity.

2. **Automated Unit & Architecture Testing**:
   - Executes local JVM unit tests (`testDebugUnitTest`).
   - Scaffolds and verifies tests for ViewModels, DAOs, utility mappers, and repository layers.
   - Asserts coroutine dispatchers and flow emission lifecycles.

3. **Autonomous ADB Device & Emulator Management**:
   - Detects connected physical devices and Android Virtual Devices (AVD).
   - Automatically starts emulator if none is booted.
   - Installs debug APKs (`adb install -r`) with zero manual friction.

4. **Stress & Chaos Stability Testing (Monkey Testing)**:
   - Injects pseudo-random UI interaction streams (taps, swipes, keypresses, system navigation) using `adb shell monkey`.
   - Simulates extreme rapid user input, orientation toggles, and memory pressure.
   - Traps Application Not Responding (ANR) events and native/JVM crashes.

5. **Deep Crash & Logcat Forensics**:
   - Real-time filtered logcat extraction (`adb logcat -d *:E`).
   - Identifies `FatalException`, `NullPointerException`, `IllegalStateException`, `OutOfMemoryError`, and Room database thread violations.
   - Flags UI thread blocks and unhandled coroutine cancellations.

6. **UI Inspection & Screen Capturing**:
   - Dumps UI layout hierarchies via `uiautomator dump /sdcard/view.xml`.
   - Captures automated screenshots at key navigation milestones for visual audit.

---

## 🛠 Usage Workflows

### 1. Quick Build & Static Health Check
```powershell
.\gradlew.bat assembleDebug
```

### 2. Run Complete Test Suite
```powershell
.\gradlew.bat testDebugUnitTest --info
```

### 3. Automated ADB Stability & Monkey Stress Run
```powershell
& ".\.agents\skills\app-tester\scripts\run_app_test.ps1"
```

### 4. Interactive Logcat Crash Triage
```powershell
& "C:\Users\anant\AppData\Local\Android\Sdk\platform-tools\adb.exe" logcat -d -b crash,system,main "*:E" | Select-String "FATAL|AndroidRuntime|ezi.gallery"
```
