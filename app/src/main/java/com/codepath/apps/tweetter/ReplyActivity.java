package com.codepath.apps.tweetter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.tweetter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ReplyActivity extends AppCompatActivity {

    private TwitterClient client;
    private Tweet tweet;
    final private int maxLength = 140;
    TextView mTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        // Unwrap the intent
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        mTitleTextView = (TextView) mCustomView.findViewById(R.id.actionbar_title);
        mTitleTextView.setText("Reply");

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        TextView tvReplyingTo = (TextView) findViewById(R.id.tvReplyingTo);
        tvReplyingTo.setText("Replying to @" + tweet.user.screenName);

        Button btSubmitReply = (Button) findViewById(R.id.btSubmitNewTweetReply);
        btSubmitReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etNewReply = (EditText) findViewById(R.id.etNewTweetReply);
                String newReplyText = "@" + tweet.user.screenName + " " +  etNewReply.getText().toString();
                // Send the request and parameters to the endpoint
                client.replyTweet(newReplyText, tweet.uid, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            tweet = Tweet.fromJSON(response);
                            Intent i = new Intent(ReplyActivity.this, TimelineActivity.class);
                            i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                            setResult(RESULT_OK, i);
                            startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ReplyActivity.this, "Failed to submit reply", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(ReplyActivity.this, "Failed to submit reply", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(ReplyActivity.this, "Failed to submit reply", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(ReplyActivity.this, "Failed to submit reply", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        final TextView tvCharacterCount = (TextView) findViewById(R.id.tvCharacterCountReply);
        EditText etNewTweet = (EditText) findViewById(R.id.etNewTweetReply);
        etNewTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharacterCount.setText(String.valueOf(maxLength - s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        client = TwitterApp.getRestClient();
    }



}
