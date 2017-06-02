package com.example.asiantech.playmusic.model

import java.io.Serializable

/**
 * @author hoaht
 */
class Song(var id: Long, var title: String, var artistId: Long, var artist: String,
           var albumId: Long, var album: String, var display: String, var isPlaying: Boolean) : Serializable
