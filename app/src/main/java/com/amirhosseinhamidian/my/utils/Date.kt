package com.amirhosseinhamidian.my.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object Date {

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val calendar: Calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(calendar.time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getYesterdayDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        return dateFormat.format(calendar.time)
    }

    fun calculateTimeInHourMinuteFormat(timeInSec: Int): String {
        val hour = timeInSec/3600
        val minute = timeInSec/60%60
        return "${hour}h : ${minute}m"
    }

}