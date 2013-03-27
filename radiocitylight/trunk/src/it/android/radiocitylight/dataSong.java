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

    public dataSong (Context mContext) {
        this.mContext = mContext;
    }

    @Override()
    public void run() {

        try {
            createNotification();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public void createNotification () throws IOException {
        String data = getSongData();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("RadioCityLight")
                        .setContentText(data)
                        .setOngoing(true);
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
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());
    }

}
