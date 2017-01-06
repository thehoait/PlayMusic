package com.example.asiantech.playmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asiantech.playmusic.R;
import com.example.asiantech.playmusic.model.Album;

import java.util.ArrayList;

/**
 * @author hoaht
 */
public class AlbumAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Album> mListAlbum;

    public AlbumAdapter(Context context, ArrayList<Album> listAlbum) {
        this.mContext = context;
        this.mListAlbum = listAlbum;
    }

    @Override
    public int getCount() {
        return mListAlbum.size();
    }

    @Override
    public Object getItem(int position) {
        return mListAlbum.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_album, parent,
                    false);
        }
        TextView tvNameAlbum = (TextView) convertView.findViewById(R.id.tvNameAlbum);
        tvNameAlbum.setText(mListAlbum.get(position).getAlbumName());
        ImageView imgAlbum = (ImageView) convertView.findViewById(R.id.imgAlbum);
        if (mListAlbum.get(position).getAlbumImage() != null) {
            imgAlbum.setImageBitmap(mListAlbum.get(position).getAlbumImage());
        } else {
            imgAlbum.setImageResource(R.drawable.img_album);
        }
        return convertView;
    }
}
