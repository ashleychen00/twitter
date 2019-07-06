package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Parcel
public class Tweet {

    // list out attributes
    public String body;
    public long uid; // database ID for tweet
    public String createdAt;
    public String relativeTime;
    public String attachedMediaUrl;

    public User user;

    public Tweet() {}

    // deserialize the data: given JSONObject, returns a Tweet object
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // extract values from JSON
        tweet.body = jsonObject.optString("full_text", null);
        if (tweet.body == null) {
            tweet.body = jsonObject.getString("text");
        }
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.relativeTime = getRelativeTimeAgo(tweet.createdAt);

        JSONObject entities = jsonObject.getJSONObject("entities"); // how to test...
        Log.i("tweet", entities.toString());
        // JSONArray urls = entities.getJSONArray("urls");
        if (entities.has("media")) {
             JSONArray media_array = entities.getJSONArray("media");
             JSONObject media = media_array.getJSONObject(0); // takes first object
             tweet.attachedMediaUrl = media.getString("media_url_https");
            Log.i("tweet", String.format("tweet from %s has media: %s", tweet.user, tweet.attachedMediaUrl));
        }
        return tweet;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
