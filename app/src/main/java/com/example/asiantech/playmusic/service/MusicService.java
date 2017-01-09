package com.example.asiantech.playmusic.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.asiantech.playmusic.MainActivity;
import com.example.asiantech.playmusic.MainActivity_;
import com.example.asiantech.playmusic.MusicReceiver;
import com.example.asiantech.playmusic.R;
import com.example.asiantech.playmusic.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author hoaht
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private static final String TAG = MusicService.class.getSimpleName();
    private static final String ACTION = "action";
    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> mPlayList;
    private int mSongPosition;
    private final IBinder mMusicBinder = new MusicBinder();
    private static final int NOTIFICATION_ID = 111;
    private String mMessage = "";
    private RemoteViews mRemoteViews;
    private Notification mNotification;
    private String mListType = "";
    private boolean mPause;
    private int mMode;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mMediaPlayer = new MediaPlayer();
        initPlayer();
        createNotification();
    }

    private void initPlayer() {
        Log.d(TAG, "initPlayer: ");
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind: ");
        mMessage = "onRebind";
        sendBroadcast();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        if (isPlayMusic()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
        super.onDestroy();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion: ");
        mMessage = "completion";
        sendBroadcast();
        switch (mMode) {
            case 0:
                if (mSongPosition == mPlayList.size() - 1) {
                    pausePlayer();
                    stopForeground(true);
                } else {
                    playNext();
                }
                break;
            case 2:
                playSong();
                break;
            case 3:
                playRandom();
                break;
            default:
                playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError: ");
        return false;
    }

    public void playSong() {
        Log.d(TAG, "playSong: ");
        mMessage = "reset";
        sendBroadcast();
        mMediaPlayer.reset();
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                mPlayList.get(mSongPosition).getId());
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
        } catch (IOException e) {
            Log.e(TAG, "playSong: " + e);
        }
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared: ");
        mp.start();
        updateNotification();
        mMessage = "play";

        sendBroadcast();
    }

    public void setPlayList(ArrayList<Song> listSong) {
        Log.d(TAG, "setPlayList: ");
        mPlayList = listSong;
    }

    public ArrayList<Song> getPlayList() {
        return mPlayList;
    }

    public void setListType(String type) {
        mListType = type;
    }

    public String getListType() {
        return mListType;
    }

    public void setSong(int position) {
        Log.d(TAG, "setSong: ");
        this.mSongPosition = position;
    }

    public long getSongPlayingId() {
        return mPlayList.get(mSongPosition).getId();
    }

    public int getSongPosition() {
        return mSongPosition;
    }

    private void createNotification() {
        Log.d(TAG, "createNotification: ");
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_play_music);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_notification)
                .setContentTitle("play music")
                .setContent(mRemoteViews);
        Intent intent = new Intent(this, MainActivity_.class);
        intent.putExtra("message", "fromNotify");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        setListenerNotification();
        mNotification = builder.build();
    }

    private void setListenerNotification() {
        Intent intent = new Intent(this, MusicReceiver.class);
        intent.putExtra(ACTION, "close");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.imgClose, pendingIntent);
        intent.putExtra(ACTION, "play");
        pendingIntent = PendingIntent.getBroadcast(this, 2, intent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.imgPlay, pendingIntent);
        intent.putExtra(ACTION, "back");
        pendingIntent = PendingIntent.getBroadcast(this, 3, intent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.imgPrevious, pendingIntent);
        intent.putExtra(ACTION, "next");
        pendingIntent = PendingIntent.getBroadcast(this, 4, intent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.imgNext, pendingIntent);
    }

    private void updateNotification() {
        if (isPlaying()) {
            mRemoteViews.setImageViewResource(R.id.imgPlay, R.drawable.button_notify_pause);
        } else {
            mRemoteViews.setImageViewResource(R.id.imgPlay, R.drawable.button_notify_play);
        }
        mRemoteViews.setTextViewText(R.id.tvSongTitle, getSongTitle());
        startForeground(NOTIFICATION_ID, mNotification);
    }

    /**
     * Music Binder
     */
    public class MusicBinder extends Binder {

        public MusicService getService() {
            Log.d(TAG, "getService: ");
            return MusicService.this;
        }
    }

    public void go() {
        Log.d(TAG, "go: ");
        mMediaPlayer.start();
        mMessage = "play";
        sendBroadcast();
        updateNotification();
    }

    public void pausePlayer() {
        Log.d(TAG, "pausePlayer: ");
        mMediaPlayer.pause();
        mPause = true;
        mMessage = "pause";
        sendBroadcast();
        updateNotification();
    }

    public void playNext() {
        Log.d(TAG, "playNext: ");
        mSongPosition++;
        if (mSongPosition >= mPlayList.size()) {
            mSongPosition = 0;
        }
        playSong();
    }

    public void playPrev() {
        Log.d(TAG, "playPrev: ");
        mSongPosition--;
        if (mSongPosition < 0) {
            mSongPosition = mPlayList.size() - 1;
        }
        playSong();
    }

    private void playRandom() {
        Log.d(TAG, "playRandom: ");
        Random random = new Random();
        mSongPosition = random.nextInt(mPlayList.size() - 1);
        playSong();
    }

    public String getSongTitle() {
        return mPlayList.get(mSongPosition).getTitle();
    }

    public String getSongDisplay() {
        return mPlayList.get(mSongPosition).getTitle() + " - " + mPlayList.get(mSongPosition)
                .getArtist();
    }

    public void setMode(int mode) {
        this.mMode = mode;
    }

    public int getCurPos() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDur() {
        return mMediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);
    }

    public boolean isPlayMusic() {
        return isPlaying() || mPause;
    }

    public int getMode() {
        return mMode;
    }

    private void sendBroadcast() {
        Log.d(TAG, "sendBroadcast: ");
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_STRING_ACTIVITY);
        intent.putExtra("message", mMessage);
        sendBroadcast(intent);
    }
}
