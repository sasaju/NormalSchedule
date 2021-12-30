package com.liflymark.normalschedule.ui.add_course

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import com.google.accompanist.pager.ExperimentalPagerApi
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme

class AddCourseComposeActivity : ComponentActivity() {

    @OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NorScTheme {
                // A surface container using the 'background' color from the theme
                UiControl()
                Scaffold(
                    topBar = {
                        NormalTopBar(label = "添加课程")
                    },
                    content = {
                        ShowAllCourseToEdit(courseName = "")
                    }
                )
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview3() {
//    NormalScheduleTheme {
//        Greeting("Android")
//    }
//}