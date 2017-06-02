package com.example.asiantech.playmusic.fragment

import com.example.asiantech.playmusic.MainActivity
import com.example.asiantech.playmusic.model.Song

/**
 * AlbumDetailFragment
 *
 * @author HoaHT
 */
class AlbumDetailFragment : BaseDetailFragment() {

    override fun getTypeId(song: Song): Long {
        return song.albumId
    }

    override fun getType(song: Song): String {
        return song.album
    }

    override fun setListType() {
        mMusicService?.listType = MainActivity.LIST_TYPE_ALBUM
    }

}