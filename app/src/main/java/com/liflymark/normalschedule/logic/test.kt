package com.liflymark.normalschedule.logic

import android.annotation.SuppressLint
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.GetDataUtil.getNowTime
import com.liflymark.normalschedule.ui.class_course.getNowWeek
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun main(){
    val a = GregorianCalendar().timeInMillis
    val b = GregorianCalendar()
    b.timeInMillis = a
    print(b.timeInMillis)
}