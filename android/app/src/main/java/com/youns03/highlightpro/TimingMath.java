package com.youns03.highlightpro;

public final class TimingMath {
    private TimingMath() {}
    public static float wordProgress(float current, float start, float end){ if(current<=start)return 0f; if(current>=end)return 1f; return Math.max(0f,Math.min(1f,(current-start)/Math.max(end-start,.0001f))); }
    public static LessonRepository.Word activeWord(LessonRepository.Sentence sentence,float current){ for(LessonRepository.Word w:sentence.words) if(current>=w.start&&current<=w.end+.05f)return w; return null; }
    public static LessonRepository.Sentence activeSentence(java.util.List<LessonRepository.Sentence> sentences,float current){ for(LessonRepository.Sentence s:sentences) if(current>=s.start&&current<=s.end+.1f)return s; return null; }
    public static float clampSeek(float requested,float duration){ return Math.max(0f,Math.min(requested,Math.max(0f,duration))); }
}
