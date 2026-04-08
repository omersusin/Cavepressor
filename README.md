<div align="center">

# 🪨 Cavepressor

### AI-Powered Text Compression for Android

*Strip the grammar. Keep the facts. Save your tokens.*

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-API%2026%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

</div>

---

## 📖 What is Cavepressor?

**Cavepressor** is a sleek Android app that uses large language models (LLMs) to intelligently compress text — removing filler words and grammatical fluff while preserving the core meaning. Think of it as a smart summarizer that keeps every fact intact, only shorter.

Whether you're trimming prompts to fit an LLM context window, compressing meeting notes, or shrinking long articles into dense bullet-proof summaries, Cavepressor does it in seconds.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🤖 **Dual AI Providers** | Choose between **Groq** and **OpenRouter** for compression |
| 🎚️ **3 Compression Levels** | Light (15–30%), Medium (30–45%), Aggressive (45–60%) |
| 🔢 **Token Counter** | Live token estimates before and after compression |
| 📊 **Compression Stats** | See original vs. compressed tokens and reduction % |
| 📋 **Copy & Share** | One-tap copy or share compressed output |
| 🕒 **History** | Browse all past compressions stored locally |
| 🎨 **7 Built-in Themes** | Crimson · Violet · Ocean · Sage · Amber · Rose · Mono |
| 🌗 **Dark / Light Mode** | System-aware with manual override |
| 🖤 **AMOLED Support** | True-black mode for OLED screens |
| 🎨 **Dynamic Color** | Material You wallpaper-based theming (Android 12+) |
| 🔑 **Secure Key Storage** | API keys saved locally via DataStore |

---

## 📱 Screenshots

> _Add screenshots or a screen recording here to showcase the app._

| Home Screen | Result View | Settings |
|---|---|---|
| *(coming soon)* | *(coming soon)* | *(coming soon)* |

---

## 🏗️ Architecture

Cavepressor follows **Clean Architecture** with an MVVM presentation layer:

```
app/
├── di/                  # Hilt dependency injection modules
├── data/
│   ├── db/              # Room database (compression history)
│   ├── datastore/       # DataStore (user settings & API keys)
│   └── repository/      # CompressionRepository
├── domain/
│   ├── model/           # Core data models (CompressionResult, ApiProvider…)
│   └── usecase/         # Business logic (CompressText, FetchModels, GetHistory)
├── network/
│   ├── api/             # Retrofit API interfaces (Groq, OpenRouter)
│   ├── interceptor/     # OkHttp auth interceptor
│   └── model/           # Network response models
├── ui/
│   ├── screens/         # Compose screens (Home, Result, History, Settings)
│   ├── components/      # Reusable Composables
│   └── theme/           # Material3 color schemes & typography
└── viewmodel/           # CompressorViewModel (single source of truth)
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin 2.0 |
| **UI** | Jetpack Compose + Material 3 |
| **DI** | Hilt |
| **Database** | Room |
| **Preferences** | DataStore |
| **Networking** | Retrofit + OkHttp + Moshi |
| **Async** | Kotlin Coroutines + Flow |
| **Navigation** | Jetpack Navigation Compose |
| **Min SDK** | API 26 (Android 8.0) |
| **Target SDK** | API 35 (Android 15) |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- A free API key from [Groq](https://console.groq.com) or [OpenRouter](https://openrouter.ai)

### 1. Clone the repository

```bash
git clone https://github.com/ilker-binzet/Cavepressor.git
cd Cavepressor
```

### 2. Open in Android Studio

- File → Open → select the `Cavepressor` folder
- Let Gradle sync complete

### 3. Build & Run

```bash
./gradlew assembleDebug
```

Or hit the ▶️ **Run** button in Android Studio.

---

## 🔑 API Key Setup

Cavepressor connects to **Groq** and **OpenRouter** for LLM inference. You need at least one API key.

| Provider | Free Tier | Models | Get Key |
|---|---|---|---|
| **Groq** | ✅ Yes | Llama 3.3 70B, Mixtral, Gemma | [console.groq.com](https://console.groq.com) |
| **OpenRouter** | ✅ Yes | 100+ models (GPT-4o, Claude, etc.) | [openrouter.ai](https://openrouter.ai) |

> API keys are stored **locally** on your device using Jetpack DataStore and are never transmitted beyond the chosen provider's API endpoint.

Once you have a key:
1. Open the app → tap the **ⓘ** icon → **Settings**
2. Select your provider (Groq or OpenRouter)
3. Paste your API key
4. Pick a model and start compressing!

---

## 🎨 Themes

Cavepressor ships with **7 hand-crafted dark themes** plus full light mode and AMOLED support:

| Theme | Primary Accent |
|---|---|
| 🔴 **Crimson** | `#B3261E` |
| 🟣 **Violet** | `#D0BCFF` |
| 🔵 **Ocean** | `#82D3E0` |
| 🟢 **Sage** | `#A8D5BA` *(default)* |
| 🟡 **Amber** | `#FFB300` |
| 🩷 **Rose** | `#FFB3B4` |
| ⚪ **Mono** | `#E3E3E3` |

Switch themes any time from the **Settings** screen.

---

## 🎚️ Compression Levels

| Level | Token Reduction | Best For |
|---|---|---|
| 🟢 **Light** | 15–30% | Casual cleanup, minor trimming |
| 🟡 **Medium** | 30–45% | Balanced compression, most use cases |
| 🔴 **Aggressive** | 45–60% | Maximum density, LLM prompt trimming |

---

## 🤝 Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Made with ❤️ using Kotlin & Jetpack Compose

</div>
