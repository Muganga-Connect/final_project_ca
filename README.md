# UPDATED PROJECT PROPOSAL
## Smart Appointment & Follow-up System (SAFS) Mobile Application

---

## 1. Define the Problem
Patients face several challenges in managing their healthcare appointments:

- **Missed Appointments:** Patients often forget scheduled visits, leading to delays in care  
- **Poor Appointment Management:** Difficulty in booking, rescheduling, or tracking appointments  
- **Lack of Reminders:** Patients are not reminded in time about upcoming visits  
- **No Personal Tracking:** Patients cannot easily view their appointment history  
- **No Predictive Support:** There is no system to identify patients likely to miss appointments  

---

## 2. Proposed Solution
The proposed solution is a **mobile application designed for patients** to manage their appointments efficiently.

The system will:
- Allow patients to book, reschedule, and cancel appointments  
- Provide automated reminders  
- Use a simple AI-based prediction system to identify possible missed appointments  
- Help patients track their appointment history  

👉 This simplified system focuses on improving **patient responsibility, convenience, and healthcare access**.

---

## 3. Key Features

### 🔹 Patient Registration & Authentication
- Register using phone number or email  
- Secure login with password  
- Optional biometric authentication (fingerprint/face)  
- Password reset functionality  

### 🔹 Appointment Management
- Book new appointments  
- Reschedule existing appointments  
- Cancel appointments  
- View upcoming appointments  

### 🔹 Reminder & Notification System
- Automatic reminders before appointments  
- Notifications sent:  
  - 1 day before  
  - Few hours before  
- Notifications via push notifications (Firebase)  

### 🔹 AI-Based No-Show Prediction 💡
The system analyzes:
- Previous missed appointments  
- Cancellation behavior  

Assigns a risk level:
- Low  
- Medium  
- High  

👉 Based on risk:
- High-risk patients receive earlier and more frequent reminders  
- Low-risk patients receive standard reminders  

### 🔹 Appointment History Tracking
- View past appointments  
- See status:  
  - Attended  
  - Missed  
- Helps patients monitor their consistency  

### 🔹 Simple & Accessible Design
- User-friendly interface  
- Works on Android devices  
- Lightweight and easy to use  

---

## 4. Technology Stack
- **Mobile Development:** Java or Kotlin (Android Studio)  
- **Database:** Firebase Firestore / SQLite  
- **Authentication:** Firebase Authentication + Biometric login  
- **Notifications:** Firebase Cloud Messaging (FCM)  
- **AI Logic:** Rule-based prediction (implemented in app logic)  
=======
# MugangaConnect+
> **Healthcare access, unified.**

A modern Android healthcare application built with Java and XML in Android Studio. MugangaConnect+ provides a clean, intuitive interface for patients to manage their healthcare needs — from appointments and reminders to vital signs tracking.

---

## Screenshots


---

## Features

- **Authentication** — Login and Sign Up screens with form validation
- **Biometric Login** — Fingerprint authentication support
- **Password Toggle** — Show/hide password visibility
- **Tab Navigation** — Smooth switching between Login and Sign Up
- **Dashboard** — Patient health overview with upcoming appointments
- **Smart Alerts** — AI-powered appointment reminders
- **Health Metrics** — Blood pressure, heart rate, glucose, and sleep tracking
- **Medication Reminders** — Scheduled medication alerts
- **Bottom Navigation** — Dashboard, Schedule, AI Assistant, Profile

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| UI | XML Layouts |
| Architecture | Activity + Fragment based |
| Layout | ConstraintLayout, LinearLayout |
| Components | CardView, ScrollView, EdgeToEdge |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |
| Build Tool | Gradle (Kotlin DSL) |

---

## Project Structure

```
app/
├── src/
│   └── main/
│       ├── java/com/example/mugangaconnect/
│       │   ├── LoginActivity.java        # Login screen (Launcher)
│       │   ├── SignUpActivity.java        # Sign up screen
│       │   └── MainActivity.java         # Dashboard screen
│       ├── res/
│       │   ├── drawable/
│       │   │   ├── bg_button_blue.xml    # Gradient blue button
│       │   │   ├── bg_edittext.xml       # Rounded input field
│       │   │   ├── bg_card.xml           # Card background
│       │   │   ├── bg_tab_selected.xml   # Active tab style
│       │   │   ├── bg_tab_unselected.xml # Inactive tab style
│       │   │   ├── bg_biometric_btn.xml  # Biometric button border
│       │   │   ├── ic_login_arrow.xml    # Login tab icon
│       │   │   ├── ic_person_add.xml     # Sign up tab icon
│       │   │   ├── ic_person.xml         # User/email field icon
│       │   │   ├── ic_lock.xml           # Password field icon
│       │   │   ├── ic_eye.xml            # Password toggle icon
│       │   │   └── ic_fingerprint.xml    # Biometrics icon
│       │   ├── layout/
│       │   │   ├── login.xml             # Login screen layout
│       │   │   ├── signup.xml            # Sign up screen layout
│       │   │   └── activity_main.xml     # Dashboard layout
│       │   └── values/
│       │       ├── colors.xml            # Color palette
│       │       ├── strings.xml           # String resources
│       │       └── themes.xml            # App theme
│       └── AndroidManifest.xml
└── build.gradle.kts
```

---

## Navigation Flow

```
LoginActivity  ──── Login ──────────────────────→  MainActivity
     │                                              (Dashboard)
     └── Sign up link ──→  SignUpActivity
                                │
                                ├── Sign-up ──→  MainActivity
                                └── Login tab ──→  LoginActivity
```

---

## Color Palette

| Name | Hex | Usage |
|---|---|---|
| `blue_primary` | `#1A5DC8` | Buttons, icons, active tab |
| `blue_dark` | `#0D3F99` | Button gradient end |
| `blue_light` | `#D6E6FF` | Borders, highlights |
| `blue_field_bg` | `#DCE9FB` | Input field background |
| `bg_light` | `#EEF4FF` | Screen background |
| `text_dark` | `#0D1B3E` | Primary text |
| `text_hint` | `#90A8C3` | Placeholder / hint text |
| `tab_selected_bg` | `#C5D9F5` | Selected tab background |
| `divider` | `#C5D9F5` | Divider lines |
| `link_blue` | `#1A5DC8` | Clickable links |

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11+
- Android SDK 36
- Gradle 8+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Muganga-Connect/MugangaConnect-Frontend.git
   ```

2. **Open in Android Studio**
   ```
   File → Open → select the cloned folder
   ```

3. **Sync Gradle**
   ```
   Click "Sync Now" in the yellow bar or go to File → Sync Project with Gradle Files
   ```

4. **Run the app**
   ```
   Click ▶ Run or press Shift + F10
   Select your emulator or connected device
   ```

---

## Dependencies

```kotlin
dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.fragment:fragment:1.8.1")
}
```

---

## AndroidManifest Overview

```xml
<!-- LoginActivity is the launcher — first screen shown -->
<activity android:name=".LoginActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<activity android:name=".SignUpActivity" android:exported="false" />
<activity android:name=".MainActivity"  android:exported="false" />
```

---

## Roadmap

- [ ] Firebase Authentication integration
- [ ] Real biometric authentication using BiometricPrompt API
- [ ] Appointment booking flow
- [ ] Push notifications for reminders
- [ ] Patient profile management
- [ ] Doctor search and filtering
- [ ] Medical records upload
- [ ] Dark mode support
- [ ] Multi-language support (Kinyarwanda, French, English)

---
