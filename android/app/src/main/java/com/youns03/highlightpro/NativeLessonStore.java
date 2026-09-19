package com.youns03.highlightpro;

import android.content.Context;
import android.net.Uri;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Native replacement for lesson/audio IndexedDB stores. */
public final class NativeLessonStore {
    private static final String FILE="projects.json"; private static final String AUDIO_DIR="audio";
    private NativeLessonStore(){}
    public static List<LessonRepository.Lesson> loadMerged(Context c,List<LessonRepository.Lesson> bundled){
        List<LessonRepository.Lesson> out=new ArrayList<>(bundled); File f=new File(c.getFilesDir(),FILE); if(!f.exists())return out;
        try{JSONArray a=new JSONArray(read(f)); for(int i=0;i<a.length();i++){LessonRepository.Lesson l=fromJson(a.getJSONObject(i)); int at=index(out,l.id); if(at>=0)out.set(at,l);else out.add(l);}}catch(Exception ignored){} return out;
    }
    public static void save(Context c,LessonRepository.Lesson lesson){try{JSONArray all=new JSONArray();File f=new File(c.getFilesDir(),FILE);if(f.exists())all=new JSONArray(read(f));JSONObject value=toJson(lesson);int at=-1;for(int i=0;i<all.length();i++)if(all.getJSONObject(i).optString("id").equals(lesson.id)){at=i;break;}if(at>=0)all.put(at,value);else all.put(value);write(f,all.toString());}catch(Exception ignored){}}
    public static Uri copyAudio(Context c,String lessonId,Uri source)throws IOException{File dir=new File(c.getFilesDir(),AUDIO_DIR);if(!dir.exists()&&!dir.mkdirs())throw new IOException("Unable to create audio directory");File target=new File(dir,lessonId+".audio");try(InputStream in=c.getContentResolver().openInputStream(source);OutputStream out=new FileOutputStream(target)){if(in==null)throw new IOException("Unable to open audio source");byte[] b=new byte[8192];int n;while((n=in.read(b))>0)out.write(b,0,n);}return Uri.fromFile(target);}
    public static Uri audioUri(Context c,String lessonId){File f=new File(new File(c.getFilesDir(),AUDIO_DIR),lessonId+".audio");return f.exists()?Uri.fromFile(f):null;}
    private static int index(List<LessonRepository.Lesson> l,String id){for(int i=0;i<l.size();i++)if(l.get(i).id.equals(id))return i;return -1;}
    private static String read(File f)throws IOException{StringBuilder s=new StringBuilder();try(BufferedReader r=new BufferedReader(new InputStreamReader(new FileInputStream(f),StandardCharsets.UTF_8))){String x;while((x=r.readLine())!=null)s.append(x);}return s.toString();}
    private static void write(File f,String s)throws IOException{try(Writer w=new OutputStreamWriter(new FileOutputStream(f),StandardCharsets.UTF_8)){w.write(s);}}
    private static JSONObject toJson(LessonRepository.Lesson l)throws Exception{JSONObject o=new JSONObject();o.put("id",l.id).put("title",l.title).put("arabicTitle",l.arabicTitle).put("level",l.level).put("voiceId",l.voiceId).put("sourceType",l.sourceType).put("speed",l.speed).put("duration",l.duration).put("progressPercent",l.progressPercent).put("bookmarked",l.bookmarked).put("isOfflineReady",l.isOfflineReady);JSONArray ss=new JSONArray();for(LessonRepository.Sentence s:l.sentences){JSONObject so=new JSONObject().put("id",s.id).put("text",s.french).put("arabic",s.arabic).put("start",s.start).put("end",s.end);JSONArray ws=new JSONArray();for(LessonRepository.Word w:s.words)ws.put(new JSONObject().put("id",w.id).put("text",w.text).put("translation",w.translation).put("start",w.start).put("end",w.end));so.put("words",ws);ss.put(so);}return o.put("sentences",ss);}
    private static LessonRepository.Lesson fromJson(JSONObject l){try{JSONArray ss=l.getJSONArray("sentences");List<LessonRepository.Sentence> sentences=new ArrayList<>();for(int i=0;i<ss.length();i++){JSONObject s=ss.getJSONObject(i);JSONArray ww=s.getJSONArray("words");List<LessonRepository.Word> words=new ArrayList<>();for(int j=0;j<ww.length();j++){JSONObject w=ww.getJSONObject(j);words.add(new LessonRepository.Word(w.getString("id"),w.getString("text"),w.optString("translation",""),(float)w.getDouble("start"),(float)w.getDouble("end")));}sentences.add(new LessonRepository.Sentence(s.getString("id"),s.getString("text"),s.optString("arabic",""),(float)s.getDouble("start"),(float)s.getDouble("end"),words));}return new LessonRepository.Lesson(l.getString("id"),l.getString("title"),l.optString("arabicTitle",""),l.optString("level","auto"),l.optString("voiceId",""),l.optString("sourceType",""),(float)l.optDouble("speed",1),(float)l.optDouble("duration",1),(float)l.optDouble("progressPercent",0),l.optBoolean("bookmarked"),l.optBoolean("isOfflineReady",true),sentences);}catch(Exception e){throw new IllegalArgumentException(e);}}
}
