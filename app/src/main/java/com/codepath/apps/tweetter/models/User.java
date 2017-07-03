package com.codepath.apps.tweetter.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by kkong on 6/26/17.
 */

@Parcel
public class User {

    /* Data Members */
    private long uid;
    private String name;
    private String screenName;
    private String profileImageUrl;

    /* Constructors */
    public User() {}

    // Deserialize the JSON
    public User(JSONObject object) throws JSONException {
        this.uid = object.getLong("id");
        this.name = object.getString("name");
        this.screenName = object.getString("screen_name");
        this.profileImageUrl = object.getString("profile_image_url");
    }

    public static User fromJSON(JSONObject object) throws JSONException {
        return new User(object);
    }

    /* Getters/Setters */
    public long getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
