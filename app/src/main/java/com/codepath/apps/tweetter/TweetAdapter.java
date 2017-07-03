package com.codepath.apps.tweetter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.codepath.apps.tweetter.TimelineActivity.REQUEST_CODE_DETAILS;
import static com.codepath.apps.tweetter.TimelineActivity.REQUEST_CODE_REPLY;
import static com.codepath.apps.tweetter.TimelineActivity.TWEET_POSITION_KEY;

/**
 * Created by kkong on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    static List<Tweet> mTweets;
    static Context context;
    static TwitterClient client;
    private LayoutInflater inflater;

    // Pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    // Constructor for onclicklistener
    public TweetAdapter(Context context, AdapterView.OnItemClickListener listener, List<Tweet> tweets) {
        inflater = LayoutInflater.from(context);
        this.mTweets = tweets;
    }

    // For each row, inflate layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        if(inflater == null) {
            inflater = LayoutInflater.from(context);
        }

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
        holder.tvUsername.setText(tweet.getUser().getName());
        holder.tvBody.setText(tweet.getBody());
        holder.tvScreenName.setText("@" + tweet.getUser().getScreenName() + " · " + TimeFormatter.getTimeDifference(tweet.getCreatedAt()));
        holder.tvReplyCount.setText("");
        holder.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));

        // Set the favorited status
        if(tweet.isFavorited()) {
            holder.ibFavorite.setImageResource(R.drawable.ic_unfavorite);
        } else {
            holder.ibFavorite.setImageResource(R.drawable.ic_favorite);
        }

        // If for some reason the favorite count is not working, set it to 1
        if(tweet.isFavorited() && tweet.getFavoriteCount() <= 0) {
            tweet.setFavoriteCount(1);
        }

        // Set the favorite count
        if(tweet.getFavoriteCount() > 0) {
            holder.tvFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
        } else {
            holder.tvFavoriteCount.setText("");
        }

        // Repeat with retweeted status
        if(tweet.isRetweeted()) {
            holder.ibRetweet.setImageResource(R.drawable.ic_unretweet);
        } else {
            holder.ibRetweet.setImageResource(R.drawable.ic_retweet);
        }

        if(tweet.isRetweeted() && tweet.getFavoriteCount() <= 0) {
            tweet.setRetweetCount(1);
        }

        if(tweet.getRetweetCount() > 0) {
            holder.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        } else {
            holder.tvRetweetCount.setText("");
        }

        // Load the profile image
        Glide.with(context)
                .load(tweet.getUser().getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                .into(holder.ivProfileImage);

        // Load any media images
        Glide.with(context)
                .load(tweet.getMedia().getMediaUrl())
                .bitmapTransform(new RoundedCornersTransformation(context, 5, 0))
                .into(holder.ivMediaImage);
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

    @Override
    public int getItemCount() {
        return mTweets.size();
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
        public ImageView ivMediaImage;
        public ImageButton ibMessage;
        public ImageButton ibRetweet;
        public ImageButton ibFavorite;
        public ImageButton ibDirectMessage;
        public View vDivider;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // Perform findViewById lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvReplyCount = (TextView) itemView.findViewById(R.id.tvReplyCount);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tvRetweetCount);
            tvFavoriteCount = (TextView) itemView.findViewById(R.id.tvFavoriteCount);
            ivMediaImage = (ImageView) itemView.findViewById(R.id.ivMediaImage);
            ibMessage = (ImageButton) itemView.findViewById(R.id.ibMessage);
            ibRetweet = (ImageButton) itemView.findViewById(R.id.ibRetweet);
            ibFavorite = (ImageButton) itemView.findViewById(R.id.ibFavorite);
            ibDirectMessage = (ImageButton) itemView.findViewById(R.id.ibDirectMessage);
            vDivider = (View) itemView.findViewById(R.id.vDivider);

            itemView.setOnClickListener(this);

            client = TwitterApp.getRestClient();
        }

        private void unRetweetTweet(final Tweet tweet, final int position) {
            client.unRetweet(tweet.getUid(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        Tweet newTweet = new Tweet(response);
                        if(newTweet.isRetweeted()) {
                            newTweet.setRetweeted(false);
                        }
                        if(newTweet.getRetweetCount() > tweet.getRetweetCount() - 1) {
                            newTweet.setRetweetCount(tweet.getRetweetCount() - 1);
                        }
                        mTweets.set(position, newTweet);
                        setRetweeted(newTweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Unable to unretweet", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                    Toast.makeText(context, "Unable to unretweet", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    throwable.printStackTrace();
                    Toast.makeText(context, "Unable to unretweet", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    throwable.printStackTrace();
                    Toast.makeText(context, "Unable to unretweet", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void retweetTweet(final Tweet tweet, final int position) {
            // Retweet the tweet
            client.retweet(tweet.getUid(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    try {
                        Tweet newTweet = new Tweet(response);
                        if(!newTweet.isRetweeted()) {
                            newTweet.setRetweeted(true);
                        }
                        if(newTweet.getRetweetCount() < tweet.getRetweetCount() + 1) {
                            newTweet.setRetweetCount(tweet.getRetweetCount() + 1);
                        }
                        mTweets.set(position, newTweet);
                        setRetweeted(newTweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Unable to retweet", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                    Toast.makeText(context, "Unable to retweet", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    throwable.printStackTrace();
                    Toast.makeText(context, "Unable to retweet", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    throwable.printStackTrace();
                    Toast.makeText(context, "Unable to retweet", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void unFavoriteTweet(final Tweet tweet, final int position) {
            client.unfavoriteTweet(tweet.getUid(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        Tweet newTweet = new Tweet(response);
                        if(newTweet.isFavorited()) {
                            newTweet.setFavorited(false);
                        }
                        if(newTweet.getFavoriteCount() > tweet. getFavoriteCount() - 1) {
                            newTweet.setFavoriteCount(tweet.getFavoriteCount() - 1);
                        }
                        mTweets.set(position, newTweet);
                        setFavorited(newTweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Unable to unfavorite", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                    Toast.makeText(context, "Unable to unfavorite", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    throwable.printStackTrace();
                    Toast.makeText(context, "Unable to unfavorite", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    throwable.printStackTrace();
                    Toast.makeText(context, "Unable to unfavorite", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void favoriteTweet(final Tweet tweet, final int position) {
            client.favoriteTweet(tweet.getUid(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        Tweet newTweet = new Tweet(response);
                        if(newTweet.isFavorited()) {
                            newTweet.setFavorited(false);
                        }
                        if(newTweet.getFavoriteCount() > tweet.getFavoriteCount() - 1) {
                            newTweet.setFavoriteCount(tweet.getFavoriteCount() - 1);
                        }
                        mTweets.set(position, newTweet);
                        setFavorited(newTweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Unable to favorite", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                    Toast.makeText(context, "Unable to favorite", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    throwable.printStackTrace();
                    Toast.makeText(context, "Unable to favorite", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    throwable.printStackTrace();
                    Toast.makeText(context, "Unable to favorite", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void setFavorited(Tweet tweet) {
            // Set the tweet favorited status
            if(tweet.isFavorited()) {
                ibFavorite.setImageResource(R.drawable.ic_unfavorite);
            } else {
                ibFavorite.setImageResource(R.drawable.ic_favorite);
            }
            // Set the number of favorited tweets
            if(tweet.getFavoriteCount() > 0) {
                tvFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
            } else {
                tvFavoriteCount.setText("");
            }
        }

        private void setRetweeted(Tweet tweet) {
            // Set the tweet retweeted status
            if(tweet.isRetweeted()) {
                ibRetweet.setImageResource(R.drawable.ic_unretweet);
            } else {
                ibRetweet.setImageResource(R.drawable.ic_retweet);
            }
            // Set the number of retweeted tweets
            if(tweet.getRetweetCount() > 0) {
                tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
            } else {
                tvRetweetCount.setText("");
            }
        }

        @OnClick(R.id.ibMessage)
        public void replyTweet() {
            final int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                // Get the tweet and redirect to the reply activity
                final Tweet tweet = mTweets.get(position);
                Intent i = new Intent(context, ReplyActivity.class);
                i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                ((AppCompatActivity)context).startActivityForResult(i, REQUEST_CODE_REPLY);
            }
        }

        @OnClick(R.id.ibRetweet)
        public void retweetTweet() {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Tweet tweet = mTweets.get(position);
                if(tweet.isRetweeted()) {
                    unRetweetTweet(tweet, position);
                } else {
                    retweetTweet(tweet, position);
                }
            }
        }

        @OnClick(R.id.ibFavorite)
        public void FavoriteTweet() {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Tweet tweet = mTweets.get(position);
                if(tweet.isFavorited()) {
                    unFavoriteTweet(tweet, position);
                } else {
                    favoriteTweet(tweet, position);
                }
            }
        }

        @OnClick(R.id.ibDirectMessage)
        public void directMessageTweet() {
            Toast.makeText(context, "DMing...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onClick(View v) {
            // Get the item position
            int position = getAdapterPosition();
            // Make sure the position is valid
            if(position != RecyclerView.NO_POSITION) {
                // Get the tweet at the location
                Tweet tweet = mTweets.get(position);
                // Create an intent to the TweetDetailsActivity
                Intent i = new Intent(context, TweetDetailsActivity.class);
                i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                i.putExtra(TWEET_POSITION_KEY, position);
                // Start the activity
                ((AppCompatActivity)context).startActivityForResult(i, REQUEST_CODE_DETAILS);
            }
        }
    }
}
