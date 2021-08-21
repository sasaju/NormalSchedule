package com.liflymark.normalschedule.ui.class_course

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.liflymark.normalschedule.logic.model.DepartmentList
import com.liflymark.normalschedule.ui.score_detail.ProgressDialog

@Composable
fun WaitingDepartList(departState:State<DepartmentList>){
    val openDialog = remember { mutableStateOf(false) }
    openDialog.value = departState.value.status != "yes"
    ProgressDialog(openDialog = openDialog,departState.value.status)
}