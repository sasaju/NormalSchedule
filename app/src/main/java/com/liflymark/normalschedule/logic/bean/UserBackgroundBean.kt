package com.liflymark.normalschedule.logic.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserBackgroundBean(
        var userBackground: String = ""
) {
    @PrimaryKey
    var id: Int = 0

}