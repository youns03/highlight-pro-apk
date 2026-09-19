package com.youns03.highlightpro;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Loads the original bundled lesson corpus without a network dependency. */
public final class LessonRepository {
    private LessonRepository() {}
    public static final class Word { public final String id,text,translation; public final float start,end; Word(String i,String t,String tr,float s,float e){id=i;text=t;translation=tr;start=s;end=e;} }
    public static final class Sentence { public final String id,french,arabic; public final float start,end; public final List<Word> words; Sentence(String i,String f,String a,float s,float e,List<Word>w){id=i;french=f;arabic=a;start=s;end=e;words=Collections.unmodifiableList(w);} }
    public static final class Lesson { public final String id,title,arabicTitle,level; public final List<Sentence> sentences; public final float duration; Lesson(String i,String t,String a,String l,List<Sentence>s,float d){id=i;title=t;arabicTitle=a;level=l;sentences=Collections.unmodifiableList(s);duration=d;} }

    public static List<Lesson> load(Context context) {
        try {
            StringBuilder json=new StringBuilder();
            BufferedReader r=new BufferedReader(new InputStreamReader(context.getAssets().open("lessons.json"), StandardCharsets.UTF_8));
            String line; while((line=r.readLine())!=null) json.append(line); r.close();
            JSONArray all=new JSONArray(json.toString()); List<Lesson> out=new ArrayList<>();
            for(int i=0;i<all.length();i++) { JSONObject l=all.getJSONObject(i); JSONArray ss=l.getJSONArray("sentences"); List<Sentence> sentences=new ArrayList<>(); float duration=0;
                for(int j=0;j<ss.length();j++){ JSONObject s=ss.getJSONObject(j); JSONArray ww=s.getJSONArray("words"); List<Word> words=new ArrayList<>();
                    for(int k=0;k<ww.length();k++){ JSONObject w=ww.getJSONObject(k); words.add(new Word(w.getString("id"),w.getString("text"),w.optString("translation",""),(float)w.getDouble("start"),(float)w.getDouble("end"))); }
                    float end=(float)s.getDouble("end"); duration=Math.max(duration,end); sentences.add(new Sentence("s-"+l.getString("id")+"-"+j,s.getString("text"),s.optString("arabic",""),(float)s.getDouble("start"),end,words)); }
                out.add(new Lesson(l.getString("id"),l.getString("title"),l.optString("arabicTitle",""),i==0?"A1":(i<3?"A2":"B1"),sentences,Math.max(duration,1f)));
            }
            return out;
        } catch(Exception e) { return Collections.emptyList(); }
    }
}
