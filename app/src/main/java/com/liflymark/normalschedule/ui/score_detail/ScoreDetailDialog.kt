package com.liflymark.normalschedule.ui.score_detail

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

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

@Composable
fun ProgressDialog(openDialog: MutableState<Boolean>, label:String){

    if (openDialog.value) {
        Dialog(
            onDismissRequest = { openDialog.value = false },
            DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Box(
                contentAlignment= Alignment.Center,
                modifier = Modifier
                    .wrapContentWidth()
                    .height(100.dp)
                    .background(White, shape = RoundedCornerShape(8.dp))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(10.dp))
                    CircularProgressIndicator()
                    if (label != "") {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = label, modifier = Modifier.wrapContentWidth())
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DialogPreview(){

}
