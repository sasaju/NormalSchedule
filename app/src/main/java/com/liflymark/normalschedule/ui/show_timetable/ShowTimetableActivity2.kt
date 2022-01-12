package com.liflymark.normalschedule.ui.show_timetable

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import coil.annotation.ExperimentalCoilApi
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import com.afollestad.materialdialogs.MaterialDialog
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.pager.*
import com.gyf.immersionbar.ImmersionBar
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.bean.getData
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.Dialog
import com.liflymark.normalschedule.logic.utils.GifLoader
import com.liflymark.normalschedule.logic.utils.TutorialOverlay
import com.liflymark.normalschedule.ui.abase.BaseComment
import com.liflymark.normalschedule.ui.add_course.AddCourseComposeActivity
import com.liflymark.normalschedule.ui.import_again.ImportCourseAgain
import com.liflymark.normalschedule.ui.theme.NorScTheme
import com.liflymark.schedule.data.Settings
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShowTimetableActivity2 : BaseComment() {
    private val viewModel by lazy { ViewModelProvider(this).get(ShowTimetableViewModel::class.java) }


    @OptIn(
        ExperimentalAnimationApi::class,
        ExperimentalMaterialApi::class,
        ExperimentalPagerApi::class, DelicateCoroutinesApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .fitsSystemWindows(false)
            .statusBarDarkFont(true)
            .navigationBarColor(R.color.white)
            .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
            .init()
        saveAllCourse(intent, this, viewModel)
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            NorScTheme {
                BackGroundImage(viewModel = viewModel)
                ProvideWindowInsets {
                    Column {
                        Drawer(viewModel) {
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

    override fun getResources(): Resources {
        return super.getResources()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAllCourse()
        viewModel.setBackground()
    }
}

@OptIn(ExperimentalSnapperApi::class)
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun Drawer(
    viewModel: ShowTimetableViewModel,
    statusSpacer: @Composable () -> Unit
) {
    val settings = viewModel.settingsLiveData.observeAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val startSchoolOrNot = viewModel.startSchool()
    val startHolidayOrNot = viewModel.startHoliday()
    val courseList: State<List<List<OneByOneCourseBean>>?> =
        viewModel.courseDatabaseLiveDataVal.observeAsState(getNeededClassList(getData()))
    val newUserOrNot =
        viewModel.newUserFLow.collectAsState(initial = false)
    val quickJumpShow = remember { mutableStateOf(false) }

    // 修复一次bug
//    LaunchedEffect(newUserVersion.value){
//        val allCourse = Repository.loadAllCourseNameNoFlow().toSet()
//        val needCourse = setOf("成本管理会计", "无机化学I(上)", "仪器分析", "学前儿童科学教育", "矩阵论", "数据结构课程设计", "最优化方法实验", "Java程序设计课程设计", "操作系统课程设计", "数据库原理课程设计", "分布式计算框架课程设计", "优化理论及方法实验", "论文写作实践", "二外日语2", "二外法语2", "美国文学选读（文学）", "中药学", "声乐主修7", "声乐主修7", "器乐主修7", "器乐主 修7", "器乐主修7", "篆刻学", "动画透视学原理", "素描2（人体结构）", "声乐主修1", "声乐主修3", "声乐主修3", "声乐主修5", "声乐主修5", "声乐主修5", "声乐主修5", "器乐主修3", "器乐主修5", "器乐主修5", "器乐主修5", "室内陈设设计", "小楷技法", "大学计算机B", "大学计算机基础B", "大学计算机基础B", "大学计算机基础B", "大学计算机基础B", "大学计算机基础C", "大学计算机基础C", "形势与政策7", "形势与政策7", "形势与政策7", "形势与政策7", "形势与政策7", "形势与政策7", "形势与政策7", "形势与政策7", "形势与政策7", "形势与政策7", "形势与政策7", "形势与政策7")
//        if (newUserVersion.value == 1 && (allCourse intersect needCourse).isNotEmpty()){
//            Toasty.info(activity,"由于版本更新，需要重新导入一次").show()
//            val intent = Intent(activity, ImportCourseAgain::class.java)
//            activity.startActivity(intent)
//            Repository.importAgain()
//            Repository.saveUserVersion(0)
//            activity.finish()
//        }
//    }
    // 拦截返回键请求
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher
    val backCallback = remember {
        object : OnBackPressedCallback(!drawerState.isClosed) {
            override fun handleOnBackPressed() {
                scope.launch {
                    drawerState.close()
                }
            }
        }
    }
    LaunchedEffect(drawerState.isClosed){
        backCallback.isEnabled = !drawerState.isClosed
    }
    DisposableEffect(backDispatcher) {
        // Add callback to the backDispatcher
        backDispatcher.addCallback(backCallback)

        // When the effect leaves the Composition, remove the callback
        onDispose {
            backCallback.remove()
        }
    }
    
    var singleClass by remember {
           mutableStateOf(getData()[0])
    }
    val showDetailDialog = remember { mutableStateOf(false) }
    ClassDetailDialog(openDialog = showDetailDialog, singleClass = singleClass)

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerNavHost(drawerState)
        },
        content = {
            Column(modifier = Modifier.background(Color.Transparent)) {
                val initialWeek = if (!startSchoolOrNot || startHolidayOrNot) {
                    0
                } else {
                    viewModel.getNowWeek()
                }
                val allWeek = 19
                var userNowWeek by remember { mutableStateOf(initialWeek) }
                val pagerState = rememberPagerState(initialPage = userNowWeek)
                statusSpacer()

                TutorialOverlay(
                    rememberCoroutineScope(),
                    "点击此处快速跳转当前周",
                    newUserOrNot.value
                ) { overMod ->
                    settings.value?.let {
                        ScheduleToolBar(
                            scope,
                            drawerState,
                            userNowWeek,
                            pagerState,
                            overMod,
                            it,
                            quickJumpClick = {quickJumpShow.value = !quickJumpShow.value}
                        )
                    }
                }
                if (quickJumpShow.value) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .horizontalScroll(
                            rememberScrollState()
                        )) {
                        for (week in 0 until allWeek){
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(week)
                                    }
                                },
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Text("第${week+1}周", color = MaterialTheme.colors.onBackground)
                            }
                        }
                    }
                }

                // 鉴于Pager-0.19.0改版后Pager实现循环的复杂度，暂不实现循环
                HorizontalPager(
                    state = pagerState,
                    count = allWeek,
                    flingBehavior = PagerDefaults.flingBehavior(
                        state = pagerState,
                        snapAnimationSpec = spring(
                            visibilityThreshold = 1f,
                        ),
                    ),
                ) { index ->
                    val page= (index - initialWeek).floorMod(allWeek)
                    settings.value?.let {
                        SingleLineClass(
                            oneWeekClass = courseList,
                            page = page,
                            settings = it
                        ){ oneBean ->
                            singleClass = oneBean
                            showDetailDialog.value = true
                        }
                    }
                }

                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collectLatest { page ->
                        userNowWeek = page.floorMod(allWeek)
                    }
                }
            }
        }
    )
}

@Composable
@OptIn(ExperimentalCoilApi::class)
fun BackGroundImage(viewModel: ShowTimetableViewModel) {
    val path = viewModel.backgroundUriStringLiveData.observeAsState()
    val showDarkBack = Repository.getShowDarkBack().collectAsState(initial = false)
    val context = LocalContext.current
    if (!isSystemInDarkTheme() || showDarkBack.value) {
//        Image(
//            painter = rememberImagePainter(
//                data = path.value,
//                builder = {
//                    this.error(R.drawable.main_background_4)
//                }
//            ),
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )

        CompositionLocalProvider(LocalImageLoader provides GifLoader(context)) {
            Image(
                painter = rememberImagePainter(
                    data = path.value,
                    builder = {
                        this.error(R.drawable.main_background_4)
                    }
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }else{
        Image(
            painter = rememberImagePainter(data = ""),
            contentDescription = null,
            modifier = Modifier
                .background(Color(1, 86, 127))
                .fillMaxSize()
        )
    }
}


@ExperimentalPagerApi
@Composable
fun ScheduleToolBar(
    scope: CoroutineScope,
    drawerState: DrawerState,
    userNowWeek: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    settings: Settings,
    quickJumpClick:() -> Unit = {},
    stViewModel: ShowTimetableViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = (LocalContext.current as? Activity)
    val nowWeek = stViewModel.getNowWeek()
    val nowWeekOrNot = (pagerState.currentPage == nowWeek)
    val startSchoolOrNot = stViewModel.startSchool()
    val startHolidayOrNot = stViewModel.startHoliday()
    val iconColor =
        if (settings.darkShowBack){
            LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
        }else{
            MaterialTheme.colors.onBackground
        }
    TopAppBar(
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        title = {
            Column(
                modifier
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
            ) {
                // 开学了，当前页面不是当前周加一个5dp的空行
                // 开学了，而且页面是当前周 显示“当前周”
                // 放假了，显示放假
                if (startSchoolOrNot && !nowWeekOrNot) {
                Spacer(modifier = Modifier.height(5.dp))
            }
                Row {
                    Text(text = "第${userNowWeek + 1}周")
                    IconButton(onClick = { quickJumpClick() }, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.ExpandMore, null)
                    }
                }
                if (nowWeekOrNot && startSchoolOrNot) {
                    Text(text = "当前周", fontSize = 15.sp)
                } else if (!startSchoolOrNot) {
                    Text(
                        text = "距离开课${-stViewModel.startSchoolDay()}天",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }
                if (stViewModel.startHoliday()) {
                    Text(text = "放假了，联系开发者更新", fontSize = 15.sp, color = Color.Gray)
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                },
            ) {
                Icon(Icons.Filled.Menu, null, tint = iconColor)
            }
        },
        actions = {
            IconButton(onClick = {
                val intent = Intent(context, AddCourseComposeActivity()::class.java)
                context.startActivity(intent)
            }) {
                Icon(Icons.Filled.Add, "添加课程", tint = iconColor)
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
                Icon(Icons.Filled.GetApp, "导入课程", tint = iconColor)
            }
        },
    )
}


@Composable
fun SingleLineClass(
    oneWeekClass: State<List<List<OneByOneCourseBean>>?>,
    page: Int,
    settings: Settings,
    stViewModel: ShowTimetableViewModel = viewModel(),
    courseClick:(oneByOne:OneByOneCourseBean) -> Unit
) {
    val context = LocalContext.current
    val perHeight = if (settings.coursePerHeight==0){70}else{settings.coursePerHeight}
    val mode = settings.colorMode
    val iconColor = if (!settings.darkShowBack){ MaterialTheme.colors.onBackground } else{ Color.Black }
    val snackbarVisibleState = remember { mutableStateOf(false) }
    val snackbarVisibleShowState = remember { mutableStateOf(true) }
    var snackbarText by remember { mutableStateOf("") }
    Column {
        // snackbar
        if (snackbarVisibleState.value && snackbarVisibleShowState.value) {
            Snackbar(
                action = {
                    TextButton(onClick = {
                        snackbarVisibleState.value = false
                        snackbarVisibleShowState.value = false
                        Toasty.error(context,"注意可能存在其他课程冲突,解决冲突后将不再提示！！！").show()
                    }) {
                        Text("关闭提示")
                    }
                },
                modifier = Modifier
                    .padding(4.dp)
                    .alpha(0.8f)
            ) { Text(text = snackbarText) }
        }
        //星期行
        Row {
            Column(
                Modifier
                    .weight(0.6F)
                    .height(40.dp)
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "${getDayOfDate(0, page)}\n月",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 10.sp, textAlign = TextAlign.Center
                )
            }

            repeat(7) {
                val textText = "${getDayOfWeek(it + 1)}\n\n${getDayOfDate(it + 1, page)}"
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1F, true)
                        .height(40.dp),
                    color = Color.Transparent
                ) {
                    if (!isSelected(it + 1, page)) {
                        Text(
                            text = textText,
                            modifier = Modifier.background(Color.Transparent),
                            fontSize = 11.sp,
                            lineHeight = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = textText,
                            modifier = Modifier
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Blue,
                                            Color.Transparent
                                        )
                                    )
                                ),
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
                            withStyle(style = ParagraphStyle(lineHeight = 6.sp)) {
                                withStyle(
                                    style = SpanStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = iconColor
                                    )
                                ) {
                                    append("\n\n${it + 1}")
                                }
                            }
                            withStyle(style = SpanStyle(fontSize = 10.sp, color = iconColor)) {
                                append("${getStartTime(it + 1)}\n")
                                append(getEndTime(it + 1))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(perHeight.dp),
                        textAlign = TextAlign.Center,

                        )
                }
            }

            // 课程
            val realOneWeekList =
                getNeededClassList(oneWeekClass.value!!.getOrElse(page) { getData() })


            for (oneDayClass in realOneWeekList) {
                var count = 0
                val nowJieShu = IntArray(12) { it + 1 }.toMutableList()
                Column(Modifier.weight(1F, true)) {
                    for (oneClass in oneDayClass) {
                        val spacerHeight = (oneClass.start - nowJieShu[0]) * perHeight

                        if (spacerHeight < 0) {
                            val nowClassAllName = oneClass.courseName.split("\n")
                            val nowClassName = nowClassAllName.getOrElse(0) { "" }
                            val nowClassBuild = nowClassAllName.getOrElse(1) { "" }
                            val lastClassAllName = oneDayClass[count - 1].courseName.split("\n")
                            val lastClassName = lastClassAllName.getOrElse(0) { "" }
                            val lastClassBuild = lastClassAllName.getOrElse(1) { "" }
                            if (nowClassName + nowClassBuild == lastClassName + lastClassBuild) {
                                stViewModel.mergeClass(
                                    nowClassName,
                                    oneClass.whichColumn,
                                    oneClass.start,
                                    oneClass.end + 1 - oneClass.start,
                                    nowClassBuild
                                )
//                                Toasty.info(context, "检测到《${nowClassAllName[0]}》存在多位老师，已合并。重启生效")
//                                    .show()
                                snackbarText = "检测到《${nowClassAllName[0]}》存在多位老师，已合并。重启生效"
                                snackbarVisibleState.value = true
                                continue
                            } else if (nowClassName == lastClassName) {
                                snackbarText = "检测到${nowClassAllName[0]}课程冲突，无法正常显示，请尝试登陆导入"
                                snackbarVisibleState.value = true
                                stViewModel.showToast += 1
                            } else {
                                snackbarText = "检测到${nowClassAllName[0]}课程冲突，务必仔细检查"
                                snackbarVisibleState.value = true
                                key(oneClass.courseName + oneClass.start + oneClass.end + stViewModel.showToast) {
                                    SingleClass2(
                                        singleClass = oneClass,
                                        settings = settings,
                                        conflict = true
                                    ) {
                                        courseClick(it)
                                    }
                                }
                                stViewModel.showToast += 1
                                continue
                            }
                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(spacerHeight.dp)
                        )
                        key(oneClass.courseName + oneClass.start + oneClass.end + stViewModel.showToast) {
                            SingleClass2(
                                singleClass = oneClass,
                                settings = settings
                            ){
                                courseClick(it)
                            }
                        }
                        nowJieShu -= IntArray(oneClass.end) { it + 1 }.toMutableList()
                        count += 1
                    }
                }
            }
        }
    }
}

/** 单个课程格子
 * @param singleClass 课程
 * @param perHeight 课程格子高度
 * @param mode 0-纯色 1-渐变色
 * @param conflict 是否与其他课程冲突
 * @param courseAlpha 课程格子透明度
 * @param bottomRight 是否显示作业本标记
 * @param borderWidth 边框宽度
 * @param borderAlpha 边框透明度
 * @param loadWork 是否加载右下角作业本标记-一般在设置页面关闭，以减轻读取负担
 * @param courseClick 课程格子单击回调
 */
@Composable
fun SingleClass2(
    singleClass: OneByOneCourseBean,
    conflict:Boolean = false,
    loadWork:Boolean = true,
    settings: Settings = Settings.getDefaultInstance(),
    courseClick:(oneByOne:OneByOneCourseBean) -> Unit,
) {
//    val context = LocalContext.current
//    val activity = (LocalContext.current as? Activity)
//    val interactionSource = remember { MutableInteractionSource() }
    val perHeight = settings.coursePerHeight
    val mode = settings.colorMode
    val bottomRight = settings.showRight
    val borderWidth = settings.courseBorderSize
    val borderAlpha = settings.courseBorderAlpha
    val courseNameSize = settings.courseNameFontSize
    val courseTeacherSize = settings.courseTeacherFontSize
    val courseAlpha = settings.courseCardAlpha
    val height =
        if (!conflict){
            perHeight * (singleClass.end - singleClass.start + 1)
        }else{
            (perHeight * (singleClass.end - singleClass.start + 1) *0.6).toInt()
        }
    val nameList = singleClass.courseName.split("\n")
    val workNameList =
        if (loadWork) { Repository
        .loadUnFinishCourseName()
        .collectAsState(initial = listOf("正在查询")) }else{ remember { mutableStateOf(listOf("正在查询")) }}
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp),
        contentAlignment = Alignment.BottomEnd
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp)
                .padding(0.95.dp) // 外边距
                .alpha(courseAlpha),
//            elevation = 1.dp, // 设置阴影
            border = BorderStroke(
                width = borderWidth.dp,
                color = Color(red = 255, green = 255, blue = 255, alpha = borderAlpha)
            )
        ) {

            //        val showDetailDialog = remember { mutableStateOf(false) }
            //        ClassDetailDialog(openDialog = showDetailDialog, singleClass = singleClass)
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.W600,
                            color = Color.White,
                            fontSize = courseNameSize.sp
                        )
                    ) {
                        append(nameList[0] + "\n" + nameList[1] + "\n\n")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.W600,
                            color = Color.White,
                            fontSize = courseTeacherSize.sp
                        )
                    ) {
                        append(nameList[2])
                    }
                },
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            Convert.stringToBrush(
                                singleClass.color.value,
                                mode = mode
                            )
                        )
                    )
                    .padding(horizontal = (borderWidth+0.95).dp, vertical = borderWidth.dp)
                    .clickable {
                        //                    showDetailDialog.value = true
                        courseClick(singleClass)
                    },
                textAlign = TextAlign.Center

            )
        }
        if (bottomRight != 0 && (nameList[0].replace("?", "") in workNameList.value)) {
            IconButton(
                onClick = { },
                modifier = Modifier
                    .alpha(0.5f)
                    .offset(x = (-5).dp)
                    .size(20.dp)
            ) {
                Icon(
                    Icons.Default.Bookmark,
                    null,
                    tint = Color.White
                )
            }
        }
    }
}


@DelicateCoroutinesApi
fun saveAllCourse(
    intent: Intent,
    activity2: ShowTimetableActivity2,
    viewModel: ShowTimetableViewModel
) {
    val dialog = MaterialDialog(activity2)
        .title(text = "保存课表至本地")
        .message(text = "正在保存请不要关闭APP....")
        .positiveButton(text = "知道了")

    GlobalScope.launch {
        if (!intent.getBooleanExtra("isSaved", false)) {
            activity2.runOnUiThread {
                dialog.show()
            }
            val allCourseListJson = intent.getStringExtra("courseList") ?: ""
            val allCourseList = Convert.jsonToAllCourse(allCourseListJson)
            viewModel.deleteAllCourse()
            viewModel.insertOriginalCourse(allCourseList)
            activity2.runOnUiThread {
                viewModel.loadAllCourse()
                dialog.message(
                    text = "已保存至本地,请务必检查课表是否完整和全面\n如果是按班级课程导入的同学请注意：" +
                            "部分情况将导致课程冲突，请务必检查！！！如无法操作，请尝试登陆导入"
                )
            }
            updateWidget(context=activity2)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NorScTheme {
        val singleClass = OneByOneCourseBean(
            "药物化学\n张三\n九教808",
            start = 1,
            end = 2,
            whichColumn = 1,
            color = Color.Gray
        )
        val height = 70 * (singleClass.end - singleClass.start + 1)
        Box(
            modifier = Modifier.fillMaxWidth()
        ){
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(height.dp)
                    .padding(2.dp) // 外边距
                    .alpha(0.75F),
                elevation = 1.dp, // 设置阴影
            ) {
                val nameList = singleClass.courseName.split("\n")

                val showDetailDialog = remember { mutableStateOf(false) }

                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.W600,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        ) {
                            append(nameList[0] + "\n" + nameList[1] + "\n\n")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.W600,
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        ) {
                            append(nameList[2])
                        }
                    },
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFFC354C), Color(0xFF0ABFBC)
                                )
                            )
                        )
                        .clickable {
                            showDetailDialog.value = true
                        },
                    textAlign = TextAlign.Center
                )
            }
            Icon(
                Icons.Default.Bookmark,
                null,
                modifier = Modifier
                    .offset(y = (-20).dp)
                    .size(10.dp)
            )
        }
    }
}