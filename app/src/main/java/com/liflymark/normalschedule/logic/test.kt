package com.liflymark.normalschedule.logic

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import com.liflymark.normalschedule.logic.utils.Convert
import java.util.*

@SuppressLint("SimpleDateFormat")
fun main(){
//    val a = GregorianCalendar().timeInMillis
//    val b = GregorianCalendar()
//    b.timeInMillis = a
//    print(b.timeInMillis)
//    val a = "#12c2e9"
//    val b = Convert.colorStringToLong(a)
//    val c = Color(b)
//    val d = c.value
//    println(d)
//
//    val e = (0xff12c2e9.toULong() and 0xffffffffUL) shl 32
//    println(e)
    val colorList = arrayListOf<String>()
    colorList.apply {
        add("#12c2e9")
        add("#376B78")
        add("#f64f59")
        add("#CBA689")
        add("#ffffbb33")
        add("#8202F2")
        add("#F77CC2")
        add("#4b5cc4")
        add("#426666")
        add("#40de5a")
        add("#f0c239")
        add("#725e82")
        add("#c32136")
        add("#b35c44")
    }
    for (i in colorList){
        var a = i.replace("#", "")
        while (true){
            if (a.length >= 8)
                break
            a = "f$a"
        }
        println("0x$a.toColorULong()")
    }
}