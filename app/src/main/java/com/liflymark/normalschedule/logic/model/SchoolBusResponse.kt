package com.liflymark.normalschedule.logic.model

import androidx.annotation.Keep

@Keep
data class SchoolBusResponse(
    val nowDay: String,
    val timeList: TimeList
)
@Keep
data class TimeList(
    val fiveToSeven: List<FiveToSeven>,
    val sevenToFive: List<SevenToFive>
)

@Keep
data class FiveToSeven(
    val runHowMany: String,
    val runNumber: String,
    val runTime: String
)

@Keep
data class SevenToFive(
    val runHowMany: String,
    val runNumber: String,
    val runTime: String
)