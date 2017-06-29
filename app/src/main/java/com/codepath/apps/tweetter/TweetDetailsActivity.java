package com.codepath.apps.tweetter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetter.models.Tweet;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.codepath.apps.tweetter.R.id.ibFavoriteDetails;
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
    @BindView(R.id.tvNumLikesDetails) TextView tvNumLikes;
    @BindView(ibFavoriteDetails) ImageView ibFavorited;
    @BindView(R.id.ibRetweetDetails) ImageView ibRetweeted;

    Tweet tweet;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.bind(this);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        // Populate the views according to this data
        tvUserName.setText(tweet.user.name);
        tvTweetBody.setText(tweet.body);
        tvScreenName.setText("@" + tweet.user.screenName); // + " · " + TimeFormatter.getTimeDifference(tweet.createdAt));
        tvTimeStamp.setText(tweet.getCreatedAt());
        // Load the profile image
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                .into(ivProfileImage);

        tvNumRetweets.setText(String.valueOf(tweet.retweetCount));
        tvNumLikes.setText(String.valueOf(tweet.favoriteCount));

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
    }

    public void setRetweeted() {
        // Set the retweeted status
        if(tweet.retweeted) {
            ibRetweeted.setImageResource(R.drawable.ic_unretweet);
        } else {
            ibRetweeted.setImageResource(R.drawable.ic_retweet);
        }
    }

    @OnClick(R.id.ibFavoriteDetails)
    public void putFavorite() {
        if(tweet.favorited) {
            tweet.favorited = false;
            setFavorited();
            // Unfavorite
//            client.unfavoriteTweet(tweet.uid, new JsonHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                    super.onSuccess(statusCode, headers, response);
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                    super.onFailure(statusCode, headers, throwable, errorResponse);
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                    super.onFailure(statusCode, headers, throwable, errorResponse);
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    super.onFailure(statusCode, headers, responseString, throwable);
//                }
//            });
            // Change the icon
        } else {
            tweet.favorited = true;
            setFavorited();
            // Favorite
//            client.favoriteTweet(tweet.uid, new JsonHttpResponseHandler() {
//
//            });
            // Change the icon back

        }
    }

}
