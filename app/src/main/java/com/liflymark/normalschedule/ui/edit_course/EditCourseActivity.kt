package com.liflymark.normalschedule.ui.edit_course

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.utils.*
import com.liflymark.normalschedule.logic.utils.Dialog.whichIs1
import com.liflymark.normalschedule.ui.score_detail.ProgressDialog
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import kotlinx.coroutines.launch

class EditCourseActivity : ComponentActivity() {
    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val courseName = intent.getStringExtra("courseName") ?: ""
        setContent {
            NorScTheme {
                UiControl()
                Column {
                    NormalTopBar(label = "添加课程")
                    AllPage(courseName = courseName)
                }
            }
        }
    }
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun AllPage(
    courseName: String,
    ecViewModel: EditCourseViewModel = viewModel(),

) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current as EditCourseActivity
    val newCourseMutableList = remember { mutableStateListOf<CourseBean>() }
    val progressShow = remember {
        mutableStateOf(false)
    }
    val showNameDialog = remember {
        mutableStateOf(false)
    }
    var sideEffectCourse by remember {
        mutableStateOf(courseName)
    }
    var addOne by remember {
        mutableStateOf(0)
    }

    if (courseName != "") {
        val courseBeanList =
            ecViewModel.loadCourseByName(courseName).collectAsState(ecViewModel.initCourseBean)
        LaunchedEffect(courseBeanList.value) {
            Log.d("EditAC", courseBeanList.value.toString())
            if (addOne < 2) {
                ecViewModel.addDeleteClass(courseBeanList.value)
                newCourseMutableList.clear()
                newCourseMutableList.addAll(courseBeanList.value)
                addOne += 1
            }
        }
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProgressDialog(openDialog = progressShow, label = "正在保存")
            EditDialog(showDialog = showNameDialog, initialString = sideEffectCourse){
                sideEffectCourse = it
                for (courseBean in newCourseMutableList){
                    courseBean.courseName = it
                    Log.d("edit", it)
                    Log.d("newCourseMutableList", newCourseMutableList.toString())
                }
            }
            CourseTitle(sideEffectCourse){
                showNameDialog.value = true
            }
            var count = 0
            for (i in newCourseMutableList) {
                key(count.toString() + i.toString()) {
                    CardContent {
                        CourseContent(
                            courseBean = i,
                            deleteClick = {
                                newCourseMutableList.remove(i)
                                Log.d("EditCOurse", newCourseMutableList.size.toString())
                            },
                            courseTime1 = {
                                val now = newCourseMutableList.indexOf(i)
                                val nowBean = i.copy()
                                nowBean.classWeek = it
                                newCourseMutableList[now] = nowBean
                            },
                            weekNum1 = { classWeek, classSessions, continueSession ->
                                val now = newCourseMutableList.indexOf(i)
                                val nowBean = i.copy()
                                nowBean.classDay = classWeek
                                nowBean.classSessions = classSessions
                                nowBean.continuingSession = continueSession
                                newCourseMutableList[now] = nowBean
                            },
                            courseTeacher1 = {
                                val now = newCourseMutableList.indexOf(i)
                                val nowBean = i.copy()
                                nowBean.teacher = it
                                newCourseMutableList[now] = nowBean
                            },
                            courseRoom1 = {
                                val now = newCourseMutableList.indexOf(i)
                                val nowBean = i.copy()
                                nowBean.teachingBuildName = it
                                newCourseMutableList[now] = nowBean
                            }
                        )
                    }
                }
                count += 0
            }
            Spacer(modifier = Modifier.height(20.dp))
            CardContent {
                Row(
                    modifier = Modifier.height(45.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val textBtMod = Modifier.weight(1f)
                    TextButton(onClick = {
                        newCourseMutableList.removeLastOrNull()
                    }, modifier = textBtMod) {
                        Text(text = "删除末尾时段")
                    }
                    TextButton(onClick = {
                        newCourseMutableList.add(ecViewModel.initCourseBean[0])
                        newCourseMutableList.last().courseName = sideEffectCourse
                    }, modifier = textBtMod) {
                        Text(text = "增加时段")
                    }
                    TextButton(onClick = {
                        scope.launch {
                            progressShow.value = true
                            ecViewModel.addNewClass(newCourseMutableList)
                            ecViewModel.updateClass()
                            context.finish()
                        }
                    }, modifier = textBtMod) {
                        Text(text = "保存更改")
                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun Test() {
    val scope = rememberCoroutineScope()
    val list = listOf("page1", "page2", "page3", "page4", "page5", "page6", "page7")
    val pagerState = rememberPagerState(
//        pageCount = list.size,
        initialPage = 0,
//        initialOffscreenLimit = list.size
    )
    Column {
        StringPicker(strList = list, pagerState = pagerState, modifier = Modifier.height(200.dp)) {}
        Button(onClick = {
            scope.launch {
                pagerState.animateScrollToPage(2)
            }
        }) {
            Text(text = "start")
        }
    }
}

@Composable
fun CourseTitle(courseName: String, onClick:()->Unit = {}) {
    CardContent {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(5.dp)
                .clickable { onClick() }

        ) {
            Icon(
                imageVector = Icons.Outlined.Book,
                contentDescription = null,
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp)
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text(text = courseName, fontSize = 17.sp)
        }
    }
}

@Composable
fun CardContent(content: @Composable () -> Unit) {
    Card(
        elevation = 2.dp, modifier = Modifier
            .fillMaxWidth(0.98f)
            .padding(5.dp),
        backgroundColor = Color(0xFF86DCF7)
    ) {
        content()
    }
}


@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun CourseContent(
    courseBean: CourseBean,
    deleteClick: (courseBean: CourseBean) -> Unit = {},
    courseTime1: (courseTime: String) -> Unit = {},
    weekNum1: (classWeek: Int, classSessions: Int, continueSession: Int) -> Unit,
    courseTeacher1: (teacher: String) -> Unit = {},
    courseRoom1: (buildName: String) -> Unit = {},
) {
    val oneList = courseBean.classWeek.whichIs1()
    val courseStartToEnd =
        "    第${courseBean.classSessions} - ${courseBean.classSessions + courseBean.continuingSession - 1}节"
    var courseTime by remember {
        mutableStateOf(Dialog.getWeekNumFormat(oneList))
    }
    val weekNum = when (courseBean.classDay) {
        1 -> "周一$courseStartToEnd"
        2 -> "周二$courseStartToEnd"
        3 -> "周三$courseStartToEnd"
        4 -> "周四$courseStartToEnd"
        5 -> "周五$courseStartToEnd"
        6 -> "周六$courseStartToEnd"
        7 -> "周日$courseStartToEnd"
        else -> "错误"
    }
    val courseTeacher = courseBean.teacher
    val courseRoom = courseBean.teachingBuildName

    Column(modifier = Modifier.padding(5.dp)) {
        val showSelectWeekDialog = remember { mutableStateOf(false) }
        val showSessionWeekDialog = remember { mutableStateOf(false) }
        val showTeacherDialog = remember { mutableStateOf(false) }
        val showBuildDialog = remember { mutableStateOf(false) }
        SelectWeekDialog(
            showDialog = showSelectWeekDialog,
            initialR = courseBean.classWeek
        ) {
            courseTime = Dialog.getWeekNumFormat(it.whichIs1())
            courseTime1(it)
        }
        SelectSessionDialog(
            showDialog = showSessionWeekDialog,
            initialWeek = courseBean.classDay - 1,
            initialStart = courseBean.classSessions - 1,
            initialEnd = courseBean.classSessions + courseBean.continuingSession - 2
        ) { week, start, end ->
            val classWeek = week + 1
            val classSessions = start + 1
            val continueSession = end + 2 - classSessions
            weekNum1(classWeek, classSessions, continueSession)
        }
        EditDialog(
            showDialog = showTeacherDialog,
            initialString = courseBean.teacher
        ) {
            courseTeacher1(it)
        }
        EditDialog(
            showDialog = showBuildDialog,
            initialString = courseBean.teachingBuildName
        ) {
            courseRoom1(it)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .height(25.dp)
        ) {
            IconButton(onClick = {
                deleteClick(courseBean)
            }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }

        ClassLine(icon = Icons.Outlined.DateRange, content = courseTime) {
            showSelectWeekDialog.value = true
        }
        ClassLine(icon = Icons.Outlined.WatchLater, content = weekNum) {
            showSessionWeekDialog.value = true
        }
        ClassLine(icon = Icons.Outlined.Group, content = courseTeacher) {
            showTeacherDialog.value = true
        }
        ClassLine(icon = Icons.Outlined.Room, content = courseRoom) {
            showBuildDialog.value = true
        }
    }
}


@Composable
fun ClassLine(icon: ImageVector, content: String, block: () -> Unit = {}) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .clickable { block() }
    ) {
        Icon(
            imageVector = icon, contentDescription = null,
            modifier = Modifier
                .width(32.dp)
                .height(32.dp)
        )
        Spacer(modifier = Modifier.width(25.dp))
        Text(text = content, modifier = Modifier.fillMaxWidth(), maxLines = 2, fontSize = 15.sp)
    }
}

@Composable
fun EditLine(icon: ImageVector, content: String, onValueChange: (it: String) -> Unit) {
    var input by remember {
        mutableStateOf(content)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Icon(
            imageVector = icon, contentDescription = null,
            modifier = Modifier
                .width(32.dp)
                .height(32.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        TextField(
            value = input,
            maxLines = 1,
            onValueChange = {
                input = it
                onValueChange(it)
            },
            textStyle = TextStyle(fontSize = 15.sp),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black,
                disabledTextColor = Color.Transparent,
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

//@ExperimentalPagerApi
//@ExperimentalMaterialApi
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview5() {
//    NormalScheduleTheme {
//        CardContent {
//            Row {
//                TextButton(onClick = {
//
//                }) {
//                    Text(text = "删除末尾时段")
//                }
//                TextButton(onClick = {
//
//                }) {
//                    Text(text = "增加时段")
//                }
//                TextButton(onClick = {
//
//                }) {
//                    Text(text = "保存更改")
//                }
//            }
//        }
//    }
//}