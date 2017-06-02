package com.example.asiantech.playmusic.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.ContentUris
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews
import com.example.asiantech.playmusic.MainActivity
import com.example.asiantech.playmusic.MusicReceiver
import com.example.asiantech.playmusic.R
import com.example.asiantech.playmusic.model.Song
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author hoaht
 */
class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private var mMediaPlayer: MediaPlayer? = null
    private val mMusicBinder = MusicBinder()
    private var mMessage = ""
    private var mRemoteViews: RemoteViews? = null
    private var mNotification: Notification? = null
    private var mPause: Boolean = false
    var mIsRunningPlayMusic: Boolean = false
    var playList = ArrayList<Song>()
        set(listSong) {
            Log.d(TAG, "setPlayList: ")
            field = listSong
        }
    var songPosition: Int = -1
    var listType = ""
    var mode: Int = 0

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
        mMediaPlayer = MediaPlayer()
        initPlayer()
        createNotification()
    }

    private fun initPlayer() {
        Log.d(TAG, "initPlayer: ")
        mMediaPlayer!!.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mMediaPlayer!!.setOnPreparedListener(this)
        mMediaPlayer!!.setOnCompletionListener(this)
        mMediaPlayer!!.setOnErrorListener(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")
        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return mMusicBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind: ")
        return true
    }

    override fun onRebind(intent: Intent) {
        super.onRebind(intent)
        Log.d(TAG, "onRebind: ")
        mMessage = MainActivity.ON_REBIND
        sendBroadcast()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        if (isPlayMusic) {
            mMediaPlayer!!.stop()
        }
        mMediaPlayer!!.release()
        super.onDestroy()
    }

    override fun onCompletion(mp: MediaPlayer) {
        Log.d(TAG, "onCompletion: ")
        mMessage = MainActivity.COMPLETION
        sendBroadcast()
        when (mode) {
            0 -> if (songPosition == playList.size - 1) {
                pausePlayer()
                stopForeground(true)
            } else {
                playNext()
            }
            2 -> playSong()
            3 -> playRandom()
            else -> playNext()
        }
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        Log.d(TAG, "onError: ")
        return false
    }

    fun playSong() {
        Log.d(TAG, "playSong: ")
        mMessage = MainActivity.RESET
        sendBroadcast()
        mMediaPlayer!!.reset()
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                playList[songPosition].id)
        try {
            mMediaPlayer!!.setDataSource(applicationContext, uri)
        } catch (e: IOException) {
            Log.e(TAG, "playSong: " + e)
        }

        mMediaPlayer!!.prepareAsync()
    }

    override fun onPrepared(mp: MediaPlayer) {
        Log.d(TAG, "onPrepared: ")
        mp.start()
        updateNotification()
        mMessage = MainActivity.PLAY
        if (!mIsRunningPlayMusic) {
            mIsRunningPlayMusic = true
        }
        sendBroadcast()
    }

    fun setSong(position: Int) {
        Log.d(TAG, "setSong: ")
        this.songPosition = position
    }

    val songPlayingId: Long
        get() = playList[songPosition].id

    private fun createNotification() {
        Log.d(TAG, "createNotification: ")
        mRemoteViews = RemoteViews(packageName, R.layout.notification_play_music)
        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_notification)
                .setContentTitle("play music")
                .setContent(mRemoteViews)
                .setPriority(Notification.PRIORITY_MAX)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.MESSAGE, MainActivity.FROM_NOTIFY)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        setListenerNotification()
        mNotification = builder.build()
    }

    private fun setListenerNotification() {
        val intent = Intent(this, MusicReceiver::class.java)
        intent.putExtra(MainActivity.ACTION, MainActivity.CLOSE)
        var pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)
        mRemoteViews!!.setOnClickPendingIntent(R.id.imgClose, pendingIntent)
        intent.putExtra(MainActivity.ACTION, MainActivity.PLAY)
        pendingIntent = PendingIntent.getBroadcast(this, 2, intent, 0)
        mRemoteViews!!.setOnClickPendingIntent(R.id.imgPlay, pendingIntent)
        intent.putExtra(MainActivity.ACTION, MainActivity.BACK)
        pendingIntent = PendingIntent.getBroadcast(this, 3, intent, 0)
        mRemoteViews!!.setOnClickPendingIntent(R.id.imgPrevious, pendingIntent)
        intent.putExtra(MainActivity.ACTION, MainActivity.NEXT)
        pendingIntent = PendingIntent.getBroadcast(this, 4, intent, 0)
        mRemoteViews!!.setOnClickPendingIntent(R.id.imgNext, pendingIntent)
    }

    private fun updateNotification() {
        if (isPlaying) {
            mRemoteViews!!.setImageViewResource(R.id.imgPlay, R.drawable.button_notify_pause)
        } else {
            mRemoteViews!!.setImageViewResource(R.id.imgPlay, R.drawable.button_notify_play)
        }
        mRemoteViews!!.setTextViewText(R.id.tvSongTitle, songTitle)
        startForeground(NOTIFICATION_ID, mNotification)
    }

    /**
     * Music Binder
     */
    inner class MusicBinder : Binder() {

        val service: MusicService
            get() {
                Log.d(TAG, "getService: ")
                return this@MusicService
            }
    }

    fun go() {
        Log.d(TAG, "go: ")
        mMediaPlayer!!.start()
        mMessage = MainActivity.PLAY
        sendBroadcast()
        updateNotification()
    }

    fun pausePlayer() {
        Log.d(TAG, "pausePlayer: ")
        mMediaPlayer!!.pause()
        mPause = true
        mMessage = MainActivity.PAUSE
        sendBroadcast()
        updateNotification()
    }

    fun playNext() {
        Log.d(TAG, "playNext: ")
        songPosition++
        if (songPosition >= playList.size) {
            songPosition = 0
        }
        playSong()
    }

    fun playPrev() {
        Log.d(TAG, "playPrev: ")
        songPosition--
        if (songPosition < 0) {
            songPosition = playList.size - 1
        }
        playSong()
    }

    private fun playRandom() {
        Log.d(TAG, "playRandom: ")
        val random = Random()
        songPosition = random.nextInt(playList.size - 1)
        playSong()
    }

    val songTitle: String
        get() = playList[songPosition].title

    val songDisplay: String
        get() = playList[songPosition].title + " - " + playList[songPosition]
                .artist

    val currentPosition: Int
        get() = mMediaPlayer!!.currentPosition

    val duration: Int
        get() = mMediaPlayer!!.duration

    val isPlaying: Boolean
        get() = mMediaPlayer!!.isPlaying

    fun seekTo(position: Int) {
        mMediaPlayer!!.seekTo(position)
    }

    val isPlayMusic: Boolean
        get() = isPlaying || mPause

    private fun sendBroadcast() {
        Log.d(TAG, "sendBroadcast: ")
        val intent = Intent()
        intent.action = MainActivity.ACTION_STRING_ACTIVITY
        intent.putExtra(MainActivity.MESSAGE, mMessage)
        sendBroadcast(intent)
    }

    companion object {

        private val TAG = MusicService::class.java.simpleName
        private val NOTIFICATION_ID = 111
    }
}
