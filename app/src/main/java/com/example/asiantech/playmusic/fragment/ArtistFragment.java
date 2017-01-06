package com.example.asiantech.playmusic.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.asiantech.playmusic.MainActivity_;
import com.example.asiantech.playmusic.OnItemListener;
import com.example.asiantech.playmusic.R;
import com.example.asiantech.playmusic.adapter.ArtistAdapter;
import com.example.asiantech.playmusic.model.Artist;
import com.example.asiantech.playmusic.model.Song;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EFragment(R.layout.artist_fragment)
public class ArtistFragment extends Fragment implements OnItemListener{
    private ArrayList<Song> mListSong;
    private ArrayList<Artist> mListArtist;
    @ViewById(R.id.recycleViewListArtist)
    RecyclerView mRecyclerListArtist;

    @AfterViews
    void afterView() {
        Log.d("TAG ARTIST_FRAGMENT", "afterView");
        if (getActivity() instanceof MainActivity_) {
            mListSong = ((MainActivity_) getActivity()).getMListSong();
        }
        mListArtist = new ArrayList<>();
        for (int i = 0; i < mListSong.size(); i++) {
            boolean exists = false;
            for (int j = 0; j < mListArtist.size(); j++) {
                if (mListSong.get(i).getArtistId() == mListArtist.get(j).getArtistId()) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                mListArtist.add(new Artist(mListSong.get(i).getArtistId(),
                        mListSong.get(i).getArtist()));
            }
        }
        ArtistAdapter adapter = new ArtistAdapter(getContext(), mListArtist, this);
        mRecyclerListArtist.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerListArtist.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        ArtistDetailFragment_ fragment = new ArtistDetailFragment_();
        Bundle bundle = new Bundle();
        bundle.putLong("artistId", mListArtist.get(position).getArtistId());
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        transaction.add(R.id.rlContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
