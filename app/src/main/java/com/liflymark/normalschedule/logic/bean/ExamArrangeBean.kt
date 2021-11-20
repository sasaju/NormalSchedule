package com.liflymark.normalschedule.logic.bean

import androidx.annotation.Keep
import androidx.room.Entity
import com.liflymark.normalschedule.logic.model.ScoreResponse


@Keep
data class ExamArrangeBean(
    val arrange_list: List<Arrange>,
    val result: String,
    val cacheDate:String
)

@Keep
data class Arrange(
    val examBuilding: String,
    val examIdCard: String,
    val examName: String,
    val examSeat: String,
    val examTime: String,
    val examWeekName: String
)
