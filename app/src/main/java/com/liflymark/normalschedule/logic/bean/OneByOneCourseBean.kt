package com.liflymark.normalschedule.logic.bean

import androidx.annotation.Keep
import androidx.compose.ui.graphics.Color
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.utils.Convert.color

/**课程表页面真正显示时拿到的类
 * @param color 这是个历史遗留值，这个值是因为在显示班级课程时需要用到，我懒得修改代码所以没有移除它
 * @param twoColorList 事实上它有三个item，第一个为纯色时的配色，第二、三个为它渐变色时的起止色
 * 如果你希望更改它，请特别小心
 * 同时它并不是一个好的写法，它同时调用了Repository和Convert单例类
 */
@Keep
data class OneByOneCourseBean(
    val courseName: String,
    val start: Int,
    val end: Int,
    val whichColumn: Int,
    val color: Color,
    val twoColorList: List<Color> = Repository.getDefaultString()[0].map { it.color }
)

fun getData(): List<OneByOneCourseBean>{
    return listOf(
        OneByOneCourseBean("点击右上角导入导入\n...\n...", 1, 2, 1, Color.Transparent)
    )
}