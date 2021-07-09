package com.liflymark.normalschedule.logic.model

import androidx.annotation.Keep

@Keep
data class ScoreDetail(
    val grade_list: List<Grades>,
    val result: String
)

@Keep
data class Grades(
    val allcj: String,
    val courseName: String,
    val coursePropertyName: String,
    val courseScore: String,
    val credit: String,
    val lastcj: String,
    val maxcj: String,
    val mincj: String,
    val rank: String,
    val usualcj: String
)