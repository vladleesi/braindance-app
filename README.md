# Braindance: Games Tracker

> **Important:**
> It is an ongoing project and is currently under development. However, I'm actively working on adding new features, enhancing existing functionality, and addressing any issues or bugs.

![wallhaven-oxmlym-1](https://github.com/vladleesi/braindance-app/assets/30999008/cdb06536-ecbf-43ae-9336-3833a89a3718)

## Overview
Braindance is a versatile game-tracking application that allows users to search, explore, and keep up with their favorite games. Powered by the extensive [IGDB database](https://api-docs.igdb.com/#getting-started), users can effortlessly find detailed information about any game, add them to their favorites, stay updated with release dates using the integrated calendar, and read the latest game news.

## Platforms
Braindance is a multiplatform application that is available for Android and iOS. It is built using the [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) (KMP).

## Setup Instructions

1. Go to the [IGDB API docs](https://api-docs.igdb.com/#getting-started), look at **Account Creation** section, and follow the instructions to get your CLIENT_ID and CLIENT_SECRET.
2. Open `local.properties` file located in the root directory of your project and add these lines:
```
CLIENT_ID=YOUR_CLIENT_ID
CLIENT_SECRET=YOUR_CLIENT_SECRET
```
3. [Build](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-create-first-app.html#run-your-application) for the target platform.

## License
This App is released under the [The GNU General Public License v3.0](LICENSE).

## Support
For any issues or questions related to the Braindance project, please open an [issue](https://github.com/vladleesi/braindance-app/issues) on the GitHub repository. I will assist you as soon as possible.

## Acknowledgments
The Braindance project is built upon various open-source libraries and technologies. I would like to acknowledge the contributions of the open-source community and express my gratitude for their valuable work.