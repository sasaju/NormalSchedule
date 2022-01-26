package com.liflymark.normalschedule.ui.class_course

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerDefaults
import com.google.accompanist.pager.rememberPagerState
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.bean.getData
import com.liflymark.normalschedule.logic.model.DepartmentList
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.show_timetable.*
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import kotlinx.coroutines.flow.collectLatest

class ClassCourseActivity : ComponentActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(ClassCourseViewModel::class.java) }

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val allowImport = intent.getBooleanExtra("allowImport", false)
        setContent {
            NorScTheme {
                UiControl()
                Column {
                    NormalTopBar(label = "班级课程")
                    SelectMajor(allowImport = allowImport)
                    ShowCourse(courseViewModel = viewModel)
                }
                    
            }
        }
    }
}

@Composable
fun SelectMajor(allowImport:Boolean = false,courseViewModel: ClassCourseViewModel = viewModel()){
    val departmentList = courseViewModel.departmentListFlow.collectAsState(initial =DepartmentList("正在加载", listOf()))
    var department by remember { mutableStateOf("点击选择学院") }
    var majorList by remember { mutableStateOf(listOf("点击选择专业")) }
    var major by remember {
        mutableStateOf("点击选择专业")
    }
    val context = LocalContext.current
    val activity = LocalContext.current as ClassCourseActivity
    LaunchedEffect(true){
        if (allowImport) {
            courseViewModel.classBeanLiveData.observe(activity) {
                val intent = Intent(context, ShowTimetableActivity2::class.java).apply {
                    putExtra("isSaved", false)
                    putExtra("courseList", Convert.allCourseToJson(it.allCourse))
                    putExtra("user", "")
                    putExtra("password", "")
                }
                activity.startActivity(intent)
                activity.finish()
            }
        }
    }
    LaunchedEffect(department) {
        for (i in departmentList.value.structure){
            if (i.department == department) {
                majorList = i.majorList
            } else {
                major = "专业"
            }
        }
    }
    WaitingDepartList(departState = departmentList)
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        val expanded = remember { mutableStateOf(false) }
        val expandedMajorList = remember {
            mutableStateOf(false)
        }
        Box(contentAlignment = Alignment.TopCenter){
            TextButton(onClick = { expanded.value = true }) {
                Text(text = department, fontSize = 15.sp)
            }
            //学院列表
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                modifier = Modifier.height(280.dp)
            ) {
                for (i in departmentList.value.structure){
                    Item(itemName = i.department){
                        department = it
                        expanded.value =false
                    }
                }
            }
        }

        Box(contentAlignment = Alignment.Center){
            TextButton(onClick = { expandedMajorList.value = true }) {
                Text(text = major, fontSize = 15.sp)
            }

            /*专业列表*/
            DropdownMenu(
                expanded = expandedMajorList.value,
                onDismissRequest = { expandedMajorList.value = false },
                modifier = Modifier.height(280.dp)
            ) {
                for (i in majorList){
                    Item(itemName = i){
                        major = it
                        expandedMajorList.value =false
                        courseViewModel.putDepartmentAndMajor(department, major)
                    }
                }
            }
        }
        if (allowImport) {
            TextButton(onClick = {
                courseViewModel.saveAccount()
                courseViewModel.putDepartmentAndMajorBean(department, major)
            }) {
                Text(text = "导入当前课表" ,fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun Item(itemName:String, selectedString: (String) -> Unit){
    DropdownMenuItem(onClick = {
        selectedString(itemName)
    }) {
        Text(itemName)
    }
}

@ExperimentalPagerApi
@Composable
fun ShowCourse(courseViewModel: ClassCourseViewModel) {
    val courseList  = courseViewModel.classCourseListLiveData.observeAsState(getNeededClassList(getData()))
    Column(modifier = Modifier.background(Color.Transparent)) {
        var userNowWeek by remember { mutableStateOf(getNowWeek()) }
        val pagerState = rememberPagerState(
//            pageCount = 19,
//            initialOffscreenLimit = 2,
            initialPage = 0,
//            infiniteLoop = true
        )

        HorizontalPager(
            state = pagerState,
            count = 19
        ) { page ->
            SingleLineClass2(oneWeekClass = courseList, page = page)
        }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collectLatest { page ->
                userNowWeek = page
            }
        }
    }
}

@Composable
fun SingleLineClass2(oneWeekClass: State<List<List<OneByOneCourseBean>>>, page:Int){
    Column() {
        //星期行
        Row() {
            Column(
                Modifier
                    .weight(0.6F)
                    .height(40.dp)) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "${getDayOfDate(0, page)}\n月",modifier = Modifier.fillMaxWidth(), fontSize = 10.sp, textAlign = TextAlign.Center)
            }

            repeat(7){
                Text(text = "${getDayOfWeek(it+1)}\n\n${getDayOfDate(it+1, page)}",
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
                            withStyle(style = ParagraphStyle(lineHeight = 6.sp)) {
                                withStyle(
                                    style = SpanStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("\n\n${it + 1}")
                                }
                            }
                            withStyle(style = SpanStyle(fontSize = 10.sp)) {
                                append("${getStartTime(it + 1)}\n")
                                append(getEndTime(it + 1))
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
            val realOneWeekClass = getNeededClassList(oneWeekClass.value.getOrElse(page){ getData() })

            for (oneDayClass in realOneWeekClass) {
                val nowJieShu = IntArray(12) { it + 1 }.toMutableList()
                Column(Modifier.weight(1F, true)) {
                    for (oneClass in oneDayClass) {
                        val spacerHeight = (oneClass.start - nowJieShu[0]) * 70
                        if (spacerHeight < 0) {
                            Log.d("TestActivity", "当前有冲突课程")
                        }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(spacerHeight.dp)
                        )
                        SingleClass3(singleClass = oneClass)
//                        Log.d("classcourseActiv", oneClass.toString())
                        nowJieShu -= IntArray(oneClass.end) { it + 1 }.toMutableList()
                    }
                }
            }
        }
    }

}

@Composable
fun SingleClass3(singleClass: OneByOneCourseBean){
//    val context = LocalContext.current
//    val activity = (LocalContext.current as? Activity)
//    val interactionSource = remember { MutableInteractionSource() }
    val height = 70*(singleClass.end- singleClass.start + 1)
    Log.d("aa", "${singleClass.end}-${singleClass.start+1}")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp)
            .padding(2.dp) // 外边距
            .alpha(0.75F),
        elevation = 1.dp, // 设置阴影
    ) {
        val nameList = singleClass.courseName.split("\n")

        val showDetailDialog = remember { mutableStateOf(false) }
//        ClassDetailDialog(openDialog = showDetailDialog, singleClass = singleClass)
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.W600, color = Color.White, fontSize = 13.sp)
                ) {
                    append(nameList[0]+"\n"+nameList[1]+"\n\n")
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.W600, color = Color.White, fontSize = 10.sp)
                ) {
                    append(nameList[2])
                }
            },
            modifier = Modifier
                .background(singleClass.color)
                .clickable {
                    showDetailDialog.value = true
                },
            textAlign = TextAlign.Center
        )
    }
}


fun getNowWeek(): Int {
    return GetDataUtil.whichWeekNow()
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview2() {
//    NormalScheduleTheme {
//
//    }
//}