package com.liflymark.normalschedule.logic.model

import androidx.annotation.Keep

@Keep
data class DevBoardResponse(
    val bulletin_list: List<Bulletin>,
    val status: String
)

@Keep
data class Bulletin(
    val author: String,
    val content: String,
    val date: String,
    val title: String
)