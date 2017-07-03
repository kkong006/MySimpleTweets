package com.codepath.apps.tweetter.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;

/**
 * Created by kkong on 6/30/17.
 */

@Parcel
public class Media {

    /* Data Members */
    private String mediaUrl;

    /* Constructors */
    public Media() { }

    public Media(JSONArray media) throws JSONException {
        mediaUrl = media.getJSONObject(0).getString("media_url_https");
    }

    public static Media fromJSON(JSONArray media) throws JSONException {
        return new Media(media);
    }

    /* Getters/Setters */
    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}
