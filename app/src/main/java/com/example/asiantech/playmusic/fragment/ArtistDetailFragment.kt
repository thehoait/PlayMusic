package com.example.asiantech.playmusic.fragment

import com.example.asiantech.playmusic.MainActivity
import com.example.asiantech.playmusic.model.Song

/**
 * ArtistDetailFragment.
 *
 * @author HoaHT
 */
class ArtistDetailFragment : BaseDetailFragment() {

    override fun getTypeId(song: Song): Long {
        return song.artistId
    }

    override fun getType(song: Song): String {
        return song.artist
    }

    override fun setListType() {
        mMusicService?.listType = MainActivity.LIST_TYPE_ARTIST
    }
}