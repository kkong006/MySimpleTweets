package com.codepath.apps.tweetter.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.tweetter.R;
import com.codepath.apps.tweetter.models.Tweet;
import com.codepath.apps.tweetter.sync.TwitterApp;
import com.codepath.apps.tweetter.sync.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

/**
 * Created by kkong on 7/7/17.
 */

public class ComposeFragment extends DialogFragment {

    @BindView(R.id.etNewTweet) EditText mTweetText;
    @BindView(R.id.tvCharacterCount) TextView tvCharacterCount;
    private Unbinder unbinder;

    private Tweet tweet;
    final private int maxLength = 140;
    private TwitterClient client;

    public interface ComposeDialogListener {
        void onFinishComposeDialog(Tweet tweet);
    }

    // Empty constructor required for DialogFragment
    public ComposeFragment() { }

    public static ComposeFragment newInstance() {
        ComposeFragment composeFragment = new ComposeFragment();
        return composeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        client = TwitterApp.getRestClient();
        initCharacterCount();

        // Set the title
        getDialog().setTitle("Compose Tweet");

        // Show soft keyboard automatically and request focus to field
        mTweetText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void initCharacterCount() {
        mTweetText.addTextChangedListener(new TextWatcher() {
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
    public void submitTweet(View v) {
        String newTweetText = mTweetText.getText().toString();
        client.sendTweet(newTweetText, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    // Get the tweet
                    tweet = Tweet.fromJSON(response);
                    // Alert the listener
                    ComposeDialogListener listener = (ComposeDialogListener) getActivity();
                    listener.onFinishComposeDialog(tweet);
                    // Close the dialog and return back to the parent activity
                    dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed to submit tweet", Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getActivity(), "Failed to submit tweet", Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getActivity(), "Failed to submit tweet", Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getActivity(), "Failed to submit tweet", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
