package it.android.radiocitylight;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimerTask;


class dataSong extends TimerTask{

    Context mContext;
    NotificationManager notificationManager;

    /* Costruttore */
    public dataSong (Context mContext) {
        this.mContext = mContext; // prendo il contesto da mainActivity per istanziare la notifica
    }

    /* Metodo lanciato dal timerTask */
    @Override()
    public void run() {

        try {
            createNotification();  // Creo una nuova notifica
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /* Legge la pagina 7.html dal server shoutcast */
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

    /* Prende in input il codice html della pagina 7.html e ne estrapola artista e titolo della cazone */
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

    /* Visualizza la notifica */
    public void createNotification () throws IOException {

        /* Creo la notifica */
        String data = getSongData();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_launcher)  // Icona
                        .setContentTitle("RadioCityLight")     // Titolo
                        .setContentText(data)                  // Testo
                        .setOngoing(true);

        /* Aggiungo alla notifica la possibilità di tornare alla mainActivity con un tocco */
        Intent resultIntent = new Intent(mContext, mainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(mainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        /* Visualizzo la notifica */
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());
    }

    /* Cancella la notifica */
    public void deleteNotification () {
        notificationManager.cancel(1);
    }

}
