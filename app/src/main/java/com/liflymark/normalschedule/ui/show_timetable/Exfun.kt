package com.liflymark.normalschedule.ui.show_timetable

import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean

fun getNeededClassList(originData: List<OneByOneCourseBean>): List<List<OneByOneCourseBean>>{
    val res = mutableListOf<MutableList<OneByOneCourseBean>>()
    repeat(7){
        res.add(mutableListOf())
    }
    for (i in originData){
        if (i.whichColumn < 8)
            res[i.whichColumn-1].add(i)
    }
    for (t in res){
        t.sortBy { oneClass -> oneClass.start }
    }
    return res
}


//fun main(){
//    val a = getNeededClassList(getData())
//    println(a.toString())
//}