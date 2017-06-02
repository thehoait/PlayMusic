package com.example.asiantech.playmusic

import android.Manifest
import android.app.ActivityManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.example.asiantech.playmusic.adapter.PagerAdapter
import com.example.asiantech.playmusic.fragment.PlaySongFragment
import com.example.asiantech.playmusic.fragment.SongListFragment
import com.example.asiantech.playmusic.model.Song
import com.example.asiantech.playmusic.service.MusicService
import com.example.asiantech.playmusic.utils.StringUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * @author hoaht
 */
class MainActivity : FragmentActivity(), OnItemListener {

    private var mOnItemListener: OnItemListener? = null
    private var mListSong = ArrayList<Song>()
    private var mBound: Boolean = false

    private var mMusicService: MusicService? = null
    private var mPlayIntent: Intent? = null
    private val mHandler = Handler()
    private var mPagerAdapter: PagerAdapter? = null
    private var mMessage: String? = null
    private var mPositionClick: Int = -1

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = intent
        if (intent.extras != null) {
            mMessage = intent.extras.getString(MESSAGE)
        }
        if (isStoragePermissionGranted) {
            getListSongFromStore()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_READ_EXTERNAL_STORAGE)
        }
        Collections.sort(mListSong) { a, b -> a.title.compareTo(b.title) }
        mPagerAdapter = PagerAdapter(supportFragmentManager)
        viewPager.adapter = mPagerAdapter
        mOnItemListener = this
        tabStrip.setViewPager(viewPager)
        llController!!.visibility = View.GONE
        tvSongTitle!!.isSelected = true
        seekBar!!.max = 1000
        seekBar!!.setOnSeekBarChangeListener(mListener)
        val intentFilter = IntentFilter(ACTION_STRING_ACTIVITY)
        registerReceiver(mReceiver, intentFilter)
        mPlayIntent = Intent(this, MusicService::class.java)

        setOnClickView()
    }

    private val isStoragePermissionGranted: Boolean
        get() = Build.VERSION.SDK_INT < 23 || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_READ_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getListSongFromStore()
            }
        }
    }

    private fun setOnClickView() {
        imgPlay!!.setOnClickListener { onClickPlay() }
        imgNext!!.setOnClickListener { onClickNext() }
        imgPrevious!!.setOnClickListener { onClickPrev() }
        rlMainController!!.setOnClickListener { onClickMainController() }
    }

    private val mMusicConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected: ")
            val binder = service as MusicService.MusicBinder
            mMusicService = binder.service
            if (mMusicService!!.listType == "") {
                mMusicService!!.playList = mListSong
                mMusicService!!.listType = LIST_TYPE_ALL_SONG
            }
            mBound = true
            if (!mMusicService!!.mIsRunningPlayMusic) {
                playMusic()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "onServiceDisconnected: ")
            mBound = false
        }
    }

    private val mRunnable = Runnable { updateProgress() }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            Log.d(TAG, "onReceive: ")
            val message = intent?.extras?.getString(MESSAGE) ?: return
            when (message) {
                PLAY -> {
                    showController()
                    updateSongPlay()
                }
                PAUSE -> {
                    updatePlayPause()
                }
                ON_REBIND -> {
                    showController()
                    updateSongPlay()
                    if (FROM_NOTIFY == mMessage) {
                        onClickMainController()
                    }
                }
                COMPLETION, RESET -> resetController()
            }
        }
    }

    fun getOnItemListener(): OnItemListener? {
        return mOnItemListener
    }

    fun getListSong(): ArrayList<Song> {
        return mListSong
    }

    fun getMusicService(): MusicService? {
        return mMusicService
    }

    private fun updateSongPlay() {
        Log.d(TAG, "updateSongPlay: ")
        for (i in mListSong.indices) {
            mListSong[i].isPlaying = false
        }
        if (mMusicService!!.listType == LIST_TYPE_ALL_SONG) {
            mListSong[mMusicService!!.songPosition].isPlaying = true
        }
        val fragment = mPagerAdapter!!.getItem(0)
        if (fragment is SongListFragment) {
            fragment.notifySongListAdapter()
        }
    }

    private val mListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (!fromUser) {
                return
            }
            mMusicService!!.seekTo(progress * mMusicService!!.duration / 1000)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {

        }
    }

    private fun getListSongFromStore() {
        Log.d(TAG, "getListSongFromStore: ")
        val musicResolver = contentResolver
        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " !=0", null, null)
        if (musicCursor != null && musicCursor.moveToFirst()) {

            val titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val artistId = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)
            val artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val displayColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
            do {
                val thisId = musicCursor.getLong(idColumn)
                val thisTitle = musicCursor.getString(titleColumn)
                val thisArtistId = musicCursor.getLong(artistId)
                val thisArtist = musicCursor.getString(artistColumn)
                val thisAlbumId = musicCursor.getLong(albumIdColumn)
                val thisAlbum = musicCursor.getString(albumColumn)
                val thisDisplay = musicCursor.getString(displayColumn)
                mListSong.add(Song(thisId, thisTitle, thisArtistId, thisArtist, thisAlbumId,
                        thisAlbum, thisDisplay, false))
            } while (musicCursor.moveToNext())
        }
        musicCursor?.close()
    }

    private fun onClickPlay() {
        Log.d(TAG, "onClickPlay: ")
        if (mMusicService != null && mBound) {
            if (isPlaying) {
                pause()
            } else {
                start()
            }
        }
    }

    private fun onClickNext() {
        Log.d(TAG, "onClickNext: ")
        resetController()
        mMusicService!!.playNext()
    }

    private fun onClickPrev() {
        Log.d(TAG, "onClickPrev: ")
        resetController()
        mMusicService!!.playPrev()
    }

    internal fun onClickMainController() {
        Log.d(TAG, "onClickMainController: ")
        val fragment = PlaySongFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_up, 0, 0, R.anim.slide_out_up)
        transaction.replace(R.id.rlMainContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun updatePlayPause() {
        Log.d(TAG, "updatePlayPause: ")
        if (isPlaying) {
            imgPlay!!.setImageResource(R.drawable.main_control_pause)
        } else {
            imgPlay!!.setImageResource(R.drawable.main_control_play)
        }
    }

    private fun updateProgress() {
        Log.d(TAG, "updateProgress")
        val duration = duration
        val currentPosition = currentPosition
        if (duration > 0) {
            val position = 1000L * currentPosition / duration
            seekBar!!.progress = position.toInt()
        }
        tvCurrentTime!!.text = StringUtils.stringForTime(currentPosition)
        if (!mMusicService!!.isPlaying) {
            return
        }
        mHandler.postDelayed(mRunnable, 1000)
    }

    private fun setSongTime() {
        Log.d(TAG, "setSongTime: ")
        val duration = duration
        tvSongTime!!.text = StringUtils.stringForTime(duration)
    }

    private val songTitle: String?
        get() {
            if (mMusicService != null && mBound) {
                return mMusicService!!.songDisplay
            }
            return null
        }

    private fun start() {
        Log.d(TAG, "start: ")
        mMusicService!!.go()
    }

    private fun pause() {
        Log.d(TAG, "pause: ")
        mMusicService!!.pausePlayer()
    }

    private val currentPosition: Int
        get() {
            if (mMusicService != null && mBound) {
                return mMusicService!!.currentPosition
            }
            return 0

        }

    private val duration: Int
        get() {
            if (mMusicService != null && mBound) {
                return mMusicService!!.duration
            }
            return 0
        }

    private val isPlaying: Boolean
        get() = mMusicService != null && mBound && mMusicService!!.isPlaying

    override fun onStart() {
        Log.d(TAG, "onStart: ")
        super.onStart()
        if (isServiceRunning) {
            bindService(mPlayIntent, mMusicConnection, 0)
        }
    }

    override fun onRestart() {
        Log.d(TAG, "onRestart: ")
        super.onRestart()
    }

    override fun onResume() {
        Log.d(TAG, "onResume: ")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause: ")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "onStop: ")
        if (mBound) {
            unbindService(mMusicConnection)
            mBound = false
        }
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        if (mMusicService != null && mBound && !mMusicService!!.isPlayMusic) {
            stopService(mPlayIntent)
        }
        mPlayIntent = null
        mMusicService = null
        unregisterReceiver(mReceiver)
        mHandler.removeCallbacks(mRunnable)
        super.onDestroy()
    }

    private val isServiceRunning: Boolean
        get() {
            Log.d(TAG, "isServiceRunning: ")
            val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            return manager.getRunningServices(Integer.MAX_VALUE).any {
                MusicService::class.java.name == it.service.className
            }
        }

    override fun onItemClick(position: Int) {
        Log.d(TAG, "onItemClick: ")
        mPositionClick = position
        if (!isServiceRunning) {
            startService(mPlayIntent)
            bindService(mPlayIntent, mMusicConnection, 0)
        }
    }

    private fun playMusic() {
        if (mMusicService!!.listType != LIST_TYPE_ALL_SONG) {
            mMusicService!!.playList = mListSong
            mMusicService!!.listType = LIST_TYPE_ALL_SONG
        }
        resetController()
        mMusicService!!.setSong(mPositionClick)
        mMusicService!!.playSong()
    }

    private fun showController() {
        llController!!.visibility = View.VISIBLE
        tvSongTitle!!.text = songTitle
        updatePlayPause()
        updateProgress()
        setSongTime()
    }

    fun resetController() {
        Log.d(TAG, "resetController: ")
        mHandler.removeCallbacks(mRunnable)
    }

    companion object {

        private val TAG = MainActivity::class.java.simpleName
        private val PERMISSION_READ_EXTERNAL_STORAGE = 101
        private val LIST_TYPE_ALL_SONG = "AllSong"
        val MESSAGE = "message"
        val ACTION = "action"
        val PLAY = "play"
        val PAUSE = "pause"
        val RESET = "reset"
        val BACK = "back"
        val NEXT = "next"
        val CLOSE = "close"
        val ON_REBIND = "onRebind"
        val COMPLETION = "completion"
        val ACTION_STRING_ACTIVITY = "ToActivity"
        val FROM_NOTIFY = "from_notify"
        val LIST_TYPE_ALBUM = "Album"
        val LIST_TYPE_ARTIST = "Artist"
    }
}
