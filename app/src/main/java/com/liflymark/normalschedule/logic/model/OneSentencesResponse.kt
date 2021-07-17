package com.liflymark.normalschedule.logic.model

import androidx.annotation.Keep

@Keep
data class OneSentencesResponse(
    val result: List<Sentence>,
    val status: String
)

@Keep
data class Sentence(
    val author: String,
    val sentence: String
)