package it.android.radiocitylight;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.io.IOException;

public class service extends Service {

    radio myRadio;
    NotificationManager notificationManager;
    private final Handler handler = new Handler();
    public static final String BROADCAST_ACTION = "it.android.radiocitylight.displaysongdata";
    Intent intent;
    boolean run = false;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate () {
        myRadio = new radio();
        myRadio.play();
        run = true;
        intent = new Intent(BROADCAST_ACTION);
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 0);
    }

    @Override
    public void onDestroy () {
        myRadio.stop();
        run = false;
        deleteNotification();                    // Elimino la notifica
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (run) {
                try {
                    myRadio.getSongData();
                    createNotification();
                    displayDataSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, 2000);
            }
        }
    };

    /* Visualizza la notifica */
    public void createNotification () throws IOException {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)  // Icona
                        .setContentTitle("RadioCityLight")     // Titolo
                        .setContentText(myRadio.songData)                  // Testo
                        .setOngoing(true);

        Intent notifyIntent = new Intent(this, mainActivity.class);
        notifyIntent.setFlags(notifyIntent.FLAG_ACTIVITY_NEW_TASK | notifyIntent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent nIntent = PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(nIntent);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());
    }

    /* Cancella la notifica */
    public void deleteNotification () {
        notificationManager.cancel(1);
    }

    private void displayDataSong() {
        intent.putExtra("dataSongAuthor",myRadio.songData);
        sendBroadcast(intent);
    }

}
