package com.example.musicplayer.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class MyFSPAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;

    public MyFSPAdapter(@NonNull FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.fragmentList = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList == null ? null : fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList == null ? null : fragmentList.size();
    }
}
