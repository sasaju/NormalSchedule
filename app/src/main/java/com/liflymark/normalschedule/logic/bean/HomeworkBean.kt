package com.liflymark.normalschedule.logic.bean

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class HomeworkBean(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    var courseName:String,
    var workContent: String,
    var createDate:Long,
    var deadLine:Long,
    var finished:Boolean
)
