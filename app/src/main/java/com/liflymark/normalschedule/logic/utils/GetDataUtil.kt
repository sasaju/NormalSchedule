package com.liflymark.normalschedule.logic.utils

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

internal object GetDataUtil {
    private val firstWeekMondayDate = Date(121, 2, 1)
    //获取当前完整的日期和时间
    @SuppressLint("SimpleDateFormat")
    fun getNowDateTime(): String? {
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        return sdf.format(Date())
    }

    @SuppressLint("SimpleDateFormat")
    fun getNowSimpleDateFormat(): SimpleDateFormat {
        val sdf = SimpleDateFormat()
        sdf.format(Date())
        return sdf
    }

    @SuppressLint("SimpleDateFormat")
    fun getNowWeekNum(): Int {
        val sdf = SimpleDateFormat("E")
        // Log.d("GEtDataUtil", sdf.format(Date()))
        return when(sdf.format(Date())){
            "周一" -> 1
            "周二" -> 2
            "周三" -> 3
            "周四" -> 4
            "周五" -> 5
            "周六" -> 6
            "周日" -> 7
            else -> 7
        }
    }

    fun dateMinusDate(first: SimpleDateFormat, second: SimpleDateFormat): Int{
        var result = 0
        if (first.calendar[1] == second.calendar[1]){
            result = first.calendar[6] - second.calendar[6]
//            print(first.calendar[6])
//            print("\n")
//            print(second.calendar[6].toString())
        }
        if (result < 0) {
            result = -1
        }
        return result
    }

    @SuppressLint("SimpleDateFormat")
    fun whichWeekNow(startDate: Date): Int {
        val sdf = SimpleDateFormat()
        sdf.format(startDate)
        val now = getNowSimpleDateFormat()
        val result = dateMinusDate(now, sdf)
        return result / 7 + 1
    }

    //@SuppressLint("SimpleDateFormat")
//    fun isNowWeek(nowData: SimpleDateFormat){
//        if (nowData. == getNowSimpleDateFormat()) {
//
//        }
//
//    }

    //获取当前时间
    @SuppressLint("SimpleDateFormat")
    fun getNowTime(): SimpleDateFormat {
        val sdf = SimpleDateFormat()
        sdf.format(Date())
        return sdf
    }

    fun getFirstWeekMondayDate() = firstWeekMondayDate


}