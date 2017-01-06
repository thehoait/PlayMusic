package com.example.asiantech.playmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.asiantech.playmusic.service.MusicService;

public class MusicReceiver extends BroadcastReceiver {

    public MusicReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG RECEIVER", "onReceive");
        MusicService.MusicBinder binder = (MusicService.MusicBinder)
                peekService(context, new Intent(context, MusicService.class));
        MusicService musicService = binder.getService();
        String message = intent.getExtras().getString("action");
        if (message != null) {
            switch (message) {
                case "close":
                    musicService.pausePlayer();
                    musicService.stopForeground(true);
                    break;
                case "play":
                    if (!musicService.isPlaying()) {
                        musicService.go();
                    } else {
                        musicService.pausePlayer();
                    }
                    break;
                case "back":
                    musicService.playPrev();
                    break;
                case "next":
                    musicService.playNext();
                    break;
            }
        }
    }
}
