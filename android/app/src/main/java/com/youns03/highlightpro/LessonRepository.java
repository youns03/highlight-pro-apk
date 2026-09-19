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
    public static final class Lesson { public final String id,title,arabicTitle,level,voiceId,sourceType; public final float speed,duration,progressPercent; public final boolean bookmarked,isOfflineReady; public final List<Sentence> sentences; Lesson(String i,String t,String a,String l,String v,String st,float sp,float d,float pp,boolean b,boolean off,List<Sentence>s){id=i;title=t;arabicTitle=a;level=l;voiceId=v;sourceType=st;speed=sp;duration=d;progressPercent=pp;bookmarked=b;isOfflineReady=off;sentences=Collections.unmodifiableList(s);} }

    public static List<Lesson> load(Context context) {
        try {
            StringBuilder json=new StringBuilder();
            BufferedReader r=new BufferedReader(new InputStreamReader(context.getAssets().open("lessons.json"), StandardCharsets.UTF_8));
            String line; while((line=r.readLine())!=null) json.append(line); r.close();
            JSONArray all=new JSONArray(json.toString()); List<Lesson> out=new ArrayList<>();
            for(int i=0;i<all.length();i++) { JSONObject l=all.getJSONObject(i); JSONArray ss=l.getJSONArray("sentences"); List<Sentence> sentences=new ArrayList<>(); float duration=0;
                for(int j=0;j<ss.length();j++){ JSONObject s=ss.getJSONObject(j); JSONArray ww=s.getJSONArray("words"); List<Word> words=new ArrayList<>();
                    for(int k=0;k<ww.length();k++){ JSONObject w=ww.getJSONObject(k); words.add(new Word(w.getString("id"),w.getString("text"),w.optString("translation",""),(float)w.getDouble("start"),(float)w.getDouble("end"))); }
                    float end=(float)s.getDouble("end"); duration=Math.max(duration,end); sentences.add(new Sentence(s.getString("id"),s.getString("text"),s.optString("arabic",""),(float)s.getDouble("start"),end,words)); }
                out.add(new Lesson(l.getString("id"),l.getString("title"),l.optString("arabicTitle",""),l.optString("level","auto"),l.optString("voiceId",""),l.optString("sourceType",""),(float)l.optDouble("speed",1.0),(float)l.optDouble("duration",Math.max(duration,1f)),(float)l.optDouble("progressPercent",0),l.optBoolean("bookmarked",false),l.optBoolean("isOfflineReady",true),sentences));
            }
            return out;
        } catch(Exception e) { return Collections.emptyList(); }
    }
}
