package it.android.radiocitylight;

import java.io.*;
import java.util.Timer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.util.Log;

/*
 * AVVIO E ARRESTO DEL SERVIZIO
 * startService(new Intent(this, RCLService.class));
 * stopService(new Intent(this, RCLService.class));
 */

public class streamService extends Service implements OnPreparedListener, OnErrorListener, AudioManager.OnAudioFocusChangeListener {

    MediaPlayer mediaPlayer;

    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }

    @Override
    public void onCreate () {
        initMediaPlayer();
    }

    @Override
    public void onDestroy () {
        if (mediaPlayer != null) mediaPlayer.release();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        // Nel caso in cui avviene un errore resetto il mediaplayer
        mediaPlayer.reset();
        return true;
    }

    private void initMediaPlayer() {
        String url = "http://217.27.88.204:8000";
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            Log.e("RCLService","Illegal argument exception");
            e.printStackTrace();
        } catch (SecurityException e) {
            Log.e("RCLService","Security exception");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e("RCLService","Illegal state exception");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("RCLService","IO exception");
            e.printStackTrace();
        }
    }

    public void onPrepared(MediaPlayer player) {
        player.start();
        dataSong myTask = new dataSong(this);
        Timer myTimer = new Timer();
        myTimer.schedule(myTask, 0, 15000); // aggiorno la notifica ogni 15 secondi
    }

    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

}
