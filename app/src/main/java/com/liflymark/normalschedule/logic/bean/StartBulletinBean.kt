package com.liflymark.normalschedule.logic.bean

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
data class StartBulletinBean(
    val bulletin_list: List<Bulletin2>,
    val status: String
)


@Keep
@Entity
data class Bulletin2(
    val author: String,
    val content: String,
    val date: String,
    val force_update: Boolean,
    @PrimaryKey val id: Int,
    val show_group: Boolean,
    val title: String,
    val update_url: String,
)

