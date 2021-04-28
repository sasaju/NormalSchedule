package com.liflymark.normalschedule.ui.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class ComposeAboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AboutPage()
        }
    }


    @Composable
    fun AboutPage(){
        Text("Hello World")
    }

    @Preview
    @Composable
    fun PreViewAbout(){
        AboutPage()
    }
}