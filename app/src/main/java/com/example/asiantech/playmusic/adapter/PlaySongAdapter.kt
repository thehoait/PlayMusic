package com.example.asiantech.playmusic.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.asiantech.playmusic.fragment.LyricsFragment
import com.example.asiantech.playmusic.fragment.PlayListFragment
import com.example.asiantech.playmusic.fragment.VisualizerFragment

/**
 * PlaySongAdapter.
 *
 * @author HoaHT
 */
class PlaySongAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = PlayListFragment()
            1 -> fragment = VisualizerFragment()
            2 -> fragment = LyricsFragment()
        }
        return fragment
    }

    override fun getCount(): Int {
        return 3
    }
}