package com.liflymark.normalschedule.ui

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun main(){
    val a = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
    val b = Calendar.getInstance()
    val month = b.get(Calendar.MONTH)
    print(b.toString())
}