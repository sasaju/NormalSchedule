package com.liflymark.normalschedule.ui.abase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConorButton(
    enable:Boolean = true,
    onClick:() -> Unit,
    text:String,
){
    Button(
        enabled = enable,
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            text.split("").forEach {
                Text(text = it, style = MaterialTheme.typography.h6, maxLines = 1)
            }
        }
    }
}

@Composable
fun ConorOutlineButton(
    enable:Boolean = true,
    onClick:() -> Unit,
    text:String,
){
    OutlinedButton(
        enabled = enable,
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            text.split("").forEach {
                Text(text = it, style = MaterialTheme.typography.h6, maxLines = 1)
            }
        }
    }
}
