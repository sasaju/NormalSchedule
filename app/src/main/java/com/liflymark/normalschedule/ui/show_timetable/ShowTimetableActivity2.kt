package com.liflymark.normalschedule.ui.show_timetable

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GetApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewmodel.compose.viewModel
import com.afollestad.materialdialogs.MaterialDialog
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.bean.getData
import com.liflymark.normalschedule.ui.add_course.AddCourseActivity
import com.liflymark.test.ui.theme.TestTheme
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShowTimetableActivity2 : ComponentActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(ShowTimetableViewModel::class.java) }
    @ExperimentalPagerApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestTheme {
                // A surface container using the 'background' color from the theme
                Drawer(viewModel)
            }
        }
    }

}

@ExperimentalPagerApi
@Composable
fun Drawer(viewModel: ShowTimetableViewModel){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column {
                Text("Text in Drawer")
                Button(onClick = {
                    scope.launch {
                        drawerState.close()
                    }
                }) {
                    Text("Close Drawer")
                }
            }
        },
        content = {
            Column {
                var userNowWeek by remember { mutableStateOf(1) }
                val pagerState = rememberPagerState(
                    pageCount = 18,
                    initialOffscreenLimit = 2,
                    initialPage = 12
                )
                ScheduleToolBar(scope, drawerState, userNowWeek, pagerState)

//                val viewModel = viewModel(modelClass = ShowTimetableViewModel::class.java)
                val courseList: State<List<List<OneByOneCourseBean>>?> =
                    viewModel.courseDatabaseLiveDataVal.observeAsState(getNeededClassList(getData()))
//                val courseList = getNeededClassList(getData()) as State<List<List<OneByOneCourseBean>>?>
                Log.d("TestList", courseList.value.toString())
                viewModel.loadAllCourse()
                HorizontalPager(state = pagerState) { page ->
//                    val data = State(getNeededClassList(Convert.courseBeanToOneByOne(courseList.value!!)[page]))
                    SingleLineClass(oneWeekClass = courseList, page = page)
                    Log.d("Test",page.toString())
                }


                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collectLatest { page ->
                        userNowWeek = page
                    }
                }
            }
        }
    )
}


@ExperimentalPagerApi
@Composable
fun ScheduleToolBar(scope: CoroutineScope, drawerState: DrawerState, userNowWeek: Int, pagerState: PagerState){
    val context = LocalContext.current
    val activity = (LocalContext.current as? Activity)
    TopAppBar(
        title = {
            Column() {
                Text(text = "第${userNowWeek+1}周", Modifier.clickable {
                    scope.launch {
                        pagerState.animateScrollToPage(10)
                    }
                })
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.open()
                }
            }) {
                Icon(Icons.Filled.Menu, null)
            }
        },
        actions = {
            IconButton(onClick = {
                val intent = Intent(context, AddCourseActivity::class.java).apply {
                    putExtra("isSaved", true)
                }
                activity?.startActivity(intent)
            }) {
                Icon(Icons.Filled.Add, null)
            }
            IconButton(onClick = {

            }) {
                Icon(Icons.Filled.GetApp, null)
            }
        }
    )
}



@Composable
fun SingleLineClass(oneWeekClass: State<List<List<OneByOneCourseBean>>?>, page:Int){
    Column() {
        //星期行
        Row() {
            Text(text = "4\n月", Modifier.weight(0.6F), fontSize = 10.sp, textAlign = TextAlign.Center)
            repeat(7){
                Text(text = "一\n\n17",
                    Modifier
                        .weight(1F, true)
                        .height(40.dp),fontSize = 11.sp, lineHeight = 10.sp, textAlign = TextAlign.Center)
            }
        }

        Row(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Column(Modifier.weight(0.6F, true)) {
                // 时间列
                repeat(11) {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = ParagraphStyle(lineHeight = 6.sp)){
                                withStyle(style = SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)){
                                    append("\n\n\n1")
                                }
                            }
                            withStyle(style = SpanStyle(fontSize = 10.sp)){
                                append("08:00\n")
                                append("08:45")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp),
                        textAlign = TextAlign.Center,

                        )
                }
            }

            // 课程
            val realOneWeekList = getNeededClassList(oneWeekClass.value!!.getOrElse(page){ getData()})
            for (oneDayClass in realOneWeekList){
                Log.d("TestOneweek",oneDayClass.toString())
                val nowJieShu = IntArray(12){it+1}.toMutableList()
                Column(Modifier.weight(1F,true)) {
                    for (oneClass in oneDayClass){
                        val spacerHeight = (oneClass.start - nowJieShu[0]) * 70
                        if (spacerHeight < 0){
                            Log.d("TestActivity", "当前有冲突课程")
                        }
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(spacerHeight.dp))
                        SingleClass2(singleClass = oneClass)
                        nowJieShu -= IntArray(oneClass.end){it+1}.toMutableList()
                    }
                }
            }
        }
    }

}


@Composable
fun SingleClass2(singleClass: OneByOneCourseBean){
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val height = 70*(singleClass.end- singleClass.start+1)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp)
            .padding(2.dp) // 外边距
            .alpha(0.8F)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                showDialog(context)
            },
        elevation = 1.dp, // 设置阴影
    ) {
        val nameList = singleClass.courseName.split("\n")
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.W600, color = Color.White, fontSize = 13.sp)
                ) {
                    append(nameList[0]+"\n"+nameList[1]+"\n\n")
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.W600, color = Color(0xFF4552B8), fontSize = 10.sp)
                ) {
                    append(nameList[2])
                }
            },
            modifier = Modifier.background(Color.LightGray),
            textAlign = TextAlign.Center
        )
    }
}

fun showDialog(context: Context) {
    MaterialDialog(context).show {
        title(text = "张子龙铁憨憨")
        message(text = "张子龙大笨蛋")
        positiveButton(text = "是的")
    }
}

@ExperimentalPagerApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestTheme {

    }
}