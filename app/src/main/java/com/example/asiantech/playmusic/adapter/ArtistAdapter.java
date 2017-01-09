package com.example.asiantech.playmusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asiantech.playmusic.OnItemListener;
import com.example.asiantech.playmusic.R;
import com.example.asiantech.playmusic.model.Artist;

import java.util.ArrayList;

/**
 * @author hoaht
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    private final Context mContext;
    private final ArrayList<Artist> mListArtist;
    private final OnItemListener mListener;

    public ArtistAdapter(Context context, ArrayList<Artist> listArtist, OnItemListener listener) {
        this.mContext = context;
        this.mListArtist = listArtist;
        this.mListener = listener;
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        holder.mTvNameArtist.setText(mListArtist.get(position).getArtistName());
    }

    @Override
    public int getItemCount() {
        return mListArtist.size();
    }

    /**
     * Artist View Holder
     */
    class ArtistViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTvNameArtist;

        public ArtistViewHolder(View itemView) {
            super(itemView);
            mTvNameArtist = (TextView) itemView.findViewById(R.id.tvNameArtist);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(getLayoutPosition());
                    }
                }
            });
        }
    }
}
