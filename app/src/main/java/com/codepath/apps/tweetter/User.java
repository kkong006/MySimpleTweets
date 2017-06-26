package com.codepath.apps.tweetter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kkong on 6/26/17.
 */

public class User {

    // List the attributes
    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;

    // Deserialize the JSON
    public static User fromJSON(JSONObject json) throws JSONException {
        User user = new User();

        // Extract and fill values
        user.name = json.getString("name");
        user.uid = json.getLong("id");
        user.screenName = json.getString("screen_name");
        user.profileImageUrl = json.getString("profile_image_url");
        return user;
    }
}
