package com.liflymark.normalschedule.ui.show_timetable

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import java.text.SimpleDateFormat
import java.util.*

//@ExperimentalPagerApi
//@Composable
//fun myPagerSetting(
//    state: PagerState
//): Float {
//
//}

object a : FlingBehavior{
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        Log.d("Exfun.kt", initialVelocity.toString())
        return 10f
    }
}

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

fun getStartTime(rowNumber: Int): String {
    return when(rowNumber){
        1->"08:00"
        2->"08:55"
        3->"10:10"
        4->"11:05"
        5->"14:30"
        6->"15:25"
        7->"16:20"
        8->"17:15"
        9->"19:00"
        10->"19:55"
        11->"20:50"
        else->"00:00"
    }
}

fun getEndTime(rowNumber: Int): String {
    return when(rowNumber){
        1->"08:45"
        2->"09:40"
        3->"10:55"
        4->"11:50"
        5->"15:15"
        6->"16:10"
        7->"17:05"
        8->"18:00"
        9->"19:45"
        10->"20:40"
        11->"21:35"
        else->"00:00"
    }
}
private val firstWeekMondayDate = GetDataUtil.getFirstWeekMondayDate()
@SuppressLint("SimpleDateFormat")
fun isSelected(nowColumn: Int, whichWeek: Int, position: Int): Boolean {

    val nowDay = GetDataUtil.getNowWeekNum()
    val nowWeek = GetDataUtil.whichWeekNow(GetDataUtil.getFirstWeekMondayDate())
    val firstWeekMonthDayDate = GetDataUtil.getFirstWeekMondayDate()
    val sdf = SimpleDateFormat()
    sdf.format(firstWeekMondayDate)
    if (nowColumn==nowDay && position+1 == nowWeek &&
        GetDataUtil.dateMinusDate(GetDataUtil.getNowTime(),sdf) >= 0){
        return true
    }

    return false
}



fun getDayOfDate(nowColumn: Int, position: Int): String {
    val calendar = GregorianCalendar()
    calendar.time = firstWeekMondayDate
//        calendar.add(Calendar.DATE, 30)
    return when(nowColumn){
        0->{
            calendar.add(Calendar.DATE, nowColumn + position*7)
            (calendar.time.month+1).toString()
        }
        1->{
            calendar.add(Calendar.DATE, nowColumn-1 + position*7)
            calendar.time.date.toString()
        }
        2->{
            calendar.add(Calendar.DATE, nowColumn-1+position*7)
            calendar.time.date.toString()
        }
        3->{
            calendar.add(Calendar.DATE, nowColumn-1+position*7)
            calendar.time.date.toString()
        }
        4->{
            calendar.add(Calendar.DATE, nowColumn-1+position*7)
            calendar.time.date.toString()
        }
        5->{
            calendar.add(Calendar.DATE, nowColumn-1+position*7)
            calendar.time.date.toString()
        }
        6->{
            calendar.add(Calendar.DATE, nowColumn-1+position*7)
            calendar.time.date.toString()
        }
        7->{
            calendar.add(Calendar.DATE, nowColumn-1+position*7)
            calendar.time.date.toString()
        }
        else->{
            calendar.add(Calendar.DATE, nowColumn-1+position*7)
            calendar.time.date.toString()
        }
    }
}

fun getDayOfWeek(nowColumn: Int): String {// 当前列是星期几  星期一,星期二....
    return when (nowColumn) {
        1 -> "一"
        2 -> "二"
        3 -> "三"
        4 -> "四"
        5 -> "五"
        6 -> "六"
        7 -> "日"
        else -> "一"
    }
}

//fun main(){
//    val a = getNeededClassList(getData())
//    println(a.toString())
//}