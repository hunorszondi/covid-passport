package com.hunorszondi.covidpassport.utils

import java.lang.Math.abs
import java.util.*
import java.text.SimpleDateFormat

/**
 * Used for getting an elegant date format from a timestamp
 */
class DateUtil {
    companion object {
        fun getDateTimeFormat(): SimpleDateFormat {
            return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        }
    }
}
