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

public class ReplyActivity extends AppCompatActivity {

    @BindView(R.id.tvCharacterCountReply) TextView tvCharacterCount;
    @BindView(R.id.etNewTweetReply) EditText etNewTweet;

    private TwitterClient client;
    private Tweet tweet;
    final private int maxLength = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        ButterKnife.bind(this);

        client = TwitterApp.getRestClient();

        initCharacterCount();

        // Unwrap the intent
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
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

    @OnClick(R.id.btSubmitNewTweetReply)
    public void submitReply() {
        String newReplyText = "@" + tweet.getUser().getScreenName() + " " +  etNewTweet.getText().toString();
        // Send the request and parameters to the endpoint
        client.replyTweet(newReplyText, tweet.getUid(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    tweet = new Tweet(response);
                    Intent i = new Intent(ReplyActivity.this, TimelineActivity.class);
                    i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    setResult(RESULT_OK, i);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ReplyActivity.this, "Failed to submit retweet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(ReplyActivity.this, "Failed to submit retweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(ReplyActivity.this, "Failed to submit retweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(ReplyActivity.this, "Failed to submit retweet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
