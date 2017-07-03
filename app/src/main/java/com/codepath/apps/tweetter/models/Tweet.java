package com.codepath.apps.tweetter.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by kkong on 6/26/17.
 */

@Parcel
public class Tweet {

    /* Data Members */
    private User user;
    private Media media;
    private long uid; // Database ID for the tweet
    private String body;
    private String createdAt;
    private boolean favorited;
    private boolean retweeted;
    private int retweetCount;
    private int favoriteCount;

    /* Constructors */
    public Tweet() { }

    // Deserialize the JSON
    public Tweet(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("user")) {
            this.user = new User(jsonObject.getJSONObject("user"));
        } else {
            this.user = new User();
        }
        if(jsonObject.getJSONObject("entities").has("media")) {
            this.media = new Media(jsonObject.getJSONObject("entities").getJSONArray("media"));
        } else {
            this.media = new Media();
        }
        this.uid = jsonObject.getLong("id");
        this.body = jsonObject.getString("text");
        this.createdAt = jsonObject.getString("created_at");
        this.favorited = jsonObject.getBoolean("favorited");
        this.retweeted = jsonObject.getBoolean("retweeted");
        this.retweetCount = jsonObject.getInt("retweet_count");
        this.favoriteCount = jsonObject.getInt("favorite_count");
    }

    // Deserialize the JSON; the exceptions will be thrown back up to the caller
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        return new Tweet(jsonObject);
    }

    /* Getters/Setters */
    public User getUser() {
        return user;
    }

    public Media getMedia() {
        return media;
    }

    public long getUid() {
        return uid;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }
}
