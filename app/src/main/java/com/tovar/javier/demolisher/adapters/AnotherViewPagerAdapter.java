package com.tovar.javier.demolisher.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by javier on 4/9/17.
 */

public class AnotherViewPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragmentList;


    public AnotherViewPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }


    public void setContent(List<Fragment> newFragments)
    {
        fragmentList = newFragments;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
