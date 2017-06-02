package com.example.asiantech.playmusic.utils

import java.util.*

/**
 * @author HoaHT
 */

object StringUtils {

    fun stringForTime(timeMs: Int): String {
        val totalSeconds = timeMs / 1000

        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600

        val formatBuilder = StringBuilder()
        formatBuilder.setLength(0)
        val formatter = Formatter(formatBuilder, Locale.getDefault())
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }
}
