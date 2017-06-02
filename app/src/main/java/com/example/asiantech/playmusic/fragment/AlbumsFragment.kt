package com.example.asiantech.playmusic.fragment

import android.content.ContentUris
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.asiantech.playmusic.MainActivity
import com.example.asiantech.playmusic.R
import com.example.asiantech.playmusic.adapter.AlbumAdapter
import com.example.asiantech.playmusic.model.Album
import com.example.asiantech.playmusic.model.Song
import kotlinx.android.synthetic.main.albums_fragment.*
import java.io.IOException

/**
 * AlbumsFragment.
 *
 * @author HoaHT
 */
class AlbumsFragment : Fragment() {

    private var listSong = ArrayList<Song>()
    private var listAlbum = ArrayList<Album>()
    private var adapter: AlbumAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity is MainActivity) {
            listSong = (activity as MainActivity).getListSong()
        }
        for (i in listSong.indices) {
            val exists = (0..listAlbum.size - 1).any { listSong[i].albumId == listAlbum[it].albumId }
            if (!exists) {
                val song: Song = listSong[i]
                listAlbum.add(Album(song.albumId, song.album, getImageBitmap(song.albumId)))
            }
        }
        adapter = AlbumAdapter(context, listAlbum)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.albums_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gvAlbums.adapter = adapter

        gvAlbums.setOnItemClickListener { _, _, position, _ ->
            val transaction = activity.supportFragmentManager.beginTransaction()
            val fragment = AlbumDetailFragment()
            val bundle = Bundle()
            bundle.putLong(BaseDetailFragment.KEY_ALBUM_ID, listAlbum[position].albumId)
            fragment.arguments = bundle
            transaction.add(R.id.rlContainer, fragment).addToBackStack(null).commit()
        }
    }

    private fun getImageBitmap(albumId: Long): Bitmap? {
        var bitmap: Bitmap? = null
        val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
        val albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId)
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, albumArtUri)
            bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true)
        } catch (e: IOException) {
            Log.e("Error get bitmap", e.toString())
        }
        return bitmap
    }
}