package com.liflymark.normalschedule.logic.model

data class ScoreDetail(
    val grade_list: List<Grades>,
    val result: String
)

data class Grades(
    val allcj: Any,
    val courseName: String,
    val coursePropertyName: String,
    val courseScore: String,
    val credit: Int,
    val lastcj: Any,
    val maxcj: String,
    val mincj: String,
    val rank: String,
    val usualcj: Any
)