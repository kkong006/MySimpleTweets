package com.codepath.apps.tweetter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kkong on 6/26/17.
 */

public class Tweet {

    // List out attributes
    public String body;
    public long uid; // Database ID for the tweet
    public User user;
    public String createdAt;

    // Deserialize the JSON; the exceptions will be thrown back up to the caller
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // Extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        return tweet;
    }
}
