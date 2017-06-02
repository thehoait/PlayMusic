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
import kotlinx.android.synthetic.main.list_detail_fragment.*

/**
 * BaseDetailFragment.
 *
 * @author HoaHT
 */
abstract class BaseDetailFragment : Fragment(), OnItemListener {
    companion object {
        val KEY_ALBUM_ID = "key_album_id"
    }

    protected var albumId: Long = 0L
    protected var mMusicService: MusicService? = null
    private var listSongAlbum = ArrayList<Song>()
    private var adapter: SongAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var listSong = ArrayList<Song>()
        albumId = arguments.getLong(KEY_ALBUM_ID)
        if (activity is MainActivity) {
            listSong = (activity as MainActivity).getListSong()
            mMusicService = (activity as MainActivity).getMusicService()
        }
        (listSong.size - 1 downTo 0)
                .map { listSong[it] }
                .filter { getTypeId(it) == albumId }
                .forEach {
                    listSongAlbum.add(Song(it.id, it.title, it.artistId, it.artist, it.albumId,
                            it.album, it.display, false))
                }
        if (mMusicService?.listType.equals(MainActivity.LIST_TYPE_ALBUM)
                || mMusicService?.listType.equals(MainActivity.LIST_TYPE_ARTIST)) {
            (0..listSongAlbum.size - 1)
                    .filter { mMusicService?.songPlayingId == listSongAlbum[it].id }
                    .forEach { listSongAlbum[it].isPlaying = true }
        }
        val intentFilter = IntentFilter(MainActivity.ACTION_STRING_ACTIVITY)
        activity.registerReceiver(receiver, intentFilter)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.list_detail_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SongAdapter(context, listSongAlbum, this)
        recycleViewListSong.layoutManager = LinearLayoutManager(context)
        recycleViewListSong.adapter = adapter
        if (listSongAlbum.size > 0) {
            tvTitle.text = getType(listSongAlbum[0])
        }
        imgGoBack.setOnClickListener {
            activity.onBackPressed()
        }
    }

    protected abstract fun getTypeId(song: Song): Long

    protected abstract fun getType(song: Song): String

    protected abstract fun setListType()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) {
                return
            }
            val message: String? = intent.extras.getString("message") ?: return
            when (message) {
                "play" -> updateSongPlay()
            }
        }
    }

    private fun updateSongPlay() {
        for (i in 0..listSongAlbum.size - 1) {
            listSongAlbum[i].isPlaying = listSongAlbum[i].id == mMusicService?.songPlayingId
        }
        adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity.unregisterReceiver(receiver)
    }

    override fun onItemClick(position: Int) {
        mMusicService?.playList = listSongAlbum
        setListType()
        if (activity is MainActivity) {
            (activity as MainActivity).resetController()
        }
        mMusicService?.setSong(position)
        mMusicService?.playSong()
        updateSongPlay()
    }
}