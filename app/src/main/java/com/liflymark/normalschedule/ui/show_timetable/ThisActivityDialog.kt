package com.liflymark.normalschedule.ui.show_timetable


import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.Dialog
import com.liflymark.normalschedule.logic.utils.Dialog.whichIs1
import com.liflymark.normalschedule.ui.edit_course.EditCourseActivity
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.flow.collect


/*
* 课程详情Dialog
* */
@Composable
fun ClassDetailDialog(openDialog:MutableState<Boolean>,singleClass: OneByOneCourseBean){
    val emptyCourseBean = CourseBean("",0,0,"011110",0,"异常查询","","","")
    val realCourseMessage = singleClass.courseName.split("\n")
    val courseBeanListState = remember { mutableStateOf(emptyCourseBean) }
    val openDeleteDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current as Activity

    DeleteCourseDialog(deleteDialogOpen = openDeleteDialog, singleClass = singleClass)

    LaunchedEffect(true){
        Repository.loadCourseByNameAndStart(
            realCourseMessage[0],
            singleClass.start,
            singleClass.whichColumn
        ).collect {
            if (it.isSuccess) {
                val result = it.getOrNull()
                if (result != null) {
                    courseBeanListState.value = result.getOrElse(0) { emptyCourseBean }
                }
            } else {
                it.getOrElse { a ->
                    Log.d("dialog", a.toString())
                }
            }
        }
    }
    if (openDialog.value){
        AlertDialog(
            modifier = Modifier.fillMaxWidth(0.95f),
            onDismissRequest = { openDialog.value = false },
            title = {
                Row() {
                    Text(text = courseBeanListState.value.courseName, Modifier.fillMaxWidth(0.8f), fontSize = 20.sp)
                    Text(text = " X ",
                        Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .clickable { openDialog.value = false }, textAlign = TextAlign.Center)
                }
            },
            text = {
                CourseDiaContent(courseBean = courseBeanListState.value)
            },
            buttons = {
                Row {
                    IconButton(onClick = {
                        openDialog.value = false
                        openDeleteDialog.value = true
                    }, modifier = Modifier.weight(1F)) {
                        Icon(imageVector = Icons.Outlined.DeleteForever, contentDescription = "删除课程")
                    }
                    IconButton(onClick = {
                        openDialog.value = false
                        val intent = Intent(context, EditCourseActivity::class.java).apply {
                            putExtra("courseName", courseBeanListState.value.courseName)
                        }
                        context.startActivity(intent)
                    }, modifier = Modifier.weight(1F)) {
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "编辑课程")
                    }
                }

            }
        )
    }
}

@Composable
fun CourseDiaContent(courseBean: CourseBean){
    val oneList = courseBean.classWeek.whichIs1()
    val courseStartToEnd =
        "    第${courseBean.classSessions} - ${courseBean.classSessions+courseBean.continuingSession-1}节"
    val courseTime = Dialog.getWeekNumFormat(oneList)
    val weekNum = when(courseBean.classDay){
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

    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)){
            Spacer(modifier = Modifier.height(25.dp))
        }
        ClassLine(icon =  Icons.Outlined.DateRange , content = courseTime)
        ClassLine(icon =  Icons.Outlined.WatchLater , content = weekNum)
        ClassLine(icon = Icons.Outlined.Group, content = courseTeacher)
        ClassLine(icon = Icons.Outlined.Room, content = courseRoom)
    }
}


@Composable
fun ClassLine(icon: ImageVector, content: String){
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier
            .width(32.dp)
            .height(32.dp))
        Spacer(modifier = Modifier.width(25.dp))
        Text(text = content, modifier = Modifier.fillMaxWidth(),maxLines = 2, fontSize = 15.sp)
    }
}

/*
* 删除课程Dialog
* */
@Composable
fun DeleteCourseDialog(deleteDialogOpen:MutableState<Boolean>,singleClass: OneByOneCourseBean, showTimetableViewModel: ShowTimetableViewModel = viewModel()){
    val context = LocalContext.current
    val realCourseName = singleClass.courseName.split("\n")[0]
    val deleteResult = showTimetableViewModel.deleteCourseBeanByNameLiveData.observeAsState()
//    LaunchedEffect(deleteResult){
//        showTimetableViewModel.loadAllCourse()
//    }
    if (deleteDialogOpen.value){
        AlertDialog(
            modifier = Modifier.fillMaxWidth(0.95f)
            ,
            onDismissRequest = { 
                deleteDialogOpen.value = false
                Toasty.info(context,"删除取消").show()
            },
            title = {
                Text(text = "你在进行一步敏感操作")
            },
            text = {
                Text(text = "你将删除《${realCourseName}》的所有课程\n" +
                        "无法恢复，务必谨慎删除！！！\n" +
                        "如失误删除请点击右上角重新导入或手动添加")
            },
            confirmButton = {
                TextButton(onClick = {
                    deleteDialogOpen.value = false
                    showTimetableViewModel.deleteCourse(realCourseName)
                    Toasty.success(context,"删除成功").show()
                    showTimetableViewModel.loadAllCourse()
                }) {
                    Text(text = "仍然删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteDialogOpen.value = false }) {
                    Text(text = "取消")
                }
            }
        )
    }
}