package com.liflymark.normalschedule.logic.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

internal object GetDataUtil {
    // 修改开学日期仅需修改此处 如2021.8.23 则 GregorianCalendar(2021, 7, 23)，January-0
    private val firstWeekMondayDate =  GregorianCalendar(2021, 7, 23)
    //获取当前完整的日期和时间
    @SuppressLint("SimpleDateFormat")
    fun getNowDateTime(): String? {
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        return sdf.format(Date())
    }

    @SuppressLint("SimpleDateFormat")
    fun getNowMonth(whichColumn:Int, whichWeek:Int): String {
        val sdf = SimpleDateFormat("MM月dd日")
        val column = when(whichColumn){
            1 -> "周一"
            2 -> "周二"
            3 -> "周三"
            4 -> "周四"
            5 -> "周五"
            6 -> "周六"
            7 -> "周日"
            else -> "错误"
        }
        return sdf.format(Date())+" | "+column
    }

    @SuppressLint("SimpleDateFormat")
    fun getThreeDay():List<String>{
        val threeDay = mutableListOf<String>()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val calendar = GregorianCalendar()
        calendar.add(Calendar.DATE, -1)
        threeDay.add(sdf.format(calendar.time))
        repeat(2){
            calendar.add(Calendar.DATE, 1)
            threeDay.add(sdf.format(calendar.time))
        }
        threeDay.add("2021-08-29")
        return threeDay.toList()
    }

    @SuppressLint("SimpleDateFormat")
    fun getNowSimpleDateFormat(): SimpleDateFormat {
        val sdf = SimpleDateFormat()
        sdf.format(Date())
        return sdf
    }

    /**
     * 根据毫秒转换为yyyy-MM-dd
     */
    @SuppressLint("SimpleDateFormat")
    fun getDateStrByMillis(millis:Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date = GregorianCalendar()
        date.timeInMillis = millis
        return sdf.format(date.time)
    }

    /**
     * 计算几天之后的日期并转换为毫秒
     */
    fun getDayMillis(afterToady:Int):Long{
        val calendar = GregorianCalendar()
        calendar.add(Calendar.DATE, afterToady)
        return calendar.timeInMillis
    }

    /**
     * 判断当前周几
     * 周一-》1
     * 周二-》2 ...
     */
    fun getNowWeekNum(): Int {
        val now = GregorianCalendar()
        // Log.d("GEtDataUtil", sdf.format(Date()))
        return when(now.get(Calendar.DAY_OF_WEEK)){
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 7
        }
    }

    fun dateMinusDate(first: GregorianCalendar, second: GregorianCalendar): Int{
        /**
         * 如果年数一致则DAY_OF_YEAR直接相减
         * 否则就进行判断
         */
        var result = 0
        val firstDayOfYear = first.get(Calendar.DAY_OF_YEAR)
        val secondDayOfYear =  second.get(Calendar.DAY_OF_YEAR)
        result = if (first.get(Calendar.YEAR)==second.get(Calendar.YEAR)){
            firstDayOfYear - secondDayOfYear
        } else {
            val firstAllYearDay = if (first.isLeapYear(first.get(Calendar.YEAR))){ 366 } else { 365 }
            val secondAllYearDay = if (first.isLeapYear(first.get(Calendar.YEAR))){ 366 } else { 365 }
            if (first.after(second)){
                secondAllYearDay - secondDayOfYear + firstDayOfYear
            } else{
                -(firstAllYearDay-firstDayOfYear + secondDayOfYear)
            }
        }
        return result
    }

    fun whichWeekNow(): Int {
        val now = GregorianCalendar()
        val result = dateMinusDate(now, firstWeekMondayDate)
        if (result < 0){
            return 0
        }
        return result / 7
    }

    fun startSchool(): Boolean{
        val now = GregorianCalendar()
        val result = dateMinusDate(now, firstWeekMondayDate)
        return result >= 0
    }

    fun startSchoolDay(): Int {
        val now = GregorianCalendar()
        return dateMinusDate(now, firstWeekMondayDate)
    }

    //获取当前时间
    fun getNowTime(): GregorianCalendar {
        return GregorianCalendar()
    }

    fun getFirstWeekMondayDate() = firstWeekMondayDate


    /**
     * 2021年-year-2021
     * 2月-month-1
     */
    fun getMonthAllDay(year:Int,month:Int):List<String>{
        val calendar = GregorianCalendar()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        return (1..maxDay).toList().map { it.toString() }
    }

    fun getCalendarByMillis(millis: Long): GregorianCalendar {
        val calendar = GregorianCalendar()
        calendar.timeInMillis = millis
        return calendar
    }


}