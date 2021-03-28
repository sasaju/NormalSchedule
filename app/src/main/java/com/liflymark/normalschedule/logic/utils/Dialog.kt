package com.liflymark.normalschedule.logic.utils

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.ui.show_timetable.ShowTimetableActivity

object Dialog {

    fun getClassDetailDialog(_context: Context, courseBean: CourseBean): MaterialDialog {
        val dialog = MaterialDialog(_context)
                .customView(R.layout.item_course_detail)

        val customView = dialog.getCustomView()
        val courseName = customView.findViewById<AppCompatTextView>(R.id.tv_item)
        val courseTime = customView.findViewById<AppCompatTextView>(R.id.et_time)
        val weekNum = customView.findViewById<AppCompatTextView>(R.id.et_weeks)
        val courseTeacher = customView.findViewById<AppCompatTextView>(R.id.et_teacher)
        val courseRoom = customView.findViewById<AppCompatTextView>(R.id.et_room)

        val oneList = courseBean.classWeek.whichIs1()
        val courseStartToEnd =
            "    第${courseBean.classSessions} - ${courseBean.classSessions+courseBean.continuingSession}节"


        courseName.text= courseBean.courseName
        courseTime.text = getWeekNumFormat(oneList)
        weekNum.text = when(courseBean.classDay){
            1 -> "周一$courseStartToEnd"
            2 -> "周二$courseStartToEnd"
            3 -> "周三$courseStartToEnd"
            4 -> "周四$courseStartToEnd"
            5 -> "周五$courseStartToEnd"
            6 -> "周六$courseStartToEnd"
            7 -> "周日$courseStartToEnd"
            else -> "错误"
        }
        courseTeacher.text = courseBean.teacher
        courseRoom.text = courseBean.teachingBuildName

        val closeButton = customView.findViewById<AppCompatTextView>(R.id.ib_delete)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    private fun String.whichIs1(): List<Int>{
        val oneList = mutableListOf<Int>()
        for (i in this.indices){
            when(this[i].toString()){
                "1" -> oneList.add(i+1)
                else -> Log.d("Dialog", "异常")
            }
        }
        return oneList
    }

    private fun getWeekNumFormat(oneList: List<Int>): String {
        var courseTimeFormat = "第"
        var type = 0
        for (i in oneList.indices-1){
            if (i == 0){
                continue
            }
//            type = if (oneList[i+1]-oneList[i] == 1 && oneList[i]-oneList[i-1] == 1) {
//                // 连续
//                0
//            } else if (oneList[i+1]-oneList[i] == 2 && oneList[i]-oneList[i-1] == 2){
//                // 单周或双周
//                1
//            } else {
//                // 未知
//                2
//            }
        }
        when (type){
            0 ->{
                Log.d("Dialog", oneList.toString())
                courseTimeFormat += " ${oneList.first()} - ${oneList.last()} 周"
            }
            1 ->{
                courseTimeFormat += if (oneList.first() % 2 == 1) {
                    "${oneList.first() - oneList.last()} 单周"
                } else {
                    "${oneList.first() - oneList.last()} 双周"
                }
            }
            2 ->{
                for (i in oneList){
                    courseTimeFormat += "$i,"
                }
                courseTimeFormat += "周"
            }
        }
        return courseTimeFormat
    }

}