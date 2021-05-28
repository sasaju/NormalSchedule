package com.liflymark.normalschedule.logic.bean

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class CourseBean(
    var campusName: String,
    var classDay: Int,
    var classSessions: Int,
    var classWeek: String,
    var continuingSession: Int,
    var courseName: String,
    var teacher: String,
    var teachingBuildName: String,
    var color: String
) {
    @PrimaryKey(autoGenerate = false)
    var id = courseName+teacher+classWeek+classDay.toString()
}

fun getInitial(): List<CourseBean> {
    return listOf(CourseBean(
        campusName="五四路校区",
        classDay=3,
        classSessions=9, classWeek="111111111111110000000000", continuingSession=3,
        courseName="毛泽东思想与中国特色社会主义理论概论",
        teacher="刘卫萍* 耿金龙 ",
        teachingBuildName="第九教学楼402",
        color="#f0c239"))
}