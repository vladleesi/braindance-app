# Braindance

> **Important:**
> It is an ongoing project and is currently under development. However, I'm actively working on adding new features, enhancing existing functionality, and addressing any issues or bugs.

![Braindance vibe](https://github.com/vladleesi/braindance-app/assets/30999008/cdb06536-ecbf-43ae-9336-3833a89a3718)

## Overview
Braindance is a versatile game-tracking application that allows users to search, explore, and keep up with their favorite games. Powered by the extensive [IGDB database](https://api-docs.igdb.com/#getting-started), users can effortlessly find detailed information about any game, add them to their favorites, stay updated with release dates using the integrated calendar, and read the latest game news.

## Platforms
Braindance is a Kotlin full-stack project: Android, iOS, and browser clients share UI and logic through
[Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) and Compose Multiplatform.
The backend runs Kotlin/JVM with Ktor.

## Releases

The current Android and iOS release is
[Braindance Mobile 0.3.1](https://github.com/vladleesi/braindance-app/releases/tag/mobile-v0.3.1).
Both apps share the same `MAJOR.MINOR.PATCH` version and build number from `version.xcconfig`.

The backend is versioned independently from the mobile apps. Its current version is `0.3.0`.
After validation promotes a new mobile version to `master`, GitHub Actions publishes its shared Android/iOS
source release. The Mobile Release workflow can also publish or retry the current version manually from `master`.

## Project layout

| Directory | Purpose |
| --- | --- |
| `androidApp` | Android application host |
| `shared` | Shared Compose UI, app logic, and networking for Android, iOS, and Wasm |
| `iosApp` | Xcode and SwiftUI host |
| `webApp` | Compose Multiplatform browser host |
| `backend` | Kotlin/JVM Ktor API that proxies IGDB and GamerPower |

The clients call the backend for both IGDB and GamerPower data. Its URL is included in client builds and is not a
secret. Keep Twitch credentials in the backend runtime secret store. See [backend setup](backend/README.md).
GitHub Actions tests and packages the backend, then publishes a container image to GHCR from `master`.
Run that image on your chosen host; image publication does not deploy a running service.
See the [Cloud Run deployment guide](backend/cloud-run.md) for runtime configuration and cost controls.

## API credentials

IGDB does not issue a separate API key: it uses a Twitch developer application's client ID and client secret.
GamerPower requires no API key. See the
[backend credential guide](backend/README.md#configuration) for configuration and secret storage.

## Requirements

- JDK 21 for the backend and client builds; Android SDK API 37 for Android.
- macOS with Xcode and an iOS Simulator for iOS builds.
- A container host to deploy the backend.

Use the checked-in Gradle wrapper; a separate Gradle installation is unnecessary.

## Local setup

1. [Run or deploy the backend](backend/README.md#run-and-package) with its Twitch secrets and chosen rate limiter.
2. Create a root `local.properties` file. Add your Android SDK path if your environment needs it, and add the
   backend URL without a trailing slash:

   ```properties
   sdk.dir=/path/to/your/Android/sdk
   BACKEND_BASE_URL=https://api.example.com
   ```

   `local.properties` is ignored by Git. For CI or temporary builds, pass
   `-PBACKEND_BASE_URL=https://api.example.com` to Gradle or set
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
5. Run the browser client from the repository root:

   ```sh
   ./gradlew :webApp:wasmJsBrowserDevelopmentRun
   ```

   The development server prints the local URL. The browser client requires a browser with WasmGC support.

## Web publication

Pushes to `master` build the production browser app and replace the `web-app` branch with its static files. The
develop validation workflow also publishes after it promotes a validated commit to `master`. Configure these under
**Repository Settings → Secrets and variables → Actions**:

- Add the backend URL as the repository **secret** `BACKEND_BASE_URL`.
- To use a custom domain, add its hostname as the repository **variable** `WEB_CUSTOM_DOMAIN`, without `https://`
  or a trailing slash. **Do not add `WEB_CUSTOM_DOMAIN` as a secret:** the publication workflow reads the `vars`
  context and would otherwise omit the generated `CNAME` file on every deployment.

Select the `web-app` branch and `/ (root)` in GitHub Pages settings, then configure the matching DNS record and Pages
custom-domain setting. Include the deployed website origin in the backend's `CORS_ALLOWED_ORIGINS` variable. The
workflow preserves configured custom domains in the generated branch but does not change Pages, DNS, or backend
settings.

The project can be built without a backend URL. Configure a reachable backend before running backend-powered flows,
and add both Twitch secrets for IGDB requests. A fresh clone does not include anyone else's URL or credentials.

## License

Braindance is licensed under [GPL-3.0](LICENSE). Report issues through the
[GitHub issue tracker](https://github.com/vladleesi/braindance-app/issues).
