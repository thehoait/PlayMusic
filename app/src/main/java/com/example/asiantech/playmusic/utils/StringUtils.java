package com.example.asiantech.playmusic.utils;

import java.util.Formatter;
import java.util.Locale;

/**
 * @author HoaHT
 */

public final class StringUtils {

    private StringUtils() {
    }

    public static String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        StringBuilder formatBuilder = new StringBuilder();
        formatBuilder.setLength(0);
        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
}
