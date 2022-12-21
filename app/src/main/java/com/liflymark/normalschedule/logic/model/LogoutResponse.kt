package com.liflymark.normalschedule.logic.model

import androidx.annotation.Keep

@Keep
data class LogoutResponse(
    val result:String,
    val msg:String
)
