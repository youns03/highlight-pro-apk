# تقرير ترحيل Android Native

## الحالة التنفيذية

تم تنفيذ **مرحلة Native قابلة للبناء والاختبار** مباشرة على الفرع `main`. أصبح مسار Android مستقلاً عن طبقة المتصفح: لا توجد داخل وحدة Android أي مراجع إلى WebView أو Render أو HTML/JavaScript خارجي أو اتصال شبكة مطلوب لبدء التطبيق.

هذه النتيجة ليست ادعاءً بأن كل وظائف تطبيق React الأصلية اكتملت 1:1. الوظائف التي لم تُنقل بعد موثقة صراحة في قسم القيود أدناه، وعلى رأسها إنشاء المشاريع من النص/الملف، التصدير، الترجمة السحابية، Gemini، ودمج Cloud TTS مع ملفات صوت فعلية.

## المعمارية القديمة

```text
React + Vite
  -> Browser APIs (IndexedDB, Web Audio, SpeechSynthesis, Blob)
  -> Android WebViewAssetLoader
  -> WebView
  -> bundled HTML/JS/CSS
```

كانت وحدة Android تفتح أصول الواجهة المجمعة عبر عنوان `appassets.androidplatform.net`. وكان الخادم TypeScript يوفر مسارات TTS والترجمة وGemini اختيارية.

## المعمارية الجديدة

```text
Android Activity
  -> Native Canvas UI
  -> LessonRepository
  -> bundled lessons.json
  -> SharedPreferences settings
  -> NativeAudioEngine
       -> MediaPlayer position callbacks for real local audio
       -> Android TextToSpeech sentence fallback
  -> TimingMath
       -> original sentence and word boundaries
```

بناء APK لا يشغّل Bun أو Vite ولا ينسخ `dist` إلى Android. تم حذف اعتمادات AndroidX الخاصة بتحميل المحتوى، وإزالة صلاحيات `INTERNET` و`RECORD_AUDIO` من الـ manifest لأن Core App الحالي لا يحتاجها.

## ما تم نقله

| المجال | النتيجة |
|---|---|
| الدروس الافتراضية | نُقلت الدروس السبعة كاملة إلى asset محلي |
| الجمل | نُقلت 17 جملة مع النص الفرنسي والعربي وحدود الجملة الأصلية |
| الكلمات | نُقلت 158 كلمة مع `start` و`end` والترجمة المعجمية الأصلية |
| Reader | واجهة Native لعرض النص الفرنسي والعربي والتظليل حسب الحدود المخزنة |
| Library | عرض الدروس وفتح القارئ دون اتصال |
| Stats | عرض عدد الدروس المحلية |
| Settings | الوضع الداكن/الفاتح وإظهار/إخفاء الترجمة مع حفظ SharedPreferences |
| Navigation | المكتبة، الإحصائيات، الإعدادات، والعودة من القارئ |
| Timing math | اختبار حدود التقدم واختيار الكلمة النشطة |
| الصوت الحقيقي | Native `MediaPlayer` مع موضع حقيقي كل 25ms عند وجود URI صوت محلي |
| TTS fallback | Android TTS على مستوى الجملة، مع رسالة صريحة بأنه غير متزامن |

## دقة التوقيت والصوت

تم فحص `audioEngine.ts` و`ttsEngine.ts` و`offlineDb.ts` و`FileReadingView.tsx` قبل الترحيل. المنطق الأصلي يميز بين مصدر صوت فعلي، حيث يأتي الزمن من `HTMLAudioElement.currentTime`، وSpeechSynthesis fallback، حيث يزيد الزمن بواسطة ticker ولا يمثل قياساً صوتياً حقيقياً. كما أن `ttsEngine.ts` يستخدم أحداث word-boundary من Cloud TTS عند توفرها، ويستخدم توزيعاً تناسبياً فقط عند غيابها.

في Native تم الحفاظ على بيانات الجمل والكلمات الأصلية حرفياً داخل `lessons.json`. وعند وجود صوت فعلي، يقرأ `NativeAudioEngine` الموضع من `MediaPlayer.getCurrentPosition()` ولا يعيد تقدير الموضع من عدد الأحرف. أما Android TTS فتم إبقاؤه fallback على مستوى الجملة فقط، ولا يُعرض للمستخدم على أنه متزامن. لم توجد ملفات صوت فعلية في المستودع للـ lessons الافتراضية، ولذلك لم يتم اختلاق ملفات أو الادعاء باختبار مزامنة غير ممكنة.

## الوظائف Online وغير المنقولة بعد

تبقى في المصدر القديم وظائف Cloud TTS، الترجمة الشبكية/Gemini، وGemini audio transcription كميزات Online للنسخة الويب فقط. لم تعد هذه الوظائف شرطاً لفتح APK Native.

لم تُنقل بعد إلى Native: إنشاء درس جديد من نص أو ملف صوت، استيراد JSON، إدارة النماذج، البحث، تصدير WhisperX/SMIL/VTT، تحرير word timings من الواجهة، وقائمة المشاريع المخصصة مع ملفات audio blobs. السبب أن نقلها يتطلب طبقة ملفات/قاعدة بيانات Native واختباراً وظيفياً منفصلاً، ولا يصح إعلان اكتمالها من مجرد نجاح Gradle.

## الاختبارات والنتائج

تم تنفيذ:

```text
gradle lint test assembleDebug --no-daemon
BUILD SUCCESSFUL
```

اختبارات JVM: **2 tests, 0 failures, 0 errors**. شمل ذلك التحقق من أن progress يُقص إلى `[0,1]` وأن اختيار الكلمة يعتمد على الحدود الأصلية.

Lint اكتمل بنجاح مع 9 تحذيرات غير مانعة، تشمل تحذيراً عن تثبيت الاتجاه الرأسي، استخدام API قديم، وإتاحة Custom View. لم يتم تشغيل APK على Emulator أو جهاز فعلي في هذه البيئة، لذلك لا أصف اختبار التثبيت/التشغيل الفعلي بأنه مُنجز.

## APK

الملف الناتج:

`android/app/build/outputs/apk/debug/app-debug.apk`

الحجم: **2,394,807 bytes (2.3 MB)**. الحزمة `com.youns03.highlightpro`، الإصدار `1.2.0`، واسم النشاط القابل للتشغيل `com.youns03.highlightpro.MainActivity`.

## Git

الفرع: `main`

| Commit | SHA |
|---|---|
| Migrate bundled lessons and exact timing data to native assets | `d830af4ac637fcb7153af1e14e2b66776d612710` |
| Create native Android reader and audio fallback | `672db987332aa36ec19585d1c550f44376c24a72` |
| Make Android CI native-only and document audio guarantees | `acbf09cd6fbbfe80c5a1501c134a926f97c146cf` |

تم التحقق من أن Android لا يحتوي على مراجع `WebView`, `WebViewClient`, `WebChromeClient`, `WebViewAssetLoader`, `appassets.androidplatform.net`, `onrender.com` أو `render.yaml`.

## المرحلة الثانية: مزامنة الصوت والتخزين

تم استكمال مسار الحالة التالي داخل Native:

```text
Audio URI
  -> NativeAudioEngine.load
  -> MediaPlayer.getCurrentPosition() كل 25ms
  -> NativeAudioEngine.Listener
  -> MainActivity.time / playing
  -> Reader إعادة الرسم
  -> active sentence / active word
```

أصبح `NativeAudioEngine` يدعم `load`, `play`, `pause`, `stop`, `seek`, `setSpeed`, وcompletion callback. عند وجود ملف صوت محفوظ فعلياً، يعتمد التظليل على موضع `MediaPlayer` الحقيقي. وعند عدم وجود ملف صوت، يشغل زر القارئ كامل الدرس عبر Android TTS queue مع تحذير صريح بأنه غير متزامن مع word timings.

أضيف `NativeLessonStore` لحفظ نسخة JSON من المشاريع والتوقيتات المعدلة داخل `filesDir/projects.json`، ونسخ ملفات الصوت إلى `filesDir/audio/<lessonId>.audio` وإعادتها إلى `MediaPlayer` عبر URI محلي. كما أضيف seek بالنقر على الجملة أو الكلمة، وتغيير سرعة التشغيل.

## التحقق الآلي من البيانات

تم تشغيل `tools/validate_native_lessons.py` بنجاح. قارَن التحقق المصدر الأصلي مع asset Native في IDs، النص الفرنسي، النص العربي، metadata، sentence boundaries، word IDs، وعدد الكلمات. النتيجة:

```text
Native parity OK: 7 lessons, 17 sentences, 158 words; metadata and text match.
```

اختبارات JVM الحالية: **4 tests, 0 failures, 0 errors**، وتشمل progress، active word، active sentence، boundary tolerance، وseek clamping.

لا يزال اختبار MediaPlayer على جهاز/Emulator فعلي غير منفذ لأن البيئة لا تحتوي جهازاً متصلاً أو system image/emulator جاهزاً. لذلك ما زلت لا أصف مزامنة ملف صوت حقيقي بأنها مُختبرة ميدانياً، رغم أن مسار callback أصبح موصولاً فعلياً في الكود.

## آخر APK

بعد المرحلة الثانية أصبح حجم debug APK **2,415,373 bytes**. تم تنفيذ `lint test assembleDebug` بنجاح.
