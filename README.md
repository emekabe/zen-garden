# 🌿 Zen Garden Task Tracker

Welcome to the **Zen Garden Task Tracker**, a modern, offline-first Android application designed to promote mindful productivity. In this application, tasks are treated as seeds: sowing a task represents a new commitment, and completing tasks represents "watering" them, causing your digital Zen Garden to bloom and flourish.

This project is built using **Modern Android Development (MAD)** tools and guidelines. It serves as an excellent reference for modern architectural patterns, clean layouts, and reactive UI concepts.

---

## 🗺️ Architectural Overview

The project uses a clean **Model-View-ViewModel (MVVM)** architecture paired with a **Unidirectional Data Flow (UDF)** design. This keeps the concerns of data persistence, business logic, and UI rendering cleanly separated.

### Code & Data Flow

Below is the layout of how data streams flow from the persistent database up to the user interface:

```text
            ┌──────────────────────────────────────────────┐
            │            ROOM SQLITE DATABASE              │
            └──────────────────────┬───────────────────────┘
                                   │ (Raw Database Storage)
                                   ▼
            ┌──────────────────────────────────────────────┐
            │          TASKDAO & CATEGORYDAO               │
            └──────────────────────┬───────────────────────┘
                                   │ (Asynchronous Kotlin Flows)
                                   ▼
            ┌──────────────────────────────────────────────┐
            │        TASKREPOSITORY (DATA LAYER)           │
            └──────────────────────┬───────────────────────┘
                                   │ (Clean Domain Streams)
                                   ▼
            ┌──────────────────────────────────────────────┐
            │      TASKVIEWMODEL (PRESENTATION LAYER)      │ ◄── [ USER ACTIONS ]
            │   Combines flows into immutable HomeUiState  │    (Sow/Water Task,
            └──────────────────────┬───────────────────────┘     Search, Filter)
                                   │ (Reactive HomeUiState Flow)
                                   ▼
            ┌──────────────────────────────────────────────┐
            │     JETPACK COMPOSE UI (HOMESCREEN)          │
            └──────────────────────┬───────────────────────┘
                                   │ (Animated Completion Rate)
                                   ▼
            ┌──────────────────────────────────────────────┐
            │    ZENGROWTHWIDGET (ORGANIC PLANT CANVAS)    │
            └──────────────────────────────────────────────┘
```

---

## 🛠️ Technology Stack & Core Concepts

If you are transitioning to modern Android development, here are the key technologies and design decisions configured in this project:

### 1. Kotlin & Coroutines
* **What it is:** Kotlin is the modern, type-safe programming language officially recommended for Android development.
* **Asynchronous Programming:** Instead of using complex threads or callbacks, this app uses **Coroutines**. A coroutine is a lightweight framework for executing tasks in the background (such as querying a database or waiting for a timer) without blocking the user interface thread.
* **Flows:** A `Flow` is an asynchronous data stream that can emit multiple values sequentially over time. In this app, when a task is edited or completed in the database, the database automatically emits the updated list to the repository and UI.

### 2. Declarative UI with Jetpack Compose
* **What it is:** Traditional Android used XML files to build interfaces. Jetpack Compose uses a declarative approach written entirely in Kotlin.
* **How it works:** Instead of manually updating a button text, you write a function (marked with `@Composable`) that describes what the UI should look like for a given state. When the state changes, the system automatically regenerates the UI (a process called **Recomposition**).
* **State Management:** Composables track local variables using `remember { mutableStateOf(...) }` to maintain user interface variables (like whether a dialog is open or closed).

### 3. Reactive UI State (ViewModel)
* **What it is:** A `ViewModel` holds and manages UI-related data in a lifecycle-aware way. This means the data survives temporary configuration changes, such as rotating the device or resizing the window.
* **HomeUiState:** All variables needed to display the home screen are bundled into a single immutable data class `HomeUiState`.
* **StateFlow:** The UI listens to a `StateFlow` (a state-holder observable flow). In [TaskViewModel.kt](file:///Users/cachukwulobe/AndroidStudioProjects/TaskTracker/app/src/main/java/com/example/tasktracker/ui/viewmodel/TaskViewModel.kt), we use the `combine` operator to merge the streams of tasks, categories, search terms, and active filters, outputting a fresh `HomeUiState` whenever any of these inputs change.

### 4. Room Database
* **What it is:** Room is the official persistence library providing an abstraction layer over SQLite. It maps database tables directly to Kotlin classes (**Entities**) and queries to Kotlin functions (**DAOs**).
* **Type Converters:** Room cannot natively save complex custom types like Enum values or Date objects. We use [PriorityConverter](file:///Users/cachukwulobe/AndroidStudioProjects/TaskTracker/app/src/main/java/com/example/tasktracker/data/local/TaskDatabase.kt#L19) to encode custom priorities as simple strings when writing to disk and back into Kotlin objects when reading.

### 5. Manual Dependency Injection (AppContainer)
* **What it is:** Dependency Injection (DI) is a software design pattern where objects receive their dependencies from outside rather than creating them internally.
* **Why Manual?** Instead of using complex frameworks like Hilt or Dagger (which can feel opaque), this project uses a transparent, constructor-based manual injection system:
  1. We define an interface [AppContainer](file:///Users/cachukwulobe/AndroidStudioProjects/TaskTracker/app/src/main/java/com/example/tasktracker/di/AppContainer.kt) detailing the dependencies our app needs.
  2. [AppDataContainer](file:///Users/cachukwulobe/AndroidStudioProjects/TaskTracker/app/src/main/java/com/example/tasktracker/di/AppContainer.kt#L14) implements this interface, instantiating the database and repository on-demand using Kotlin's `lazy` delegate.
  3. The container is initialized in [TaskTrackerApplication](file:///Users/cachukwulobe/AndroidStudioProjects/TaskTracker/app/src/main/java/com/example/tasktracker/TaskTrackerApplication.kt#L7) which lives for the entire duration of the app.
  4. The view model retrieves repository instances via [TaskViewModel.Factory](file:///Users/cachukwulobe/AndroidStudioProjects/TaskTracker/app/src/main/java/com/example/tasktracker/ui/viewmodel/TaskViewModel.kt#L139).

---

## 📂 Project Directory Structure

```text
TaskTracker/
├── app/
│   ├── build.gradle.kts          # App-specific build and dependencies configuration
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml # Core app declarations (app name, components, permissions)
│       │   ├── java/com/example/tasktracker/
│       │   │   ├── MainActivity.kt # Entry point activity; hosts the Jetpack Compose environment
│       │   │   ├── TaskTrackerApplication.kt # Hooks up manual DI containers
│       │   │   ├── data/           # Data persistence and model layer
│       │   │   │   ├── local/
│       │   │   │   │   ├── dao/     # Data Access Objects (SQL queries)
│       │   │   │   │   ├── entity/  # Room database schema definitions
│       │   │   │   │   └── TaskDatabase.kt # Main Room database config and pre-population
│       │   │   │   ├── model/       # App domain data models (enums, etc.)
│       │   │   │   └── repository/  # Interface & implementation mapping data flows
│       │   │   ├── di/              # Dependency injection container interfaces
│       │   │   └── ui/              # User Interface components
│       │   │       ├── components/  # Reusable UI widgets (e.g. ZenGrowthWidget)
│       │   │       ├── screens/     # Screen-level composables (HomeScreen, SplashScreen)
│       │   │       ├── theme/       # Nature-inspired design tokens (Color, Type, Theme)
│       │   │       └── viewmodel/   # State holders coordinating database data and UI
│       │   └── res/                # XML rules, icons, graphics assets, and metadata
│       └── test/                   # Local Unit tests (testing view models and repositories)
├── gradle/                         # Gradle build wrapper configuration files
├── build.gradle.kts                # Project-wide dependency management and plugins
├── settings.gradle.kts             # Subproject and plugin repository definitions
└── gradle.properties               # JVM limits, build speeds, and Kotlin configurations
```

---

## 💻 Setting Up Your Development Environment

### 1. Install Android Studio
* Download and install the latest stable version of [Android Studio](https://developer.android.com/studio).
* During installation, accept the defaults to install the Android SDK (Software Development Kit) and necessary virtual device packages.

### 2. Import the Project
1. Open Android Studio.
2. Select **File > Open** (or **Open an Existing Project** on the welcome screen).
3. Navigate to the folder where you cloned this repository and click **Open**.
4. Wait for Android Studio to import the files. On the bottom right, you will see a progress bar indicating that **Gradle is syncing**. This downloads the necessary project dependencies and indexing tools.

> [!TIP]
> Ensure your machine is connected to the internet during the first import so Gradle can retrieve packages successfully.

---

## 🚀 Running the Application

You can execute the application using either a simulated phone (Emulator) or your physical Android device.

### Method A: Running on a Virtual Device (Emulator)
1. In Android Studio, open the **Device Manager** by clicking the phone icon on the top right toolbar or going to **Tools > Device Manager**.
2. Click **Create Device**.
3. Choose a device definition (e.g., Pixel 8) and click **Next**.
4. Select a system image (typically the latest stable version, e.g., API 35 or 36) and download it if needed.
5. Click **Finish** to create the emulator.
6. Once created, click the green **Play/Launch** button next to the device to boot it up.
7. With the emulator running, click the green **Run** arrow in the top toolbar of Android Studio (`Shift + F10` on Windows, `Control + R` on macOS) to compile and deploy the app.

### Method B: Running on a Physical Device
1. On your Android phone, open **Settings**.
2. Navigate to **About Phone** and tap **Build Number** seven (7) times. You will see a toast saying: *"You are now a developer!"*
3. Go back to the main Settings page, search for **Developer Options**, and turn on **USB Debugging**.
4. Connect your phone to your computer using a USB cable. Allow USB debugging permissions if prompted on your phone screen.
5. In Android Studio, locate the device dropdown in the top toolbar. Your phone's model name should now be visible.
6. Click the green **Run** arrow.

---

## 🛠️ Useful Command Line Commands

You can interact with the project build system using the terminal wrapper script (`./gradlew` on macOS/Linux or `gradlew.bat` on Windows) at the root of the project:

* **Compile the project:**
  ```bash
  ./gradlew assembleDebug
  ```
  *(This builds a runnable debugger package in your build directory.)*

* **Clean previous builds:**
  ```bash
  ./gradlew clean
  ```
  *(Useful if you encounter stale compilation artifacts or compile cache issues.)*

* **Run unit tests:**
  ```bash
  ./gradlew test
  ```
  *(Runs the automated unit test suite inside the `app/src/test` directory.)*

---

## 🎨 Learning Exercises & Customization Guide

The best way to understand modern Android development is by making changes. Here are three step-by-step exercises to practice on this codebase.

### Exercise 1: Customizing Default Categories
When the app is opened for the first time, the Room database pre-populates categories using a callback. Let's add a new default category called "Learning & Study".

1. Open [TaskDatabase.kt](file:///Users/cachukwulobe/AndroidStudioProjects/TaskTracker/app/src/main/java/com/example/tasktracker/data/local/TaskDatabase.kt).
2. Locate the private subclass `DatabaseCallback` at the bottom of the file (lines 59–107).
3. Inside the `onCreate` method, add the following code block:
   ```kotlin
   categoryDao.insertCategory(
       CategoryEntity(
           name = "Learning & Study",
           colorHex = 0xFF9E7BB5.toInt(), // Soft Lavender
           iconName = "School"
       )
   )
   ```
4. **Test it:** Uninstall the app from your device/emulator (to clear the database disk files) and run the app again. The database will recreate and populate with your new category!

---

### Exercise 2: Adding a Custom Property to a Task (e.g., "Notes")
Let's add a `notes` field to the task entry form and persist it to the SQLite database.

1. **Step 1: Update the Entity**
   Open [TaskEntity.kt](file:///Users/cachukwulobe/AndroidStudioProjects/TaskTracker/app/src/main/java/com/example/tasktracker/data/local/entity/TaskEntity.kt) and add the new field:
   ```kotlin
   val notes: String = ""
   ```
2. **Step 2: Update the ViewModel**
   Open [TaskViewModel.kt](file:///Users/cachukwulobe/AndroidStudioProjects/TaskTracker/app/src/main/java/com/example/tasktracker/ui/viewmodel/TaskViewModel.kt). Find the `insertTask` method (line 94) and update it to accept the notes parameter:
   ```kotlin
   fun insertTask(title: String, description: String, notes: String, categoryId: Long?, priority: Priority, dueDate: Long?) {
       viewModelScope.launch {
           val task = TaskEntity(
               title = title,
               description = description,
               notes = notes, // Map parameter to the model
               createdDate = System.currentTimeMillis(),
               dueDate = dueDate,
               priority = priority,
               isCompleted = false,
               completedDate = null,
               categoryId = categoryId
           )
           repository.insertTask(task)
       }
   }
   ```
3. **Step 3: Update the Dialog UI**
   Open [TaskEntryDialog.kt](file:///Users/cachukwulobe/AndroidStudioProjects/TaskTracker/app/src/main/java/com/example/tasktracker/ui/components/TaskEntryDialog.kt). Add a new text input field for the notes and pass the string parameter back to the VM on submission.

---

### Exercise 3: Under the Hood of the Zen Garden Widget
The [ZenGrowthWidget.kt](file:///Users/cachukwulobe/AndroidStudioProjects/TaskTracker/app/src/main/java/com/example/tasktracker/ui/components/ZenGrowthWidget.kt) uses Compose's low-level drawing canvas to render a growing plant. Let's see how this works:

* **State Animation:** `animatedProgress` uses `animateFloatAsState` to smoothly sweep from `0f` to `1f` in 800 milliseconds:
  ```kotlin
  val animatedProgress by animateFloatAsState(
      targetValue = completionRate,
      animationSpec = tween(durationMillis = 800),
      label = "plantGrowth"
  )
  ```
* **Bezier Curves:** It draws a natural stem utilizing quadratic curves on a pixel matrix canvas:
  ```kotlin
  val stemPath = Path().apply {
      moveTo(canvasWidth * 0.5f, canvasHeight * 0.78f)
      quadraticBezierTo(
          canvasWidth * 0.47f, canvasHeight * 0.6f,
          canvasWidth * 0.52f, stemTopY
      )
  }
  ```
* **Milestone Thresholds:** The app renders different parts of the plant depending on the completion percentage:
  * **> 5%**: Seed transforms into a growing stem.
  * **> 15% / > 35%**: Lower leaves sprout out of the stem.
  * **> 55% / > 75%**: Higher leaves appear.
  * **> 90%**: A beautiful terracotta-colored flower blossom blooms at the peak.

Try changing the blossom colors or adjusting the completion thresholds in the drawing code to customize your garden experience!
