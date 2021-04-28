package com.liflymark.normalschedule.logic.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class TimeTableBean(
        @PrimaryKey(autoGenerate = true)
        var id: Int,
        var name: String,
        var sameLen: Boolean = true,
        var courseLen: Int = 50
)