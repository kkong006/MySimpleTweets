package com.codepath.apps.tweetter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.tweetter.EndlessRecyclerViewScrollListener;
import com.codepath.apps.tweetter.R;
import com.codepath.apps.tweetter.TweetAdapter;
import com.codepath.apps.tweetter.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by kkong on 7/3/17.
 */

public class TweetsListFragment extends Fragment implements TweetAdapter.TweetAdapterListener {

    public interface TweetSelectedListener {
        // Handle tweet selection
        public void onTweetSelected(Tweet tweet, int position);
    }

    public interface LoadingProgressDialog {
        // Show/hide progress dialog
        public void showProgressBar();
        public void hideProgressBar();
    }

    public TweetAdapter tweetAdapter;
    public ArrayList<Tweet> tweets;
    public RecyclerView rvTweets;
    private EndlessRecyclerViewScrollListener scrollListener;

    public SwipeRefreshLayout swipeContainer;
    
    // Inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);

        // Find the RecyclerView
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweets);
        // Init the arraylist (data source)
        tweets = new ArrayList<>();
        // Construct the adapter from this datasource
        tweetAdapter = new TweetAdapter(tweets, this);
        // RecyclerView setup (layout manager, use adapter)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvTweets.setLayoutManager(linearLayoutManager);
        // Set the adapter
        rvTweets.setAdapter(tweetAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Tweet lastTweet = tweets.get(tweets.size() - 1);
                fetchNextPage(lastTweet.uid - 1);
            }
        };

        rvTweets.addOnScrollListener(scrollListener);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh the list here
                fetchTimelineAsync(0);
            }
        });

//      Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return v;
    }

    public void addItems(JSONArray response) {
        try {
            for(int i = 0; i < response.length(); i++) {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                tweets.add(tweet);
                tweetAdapter.notifyItemInserted(tweets.size() - 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(View view, int position) {
        Tweet tweet = tweets.get(position);
        ((TweetSelectedListener)getActivity()).onTweetSelected(tweet, position);
    }

    public void fetchTimelineAsync(int page) {

    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void fetchNextPage(long max_id) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()

    }

    public void showProgressBar() {
        ((LoadingProgressDialog)getActivity()).showProgressBar();
    }

    public void hideProgressBar() {
        ((LoadingProgressDialog)getActivity()).hideProgressBar();
    }
}
