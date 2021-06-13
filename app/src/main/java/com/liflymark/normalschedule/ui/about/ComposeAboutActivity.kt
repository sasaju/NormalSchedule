//package com.liflymark.normalschedule.ui.about
//
//import android.graphics.ImageDecoder
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Column
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import com.liflymark.normalschedule.R
//
//class ComposeAboutActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            AboutPage()
//        }
//    }
//
//
//    @Composable
//    fun AboutPage(){
//        Column {
//            Image(painter = painterResource(R.mipmap.ic_launcher),
//                contentDescription = null)
//        }
//    }
//
//    @Preview
//    @Composable
//    fun PreViewAbout(){
//        AboutPage()
//    }
//}