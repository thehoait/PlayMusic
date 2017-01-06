package com.example.asiantech.playmusic.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.asiantech.playmusic.MainActivity_;
import com.example.asiantech.playmusic.OnItemListener;
import com.example.asiantech.playmusic.R;
import com.example.asiantech.playmusic.adapter.SongAdapter;
import com.example.asiantech.playmusic.model.Song;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EFragment(R.layout.song_list_fragment)
public class SongListFragment extends Fragment {
    @ViewById(R.id.recycleViewListSong)
    RecyclerView mRecycleListSong;
    private SongAdapter mAdapter;
    private ArrayList<Song> mListSong;
    private OnItemListener mOnItemListener;

    @AfterViews
    void afterView() {
        Log.d("TAG SONG LIST FRAGMENT","afterView");
        mListSong = new ArrayList<>();
        if (getActivity() instanceof MainActivity_) {
            mListSong = ((MainActivity_) getActivity()).getMListSong();
            mOnItemListener = ((MainActivity_) getActivity()).getMOnItemListener();
        }
        mAdapter = new SongAdapter(getContext(), mListSong, mOnItemListener);
        mRecycleListSong.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleListSong.setAdapter(mAdapter);
    }

    public void notifySongListAdapter() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        } else {
            Log.d("sss", "adapter is null");
        }

    }
}
