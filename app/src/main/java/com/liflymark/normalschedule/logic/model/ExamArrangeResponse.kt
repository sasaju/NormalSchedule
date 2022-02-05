package com.liflymark.normalschedule.logic.model

import androidx.annotation.Keep

@Keep
data class ExamArrangeResponse(
    val arrange_list: List<Arrange>,
    var result: String
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