package it.android.radiocitylight;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/*
 * AVVIO E ARRESTO DEL SERVIZIO
 * startService(new Intent(this, RCLService.class));
 * stopService(new Intent(this, RCLService.class));
 */

public class streamService extends Service implements OnPreparedListener, OnErrorListener, AudioManager.OnAudioFocusChangeListener {

    MediaPlayer mediaPlayer;
    NotificationManager notificationManager;

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
        notificationManager.cancel(1);
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
        try {
            createNotification ();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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

    public void createNotification () throws IOException {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("RadioCityLight")
                        .setContentText(getSongData())
                        .setOngoing(true);
        Intent resultIntent = new Intent(this, mainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(mainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());
    }

    public String getSongData () throws IOException {
        URL url = new URL("http://217.27.88.204:8000/7.html");
        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
        urlc.setRequestProperty("User-Agent", "Mozilla/5.001 (windows; U; NT4.0; en-US; rv:1.0) Gecko/25250101");
        urlc.setRequestProperty("Connection", "close");
        urlc.setConnectTimeout(5000); // Thirty seconds timeout in milliseconds
        urlc.setRequestMethod("GET");
        urlc.setDoInput(true);
        urlc.connect();
        InputStream is = urlc.getInputStream();
        String contentAsString = readIt(is);
        is.close();
        return contentAsString;
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[500];
        reader.read(buffer);
        String htmlContent = new String (buffer);
        Integer start = htmlContent.indexOf("<body>") + 6;
        Integer end = htmlContent.indexOf("</body>");
        htmlContent = htmlContent.substring(start,end);
        String songData[] = htmlContent.split(",");
        return songData[6].toUpperCase();
    }
}
