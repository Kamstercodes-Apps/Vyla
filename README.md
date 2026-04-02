# VYLA - Minimal & Customizable Android Launcher

VYLA is a modern, high-performance Android launcher built with **Jetpack Compose**. It blends minimalist aesthetics with powerful customization, inspired by the intuitive design of Smart Launcher 6.

![Launcher Preview](https://via.placeholder.com/800x400.png?text=VYLA+Launcher+Preview)

## ✨ Features

### 🌸 Smart Home Screen
- **Dual-Page Layout**: Focused Home Screen for favorites and a Categorized App Drawer for your full library.
- **Flower Layout**: An ergonomic, circular app arrangement optimized for one-handed use.
- **Dynamic Widgets**: Built-in glassmorphic widgets for **Clock**, **Weather**, and **Battery Status**.

### 📁 Intelligent App Drawer
- **Automatic Categorization**: Apps are sorted automatically into categories (Communication, Games, Media, Utilities, etc.) using system metadata.
- **Sidebar Navigation**: Quick-access category bar with smooth scaling animations and glassmorphism.
- **Real-time Search**: Instant app filtering as you type.

### 🎨 Deep Customization
- **Icon Pack Engine**: Support for all standard third-party icon packs (Nova, ADW, Go formats) with full `appfilter.xml` parsing.
- **Transparent Accents**: Adjust UI transparency from 5% to 80% to showcase your wallpaper.
- **Visual Themes**:
    - **Neon Mode**: Applies a vibrant glow and accent tint to all icons.
    - **Pastel Mode**: A soft, minimal aesthetic for a clean look.
- **Granular Controls**: Independently adjust **Icon Size**, **Label Size**, and **Grid Columns** (3 to 6).

## 🚀 Getting Started

### Prerequisites
- Android device running **Android 8.0 (Oreo) / API 26** or higher (for full categorization support).
- Android Studio Ladybug or newer.

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/vyla-launcher.git
   ```
2. Open the project in **Android Studio**.
3. Build and run the `:app` module on your device.
4. Set **VYLA** as your default home application in Android Settings.

## 🛠 Built With
- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Navigation**: [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
- **Storage**: [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Preferences)
- **Asynchrony**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)

## 📸 Screenshots
| Home (Grid) | Home (Flower) | App Drawer | Settings |
| :---: | :---: | :---: | :---: |
| ![Home Grid](https://via.placeholder.com/200x400) | ![Home Flower](https://via.placeholder.com/200x400) | ![App Drawer](https://via.placeholder.com/200x400) | ![Settings](https://via.placeholder.com/200x400) |

## 📜 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙌 Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

---
Developed by **KamsterCodes**
