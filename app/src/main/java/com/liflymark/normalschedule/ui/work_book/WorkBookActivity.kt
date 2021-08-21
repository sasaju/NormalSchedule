package com.liflymark.normalschedule.ui.work_book

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.liflymark.normalschedule.ui.login_space_room.ShowSpaceViewModel
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme

class WorkBookActivity : ComponentActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(WorkBookViewModel::class.java) }
    @ExperimentalMaterialApi
    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val courseName = intent.getStringExtra("courseName")?.replace("?", "")?.replace("/", "")
        setContent {
            val nav = rememberNavController()
            if (courseName == null) {
                WorkBookNavGraph(
                    navController = nav,
                    startDestination = "allWork"
                )
            } else {
                Log.d("WOrkbookAc", courseName)
                ACourseWorkList(courseName = courseName)
            }
        }
    }
}


//@ExperimentalMaterialApi
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview2() {
//    NorScTheme {
//        SingleBookButton()
//    }
//}