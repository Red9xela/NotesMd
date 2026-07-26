# 📝 NotesMd

NotesMd is a native Android note-taking app with full Markdown support. Built entirely in Kotlin with Clean Architecture and MVVM, it keeps business logic platform-independent. The UI uses Jetpack Compose, data is stored offline in Room with fast full-text search, and Hilt handles dependency injection.

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🖊️ **Rich Markdown Editing** | Write notes in Markdown with live preview and syntax highlighting |
| 🔍 **Full-Text Search** | Lightning-fast search across all notes via optimized Room indexing |
| 💾 **100% Offline** | All data stored locally — no cloud, no accounts, no tracking |
| 🏷️ **Note Organization** | Categorize and manage notes with tags/folders |
| 🌙 **Material You** | Clean, modern UI with dynamic theming support |
| ⚡ **Instant Sync** | Auto-save and state restoration for seamless editing |

---

## 🏗️ Architecture

**Key architectural decisions:**
- **Platform-independent business logic** — Domain layer has zero Android dependencies, making it fully testable and portable
- **Unidirectional Data Flow** — UI state is derived from ViewModels, ensuring predictable behavior
- **Repository Pattern** — Abstracted data access for easy future extensibility

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|------------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose |
| **Architecture** | MVVM + Clean Architecture |
| **DI** | Hilt |
| **Database** | Room |
| **Async** | Kotlin Coroutines + Flow |
| **Markdown** | Compose Richtext |
| **Navigation** | Jetpack Navigation Compose |
| **Build** | Gradle |

---

## 🗺️ Roadmap

- [ ] Folders support
- [ ] Note export to PDF / HTML
- [ ] Biometric lock for sensitive notes
- [ ] Widget for home screen
- [ ] Note backup & restore (JSON/Zip)
- [ ] Cross-device sync via optional cloud backup
