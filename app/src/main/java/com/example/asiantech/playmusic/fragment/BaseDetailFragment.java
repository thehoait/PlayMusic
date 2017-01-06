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
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * @author HoaHT
 */
@EFragment
public abstract class BaseDetailFragment extends Fragment implements OnItemListener {

    private static final String TAG = BaseDetailFragment.class.getSimpleName();

    @ViewById(R.id.recycleViewListSong)
    RecyclerView mRecycleListSong;

    @ViewById(R.id.tvTitle)
    TextView mTvTitle;

    @FragmentArg
    long albumId;

    protected MusicService mMusicService;
    private ArrayList<Song> mListSongAlbum;
    private SongAdapter mAdapter;

    @AfterViews
    void afterViews() {
        Log.d(TAG, "afterViews: ");
        ArrayList<Song> listSong = new ArrayList<>();
        if (getActivity() instanceof MainActivity_) {
            listSong = ((MainActivity_) getActivity()).getMListSong();
            mMusicService = ((MainActivity_) getActivity()).getMMusicService();
        }
        mListSongAlbum = new ArrayList<>();
        for (int i = listSong.size() - 1; i >= 0; i--) {
            Song song = listSong.get(i);
            if (getTypeId(song) == albumId) {
                mListSongAlbum.add(new Song(song.getId(), song.getTitle(), song.getArtistId(),
                        song.getArtist(), song.getAlbumId(), song.getAlbum(), song.getDisplay(),
                        false));
            }
        }
        mTvTitle.setText(getType(mListSongAlbum.get(0)));
        if (mMusicService.getListType().equals(MainActivity.LIST_TYPE_ALBUM)
                || mMusicService.getListType().equals(MainActivity.LIST_TYPE_ARTIST)) {
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

    protected abstract long getTypeId(Song song);

    protected abstract String getType(Song song);

    protected abstract void setListType();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
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

    @Click(R.id.imgGoBack)
    void onClickGoBack() {
        getActivity().onBackPressed();
    }

    @Override
    public void onDestroy() {
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onItemClick: ");
        mMusicService.setPlayList(mListSongAlbum);
        setListType();
        if (getActivity() instanceof MainActivity_) {
            ((MainActivity_) getActivity()).resetController();
        }
        mMusicService.setSong(position);
        mMusicService.playSong();
        updateSongPlay();
    }
}
