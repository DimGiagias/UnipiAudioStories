# UnipiAudioStories

UnipiAudioStories is an Android application designed to provide users with an immersive audio storytelling experience. The app allows users to explore, listen to, and manage their favorite audio stories, while also offering features like text-to-speech playback, multilingual support, and user statistics.

---

## Key Features

### 🎧 Audio Storytelling
- Browse a collection of stories with rich content and images.
- Play stories using Text-to-Speech (TTS) technology with customizable voice and speed options.

### 🌐 Multilingual Support
- Supports multiple languages, including English, Spanish, Japanese, and Greek.
- Users can switch between languages seamlessly.

### 📊 User Statistics
- Track your favorite stories and playback history.
- View rankings of your most-played stories.

### 🛠️ User Management
- Register and log in securely using Firebase Authentication.
- Manage your profile, including profile picture and email.

### 📱 Adaptive UI
- Optimized for different screen sizes using Material Design 3.
- Adaptive navigation bar and layout for phones and tablets.

### 🔄 Cloud Integration
- Stories and user data are stored in Firebase Firestore for real-time updates and synchronization.
- Firebase Crashlytics integration for monitoring app stability.

---

## Technologies Used

### 🛠️ Core Technologies
- **Kotlin**: The primary programming language for Android development.
- **Jetpack Compose**: Modern UI toolkit for building native Android UIs.
- **Firebase**: Backend services for authentication, Firestore database, and Crashlytics.

### 📚 Libraries
- **Material Design 3**: For a modern and consistent UI/UX.
- **Coil**: For efficient image loading and caching.
- **AndroidX Navigation**: For managing app navigation.
- **Text-to-Speech (TTS)**: For converting text into speech.

### 🔧 Build Tools
- **Gradle**: Build automation tool for managing dependencies and project configuration.
- **Compose Compiler**: For Jetpack Compose support.

---

## Installation

### Prerequisites
- Android Studio (latest version)
- Java 11 or higher
- Gradle 8.10.2 or higher

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/UnipiAudioStories.git
2. Open the project in Android Studio.
3. Sync the Gradle files.
4. Add your google-services.json file to the app/ directory for Firebase integration.
5. Build and run the app on an emulator or physical device.

## Usage

### Register/Login
- Create an account or log in to access the app.

### Browse Stories
- Explore the list of available stories on the home screen.

### Play Stories
- Select a story to view its details and play it using Text-to-Speech (TTS).

### Profile Management
- View and update your profile, and switch app languages.

### View Stats
- Check your favorite stories and playback rankings.

---

## Project Structure

```plaintext
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/unipiaudiostories/
│   │   │   ├── data/          # Data models, repositories, and Firebase services
│   │   │   ├── ui/            # UI components and screens
│   │   │   ├── utils/         # Utility classes (e.g., TTSManager, URL transformations)
│   │   │   ├── viewmodel/     # ViewModels for managing app state
│   │   ├── res/               # Resources (layouts, strings, drawables, etc.)
│   │   ├── [AndroidManifest.xml]
│   ├── test/                  # Unit tests
│   ├── androidTest/           # Instrumented tests
├── [build.gradle.kts]         # Project-level Gradle configuration
├── [settings.gradle.kts]      # Gradle settings
