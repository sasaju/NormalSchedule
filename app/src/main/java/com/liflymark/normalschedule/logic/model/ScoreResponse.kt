package com.liflymark.normalschedule.logic.model

import androidx.annotation.Keep

@Keep
data class ScoreResponse(
    val grade_list: List<Grade>,
    val result: String
)

@Keep
data class Grade(
        val project: String,
        val thisProjectGradeList: List<List<ThisProjectGrade>>
)

@Keep
data class ThisProjectGrade(
        val attributeName: String,
        val courseName: String,
        val score: Double,
        val time: String
)