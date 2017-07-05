package com.codepath.apps.tweetter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    
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
        rvTweets.setLayoutManager(new LinearLayoutManager((getContext())));
        // Set the adapter
        rvTweets.setAdapter(tweetAdapter);

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
}
