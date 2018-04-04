package com.example.no0ne.appointmentsystem.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.no0ne.appointmentsystem.fragment.AcceptedFragment;
import com.example.no0ne.appointmentsystem.fragment.PendingFragment;

/**
 * Created by no0ne on 9/18/17.
 */

public class TeacherPagerAdapter extends FragmentPagerAdapter {

    public TeacherPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AcceptedFragment();
            case 1:
                return new PendingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public String getPageTitle(int position) {
        switch (position) {
            case 0:
                return "ACCEPTED";
            case 1:
                return "PENDING";
            default:
                return null;
        }
    }
}
