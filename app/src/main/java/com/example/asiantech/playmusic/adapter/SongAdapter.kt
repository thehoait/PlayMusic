package com.example.asiantech.playmusic.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.asiantech.playmusic.OnItemListener
import com.example.asiantech.playmusic.R
import com.example.asiantech.playmusic.model.Song

/**
 * SongAdapter.
 *
 * @author HoaHT
 */
class SongAdapter(val context: Context, val listSong: ArrayList<Song>, val listener: OnItemListener?) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    override fun onBindViewHolder(holder: SongViewHolder?, position: Int) {
        val song: Song = listSong[position]
        holder?.tvTitle?.text = song.title
        holder?.tvArtist?.text = song.artist
        if (song.isPlaying) {
            holder?.tvTitle?.setTextColor(Color.parseColor("#FF4081"))
        } else {
            holder?.tvTitle?.setTextColor(Color.parseColor("#FFFFFF"))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SongViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_list_song, parent, false)
        return SongViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.findViewById(R.id.tvTitle) as TextView
        val tvArtist = itemView.findViewById(R.id.tvArtist) as TextView

        init {
            itemView.setOnClickListener {
                listener?.onItemClick(layoutPosition)
            }
        }
    }
}