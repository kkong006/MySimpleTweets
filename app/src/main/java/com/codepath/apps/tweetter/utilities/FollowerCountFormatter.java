package com.codepath.apps.tweetter.utilities;

/**
 * Created by kkong on 7/6/17.
 */

public class FollowerCountFormatter {
    public static String getFollowerCount(int followerCount) {
        String convertedCount = String.valueOf(followerCount);
        if((followerCount / 1000000000) > 0) {
            convertedCount = followerCount / 1000000000 + "B";
        } else if((followerCount / 1000000) > 0) {
            convertedCount = followerCount / 1000000 + "M";
        } else if((followerCount / 1000) > 0) {
            convertedCount = followerCount / 1000 + "K";
        }
        return convertedCount;
    }
}
