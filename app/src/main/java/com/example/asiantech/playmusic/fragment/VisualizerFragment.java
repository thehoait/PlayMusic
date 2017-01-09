package com.example.asiantech.playmusic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.example.asiantech.playmusic.MainActivity;
import com.example.asiantech.playmusic.R;
import com.example.asiantech.playmusic.service.MusicService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * @author hoaht
 */
@EFragment(R.layout.visualizor_fragment)
public class VisualizerFragment extends Fragment {

    private static final String TAG = VisualizerFragment.class.getSimpleName();

    @ViewById(R.id.imgCircleMusic)
    ImageView mImgCircleMusic;

    private CircleAnimation mAnimation;
    private MusicService mMusicService;

    @AfterViews
    void afterView() {
        Log.d(TAG, "afterView: ");
        initAnimation();
        if (getActivity() instanceof MainActivity) {
            mMusicService = ((MainActivity) getActivity()).getMMusicService();
        }
        updateAnimation();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_STRING_ACTIVITY);
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    private void initAnimation() {
        mAnimation = new CircleAnimation(
        );
        mAnimation.setDuration(5000);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.RESTART);
        mAnimation.setInterpolator(new LinearInterpolator());
        mImgCircleMusic.setAnimation(mAnimation);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            if (intent != null) {
                String message = intent.getExtras().getString("message");
                if (message != null) {
                    updateAnimation();
                }
            }
        }
    };

    private void updateAnimation() {
        Log.d(TAG, "updateAnimation: ");
        if (mMusicService.isPlaying()) {
            mAnimation.resume();
        } else {
            mAnimation.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
        mAnimation.cancel();
    }

    /**
     * Circle Animation
     */
    public class CircleAnimation extends RotateAnimation {

        private long mElapsedAtPause = 0;
        private boolean mPaused = false;

        private CircleAnimation() {
            super((float) 0, (float) 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }

        @Override
        public boolean getTransformation(long currentTime, Transformation outTransformation) {
            if (mPaused && mElapsedAtPause == 0) {
                mElapsedAtPause = currentTime - getStartTime();
            }
            if (mPaused) {
                setStartTime(currentTime - mElapsedAtPause);
            }
            return super.getTransformation(currentTime, outTransformation);
        }

        private void pause() {
            mElapsedAtPause = 0;
            mPaused = true;
        }

        private void resume() {
            mPaused = false;
        }
    }
}
