package com.liflymark.normalschedule.logic.bean

import androidx.annotation.Keep
import androidx.compose.ui.graphics.Color

@Keep
data class OneByOneCourseBean(
    val courseName: String,
    val start: Int,
    val end: Int,
    val whichColumn: Int,
    val color: Color
)

fun getData(): List<OneByOneCourseBean>{
    return listOf(
        OneByOneCourseBean("点击右上角导入导入\n...\n...", 1, 2, 1, Color.Blue))
}