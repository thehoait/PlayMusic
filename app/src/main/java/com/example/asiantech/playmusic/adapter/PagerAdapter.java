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
    private String[] mTitles = {"Songs", "Albums", "Artist"};
    private Fragment mSongListFragment = new SongListFragment_();
    private Fragment mAlbumsFragment = new AlbumsFragment_();
    private Fragment mArtistFragment = new ArtistFragment_();

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
