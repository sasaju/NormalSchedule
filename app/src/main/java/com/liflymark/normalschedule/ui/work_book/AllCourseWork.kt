package com.liflymark.normalschedule.ui.work_book

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowColumn
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme

@ExperimentalMaterialApi
@Composable
fun AllCourseBook(navController: NavController){
    NorScTheme {
        UiControl()
        Scaffold(
            topBar = {
                NormalTopBar(label = "作业本")
            },
            content = {
                ContentAllBook(navController)
            }
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun ContentAllBook(
    navController: NavController,
    wbViewModel: WorkBookViewModel = viewModel()
){
    val courseNameList = wbViewModel.courseNameListFLow
        .collectAsState(initial = wbViewModel.initCourseName)
    FlowRow(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 10.dp),
        mainAxisAlignment = FlowMainAxisAlignment.Center,
        mainAxisSpacing = 20.dp,
        crossAxisSpacing = 20.dp
    ) {
        for (courseName in courseNameList.value){
            key(courseName){
                SingleBookButton(courseName = courseName){
                    navController.navigate("singleCourse/$courseName")
                }
            }
        }
        if (courseNameList.value.isEmpty()){
            Text(
                text = "你还没有添加作业\n" +
                        "点击课表主界面的课程格子\n" +
                        "->点击弹窗右下角按钮\n" +
                        "->添加作业",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}


@ExperimentalMaterialApi
@Composable
fun SingleBookButton(
    courseName:String,
    onClick:(courseName:String)->Unit
){
    Card(
        backgroundColor = Color.Transparent,
        contentColor = Color.White,
        modifier = Modifier
            .width(148.51.dp)
            .height(210.dp),
        onClick = {
            onClick(courseName)
        }
    ) {
        BrushBackground()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(5.dp)
        ) {
            Text(
                text = courseName,
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Book,
                    null,
                )
                Text(text = "作业本")
            }

        }
    }
}

@Composable
fun BrushBackground(){
    val backgroundColor =
        listOf(
            listOf(Color(0xFF5EFCE8), Color(0xFF736EFE)),
            listOf(Color(0xFFC2FFD8), Color(0xFF465EFB)),
            listOf(Color(0xFF2AFADF), Color(0xFF4C83FF))
        )
    Spacer(
        modifier = Modifier
            .background(
                brush = Brush.linearGradient(
                    colors = backgroundColor[2]
                )
            )
            .fillMaxSize()
    )
}

//@ExperimentalMaterialApi
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview2() {
//    NorScTheme {
//        ContentAllBook()
//    }
//}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun DefaultPreviewNight() {
    NorScTheme(darkTheme = true) {
        SingleBookButton("药用高分子材料学"){}
    }
}