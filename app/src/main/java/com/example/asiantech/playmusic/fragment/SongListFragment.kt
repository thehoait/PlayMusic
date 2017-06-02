package com.example.asiantech.playmusic.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.asiantech.playmusic.MainActivity
import com.example.asiantech.playmusic.OnItemListener
import com.example.asiantech.playmusic.R
import com.example.asiantech.playmusic.adapter.SongAdapter
import com.example.asiantech.playmusic.model.Song
import kotlinx.android.synthetic.main.song_list_fragment.*

/**
 * SongListFragment.
 *
 * @author HoaHT
 */
class SongListFragment : Fragment() {

    private var mAdapter: SongAdapter? = null
    private var mOnItemListener: OnItemListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var listSong = ArrayList<Song>()
        if (activity is MainActivity) {
            listSong = (activity as MainActivity).getListSong()
            mOnItemListener = (activity as MainActivity).getOnItemListener()
        }
        mAdapter = SongAdapter(context, listSong, mOnItemListener)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.song_list_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleViewListSong.layoutManager = LinearLayoutManager(context)
        recycleViewListSong.adapter = mAdapter
    }

    fun notifySongListAdapter() {
        mAdapter?.notifyDataSetChanged()
    }
}