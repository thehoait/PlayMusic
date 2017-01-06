package com.example.asiantech.playmusic.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author hoaht
 */
@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class Song implements Serializable {
    private long id;
    private String title;
    private long artistId;
    private String artist;
    private long albumId;
    private String album;
    private String display;
    private boolean isPlaying;
}
