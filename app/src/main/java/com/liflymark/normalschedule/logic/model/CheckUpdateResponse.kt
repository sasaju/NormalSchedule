package com.liflymark.normalschedule.logic.model

import androidx.annotation.Keep

@Keep
data class CheckUpdateResponse(
    val force: Boolean?,
    val newUrl: String?,
    val result: String,
    val status: String
)