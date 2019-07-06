package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    Context context;

    // pass in tweets array in constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    static String REPLY_STATUS_ID = "status_id";
    static String REPLY_USERNAME = "username";
    public static int reply_code = 10;
    public static final int EDIT_REQUEST_CODE = 20;

    // for each row, inflate layout and cache references into viewholder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind values based on position of element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        // get data according to position
        Tweet tweet = mTweets.get(i);

        // populate data into the viewholder
        viewHolder.tvBody.setText(tweet.body);
        viewHolder.tvUsername.setText(Html.fromHtml(String.format("<b>%s</b>", tweet.user.name)));
        viewHolder.tvRelativeTime.setText(tweet.relativeTime);

        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .placeholder(R.drawable.ic_launcher)
                .bitmapTransform(new RoundedCornersTransformation(context, 5, 0))
                .into(viewHolder.ivProfileImage);

        if (tweet.attachedMediaUrl != null) {
            viewHolder.ivAttachedMedia.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(tweet.attachedMediaUrl)
                    .placeholder(R.drawable.ic_launcher)
                    .bitmapTransform(new RoundedCornersTransformation(context, 10, 5))
                    .into(viewHolder.ivAttachedMedia);
        }

    }

    // create viewholder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvRelativeTime;
        public ImageButton btReplyShortcut;
        public ImageView ivAttachedMedia;

        public ViewHolder(View itemView) {
            super(itemView);

            // add info into the sub-views
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUserName);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            btReplyShortcut = itemView.findViewById(R.id.btReplyShortcut);
            ivAttachedMedia = itemView.findViewById(R.id.ivAttachedMedia);

            btReplyShortcut.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) { // no_position = -1, but better not to hard code
                        // means position is valid
                        Tweet tweet = mTweets.get(position);
                        String username = tweet.user.screenName;
                        long tweet_id = tweet.uid;
                        Intent intent = new Intent(context, ComposeActivity.class);
                        intent.putExtra(REPLY_USERNAME, username);
                        intent.putExtra(REPLY_STATUS_ID, tweet_id);
                        intent.putExtra("reply_code", reply_code);
                        ((Activity) context).startActivityForResult(intent, EDIT_REQUEST_CODE);
                    }
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.i("tweet adapter", "onclick");
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) { // no_position = -1, but better not to hard code
                // means position is valid
                Tweet tweet = mTweets.get(position);
                Intent intent = new Intent(context, TweetDetailsActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                context.startActivity(intent);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

}
