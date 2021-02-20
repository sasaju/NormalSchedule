package com.liflymark.normalschedule.ui.show_timetable

import com.liflymark.icytimetable.IcyTimeTableManager
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean

data class MyCourse(
    val courseName: String,
    override val start: Int,
    override val end: Int,
    override val whichColumn: Int
) : IcyTimeTableManager.BaseCourse()

//val data = listOf(
//    MyCourse("语文",0,2,0),
//    MyCourse("数学",3,4,0),
//    MyCourse("英语",1,2,1),
//    MyCourse("物理",3,5,1),
//    MyCourse("科学",4,5,2),
//    MyCourse("生物",4,5,3),
//    MyCourse("语文",2,4,4),
//    MyCourse("数学",5,6,4),
//    MyCourse("高数",2,5,5),
//    MyCourse("科学",8,9,2),
//    MyCourse("生物",9,10,3),
//    MyCourse("语文",7,11,4),
//)
val data = listOf(
    MyCourse(courseName="毛泽东思想与中国特色社会主义理论概论\n第九教学楼402\n刘卫萍* 耿金龙" , start=9, end=12, whichColumn=2),
    MyCourse(courseName="毛泽东思想与中国特色社会主义理论概论\n第八教学楼304\n刘卫萍* 耿金龙 ", start=9, end=12, whichColumn=4))