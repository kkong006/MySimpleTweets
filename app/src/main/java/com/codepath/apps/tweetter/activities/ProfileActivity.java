package com.codepath.apps.tweetter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetter.R;
import com.codepath.apps.tweetter.fragments.TweetsListFragment;
import com.codepath.apps.tweetter.fragments.UserTimelineFragment;
import com.codepath.apps.tweetter.models.Tweet;
import com.codepath.apps.tweetter.models.User;
import com.codepath.apps.tweetter.sync.TwitterApp;
import com.codepath.apps.tweetter.sync.TwitterClient;
import com.codepath.apps.tweetter.utilities.FollowerCountFormatter;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.codepath.apps.tweetter.activities.TimelineActivity.REQUEST_CODE_DETAILS;
import static com.codepath.apps.tweetter.activities.TimelineActivity.TWEET_POSITION_KEY;

public class ProfileActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener, TweetsListFragment.LoadingProgressDialog {

    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvTagline) TextView tvTagline;
    @BindView(R.id.tvFollowers) TextView tvFollowers;
    @BindView(R.id.tvFollowing) TextView tvFollowing;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;

    TwitterClient client;
    Tweet tweet;
    UserTimelineFragment userTimelineFragment;
    MenuItem miActionProgressItem;
    TextView mTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        client = TwitterApp.getRestClient();

        String screenName = getIntent().getStringExtra("screen_name");
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        if(tweet != null) {
            screenName = tweet.user.screenName;
        }

        // Custom Actionbar Setup
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.actionbar_custom, null);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        // Create the user fragment
        userTimelineFragment = UserTimelineFragment.newInstance(screenName);
        // Display the user timeline fragment inside the container (dynamically)
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Make transaction
        ft.replace(R.id.flContainer, userTimelineFragment);
        // Commit transaction
        ft.commit();

        // If the tweet is valid, get another user's info
        if(tweet != null) {
            populateUserHeadline(tweet.user);
        // If the tweet is invalid, get this user's info
        } else {
            getCurrentUserInfo();
        }
    }

    public void populateUserHeadline(User user) {
        // Populate profile info views
        tvName.setText(user.name);
        tvTagline.setText(user.tagLine);
        tvFollowers.setText(FollowerCountFormatter.getFollowerCount(user.followersCount) + " Followers");
        tvFollowing.setText(FollowerCountFormatter.getFollowerCount(user.followingCount) + " Following");
        mTitleTextView = (TextView) findViewById(R.id.actionbar_title);
        mTitleTextView.setText("@" + user.screenName);

        // Load profile image with Glide
        Glide.with(this)
                .load(user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(this, 150, 0))
                .into(ivProfileImage);
    }

    public void getCurrentUserInfo() {
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // Deserialize the user object
                    User user = User.fromJSON(response);
                    // Populate the user headline
                    populateUserHeadline(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed to get profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to get profile", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to get profile", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to get profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTweetSelected(Tweet tweet, int position) {
        if(position != RecyclerView.NO_POSITION) {
            // Create an intent to the TweetDetailsActivity with tweet and position
            Intent i = new Intent(this, TweetDetailsActivity.class);
            i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
            i.putExtra(TWEET_POSITION_KEY, position);
            startActivityForResult(i, REQUEST_CODE_DETAILS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If returning successfully from DetailsActivity
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_DETAILS) {
            // Deserialize the tweet and position
            Tweet newTweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            int position = data.getIntExtra(TWEET_POSITION_KEY, 0);
            // Replace the tweet with the new one and notify the adapter
            userTimelineFragment.tweets.set(position, newTweet);
            userTimelineFragment.tweetAdapter.notifyItemChanged(position);
            userTimelineFragment.rvTweets.scrollToPosition(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgressProfile);
        // Extract the action-view from the menu item
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void showProgressBar() {
        if(miActionProgressItem != null) {
            miActionProgressItem.setVisible(true);
        }
    }

    @Override
    public void hideProgressBar() {
        if(miActionProgressItem != null) {
            miActionProgressItem.setVisible(false);
        }
    }
}
