package com.oracle.visualize.util

import android.content.Context
import com.oracle.visualize.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    fun formatTime(date: Date, context: Context): String {
        val now   = Date()
        val diff  = now.time - date.time
        val mins  = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days  = TimeUnit.MILLISECONDS.toDays(diff)
        return when {
            mins  < 1  -> context.getString(R.string.time_just_now)
            mins  < 60 -> context.getString(R.string.time_mins_ago, mins.toInt())
            hours < 24 -> context.getString(R.string.time_hours_ago, hours.toInt())
            days  < 7  -> context.getString(R.string.time_days_ago, days.toInt())
            else       -> SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(date)
        }
    }
}
