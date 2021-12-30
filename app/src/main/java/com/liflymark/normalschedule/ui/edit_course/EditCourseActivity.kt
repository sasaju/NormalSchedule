package com.liflymark.normalschedule.ui.edit_course

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.liflymark.normalschedule.ui.add_course.ShowAllCourseToEdit
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme

class EditCourseActivity : ComponentActivity() {

    @OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val courseName = intent.getStringExtra("courseName") ?: ""
        setContent {
            NorScTheme {
                UiControl()
                Scaffold(
                    topBar = {
                        NormalTopBar(label = "添加课程")
                    },
                    content = {
                        ShowAllCourseToEdit(courseName = courseName)
                    }
                )
            }
        }
    }
}
