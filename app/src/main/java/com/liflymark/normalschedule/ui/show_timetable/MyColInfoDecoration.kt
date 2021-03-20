package com.liflymark.normalschedule.ui.show_timetable

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import com.liflymark.icytimetable.IcyColInfoDecoration
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import java.text.SimpleDateFormat
import java.util.*


// 设置周数相关内容
class MyColInfoDecoration(
        private val columnCount: Int, //列数    若输入为7 有7列     0-6列有值
        private val height: Int, //列信息的高度
        private val colTextColor: Int,
        private val selectedTextColor: Int,
        private val selectedBackGroundColor:Int,
        private val colTextSize: Float,
        private val backGroundColor: Int = Color.WHITE,
        private val position: Int
) : IcyColInfoDecoration(
    columnCount, height,
    colTextColor, selectedTextColor, selectedBackGroundColor,colTextSize, backGroundColor, position
) {
    private val firstWeekMondayDate = GetDataUtil.getFirstWeekMondayDate()
    @SuppressLint("SimpleDateFormat")
    override fun isSelected(nowColumn: Int, whichWeek: Int): Boolean {

        val nowDay = GetDataUtil.getNowWeekNum()
        val nowWeek = GetDataUtil.whichWeekNow(GetDataUtil.getFirstWeekMondayDate())
        val firstWeekMonthDayDate = GetDataUtil.getFirstWeekMondayDate()
        val sdf = SimpleDateFormat()
        sdf.format(firstWeekMondayDate)
//        Log.d("postion",(position+1).toString())
//        // Log.d("nowWeek", nowWeek.toString())
//        Log.d("position+1 == nowWeek", (position+1 == nowWeek).toString())
        if (nowColumn==nowDay && position+1 == nowWeek &&
                GetDataUtil.dateMinusDate(GetDataUtil.getNowTime(),sdf) >= 0){
            return true
        }

        return false
    }



    override fun getDayOfDate(nowColumn: Int, position: Int): String {
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
}