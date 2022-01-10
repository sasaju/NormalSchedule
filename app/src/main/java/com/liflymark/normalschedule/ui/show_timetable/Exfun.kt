package com.liflymark.normalschedule.ui.show_timetable

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.bean.getInitial
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import com.liflymark.normalschedule.ui.app_widget_miui.DayNewWidgetProvider
import java.util.*


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

fun isSelected(nowColumn: Int, position: Int): Boolean {

    val nowDay = GetDataUtil.getNowWeekNum()
    val nowWeek = GetDataUtil.whichWeekNow()
    if (nowColumn==nowDay && position == nowWeek && GetDataUtil.startSchool()){
        return true
    }
    return false
}



fun getDayOfDate(nowColumn: Int, position: Int): String {
    val calendar = GregorianCalendar()
    calendar.time = firstWeekMondayDate.time
//        calendar.add(Calendar.DATE, 30)
    return when(nowColumn){
        0->{
            calendar.add(Calendar.DATE, nowColumn + position*7)
            (calendar.get(Calendar.MONTH)+1).toString()
        }
        else->{
            calendar.add(Calendar.DATE, nowColumn-1+position*7)
            calendar.get(Calendar.DAY_OF_MONTH).toString()
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

fun Int.floorMod(other: Int): Int = when (other) {
    0 -> this
    else -> this - floorDiv(other) * other
}

fun updateWidget(context: Context) {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val thisAppWidget = ComponentName(
        context.packageName,
        DayNewWidgetProvider::class.java.name
    )
    val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
    Convert.onUpdateMIUIWidget(context, appWidgetManager, appWidgetIds)
}
// snackbar
//val LocalShowSnackbar = compositionLocalOf { false }
//val LocalSnackText = staticCompositionLocalOf { "" }

//fun main(){
//    val a = getNeededClassList(getData())
//    println(a.toString())
//}