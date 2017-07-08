package com.codepath.apps.tweetter.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.apps.tweetter.R;
import com.codepath.apps.tweetter.fragments.ComposeFragment;
import com.codepath.apps.tweetter.fragments.HomeTimelineFragment;
import com.codepath.apps.tweetter.fragments.TweetsListFragment;
import com.codepath.apps.tweetter.fragments.TweetsPagerAdapter;
import com.codepath.apps.tweetter.models.Tweet;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import android.support.v4.app.FragmentManager;

public class TimelineActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener, TweetsListFragment.LoadingProgressDialog, ComposeFragment.ComposeDialogListener {

    public static TweetsPagerAdapter adapter;
    public static ViewPager vpPager;
    public static final String TWEET_POSITION_KEY = "tweetPositionKey";

    public static final int REQUEST_CODE_COMPOSE = 20;
    public static final int REQUEST_CODE_DETAILS = 30;
    public static final int REQUEST_CODE_REPLY = 40;

    @BindView(R.id.sliding_tabs) TabLayout tabLayout;

    MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        // Set up the action bar
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.actionbar_custom, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.actionbar_title);
        mTitleTextView.setText(getString(R.string.app_name));

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        // Get the view pager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new TweetsPagerAdapter(getSupportFragmentManager(), this);
        // Set the adapter for the pager
        vpPager.setAdapter(adapter);
        // Setup the TabLayout to use the view pager
        tabLayout.setupWithViewPager(vpPager);
    }

    @OnClick(R.id.fabNewTweet)
    public void composeTweet() {
        FragmentManager fragmentManager = getFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance();
        composeFragment.show(fragmentManager, "fragment_compose");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                // Go to search activity and send the search query
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("search_query", query);
                startActivity(i);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch(item.getItemId()) {
            case R.id.miProfile:
                // Launch the profile activity
                Intent i = new Intent(this, ProfileActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If returning successfully from details
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_DETAILS) {
            // Deserialize the tweet and its position
            Tweet newTweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            int position = data.getIntExtra(TWEET_POSITION_KEY, 0);
            // Get the current fragment, update the tweet, and notify the adapter
            TweetsListFragment currentFragment = adapter.getRegisteredFragment(vpPager.getCurrentItem());
            currentFragment.tweets.set(position, newTweet);
            currentFragment.tweetAdapter.notifyItemChanged(position);
            currentFragment.rvTweets.scrollToPosition(position);
        // If returning successfully from compose
        } else if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
            // Deserialize the tweet
            Tweet newTweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            // Get the current fragment, insert the tweet, and notify the adapter
            TweetsListFragment currentFragment = adapter.getRegisteredFragment(vpPager.getCurrentItem());
            currentFragment.tweets.set(0, newTweet);
            currentFragment.tweetAdapter.notifyItemInserted(0);
            currentFragment.rvTweets.scrollToPosition(0);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
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

    @Override
    public void onTweetSelected(Tweet tweet, int position) {
        // Make sure the position is valid
        if(position != RecyclerView.NO_POSITION) {
            // Create an intent to the TweetDetailsActivity and send tweet and its position
            Intent i = new Intent(this, TweetDetailsActivity.class);
            i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
            i.putExtra(TWEET_POSITION_KEY, position);
            startActivityForResult(i, REQUEST_CODE_DETAILS);
        }
    }

    @Override
    public void onFinishComposeDialog(Tweet tweet) {
        // Get the current fragment
        TweetsListFragment currentFragment = adapter.getRegisteredFragment(vpPager.getCurrentItem());
        // If the current fragment is hometimeline, update it by inserting the new tweet
        if(currentFragment instanceof HomeTimelineFragment) {
            currentFragment.tweets.set(0, tweet);
            currentFragment.tweetAdapter.notifyItemInserted(0);
            currentFragment.rvTweets.scrollToPosition(0);
        }
    }
}
