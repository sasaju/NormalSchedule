package com.liflymark.normalschedule.ui.show_timetable

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.google.accompanist.glide.rememberGlidePainter
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.gyf.immersionbar.ImmersionBar
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.bean.UserBackgroundBean
import com.liflymark.normalschedule.logic.bean.getData
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.Dialog
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import com.liflymark.normalschedule.ui.add_course.AddCourseActivity
import com.liflymark.normalschedule.ui.import_again.ImportCourseAgain
import com.liflymark.normalschedule.ui.import_show_score.ImportScoreActivity
import com.liflymark.test.ui.theme.TestTheme
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShowTimetableActivity2 : ComponentActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(ShowTimetableViewModel::class.java) }
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

                // A surface container using the 'background' color from the theme
                ProvideWindowInsets() {
                    Column() {
                        Spacer(
                            Modifier
                                .background(Color.Transparent)
                                .statusBarsHeight() // Match the height of the status bar
                                .fillMaxWidth()
                        )
                        Drawer(viewModel)
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

@ExperimentalPagerApi
@Composable
fun Drawer(viewModel: ShowTimetableViewModel){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

//    SideEffect {
//        // Update all of the system bar colors to be transparent, and use
//        // dark icons if we're in light theme
//        systemUiController.setSystemBarsColor(
//            color = Color.Transparent,
//            darkIcons = useDarkIcons
//        )
//
//        // setStatusBarsColor() and setNavigationBarsColor() also exist
//    }
//    val courseList: State<List<List<OneByOneCourseBean>>?> =
//        viewModel.courseDatabaseLiveDataVal.observeAsState(getNeededClassList(getData()))
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
                var userNowWeek by remember { mutableStateOf(viewModel.getNowWeek()) }
                val pagerState = rememberPagerState(
                    pageCount = 19,
                    initialOffscreenLimit = 2,
                    initialPage = userNowWeek
                )
                ScheduleToolBar(scope, drawerState, userNowWeek, pagerState)

                HorizontalPager(state = pagerState) { page ->
                    SingleLineClass(oneWeekClass = courseList, page = page, viewModel = viewModel)
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

//    viewModel.loadAllCourse()
//    if (path.value?.userBackground == "0"){
//        Image(
//            painter = rememberGlidePainter(request = R.drawable.main_background_4), contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.FillBounds
//        )
//        Log.d("BackgroundImage", path.toString())
//    } else{
//        Log.d("BackgroundImage", path.toString())
//        Image(
//            painter = rememberGlidePainter(request = getPath(path.value)), contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.FillBounds
//        )
//    }

//    if(path.value == null) {
//        Image(
//            painter = rememberGlidePainter(request = R.drawable.main_background_4), contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.FillBounds
//        )
//    } else {
//            Image(
//                painter = rememberGlidePainter(request = Uri.parse(path.value as String?)), contentDescription = null,
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.FillBounds
//            )
//    }
}


@ExperimentalPagerApi
@Composable
fun ScheduleToolBar(scope: CoroutineScope, drawerState: DrawerState, userNowWeek: Int, pagerState: PagerState){
    val context = LocalContext.current
    val activity = (LocalContext.current as? Activity)
    TopAppBar(
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        title = {
            Column(
                Modifier
                    .clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(GetDataUtil.whichWeekNow(GetDataUtil.getFirstWeekMondayDate()) - 1)
                        }
                    }
                    .fillMaxHeight(0.9F)) {
                val nowWeekOrNot = GetDataUtil.whichWeekNow(GetDataUtil.getFirstWeekMondayDate())==userNowWeek+1
                if (!nowWeekOrNot){
                    Spacer(modifier = Modifier.height(5.dp))
                }
                Text(text = "第${userNowWeek+1}周")
                if (nowWeekOrNot){
                    Text(text = "当前周", fontSize = 15.sp)
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
                    Repository.clearSharePreference()
                    activity.finish()
                }

            }) {
                Icon(Icons.Filled.GetApp, "导入课程")
            }
        },
    )
}



@DelicateCoroutinesApi
@Composable
fun SingleLineClass(oneWeekClass: State<List<List<OneByOneCourseBean>>?>, page:Int, viewModel: ShowTimetableViewModel){
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
                        SingleClass2(singleClass = oneClass, viewModel = viewModel)
                        nowJieShu -= IntArray(oneClass.end){it+1}.toMutableList()
                    }
                }
            }
        }
    }

}



@DelicateCoroutinesApi
@Composable
fun SingleClass2(singleClass: OneByOneCourseBean, viewModel: ShowTimetableViewModel){
    val context = LocalContext.current
    val activity = (LocalContext.current as? Activity)
    val interactionSource = remember { MutableInteractionSource() }
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
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            if (activity != null) {
                                showDeleteDialog(activity as ShowTimetableActivity2, singleClass, viewModel)
                            }
                        },
                        onTap = {
                            if (activity != null) {
                                showDialog(activity, singleClass)
                            }
                        }
                    )
                },
            textAlign = TextAlign.Center
        )
    }
}


@DelicateCoroutinesApi
fun showDialog(context: Activity, singleClass: OneByOneCourseBean) {
//    MaterialDialog(context).show {
//        title(text = "张子龙铁憨憨")
//        message(text = "张子龙大笨蛋")
//        positiveButton(text = "是的")
//    }
    val realCourseMessage = singleClass.courseName.split("\n")
    GlobalScope.launch{
        val courseBeanList = Repository.loadCourseByNameAndStart(
            realCourseMessage[0],
            singleClass.start,
            singleClass.whichColumn
        )
        context.runOnUiThread {
            val dialog = Dialog.getClassDetailDialog(
                context,
                courseBeanList!![0]
            )
            dialog.show()
        }
    }

}

fun showDeleteDialog(context: ShowTimetableActivity2, singleClass: OneByOneCourseBean, viewModel: ShowTimetableViewModel){
    val realCourseName = singleClass.courseName.split("\n")[0]
    viewModel.deleteCourseBeanByNameLiveData.observe(context, Observer {
        if (it){
            viewModel.loadAllCourse()
        } else {
            Toasty.error(context, "删除操作异常").show()
        }
    })
    val dialog = MaterialDialog(context)
        .title(text = "你在进行一步敏感操作")
        .message(text = "你将删除《${realCourseName}》的所有课程\n无法恢复，务必谨慎删除！！！\n如失误删除请重新导入")
        .positiveButton(text = "仍然删除") { _ ->
            viewModel.deleteCourse(realCourseName)
//            viewModel.updateCourse()
            Toasty.success(context, "删除成功").show()
        }
        .negativeButton(text = "取消") { _ ->
            Toasty.info(context, "删除操作取消", Toasty.LENGTH_SHORT).show()
        }
        .cancelOnTouchOutside(false)
    dialog.show()
    return
}

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
            val user = intent.getStringExtra("user")?:""
            val password = intent.getStringExtra("password")?:""
            viewModel.deleteAllCourse()
            viewModel.insertOriginalCourse(allCourseList)
            activity2.runOnUiThread {
                dialog.message(text = "已保存至本地")
            }
        }
    }
}

@ExperimentalPagerApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestTheme {

    }
}