package com.example.asiantech.playmusic.model;

import android.graphics.Bitmap;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author hoaht
 */
@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class Album {
    private long albumId;
    private String albumName;
    private Bitmap albumImage;
}
