package com.liflymark.normalschedule.logic.bean

import com.liflymark.icytimetable.IcyTimeTableManager

data class OneByOneCourseBean(
    val courseName: String,
    override val start: Int,
    override val end: Int,
    override val whichColumn: Int
) : IcyTimeTableManager.BaseCourse()