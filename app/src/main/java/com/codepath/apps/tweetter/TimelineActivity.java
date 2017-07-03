package com.codepath.apps.tweetter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.codepath.apps.tweetter.fragments.TweetsListFragment;
import com.codepath.apps.tweetter.fragments.TweetsPagerAdapter;

public class TimelineActivity extends AppCompatActivity {

    private final int REQUEST_CODE_COMPOSE = 20;
    public static final int REQUEST_CODE_DETAILS = 30;
    public static final int REQUEST_CODE_REPLY = 40;


    public static final String RESULT_CODE_DETAILS = "resultCodeDetails";
//    public static final String TWEET_DETAILS_ACTIVITY = "tweetDetailsActivity";
    public static final String TWEET_POSITION_KEY = "tweetPositionKey";


    private SwipeRefreshLayout swipeContainer;
//    private TwitterClient client;

    MenuItem miActionProgressItem;
//    TweetAdapter tweetAdapter;
//    ArrayList<Tweet> tweets;
//    RecyclerView rvTweets;
    TweetsListFragment fragmentTweetsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

//        client = TwitterApp.getRestClient();

//        fragmentTweetsList = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);

        // Get the view pager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);

        // Set the adapter for the pager
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager(), this));

        // Setup the TabLayout to use the view pager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

//        client = TwitterApp.getRestClient();
//        populateTimeline();
//        // Find the RecyclerView
//        rvTweets = (RecyclerView) findViewById(rvTweets);
//        // Init the arraylist (data source)
//        tweets = new ArrayList<>();
//        // Construct the adapter from this datasource
//        tweetAdapter = new TweetAdapter(tweets);
//        // RecyclerView setup (layout manager, use adapter)
//        rvTweets.setLayoutManager(new LinearLayoutManager((this)));
//        // Set the adapter
//        rvTweets.setAdapter(tweetAdapter);

        // Lookup the swipe container view
//        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
//        // Setup refresh listener which triggers new data loading
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // Refresh the list here
//                fetchTimelineAsync(0);
//            }
//        });

        // Configure the refreshing colors
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
    }

//    public void fetchTimelineAsync(int page) {
//        // Send the network request to fetch the updated data
//        // `client` here is an instance of Android Async HTTP
//        // getHomeTimeline is an example endpoint.
//
//        client.getHomeTimeline(0, new JsonHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                tweetAdapter.clear();
//                for(int i = 0; i < response.length(); i++) {
//                    try {
//                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
//                        tweets.add(tweet);
//                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                swipeContainer.setRefreshing(false);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.d("TwitterClient", errorResponse.toString());
//                throwable.printStackTrace();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                Log.d("TwitterClient", errorResponse.toString());
//                throwable.printStackTrace();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Log.d("TwitterClient", responseString);
//                throwable.printStackTrace();
//            }
//        });
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//    private void populateTimeline() {
////        showProgressBar();
//
//        client.getHomeTimeline(0, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
////                hideProgressBar();
//                Log.d("TwitterClient", response.toString());
//                // Notify the adapter that we've added an item
////                try {
////                    Tweet tweet = Tweet.fromJSON(response);
////                    tweets.add(tweet);
////                    tweetAdapter.notifyItemInserted(tweets.size() - 1);
////
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
////                hideProgressBar();
//                Log.d("TwitterClient", response.toString());
//                fragmentTweetsList.addItems(response);
//                // Iterate through the JSON array
//                // For each entry, deserialize the JSON object
//
////                    try {
////                        for(int i = 0; i < response.length(); i++) {
////                            Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
////                            tweets.add(tweet);
////                            tweetAdapter.notifyItemInserted(tweets.size() - 1);
////                        }
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
//
//                // Convert each object to a Tweet model
//                // Add that Tweet model to our data source
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
////                hideProgressBar();
//                Log.d("TwitterClient", responseString);
//                throwable.printStackTrace();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
////                hideProgressBar();
//                Log.d("TwitterClient", errorResponse.toString());
//                throwable.printStackTrace();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
////                hideProgressBar();
//                Log.d("TwitterClient", errorResponse.toString());
//                throwable.printStackTrace();
//            }
//        });
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch(item.getItemId()) {
            case R.id.itCompose:
                Intent i = new Intent(this, ComposeActivity.class);
                startActivityForResult(i, REQUEST_CODE_COMPOSE);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
//            Tweet newTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
//            tweets.add(0, newTweet);
//            tweetAdapter.notifyItemInserted(0);
//            rvTweets.scrollToPosition(0);
//        } else if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_DETAILS) {
//            Tweet newTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
//            int position = data.getIntExtra(TWEET_POSITION_KEY, 0);
//            tweets.set(position, newTweet);
//            tweetAdapter.notifyItemChanged(position);
//            rvTweets.scrollToPosition(position);
//        } else if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_REPLY) {
//            Tweet newTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
//            tweets.add(0, newTweet);
//            tweetAdapter.notifyItemInserted(0);
//            rvTweets.scrollToPosition(0);
//        } else {
//            Toast.makeText(this, "Unable to submit tweet", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Initialize the client and pull the data
//        client = TwitterApp.getRestClient();
//        populateTimeline();
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }
//
    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    public void onProfileView(MenuItem item) {
        // Launch the profile view
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }
}
