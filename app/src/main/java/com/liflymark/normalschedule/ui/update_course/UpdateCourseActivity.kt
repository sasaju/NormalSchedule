package com.liflymark.normalschedule.ui.update_course

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.ui.theme.NorScTheme

class UpdateCourseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NorScTheme {
                val userType = Repository.getUserType().collectAsState(initial = null)
                userType.value?.let {
                    when(it.statusCode){
                        300 -> {

                        }
                        200 -> {

                        }
                        301 -> {

                        }
                        302 -> {

                        }
                    }
                }
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun NormalUser(userNumber:String) {
    if (userNumber.length < 8){

    }else{

    }
}

@Composable
fun ExampleUser(){
    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 20.sp
                    )
                ){
                    append("您当前的用户类型：")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.primary,
                        fontSize = 20.sp
                    )
                ){
                    append("游客")
                }
            }
        )
    }
}

@Composable
fun CanUseCourse(){

}

@Composable
fun OnlyError(content:String){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = content,
            color = Color.Gray,
            style = MaterialTheme.typography.h5
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview3() {
    NorScTheme {
        OnlyError("您当前未登陆")
    }
}