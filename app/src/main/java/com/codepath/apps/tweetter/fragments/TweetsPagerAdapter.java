package com.codepath.apps.tweetter.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

/**
 * Created by kkong on 7/3/17.
 */

public class TweetsPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] {"Home", "Mentions"};
    private Context context;

    public TweetsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    // Return the total # of fragments
    @Override
    public int getCount() {
        return 2;
    }

    // Return the fragment to use depending on the position
    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            Toast.makeText(context, "Position is 0", Toast.LENGTH_SHORT).show();
            return new HomeTimelineFragment();
        } else if (position == 1) {
            Toast.makeText(context, "Position is 1", Toast.LENGTH_SHORT).show();
            return new MentionsTimelineFragment();
        } else {
            Toast.makeText(context, "Position is NULL", Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    // Return title based on position
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
