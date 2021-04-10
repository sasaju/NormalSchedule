package com.liflymark.normalschedule.logic.utils

import android.content.Context
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.ui.add_course.AddCourseActivity


object Dialog {

    fun getContractDialog(_context: Context): MaterialDialog {
        val dialog = MaterialDialog(_context)
                .title(text = "APP用户协议")
                .message(text = "版本：1.0\n" + " 一）在此特别提醒用户在成为《河大课表》用户之前，请认真阅读本《APP用户协议》（以下简称“协议”）。" +
                        "确保用户充分理解本协议中各条款，请审慎阅读并选择接受或不接受本协议。" +
                        "同意并点击确认本协议条款，才能成为本APP用户，并享受各类服务。登录、使用等行为将视为对本协议的接受，并同意接受本协议各项条款的约束。" +
                        "若不同意本协议，或对本协议中的条款存在任何疑问，请立即停止使用该程序，并可以选择不使用APP服务。\n\n" +
                        "二）隐私及信息准确性：\n" +
                        "1.开发者会尽量保证课程、成绩等信息准确，但不会对任何信息的准确性负责，由于信息不准确造成的不良后果(如旷课)，开发者概不负责\n"+
                        "2.开发者承诺本软件及其提供服务的服务器不会存储你的任何个人信息，即使是开发者也无法查看你的任何信息(包括但不限于您的姓名、性别、专业、课程表内容)\n" +
                        "3.如果您由于木马入侵、连接不安全网络等原因造成的在使用本app时造成信息泄露和损失由您本人负责\n" +
                        "\n三）信息来源及其准确性:\n" + "1.本软件所有行为未获得河北大学官方及其任何组织支持，并非官方指定的教务软件，所有功能仅为个人开发" +
                        "\n2.本软件本软件会模拟您的登陆教务系统操作，由于您的任何行为造成无法登陆，开发者概不负责" +
                        "\n3.如河北大学官方禁止部分信息的抓取和发布，开发者将立即停止该功能，并尽最大努力消除影响。" +
                        "\n作者：1289142675@qq.com\n" +
                                "著作权归作者所有。")
                .positiveButton(text = "知道了")
        return dialog
    }

    fun getImportAgain(_context: Context): MaterialDialog{
        val dialog = MaterialDialog(_context)
                .title(text ="请谨慎使用此功能")
                .message(text = "即将进入重新导入界面，需要将清除当前课表")
                .positiveButton(text = "清空当前课表")
                .negativeButton(text = "取消")
        return dialog
    }

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

    fun getSelectClassTime(_context: Context): MaterialDialog{
        val dialog = MaterialDialog(_context).customView(R.layout.dialog_select_class_time)
            .title(text = "输入周数")
        dialog.apply {
            noAutoDismiss()
            cancelable(false)
            cancelOnTouchOutside(true)
        }
        return dialog
    }

    fun getSelectWeekAndSection(_context: Context) {
        val allWeek = listOf("周一","周二","周三","周四","周五","周六","周日",)
        val startSection = mutableListOf<String>()
        val endSection = mutableListOf<String>()
        for (i in 1..11){
            startSection.add("第 $i 节")
            endSection.add("第 $i 节")
        }
        val dialog = OptionsPickerBuilder(_context, OnOptionsSelectListener{ week, startSection_, endSection_, _ ->
            if (_context is AddCourseActivity){
                _context.setClassWeek(week, startSection_, endSection_)
            }
        })
                .setTitleText("选择时间")
                .setCyclic(true, false, false)
                .isDialog(true)
                .setSubmitColor(R.color.purple_500)
                .setCancelColor(R.color.purple_500)
                .setTitleBgColor(0xFF666666.toInt())
                .build<Any>()
        dialog.setNPicker(allWeek, startSection.toList(), endSection.toList())
        dialog.show()
    }

    fun getSelectDepartmentAndClass(_context: Context){
        

    }

    fun String.whichIs1(): List<Int>{
        val oneList = mutableListOf<Int>()
        for (i in this.indices){
            when(this[i].toString()){
                "1" -> oneList.add(i + 1)
            }
        }
        return oneList
    }

    fun getWeekNumFormat(oneList: List<Int>): String {
        var courseTimeFormat = "第"
        var type = 0
        if (oneList.last()-oneList.first() == oneList.size-1){
            type = 0
        } else {
            for (i in oneList){
                if (i % 2 == 1){
                    type = 1
                } else if (type == 1){
                    type = 2
                }
            }
        }

        when (type){
            0 -> {
                courseTimeFormat += " ${oneList.first()} - ${oneList.last()} 周"
            }
            1 -> {
                courseTimeFormat += if (oneList.first() % 2 == 1) {
                    "${oneList.first()} - ${oneList.last()} 单周"
                } else {
                    "${oneList.first()} - ${oneList.last()} 双周"
                }
            }
            2 -> {
                for (i in oneList) {
                    courseTimeFormat += "$i,"
                }
                courseTimeFormat += "周"
            }
        }
        return courseTimeFormat
    }

}