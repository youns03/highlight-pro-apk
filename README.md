# Highlight Pro

A French-learning application with an Android build.

## Standalone Android APK

The Android APK is built as a self-contained WebView application. The web frontend is compiled first and then bundled into the APK under Android assets.

The APK does **not** open or depend on a Render URL at startup.

### Build with GitHub Actions

Use the **Build Standalone Android APK** workflow. It:

1. installs the web dependencies;
2. builds the web frontend;
3. copies the generated frontend into Android assets;
4. builds the Android APK;
5. uploads the APK as a workflow artifact.

### Important limitation

The bundled frontend can work offline for features implemented locally, including local storage and the built-in lesson/dictionary data. Features that previously called the Node backend at `/api/*` (such as server-side neural translation/TTS/Gemini audio transcription) still require a backend or a future native/local implementation.

This change removes the Android app's **hosting dependency on Render**; it does not magically make third-party AI services themselves local.

## Run the web version

`npm install`

Set `GEMINI_API_KEY` if the server-side AI features are needed, then:

`npm run dev`
