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
import static com.codepath.apps.tweetter.TimelineActivity.TWEET_POSITION_KEY;
import static com.codepath.apps.tweetter.TweetAdapter.context;

public class TweetDetailsActivity extends AppCompatActivity {

    @BindView(R.id.ivProfileImageDetails)
    ImageView ivProfileImage;
    @BindView(R.id.tvUserNameDetails)
    TextView tvUserName;
    @BindView(R.id.tvScreenNameDetails)
    TextView tvScreenName;
    @BindView(R.id.tvTimeStampDetails)
    TextView tvTimeStamp;
    @BindView(R.id.tvTweetTextDetails)
    TextView tvTweetBody;
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
        Toast.makeText(this, "Details: " + position, Toast.LENGTH_SHORT).show();

        // Populate the views according to this data
        tvUserName.setText(tweet.user.name);
        tvTweetBody.setText(tweet.body);
        tvScreenName.setText("@" + tweet.user.screenName);
        tvTimeStamp.setText(TimeFormatter.getTimeStamp(tweet.getCreatedAt()));
        // Load the profile image
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                .into(ivProfileImage);

        // Load the media image
        Glide.with(context)
                .load(tweet.media.getMediaUrl())
                .bitmapTransform(new RoundedCornersTransformation(this, 5, 0))
                .into(ivMediaImage);

        setFavorited();
        setRetweeted();
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

    @OnClick(R.id.ibRetweetDetails)
    public void putRetweet() {
        if(tweet.retweeted) {
            client.unRetweet(tweet.uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        int oldRetweetCount = tweet.retweetCount;
                        tweet = Tweet.fromJSON(response);
                        if(tweet.retweeted) {
                            tweet.retweeted = false;
                        }
                        if(tweet.retweetCount > oldRetweetCount - 1) {
                            tweet.retweetCount = oldRetweetCount - 1;
                        }
                        setRetweeted();
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
            client.retweet(tweet.uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        int oldRetweetCount = tweet.retweetCount;
                        tweet = Tweet.fromJSON(response);
                        if(!tweet.retweeted) {
                            tweet.retweeted = true;
                        }
                        if(tweet.retweetCount < oldRetweetCount + 1) {
                            tweet.retweetCount = oldRetweetCount + 1;
                        }
                        setRetweeted();
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

    @OnClick(R.id.ibFavoriteDetails)
    public void putFavorite() {
        if(tweet.favorited) {
            client.unfavoriteTweet(tweet.uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
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
                    }
                }
                
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(context, "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(context, "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(context, "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
                }


            });
        // Change the icon
        } else {
            // Favorite the tweet
            client.favoriteTweet(tweet.uid, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
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
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(context, "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(context, "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(context, "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(context, "Back pressed: " + position, Toast.LENGTH_SHORT).show();
        Intent i = new Intent();
        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        i.putExtra(TWEET_POSITION_KEY, position);
        setResult(RESULT_OK, i);
        finish();
    }
}
