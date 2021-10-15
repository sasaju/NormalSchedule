package com.liflymark.normalschedule.ui.add_course

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.utils.EditDialog
import com.liflymark.normalschedule.ui.edit_course.CardContent
import com.liflymark.normalschedule.ui.edit_course.CourseContent
import com.liflymark.normalschedule.ui.edit_course.CourseTitle
import com.liflymark.normalschedule.ui.score_detail.ProgressDialog
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import kotlinx.coroutines.launch

class AddCourseComposeActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NorScTheme {
                // A surface container using the 'background' color from the theme
                NorScTheme {
                    UiControl()
                    Column {
                        NormalTopBar(label = "添加课程")
                        AddPage(courseName = "点击此设置名称")
                    }
                }
            }
        }
    }
}


@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun AddPage(
    courseName: String,
    ecViewModel: AddCourseActivityViewModel = viewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current as AddCourseComposeActivity
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
            ecViewModel.loadCourseByName(courseName).collectAsState(null)
        LaunchedEffect(courseBeanList.value) {
            Log.d("EditAC", courseBeanList.value.toString())
            if (addOne < 2) {
                courseBeanList.value?.let { ecViewModel.addDeleteClass(it) }
                newCourseMutableList.clear()
                courseBeanList.value?.let { newCourseMutableList.addAll(it) }
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
                        newCourseMutableList.add(ecViewModel.getNewBean(sideEffectCourse))
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
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview3() {
//    NormalScheduleTheme {
//        Greeting("Android")
//    }
//}