package com.liflymark.normalschedule.ui.update_course

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.model.GetNewCourseResponse
import com.liflymark.normalschedule.logic.utils.Convert
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// 上传-本地课程选择页面 + 网络端界面
@Composable
fun UpdateHome(
    networkCourse: GetNewCourseResponse?,
    userNumber:String,
    updateCourseViewModel: UpdateCourseViewModel = viewModel()
){
    val networkCourseRes = mutableListOf<CourseBean>()
    val localCourseNames = Repository.loadAllCourseName().collectAsState(initial = listOf())
    val scope = rememberCoroutineScope()
    val uploadMap = updateCourseViewModel.uploadResponse.observeAsState()
    val context = LocalContext.current
    LaunchedEffect(uploadMap.value){
        uploadMap.value?.let { Toasty.info(context, it.status).show() }
    }
    Spacer(modifier = Modifier.fillMaxWidth())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        LocalCourse(
            modifier = Modifier
                .weight(0.5f)
                .wrapContentHeight(),
            allCourseName = localCourseNames.value,
            postCourse = { courseNames ->
                updateCourseViewModel.uploadCourse(
                    courseNames = courseNames,
                    userNumber = userNumber,
                    userCode = "0820"
                )
            }
        )
        networkCourse?.content?.forEach { singleNet ->
            val res = Convert.StringToListBean(singleNet.fields.bean_list_str)
            networkCourseRes.addAll(res)
            NetworkCourses(
                    modifier = Modifier
                        .weight(0.5f)
                        .wrapContentHeight(),
            listBean = networkCourseRes.toList()
            ) {
                updateCourseViewModel.uploadCourse(
                    courseNames = null,
                    userNumber = userNumber,
                    userCode = "0820"
                )
            }
        }
    }
}

// 上传-本地端显示页面
@Composable
fun LocalCourse(
    modifier: Modifier,
    allCourseName:List<String>,
    postCourse:(listString:List<String>) -> Unit
){
    val allCourseNameNative:MutableList<String> = mutableListOf()
    // 该写法不够优雅
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(text = "本地的课程")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                allCourseName.forEach{
                    var selected by rememberSaveable { mutableStateOf(false) }
                    if (selected){ allCourseNameNative.add(it) }
                    key(it) {
                        SingleCourse(
                            selected = selected,
                            onClick =
                            {
                                selected = !selected
                            },
                            text = it
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f),
            contentAlignment = Alignment.TopCenter
        ) {
            OutlinedButton(onClick = {
                postCourse(allCourseNameNative)
            }) {
                Text(text = "上传并更新数据")
            }
        }
    }
}

// 上传-网络端显示界面
@Composable
fun NetworkCourses(
    modifier: Modifier,
    listBean: List<CourseBean>,
    clear:() -> Unit
){
    val courseNamesSet = listBean.map { it.courseName }.toSet()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(text = "已上传的课程")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                courseNamesSet.forEach{
                    var selected by rememberSaveable { mutableStateOf(false) }
                    key(it) {
                        NoSelectCourse(
                            onClick =
                            {
                                selected = !selected
                            },
                            text = it
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier =
        Modifier
            .fillMaxWidth()
            .weight(0.2f),
            contentAlignment = Alignment.TopCenter
        ) {
            OutlinedButton(onClick = {
                clear()
            }) {
                Text(text = "清空上传课程")
            }
        }
    }
}

