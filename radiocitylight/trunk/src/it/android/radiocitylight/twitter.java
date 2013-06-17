package it.android.radiocitylight;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class twitter {

    String screenName = "radiocitylight";
    String consumerKey = "ccZjoML3hV2iJyTvs9fpQ";
    String consumerSecret = "5Wk3bfoaP3tMSrG5Xi8sHOO6xFstM9XM6g6O6wDKABA";
    String accessToken = "16282725-RKQwImMxesPfc77xIOMAATr2R1J5c7fjCMn4qEJnQ";
    String accessTokenSecret = "MNIRyVUwc7sOVTvbkUyM9SpSeqSv2618hQ8BpxqMk";

    ConfigurationBuilder cb;
    TwitterFactory tf;
    Twitter twitter;


    public twitter() {
        cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
        tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }

    public List<Status> getTimeLine () {
        //Twitter twitter = TwitterFactory.getSingleton();
        List<Status> statuses = null;
        try {
            statuses = twitter.getUserTimeline(screenName);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return statuses;
    }


}
