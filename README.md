# RJNX Game Booster

Cyberpunk-style Android game booster (Kotlin + Jetpack Compose + Room + DataStore) with a live
FPS/system telemetry HUD, one-tap memory optimiser, floating in-game overlay panel, per-game
profiles, DND notification cleaner and a screen-recorder scaffold.

## Requirements

| Tool | Version |
| --- | --- |
| JDK | 17 (Temurin recommended) |
| Gradle | provided by the wrapper (`./gradlew`, Gradle 9.7.1) |
| Android Gradle plugin | 9.1.1 |
| Android SDK | Platform **android-36.1** (API 36 minor 1) + Build-Tools **36.0.0** |
| Kotlin | 2.2.10 (AGP 9.x built-in Kotlin support, Compose compiler plugin) |

Android Studio reads `local.properties` (`sdk.dir=/path/to/Android/sdk`); on CI the `ANDROID_HOME`
environment variable is enough.

## Build an APK

```bash
# 1. optional: a project-local debug keystore (otherwise AGP's ~/.android/debug.keystore is used)
keytool -genkeypair -v -keystore debug.keystore -storepass android -keypass android \
  -alias androiddebugkey -keyalg RSA -keysize 2048 -validity 10000 \
  -dname "CN=RJNX Debug, OU=Dev, O=RJNX, L=Local, C=US"

# 2. optional: the upload key used by the release variant (falls back to the debug key if absent)
keytool -genkeypair -v -keystore my-upload-key.jks -storepass change-me -keypass change-me \
  -alias upload -keyalg RSA -keysize 2048 -validity 10000 \
  -dname "CN=RJNX Upload, OU=Dev, O=RJNX, L=Local, C=US"

# 3. secrets (no Gemini call sites today, an empty file is fine)
cp .env.example .env

# 4. build
./gradlew assembleDebug            # -> app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease          # -> app/build/outputs/apk/release/app-release.apk
```

Install and run:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.aistudio.rjnxgamebooster.kxmpzq/com.example.MainActivity
```

Both variants are signed (debug key fallback) so they install directly on a device. The app requests
two permissions at the OS level, from **Settings → System permissions diagnostic** inside the app:
*Display over other apps* (floating HUD) and *Do Not Disturb access* (notification cleaner). The
game detector is an Accessibility Service and must be enabled in system settings.

## CI

`.github/workflows/android.yml` builds both APKs and uploads them as the `RJNX-Game-Booster-APK`
artifact; the run also posts an “APK build report” check containing the Gradle error excerpt.

```bash
gh workflow run "Build Android APK" --ref <branch>
gh run list --workflow "Build Android APK"
gh run download <run-id> --name RJNX-Game-Booster-APK
```

## Project layout

```
app/src/main/java/com/example/
  data/db/          Room entities + DAOs (games, boost history, per-game stats)
  data/model/       PerformanceMode, SystemStats, BoostResult
  data/preferences/ DataStore settings (overlay, DND, HUD animation speed, overlay position)
  data/repository/  Game + system-monitor repositories
  data/TelemetryBus.kt  process-wide stats bridge used by the services
  service/          GamingForegroundService, GamingOverlayService, GamingAccessibilityService
  ui/components/    Compose design system (gauges, graphs, carousel, holographic core, HUD dock)
  ui/screens/       Home, Game library, Per-game tuner, Modes, System monitor, Settings, splash
  utils/            BoostManager, SystemMonitorManager, NotificationCleaner, ScreenRecorder
```

## Notes

* Keystores and `.env` are git-ignored; the build scripts only wire up a signing config when the
  keystore file actually exists, so a fresh clone builds without extra setup.
* `killBackgroundProcesses()` only affects your own/other apps' *background* processes on
  un-rooted devices — the boost figures shown in the UI are intentionally partly simulated.
