package com.liflymark.normalschedule.logic.model

import androidx.annotation.Keep
import androidx.room.Dao
import androidx.room.PrimaryKey

// 用于接收api返回的课程信息

@Keep
data class CourseResponse(
    val allCourse: List<AllCourse>,
    val status: String
)


@Keep
data class AllCourse(
        val campusName: String,
        val classDay: Int,
        val classSessions: Int,
        val classWeek: String,
        val continuingSession: Int,
        val courseName: String,
        val teacher: String,
        val teachingBuildName: String,
        val coureNumber:String = ""
)
