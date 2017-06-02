package com.example.asiantech.playmusic.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.example.asiantech.playmusic.service.MusicService
import kotlinx.android.synthetic.main.play_list_fragment.*

/**
 * PlayListFragment.
 *
 * @author HoaHT
 */
class PlayListFragment : Fragment(), OnItemListener {

    private var adapter: SongAdapter? = null
    private var musicService: MusicService? = null
    private var playList = ArrayList<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity is MainActivity) {
            musicService = (activity as MainActivity).getMusicService()
        }
        if (musicService != null) {
            playList = musicService!!.playList
        }
        val intentFilter = IntentFilter(MainActivity.ACTION_STRING_ACTIVITY)
        activity.registerReceiver(receiver, intentFilter)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.play_list_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SongAdapter(context, playList, this)
        recycleViewPlayList.layoutManager = LinearLayoutManager(context)
        recycleViewPlayList.adapter = adapter
        recycleViewPlayList.scrollToPosition(musicService!!.songPosition)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.extras?.getString("message") ?: return
            when (message) {
                "play" -> updateSongPlay()
            }
        }
    }

    private fun updateSongPlay() {
        for (i in playList.indices) {
            playList[i].isPlaying = playList[i].id == musicService?.songPlayingId
        }
        adapter?.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int) {
        musicService?.setSong(position)
        musicService?.playSong()
    }

    override fun onDestroy() {
        activity.unregisterReceiver(receiver)
        super.onDestroy()
    }
}
