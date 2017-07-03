package com.codepath.apps.tweetter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.codepath.apps.tweetter.R.id.ibFavoriteDetails;
import static com.codepath.apps.tweetter.TimelineActivity.REQUEST_CODE_REPLY;
import static com.codepath.apps.tweetter.TimelineActivity.TWEET_POSITION_KEY;
import static com.codepath.apps.tweetter.TweetAdapter.context;

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
    private TwitterClient client;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.bind(this);

        client = TwitterApp.getRestClient();

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        position = getIntent().getIntExtra(TWEET_POSITION_KEY, 0);

        // Populate the views according to this data
        tvUserName.setText(tweet.getUser().getName());
        tvTweetBody.setText(tweet.getBody());
        tvScreenName.setText("@" + tweet.getUser().getScreenName());
        tvTimeStamp.setText(TimeFormatter.getTimeStamp(tweet.getCreatedAt()));

        // Load the profile image
        Glide.with(context)
                .load(tweet.getUser().getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                .into(ivProfileImage);

        // Load the media image
        Glide.with(context)
                .load(tweet.getMedia().getMediaUrl())
                .bitmapTransform(new RoundedCornersTransformation(this, 5, 0))
                .into(ivMediaImage);

        setFavorited();
        setRetweeted();
    }

    private void setFavorited() {
        // Set the tweet favorited status
        if(tweet.isFavorited()) {
            ibFavorited.setImageResource(R.drawable.ic_unfavorite);
        } else {
            ibFavorited.setImageResource(R.drawable.ic_favorite);
        }
        // Set the number of favorites
        if(tweet.getFavoriteCount() > 0) {
            tvNumFavorites.setText(String.valueOf(tweet.getFavoriteCount()));
        } else {
            tvNumFavorites.setText("");
        }
    }

    private void setRetweeted() {
        // Set the retweeted status
        if(tweet.isRetweeted()) {
            ibRetweeted.setImageResource(R.drawable.ic_unretweet);
        } else {
            ibRetweeted.setImageResource(R.drawable.ic_retweet);
        }
        // Set the number of retweets
        if(tweet.getRetweetCount() > 0) {
            tvNumRetweets.setText(String.valueOf(tweet.getRetweetCount()));
        } else {
            tvNumRetweets.setText("");
        }
    }

    private void unRetweetTweet() {
        client.unRetweet(tweet.getUid(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    int oldRetweetCount = tweet.getRetweetCount();
                    tweet = new Tweet(response);
                    if(tweet.isRetweeted()) {
                        tweet.setRetweeted(false);
                    }
                    if(tweet.getRetweetCount() > oldRetweetCount - 1) {
                        tweet.setRetweetCount(oldRetweetCount - 1);
                    }
                    setRetweeted();
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

    private void retweetTweet() {
        client.retweet(tweet.getUid(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    int oldRetweetCount = tweet.getRetweetCount();
                    tweet = new Tweet(response);
                    if(!tweet.isRetweeted()) {
                        tweet.setRetweeted(true);
                    }
                    if(tweet.getRetweetCount() < oldRetweetCount + 1) {
                        tweet.setRetweetCount(oldRetweetCount + 1);
                    }
                    setRetweeted();
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

    private void unfavoriteTweet() {
        client.unfavoriteTweet(tweet.getUid(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    int oldFavoriteCount = tweet.getFavoriteCount();
                    tweet = new Tweet(response);
                    if(tweet.isFavorited()) {
                        tweet.setFavorited(false);
                    }
                    if(tweet.getFavoriteCount() > oldFavoriteCount - 1) {
                        tweet.setFavoriteCount(oldFavoriteCount - 1);
                    }
                    setFavorited();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Failed to unfavorite tweet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(context, "Failed to unfavorite tweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(context, "Failed to unfavorite tweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(context, "Failed to unfavorite tweet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void favoriteTweet() {
        client.favoriteTweet(tweet.getUid(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    int oldFavoriteCount = tweet.getFavoriteCount();
                    tweet = new Tweet(response);
                    if(!tweet.isFavorited()) {
                        tweet.setFavorited(true);
                    }
                    if(tweet.getFavoriteCount() < oldFavoriteCount + 1) {
                        tweet.setFavoriteCount(oldFavoriteCount + 1);
                    }
                    setFavorited();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(context, "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(context, "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(context, "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.ibMessageDetails)
    public void putReply() {
        Intent i = new Intent(context, ReplyActivity.class);
        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        ((AppCompatActivity)context).startActivityForResult(i, REQUEST_CODE_REPLY);
    }

    @OnClick(R.id.ibRetweetDetails)
    public void putRetweet() {
        if(tweet.isRetweeted()) {
            unRetweetTweet();
        } else {
            retweetTweet();
        }
    }

    @OnClick(R.id.ibFavoriteDetails)
    public void putFavorite() {
        if(tweet.isFavorited()) {
            unfavoriteTweet();
        } else {
            favoriteTweet();
        }
    }

    // On back pressed, return to the the timeline with the updated tweet
    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        i.putExtra(TWEET_POSITION_KEY, position);
        setResult(RESULT_OK, i);
        finish();
    }
}
