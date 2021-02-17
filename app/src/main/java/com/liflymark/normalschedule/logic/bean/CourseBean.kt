package com.liflymark.normalschedule.logic.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CourseBean(
    var campusName: String,
    var classDay: Int,
    var classSessions: Int,
    var classWeek: String,
    var continuingSession: Int,
    var courseName: String,
    var teacher: String,
    var teachingBuildName: String
) {
    @PrimaryKey(autoGenerate = false)
    var id = courseName+teacher+classWeek+classDay.toString()
}