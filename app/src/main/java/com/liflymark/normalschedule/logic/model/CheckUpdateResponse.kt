package com.liflymark.normalschedule.logic.model

data class CheckUpdateResponse(
    val force: Boolean?,
    val newUrl: String?,
    val result: String,
    val status: String
)