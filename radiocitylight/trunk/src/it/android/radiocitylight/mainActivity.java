package it.android.radiocitylight;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class mainActivity extends Activity{

    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        intent = new Intent(this, service.class);
    }

    public void onToggleClicked (View view) {

        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            new serviceCheck().execute();
        } else {
            stopService(intent);
            unregisterReceiver(broadcastReceiver);
            TextView dataSongTxt = (TextView) findViewById(R.id.dataSongTxt);
            dataSongTxt.setText("Nessuna riproduzione");
        }
    }

    public void displayErrorDialog (String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("RadioCityLight");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Chiudi",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                dialog.dismiss();
                mainActivity.this.finish();
            }
        });
        builder.show();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {
        String song = intent.getStringExtra("dataSongAuthor");
        TextView dataSongTxt = (TextView) findViewById(R.id.dataSongTxt);
        dataSongTxt.setText(song);
    }

    private class serviceCheck extends AsyncTask<Void, Integer, Integer> {

        protected Integer doInBackground(Void... voids) {

            Integer code = -1;

            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {

                try {
                    URL url = new URL("http://217.27.88.204:8000/7.html");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setRequestProperty("User-Agent", "Mozilla/5.001 (windows; U; NT4.0; en-US; rv:1.0) Gecko/25250101");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(5000); // Thirty seconds timeout in milliseconds
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) { // Good response
                        code = 0;
                    } else { // Anything else is unwanted
                        code = 1;
                    }
                } catch (IOException e) {
                    code = 2;
                }
            }
            return code;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Integer result) {
            if (result > 0) displayErrorDialog("Ops! Il servizio non è momentaneamente disponibile. Riprovare più tardi.");
            if (result < 0) displayErrorDialog("Connessione ad internet assente. Assicurarsi di essere connessi e riprovare.");
            if (result == 0) {
                startService(intent);
                registerReceiver(broadcastReceiver, new IntentFilter(service.BROADCAST_ACTION));
            }
        }
    }

    /** Called when the user clicks the Send button */
    public void displaySocialActivity() {
        Intent intent = new Intent(this, socialActivity.class);
        startActivity(intent);
    }

}
