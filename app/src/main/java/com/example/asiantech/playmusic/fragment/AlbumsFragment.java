package com.example.asiantech.playmusic.fragment;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.asiantech.playmusic.MainActivity_;
import com.example.asiantech.playmusic.R;
import com.example.asiantech.playmusic.adapter.AlbumAdapter;
import com.example.asiantech.playmusic.model.Album;
import com.example.asiantech.playmusic.model.Song;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author hoaht
 */
@EFragment(R.layout.albums_fragment)
public class AlbumsFragment extends Fragment {

    private static final String TAG = AlbumsFragment.class.getSimpleName();
    private ArrayList<Song> mListSong;
    private ArrayList<Album> mListAlbum;
    @ViewById(R.id.gvAlbums)
    GridView mGvAlbums;

    @AfterViews
    void afterView() {
        Log.d(TAG, "afterView: ");
        if (getActivity() instanceof MainActivity_) {
            mListSong = ((MainActivity_) getActivity()).getMListSong();
        }
        mListAlbum = new ArrayList<>();
        for (int i = 0; i < mListSong.size(); i++) {
            boolean exists = false;
            for (int j = 0; j < mListAlbum.size(); j++) {
                if (mListSong.get(i).getAlbumId() == mListAlbum.get(j).getAlbumId()) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                mListAlbum.add(new Album(mListSong.get(i).getAlbumId(), mListSong.get(i).getAlbum(),
                        getImageBitmap(mListSong.get(i).getAlbumId())));
            }
        }
        AlbumAdapter adapter = new AlbumAdapter(getContext(), mListAlbum);
        mGvAlbums.setAdapter(adapter);

        mGvAlbums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                transaction.add(R.id.rlContainer, AlbumDetailFragment_
                        .builder()
                        .albumId(mListAlbum.get(position).getAlbumId())
                        .build());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private Bitmap getImageBitmap(long albumId) {
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),
                    albumArtUri);
            bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
        } catch (IOException e) {
            Log.e(TAG, "getImageBitmap: " + e);
        }
        return bitmap;
    }
}
