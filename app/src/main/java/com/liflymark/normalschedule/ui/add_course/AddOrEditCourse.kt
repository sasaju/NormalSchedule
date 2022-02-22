package com.liflymark.normalschedule.ui.add_course

import android.app.Activity
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Room
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.utils.*
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.Dialog.whichIs1
import com.liflymark.normalschedule.ui.score_detail.ProgressDialog
import com.liflymark.normalschedule.ui.theme.NorScTheme
import com.liflymark.schedule.data.twoColorItem
import kotlinx.coroutines.launch
import kotlin.reflect.KMutableProperty1

class ViewFactory(private val courseName: String):ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddOrEditCourseViewModel(courseName) as T
    }
}


@Composable
fun ShowAllCourseToEdit(
    courseName:String,
    addOrEditCourseViewModel: AddOrEditCourseViewModel = viewModel(factory = ViewFactory(courseName = courseName))
){
    val context = LocalContext.current as Activity
    val courseBeanListState = remember { addOrEditCourseViewModel.courseListState }
    var courseNameState by rememberSaveable { mutableStateOf(courseName) }
    val progressShow = rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var setColorIndex by remember{ addOrEditCourseViewModel.courseColorIndex }
    val colorListSettings = Repository.getScheduleSettingsColorList()
        .collectAsState(initial = Repository.colorStringListToTwoItems())
    fun colorListToColors(colorsList:List<twoColorItem>): IntArray {
        val colorStrings = colorsList.map { it.colorItemList[0] }
        return colorStrings.map { android.graphics.Color.parseColor(it) }.toIntArray()
    }
    var colors by remember(colorListSettings.value) { mutableStateOf(colorListToColors(colorListSettings.value)) }
    LaunchedEffect(colorListSettings.value){ colors =  colorListToColors(colorListSettings.value) }
    val colorPickerDialog = remember {
        Dialog.getColorPickerDialog(context, colors = colors) { _, colorInt ->
            setColorIndex = colors.indexOf(colorInt)
        }
    }

    LaunchedEffect(true){
        progressShow.value = true

        progressShow.value = false
    }
    // 加载条
    ProgressDialog(openDialog = progressShow, label = "正在保存", dismissOnClickOutside = false)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 3.dp)
            .verticalScroll(rememberScrollState())
            .animateContentSize()
    ) {
        // 课程名称显示
        CourseNameShow(
            courseName = courseNameState,
            onValueChange = {
                courseNameState = it
            }
        )
        Spacer(modifier = Modifier.height(3.dp))
        Surface( color = MaterialTheme.colors.secondary, shape = MaterialTheme.shapes.medium ) {
            ClassLine(icon = Icons.Filled.ColorLens, content = "点击选择颜色"){
                colorPickerDialog.show()
            }
        }

//        SideEffect {
//            needAddToStateList()
//        }
        // 课程时间段显示
        courseBeanListState.forEachIndexed { index,courseBean ->
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
                        courseBeanListState[index] = it
                        Log.d("AddOrEdit", "ShowTiemPart：${it}")
                                    },
                    deleteClick = {
                        courseBeanListState.remove(it)
                    },
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
                    if (!courseBeanListState.isEmpty()){
                        courseBeanListState.removeLast()
                    }
                }, modifier = textBtMod) {
                    Text(text = "删除末尾时段")
                }
                TextButton(onClick = {
                    courseBeanListState.add(
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
                    if (setColorIndex==-1){setColorIndex=Convert.courseNameToIndex(courseName,13)}
                }, modifier = textBtMod) {
                    Text(text = "增加时段")
                }
                TextButton(onClick = {
                    scope.launch {
                        progressShow.value = true
                        addOrEditCourseViewModel.saveChange(courseNameState, setColorIndex)
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
    val oneList = courseBean.classWeek.whichIs1()
    var courseStartToEnd by
        remember {
            mutableStateOf("    第${courseBean.classSessions} - ${courseBean.classSessions + courseBean.continuingSession - 1}节")
        }
    var courseTime by remember { mutableStateOf(Dialog.getWeekNumFormat(oneList)) }
    var weekNum by remember{
        mutableStateOf(
            when (courseBean.classDay) {
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
    Log.d("AddOrEditCOurse","ShowTImePart重组")
    val showSelectWeekDialog = rememberSaveable { mutableStateOf(false) }
    val showSessionWeekDialog = rememberSaveable { mutableStateOf(false) }
    val showTeacherTextDialog = rememberSaveable { mutableStateOf(false) }
    val showBuildTextDialog = rememberSaveable { mutableStateOf(false) }

    fun <T> onChange(field: KMutableProperty1<CourseBean, T>, value: T){
        val next = courseBean.copy()
        field.set(next, value)
        onValueChange(next)
    }
    fun <T> onChange(fields: List<KMutableProperty1<CourseBean, T>>, values: List<T>){
        val next = courseBean.copy()
        fields.forEachIndexed { index, it ->
            it.set(next, values[index])
        }
        onValueChange(next)
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
//        Log.d("addOrEdit", classWeek.toString())
//        onChange(CourseBean::classDay,classWeek)
//        onChange(CourseBean::classSessions, classSessions)
        courseBean.classDay = classWeek
        courseBean.classSessions = classSessions
        courseBean.continuingSession = continueSession
        courseStartToEnd = "    第${courseBean.classSessions} - ${courseBean.classSessions + courseBean.continuingSession - 1}节"
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
        onChange(listOf(CourseBean::continuingSession, CourseBean::classDay, CourseBean::classSessions),
                    listOf(continueSession, classWeek, classSessions))
    }
    if (showTeacherTextDialog.value) {
        TextFieldDialog(
            value = courseBean.teacher,
            placeHolder = "输入教师名称",
            onDismissRequest = { showTeacherTextDialog.value = false },
            onClick = {
                onChange(CourseBean::teacher, it)
                showTeacherTextDialog.value = false
            }
        )
    }

    if (showBuildTextDialog.value){
        TextFieldDialog(
            value = courseBean.teachingBuildName,
            placeHolder = "请输入上课地点",
            onDismissRequest = { showBuildTextDialog.value = false },
            onClick = {
                onChange(CourseBean::teachingBuildName, it)
                showBuildTextDialog.value = false
            }
        )
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        backgroundColor = MaterialTheme.colors.secondary
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
            ClassLine(
                icon = Icons.Outlined.Group,
                content = courseBean.teacher.emptyTo("教师名未填-可不填")
            ) {
                showTeacherTextDialog.value = true
            }
            ClassLine(
                icon = Icons.Outlined.Room,
                content = courseBean.teachingBuildName.emptyTo("上课地未填-可不填")
            ) {
                showBuildTextDialog.value = true
            }
        }
    }
}
private fun String.emptyTo(targetString:String) = if (this==""){ targetString }else{ this }
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
        backgroundColor = MaterialTheme.colors.secondary
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