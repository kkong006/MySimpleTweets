package com.codepath.apps.tweetter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetter.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by kkong on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    static List<Tweet> mTweets;
    static Context context;

    // Pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    // For each row, inflate layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }


    // Bind the values based on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data according to position
        Tweet tweet = mTweets.get(position);

        // Populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvScreenName.setText("@" + tweet.user.screenName + " · " + TimeFormatter.getTimeDifference(tweet.createdAt));
        holder.tvReplyCount.setText("");
        holder.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
        holder.tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));

        // Load the profile image
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                .into(holder.ivProfileImage);

        // Remove the dividing line on the last row
        if(position == getItemCount() - 1) {
            holder.vDivider.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    // Create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvScreenName;
        public TextView tvReplyCount;
        public TextView tvRetweetCount;
        public TextView tvFavoriteCount;
        public ImageButton ibMessage;
        public ImageButton ibRetweet;
        public ImageButton ibFavorite;
        public ImageButton ibDirectMessage;
        public View vDivider;

        public ViewHolder(View itemView) {
            super(itemView);
            // Perform findViewById lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvReplyCount = (TextView) itemView.findViewById(R.id.tvReplyCount);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tvRetweetCount);
            tvFavoriteCount = (TextView) itemView.findViewById(R.id.tvFavoriteCount);
            ibMessage = (ImageButton) itemView.findViewById(R.id.ibMessage);
            ibRetweet = (ImageButton) itemView.findViewById(R.id.ibRetweet);
            ibFavorite = (ImageButton) itemView.findViewById(R.id.ibFavorite);
            ibDirectMessage = (ImageButton) itemView.findViewById(R.id.ibDirectMessage);
            vDivider = (View) itemView.findViewById(R.id.vDivider);

            itemView.setOnClickListener(this);

            ibMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Messaging...", Toast.LENGTH_SHORT).show();

                }
            });

            ibRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Retweeting...", Toast.LENGTH_SHORT).show();
                }
            });

            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Favoriting...", Toast.LENGTH_SHORT).show();
                }
            });

            ibDirectMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "DMing...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            // Get the item position
            int position = getAdapterPosition();
            // Make sure the position is valid
            if(position != RecyclerView.NO_POSITION) {
//                switch(v.getId()) {
//                    case(R.id.ibMessage):
//                        Toast.makeText(context, "Messaging...", Toast.LENGTH_SHORT).show();
//
//                        break;
//                    case(R.id.ibRetweet):
//                        Toast.makeText(context, "Retweeting...", Toast.LENGTH_SHORT).show();
//                        break;
//                    case(R.id.ibFavorite):
//                        Toast.makeText(context, "Favoriting...", Toast.LENGTH_SHORT).show();
//                        break;
//                    case(R.id.ibDirectMessage):
//                        Toast.makeText(context, "DMing...", Toast.LENGTH_SHORT).show();
//                        break;
//                    case(R.id.llButtonRow):
//                        Toast.makeText(context, "Button Row...", Toast.LENGTH_SHORT).show();
//                        break;
//                    default:
                        // Get the tweet at the location
                        Tweet tweet = mTweets.get(position);
                        // Create an intent to the TweetDetailsActivity
                        Intent i = new Intent(context, TweetDetailsActivity.class);
                        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                        // Start the activity
                        context.startActivity(i);
//                }
            }
        }
    }
}
