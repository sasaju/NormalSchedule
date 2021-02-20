package com.liflymark.normalschedule.ui.show_timetable

import android.graphics.Color
import com.liflymark.icytimetable.IcyColInfoDecoration

class MyColInfoDecoration(
    private val columnCount: Int, //列数    若输入为7 有7列     0-6列有值
    private val height: Int, //列信息的高度
    private val colTextColor: Int,
    private val selectedTextColor: Int,
    private val selectedBackGroundColor:Int,
    private val colTextSize: Float,
    private val backGroundColor: Int = Color.WHITE
) : IcyColInfoDecoration(
    columnCount, height,
    colTextColor, selectedTextColor, selectedBackGroundColor,colTextSize, backGroundColor
) {
    override fun isSelected(nowColumn: Int): Boolean {
        if (nowColumn==3)
            return true
        return false
    }



    override fun getDayOfDate(nowColumn: Int): String {
        return when(nowColumn){
            1->"18"
            2->"19"
            3->"20"
            4->"21"
            5->"22"
            6->"23"
            7->"24"
            else->"0"
        }
    }
}