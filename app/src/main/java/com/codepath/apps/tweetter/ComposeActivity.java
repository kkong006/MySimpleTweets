package com.codepath.apps.tweetter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.tweetter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    @BindView(R.id.etNewTweet) EditText etNewTweet;
    @BindView(R.id.tvCharacterCount) TextView tvCharacterCount;

    private TwitterClient client;
    private Tweet tweet;
    final private int maxLength = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);

        client = TwitterApp.getRestClient();

        initCharacterCount();
    }

    // Add a text change listener to update the character count
    private void initCharacterCount() {
        etNewTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharacterCount.setText(String.valueOf(maxLength - s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @OnClick(R.id.btSubmitNewTweet)
    public void submitTweet() {
        // Get the tweet text
        String newTweetText = etNewTweet.getText().toString();

        // Send it to the endpoint
        client.sendTweet(newTweetText, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    // Get the tweet response
                    tweet = new Tweet(response);
                    // Return to the timeline class
                    Intent i = new Intent(ComposeActivity.this, TimelineActivity.class);
                    i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    setResult(RESULT_OK, i);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ComposeActivity.this, "Failed to submit tweet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
                throwable.printStackTrace();
                Toast.makeText(ComposeActivity.this, "Failed to submit tweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
                throwable.printStackTrace();
                Toast.makeText(ComposeActivity.this, "Failed to submit tweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
                throwable.printStackTrace();
                Toast.makeText(ComposeActivity.this, "Failed to submit tweet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ComposeActivity.this, TimelineActivity.class);
        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        setResult(RESULT_OK, i);
        finish();
    }
}
