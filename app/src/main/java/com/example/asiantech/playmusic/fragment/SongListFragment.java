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

/**
 * @author hoaht
 */
@EFragment(R.layout.song_list_fragment)
public class SongListFragment extends Fragment {

    private static final String TAG = SongListFragment.class.getSimpleName();

    @ViewById(R.id.recycleViewListSong)
    RecyclerView mRecycleListSong;

    private SongAdapter mAdapter;
    private OnItemListener mOnItemListener;

    @AfterViews
    void afterView() {
        Log.d(TAG, "afterView: ");
        ArrayList<Song> listSong = new ArrayList<>();
        if (getActivity() instanceof MainActivity_) {
            listSong = ((MainActivity_) getActivity()).getMListSong();
            mOnItemListener = ((MainActivity_) getActivity()).getMOnItemListener();
        }
        mAdapter = new SongAdapter(getContext(), listSong, mOnItemListener);
        mRecycleListSong.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleListSong.setAdapter(mAdapter);
    }

    public void notifySongListAdapter() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        } else {
            Log.d(TAG, "notifySongListAdapter: adapter is null");
        }

    }
}
