package com.liflymark.normalschedule.ui.add_course

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.Convert.toColorULong
import com.liflymark.normalschedule.logic.utils.Dialog
import com.liflymark.normalschedule.logic.utils.Dialog.whichIs1
import com.liflymark.normalschedule.logic.utils.SelectSessionDialog
import com.liflymark.normalschedule.logic.utils.SelectWeekDialog
import com.liflymark.normalschedule.ui.score_detail.ProgressDialog
import com.liflymark.normalschedule.ui.theme.NorScTheme
import kotlinx.coroutines.launch
import kotlin.reflect.KMutableProperty1


@Composable
fun ShowAllCourseToEdit(
    courseName:String
){
    val context = LocalContext.current as Activity
    var courseNameState by rememberSaveable { mutableStateOf(courseName) }
    val deleteCourseList = remember{ mutableListOf<CourseBean>() }
    val needAddCourseList = remember{ mutableStateListOf<CourseBean>() }
    val courseBeanList = remember(needAddCourseList){ mutableStateListOf<CourseBean>() }
    val progressShow = rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var setColor by rememberSaveable{ mutableStateOf("") }
    val colorList = remember {
        arrayListOf(
            "#12c2e9",
            "#376B78",
            "#f64f59",
            "#CBA689",
            "#ffffbb33",
            "#8202F2",
            "#F77CC2",
            "#4b5cc4",
            "#426666",
            "#40de5a",
            "#f0c239",
            "#725e82",
            "#c32136"
        )
    }
    val colors = colorList.map { Convert.colorStringToInt(it) }.toIntArray()
    val colorPickerDialog = remember {
        Dialog.getColorPickerDialog(context, colors = colors) { _, colorInt ->
            setColor = colorList[colors.indexOf(colorInt)]
        }
    }
    fun needAddToStateList(){
        courseBeanList.clear()
        courseBeanList.addAll(needAddCourseList)
    }

    fun sideAddToState(index:Int,courseBean:CourseBean){
        courseBeanList[index].apply {
            classWeek=courseBean.classWeek
            classDay=courseBean.classDay
            classSessions = courseBean.classSessions
            continuingSession = courseBean.continuingSession
            teacher = courseBean.teacher
            teachingBuildName = courseBean.teachingBuildName
        }
    }

    LaunchedEffect(true){
        progressShow.value = true
        val courseBeanLoad = Repository.loadCourseByName(courseName)
        courseBeanList.addAll(courseBeanLoad)
        needAddCourseList.addAll(courseBeanLoad)
        deleteCourseList.addAll(courseBeanLoad)
        progressShow.value = false
    }
    // 加载条
    ProgressDialog(openDialog = progressShow, label = "正在保存", dismissOnClickOutside = false)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 3.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 课程名称显示
        CourseNameShow(
            courseName = courseNameState,
            onValueChange = {
                courseNameState = it
            }
        )
        Spacer(modifier = Modifier.height(3.dp))
        Surface( color = Color(0xFFB9EEFF), shape = MaterialTheme.shapes.medium ) {
            ClassLine(icon = Icons.Filled.ColorLens, content = "点击选择颜色"){
                colorPickerDialog.show()
            }
        }

//        SideEffect {
//            needAddToStateList()
//        }
        // 课程时间段显示
        courseBeanList.forEachIndexed { index,courseBean ->
            Log.d("AddorEdit", "ShowAll重组一次")
//            sideAddToState()
            key(
                courseBean.campusName+courseBean.classDay+courseBean.classSessions+courseBean.classWeek+
                        courseBean.color+courseBean.continuingSession+courseBean.teacher+courseBean.teachingBuildName
            ) {
                Spacer(modifier = Modifier.height(3.dp))
                ShowTimePart(
                    courseBean =courseBean,
                    onValueChange = {
                        needAddCourseList[index] = it
                        sideAddToState(index, it)
                        Log.d("AddorEdit", it.classDay.toString())
                                    },
                    deleteClick = {
                        needAddCourseList.remove(it)
                        needAddToStateList()
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color(0xFFB9EEFF)
        ) {
            Row(
                modifier = Modifier
                    .height(45.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val textBtMod = Modifier.weight(1f)
                TextButton(onClick = {
                    if (!needAddCourseList.isEmpty()){
                        needAddCourseList.removeLast()
                        needAddToStateList()
                    }
                }, modifier = textBtMod) {
                    Text(text = "删除末尾时段")
                }
                TextButton(onClick = {
                    needAddCourseList.add(
                        CourseBean(
                            campusName="五四路校区",
                            classDay=3,
                            classSessions=9,
                            classWeek="111111111111110000000000",
                            continuingSession=3,
                            courseName=courseName,
                            teacher="",
                            teachingBuildName="",
                            color="#f0c239"
                        )
                    )
                    needAddToStateList()
                }, modifier = textBtMod) {
                    Text(text = "增加时段")
                }
                TextButton(onClick = {
                    scope.launch {
                        progressShow.value = true
                        Repository.deleteCourseByList(deleteCourseList)
                        needAddCourseList.map {
                            it.courseName = courseNameState
                            it.color = if (setColor==""){ Convert.stringToColor(courseNameState) }else{ setColor }
                        }
                        Repository.insertCourse(needAddCourseList)
                        updateWidget(context = context)
                        progressShow.value = false
                        context.finish()
                    }
                }, modifier = textBtMod) {
                    Text(text = "保存更改")
                }
            }
        }
    }

}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun ShowTimePart(
    courseBean:CourseBean,
    onValueChange: (CourseBean) -> Unit,
    deleteClick: (courseBean: CourseBean) -> Unit
){
    val (courseBeanState, setCourseBean) = remember { mutableStateOf(courseBean) }
    val oneList = courseBeanState.classWeek.whichIs1()
    var courseStartToEnd by
        remember {
            mutableStateOf("    第${courseBeanState.classSessions} - ${courseBeanState.classSessions + courseBeanState.continuingSession - 1}节")
        }
    var courseTime by remember { mutableStateOf(Dialog.getWeekNumFormat(oneList)) }
    var weekNum by remember{
        mutableStateOf(
            when (courseBeanState.classDay) {
                1 -> "周一$courseStartToEnd"
                2 -> "周二$courseStartToEnd"
                3 -> "周三$courseStartToEnd"
                4 -> "周四$courseStartToEnd"
                5 -> "周五$courseStartToEnd"
                6 -> "周六$courseStartToEnd"
                7 -> "周日$courseStartToEnd"
                else -> "错误"
            }
        )
    }
    var courseTeacher by rememberSaveable { mutableStateOf(courseBean.teacher) }
    var courseRoom by rememberSaveable { mutableStateOf(courseBean.teachingBuildName) }
    LaunchedEffect(courseBeanState){
        onValueChange(courseBeanState)
    }

    val showSelectWeekDialog = rememberSaveable { mutableStateOf(false) }
    val showSessionWeekDialog = rememberSaveable { mutableStateOf(false) }

    fun <T> onChange(field: KMutableProperty1<CourseBean, T>, value: T){
        val next = courseBean.copy()
        field.set(next, value)
        setCourseBean(next)
    }

    SelectWeekDialog(
        showDialog = showSelectWeekDialog,
        initialR = courseBean.classWeek
    ) {
        courseTime = Dialog.getWeekNumFormat(it.whichIs1())
        onChange(CourseBean::classWeek, it)
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
//        weekNum1(classWeek, classSessions, -continueSession)
        Log.d("addOrEdit", classWeek.toString())
//        onChange(CourseBean::classDay,classWeek)
//        onChange(CourseBean::classSessions, classSessions)
        courseBeanState.classDay = classWeek
        courseBeanState.classSessions = classSessions
        courseBeanState.continuingSession = continueSession
        courseStartToEnd = "    第${courseBeanState.classSessions} - ${courseBeanState.classSessions + courseBeanState.continuingSession - 1}节"
        weekNum = when (classWeek) {
            1 -> "周一$courseStartToEnd"
            2 -> "周二$courseStartToEnd"
            3 -> "周三$courseStartToEnd"
            4 -> "周四$courseStartToEnd"
            5 -> "周五$courseStartToEnd"
            6 -> "周六$courseStartToEnd"
            7 -> "周日$courseStartToEnd"
            else -> "错误"
        }
        onChange(CourseBean::continuingSession, continueSession)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        backgroundColor = Color(0xFFB9EEFF)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            // 删除按钮
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    deleteClick(courseBean)
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "删除")
                }
            }

            ClassLine(
                icon = Icons.Outlined.DateRange,
                content = courseTime
            ) {
                showSelectWeekDialog.value = true
            }
            ClassLine(
                icon = Icons.Outlined.WatchLater,
                content = weekNum
            ) {
                showSessionWeekDialog.value = true
            }
            TeacherOrBuildTextField(
                onValueChange =
                {
                    courseTeacher = it
                    onChange(CourseBean::teacher, courseTeacher)
                },
                value = courseTeacher,
                placeHolder = "点击此处输入任课老师",
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                ,
                leadingIcon = Icons.Filled.Group
            )
            TeacherOrBuildTextField(
                onValueChange =
                {
                    courseRoom = it
                    onChange(CourseBean::teachingBuildName, courseRoom)
                },
                value = courseRoom,
                placeHolder = "点击此处输入教室",
                leadingIcon = Icons.Filled.Room,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun TeacherOrBuildTextField(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    value: String,
    placeHolder: String,
    leadingIcon: ImageVector
){
    TransparentTextFiled(
        value = value,
        onValueChange = { onValueChange(it) },
        placeHolder = placeHolder,
        modifier = modifier,
        leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) }
    )
}

@Composable
fun ClassLine(icon: ImageVector, content: String, onClick: () -> Unit = {}) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .clickable { onClick() }
    ) {
        Spacer(modifier = Modifier.width(15.dp))
        Icon(
            imageVector = icon, contentDescription = null,
            modifier = Modifier
                .width(25.dp)
                .height(25.dp)
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(text = content, modifier = Modifier.fillMaxWidth(), maxLines = 2, fontSize = 15.sp)
    }
}

@Composable
fun CourseNameShow(
    courseName:String,
    onValueChange: (String) -> Unit
){
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
        ,
        shape = MaterialTheme.shapes.medium,
        backgroundColor = Color(0xFFB9EEFF)
    ) {
        TransparentTextFiled(
            value = courseName,
            onValueChange = { onValueChange(it) },
            placeHolder = "点击此处输入课程名称",
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Book, contentDescription = "课程名称") }
        )
    }
}

@Composable
fun TransparentTextFiled(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeHolder:String,
    leadingIcon: @Composable (() -> Unit)? = null,
){
    var textFieldValueState by remember { mutableStateOf(value) }
    val textFieldValue = textFieldValueState
    TextField(
        value = value,
        onValueChange = {
            textFieldValueState = it
            if (textFieldValue != it) {
                onValueChange(it)
            }
        },
        modifier = modifier,
        maxLines = 1,
        placeholder = { Text(text = placeHolder) },
        leadingIcon = leadingIcon,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        singleLine = true
    )
}


@Preview(showBackground = true)
@Composable
fun EditOrAddPreview(){
    NorScTheme {
        Column {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(30.dp))
            ShowAllCourseToEdit(courseName = "药物化学")
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(30.dp))
        }
    }
}