package com.example.asiantech.playmusic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.asiantech.playmusic.service.MusicService

/**
 * @author hoaht
 */
class MusicReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("TAG RECEIVER", "onReceive")
        val binder = peekService(context, Intent(context, MusicService::class.java)) as MusicService.MusicBinder
        val musicService = binder.service
        val message = intent.extras.getString(MainActivity.ACTION) ?: return
        when (message) {
            MainActivity.CLOSE -> {
                musicService.pausePlayer()
                musicService.stopForeground(true)
            }
            MainActivity.PLAY -> if (!musicService.isPlaying) {
                musicService.go()
            } else {
                musicService.pausePlayer()
            }
            MainActivity.BACK -> musicService.playPrev()
            MainActivity.NEXT -> musicService.playNext()
        }
    }
}
