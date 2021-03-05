package com.liflymark.normalschedule.ui

import android.annotation.SuppressLint
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import com.liflymark.normalschedule.logic.utils.GetDataUtil.dateMinusDate
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun main(){
//    val a = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
//    val b = Calendar.getInstance()
//    val month = b.get(Calendar.MONTH)
//    print(b.toString())
    val firstWeekMonthDayDate = GetDataUtil.getFirstWeekMondayDate()
    val sdf = SimpleDateFormat()
    sdf.format(firstWeekMonthDayDate)
//    dateMinusDate(sdf, GetDataUtil.getNowTime())
//    print(GetDataUtil.whichWeekNow(firstWeekMonthDayDate))
//    print(GetDataUtil.dateMinusDate(sdf, GetDataUtil.getNowTime()))
//    print("\n")
//    println(sdf.calendar.toString())
//    println(GetDataUtil.getNowTime().calendar.toString())
//    println(GetDataUtil.whichWeekNow(firstWeekMonthDayDate).toString())
    print(GetDataUtil.dateMinusDate(GetDataUtil.getNowTime(),sdf))
}