package com.example.asiantech.playmusic.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.asiantech.playmusic.fragment.LyricsFragment_;
import com.example.asiantech.playmusic.fragment.PlayListFragment_;
import com.example.asiantech.playmusic.fragment.VisualizerFragment_;

public class PlaySongAdapter extends FragmentPagerAdapter {
    public PlaySongAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new PlayListFragment_();
                break;
            case 1:
                fragment = new VisualizerFragment_();
                break;
            case 2:
                fragment = new LyricsFragment_();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
