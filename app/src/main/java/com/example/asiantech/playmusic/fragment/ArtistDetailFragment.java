package com.example.asiantech.playmusic.fragment;

import com.example.asiantech.playmusic.MainActivity;
import com.example.asiantech.playmusic.R;
import com.example.asiantech.playmusic.model.Song;

import org.androidannotations.annotations.EFragment;

/**
 * @author hoaht
 */
@EFragment(R.layout.list_detail_fragment)
public class ArtistDetailFragment extends BaseDetailFragment {

    @Override
    protected long getTypeId(Song song) {
        return song.getArtistId();
    }

    @Override
    protected String getType(Song song) {
        return song.getArtist();
    }

    @Override
    protected void setListType() {
        mMusicService.setListType(MainActivity.LIST_TYPE_ARTIST);
    }
}
