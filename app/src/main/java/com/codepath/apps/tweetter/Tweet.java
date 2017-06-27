package com.codepath.apps.tweetter;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by kkong on 6/26/17.
 */

@Parcel
public class Tweet {

    // List out attributes
    public String body;
    public long uid; // Database ID for the tweet
    public User user;
    public String createdAt;

    public Tweet() {}
    public Tweet(JSONObject jsonObject) throws JSONException {
        this.body = jsonObject.getString("text");
        this.uid = jsonObject.getLong("id");
        this.createdAt = jsonObject.getString("created_at");
        this.user = User.fromJSON(jsonObject.getJSONObject("user"));
    }

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

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
