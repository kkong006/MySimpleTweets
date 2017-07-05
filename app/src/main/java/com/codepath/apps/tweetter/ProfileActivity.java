package com.codepath.apps.tweetter;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetter.fragments.UserTimelineFragment;
import com.codepath.apps.tweetter.models.Tweet;
import com.codepath.apps.tweetter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient client;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String screenName = getIntent().getStringExtra("screen_name");
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        if(tweet != null) {
            screenName = tweet.user.screenName;
        }
        // Create the user fragment
        UserTimelineFragment userTimelineFragmentFragment = UserTimelineFragment.newInstance(screenName);
        // Display the user timeline fragment inside the container (dynamically)

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        // Make change
        ft.replace(R.id.flContainer, userTimelineFragmentFragment);

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
                        getSupportActionBar().setTitle(user.screenName);
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
        tvFollowers.setText(user.followersCount + " Followers");
        tvFollowing.setText(user.followingCount + " Following");

        // Load profile image with Glide
        Glide.with(this).load(user.profileImageUrl).into(ivProfileImage);
        Log.e("ProfileActivity", user.profileImageUrl);

    }
}
