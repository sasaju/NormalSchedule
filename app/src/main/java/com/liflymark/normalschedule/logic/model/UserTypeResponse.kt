package com.liflymark.normalschedule.logic.model


import androidx.annotation.Keep

/**
 * statusCode 200:正常  300：异常
 */
@Keep
data class UserTypeResponse(
    val content: String,
    val statusCode: Int
)



@Keep
data class GotResponse(
    val statusCode: Int
)

@Keep
data class UploadResponse(
    val statusCode: Int,
    val status: String
)


