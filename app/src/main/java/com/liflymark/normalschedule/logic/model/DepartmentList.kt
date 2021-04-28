package com.liflymark.normalschedule.logic.model

import androidx.annotation.Keep

@Keep
data class DepartmentList(
    val status: String,
    val structure: List<Structure>
)

@Keep
data class Structure(
        val department: String,
        val majorList: List<String>
)