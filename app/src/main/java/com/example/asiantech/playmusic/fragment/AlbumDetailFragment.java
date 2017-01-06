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

@EFragment(R.layout.list_detail_fragment)
public class AlbumDetailFragment extends Fragment implements OnItemListener {
    @ViewById(R.id.recycleViewListSong)
    RecyclerView mRecycleListSong;
    @ViewById(R.id.tvTitle)
    TextView mTvTitle;
    private long mAlbumId;
    private MusicService mMusicService;
    private ArrayList<Song> mListSongAlbum;
    private SongAdapter mAdapter;

    @AfterViews
    void afterView() {
        Log.d("TAG DETAIL FRAGMENT", "afterView");
        ArrayList<Song> listSong = new ArrayList<>();
        if (getArguments() != null) {
            mAlbumId = getArguments().getLong("albumId");
        }
        if (getActivity() instanceof MainActivity_) {
            listSong = ((MainActivity_) getActivity()).getMListSong();
            mMusicService = ((MainActivity_) getActivity()).getMMusicService();
        }
        mListSongAlbum = new ArrayList<>();
        for (int i = listSong.size() - 1; i >= 0; i--) {
            if (listSong.get(i).getAlbumId() == mAlbumId) {
                Song song = listSong.get(i);
                mListSongAlbum.add(new Song(song.getId(), song.getTitle(), song.getArtistId(),
                        song.getArtist(), song.getAlbumId(), song.getAlbum(), song.getDisplay(),
                        false));
            }
        }
        mTvTitle.setText(mListSongAlbum.get(0).getAlbum());
        if (mMusicService.getListType().equals(MainActivity.LIST_TYPE_ALBUM)) {
            for (int i = 0; i < mListSongAlbum.size(); i++) {
                if (mMusicService.getSongPlayingId() == mListSongAlbum.get(i).getId()) {
                    mListSongAlbum.get(i).setPlaying(true);
                }
            }
        }
        mAdapter = new SongAdapter(getContext(), mListSongAlbum, this);
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
        mMusicService.setPlayList(mListSongAlbum);
        mMusicService.setListType(MainActivity.LIST_TYPE_ALBUM);
        if (getActivity() instanceof MainActivity_) {
            ((MainActivity_) getActivity()).resetController();
        }
        mMusicService.setSong(position);
        mMusicService.playSong();
        updateSongPlay();
    }

    private void updateSongPlay() {
        for (int i = 0; i < mListSongAlbum.size(); i++) {
            if (mListSongAlbum.get(i).getId() == mMusicService.getSongPlayingId()) {
                mListSongAlbum.get(i).setPlaying(true);
            } else {
                mListSongAlbum.get(i).setPlaying(false);
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
