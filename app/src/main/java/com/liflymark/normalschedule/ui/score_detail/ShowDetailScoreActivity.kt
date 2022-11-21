package com.liflymark.normalschedule.ui.score_detail

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.model.Grades
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import es.dmoral.toasty.Toasty

class ShowDetailScoreActivity : ComponentActivity() {
    @OptIn(
        ExperimentalAnimationApi::class,
        ExperimentalMaterialApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val allGradeListString = intent.getStringExtra("detail_list")?:""
        setContent {
            UiControl()
            NorScTheme {
                Scaffold(
                    topBar = { NormalTopBar(label = "成绩明细") },
                    content = {
                        it
                        AllGrades(allGradeListString = allGradeListString)
                    }
                )
            }
        }
    }
}


@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun AllGrades(allGradeListString: String){
    val allGradeList by rememberSaveable {
        mutableStateOf(Convert.jsonToGradesList(allGradeListString))
    }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "结果仅供参考，一切请以教务系统数据为准！！！")
        OutlinedButton(onClick = {
            Repository.setScoreDetail(allGradeListString)
            Toasty.success(context, "缓存完成").show()
        }) {
            Text(text = "缓存至本地")
        }
        if (allGradeList.isEmpty()){
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "当前教务系统“本学期成绩”为空")
        }
        for (i in allGradeList){
            SingleGrade(grades = i)
            Spacer(modifier = Modifier.height(20.dp))
        }

    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun SingleGrade(grades: Grades) {
    var expand by remember { mutableStateOf(false) }
    var expandIcon by remember { mutableStateOf(Icons.Filled.ExpandLess) }
    Card(onClick = {
        expand = !expand
        expandIcon = if (expand){ Icons.Filled.ExpandMore } else { Icons.Filled.ExpandLess}},
        modifier = Modifier.padding(2.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${grades.courseName}： ${grades.courseScore}",
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(0.8F)
                )
                Icon(imageVector = expandIcon, contentDescription = null, modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth())
            }
            AnimatedVisibility(visible = expand) {

                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "平时成绩:${grades.usualcj}",modifier = Modifier.weight(1F))
                        Text(text = "期末成绩:${grades.lastcj}",modifier = Modifier.weight(1F))
                        Text(text = "总成绩:${grades.allcj}",modifier = Modifier.weight(1F))
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "排名:${grades.rank}", modifier = Modifier.weight(1F) )
                        Text(text = "最高成绩:${grades.maxcj}", modifier = Modifier.weight(1F))
                        Text(text = "最低成绩:${grades.mincj}", modifier = Modifier.weight(1F))
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "课程学分:${grades.credit}", modifier = Modifier.weight(1F))
                        Text(text = "课程类型:${grades.coursePropertyName}", modifier = Modifier.weight(1F))
                    }
                }
            }
        }
    }
}