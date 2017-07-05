package com.codepath.apps.tweetter.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by kkong on 6/26/17.
 */

@Parcel
public class User {

    // List the attributes
    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;

    public String tagLine;
    public int followersCount;
    public int followingCount;

    public User() {}

    public User(JSONObject object) throws JSONException {
        this.name = object.getString("name");
        this.uid = object.getLong("id");
        this.screenName = object.getString("screen_name");
        this.profileImageUrl = object.getString("profile_image_url").replace("_normal", ""); // Can replace with "_
        this.tagLine = object.getString("description");
        this.followersCount = object.getInt("followers_count");
        this.followingCount = object.getInt("friends_count");
    }

    // Deserialize the JSON
    public static User fromJSON(JSONObject json) throws JSONException {
        return new User(json);
    }
}
