package com.codepath.apps.tweetter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

public class ComposeActivity extends AppCompatActivity {

    private TwitterClient client;
    private Tweet tweet;
    final private int maxLength = 140;

//    @BindView(R.id.etNewTweet) EditText etNewTweet;
//    @BindView(R.id.btSubmitNewTweet) Button btSubmitNewTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Button btSubmitNewTweet = (Button) findViewById(R.id.btSubmitNewTweet);
        btSubmitNewTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etNewTweet = (EditText) findViewById(R.id.etNewTweet);
                String newTweetText = etNewTweet.getText().toString();
                client.sendTweet(newTweetText, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            // Get the tweet
                            tweet = Tweet.fromJSON(response);
                            // Make the intent
                            Intent i = new Intent(ComposeActivity.this, TimelineActivity2.class);
                            i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                            setResult(RESULT_OK, i);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(ComposeActivity.this, "Submitted tweet", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            for(int i = 0;  i < response.length(); i++) {
                                tweet = Tweet.fromJSON(response.getJSONObject(i));
                                Intent intent = new Intent(ComposeActivity.this, TimelineActivity2.class);
                                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ComposeActivity.this, "Failed to submit tweet", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(ComposeActivity.this, "Submitted tweet", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        super.onSuccess(statusCode, headers, responseString);
                        Toast.makeText(ComposeActivity.this, "Submitted tweet", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(ComposeActivity.this, "Failed to submit tweet", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        final TextView tvCharacterCount = (TextView) findViewById(R.id.tvCharacterCount);
        EditText etNewTweet = (EditText) findViewById(R.id.etNewTweet);
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
