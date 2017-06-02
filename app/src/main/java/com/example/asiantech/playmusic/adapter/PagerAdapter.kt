package com.example.asiantech.playmusic.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.asiantech.playmusic.fragment.AlbumsFragment
import com.example.asiantech.playmusic.fragment.ArtistFragment
import com.example.asiantech.playmusic.fragment.SongListFragment

/**
 * PagerAdapter.
 *
 * @author HoaHT
 */
class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val titles = listOf("Songs", "Albums", "Artist")
    private val songListFragment = SongListFragment()
    private val albumsFragment = AlbumsFragment()
    private val artistFragment = ArtistFragment()

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return songListFragment
            1 -> return albumsFragment
            2 -> return artistFragment
        }
        return null
    }

    override fun getCount(): Int {
        return titles.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }
}
