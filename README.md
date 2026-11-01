# StudySync 📚

### Campus Activity & Study Group Finder

**SOFE 4640U — Mobile Application Development**  
**Ontario Tech University**

StudySync is a native Android application designed to help university students discover, create, and participate in study groups and academic activities happening around campus.

The application combines location-based discovery, interactive maps, and study group management to make academic collaboration more accessible.

## Project Overview

Students often struggle to find classmates who are studying the same subjects, preparing for exams, or organizing collaborative study sessions.

StudySync addresses this challenge by providing a centralized platform where students can create study groups, discover available sessions, and locate nearby academic activities using Google Maps.

Our goal is to make campus collaboration easier, more accessible, and more connected.

## Tech Stack

| Component | Technology |
|---|---|
| Android Application | Kotlin |
| UI Framework | Jetpack Compose |
| Backend | Node.js, Express.js |
| Database | PostgreSQL |
| Geospatial Database | PostGIS |
| Maps | Google Maps SDK for Android |
| Location Services | Google Play Services |
| API Communication | REST APIs, JSON |
| Development Tools | Android Studio, VS Code, Git, GitHub |

## Features

### Implemented in the Prototype

**1. Android User Interface**
- Landing, signup, and login screens
- Home screen displaying study groups
- Study group creation interface
- Interactive Google Maps screen
- Navigation between application screens

**2. Authentication Foundation**
- Signup and login interfaces
- Backend authentication endpoints
- PostgreSQL user account storage
- Android API methods for authentication

*Full integration of authentication into the current Android navigation flow is still being finalized.*

**3. Study Group Creation**
- Create study groups for specific courses
- Enter study session titles and locations
- Specify meeting times
- Save study groups to PostgreSQL
- Retrieve existing study groups from the backend

**4. GPS and Location Services**
- Request Android location permissions
- Retrieve device GPS coordinates
- Associate study groups with geographic locations
- Display the device's current location on Google Maps

**5. Google Maps Integration**
- Interactive campus map
- Study group location markers
- GPS blue-dot location indicator
- Marker clustering for nearby study groups
- Shared-location grouping for sessions at identical coordinates

**6. PostgreSQL and PostGIS**
- Persistent study group storage
- Geographic coordinate storage
- Spatial data retrieval
- Backend API integration

## System Architecture

StudySync follows a client-server architecture.

```text
┌─────────────────────────────┐
│      Android Application    │
│    Kotlin + Jetpack Compose │
└──────────────┬──────────────┘
               │
               │ HTTP / JSON
               ▼
┌─────────────────────────────┐
│        REST API             │
│     Node.js + Express       │
└──────────────┬──────────────┘
               │
               │ SQL Queries
               ▼
┌─────────────────────────────┐
│        PostgreSQL           │
│          + PostGIS          │
└─────────────────────────────┘

Android Application
       │
       ├── Google Maps SDK
       │
       └── GPS / Location Services
```

## Project Structure

```text
StudySync/
│
├── AndroidApp/
│   ├── app/
│   │   └── src/main/
│   │       ├── java/com/example/studysync/
│   │       │   ├── MainActivity.kt
│   │       │   ├── LandingScreen.kt
│   │       │   ├── SignupScreen.kt
│   │       │   ├── LoginScreen.kt
│   │       │   ├── HomeScreen.kt
│   │       │   ├── CreateGroupScreen.kt
│   │       │   ├── StudyMapScreen.kt
│   │       │   ├── LocationHelper.kt
│   │       │   └── StudySyncApi.kt
│   │       └── AndroidManifest.xml
│   │
│   └── build.gradle.kts
│
├── Backend/
│   ├── server.js
│   ├── package.json
│   └── package-lock.json
│
└── README.md
```

## Getting Started

### Prerequisites

Before running StudySync, install:

- Android Studio
- Android SDK and emulator
- Node.js and npm
- PostgreSQL
- PostGIS extension
- Git

A Google Maps API key is also required.

### 1. Clone the Repository

```bash
git clone https://github.com/liamcarey/StudySync.git
cd StudySync
```

### 2. Configure the Backend

Navigate to the backend directory:

```bash
cd Backend
npm install
```

Configure the required PostgreSQL connection settings using local environment variables.

Create the application's database and required tables using the project's database schema.

Enable PostGIS:

```sql
CREATE EXTENSION IF NOT EXISTS postgis;
```

Start the backend server:

```bash
node server.js
```

The development backend is configured to run locally on port `3000`.

### 3. Run the Android Application

1. Open the `AndroidApp` directory in Android Studio.
2. Allow Gradle to synchronize.
3. Configure a valid Google Maps API key using local, untracked configuration.
4. Start an Android emulator.
5. Run the Android application.

For local emulator-to-backend communication, the development setup uses:

```bash
adb reverse tcp:3000 tcp:3000
```

The Android application connects to the local backend through:

```text
http://127.0.0.1:3000
```

### 4. Test the Prototype

Once the backend and Android application are running:

1. Open StudySync.
2. Navigate to the Home screen.
3. Create a new study group.
4. Enter course, title, location, and meeting time.
5. Allow the application to access the device location.
6. Save the study group.
7. Confirm that it appears on the Home screen.
8. Select **View Study Groups on Map**.
9. Verify that the study group appears on Google Maps.

## Current Development Status

| Feature | Status |
|---|---|
| Android UI | Implemented |
| Signup and login screens | Implemented |
| Backend authentication foundation | Implemented |
| Full Android authentication flow | In progress |
| Study group creation | Working |
| PostgreSQL integration | Working |
| GPS integration | Working |
| Google Maps integration | Working |
| Map markers and clustering | Implemented |
| Persistent group joining | Planned |
| Real-time group chat | Planned |
| Room offline caching | Planned |
| Camera, audio, and sensor integration | Planned |

## Future Development

The next development phase will focus on:

- Completing authentication and session management
- Persistent study group joining and membership
- Real-time group messaging
- Nearby study group discovery and filtering
- Offline data caching using Room
- Additional Android device capabilities
- UI improvements and testing

## Development Approach

StudySync is being developed incrementally.

The initial milestone focuses on establishing a working end-to-end prototype connecting the Android interface, backend API, PostgreSQL database, GPS services, and Google Maps.

Additional functionality will be introduced and tested throughout the remainder of the project.

## Team

Developed as a group project for **SOFE 4640U — Mobile Application Development** at **Ontario Tech University**.

Team members contribute to Android development, backend integration, database management, testing, documentation, and project delivery.

## Academic Purpose

This project was developed for educational purposes as part of the SOFE 4640U course at Ontario Tech University.
