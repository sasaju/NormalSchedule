package com.liflymark.normalschedule.ui.tool_box

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.liflymark.normalschedule.ui.class_course.Item
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme

/**
 * 校车总界面
 */
@Composable
fun SchoolBusAll(navController: NavController){
    NorScTheme {
        Scaffold(
            topBar = {
                NormalTopBar(label = "校车时刻表") {
                    navController.navigateUp()
                }
            },
            content = {
                SchoolBusCard()
            }
        )
    }
}

/**
 * 校车Content界面
 */
@Composable
fun SchoolBusCard(tbViewModel: ToolBoxViewModel = viewModel()){
    val schoolBusResponse =
        tbViewModel.busTimeLiveData.observeAsState(initial = tbViewModel.initialSchoolBus)
    Column(modifier = Modifier.fillMaxWidth()
    ) {
        FullCard(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp)) {
            Log.d("BusTime", schoolBusResponse.value.nowDay)
            ShowHowToUse(tbViewModel.getTypeToString(schoolBusResponse.value.nowDay))
        }
        RoundedHeader(title = "五四路<-->七一路")
        Column(
            modifier= Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("五四路  >>>  七一路", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(3.dp))
            SingleRowTime(firstCo = "班次", secondCo = "发车时间", thirdCo = "发车数量", isFirst = true)
//            SingleRowTime(firstCo = "1", secondCo = "7：30", thirdCo = "1")
//            SingleRowTime(firstCo = "1", secondCo = "7：30", thirdCo = "1")
//            SingleRowTime(firstCo = "1", secondCo = "7：30", thirdCo = "1")
//            SingleRowTime(firstCo = "1", secondCo = "7：30", thirdCo = "1")
            for (singleFive in schoolBusResponse.value.timeList.fiveToSeven){
                key(singleFive.runTime+singleFive.runNumber) {
                    SingleRowTime(
                        firstCo = singleFive.runNumber,
                        secondCo = singleFive.runTime,
                        thirdCo = singleFive.runHowMany
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("七一路  >>>  五四路", style = MaterialTheme.typography.body1)
            SingleRowTime(firstCo = "班次", secondCo = "发车时间", thirdCo = "发车数量", isFirst = true)
            for (singleFive in schoolBusResponse.value.timeList.sevenToFive){
                key(singleFive.runTime+singleFive.runNumber) {
                    SingleRowTime(
                        firstCo = singleFive.runNumber,
                        secondCo = singleFive.runTime,
                        thirdCo = singleFive.runHowMany
                    )
                }

            }
        }
    }
}

/**
 * 单行校车时刻
 */
@Composable
fun SingleRowTime(firstCo:String, secondCo:String, thirdCo:String,isFirst:Boolean = false){
    Row(Modifier.padding(2.dp)){
        if (isFirst){
            Spacer(modifier = Modifier.size(23.dp))
        }else{
            Icon(Icons.Default.DirectionsBus, null)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = firstCo, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(text = secondCo, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(text = thirdCo, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
    }
}


/**
 * 说明如何使用校车时刻表
 */
@Composable
fun ShowHowToUse(type:String, tbViewModel: ToolBoxViewModel = viewModel()){
    var userType by remember { mutableStateOf(type) }
    var expand by remember { mutableStateOf(false) }
    val typeMap = mapOf("节假日或寒暑假" to "holiday", "工作日" to "workday", "双休日" to "weekday")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
    ) {
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle()){ append("系统判断当前处于：") }
                withStyle(SpanStyle(color = Color.Green)){
                    append("$type\n") }
                withStyle(SpanStyle()){ append("如有错误，请点击下方文字修改") }
            }
        )

        Box {
            TextButton(onClick = {
                expand = true
            }) {
                Text(text = "当前显示日期类型:${userType}", color = Color.White)
            }

            DropdownMenu(
                expanded = expand,
                onDismissRequest = { expand = false },
                modifier = Modifier.height(200.dp)
            ){
                for (i in typeMap.keys){
                    Item(itemName = i) {
                        userType = it
                        expand =false
                        typeMap[it]?.let { it1 -> tbViewModel.getBusTime(it1) }
                    }
                }
            }
        }
    }
}