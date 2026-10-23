<div align="center">

# 🏋️ DyeNTrack

### Your fitness, nutrition and wellness companion

**Android · Kotlin · Fitness Tracking · Health Monitoring**

<br />

<img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
<img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
<img src="https://img.shields.io/badge/Backend-Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />

<br /><br />

**DyeNTrack is an Android application designed to help users track their workouts, nutrition, sleep and personal progress.**

</div>

---

## 📖 Overview

DyeNTrack is a mobile application focused on fitness tracking and personal wellness.

The application brings together different aspects of everyday health monitoring, allowing users to follow their activities and better understand their progress over time.

Developed with Kotlin for Android, DyeNTrack communicates with a dedicated backend API built with Java and Spring Boot.

The project combines mobile development, backend integration and health-related data tracking in a single application.

## ✨ Key Features

- 🏋️ **Workout Tracking** — Follow fitness activities and training progress.
- 🥗 **Nutrition Monitoring** — Track nutritional information and calorie intake.
- 😴 **Sleep Tracking** — Monitor sleep-related information.
- 📊 **Progress Monitoring** — Follow personal metrics and changes over time.
- 🔗 **Backend Integration** — Communicate with a dedicated REST API.
- ❤️ **Health Data Integration** — Support for connecting health-related data sources where configured.

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin | Main programming language |
| Android | Mobile application platform |
| Spring Boot | Backend API |
| REST API | Client-server communication |
| Git | Version control |

## 🏗️ Architecture

DyeNTrack is composed of two complementary applications:

```text
┌────────────────────────────────┐
│       DyeNTrack Frontend       │
│                                │
│       Android Application      │
│             Kotlin             │
└───────────────┬────────────────┘
                │
                │ HTTPS / REST API
                ▼
┌────────────────────────────────┐
│        DyeNTrack Backend       │
│                                │
│          Java 21               │
│         Spring Boot            │
│       Spring Security          │
│             JWT                │
└───────────────┬────────────────┘
                │
                ▼
┌────────────────────────────────┐
│           MySQL                │
│       Data Persistence         │
└────────────────────────────────┘
```

The Android application provides the mobile interface, while the backend handles server-side operations, authentication and data persistence.

➡️ Backend repository: [HugoMolinier/DyeNTrack-BackEnd](https://github.com/HugoMolinier/DyeNTrack-BackEnd)

## 🚀 Getting Started

### Prerequisites

To build and run the application, you will need:

- Android Studio.
- The Android SDK required by the project.
- A compatible Android device or emulator.
- Access to a running DyeNTrack backend instance.

### 1. Clone the repository

```bash
git clone https://github.com/HugoMolinier/DyeNTrack-FrontEnd.git
cd DyeNTrack-FrontEnd
```

### 2. Open the project

1. Launch Android Studio.
2. Select **Open** and choose the cloned project directory.
3. Allow Gradle to synchronize the project.
4. Resolve any missing SDK or configuration requirements.

### 3. Configure the backend

Make sure the DyeNTrack backend is running and accessible from your Android device or emulator.

Configure the API base URL according to the application's existing network configuration.

For an Android emulator, the address `10.0.2.2` can be used to access a service running on the host machine, provided the backend listens on the corresponding port.

For a physical device, use an address reachable from the device.

### 4. Run the application

Select an Android emulator or connected device in Android Studio and launch the application using the **Run** button.

The application will require a working backend connection for features that depend on server-side data.

## 🔗 Related Project

### DyeNTrack Backend

The backend provides the server-side API used by the mobile application.

**Technologies:** Java 21 · Spring Boot · Spring Security · JWT · MySQL · Docker

## 👨‍💻 Author

**Hugo Molinier**

---

<div align="center">

**DyeNTrack** — Track your progress. Improve your habits. Keep moving.

</div>
