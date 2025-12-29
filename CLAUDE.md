# CLAUDE.md

This file provides guidance to Claude Code when working with code in this repository.

**User Reference**: See `PROMPTS.md` for quick copy/paste prompts to update this file.

## Token Optimization Strategy

When exploring the codebase, prefer targeted analysis over exhaustive file reads. User values efficiency and token optimization.

## Documentation Guidelines

- **No usernames**: Never include specific usernames or user paths in documentation files
- Use placeholders like `/path/to/` or `$HOME` instead of actual user directories

## Project Structure

**Modules** (10 total - saves ~200 tokens vs reading settings.gradle):
- `app` - Main application
- `room` - Database layer
- `filesync`, `filesync_pro`, `filesync_sheet`, `filesync_csv`, `filesync_excel` - Sync features
- `filesync_tests` - Shared test utilities
- `android-apache-poi-5`, `android-apache-poi-5-with-dependencies` - Library modules (no tests)


## Java/Gradle Configuration

### This project requires Java 17 (not Java 24)

**Important considerations**:
- **Why this matters**: Java 24 causes Gradle 8.13 to fail with cryptic errors like "Type T not present" when creating test tasks
- **Android Studio vs CLI**: Android Studio's Gradle JDK setting (visible in IDE preferences) does NOT affect command-line builds
- **CLI requirement**: You MUST export JAVA_HOME when running gradlew:
  ```bash
  export JAVA_HOME=/path/to/java-17 && bash gradlew <task>
  ```

## Running Tests

**Quick Commands** (saves ~900 tokens vs exploration):

**Finding Java 17:**
Check `$HOME/Library/Java/JavaVirtualMachines/` for a Java 17 installation (look for directories like `jbr-17.*` or `temurin-17.*`). Use the path: `$HOME/Library/Java/JavaVirtualMachines/<java-17-dir>/Contents/Home`

### Unit Tests (All Modules)
```bash
export JAVA_HOME=/path/to/java-17 && bash gradlew test --continue --quiet
```
- **Execution time:** ~6 seconds
- **The `--continue` flag:** Ensures all modules are tested even if one fails
- **The `--quiet` flag:** Reduces log verbosity (saves ~6000-8000 tokens), only shows warnings/errors and BUILD status
- **Test reports:** `<module>/build/reports/tests/test/index.html`

### Instrumented Tests (All Modules)
**IMPORTANT:** Always check for connected devices BEFORE running instrumented tests. If no device is connected, inform the user that instrumented tests cannot run without a connected Android device.

**Device check command:**
```bash
$HOME/Library/Android/sdk/platform-tools/adb devices
```
Expected output should show at least one device (not just "List of devices attached").

**Run instrumented tests:**
```bash
export JAVA_HOME=/path/to/java-17 && bash gradlew connectedAndroidTest --continue --quiet
```
- **Execution time:** Several minutes
- **Prerequisites:** Android device connected via ADB
- **The `--quiet` flag:** Reduces log verbosity (saves ~6000-8000 tokens), only shows warnings/errors and BUILD status
- **Test reports:** `<module>/build/reports/androidTests/connected/debug/index.html`

#### FilesSync Pro Specific Tests
For targeted testing of FilesSync integration:
- **Module:** `filesync_pro`
- **Test runner:** `filesync_pro/src/androidTest/java/pl/gocards/filesync_pro/tests/RunInstrumentedTest.java`
- **Framework:** Cucumber-based BDD tests
- **Current configuration:** XlsxSync tests enabled (line 35 in RunInstrumentedTest.java)
- **Single module command:** `export JAVA_HOME=/path/to/java-17 && bash gradlew :filesync_pro:connectedAndroidTest --quiet`
