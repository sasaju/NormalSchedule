package com.liflymark.normalschedule.ui.score_detail

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun WaitDialog(openDialog: MutableState<Boolean>){
    if(openDialog.value){
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = "正在加载")
            },
            text = {
                Text(text = "请等待......")
            },
            buttons = {

            }
        )
    }
}
