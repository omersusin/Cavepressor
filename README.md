<div align="center">

# 🪨 Cavepressor

**AI-powered text compression for Android**

*Strip grammar. Keep facts. Cave style.*

[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-✓-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26-orange?style=for-the-badge)](https://developer.android.com)

</div>

---

## 📖 What is Cavepressor?

Cavepressor is an Android app that uses large language models to compress your text like a caveman — stripping away filler words, grammar noise, and redundancy while keeping every important fact, number, name, and technical detail intact.

Think of it as the opposite of fancy writing: maximum information, minimum tokens.

```
Before → "In order to be able to successfully accomplish the task at hand,
          it is essentially very important that you follow the given steps."

After  → "Follow steps to complete task."
```

---

## ✨ Features

| Feature | Description |
|---|---|
| 🤖 **Multi-Provider AI** | OpenRouter, Groq, and Hugging Face — pick your preferred cloud LLM |
| 📱 **On-Device LLM** | Run Gemma 2B locally via MediaPipe — no API key, no internet needed |
| 📊 **3 Compression Levels** | Light, Medium, and Aggressive with precise reduction targets |
| 🔢 **Token Counter** | Live token estimate so you always know your input size |
| 🎨 **7 Themes** | Crimson, Violet, Ocean, Sage, Amber, Rose, and Mono — plus AMOLED dark |
| 🕘 **History** | Every compression is saved locally; browse, copy, or delete past results |
| 📋 **Copy & Share** | One-tap copy or share your compressed text to any app |
| 🔒 **Private** | API keys stored on-device only, never sent anywhere else |

---

## 🗜️ Compression Levels

```
╔══════════════╦═══════════════╦══════════════════════════════════════════════╗
║ Level        ║ Reduction     ║ Style                                        ║
╠══════════════╬═══════════════╬══════════════════════════════════════════════╣
║ 🟢 Light     ║  15 – 30 %    ║ Remove filler words, keep sentence structure ║
║ 🟡 Medium    ║  30 – 45 %    ║ No articles, short sentences, active voice   ║
║ 🔴 Aggressive║  45 – 60 %    ║ Telegram-style: nouns + verbs + symbols only ║
╚══════════════╩═══════════════╩══════════════════════════════════════════════╝
```

### Aggressive Example
```
Before:  "The deployment pipeline was failing because the configuration
          file had incorrect environment variables set in the staging server."

After:   "Deploy fail → wrong env vars in staging config."
```

---

## 🤖 Supported AI Providers

<table>
<tr>
  <th align="center">Provider</th>
  <th>Models</th>
  <th>Notes</th>
</tr>
<tr>
  <td align="center"><img src="app/src/main/res/drawable/ic_groq.xml" width="1" height="1"><b>Groq</b></td>
  <td>llama-3.3-70b, mixtral-8x7b, gemma-7b, and more</td>
  <td>Very fast inference, generous free tier</td>
</tr>
<tr>
  <td align="center"><b>OpenRouter</b></td>
  <td>Claude, GPT-4o, Mistral, Llama, and 200+ models</td>
  <td>Largest model selection; pay-per-token</td>
</tr>
<tr>
  <td align="center"><b>Hugging Face</b></td>
  <td>Any model on Inference API</td>
  <td>Open-source models, free quota available</td>
</tr>
<tr>
  <td align="center"><b>Local LLM</b></td>
  <td>Gemma 2B (INT4, ~1.5 GB)</td>
  <td>100% offline — download once, compress forever</td>
</tr>
</table>

---

## 🎨 Themes

Cavepressor ships with seven hand-crafted dark themes, plus automatic light mode support:

<table>
<tr>
  <td align="center">🩸 <b>Crimson</b></td>
  <td align="center">🟣 <b>Violet</b></td>
  <td align="center">🌊 <b>Ocean</b></td>
  <td align="center">🌿 <b>Sage</b></td>
</tr>
<tr>
  <td align="center">🟡 <b>Amber</b></td>
  <td align="center">🌸 <b>Rose</b></td>
  <td align="center">⚪ <b>Mono</b></td>
  <td align="center">⬛ <b>AMOLED</b></td>
</tr>
</table>

Switch themes live from the Settings screen — no restart needed.

---

## 🏗️ Architecture

Cavepressor follows **Clean Architecture** with a unidirectional data flow:

```
┌─────────────────────────────────────────────────────────┐
│                     UI Layer                             │
│  HomeScreen · SettingsScreen · HistoryScreen            │
│  ResultScreen                                           │
│                 ↑ StateFlow                              │
│         CompressorViewModel (Hilt)                       │
└───────────────────────┬─────────────────────────────────┘
                        │ Use Cases
┌───────────────────────▼─────────────────────────────────┐
│                  Domain Layer                            │
│  CompressTextUseCase · GetHistoryUseCase                 │
│  FetchModelsUseCase · DownloadLocalModelUseCase          │
└──────────┬─────────────────────────┬────────────────────┘
           │                         │
┌──────────▼──────────┐   ┌──────────▼──────────────────┐
│    Data Layer        │   │      Data Layer             │
│  Cloud APIs          │   │  Room DB (History)          │
│  Retrofit + OkHttp   │   │  DataStore (Settings)       │
│  OpenRouter / Groq / │   │  MediaPipe (Local LLM)      │
│  Hugging Face        │   │                             │
└─────────────────────┘   └─────────────────────────────┘
```

### Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| DI | Hilt |
| Navigation | Compose Navigation |
| Networking | Retrofit 2 + OkHttp + Moshi |
| Local DB | Room |
| Preferences | DataStore |
| On-device AI | MediaPipe Tasks GenAI |
| Async | Kotlin Coroutines + Flow |
| Build | Gradle KTS + KSP |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- Android device / emulator running **API 26+** (Android 8.0)

### Build & Run

```bash
git clone https://github.com/ilker-binzet/Cavepressor.git
cd Cavepressor
./gradlew assembleDebug
```

### Add an API Key

1. Open the app → tap ⚙️ **Settings**
2. Choose your provider (Groq, OpenRouter, or Hugging Face)
3. Tap the **key icon** and paste your API key
4. Select a model — the list is fetched automatically

> **Groq** offers a free tier and is the fastest option for getting started.  
> Get your key at [console.groq.com](https://console.groq.com).

### Use the Local LLM (no key needed)

1. Settings → **Engine** → toggle to **Local LLM**
2. Tap **Download Model** (~1.5 GB, one-time)
3. Compress offline — no internet, no API key

---

## 📂 Project Structure

```
app/src/main/java/com/cavepressor/
├── data/
│   ├── datastore/       # Settings persistence (DataStore)
│   ├── db/              # Room database & DAO
│   ├── llm/             # MediaPipe local LLM service
│   └── repository/      # Compression result repository
├── di/                  # Hilt modules
├── domain/
│   ├── model/           # ApiProvider, CompressionResult, CompressionLevel
│   └── usecase/         # Business logic use cases
├── network/
│   ├── api/             # Retrofit API interfaces
│   ├── interceptor/     # Auth interceptor
│   └── model/           # Request / response models
├── ui/
│   ├── components/      # Reusable Compose components
│   ├── screens/         # Home, Settings, History, Result
│   └── theme/           # Colors, typography, themes
└── viewmodel/           # CompressorViewModel
```

---

## 🤝 Contributing

Pull requests are welcome! For large changes, open an issue first to discuss the approach.

1. Fork the repo
2. Create your branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m "Add my feature"`
4. Push: `git push origin feature/my-feature`
5. Open a Pull Request

---

## 📄 License

```
MIT License — see LICENSE for details.
```

---

<div align="center">

Made with 🪨 and Kotlin

*When words too many. Cavepressor fix.*

</div>
