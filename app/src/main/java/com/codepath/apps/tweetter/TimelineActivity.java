package com.codepath.apps.tweetter;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.apps.tweetter.fragments.ComposeFragment;
import com.codepath.apps.tweetter.fragments.HomeTimelineFragment;
import com.codepath.apps.tweetter.fragments.TweetsListFragment;
import com.codepath.apps.tweetter.fragments.TweetsPagerAdapter;
import com.codepath.apps.tweetter.models.Tweet;

import org.parceler.Parcels;

//import android.support.v4.app.FragmentManager;

public class TimelineActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener, TweetsListFragment.LoadingProgressDialog, ComposeFragment.ComposeDialogListener {

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
    public static TweetsPagerAdapter adapter;
    public static ViewPager vpPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.actionbar_title);
        mTitleTextView.setText(getString(R.string.app_name));

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

//        client = TwitterApp.getRestClient();

//        fragmentTweetsList = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);

        // Get the view pager
        vpPager = (ViewPager) findViewById(R.id.viewpager);

        adapter = new TweetsPagerAdapter(getSupportFragmentManager(), this);

        // Set the adapter for the pager
        vpPager.setAdapter(adapter);

        // Setup the TabLayout to use the view pager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNewTweet);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                ComposeFragment composeFragment = ComposeFragment.newInstance();
                composeFragment.show(fragmentManager, "fragment_compose");
            }
        });

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
            case R.id.miProfile:
                // Launch the profile view
                Intent i = new Intent(this, ProfileActivity.class);
                startActivity(i);
//                Intent i = new Intent(this, ComposeActivity.class);
//                startActivityForResult(i, REQUEST_CODE_COMPOSE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_DETAILS) {
            Tweet newTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            int position = data.getIntExtra(TWEET_POSITION_KEY, 0);
            TweetsListFragment currentFragment = adapter.getRegisteredFragment(vpPager.getCurrentItem());
            currentFragment.tweets.set(position, newTweet);
            currentFragment.tweetAdapter.notifyItemChanged(position);
            currentFragment.rvTweets.scrollToPosition(position);
        } else if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
            Tweet newTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            TweetsListFragment currentFragment = adapter.getRegisteredFragment(vpPager.getCurrentItem());
            currentFragment.tweets.set(0, newTweet);
            currentFragment.tweetAdapter.notifyItemInserted(0);
            currentFragment.rvTweets.scrollToPosition(0);
        }
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
    }

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
    @Override
    public void showProgressBar() {
        // Show progress item
        if(miActionProgressItem != null) {
            miActionProgressItem.setVisible(true);
        }
    }

    @Override
    public void hideProgressBar() {
        // Hide progress item
        if(miActionProgressItem != null) {
            miActionProgressItem.setVisible(false);
        }
    }

//    public void onProfileView(MenuItem item) {
//        // Launch the profile view
//        Intent i = new Intent(this, ProfileActivity.class);
//        startActivity(i);
//    }
//
    @Override
    public void onTweetSelected(Tweet tweet, int position) {
//        Toast.makeText(this, tweet.body, Toast.LENGTH_SHORT).show();
        // Make sure the position is valid
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
    public void onFinishComposeDialog(Tweet tweet) {
        TweetsListFragment currentFragment = adapter.getRegisteredFragment(vpPager.getCurrentItem());
        if(currentFragment instanceof HomeTimelineFragment) {
            currentFragment.tweets.set(0, tweet);
            currentFragment.tweetAdapter.notifyItemInserted(0);
            currentFragment.rvTweets.scrollToPosition(0);
        }
    }
}
