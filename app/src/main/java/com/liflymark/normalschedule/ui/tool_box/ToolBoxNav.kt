package com.liflymark.normalschedule.ui.tool_box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import kotlinx.coroutines.launch

@Composable
fun ToolBoxNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "functionList",
){
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("functionList"){
            FunctionList(navController = navController)
        }
        composable("devBoard"){
            DevBoard(navController = navController)
        }
        composable("busTime"){
            SchoolBusAll(navController = navController)
        }
        composable("schoolCalendar"){
            HbuCalendar(navController = navController)
        }
        composable("wait"){
            WaitTime(navController = navController)
        }
    }
}

@Composable
fun FunctionList(navController: NavController){
    NorScTheme {
        val scope = rememberCoroutineScope()
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            NormalTopBar(label = "工具箱")
            SinglePart(
                titleIcon = Icons.Default.Message,
                titleName = "开发者公告"
            ) {
                SingleButton(Icons.Filled.StickyNote2, "公告栏"){
                    navController.navigate("devBoard")
                }
                SingleButton(Icons.Filled.StickyNote2, "使用技巧"){
                    scope.launch {
//                        Repository.saveUserVersion(0)
                        navController.navigate("wait")
                    }
                }
            }

            SinglePart(
                titleIcon = Icons.Default.EmojiSymbols,
                titleName = "校内生活"
            ) {
                SingleButton(Icons.Filled.CalendarToday, "校历") {
                    navController.navigate("schoolCalendar")
                }
                SingleButton(Icons.Filled.DirectionsBus, "校车时刻表") {
                    navController.navigate("busTime")
                }
                SingleButton(Icons.Filled.LunchDining, "作息时刻表") {
                    navController.navigate("wait")
                }
            }

            SinglePart(
                titleIcon = Icons.Default.Fastfood,
                titleName = "吃喝玩乐"
            ) {
                SingleButton(Icons.Filled.ShoppingBag, "购物指南") {
                    navController.navigate("wait")
                }
                SingleButton(Icons.Filled.FoodBank, "美食指南") {
                    navController.navigate("wait")
                }
            }
        }
    }
}

