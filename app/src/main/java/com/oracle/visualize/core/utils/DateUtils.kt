package com.oracle.visualize.core.utils

import android.content.Context
import com.oracle.visualize.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    fun formatTime(date: Date, context: Context, isShort: Boolean = false): String {
        val now   = Date()
        val diff  = now.time - date.time
        val mins  = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days  = TimeUnit.MILLISECONDS.toDays(diff)

        return if (isShort) {
            when {
                mins < 1 -> context.getString(R.string.time_just_now)
                mins < 60 -> context.getString(R.string.time_short_mins, mins.toInt())
                hours < 24 -> context.resources.getQuantityString(R.plurals.time_short_hours, hours.toInt(), hours.toInt())
                days < 7 -> context.resources.getQuantityString(R.plurals.time_short_days, days.toInt(), days.toInt())
                else -> SimpleDateFormat("MMM dd, yyyy", Locale.US).format(date)
            }
        } else {
            when {
                mins < 1 -> context.getString(R.string.time_just_now)
                mins < 60 -> context.getString(R.string.time_mins_ago, mins.toInt())
                hours < 24 -> context.resources.getQuantityString(R.plurals.time_hours_ago, hours.toInt(), hours.toInt())
                days < 7 -> context.resources.getQuantityString(R.plurals.time_days_ago, days.toInt(), days.toInt())
                days < 8 -> context.getString(R.string.time_a_week_ago)
                else -> SimpleDateFormat("MMM dd, yyyy", Locale.US).format(date)
            }
        }
    }
}
