package com.liflymark.normalschedule.logic.model


import androidx.annotation.Keep

@Keep
data class GetNewCourseResponse(
    val content: List<Content>,
    val statusCode: Int
) {
    @Keep
    data class Content(
        val fields: Fields,
        val model: String,
        val pk: Int
    ) {
        @Keep
        data class Fields(
            val bean_list_str: String,
            val class_name: String,
            val class_number: String,
            val error_count: Int,
            val force_stable: Boolean,
            val get_count: Int,
            val update_time: String,
            val update_user: String,
            val visible: Boolean
        )
    }
}