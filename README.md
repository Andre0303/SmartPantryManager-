# Smart Pantry Manager

## Description

Smart Pantry Manager is a Java-based Android application designed to help reduce food waste. Users can record ingredients available in their pantry, manage quantities and expiry dates, and find recipes they can prepare using ingredients they already have.

The application is designed to suggest recipes only when the user has all the required ingredients in sufficient quantities. This helps users make better use of existing food and avoid unnecessary shopping trips.

## Features

- Add pantry ingredients.
- View saved pantry ingredients.
- Edit and delete ingredients.
- Record ingredient quantities, units and optional expiry dates.
- Browse suggested recipes.
- View recipe preparation instructions.
- Store pantry items and recipe information locally.

## Database Choice

The application uses **SQLite**, accessed through Android's `SQLiteOpenHelper`.

SQLite was chosen because it is built into Android, works offline, and stores information locally on the device without requiring a separate database server or internet connection. It is suitable for a small pantry application that needs to save ingredient quantities and recipe data between sessions.

The database is named `smart_pantry.db`.

## Technologies Used

- Java
- Android Studio
- Android SDK
- SQLite
- Gradle

## Setup and Run Instructions

1. Install Android Studio.
2. Open the Smart Pantry Manager project in Android Studio.
3. Allow Gradle to sync and download any required dependencies.
4. Connect an Android device with USB debugging enabled, or create an Android emulator.
5. Select the device or emulator in Android Studio.
6. Click **Run** to build and launch the application.

The project uses the Gradle wrapper included in the repository.

## Data Storage

Pantry and recipe data are stored in the application's local SQLite database. The database is managed by `DatabaseHelper.java`.

The database belongs to the installed app. Uninstalling the application or clearing its data may remove the locally stored information.

## Project Status

This project is being developed incrementally as part of an Android application development assignment.
