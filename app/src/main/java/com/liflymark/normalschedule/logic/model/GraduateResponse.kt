package com.liflymark.normalschedule.logic.model

import androidx.annotation.Keep

@Keep
data class GraduateResponse(
    val allCourse: List<AllCourse>,
    val result: String,
    val status: String
)