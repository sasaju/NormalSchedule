package com.liflymark.normalschedule.ui.work_book

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.liflymark.normalschedule.ui.tool_box.*

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun WorkBookNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "allWork",
){
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("allWork"){
            AllCourseBook(navController = navController)
        }
        composable(
            "singleCourse/{courseName}",
            arguments = listOf(navArgument("courseName") { type = NavType.StringType })
        ){ backStackEntry->
            Log.d("WorkNav", "null")
            val courseName = backStackEntry.arguments?.getString("courseName", "0")
            if (courseName != null) {
                Log.d("WorkNav", "null")
                ACourseWorkList(courseName = courseName, navController = navController)
            }
        }
//        composable(
//            "singleWorkList/{homeWorkId}",
//            arguments = listOf(navArgument("homeWorkId"){type = NavType.IntType})
//        ){
//            val homeworkId = it.arguments?.getInt("homeWorkId", 0)
//        }
    }
}