package com.example.asiantech.playmusic.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.asiantech.playmusic.MainActivity
import com.example.asiantech.playmusic.OnItemListener
import com.example.asiantech.playmusic.R
import com.example.asiantech.playmusic.adapter.ArtistAdapter
import com.example.asiantech.playmusic.model.Artist
import com.example.asiantech.playmusic.model.Song
import kotlinx.android.synthetic.main.artist_fragment.*

/**
 * ArtistFragment.
 *
 * @author HoaHT
 */
class ArtistFragment : Fragment(), OnItemListener {

    private var listSong = ArrayList<Song>()
    private var listArtist = ArrayList<Artist>()
    private var adapter: ArtistAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity is MainActivity) {
            listSong = (activity as MainActivity).getListSong()
        }
        for (i in listSong.indices) {
            val exists = listArtist.indices.any {
                listSong[i].artistId == listArtist[it].artistId
            }
            if (!exists) {
                val song = listSong[i]
                listArtist.add(Artist(song.artistId, song.artist))
            }
        }
        adapter = ArtistAdapter(context, listArtist, this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.artist_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleViewListArtist.layoutManager = LinearLayoutManager(context)
        recycleViewListArtist.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        val transition = activity.supportFragmentManager.beginTransaction()
        val fragment = ArtistDetailFragment()
        val bundle = Bundle()
        bundle.putLong(BaseDetailFragment.KEY_ALBUM_ID, listArtist[position].artistId)
        fragment.arguments = bundle
        transition.add(R.id.rlContainer, fragment).addToBackStack(null).commit()
    }

}