package com.codepath.apps.tweetter.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.codepath.apps.tweetter.fragments.HomeTimelineFragment;
import com.codepath.apps.tweetter.fragments.MentionsTimelineFragment;
import com.codepath.apps.tweetter.fragments.TweetsListFragment;

/**
 * Created by kkong on 7/3/17.
 */

public class TweetsPagerAdapter extends FragmentStatePagerAdapter {

    SparseArray<TweetsListFragment> registeredFragments = new SparseArray<TweetsListFragment>();
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
            return new HomeTimelineFragment();
        } else if (position == 1) {
            return new MentionsTimelineFragment();
        } else {
            return null;
        }
    }

    // Return title based on position
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TweetsListFragment fragment =  (TweetsListFragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public TweetsListFragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}