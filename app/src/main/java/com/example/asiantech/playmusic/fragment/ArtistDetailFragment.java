package com.example.asiantech.playmusic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.asiantech.playmusic.MainActivity;
import com.example.asiantech.playmusic.MainActivity_;
import com.example.asiantech.playmusic.OnItemListener;
import com.example.asiantech.playmusic.R;
import com.example.asiantech.playmusic.adapter.SongAdapter;
import com.example.asiantech.playmusic.model.Song;
import com.example.asiantech.playmusic.service.MusicService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * @author hoaht
 */
@EFragment(R.layout.list_detail_fragment)
public class ArtistDetailFragment extends Fragment implements OnItemListener {
    @ViewById(R.id.recycleViewListSong)
    RecyclerView mRecycleListSong;
    @ViewById(R.id.tvTitle)
    TextView mTvTitle;
    private long mArtistId;
    MusicService mMusicService;
    ArrayList<Song> mListSongArtist;
    SongAdapter mAdapter;

    @AfterViews
    void afterView() {
        Log.d("TAG DETAIL FRAGMENT", "afterView");
        ArrayList<Song> listSong = new ArrayList<>();
        if (getArguments() != null) {
            mArtistId = getArguments().getLong("artistId");
        }
        if (getActivity() instanceof MainActivity_) {
            listSong = ((MainActivity_) getActivity()).getMListSong();
            mMusicService = ((MainActivity_) getActivity()).getMMusicService();
        }
        mListSongArtist = new ArrayList<>();
        for (int i = listSong.size() - 1; i >= 0; i--) {
            if (listSong.get(i).getArtistId() == mArtistId) {
                Song song = listSong.get(i);
                mListSongArtist.add(new Song(song.getId(), song.getTitle(), song.getArtistId(),
                        song.getArtist(), song.getAlbumId(), song.getAlbum(), song.getDisplay(),
                        false));
            }
        }
        mTvTitle.setText(mListSongArtist.get(0).getArtist());
        if (mMusicService.getListType().equals(MainActivity.LIST_TYPE_ARTIST)) {
            for (int i = 0; i < mListSongArtist.size(); i++) {
                if (mMusicService.getSongPlayingId() == mListSongArtist.get(i).getId()) {
                    mListSongArtist.get(i).setPlaying(true);
                }
            }
        }
        mAdapter = new SongAdapter(getContext(), mListSongArtist, this);
        mRecycleListSong.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleListSong.setAdapter(mAdapter);
        if (mReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_STRING_ACTIVITY);
            getActivity().registerReceiver(mReceiver, intentFilter);
        }
    }

    @Click(R.id.imgGoBack)
    void onClickGoBack() {
        getActivity().onBackPressed();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG ARTIST_FRAGMENT", "onReceive");
            if (intent != null) {
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
        Log.d("TAG ARTIST_FRAGMENT", "onItemClick");
        mMusicService.setPlayList(mListSongArtist);
        mMusicService.setListType(MainActivity.LIST_TYPE_ARTIST);
        if (getActivity() instanceof MainActivity_) {
            ((MainActivity_) getActivity()).resetController();
        }
        mMusicService.setSong(position);
        mMusicService.playSong();
        updateSongPlay();
    }

    private void updateSongPlay() {
        for (int i = 0; i < mListSongArtist.size(); i++) {
            if (mListSongArtist.get(i).getId() == mMusicService.getSongPlayingId()) {
                mListSongArtist.get(i).setPlaying(true);
            } else {
                mListSongArtist.get(i).setPlaying(false);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }
}
