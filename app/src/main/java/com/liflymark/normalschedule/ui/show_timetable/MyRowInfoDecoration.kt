package com.liflymark.normalschedule.ui.show_timetable

import com.liflymark.icytimetable.IcyRowTimeInfoDecoration


class MyRowInfoDecoration(
    width: Int,
    perCourseHeight: Int,
    textColor: Int,
    numberTextSize: Float,
    timeTextSize: Float,
    rowNumberList: List<IcyRowInfo>,
    totalCoursePerDay: Int
) : IcyRowTimeInfoDecoration(
    width,
    perCourseHeight,
    textColor,
    numberTextSize,
    timeTextSize,
    rowNumberList,
    totalCoursePerDay
) {
    override fun getStartTime(rowNumber: Int): String {
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

    override fun getEndTime(rowNumber: Int): String {
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
}