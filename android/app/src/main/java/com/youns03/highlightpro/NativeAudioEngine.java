package com.youns03.highlightpro;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.os.Handler;
import java.util.Locale;

/** Native audio path. Position comes from MediaPlayer; TTS is intentionally sentence-only fallback. */
public final class NativeAudioEngine implements TextToSpeech.OnInitListener {
    public interface Listener { void onPosition(float seconds, boolean playing); void onEnded(); }
    private final Handler handler=new Handler(); private final TextToSpeech tts; private final Context context; private MediaPlayer player; private Listener listener; private float duration;
    public NativeAudioEngine(Context context){ this.context=context.getApplicationContext(); tts=new TextToSpeech(context,this); }
    public void setListener(Listener l){listener=l;}
    public void load(Uri uri){ releasePlayer(); if(uri==null)return; player=MediaPlayer.create(context,uri); if(player!=null){duration=player.getDuration()/1000f; player.setOnCompletionListener(p->{if(listener!=null)listener.onEnded();});}}
    public boolean hasAudio(){return player!=null;}
    public float duration(){return duration;}
    public void play(){if(player==null)return;player.start();tick();}
    public void pause(){if(player!=null)player.pause();}
    public void seek(float seconds){if(player!=null)player.seekTo((int)(seconds*1000));}
    public void speakSentence(String text,float rate){if(text==null||text.trim().isEmpty())return; tts.setSpeechRate(Math.max(.5f,Math.min(2f,rate))); tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"sentence");}
    private void tick(){handler.postDelayed(()->{if(player!=null&&player.isPlaying()){if(listener!=null)listener.onPosition(player.getCurrentPosition()/1000f,true);tick();}},25);}
    private void releasePlayer(){if(player!=null){player.release();player=null;}}
    public void release(){releasePlayer();tts.stop();tts.shutdown();}
    @Override public void onInit(int status){if(status==TextToSpeech.SUCCESS)tts.setLanguage(Locale.FRENCH);}
}
