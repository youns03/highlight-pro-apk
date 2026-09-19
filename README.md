# Highlight Pro — Native Android

The Android application is now a standalone native implementation. Its core path is:

```text
Android Activity
  -> native Canvas reader UI
  -> asset-backed LessonRepository
  -> SharedPreferences settings
  -> NativeAudioEngine (MediaPlayer position callbacks)
  -> Android TextToSpeech sentence fallback
```

The legacy React/Vite source remains in the repository as a functional reference for future parity work, but it is not copied, built, or loaded by the Android module. The Android APK contains no HTML, JavaScript runtime, or browser wrapper.

## Offline core

All seven original bundled lessons are packaged in `android/app/src/main/assets/lessons.json`. The asset is generated from the original `src/utils/storage.ts` and preserves the source sentence and word-level timing records. The library, reader, Arabic translations, settings, navigation, and timing-based word highlighting work without network access.

## Audio and synchronization contract

`NativeAudioEngine` uses `MediaPlayer.getCurrentPosition()` at a 25 ms callback cadence when an actual local audio URI is available. The reader uses the original sentence and word boundaries and therefore does not invent new timing data. The current repository does not contain audio files for the bundled lessons, so the APK does not claim synchronized playback for them. Android TextToSpeech is available as a sentence-level offline fallback and is explicitly reported as **not synchronized** with the stored timestamps. The original cloud neural TTS remains documented in the legacy web source as an optional online feature and is not a startup dependency of the native core.

## Build and test

```bash
cd android
gradle lint --no-daemon
gradle test --no-daemon
gradle assembleDebug --no-daemon
```

The GitHub Actions workflow follows the same native-only sequence and uploads `app-debug.apk`.

## Migration notes

The browser-only IndexedDB, Web Audio, SpeechSynthesis, and Blob mechanisms are not used by Android. They remain in the legacy source because they are still used by the web build. Native Android uses packaged JSON for lesson data, `SharedPreferences` for settings, `MediaPlayer` for real local audio, and Android TTS for the explicitly non-synchronized fallback. Cloud translation, neural TTS, and Gemini transcription remain online-only features of the legacy web application and are not required for native startup or reading.
