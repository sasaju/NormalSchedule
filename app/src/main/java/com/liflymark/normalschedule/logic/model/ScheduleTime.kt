package com.liflymark.normalschedule.logic.model

/*
此数据类为每节课的上课时间，便于修改和设置上课时间
严格来讲作为河大专用课程表，不需要此功能，河大无论季节都没有修改过作息时间
为防止以后频繁的修改作息，故添加
 */


data class ScheduleTime(
        val node: Int,
        var startTime: String,
        var endTime: String,
        var timeTable: Int = 1
)
