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

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.codepath.apps.tweetter.TimelineActivity.REQUEST_CODE_REPLY;

/**
 * Created by kkong on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    List<Tweet> mTweets;
    static Context context;
    TwitterClient client;
    LayoutInflater inflater;
    private TweetAdapterListener mListener;

    // Desfine an interface required by the ViewHolder
    public interface TweetAdapterListener {
        public void onItemSelected(View view, int position);
    }

    // Pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets, TweetAdapterListener listener) {
        mTweets = tweets;
        mListener = listener;
    }

    // TODO: move intent from holder to TimelineActivity
    // Constructor for onclicklistener
    public TweetAdapter(Context context, AdapterView.OnItemClickListener listener, List<Tweet> tweets) {
        inflater = LayoutInflater.from(context);
        this.mTweets = tweets;
//        this.onItemClickListener = listener;
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
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvScreenName.setText("@" + tweet.user.screenName + " · " + TimeFormatter.getTimeDifference(tweet.createdAt));
        holder.tvReplyCount.setText("");
        holder.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));

        // Set the favorited status
        if(tweet.favorited) {
            holder.ibFavorite.setImageResource(R.drawable.ic_unfavorite);
        } else {
            holder.ibFavorite.setImageResource(R.drawable.ic_favorite);
        }

        // If for some reason the favorite count is not working, set it to 1
        if(tweet.favorited && tweet.favoriteCount <= 0) {
            tweet.favoriteCount = 1;
        }

        // Set the favorite count
        if(tweet.favoriteCount > 0) {
            holder.tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
        } else {
            holder.tvFavoriteCount.setText("");
        }

        if(tweet.retweeted) {
            holder.ibRetweet.setImageResource(R.drawable.ic_unretweet);
        } else {
            holder.ibRetweet.setImageResource(R.drawable.ic_retweet);
        }

        if(tweet.retweetCount > 0) {
            holder.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
        } else {
            holder.tvRetweetCount.setText("");
        }

        // Load the profile image
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 150, 0))
                .into(holder.ivProfileImage);

        Glide.with(context)
                .load(tweet.media.getMediaUrl())
                .bitmapTransform(new RoundedCornersTransformation(context, 5, 0))
                .into(holder.ivMediaImage);

//        // Remove the dividing line on the last row
//        if(position == getItemCount() - 1) {
//            holder.vDivider.setVisibility(View.INVISIBLE);
//        }
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageButton ivProfileImage;
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
            // Perform findViewById lookups
            ivProfileImage = (ImageButton) itemView.findViewById(R.id.ivProfileImage);
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

//            final TwitterClient client;
            client = TwitterApp.getRestClient();

//            // Handle row click event
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });

            ibMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        final Tweet tweet = mTweets.get(position);
                        Intent i = new Intent(context, ReplyActivity.class);
                        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                        ((AppCompatActivity)context).startActivityForResult(i, REQUEST_CODE_REPLY);
                    }
                }
            });

            ibRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        final Tweet tweet = mTweets.get(position);
                        if(tweet.retweeted) {
                            // Unretweet the tweet
                            client.unRetweet(tweet.uid, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    super.onSuccess(statusCode, headers, response);
                                    try {
                                        Tweet newTweet = Tweet.fromJSON(response);
                                        if(newTweet.retweeted) {
                                            newTweet.retweeted = false;
                                        }
                                        if(newTweet.retweetCount > tweet.retweetCount - 1) {
                                            newTweet.retweetCount = tweet.retweetCount - 1;
                                        }
                                        mTweets.set(position, newTweet);
                                        setRetweeted(newTweet);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                    Toast.makeText(context, "Unable to unretweet", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                    Toast.makeText(context, "Unable to unretweet", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    super.onFailure(statusCode, headers, responseString, throwable);
                                    Toast.makeText(context, "Unable to unretweet", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Retweet the tweet
                            client.retweet(tweet.uid, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    super.onSuccess(statusCode, headers, response);

                                    try {
                                        Tweet newTweet = Tweet.fromJSON(response);
                                        if(!newTweet.retweeted) {
                                            newTweet.retweeted = true;
                                        }
                                        if(newTweet.retweetCount < tweet.retweetCount + 1) {
                                            newTweet.retweetCount = tweet.retweetCount + 1;
                                        }
                                        mTweets.set(position, newTweet);
                                        setRetweeted(newTweet);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                    Toast.makeText(context, "Unable to retweet", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                    Toast.makeText(context, "Unable to retweet", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    super.onFailure(statusCode, headers, responseString, throwable);
                                    Toast.makeText(context, "Unable to retweet", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });

            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        final Tweet tweet = mTweets.get(position);
                        if(tweet.favorited) {
                            client.unfavoriteTweet(tweet.uid, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    super.onSuccess(statusCode, headers, response);
                                    try {
                                        Tweet newTweet = Tweet.fromJSON(response);
                                        mTweets.set(position, newTweet);
                                        setFavorited(newTweet);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                    Toast.makeText(context, "Unable to favorite", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                    Toast.makeText(context, "Unable to favorite", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    super.onFailure(statusCode, headers, responseString, throwable);
                                    Toast.makeText(context, "Unable to favorite", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            client.favoriteTweet(tweet.uid, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    super.onSuccess(statusCode, headers, response);
                                    try {
                                        Tweet newTweet = Tweet.fromJSON(response);
                                        mTweets.set(position, newTweet);
                                        setFavorited(newTweet);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                    Toast.makeText(context, "Unable to unfavorite", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                    Toast.makeText(context, "Unable to unfavorite", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    super.onFailure(statusCode, headers, responseString, throwable);
                                    Toast.makeText(context, "Unable to unfavorite", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });

            ibDirectMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "DMing...", Toast.LENGTH_SHORT).show();
                }
            });

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Fire an intent at profile activity
                    final int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        Tweet tweet = mTweets.get(position);
                        Intent i = new Intent(context, ProfileActivity.class);
                        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                        ((AppCompatActivity)context).startActivity(i);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            // Get the item position

            if(mListener != null) {
                // Get the position of the row element
                int position = getAdapterPosition();
                // Fire the listener callback
                mListener.onItemSelected(v, position);
            }
//            // Make sure the position is valid
//            if(position != RecyclerView.NO_POSITION) {
//                // Get the tweet at the location
//                Tweet tweet = mTweets.get(position);
//                // Create an intent to the TweetDetailsActivity
//                Intent i = new Intent(context, TweetDetailsActivity.class);
//                i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
//                i.putExtra(TWEET_POSITION_KEY, position);
//                // Start the activity
//                ((AppCompatActivity)context).startActivityForResult(i, REQUEST_CODE_DETAILS);
//            }
        }

        public void setFavorited(Tweet tweet) {
            // Set the tweet favorited status
            if(tweet.favorited) {
                ibFavorite.setImageResource(R.drawable.ic_unfavorite);
            } else {
                ibFavorite.setImageResource(R.drawable.ic_favorite);
            }
            // Set the number of favorited tweets
            if(tweet.favoriteCount > 0) {
                tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
            } else {
                tvFavoriteCount.setText("");
            }
        }

        public void setRetweeted(Tweet tweet) {
            // Set the tweet retweeted status
            if(tweet.retweeted) {
                ibRetweet.setImageResource(R.drawable.ic_unretweet);
            } else {
                ibRetweet.setImageResource(R.drawable.ic_retweet);
            }
            // Set the number of retweeted tweets
            if(tweet.retweetCount > 0) {
                tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
            } else {
                tvRetweetCount.setText("");
            }
        }
    }
}
