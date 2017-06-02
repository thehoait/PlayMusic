package com.example.asiantech.playmusic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.asiantech.playmusic.R
import com.example.asiantech.playmusic.model.Album

/**
 * AlbumAdapter.
 *
 * @author HoaHT
 */
class AlbumAdapter(val context: Context, val listAlbum: ArrayList<Album>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_list_album, parent, false)
        }
        val tvNameAlbum = view?.findViewById(R.id.tvNameAlbum) as TextView
        tvNameAlbum.text = listAlbum[position].albumName
        val imgAlbum = view.findViewById(R.id.imgAlbum) as ImageView
        if (listAlbum[position].albumImage == null) {
            imgAlbum.setImageBitmap(listAlbum[position].albumImage)
        } else {
            imgAlbum.setImageResource(R.drawable.img_album)
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return listAlbum[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listAlbum.size
    }

}