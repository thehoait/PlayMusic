package com.example.asiantech.playmusic.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.asiantech.playmusic.MainActivity
import com.example.asiantech.playmusic.R
import com.example.asiantech.playmusic.adapter.PlaySongAdapter
import com.example.asiantech.playmusic.service.MusicService
import com.example.asiantech.playmusic.utils.StringUtils
import kotlinx.android.synthetic.main.play_song_fragment.*

/**
 * PlaySongFragment.
 *
 * @author HoaHT
 */
class PlaySongFragment : Fragment() {

    private var musicService: MusicService? = null
    private val handler = Handler()
    private var mode: Int = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.play_song_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setOnClickView()
    }

    private fun init() {
        initView()
        if (activity is MainActivity) {
            musicService = (activity as MainActivity).getMusicService()
        }
        seekBar.max = 1000
        setSongTitle()
        updatePlayPause()
        setSongTime()
        updateProgress()
        updateMode()
        val intentFilter = IntentFilter(MainActivity.ACTION_STRING_ACTIVITY)
        activity.registerReceiver(receiver, intentFilter)
        seekBar.setOnSeekBarChangeListener(listener)
    }

    private fun initView() {
        val adapter = PlaySongAdapter(childFragmentManager)
        pager.adapter = adapter
        indicator.setViewPager(pager)
        pager.currentItem = 1
    }

    private val listener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (!fromUser) {
                return
            }
            musicService?.seekTo(progress * musicService!!.duration / 1000)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }

    }

    private val runnable = Runnable {
        updateProgress()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!isAdded) {
                return
            }
            val message = intent?.extras?.getString(MainActivity.MESSAGE) ?: return
            when (message) {
                MainActivity.PLAY -> {
                    updatePlayPause()
                    updateProgress()
                    setSongTime()
                    setSongTitle()
                }
                MainActivity.PAUSE -> updatePlayPause()
            }
        }

    }

    private fun setOnClickView() {
        imgPlay.setOnClickListener {
            if (musicService!!.isPlaying) {
                musicService?.pausePlayer()
            } else {
                musicService?.go()
            }
            updatePlayPause()
        }
        imgNext.setOnClickListener {
            resetController()
            musicService?.playNext()
        }
        imgPrevious.setOnClickListener {
            resetController()
            musicService?.playPrev()
        }
        imgMode.setOnClickListener {
            mode++
            if (mode > 3) {
                mode = 0
            }
            musicService?.mode = mode
            updateMode()
        }
        imgGoBack.setOnClickListener {
            activity.onBackPressed()
        }
    }

    private fun updateMode() {
        when (musicService!!.mode) {
            0 -> imgMode.setImageResource(R.drawable.mode_list)
            1 -> imgMode.setImageResource(R.drawable.mode_list_cycle)
            2 -> imgMode.setImageResource(R.drawable.mode_single_cycle)
            3 -> imgMode.setImageResource(R.drawable.mode_random)
        }
    }

    private fun setSongTime() {
        val duration: Int = musicService!!.duration
        tvSongTime.text = StringUtils.stringForTime(duration)
    }

    private fun setSongTitle() {
        tvSongTitle.text = musicService?.songTitle
    }

    private fun updateProgress() {
        if (!isAdded) {
            return
        }
        val duration = musicService!!.duration
        val currentPosition = musicService!!.currentPosition
        if (duration > 0) {
            val position: Long = 1000L * currentPosition / duration
            if (seekBar != null) {
                seekBar.progress = position.toInt()
            }
        }
        tvCurrentTime.text = StringUtils.stringForTime(currentPosition)
        if (!musicService!!.isPlaying) {
            return
        }
        handler.postDelayed(runnable, 1000)
    }

    private fun updatePlayPause() {
        if (musicService!!.isPlaying) {
            imgPlay.setImageResource(R.drawable.music_play_control_pause)
        } else {
            imgPlay.setImageResource(R.drawable.music_play_control_play)
        }
    }

    private fun resetController() {
        handler.removeCallbacks(runnable)
    }

    override fun onDestroy() {
        activity.unregisterReceiver(receiver)
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }
}