package com.liflymark.normalschedule.logic.model

data class ScoreResponse(
    val grade_list: List<Grade>,
    val result: String
)

data class Grade(
        val project: String,
        val thisProjectGradeList: List<List<ThisProjectGrade>>
)

data class ThisProjectGrade(
        val attributeName: String,
        val courseName: String,
        val score: Double,
        val time: String
)