package com.example.asiantech.playmusic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.asiantech.playmusic.MainActivity;
import com.example.asiantech.playmusic.R;
import com.example.asiantech.playmusic.adapter.PlaySongAdapter;
import com.example.asiantech.playmusic.service.MusicService;
import com.example.asiantech.playmusic.utils.StringUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import me.relex.circleindicator.CircleIndicator;

/**
 * @author hoaht
 */
@EFragment(R.layout.play_song_fragment)
public class PlaySongFragment extends Fragment {

    @ViewById(R.id.tvSongTitle)
    TextView mTvSongTitle;

    @ViewById(R.id.seekBar)
    SeekBar mSeekBar;

    @ViewById(R.id.tvCurrentTime)
    TextView mTvCurrentTime;

    @ViewById(R.id.tvSongTime)
    TextView mTvSongTime;

    @ViewById(R.id.imgPlay)
    ImageView mImgPlay;

    @ViewById(R.id.imgMode)
    ImageView mImgMode;

    @ViewById(R.id.pager)
    ViewPager mViewPager;

    @ViewById(R.id.indicator)
    CircleIndicator mIndicator;

    private static final String TAG = PlaySongFragment.class.getSimpleName();
    private MusicService mMusicService;
    private final Handler mHandler = new Handler();
    private int mMode;

    @AfterViews
    void afterView() {
        Log.d(TAG, "afterView: ");
        intView();
        if (getActivity() instanceof MainActivity) {
            mMusicService = ((MainActivity) getActivity()).getMMusicService();
        }
        mSeekBar.setMax(1000);
        setSongTitle();
        updatePlayPause();
        setSongTime();
        updateProgress();
        updateMode();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_STRING_ACTIVITY);
        getActivity().registerReceiver(mReceiver, intentFilter);
        mSeekBar.setOnSeekBarChangeListener(mListener);
    }

    private void intView() {
        Log.d(TAG, "intView: ");
        PlaySongAdapter adapter = new PlaySongAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mIndicator.setViewPager(mViewPager);
        mViewPager.setCurrentItem(1);
    }

    private final SeekBar.OnSeekBarChangeListener mListener = new SeekBar.OnSeekBarChangeListener() {
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

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String message = intent.getExtras().getString("message");
                if (message != null) {
                    switch (message) {
                        case "play":
                            updateProgress();
                            setSongTime();
                            setSongTitle();
                            break;
                        case "pause":
                            updatePlayPause();
                            break;
                    }
                }
            }
        }
    };

    @Click(R.id.imgPlay)
    void onClickPlay() {
        if (mMusicService.isPlaying()) {
            mMusicService.pausePlayer();
        } else {
            mMusicService.go();
        }
        updatePlayPause();
    }

    @Click(R.id.imgNext)
    void onClickNext() {
        resetController();
        mMusicService.playNext();
    }

    @Click(R.id.imgPrevious)
    void onClickBack() {
        resetController();
        mMusicService.playPrev();
    }

    @Click(R.id.imgMode)
    void onClickMode() {
        mMode++;
        if (mMode > 3) {
            mMode = 0;
        }
        mMusicService.setMode(mMode);
        updateMode();
    }

    @Click(R.id.imgGoBack)
    void onClickGoBack() {
        getActivity().onBackPressed();
    }

    private void updateMode() {
        switch (mMusicService.getMode()) {
            case 0:
                mImgMode.setImageResource(R.drawable.mode_list);
                break;
            case 1:
                mImgMode.setImageResource(R.drawable.mode_list_cycle);
                break;
            case 2:
                mImgMode.setImageResource(R.drawable.mode_single_cycle);
                break;
            case 3:
                mImgMode.setImageResource(R.drawable.mode_random);
                break;
        }
    }

    private void setSongTime() {
        Log.d(TAG, "setSongTime: ");
        int duration = mMusicService.getDur();
        mTvSongTime.setText(StringUtils.stringForTime(duration));
    }

    private void setSongTitle() {
        Log.d(TAG, "setSongTitle: ");
        mTvSongTitle.setText(mMusicService.getSongTitle());
    }

    private void updateProgress() {
        int duration = mMusicService.getDur();
        int currentPosition = mMusicService.getCurPos();
        if (duration > 0) {
            long position = 1000L * currentPosition / duration;
            mSeekBar.setProgress((int) position);
        }
        mTvCurrentTime.setText(StringUtils.stringForTime(currentPosition));
        mHandler.postDelayed(mRunnable, 1000);
    }

    private void updatePlayPause() {
        Log.d(TAG, "updatePlayPause: ");
        if (mMusicService.isPlaying()) {
            mImgPlay.setImageResource(R.drawable.music_play_control_pause);
        } else {
            mImgPlay.setImageResource(R.drawable.music_play_control_play);
        }
    }

    private void resetController() {
        Log.d(TAG, "resetController: ");
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        mHandler.removeCallbacks(mRunnable);
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
