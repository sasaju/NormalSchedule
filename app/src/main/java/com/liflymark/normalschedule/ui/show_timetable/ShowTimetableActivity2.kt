package com.liflymark.normalschedule.ui.show_timetable

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GetApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.afollestad.materialdialogs.MaterialDialog
import com.google.accompanist.glide.rememberGlidePainter
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.pager.*
import com.gyf.immersionbar.ImmersionBar
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.bean.getData
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.Dialog
import com.liflymark.normalschedule.ui.add_course.AddCourseActivity
import com.liflymark.normalschedule.ui.import_again.ImportCourseAgain
import com.liflymark.test.ui.theme.TestTheme
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShowTimetableActivity2 : ComponentActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(ShowTimetableViewModel::class.java) }
    @DelicateCoroutinesApi
    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    @ExperimentalPagerApi

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .fitsSystemWindows(false).
            statusBarDarkFont(true).
            init()
        saveAllCourse(intent, this, viewModel)
        setContent {
            TestTheme {
                BackGroundImage(viewModel = viewModel)
                ProvideWindowInsets() {
                    Column() {
                        Drawer(viewModel){
                            Spacer(
                                Modifier
                                    .background(Color.Transparent)
                                    .statusBarsHeight() // Match the height of the status bar
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAllCourse()
        viewModel.setBackground()
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun Drawer(viewModel: ShowTimetableViewModel, statusSpacer: @Composable () -> Unit){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val startSchoolOrNot = viewModel.startSchool()
    val startHolidayOrNot = viewModel.startHoliday()
    val courseList: State<List<List<OneByOneCourseBean>>?> =
        viewModel.courseDatabaseLiveDataVal.observeAsState(getNeededClassList(getData()))

    viewModel.loadAllCourse()
    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
                DrawerNavHost(drawerState)
        },
        content = {

            Column(modifier = Modifier.background(Color.Transparent)) {
                var userNowWeek by remember { mutableStateOf( if(!startSchoolOrNot || startHolidayOrNot){0}else{viewModel.getNowWeek()}) }
                val pagerState = rememberPagerState(
                    pageCount = 19,
                    initialOffscreenLimit = 2,
                    initialPage = userNowWeek,
                    infiniteLoop = true
                )
                statusSpacer()
                ScheduleToolBar(scope, drawerState, userNowWeek, pagerState)

                HorizontalPager(
                    state = pagerState,
                    flingBehavior = PagerDefaults.defaultPagerFlingConfig(
                        state = pagerState,
                        snapAnimationSpec = spring(stiffness = 500f)
                    )
                ) { page ->
                    SingleLineClass(oneWeekClass = courseList, page = page)
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

@Composable
fun BackGroundImage(viewModel:ShowTimetableViewModel){
    val path = viewModel.backgroundUriStringLiveData.observeAsState(initial = Uri.parse(""))
    Image(
        painter = rememberGlidePainter(request = path.value), contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )

}


@ExperimentalPagerApi
@Composable
fun ScheduleToolBar(scope: CoroutineScope,
                    drawerState: DrawerState,
                    userNowWeek: Int,
                    pagerState: PagerState,
                    stViewModel: ShowTimetableViewModel = viewModel()
){
    val context = LocalContext.current
    val activity = (LocalContext.current as? Activity)
    val nowWeek = stViewModel.getNowWeek()
    val nowWeekOrNot = (pagerState.currentPage == nowWeek)
    val startSchoolOrNot = stViewModel.startSchool()
    val startHolidayOrNot = stViewModel.startHoliday()
    TopAppBar(
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        title = {
            Column(
                Modifier
                    .clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                if (startSchoolOrNot && !startHolidayOrNot) {
                                    nowWeek
                                } else {
                                    0
                                }
                            )
                        }
                    }
                    .fillMaxHeight(0.9F)
                    .onGloballyPositioned { coordinates ->
                        coordinates.positionInWindow()
                        Log.d(
                            "ShowActivity2",
                            coordinates
                                .positionInWindow()
                                .toString()
                        )
                    }
            ) {
                // 开学了，当前页面不是当前周加一个5dp的空行
                // 开学了，而且页面是当前周 显示“当前周”
                // 放假了，显示放假
                if (startSchoolOrNot && !nowWeekOrNot){
                    Spacer(modifier = Modifier.height(5.dp))
                }
                Text(text = "第${userNowWeek+1}周")
                if (nowWeekOrNot && startSchoolOrNot){
                    Text(text = "当前周", fontSize = 15.sp)
                } else if(!startSchoolOrNot) {
                    Text(text = "距离开课${-stViewModel.startSchoolDay()}天", fontSize = 15.sp, color = Color.Gray)
                }
                if (stViewModel.startHoliday()){
                    Text(text = "放假了，联系开发者更新", fontSize = 15.sp, color = Color.Gray)
                }
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
                val intent = Intent(context, AddCourseActivity::class.java)
                activity?.startActivity(intent)
            }) {
                Icon(Icons.Filled.Add, "添加课程")
            }
            IconButton(onClick = {
                val dialog = activity?.let { Dialog.getImportAgain(it) }
                dialog?.show()
                dialog?.positiveButton {
                    val intent = Intent(context, ImportCourseAgain::class.java)
                    activity.startActivity(intent)
                    Repository.importAgain()
                    activity.finish()
                }

            }) {
                Icon(Icons.Filled.GetApp, "导入课程")
            }
        },
    )
}




@Composable
fun SingleLineClass(oneWeekClass: State<List<List<OneByOneCourseBean>>?>,
                    page:Int,
                    stViewModel: ShowTimetableViewModel = viewModel()){
    val context = LocalContext.current
    Column() {
        //星期行
        Row() {
            Column(
                Modifier
                    .weight(0.6F)
                    .height(40.dp)
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "${getDayOfDate(0, page)}\n月",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 10.sp, textAlign = TextAlign.Center)
            }
                
            repeat(7){
                val textText = "${getDayOfWeek(it+1)}\n\n${getDayOfDate(it+1, page)}"
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1F, true)
                        .height(40.dp),
                    color = Color.Transparent
                ){
                    if (!isSelected(it+1, page)){
                        Text(text = textText,
                            modifier = Modifier.background(Color.Transparent),
                            fontSize = 11.sp,
                            lineHeight = 10.sp,
                            textAlign = TextAlign.Center)
                    } else {
                        Text(text = textText,
                            modifier =  Modifier
                                .background(Brush.verticalGradient(listOf(Color.Blue, Color.Transparent))),
                            fontSize = 11.sp,
                            lineHeight = 10.sp,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
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
                                    append("\n\n${it+1}")
                                }
                            }
                            withStyle(style = SpanStyle(fontSize = 10.sp)){
                                append("${getStartTime(it+1)}\n")
                                append(getEndTime(it+1))
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
                var count = 0
                val nowJieShu = IntArray(12){it+1}.toMutableList()
                Column(Modifier.weight(1F,true)) {
                    for (oneClass in oneDayClass){
                        val spacerHeight = (oneClass.start - nowJieShu[0]) * 70

                        if (spacerHeight < 0){
                            val nowClassAllName = oneClass.courseName.split("\n")
                            val nowClassName = nowClassAllName.getOrElse(0){""}
                            val nowClassBuild = nowClassAllName.getOrElse(1){""}
                            val lastClassAllName = oneDayClass[count-1].courseName.split("\n")
                            val lastClassName = lastClassAllName.getOrElse(0){""}
                            val lastClassBuild = lastClassAllName.getOrElse(1){""}
                            Log.d("TestActivity", "当前有冲突课程")
                            if (nowClassName+nowClassBuild == lastClassName+lastClassBuild){
                                stViewModel.mergeClass(
                                    nowClassName,
                                    oneClass.whichColumn,
                                    oneClass.start,
                                    oneClass.end +1 - oneClass.start,
                                    nowClassBuild
                                )
                                Toasty.info(context, "检测到《${nowClassAllName[0]}》存在多位老师，已合并。重启生效").show()
                                continue
                            } else if (nowClassName == lastClassName){
                                if (stViewModel.showToast < 5){Toasty.info(context, "检测到${nowClassAllName[0]}课程冲突，无法正常显示，请尝试登陆导入").show()}
                                stViewModel.showToast += 1
                            }
                            if (stViewModel.showToast < 5){Toasty.info(context, "检测到${nowClassAllName[0]}课程冲突，务必仔细检查").show()}
                            stViewModel.showToast += 1
                        }

                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(spacerHeight.dp))
                        key(oneClass.courseName+oneClass.start+oneClass.end+stViewModel.showToast) {
                            SingleClass2(singleClass = oneClass)
                        }
                        nowJieShu -= IntArray(oneClass.end){it+1}.toMutableList()
                        count += 1
                    }
                }
            }
        }
    }
}



@Composable
fun SingleClass2(singleClass: OneByOneCourseBean){
//    val context = LocalContext.current
//    val activity = (LocalContext.current as? Activity)
//    val interactionSource = remember { MutableInteractionSource() }
    val height = 70*(singleClass.end- singleClass.start+1)
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
        ClassDetailDialog(openDialog = showDetailDialog, singleClass = singleClass)

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


@DelicateCoroutinesApi
fun saveAllCourse(intent: Intent, activity2: ShowTimetableActivity2, viewModel: ShowTimetableViewModel){
    val dialog = MaterialDialog(activity2)
        .title(text = "保存课表至本地")
        .message(text = "正在保存请不要关闭APP....")
        .positiveButton(text = "知道了")

    GlobalScope.launch {
        if (!intent.getBooleanExtra("isSaved", false)) {
            activity2.runOnUiThread {
                dialog.show()
            }
            val allCourseListJson = intent.getStringExtra("courseList")?:""
            val allCourseList = Convert.jsonToAllCourse(allCourseListJson)
            viewModel.deleteAllCourse()
            viewModel.insertOriginalCourse(allCourseList)
            activity2.runOnUiThread {
                viewModel.loadAllCourse()
                dialog.message(text = "已保存至本地\n如果是按班级课程导入的同学请注意：" +
                        "部分情况将导致课程冲突，请务必检查！！！如无法操作，请尝试登陆导入")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestTheme {
        Text(text = "Test")
    }
}