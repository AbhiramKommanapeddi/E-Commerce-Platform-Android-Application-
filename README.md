# MarketHub - E-Commerce Platform Android Application

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20(Material3)-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean%20Architecture-blue.svg)](https://developer.android.com/topic/architecture)
[![Room](https://img.shields.io/badge/Local%20DB-Room-orange.svg)](https://developer.android.com/training/data-storage/room)
[![Firebase](https://img.shields.io/badge/Backend-Firebase%20Auth%20%7C%20Firestore%20%7C%20Storage-FFCA28.svg)](https://firebase.google.com/)
[![Hilt](https://img.shields.io/badge/DI-Dagger%20Hilt-brightgreen.svg)](https://dagger.dev/hilt/)
[![Retrofit](https://img.shields.io/badge/Network-Retrofit%202%20(FakeStore%20API)-red.svg)](https://square.github.io/retrofit/)

---

## 📌 Project Overview
**MarketHub** is an e-commerce Android application built for the **TuteDude Assignment**. It showcases modern Android engineering best practices with **MVVM architecture**, **Jetpack Compose (Material 3)**, **Hilt dependency injection**, **Room database** for offline local favorites, **Firebase Authentication & Firestore/Storage** for cloud marketplace persistence, and **Retrofit** for live recommended product feeds via the **FakeStore API**.

---

## 🚀 Key Features Implemented

### 1. Authentication (Firebase Auth)
- **User Registration**: Create an account with Full Name, Email, Password (with confirmation and length validation), and Phone Number.
- **User Login**: Secure sign-in with email and password, real-time error messages, and loading feedback.
- **Session Management**: Automatically restores session if already signed in, and provides a **Sign Out** option on the Profile screen.
- **Quick Demo / Evaluator Mode**: Allows instant testing even prior to configuring production Firebase API keys.

### 2. Home Screen
- **Marketplace Feed**: Displays products uploaded by users with title, category badge, thumbnail image, short description, price, and rating.
- **Click-to-Details**: Seamless navigation to the Product Details screen.
- **Search & Filter (Bonus)**: Real-time search query filtering across product titles and descriptions.
- **Category Filter Chips (Bonus)**: Filter products by categories like *Electronics*, *Fashion*, *Jewelery*, *Home & Living*, *Books*, and *All*.
- **Recommended Products Section**: Live recommendations fetched from the public **FakeStore API** using Retrofit, displayed horizontally with distinct badges.

### 3. Product Details Screen
- **Multi-Image Carousel / Gallery**: Supports previewing **minimum 3 images** with active dots indicators and clickable thumbnail strip.
- **Product Information**: Full title, price, category tag, rating, and comprehensive description.
- **Uploader Information**: Displays uploader name, verified seller badge, email, and contact number.
- **Direct Contact Action**: One-tap phone dialer (`Intent.ACTION_DIAL`) and email client (`Intent.ACTION_SENDTO`) to contact the seller.
- **Favorites Integration**: Quick toggle heart button synced with local Room database.

### 4. Upload Product Screen
- **Form Inputs**: Title, Price, Category dropdown, and Multiline Description.
- **Photo Selection**: Multi-photo picker using Android's modern photo contracts.
- **Minimum 3 Images Requirement**: Validates that at least 3 photos are attached before allowing publication (with an "Auto 3 Photos" helper for quick emulator testing).
- **Cloud Upload**: Uploads image files to **Firebase Storage** and writes product document to **Firebase Firestore**.
- **Push Notification (Bonus)**: Fires a local push notification (`NotificationCompat`) upon successful upload.

### 5. Favorites Screen (Room Database)
- **Offline Persistence**: Stores favorite products locally using an SQLite Room database (`AppDatabase`, `FavoriteProductDao`, `FavoriteProductEntity`).
- **Instant Sync**: Changes in favorites reflect automatically on the Home and Details screens via Kotlin Coroutine `Flow`.
- **Offline Accessible**: Accessible via the bottom navigation bar at any time.

---

## 🏗️ Architecture & Clean Code

The application follows the **Google Recommended Android Architecture** (Clean Architecture + MVVM):

```
app/src/main/java/com/tutedude/ecommerce/
│
├── data/
│   ├── local/                     # Room Database, DAO & Entity
│   │   ├── AppDatabase.kt
│   │   ├── FavoriteProductDao.kt
│   │   └── FavoriteProductEntity.kt
│   ├── remote/
│   │   ├── api/                   # Retrofit & FakeStore API
│   │   │   ├── FakeStoreApiService.kt
│   │   │   └── FakeStoreProductDto.kt
│   │   └── firebase/              # Firebase Auth, Firestore, Storage
│   │       └── FirebaseManager.kt
│   └── repository/                # Repository Pattern Implementations
│       ├── AuthRepository.kt & AuthRepositoryImpl.kt
│       ├── ProductRepository.kt & ProductRepositoryImpl.kt
│       └── FavoriteRepository.kt & FavoriteRepositoryImpl.kt
│
├── domain/
│   ├── model/                     # Core Domain Entities
│   │   ├── Product.kt
│   │   ├── User.kt
│   │   └── Category.kt
│   └── util/
│       └── Resource.kt            # Sealed Class for Success/Error/Loading
│
├── di/                            # Dagger Hilt Dependency Injection
│   ├── DatabaseModule.kt
│   ├── FirebaseModule.kt
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
│
├── ui/
│   ├── components/                # Reusable Jetpack Compose Components
│   │   ├── ProductCard.kt
│   │   ├── CategorySelector.kt
│   │   ├── SearchBar.kt
│   │   ├── ImageCarousel.kt
│   │   └── LoadingAndEmptyStates.kt
│   ├── navigation/                # Compose Navigation & BottomBar
│   │   ├── Screen.kt
│   │   └── NavGraph.kt
│   ├── screens/
│   │   ├── auth/                  # Login & Register UI + AuthViewModel
│   │   ├── home/                  # Home Feed UI + HomeViewModel
│   │   ├── details/               # Product Details UI + DetailsViewModel
│   │   ├── upload/                # Upload Product UI + UploadViewModel
│   │   ├── favorites/             # Favorites UI + FavoritesViewModel
│   │   └── profile/               # User Profile UI
│   └── theme/                     # Material 3 Theme, Typography, Colors
│
└── util/                          # Helpers
    └── NotificationHelper.kt      # Push Notifications channel and builder
```

---

## 🛠️ Tech Stack & Libraries
| Component | Technology |
|---|---|
| **Language** | Kotlin 2.0.21 |
| **UI Toolkit** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM + Repository Pattern |
| **Dependency Injection** | Dagger Hilt 2.52 |
| **Local Database** | Room 2.6.1 |
| **Networking** | Retrofit 2.11.0 + OkHttp Logging Interceptor |
| **JSON Parser** | Gson Converter |
| **Image Loading** | Coil Compose 2.7.0 |
| **Cloud Services** | Firebase Auth, Firestore, Storage (BoM 33.6.0) |
| **Concurrency** | Kotlin Coroutines & StateFlow |

---

## ⚙️ How to Setup & Run

### 1. Prerequisites
- **Android Studio** Ladybug (2024.2+) or newer.
- **JDK 17 or 21** configured in Android Studio (`Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK`).
- Android SDK with Platform 34 or 35 installed.

### 2. Clone and Open
1. Open Android Studio.
2. Select **Open** and choose the `E-Commerce Platform Android Application` folder.
3. Allow Gradle to sync dependencies.

### 3. Firebase Configuration (Optional for Production)
The app includes a working template `app/google-services.json` and resilient fallback data so it can be previewed immediately. To connect your personal Firebase project:
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Create a project and add an Android app with package name `com.tutedude.ecommerce`.
3. Download `google-services.json` and replace the existing one in `app/google-services.json`.
4. Enable **Authentication** (Email/Password), **Cloud Firestore**, and **Firebase Storage**.

### 4. Build and Run
- Select an Android Emulator (API 26+) or a physical device.
- Click the green **Run (▶)** button in Android Studio, or run via terminal:
```bash
./gradlew assembleDebug
```
- The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`.

---

## 🧪 Testing
Unit tests are included under `app/src/test/java/com/tutedude/ecommerce/`:
- `ProductModelTest.kt`: Verifies domain transformations and image fallback behaviors.
To run tests:
```bash
./gradlew testDebugUnitTest
```

---

## 📜 Assignment Rubric Compliance
- [x] **Firebase Authentication**: Email/Password login & registration with error states.
- [x] **Home Screen**: List of uploaded products with image, title, short description, price.
- [x] **Product Details**: Full description, multi-images carousel, price, uploader info with call/email actions.
- [x] **Upload Product**: Form with minimum 3 images validation, Firestore integration, and Storage support.
- [x] **Favorites List**: Stored locally in Room database, accessible via navigation tab.
- [x] **Architecture**: MVVM with Hilt DI, clean separation of UI, repository, and data layers.
- [x] **FakeStore API (Optional)**: Retrofit integration fetching recommended products.
- [x] **Bonus**: Real-time Search, Category Filter chips, and Push Notification on upload.
