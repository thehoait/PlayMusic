package com.example.asiantech.playmusic.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.asiantech.playmusic.OnItemListener
import com.example.asiantech.playmusic.R
import com.example.asiantech.playmusic.model.Artist

/**
 * ArtistAdapter.
 *
 * @author HoaHT
 */
class ArtistAdapter(val context: Context, val listArtist: ArrayList<Artist>, val listener: OnItemListener?) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    override fun onBindViewHolder(holder: ArtistViewHolder?, position: Int) {
        holder?.tvNameArtist?.text = listArtist[position].artistName
    }

    override fun getItemCount(): Int {
        return listArtist.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_list_artist, parent, false)
        return ArtistViewHolder(view)
    }

    inner class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNameArtist = itemView.findViewById(R.id.tvNameArtist) as TextView

        init {
            itemView.setOnClickListener {
                listener?.onItemClick(layoutPosition)
            }
        }
    }
}