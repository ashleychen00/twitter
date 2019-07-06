package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    // track edit text
    EditText etComposeTweet;
    // track character count edit text
    TextView tvCharCount;

    // twitter client to post tweet (not sent via parceler?)
    private TwitterClient client;

    // reply info
    String handle;
    long tweet_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        etComposeTweet = findViewById(R.id.etComposeTweet);
        tvCharCount = findViewById(R.id.tvCharCount);

        if (getIntent().getIntExtra("reply_code", 20) == 10) {
            handle = "@" + getIntent().getStringExtra(TweetAdapter.REPLY_USERNAME);
            tweet_id = getIntent().getLongExtra(TweetAdapter.REPLY_STATUS_ID, 0);
            etComposeTweet.setText(handle);
        }

        etComposeTweet.addTextChangedListener(new TextWatcher() { // IMPLEMENT METHODS

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // doesn't do anything
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // doesn't do anything
            }

            @Override
            public void afterTextChanged(Editable s) {
                int charCount = etComposeTweet.getText().toString().length();
                tvCharCount.setText(String.format("%s left", 280 - charCount));
            }
        });

        getSupportActionBar().setTitle("Compose Tweet");

    }

    public void onPostTweet(View view) {
        String newPost = etComposeTweet.getText().toString();

        client.sendTweet(tweet_id, newPost, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(ComposeActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
                // response is the JSONObject of the tweet
                try {
                    Tweet tweet = new Tweet().fromJSON(response);

                    // needs to take intent to go back to timeline activity
                    Intent intent = new Intent(ComposeActivity.this, TimelineActivity.class);
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    setResult(RESULT_OK, intent);
                    finish();

                } catch (JSONException e) {
                    Log.e("compose activity", "parsing posted tweet response failure", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                Log.e("compose activity", "posting tweet failed", throwable);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection -- only cancel button can be pressed so ?
        Intent intent = new Intent(ComposeActivity.this, TimelineActivity.class);
        setResult(RESULT_CANCELED, intent);
        finish();
        return true;
    }

//    public void onCancel(View view) { // should've been menu item parameter after moved x to menu
//        Intent intent = new Intent(ComposeActivity.this, TimelineActivity.class);
//        setResult(RESULT_CANCELED, intent);
//        finish();
//    }
}
