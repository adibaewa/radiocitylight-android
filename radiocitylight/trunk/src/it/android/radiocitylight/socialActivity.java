package it.android.radiocitylight;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class socialActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social);

        twitter mt= new twitter(this);
        mt.execute();
    }

    public class twitter extends AsyncTask <Void,Void,Integer> {

        String screenName = "radiocitylight";
        String consumerKey = "ccZjoML3hV2iJyTvs9fpQ";
        String consumerSecret = "5Wk3bfoaP3tMSrG5Xi8sHOO6xFstM9XM6g6O6wDKABA";
        String accessToken = "16282725-RKQwImMxesPfc77xIOMAATr2R1J5c7fjCMn4qEJnQ";
        String accessTokenSecret = "MNIRyVUwc7sOVTvbkUyM9SpSeqSv2618hQ8BpxqMk";

        List<twitter4j.Status> statuses;
        Context context;

        public twitter(Context context) {
            this.context = context;
        }

        @Override
        protected Integer doInBackground(Void... Params) {

            Integer res = 0;
            ConfigurationBuilder cb;
            TwitterFactory tf;
            Twitter twitter;

            cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(consumerKey)
                    .setOAuthConsumerSecret(consumerSecret)
                    .setOAuthAccessToken(accessToken)
                    .setOAuthAccessTokenSecret(accessTokenSecret);
            tf = new TwitterFactory(cb.build());
            twitter = tf.getInstance();

            try {
                statuses = twitter.getUserTimeline(screenName);
            } catch (TwitterException e) {
                e.printStackTrace();
                res = 1;
            }

            return res;
        }

        protected void onPostExecute(Integer res) {


            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
            pb.setVisibility(View.GONE);

            if (res == 0) {

                ListView tweetsView = (ListView) findViewById(R.id.tweets);
                List list = new LinkedList();
                for (twitter4j.Status status : statuses) {
                    list.add ( new tweet (status.getUser().getScreenName(),status.getText(),status.getCreatedAt()));
                }
                CustomAdapterOptimize adapter = new CustomAdapterOptimize(context, R.layout.rowcustom, list);
                tweetsView.setAdapter(adapter);

            }

        }


        public class tweet {

            private String screenName;
            private String text;
            private Date date;

            public tweet(String screenName, String text, Date date) {
                this.screenName = screenName;
                this.text = text;
                this.date = date;
            }

            public String getScreenName() {
                return screenName;
            }

            public void setScreenName(String screenName) {
                this.screenName = screenName;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public Date getDate() {
                return date;
            }

            public void setDate(Date date) {
                this.date = date;
            }

        }

        public class CustomAdapterOptimize extends ArrayAdapter<tweet> {

            public CustomAdapterOptimize(Context context, int textViewResourceId,
                                         List<tweet> objects) {
                super(context, textViewResourceId, objects);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return getViewOptimize(position, convertView, parent);
            }

            public View getViewOptimize(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder = null;
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.rowcustom, null);
                    viewHolder = new ViewHolder();
                    viewHolder.name = (TextView)convertView.findViewById(R.id.textViewUser);
                    viewHolder.text = (TextView)convertView.findViewById(R.id.textViewTweet);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                tweet t = getItem(position);
                String temp = t.getDate().toString();
                temp = temp.substring(0,temp.length()-15);
                viewHolder.name.setText(temp);
                viewHolder.text.setText(t.getText());
                return convertView;
            }

            private class ViewHolder {
                public TextView name;
                public TextView text;
            }
        }

    }

}