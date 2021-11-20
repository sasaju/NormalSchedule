package com.liflymark.normalschedule.logic.bean

import androidx.annotation.Keep
import androidx.room.Entity
import com.liflymark.normalschedule.logic.model.Grade

@Keep
@Entity
data class ScoreBean(
    val grade_list: List<Grade>,
    val result: String,
    val data: String
)
