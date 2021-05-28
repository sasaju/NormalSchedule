package com.liflymark.normalschedule.logic.bean

import androidx.annotation.Keep
import androidx.compose.ui.graphics.Color
import com.liflymark.icytimetable.IcyTimeTableManager

@Keep
data class OneByOneCourseBean(
    val courseName: String,
    override val start: Int,
    override val end: Int,
    override val whichColumn: Int,
    val color: String
) : IcyTimeTableManager.BaseCourse()

fun getData(): List<OneByOneCourseBean>{
    return listOf(
        OneByOneCourseBean("点击右上角导入导入\n...\n...", 1, 2, 1, Color.Black.toString()),)
}