package com.liflymark.normalschedule.logic.utils

import android.annotation.SuppressLint
import android.util.Log
import com.liflymark.normalschedule.NormalScheduleApplication.Companion.settingData
import com.liflymark.normalschedule.NormalScheduleApplication.Companion.settingFirst
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import kotlin.math.abs

internal object GetDataUtil {
    // 修改开学日期仅需修改此处 如2023.2.20 则 GregorianCalendar(2021, 7, 23)，January-0 2022-2-28
    private val defaultFirstWeekMondayDate =  GregorianCalendar(2023, 1, 20)
    init {
        if (settingFirst.year!=0){
            defaultFirstWeekMondayDate.apply {
                set(Calendar.YEAR, settingFirst.year)
                set(Calendar.MONTH, settingFirst.month-1)
                set(Calendar.DAY_OF_MONTH, settingFirst.day)
            }
        }
    }
//    fun getFirstWeekMondayDate(year: Int, month: Int, day: Int):GregorianCalendar{
//        if (year == 0){
//            return defaultFirstWeekMondayDate
//        }
//        return GregorianCalendar().apply {
//            set(Calendar.YEAR, year)
//            set(Calendar.MONTH, month)
//            set(Calendar.DAY_OF_MONTH, day)
//        }
//    }

    // 输出当前开学日期的yyyy-MM-dd
    fun getNowKaiXueDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        return sdf.format(defaultFirstWeekMondayDate.time)
    }

    // 验证日期是否合理
    fun validateDateWithSimpleDateFormat(dateString: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        try {
            val date = LocalDate.parse(dateString, formatter)
            if (date.dayOfWeek.value!=1){
                return false
            }
            return true
        } catch (e: DateTimeParseException) {
            return false
        }
    }
    //获取当前完整的日期和时间
    @SuppressLint("SimpleDateFormat")
    fun getNowDateTime(): String {
        val sdf = SimpleDateFormat("MM月dd日")
        return sdf.format(Date())
    }

    @SuppressLint("SimpleDateFormat")
    fun thisStringIsToday(dateString: String):Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val today = sdf.format(Date())
        return today == dateString
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
        repeat(3){
            calendar.add(Calendar.DATE, 1)
            threeDay.add(sdf.format(calendar.time))
        }
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

    @SuppressLint("SimpleDateFormat")
    fun getTodayDateString():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())
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

    // add=1代表获取明天是第几周
    fun whichWeekNow(add:Int = 0): Int {
        val now = GregorianCalendar()
        val result = dateMinusDate(now, defaultFirstWeekMondayDate) + add
        if (result < 0){
            return 0
        }
        return result / 7
    }

    fun startSchool(): Boolean{
        val now = GregorianCalendar()
        val result = dateMinusDate(now, defaultFirstWeekMondayDate)
        return result >= 0
    }

    fun startSchoolDay(): Int {
        val now = GregorianCalendar()
        return dateMinusDate(now, defaultFirstWeekMondayDate)
    }

    //获取当前时间
    fun getNowTime(): GregorianCalendar {
        return GregorianCalendar()
    }

    fun getFirstWeekMondayDate() = defaultFirstWeekMondayDate


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

    fun hadOvered(endRow:Int):Boolean{
        val calendar = GregorianCalendar()
        val courseTimeStrList = getEndTime(endRow).split(":")
        var endHour = courseTimeStrList[0].toInt()
        val endMinute = courseTimeStrList[1].toInt()
        if (endHour > 12){
            calendar.set(Calendar.AM_PM, Calendar.PM)
            endHour -= 12
        } else {
            calendar.set(Calendar.AM_PM, Calendar.AM)
        }
        calendar.set(Calendar.HOUR, endHour)
        calendar.set(Calendar.MINUTE, endMinute)
        val nowCalendar = GregorianCalendar()
        return nowCalendar.after(calendar)
    }

    // return how many min are will start or end, but this function request 课间时间 < 上课时间
    fun hadStartedOrOver(startRow: Int, endRow: Int): Result {
        var nowType = "开始"
        var minusResult = 24 * 60 * 60 - 1
        for (nowLesson in startRow..endRow){
            // Time's format is string, Convert it to List<Int>
            val startTime = getStartTime(nowLesson).split(":").map { it.toInt() }
            val endTime = getEndTime(nowLesson).split(":").map { it.toInt() }

            // Set start and end Time by LocalTime
            val startDate = LocalTime.of(startTime[0], startTime[1])
            val endDate = LocalTime.of(endTime[0], endTime[1])
            val nowDate = LocalTime.now()
            if (nowDate.isAfter(startDate) && nowDate.isBefore(endDate)){
                nowType = "结束"
                minusResult = endDate.toSecondOfDay() - nowDate.toSecondOfDay()
                Log.d("GetDateUnit","startRow=$startRow endRow=$endRow")
                break
            } else {
                minusResult = minOf(abs(nowDate.toSecondOfDay()-startDate.toSecondOfDay()), minusResult)
            }
        }
        return Result(nowType,minusResult / 60)
    }

    // 计算第startRow节数提前advancedMillis毫秒时对应的stringerMills
    fun getAdvancedTimeMillis(
        startRow: Int,
        advancedMillis: Long
    ):Long {
        val startTime = getStartTime(startRow).split(":").map { it.toInt() }
        val cal = GregorianCalendar()
        cal.set(Calendar.HOUR, startTime[0])
        cal.set(Calendar.MINUTE, startTime[1])

        return cal.timeInMillis - advancedMillis
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

    // 当前列是星期几  星期一,星期二....
    fun getDayOfWeek(add: Int=0): String {
        val nowColumnCalendar = GregorianCalendar()
        return when (nowColumnCalendar.get(Calendar.DAY_OF_WEEK) + add) {
            1 -> "周日"
            2 -> "周一"
            3 -> "周二"
            4 -> "周三"
            5 -> "周四"
            6 -> "周五"
            7 -> "周六"
            else -> "一"
        }
    }
}

data class Result(val nowType: String, val minus: Int)