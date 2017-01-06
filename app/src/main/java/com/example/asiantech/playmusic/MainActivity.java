package com.example.asiantech.playmusic;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.example.asiantech.playmusic.adapter.PagerAdapter;
import com.example.asiantech.playmusic.fragment.PlaySongFragment_;
import com.example.asiantech.playmusic.fragment.SongListFragment_;
import com.example.asiantech.playmusic.model.Song;
import com.example.asiantech.playmusic.service.MusicService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Locale;

import lombok.Getter;

@EActivity(R.layout.activity_main)
public class MainActivity extends FragmentActivity implements OnItemListener {

    @ViewById(R.id.tabStrip)
    PagerSlidingTabStrip mTabStrip;
    @ViewById(R.id.viewPager)
    ViewPager mViewPager;
    @ViewById(R.id.imgPlay)
    ImageView mImgPlay;
    @ViewById(R.id.seekBar)
    SeekBar mSeekBar;
    @ViewById(R.id.tvCurrentTime)
    TextView mTvCurrentTime;
    @ViewById(R.id.tvSongTime)
    TextView mTvSongTime;
    @ViewById(R.id.llController)
    LinearLayout mController;
    @ViewById(R.id.tvSongTitle)
    TextView mTvSongTitle;
    @Getter
    private OnItemListener mOnItemListener;
    @Getter
    private ArrayList<Song> mListSong;
    private boolean mBound;
    @Getter
    private MusicService mMusicService;
    private Intent mPlayIntent;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private Handler mHandler = new Handler();
    public static final String ACTION_STRING_ACTIVITY = "ToActivity";
    public static final String LIST_TYPE_ALL_SONG = "AllSong";
    public static final String LIST_TYPE_ALBUM = "Album";
    public static final String LIST_TYPE_ARTIST = "Artist";
    private PagerAdapter mPagerAdapter;
    private String mMessage = "";

    @AfterViews
    void afterView() {
        Log.d("TAG ACTIVITY", "afterView");
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            mMessage = intent.getExtras().getString("message");
        }
        mListSong = new ArrayList<>();
        getListSong();
        Collections.sort(mListSong, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mOnItemListener = this;
        mTabStrip.setViewPager(mViewPager);
        mController.setVisibility(View.GONE);
        mTvSongTitle.setSelected(true);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        mSeekBar.setMax(1000);
        mSeekBar.setOnSeekBarChangeListener(mListener);
        if (mReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
            registerReceiver(mReceiver, intentFilter);
        }
    }

    private ServiceConnection mMusicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("TAG ACTIVITY", "onServiceConnected");
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            mMusicService = binder.getService();
            if (mMusicService.getListType().equals("")) {
                mMusicService.setPlayList(mListSong);
                mMusicService.setListType(LIST_TYPE_ALL_SONG);
            }
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("TAG ACTIVITY", "onServiceDisconnected");
            mBound = false;
        }
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG ACTIVITY", "onReceive");
            if (intent != null) {
                String message = intent.getExtras().getString("message");
                if (message != null) {
                    switch (message) {
                        case "play":
                            showController();
                            updateSongPlay();
                            break;
                        case "pause":
                            updatePlayPause();
                            break;
                        case "onRebind":
                            showController();
                            updateSongPlay();
                            if ("fromNotify".equals(mMessage)) {
                                onClickMainController();
                            }
                            break;
                        case "completion":
                            resetController();
                            break;
                        case "reset":
                            resetController();
                            break;
                    }
                }
            }
        }
    };

    private void updateSongPlay() {

        Log.d("TAG ACTIVITY", "updateSongPlay");
        for (int i = 0; i < mListSong.size(); i++) {
            mListSong.get(i).setPlaying(false);
        }
        if (mMusicService.getListType().equals(LIST_TYPE_ALL_SONG)) {
            mListSong.get(mMusicService.getSongPosition()).setPlaying(true);
        }
        Fragment fragment = mPagerAdapter.getItem(0);
        if (fragment instanceof SongListFragment_) {
            ((SongListFragment_) fragment).notifySongListAdapter();
        }
    }

    private SeekBar.OnSeekBarChangeListener mListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            mMusicService.seekTo(progress * mMusicService.getDur() / 1000);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void getListSong() {
        Log.d("TAG ACTIVITY", "getListSong");
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC +
                " !=0", null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {

            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistId = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int displayColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                long thisArtistId = musicCursor.getLong(artistId);
                String thisArtist = musicCursor.getString(artistColumn);
                long thisAlbumId = musicCursor.getLong(albumIdColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                String thisDisplay = musicCursor.getString(displayColumn);
                mListSong.add(new Song(thisId, thisTitle, thisArtistId, thisArtist, thisAlbumId,
                        thisAlbum, thisDisplay, false));
            }
            while (musicCursor.moveToNext());
        }
        if (musicCursor != null) {
            musicCursor.close();
        }
    }

    @Click(R.id.imgPlay)
    void onClickPlay() {
        Log.d("TAG ACTIVITY", "onClickPlay");
        if (mMusicService != null && mBound) {
            if (isPlaying()) {
                pause();
            } else {
                start();
            }
        }
    }

    @Click(R.id.imgNext)
    void onClickNext() {
        Log.d("TAG ACTIVITY", "onClickNext");
        resetController();
        mMusicService.playNext();
    }

    @Click(R.id.imgPrevious)
    void onClickPrev() {
        Log.d("TAG ACTIVITY", "onClickPrev");
        resetController();
        mMusicService.playPrev();
    }

    @Click(R.id.rlMainController)
    void onClickMainController() {
        Log.d("TAG ACTIVITY", "onClickMainController");
        PlaySongFragment_ fragment = new PlaySongFragment_();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, 0, 0, R.anim.slide_out_up);
        transaction.replace(R.id.rlMainContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void updatePlayPause() {
        Log.d("TAG ACTIVITY", "updatePlayPause");
        if (isPlaying()) {
            mImgPlay.setImageResource(R.drawable.main_control_pause);
        } else {
            mImgPlay.setImageResource(R.drawable.main_control_play);
        }
    }

    private void updateProgress() {
        int duration = getDuration();
        int currentPosition = getCurrentPosition();
        if (duration > 0) {
            long position = 1000L * currentPosition / duration;
            mSeekBar.setProgress((int) position);
        }
        mTvCurrentTime.setText(stringForTime(currentPosition));
        mHandler.postDelayed(mRunnable, 1000);

    }

    private void setSongTime() {
        Log.d("TAG ACTIVITY", "setSongTime");
        int duration = getDuration();
        mTvSongTime.setText(stringForTime(duration));
    }

    private String getSongTitle() {
        if (mMusicService != null && mBound) {
            return mMusicService.getSongDisplay();
        }
        return null;
    }

    private void start() {
        Log.d("TAG ACTIVITY", "start");
        mMusicService.go();
    }

    private void pause() {
        Log.d("TAG ACTIVITY", "pause");
        mMusicService.pausePlayer();
    }

    private int getCurrentPosition() {
        if (mMusicService != null && mBound) {
            return mMusicService.getCurPos();
        }
        return 0;

    }

    private int getDuration() {
        if (mMusicService != null && mBound) {
            return mMusicService.getDur();
        }
        return 0;
    }

    private boolean isPlaying() {
        return mMusicService != null && mBound && mMusicService.isPlaying();

    }

    @Override
    protected void onStart() {
        Log.d("TAG ACTIVITY", "onStart");
        super.onStart();
        connectService();
    }

    private void connectService() {
        Log.d("TAG ACTIVITY", "connectService");
        if (mPlayIntent == null) {
            mPlayIntent = new Intent(this, MusicService.class);
        }
        if (!isServiceRunning()) {
            startService(mPlayIntent);
        }
        bindService(mPlayIntent, mMusicConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onRestart() {
        Log.d("TAG ACTIVITY", "onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d("TAG ACTIVITY", "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("TAG ACTIVITY", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("TAG ACTIVITY", "onStop");
        unbindService(mMusicConnection);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("TAG ACTIVITY", "onDestroy");
        if (mMusicService != null && mBound && !mMusicService.isPlayMusic()) {
            stopService(mPlayIntent);
        }
        mPlayIntent = null;
        mMusicService = null;
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private boolean isServiceRunning() {
        Log.d("TAG ACTIVITY", "isServiceRunning");
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (MusicService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.d("TAG ACTIVITY", "onItemClick");
        if (!mMusicService.getListType().equals(LIST_TYPE_ALL_SONG)) {
            mMusicService.setPlayList(mListSong);
            mMusicService.setListType(LIST_TYPE_ALL_SONG);
        }
        resetController();
        mMusicService.setSong(position);
        mMusicService.playSong();
    }

    private void showController() {
        mController.setVisibility(View.VISIBLE);
        mTvSongTitle.setText(getSongTitle());
        updatePlayPause();
        updateProgress();
        setSongTime();
    }

    public void resetController() {
        Log.d("TAG ACTIVITY", "resetController");
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }
}
