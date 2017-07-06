package com.codepath.apps.tweetter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetter.fragments.TweetsListFragment;
import com.codepath.apps.tweetter.fragments.UserTimelineFragment;
import com.codepath.apps.tweetter.models.Tweet;
import com.codepath.apps.tweetter.models.User;
import com.codepath.apps.tweetter.utilities.FollowerCountFormatter;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.tweetter.TimelineActivity.REQUEST_CODE_DETAILS;
import static com.codepath.apps.tweetter.TimelineActivity.TWEET_POSITION_KEY;

public class ProfileActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener {

    TwitterClient client;
    Tweet tweet;
    UserTimelineFragment userTimelineFragment;
    TextView mTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String screenName = getIntent().getStringExtra("screen_name");
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        if(tweet != null) {
            screenName = tweet.user.screenName;
        }

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        mTitleTextView = (TextView) mCustomView.findViewById(R.id.actionbar_title);
        mTitleTextView.setText(screenName);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        // Create the user fragment
        userTimelineFragment = UserTimelineFragment.newInstance(screenName);
        // Display the user timeline fragment inside the container (dynamically)

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        // Make change
        ft.replace(R.id.flContainer, userTimelineFragment);

        // Commit transaction
        ft.commit();

        client = TwitterApp.getRestClient();

        if(tweet != null) {
            // Get the other user's info and populate the layout
            populateUserHeadline(tweet.user);
        } else {
            // Get this user's info and populate the layout
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // Deserialize the user object
                    try {
                        User user = User.fromJSON(response);
                        // Set the title of the ActionBar based on the user info
                        Log.e("ProfileActivity", user.screenName);
                        // Populate the user headline
                        populateUserHeadline(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("ProfileActivity", "Failed to fetch user info");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    throwable.printStackTrace();
                }
            });
        }
    }

    public void populateUserHeadline(User user) {
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);

        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);

        tvName.setText(user.name);
        tvTagline.setText(user.tagLine);
        tvFollowers.setText(FollowerCountFormatter.getFollowerCount(user.followersCount) + " Followers");
        tvFollowing.setText(FollowerCountFormatter.getFollowerCount(user.followingCount) + " Following");
        mTitleTextView.setText(user.screenName);

        // Load profile image with Glide
        Glide.with(this).load(user.profileImageUrl).into(ivProfileImage);
        Log.e("ProfileActivity", user.profileImageUrl);
    }

    @Override
    public void onTweetSelected(Tweet tweet, int position) {
        if(position != RecyclerView.NO_POSITION) {
            // Create an intent to the TweetDetailsActivity
            Intent i = new Intent(this, TweetDetailsActivity.class);
            i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
            i.putExtra(TWEET_POSITION_KEY, position);
            // Start the activity
            startActivityForResult(i, REQUEST_CODE_DETAILS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_DETAILS) {
            Tweet newTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            int position = data.getIntExtra(TWEET_POSITION_KEY, 0);
            userTimelineFragment.tweets.set(position, newTweet);
            userTimelineFragment.tweetAdapter.notifyItemChanged(position);
            userTimelineFragment.rvTweets.scrollToPosition(position);
        }
    }
}
