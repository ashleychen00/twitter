package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    // pull to refresh swipe container feature
    private SwipeRefreshLayout swipeContainer;

    // numeric code to identify edit activity
    public static final int EDIT_REQUEST_CODE = 20;

    private TwitterClient client;

    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;

    DividerItemDecoration itemDecor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);

        // find swipe container in xml layout file
        swipeContainer = findViewById(R.id.swipeContainer);
        // set up refresh listener
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                populateTimeline();
            }
        });
        // set refresh colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright);

        // find recyclerview
        rvTweets = findViewById(R.id.rvTweet);

        // init array list
        tweets = new ArrayList<>();

        // construct adapter from datasource
        tweetAdapter = new TweetAdapter(tweets);

        // set up recyclerview (layout manager, connect with adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        // set adapter
        rvTweets.setAdapter(tweetAdapter);

        populateTimeline();
    }

    private void populateTimeline() { // edited to include refresh
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // iterate through array, deserialize each object, convert to tweet, add to list, notify adapter
                clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1); // don't need tweetAdapter.addAll?
                        swipeContainer.setRefreshing(false);

                        if (itemDecor != null) {
                            rvTweets.removeItemDecoration(itemDecor);
                        }

                        itemDecor = new DividerItemDecoration(TimelineActivity.this, DividerItemDecoration.VERTICAL);
                        rvTweets.addItemDecoration(itemDecor);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    public void onComposeTweet(MenuItem mi) {
        // open a new compose activity via intent
        Intent intent = new Intent(this, ComposeActivity.class);
        intent.putExtra("reply_code", 20); // to separate from replying to a tweet
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check request code and result code first
        if (resultCode == RESULT_OK) {
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            Log.d("timeline activity", String.format("%s", tweet.body));
            // insert tweet into arraylist
            tweets.add(0, tweet);
            // notify adapter has changed
            tweetAdapter.notifyItemInserted(0);
            // scroll list back to top:
            rvTweets.scrollToPosition(0);
        }
    }

    public void clear() {
        // clears all elements of underlying data set
        tweets.clear();
        tweetAdapter.notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        tweetAdapter.notifyDataSetChanged();
    }
}
