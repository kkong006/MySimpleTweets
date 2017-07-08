package com.codepath.apps.tweetter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetter.R;
import com.codepath.apps.tweetter.models.Tweet;
import com.codepath.apps.tweetter.sync.TwitterApp;
import com.codepath.apps.tweetter.sync.TwitterClient;
import com.codepath.apps.tweetter.utilities.TimeFormatter;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.codepath.apps.tweetter.R.id.ibFavoriteDetails;
import static com.codepath.apps.tweetter.activities.TimelineActivity.REQUEST_CODE_REPLY;
import static com.codepath.apps.tweetter.activities.TimelineActivity.TWEET_POSITION_KEY;

public class TweetDetailsActivity extends AppCompatActivity {

    @BindView(R.id.ivProfileImageDetails) ImageView ivProfileImage;
    @BindView(R.id.tvUserNameDetails) TextView tvUserName;
    @BindView(R.id.tvScreenNameDetails) TextView tvScreenName;
    @BindView(R.id.tvTimeStampDetails) TextView tvTimeStamp;
    @BindView(R.id.tvTweetTextDetails) TextView tvTweetBody;
    @BindView(R.id.tvNumRetweetsDetails) TextView tvNumRetweets;
    @BindView(R.id.tvNumLikesDetails) TextView tvNumFavorites;
    @BindView(ibFavoriteDetails) ImageView ibFavorited;
    @BindView(R.id.ibRetweetDetails) ImageView ibRetweeted;
    @BindView(R.id.ivMediaImageDetails) ImageView ivMediaImage;

    Tweet tweet;
    TwitterClient client;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.bind(this);
        client = TwitterApp.getRestClient();

        // Set up the action bar
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.actionbar_custom, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.actionbar_title);
        mTitleTextView.setText("Details");

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        // Deserialize the tweet and position
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        position = getIntent().getIntExtra(TWEET_POSITION_KEY, 0);

        populateDetails();

        setFavorited();
        setRetweeted();
    }

    public void populateDetails() {
        // Populate the views according to this data
        tvUserName.setText(tweet.user.name);
        tvTweetBody.setText(tweet.body);
        tvScreenName.setText("@" + tweet.user.screenName);
        tvTimeStamp.setText(TimeFormatter.getTimeStamp(tweet.getCreatedAt()));

        // Load the profile image
        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(this, 150, 0))
                .into(ivProfileImage);

        // Load the media image
        Glide.with(this)
                .load(tweet.media.getMediaUrl())
                .bitmapTransform(new RoundedCornersTransformation(this, 5, 0))
                .into(ivMediaImage);
    }

    public void setFavorited() {
        // Set the tweet favorited status
        if(tweet.favorited) {
            ibFavorited.setImageResource(R.drawable.ic_unfavorite);
        } else {
            ibFavorited.setImageResource(R.drawable.ic_favorite);
        }
        // Set the number of favorites
        if(tweet.favoriteCount > 0) {
            tvNumFavorites.setText(String.valueOf(tweet.favoriteCount));
        } else {
            tvNumFavorites.setText("");
        }
    }

    public void setRetweeted() {
        // Set the retweeted status
        if(tweet.retweeted) {
            ibRetweeted.setImageResource(R.drawable.ic_unretweet);
        } else {
            ibRetweeted.setImageResource(R.drawable.ic_retweet);
        }
        // Set the number of retweets
        if(tweet.retweetCount > 0) {
            tvNumRetweets.setText(String.valueOf(tweet.retweetCount));
        } else {
            tvNumRetweets.setText("");
        }
    }

    @OnClick(R.id.ibMessageDetails)
    public void putReply() {
        // Send the new tweet back to reply
        Intent i = new Intent(this, ReplyActivity.class);
        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        startActivityForResult(i, REQUEST_CODE_REPLY);
    }

    @OnClick(R.id.ibRetweetDetails)
    public void putRetweet() {
        if(tweet.retweeted) {
            unretweetTweet();
        } else {
            retweetTweet();
        }
    }

    @OnClick(R.id.ibFavoriteDetails)
    public void putFavorite() {
        if(tweet.favorited) {
            unfavoriteTweet();
        } else {
            favoriteTweet();
        }
    }

    public void retweetTweet() {
        client.retweet(tweet.uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Tweet newTweet = Tweet.fromJSON(response);
                    tweet.retweeted = true;
                    if(tweet.retweetCount < newTweet.retweetCount) {
                        tweet.retweetCount = newTweet.retweetCount;
                    }
                    setRetweeted();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Unable to retweet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to retweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to retweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to retweet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void unretweetTweet() {
        client.unRetweet(tweet.uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Tweet newTweet = Tweet.fromJSON(response);
                    tweet.retweeted = false;
                    if(tweet.retweetCount > newTweet.retweetCount) {
                        tweet.retweetCount = newTweet.retweetCount;
                    }
                    setRetweeted();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Unable to unretweet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to unretweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to unretweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to unretweet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void favoriteTweet() {
        client.favoriteTweet(tweet.uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    // Endpoint returns original tweet; update the tweet
                    int oldFavoriteCount = tweet.favoriteCount;
                    tweet = Tweet.fromJSON(response);
                    if(!tweet.favorited) {
                        tweet.favorited = true;
                    }
                    if(tweet.favoriteCount < oldFavoriteCount + 1) {
                        tweet.favoriteCount = oldFavoriteCount + 1;
                    }
                    setFavorited();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void unfavoriteTweet() {
        client.unfavoriteTweet(tweet.uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    // Endpoint returns original tweet; update the tweet
                    int oldFavoriteCount = tweet.favoriteCount;
                    tweet = Tweet.fromJSON(response);
                    if(tweet.favorited) {
                        tweet.favorited = false;
                    }
                    if(tweet.favoriteCount > oldFavoriteCount - 1) {
                        tweet.favoriteCount = oldFavoriteCount - 1;
                    }
                    setFavorited();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Return to the calling activity, send the new tweet and position
        Intent i = new Intent();
        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        i.putExtra(TWEET_POSITION_KEY, position);
        setResult(RESULT_OK, i);
        finish();
    }
}
