package com.liflymark.normalschedule.logic.bean

import androidx.annotation.Keep
import com.liflymark.icytimetable.IcyTimeTableManager

@Keep
data class OneByOneCourseBean(
    val courseName: String,
    override val start: Int,
    override val end: Int,
    override val whichColumn: Int,
    val color: String
) : IcyTimeTableManager.BaseCourse()