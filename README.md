# Braindance

> **Important:**
> It is an ongoing project and is currently under development. However, I'm actively working on adding new features, enhancing existing functionality, and addressing any issues or bugs.

![Braindance vibe](https://github.com/vladleesi/braindance-app/assets/30999008/cdb06536-ecbf-43ae-9336-3833a89a3718)

## Overview
Braindance is a versatile game-tracking application that allows users to search, explore, and keep up with their favorite games. Powered by the extensive [IGDB database](https://api-docs.igdb.com/#getting-started), users can effortlessly find detailed information about any game, add them to their favorites, stay updated with release dates using the integrated calendar, and read the latest game news.

## Platforms
Braindance is a multiplatform application that is available for Android and iOS. It is built using the [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) (KMP).

## Releases

The current Android and iOS release is
[Braindance Mobile 0.2.0](https://github.com/vladleesi/braindance-app/releases/tag/mobile-v0.2.0).
Both apps share the same `MAJOR.MINOR.PATCH` version and build number from `version.xcconfig`.

The Cloudflare Worker is versioned independently from the mobile apps. Its current version is `0.2.0`.
After validation promotes a new mobile version to `master`, GitHub Actions publishes its shared Android/iOS
source release. The Mobile Release workflow can also publish or retry the current version manually from `master`.

## Project layout

| Directory | Purpose |
| --- | --- |
| `androidApp` | Android application host |
| `shared` | Shared Compose UI, app logic, networking, and Android/iOS targets |
| `iosApp` | Xcode and SwiftUI host |
| `backend` | JavaScript API deployed as a Cloudflare Worker that proxies IGDB and GamerPower |

The mobile app calls the Worker for both IGDB and GamerPower data. The Worker URL is included in each mobile
build, so use your own deployment; its URL is not a secret. Keep Twitch credentials only in Cloudflare Worker
secrets. See [backend setup](backend/README.md) for deployment and abuse-control notes.

## Requirements

- JDK 21 and an Android SDK with API 37 installed for Android builds.
- macOS with Xcode and an iOS Simulator for iOS builds.
- Node.js 20 or newer and a Cloudflare account to deploy the Worker.

Use the checked-in Gradle wrapper; a separate Gradle installation is unnecessary.

## Local setup

1. [Deploy your own Worker](backend/README.md) and configure its Twitch secrets.
2. Create a root `local.properties` file. Add your Android SDK path if your environment needs it, and add the
   Worker URL without a trailing slash:

   ```properties
   sdk.dir=/path/to/your/Android/sdk
   BACKEND_BASE_URL=https://your-worker.example.workers.dev
   ```

   `local.properties` is ignored by Git. For CI or temporary builds, pass
   `-PBACKEND_BASE_URL=https://your-worker.example.workers.dev` to Gradle or set
   `ORG_GRADLE_PROJECT_BACKEND_BASE_URL`. No Twitch client ID or secret belongs in this file or in a mobile build.
3. Build the Android app from the repository root:

   ```sh
   ./gradlew :androidApp:assembleDebug
   ```

   Install the resulting APK from `androidApp/build/outputs/apk/debug/`, or run the `androidApp` configuration in
   Android Studio.
4. On macOS, open `iosApp/iosApp.xcodeproj` in Xcode, select an iOS Simulator, and run the `iosApp` target. Xcode
   invokes `:shared:embedAndSignAppleFrameworkForXcode`. For a physical device or distribution, select your own
   Apple development team and bundle identifier in Xcode.

The project can be built without a Worker URL. Configure a reachable Worker before running backend-powered flows,
and add both Twitch secrets for IGDB requests. A fresh clone does not include anyone else's Worker URL or credentials.

## License

Braindance is licensed under [GPL-3.0](LICENSE). Report issues through the
[GitHub issue tracker](https://github.com/vladleesi/braindance-app/issues).
