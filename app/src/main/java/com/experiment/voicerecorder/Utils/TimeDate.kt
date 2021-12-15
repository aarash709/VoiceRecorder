package com.experiment.voicerecorder.Utils

import java.text.SimpleDateFormat
import java.util.*

class TimeDate {
    val sdf = SimpleDateFormat("yymmdd", Locale.getDefault())
    val date = sdf.format(Date())
    val calendar =Calendar.getInstance()
    val month = calendar.get(Calendar.MONTH)
    val year = calendar.get(Calendar.YEAR)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val timeMillis = sdf.format(System.currentTimeMillis())
//    Timber.e("date: $date")
//    Timber.e("time : $year$month$day")
//    Timber.e("time : $timeMillis")

}