package com.example.asiantech.playmusic.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.asiantech.playmusic.fragment.AlbumsFragment_;
import com.example.asiantech.playmusic.fragment.ArtistFragment_;
import com.example.asiantech.playmusic.fragment.SongListFragment_;

/**
 * @author hoaht
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    private final String[] mTitles = {"Songs", "Albums", "Artist"};
    private final Fragment mSongListFragment = new SongListFragment_();
    private final Fragment mAlbumsFragment = new AlbumsFragment_();
    private final Fragment mArtistFragment = new ArtistFragment_();

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mSongListFragment;
            case 1:
                return mAlbumsFragment;
            case 2:
                return mArtistFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
