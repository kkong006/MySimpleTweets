package com.codepath.apps.tweetter.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;

/**
 * Created by kkong on 6/30/17.
 */

@Parcel
public class Media {

    private String mediaUrl;

    public Media() { }

    public Media(JSONArray media) throws JSONException {
        try {
            mediaUrl = media.getJSONObject(0).getString("media_url_https");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getMediaUrl() {
        return mediaUrl;
    }
}
