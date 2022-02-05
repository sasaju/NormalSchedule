package com.liflymark.normalschedule.ui.sign_in_compose

import android.app.Activity
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview



@Composable
fun NormalTopBar(label: String){
    val activity = LocalContext.current as Activity
    TopAppBar(
        title = {
            Text(label)
        },
        navigationIcon = {
            IconButton(onClick = {
                activity.finish()
            }) {
                Icon(Icons.Filled.ArrowBack, null)
            }
        },
        backgroundColor = Color(0xFF2196F3)
    )
}

@Composable
fun NormalTopBar(label: String,nav: () -> Unit){
    TopAppBar(
        title = {
            Text(label)
        },
        navigationIcon = {
            IconButton(onClick = {
                nav()
            }) {
                Icon(Icons.Filled.Close, null)
            }
        },
        backgroundColor = Color(0xFF2196F3)
    )
}




@Preview(showBackground = true)
@Composable
fun MyPreView() {

}