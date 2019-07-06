package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.codepath.apps.restclienttemplate.TweetAdapter.REPLY_STATUS_ID;
import static com.codepath.apps.restclienttemplate.TweetAdapter.REPLY_USERNAME;

public class TweetRepliesAdapter extends RecyclerView.Adapter<TweetRepliesAdapter.ViewHolder>{

    ArrayList<Tweet> mTweetReplies;
    Context context;
    int reply_code = 10;

    public TweetRepliesAdapter(ArrayList<Tweet> replies) {
        mTweetReplies = replies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, viewGroup, false);
        TweetRepliesAdapter.ViewHolder viewHolder = new TweetRepliesAdapter.ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Tweet tweet = mTweetReplies.get(i);

        // populate data into the viewholder
        viewHolder.tvBody.setText(tweet.body);
        viewHolder.tvUsername.setText(Html.fromHtml(String.format("<b>%s</b>", tweet.user.name)));
        viewHolder.tvRelativeTime.setText(tweet.relativeTime);

        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .placeholder(R.drawable.ic_launcher)
                .bitmapTransform(new RoundedCornersTransformation(context, 5, 0))
                .into(viewHolder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweetReplies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvRelativeTime;
        public Button btReplyShortcut;

        public ViewHolder(View itemView) {
            super(itemView);

            // add info into the sub-views
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUserName);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            btReplyShortcut = itemView.findViewById(R.id.btReplyShortcut);

            btReplyShortcut.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) { // no_position = -1, but better not to hard code
                        // means position is valid
                        Tweet tweet = mTweetReplies.get(position);
                        String username = tweet.user.screenName;
                        long tweet_id = tweet.uid;
                        Intent intent = new Intent(context, ComposeActivity.class);
                        intent.putExtra(REPLY_USERNAME, username);
                        intent.putExtra(REPLY_STATUS_ID, tweet_id);
                        intent.putExtra("reply_code", reply_code);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
