package com.liflymark.normalschedule.logic.model

import androidx.annotation.Keep

@Keep
data class SpaceResponse(
    val roomList: List<Room>,
    val roomName: String
)

@Keep
data class Room(
    val classroomName: String,
    val placeNum: String,
    val spaceNow: String,
    val type: String
)