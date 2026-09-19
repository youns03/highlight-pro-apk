# Highlight Pro

A French-learning application with an Android build.

## Standalone / Offline-first Android APK

The Android APK is a self-contained WebView application. The web frontend is compiled and bundled into the APK under Android assets.

The APK **does not open or depend on Render at startup**. It can launch, display the bundled interface, browse bundled lessons, read their French/Arabic text, use local storage, and use locally available lesson audio without an internet connection.

The Android wrapper uses Android's `WebViewAssetLoader` instead of `file://`. Android recommends `WebViewAssetLoader` for bundled HTML/CSS/JavaScript and other in-app content because it provides a compatible HTTP(S) origin while keeping the content packaged locally. citeturn0search0

### What works offline

- App launch and the bundled interface.
- The built-in French lessons and their Arabic translations.
- Browsing and reading the bundled lesson library.
- Search/filtering of locally available lessons.
- User-created lessons saved in IndexedDB/local storage.
- Imported local audio after it has been saved with a lesson.
- Playback of audio already stored in a lesson.
- Device/browser French speech synthesis where the Android WebView exposes a French voice.

### What remains optional and online-dependent

Some creation/enrichment features still use external services when requested:

- Neural server TTS at `/api/tts`.
- Batch TTS at `/api/tts/batch`.
- Online translation services and Gemini contextual translation.
- Gemini audio transcription.
- Any future feature that explicitly downloads a remote model or calls an external API.

The important point is that these services are **not required to start the app or read the bundled lessons**.

### True local AI is a separate step

Removing Render makes the Android package independent of Render hosting. It does **not** make third-party neural models magically local.

Fully offline neural TTS, high-quality offline translation, and offline AI transcription require their models and compatible runtimes to be bundled/downloaded locally. That is a substantially larger APK/model-engineering step. The app therefore follows an offline-first design: local learning content remains available, while optional online AI capabilities can be used when connectivity exists.

## Build with GitHub Actions

Use the **Build Standalone Android APK** workflow. It:

1. installs the web dependencies;
2. builds the web frontend;
3. copies the generated frontend into Android assets;
4. builds the Android APK;
5. uploads the APK as a workflow artifact.

The generated file is a normal installable Android APK. GitHub's artifact retention period controls only how long GitHub stores the downloadable build artifact; it does not impose an expiration date on an installed APK.

## Run the web version

`npm install`

Set `GEMINI_API_KEY` if the server-side AI features are needed, then:

`npm run dev`
