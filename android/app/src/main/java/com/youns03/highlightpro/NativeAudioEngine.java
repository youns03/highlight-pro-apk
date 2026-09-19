package com.youns03.highlightpro;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

/** Native playback source. Word highlighting is driven by real MediaPlayer position when audio exists. */
public final class NativeAudioEngine implements TextToSpeech.OnInitListener {
    public interface Listener { void onPosition(float seconds, boolean playing); void onEnded(); }
    private final Handler handler=new Handler(); private final TextToSpeech tts; private final Context context;
    private MediaPlayer player; private Listener listener; private float duration; private float speed=1f; private boolean released;
    private final Runnable ticker= new Runnable(){ @Override public void run(){ if(released)return; if(player!=null&&player.isPlaying()){ notifyPosition(true); handler.postDelayed(this,25); } } };
    public NativeAudioEngine(Context context){ this.context=context.getApplicationContext(); tts=new TextToSpeech(context,this); }
    public void setListener(Listener l){listener=l;}
    public void load(Uri uri){ stop(); releasePlayer(); duration=0; if(uri==null)return; player=MediaPlayer.create(context,uri); if(player!=null){ duration=player.getDuration()/1000f; player.setPlaybackParams(new PlaybackParams().setSpeed(speed)); player.setOnCompletionListener(p->{handler.removeCallbacks(ticker); notifyPosition(false); if(listener!=null)listener.onEnded();}); } }
    public boolean hasAudio(){return player!=null;}
    public float duration(){return duration;}
    public boolean isPlaying(){return player!=null&&player.isPlaying();}
    public float position(){return player==null?0:player.getCurrentPosition()/1000f;}
    public void play(){if(player==null)return; player.start(); notifyPosition(true); handler.removeCallbacks(ticker); handler.post(ticker);}
    public void pause(){if(player!=null){player.pause();handler.removeCallbacks(ticker);notifyPosition(false);}}
    public void stop(){if(player!=null){player.stop();handler.removeCallbacks(ticker);notifyPosition(false);}}
    public void seek(float seconds){if(player==null)return; float safe=Math.max(0,Math.min(seconds,duration)); player.seekTo((int)(safe*1000)); notifyPosition(isPlaying());}
    public void setSpeed(float rate){speed=Math.max(.5f,Math.min(2f,rate)); if(player!=null)player.setPlaybackParams(new PlaybackParams().setSpeed(speed));}
    public float speed(){return speed;}
    public void speakSentence(String text,float rate){if(text==null||text.trim().isEmpty())return; tts.setSpeechRate(Math.max(.5f,Math.min(2f,rate))); tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"sentence");}
    public void speakLesson(java.util.List<String> sentences,float rate){
        tts.setSpeechRate(Math.max(.5f,Math.min(2f,rate))); tts.stop();
        boolean first=true; for(String sentence:sentences){ if(sentence!=null&&!sentence.trim().isEmpty()){ tts.speak(sentence,first?TextToSpeech.QUEUE_FLUSH:TextToSpeech.QUEUE_ADD,null,"lesson"); first=false; } }
    }
    private void notifyPosition(boolean playing){if(listener!=null)listener.onPosition(position(),playing);}
    private void releasePlayer(){if(player!=null){player.release();player=null;}}
    public void release(){released=true;handler.removeCallbacks(ticker);releasePlayer();tts.stop();tts.shutdown();}
    @Override public void onInit(int status){if(status==TextToSpeech.SUCCESS)tts.setLanguage(Locale.FRENCH);}
}
