package com.liflymark.normalschedule.logic.model

data class DepartmentList(
    val status: String,
    val structure: List<Structure>
)

data class Structure(
        val department: String,
        val majorList: List<String>
)