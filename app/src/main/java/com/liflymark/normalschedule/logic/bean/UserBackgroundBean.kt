package com.liflymark.normalschedule.logic.bean

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class UserBackgroundBean(
        var userBackground: String = ""
) {
    @PrimaryKey
    var id: Int = 0

}