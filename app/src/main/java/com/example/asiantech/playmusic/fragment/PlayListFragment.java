package com.example.asiantech.playmusic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.asiantech.playmusic.MainActivity;
import com.example.asiantech.playmusic.MainActivity_;
import com.example.asiantech.playmusic.OnItemListener;
import com.example.asiantech.playmusic.R;
import com.example.asiantech.playmusic.adapter.SongAdapter;
import com.example.asiantech.playmusic.model.Song;
import com.example.asiantech.playmusic.service.MusicService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * @author hoaht
 */
@EFragment(R.layout.play_list_fragment)
public class PlayListFragment extends Fragment implements OnItemListener {
    @ViewById(R.id.recycleViewPlayList)
    RecyclerView mRecycleListSong;
    private SongAdapter mAdapter;
    private MusicService mMusicService;
    private ArrayList<Song> mPlayList;

    @AfterViews
    void afterView() {
        Log.d("TAG PLAY LIST FRAGMENT", "afterView");
        mPlayList = new ArrayList<>();
        if (getActivity() instanceof MainActivity_) {
            mMusicService = ((MainActivity_) getActivity()).getMMusicService();
        }
        if (mMusicService != null) {
            mPlayList = mMusicService.getPlayList();
        }
        mAdapter = new SongAdapter(getContext(), mPlayList, this);
        mRecycleListSong.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleListSong.setAdapter(mAdapter);
        mRecycleListSong.scrollToPosition(mMusicService.getSongPosition());
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_STRING_ACTIVITY);
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG PLAY LIST FRAGMENT", "onReceive");
            if (intent.getExtras() != null) {
                String message = intent.getExtras().getString("message");
                if (message != null) {
                    switch (message) {
                        case "play":
                            updateSongPlay();
                            break;
                    }
                }
            }
        }
    };

    @Override
    public void onItemClick(int position) {
        mMusicService.setSong(position);
        mMusicService.playSong();
    }

    private void updateSongPlay() {
        Log.d("TAG PLAY LIST FRAGMENT", "updateSongPlay");
        for (int i = 0; i < mPlayList.size(); i++) {
            if (mPlayList.get(i).getId() == mMusicService.getSongPlayingId()) {
                mPlayList.get(i).setPlaying(true);
            } else {
                mPlayList.get(i).setPlaying(false);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
