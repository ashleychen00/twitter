package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.codepath.apps.restclienttemplate.TweetAdapter.REPLY_STATUS_ID;
import static com.codepath.apps.restclienttemplate.TweetAdapter.REPLY_USERNAME;

public class TweetDetailsActivity extends AppCompatActivity {

    private TwitterClient client; // need to get replies

    private Tweet tweet; // the main tweet
    ImageView ivProfileImage;
    TextView tvUsername;
    TextView tvUserId;
    TextView tvTweet;
    TextView tvTime;
    TextView tvDate;
    ImageButton btReply;
    ImageView ivAttachedMedia;

//    TweetRepliesAdapter tweetRepliesAdapter;
//    ArrayList<Tweet> tweetReplies;
//    RecyclerView rvTweetReplies;
//
//    DividerItemDecoration itemDecor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        client = TwitterApp.getRestClient(this);

        // unwrap tweet parcel
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        // attach views to class fields + input information
        matchTweetViews();

        btReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = tweet.user.screenName;
                long tweet_id = tweet.uid;
                Intent intent = new Intent(TweetDetailsActivity.this, ComposeActivity.class);
                intent.putExtra(REPLY_USERNAME, username);
                intent.putExtra(REPLY_STATUS_ID, tweet_id);
                intent.putExtra("reply_code", TweetAdapter.reply_code);
                TweetDetailsActivity.this.startActivityForResult(intent, TweetAdapter.EDIT_REQUEST_CODE);
            }
        });

        if (tweet.attachedMediaUrl != null) {
            ivAttachedMedia.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(tweet.attachedMediaUrl)
                    .placeholder(R.drawable.ic_launcher)
                    .bitmapTransform(new RoundedCornersTransformation(this, 10, 5))
                    .into(ivAttachedMedia);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        this.finish();
        return true;
    }


    // helper functions

    private void getDateTime() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        try {
            Date date = sf.parse(tweet.createdAt);
            int date_number = date.getDate();
            int date_month = date.getMonth();

            int time_hours = date.getHours();
            int time_minutes = date.getMinutes(); // doesn't have am/pm option

            tvDate.setText(String.format("%s/%s", date_month, date_number));
            tvTime.setText(String.format("%s:%s", time_hours, time_minutes));

            Log.i("tweet details activity", String.format("date: %s, time: %s",
                                                                tvDate.getText().toString(),
                                                                tvTime.getText().toString()));
        } catch (ParseException e) {
            Log.e("tweet details activity", "error parsing tweet created at", e);
        }
    }

    private void matchTweetViews() {
        // attaching views
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvUsername = findViewById(R.id.tvUsername);
        tvTweet = findViewById(R.id.tvTweet);
        tvUserId = findViewById(R.id.tvUserId);
        tvTime = findViewById(R.id.tvTime);
        tvDate = findViewById(R.id.tvDate);
        btReply = findViewById(R.id.btReply);
        ivAttachedMedia = findViewById(R.id.ivAttachedMedia);

        // inputting information
        tvUsername.setText(tweet.user.name);
        tvTweet.setText(tweet.body);
        tvUserId.setText(tweet.user.screenName);

        // handling date + time
        getDateTime();

        // handle image
        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .placeholder(R.drawable.ic_launcher)
                .bitmapTransform(new RoundedCornersTransformation(this, 10, 5))
                .into(ivProfileImage);
    }


}
