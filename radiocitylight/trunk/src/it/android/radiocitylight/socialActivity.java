package it.android.radiocitylight;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import twitter4j.Status;

public class socialActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social);

        twitter myTwitter = new twitter();
        List<Status> statuses = myTwitter.getTimeLine();
        ListView tweetsView = (ListView) findViewById(R.id.tweets);


        List list = new LinkedList();
        for (Status status : statuses) {
            list.add ( new tweet (status.getUser().getScreenName(),status.getText(),status.getCreatedAt()));
        }
        CustomAdapterOptimize adapter = new CustomAdapterOptimize(this, R.layout.rowcustom, list);
        tweetsView.setAdapter(adapter);



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
            viewHolder.name.setText(t.getScreenName() + t.getDate().toString());
            viewHolder.text.setText(t.getText());
            return convertView;
        }

        private class ViewHolder {
            public TextView name;
            public TextView text;
        }
    }
}