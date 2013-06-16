package it.android.radiocitylight;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class radio implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    MediaPlayer mediaPlayer;
    String url;
    String songData;

    public radio() {
        url = "http://217.27.88.204:8000";
    }

    public void play () {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            Log.e("RCLService", "Illegal argument exception");
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
        player.start();                     // Avvio il mediaPlayer
    }

    public void stop () {
        if (mediaPlayer != null) mediaPlayer.release(); // Stoppo il mediaPlayer
    }

    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) play();
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

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediaPlayer.reset();
        return true;
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
        songData = contentAsString;
        return contentAsString;
    }

    private String readIt(InputStream stream) throws IOException {
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
