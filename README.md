# Braindance: Games Tracker

## Overview:
The Braindance is an open-source Android application designed to help users track and manage their video game collections. It allows users to organize their games, track their progress, and discover new titles. This README file serves as a guide on how to build the application from source code and configure the necessary settings.

## Prerequisites:
Before proceeding with the build process, ensure that you have the following:

1. Android Studio: Download and install the latest version of Android Studio from the official website (https://developer.android.com/studio).

Certainly! Here's an updated version of the "Getting Started" section with instructions on how to create an application in Twitch Developers and obtain the client ID and client secret for use in the Braindance app:

## Getting Started:
Follow the steps below to build the Braindance application from source code:

1. Clone the Repository:
   ```
   git clone https://github.com/vladleesi/braindance-app.git
   ```

2. Import Project:
    - Launch Android Studio.
    - Click on "Open an existing Android Studio project" or select "File" -> "Open" from the menu.
    - Navigate to the directory where you cloned the repository and select the root folder.
    - Click "OK" to import the project.

3. Configure Twitch API Credentials:
    - Visit the [Twitch Developers Console](https://dev.twitch.tv/console) website and sign in with your Twitch account or create a new account if you don't have one.

4. Create a New Application:
    - Once logged in, navigate to the "Applications" section and click on "Register Your Application."
    - Fill in the required information, such as the name, description, and OAuth Redirect URLs.
    - After completing the form, submit it to create your Twitch application.

5. Obtain Client ID and Client Secret:
    - Once your application is created, you will be provided with a Client ID and Client Secret.
    - Copy these values as you will need them in the next step.

6. Configure Twitch API Credentials in the Braindance App:
    - Inside the project, locate the `shared/src/commonMain/kotlin/io/github/vladleesi/braindanceapp/config/Config.kt` file.
    - Replace the `CLIENT_ID` and `CLIENT_SECRET` values with your Twitch application's Client ID and Client Secret, respectively.

7. Build and Run:
    - Connect your Android device to your computer or set up an emulator.
    - Click on the "Run" button in Android Studio.
    - Select your device or emulator from the list and click "OK".
    - Android Studio will build the project and install the Braindance application on the selected device.

## Contribution:
Contributions to the Braindance project are welcome. If you would like to contribute, please follow these steps:

1. Fork the repository on GitHub.

2. Make the desired changes and commit them to your forked repository.

3. Submit a pull request detailing the changes you have made.

4. The project maintainers will review your pull request, provide feedback, and merge it if appropriate.

Please ensure that your contributions adhere to the project's coding conventions and follow best practices.

## License:
This App is released under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0). You can find the license details in the `LICENSE` file in the project repository.

## Support:
For any issues or questions related to the Braindance project, please open an issue on the GitHub repository (https://github.com/vladleesi/braindance-app/issues). I will assist you as soon as possible.

## Acknowledgments:
The Braindance project is built upon various open-source libraries and technologies. I would like to acknowledge the contributions of the open-source community and express my gratitude for their valuable work.