# Software Requirements Specification (SRS)
## MugangaConnect+

---

## 1. Introduction

### 1.1 Purpose
This document defines the functional and non-functional requirements for **MugangaConnect+**, a mobile application designed to help patients easily book, manage, and track their medical appointments.
The system also aims to reduce missed appointments through reminders and simple AI-based predictions.

### 1.2 Scope
The system will be an Android mobile application developed using Android Studio.
It will allow patients only to:
- Book appointments
- Receive reminders
- Manage their schedules

The system will include a basic AI feature to predict the likelihood of missing an appointment and trigger early reminders.

### 1.3 Definitions

| Term | Definition |
|------|------------|
| AI   | A simple prediction mechanism based on patient behavior |
| SRS  | Software Requirements Specification |
| SAFS | Smart Appointment & Follow-up System |

### 1.4 References
- Rwanda Ministry of Health Digital Strategy
- Firebase Documentation

---

## 2. System Overview

The SAFS mobile app is designed for **patients only**. It will:
- Allow patients to book and manage appointments
- Send automatic reminders
- Use AI prediction to identify possible missed appointments

---

## 3. User Roles

### Use Case Diagram
> Overview of all patient interactions with the system.

![Use Case Diagram](./muganga_use_case_diagram.svg)

### Login Use Case — UC-01
> Detailed use case description for the Login action including UML diagram.

![Login Use Case](./muganga_login_usecase.svg)

Only one role exists: **Patient**
- Register and log in
- Book, cancel, or reschedule appointments
- Receive reminders
- View appointment history

---

## 4. Functional Requirements

### 4.1 Authentication

| ID   | Requirement |
|------|-------------|
| FR-1 | The system shall allow patients to register using phone number or email |
| FR-2 | The system shall allow secure login |
| FR-3 | The system shall allow password reset |
| FR-4 | The system shall support biometric authentication (fingerprint or facial recognition) for login |
| FR-5 | The system shall allow patients to enable or disable biometric login |
| FR-6 | The system shall use device-level biometric security (e.g., Android fingerprint API) |
| FR-7 | The system shall require initial login using password before enabling biometric authentication |

### 4.2 Appointment Management

| ID    | Requirement |
|-------|-------------|
| FR-8  | The system shall allow patients to book appointments |
| FR-9  | The system shall allow patients to reschedule appointments |
| FR-10 | The system shall allow patients to cancel appointments |
| FR-11 | The system shall display upcoming and past appointments |

### 4.3 Reminder System

| ID    | Requirement |
|-------|-------------|
| FR-12 | The system shall send appointment reminders via notifications |
| FR-13 | The system shall send reminders at predefined times (e.g., 1 day before, 2 hours before) |

### 4.4 AI Prediction 💡

| ID    | Requirement |
|-------|-------------|
| FR-14 | The system shall analyze patient behavior (e.g., missed appointments history) |
| FR-15 | The system shall predict if a patient is likely to miss an appointment |
| FR-16 | The system shall send earlier or more frequent reminders to high-risk patients |

### 4.5 Appointment History

| ID    | Requirement |
|-------|-------------|
| FR-17 | The system shall store and display past appointments |
| FR-18 | The system shall mark appointments as attended or missed |

---

## 5. Non-Functional Requirements

| ID    | Requirement |
|-------|-------------|
| NFR-1 | The system shall be user-friendly and easy to navigate |
| NFR-2 | The system shall load within 3 seconds |
| NFR-3 | The system shall ensure data security using authentication |
| NFR-4 | The system shall be available 24/7 |
| NFR-5 | The system shall support Android devices |
| NFR-6 | The system shall ensure secure authentication using device-level biometric technologies |

---

## 6. Technical Requirements

| Component     | Technology |
|---------------|------------|
| Frontend      | Android Studio (Java or Kotlin) |
| Backend       | Firebase (Authentication + Firestore/Realtime DB) |
| Notifications | Firebase Cloud Messaging (FCM) |
| Database      | Firebase / SQLite |

---

## 7. System Architecture (Simplified)

```
┌─────────────────────────────┐
│  Presentation Layer         │  Mobile App (UI screens)
├─────────────────────────────┤
│  Application Layer          │  Appointment logic + AI logic
├─────────────────────────────┤
│  Data Layer                 │  Firebase database
└─────────────────────────────┘
```

### Class Diagram
> Core domain model showing all classes, attributes, methods, and relationships.

![Class Diagram](./muganga_class_diagram.svg)

### Activity Diagram
> Patient journey from app launch through authentication, booking, AI prediction, and reminders.

![Activity Diagram](./muganga_activity_diagram.svg)

### Sequence Diagram
> Step-by-step interactions between Patient, Mobile App, Firebase Auth, Firestore, AI Engine, and FCM.

![Sequence Diagram](./muganga_sequence_diagram.svg)

### Data Flow Diagram (Level 1)
> Data flows between all system processes, external entities, and Firebase data stores.

![Data Flow Diagram](./muganga_dfd.svg)

---

## 8. Implementation Plan

| Phase   | Description |
|---------|-------------|
| Phase 1 | Requirements + UI Design |
| Phase 2 | Authentication + Appointment booking |
| Phase 3 | Notifications (reminders) |
| Phase 4 | AI prediction feature |
| Phase 5 | Testing and deployment |

---

## 9. Expected Outcomes

- Reduced missed appointments
- Improved patient time management
- Better healthcare service access

---

## 10. Conclusion

The SAFS system provides a simple, scalable solution focused on patients. By combining appointment management with basic AI prediction, it enhances reliability and efficiency in healthcare access.

> **Document maintained by:** Uwizeye Gentille | Documentation & Testing
