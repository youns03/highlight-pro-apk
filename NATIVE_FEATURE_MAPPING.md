# Original-to-Native Feature Mapping

| Original feature | Original implementation | Native target/current state | Status |
|---|---|---|---|
| Default lesson corpus | `DEFAULT_FRENCH_LESSONS` in `src/utils/storage.ts` | `lessons.json` + `LessonRepository` | Migrated and parity-validated: 7 lessons, 17 sentences, 158 words |
| Sentence and word timing | `SentenceItem`, `WordTiming`, `calculateWordProgress` | `LessonRepository.Word/Sentence`, `TimingMath` | Original IDs, text, boundaries, translations, and transition tolerance preserved |
| Real audio playback | `HTMLAudioElement`, `currentTime`, 25ms ticker | `MediaPlayer`, `getCurrentPosition`, native ticker | Activity callback/load/play/pause/stop path wired; requires an actual stored audio file |
| Cloud Neural TTS | `/api/tts`, subtitles/word boundaries | Native online adapter to be added separately | Not yet migrated |
| Browser SpeechSynthesis | browser voice fallback | Android `TextToSpeech` sentence fallback | Available, explicitly non-synchronized |
| IndexedDB lessons | `offlineDb.ts` lessons store | Native project store | `NativeLessonStore` persists JSON projects and edited timings |
| IndexedDB audio blobs | `offlineDb.ts` audio_blobs store | Internal files + URI references | `copyAudio` and `audioUri` implement the native file path |
| Reader translation | `FileReadingView.tsx` Arabic rendering | Native reader Arabic rendering | Partially migrated |
| Word seek | click word -> `onSeek` | Native touch hit testing -> `audio.seek` | Implemented against stored word start |
| Sentence seek | click sentence -> `onSeek` | Native sentence hit testing -> `audio.seek` | Implemented against stored sentence start |
| Play/pause/resume/stop | `AudioEngine` | `NativeAudioEngine` | Activity controls wired to real MediaPlayer state |
| Playback speed | HTML playbackRate / TTS rate | MediaPlayer PlaybackParams / TTS rate | Reader control cycles 0.5x–1.5x |
| Full text mode | React reader mode | Native reader | Not yet implemented as a separate mode |
| Timing tune mode | editable word fields + persistence | Native timing editor + project store | Not yet implemented |
| Export | Project/WhisperX/SMIL/VTT | Native file exporter | Not yet implemented |
| Search tab | React navigation/search UI | Native search/filter screen | Hidden from Native navigation until implemented |
| Voice manager | React model manager modal | Native voice/TTS settings | Hidden from Native navigation until implemented |
| Settings | React config + localStorage | SharedPreferences | Dark mode and Arabic visibility migrated |
| Stats | React stats view | Native stats view | Basic count migrated |
| Online isolation | optional fetch/API calls | Native core does not request network | Core startup independent |
