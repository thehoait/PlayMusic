package com.example.asiantech.playmusic.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class Artist {
    private long artistId;
    private String artist;
}
