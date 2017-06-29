package com.codepath.apps.tweetter.models;

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
    public int retweetCount;
    public int favoriteCount;
    public User user;
    public String createdAt;
    public boolean favorited;
    public boolean retweeted;

    public Tweet() {}
    public Tweet(JSONObject jsonObject) throws JSONException {
        this.body = jsonObject.getString("text");
        this.uid = jsonObject.getLong("id");
        this.createdAt = jsonObject.getString("created_at");
        this.user = User.fromJSON(jsonObject.getJSONObject("user"));
        try {
            this.favorited = jsonObject.getBoolean("favorited");
        } catch(JSONException e) {
            this.favorited = false;
        }
        try {
            this.retweeted = jsonObject.getBoolean("retweeted");
        } catch(JSONException e) {
            this.retweeted = false;
        }
        try {
            this.retweetCount = jsonObject.getInt("retweet_count");
        } catch (JSONException e) {
            this.retweetCount = 0;
        }
        try {
            this.favoriteCount = jsonObject.getInt("favorite_count");
        } catch (JSONException e) {
            this.favoriteCount = 0;
        }
    }

    // Deserialize the JSON; the exceptions will be thrown back up to the caller
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        return new Tweet(jsonObject);
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
