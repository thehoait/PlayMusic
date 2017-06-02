package com.example.asiantech.playmusic.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.Transformation
import android.widget.ImageView

import com.example.asiantech.playmusic.MainActivity
import com.example.asiantech.playmusic.R
import com.example.asiantech.playmusic.service.MusicService

/**
 * @author hoaht
 */
class VisualizerFragment : Fragment() {

    private var mImgCircleMusic: ImageView? = null
    private var mAnimation: CircleAnimation? = null
    private var mMusicService: MusicService? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(activity).inflate(R.layout.visualizor_fragment, container, false)
        mImgCircleMusic = view.findViewById(R.id.imgCircleMusic) as ImageView

        initAnimation()
        if (activity is MainActivity) {
            mMusicService = (activity as MainActivity).getMusicService()
        }
        updateAnimation()
        val intentFilter = IntentFilter(MainActivity.ACTION_STRING_ACTIVITY)
        activity.registerReceiver(mReceiver, intentFilter)
        return view
    }

    private fun initAnimation() {
        mAnimation = CircleAnimation()
        mAnimation!!.duration = 5000
        mAnimation!!.repeatCount = Animation.INFINITE
        mAnimation!!.repeatMode = Animation.RESTART
        mAnimation!!.interpolator = LinearInterpolator()
        mImgCircleMusic!!.animation = mAnimation
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            Log.d(TAG, "onReceive: ")
            if (intent != null) {
                val message = intent.extras.getString("message")
                if (message != null) {
                    updateAnimation()
                }
            }
        }
    }

    private fun updateAnimation() {
        Log.d(TAG, "updateAnimation: ")
        if (mMusicService!!.isPlaying) {
            mAnimation!!.resume()
        } else {
            mAnimation!!.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity.unregisterReceiver(mReceiver)
        mAnimation!!.cancel()
    }

    /**
     * Circle Animation
     */
    inner class CircleAnimation : RotateAnimation(0.toFloat(), 360.toFloat(), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f) {

        private var mElapsedAtPause: Long = 0
        private var mPaused = false

        override fun getTransformation(currentTime: Long, outTransformation: Transformation): Boolean {
            if (mPaused && mElapsedAtPause == 0L) {
                mElapsedAtPause = currentTime - startTime
            }
            if (mPaused) {
                startTime = currentTime - mElapsedAtPause
            }
            return super.getTransformation(currentTime, outTransformation)
        }

        fun pause() {
            mElapsedAtPause = 0
            mPaused = true
        }

        fun resume() {
            mPaused = false
        }
    }

    companion object {
        private val TAG = VisualizerFragment::class.java.simpleName
    }
}
