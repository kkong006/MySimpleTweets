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
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

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

    Tweet tweet;

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
    }


}
