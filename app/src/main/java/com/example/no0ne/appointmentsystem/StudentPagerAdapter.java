package com.example.no0ne.appointmentsystem;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by no0ne on 2/3/18.
 */

public class StudentPagerAdapter extends FragmentPagerAdapter {

    public StudentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AppointmentAcceptedFragment();
            case 1:
                return new AppointmentDeclinedFragment();
            case 2:
                return new TeacherListFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public String getPageTitle(int position) {
        switch (position) {
            case 0:
                return "ACCEPTED";
            case 1:
                return "DECLINED";
            case 2:
                return "TEACHER LIST";
            default:
                return null;
        }
    }
}
