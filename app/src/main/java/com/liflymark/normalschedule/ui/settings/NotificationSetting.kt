package com.liflymark.normalschedule.ui.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.notice.setRepeatCourseNoticeCheck
import com.liflymark.normalschedule.ui.app_wdiget_twoday.TwoDayWidgetProvider
import com.liflymark.normalschedule.ui.app_wdiget_twoday.TwoDayWidgetReceiver
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import kotlinx.coroutines.launch


@Composable
fun NotificationSettingPage(
    navController: NavController
){
    val context = LocalContext.current
    var haveWidget by rememberSaveable{ mutableStateOf(false) }
    var haveAlarmPer by rememberSaveable{ mutableStateOf(false) }
    val noticeStart = Repository.getScheduleSettingsNotice().collectAsState(initial = null)
    val triggerMillis = Repository.getCourseNoticeTimeFlow().collectAsState(initial = 600000)
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    var showEditTimeDialog by rememberSaveable { mutableStateOf(false)  }
    OnLifecycleEvent{event ->
        when(event){
            Lifecycle.Event.ON_RESUME ->{
                scope.launch {
                    // 检查是否有两日小部件
                    val manager = GlanceAppWidgetManager(context)
                    val glanceId = manager
                        .getGlanceIds(TwoDayWidgetProvider::class.java)
                        .firstOrNull()
                    haveWidget = glanceId != null

                    // 检查是否有精确提醒权限
                    val alarmManager =
                        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                    if (alarmManager != null) {
                        haveAlarmPer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            alarmManager.canScheduleExactAlarms()
                        } else {
                            true
                        }
                    }
                }
            }
            else -> { }
        }
    }
    if (showEditTimeDialog){
        OutlineTextFiledTimeDialog(
            value = (triggerMillis.value/60000).toString(),
            onDismissRequest = { showEditTimeDialog = false },
            onRes = {
                scope.launch{
                    Repository.setCourseTriggerMillis(it*60000)
                    showEditTimeDialog = false
                }
            }
        )
    }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            NormalTopBar(label = "课程提醒配置") {
                navController.navigateUp()
            }
        },
        content = { paddingValues ->

            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                NotificationDescription()
                SetTwoDayWidget(
                    haveWidgetId = haveWidget,
                    addWidgetClick = {
                        scope.launch {
                            val manager = GlanceAppWidgetManager(context)
                            val glanceId = manager
                                .getGlanceIds(TwoDayWidgetProvider::class.java)
                                .firstOrNull()
                            if (glanceId != null) {
                                TwoDayWidgetProvider().update(context, glanceId)
                                haveWidget = true
                                scaffoldState.snackbarHostState.showSnackbar("小部件已添加，不要再加啦~")
                            }else{
                                val res = manager.requestPinGlanceAppWidget(TwoDayWidgetReceiver::class.java)
                                if (res){
                                    haveWidget = true
                                    scaffoldState.snackbarHostState.showSnackbar("已尝试自动添加，如果多次点击仍无法成功，请手动添加")
//                                    haveWidget = true
                                }else{
                                    scaffoldState.snackbarHostState.showSnackbar("APP没有添加权限请手动添加")
                                }
                            }
                        }
                    }
                )
                RequestAlarm(
                    haveAlarmPer = haveAlarmPer,
                    requestAlarmClick = {
                        if (Build.VERSION.SDK_INT>=31){
                            val uri = Uri.parse("package:"+context.packageName)
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, uri)
                            context.startActivity(intent)
                        }else{
                            haveAlarmPer = true
                            scope.launch { scaffoldState.snackbarHostState.showSnackbar("该系统版本无需授权") }
                        }
                    }
                )
                RequestAutoStart{
                    checkAutoStart(context)
                }
                StartCourseNotice(
                    isStartNotice = noticeStart.value?:false,
                    triggerMinutes = triggerMillis.value/60000,
                    onCheckChange = {
                        scope.launch{
                            val needSet:Boolean = if(noticeStart.value==null){true}else{!noticeStart.value!! }
                            if (needSet){
                                if (haveWidget && haveAlarmPer) {
                                    Repository.updateSettings {
                                        it.toBuilder()
                                            .setOpenCourseNotice(true)
                                            .build()
                                    }
                                    // 更新一次小部件以开始任务，
//                                    val manager = GlanceAppWidgetManager(context)
//                                    val glanceId = manager
//                                        .getGlanceIds(TwoDayWidgetProvider::class.java)
//                                        .firstOrNull()
//                                    if (glanceId != null) {
//                                        TwoDayWidgetProvider().update(context, glanceId)
//                                    }
                                    // 强制重置一次以防当天关闭又开启的情况
                                    setRepeatCourseNoticeCheck(context, true)
                                }else{
                                    scaffoldState.snackbarHostState.showSnackbar("未完成以上步骤")
                                }
                            }else{
                                // 取消所有任务
                                val alarmManager =
                                    context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                                val repeatIntent =  Intent(context, TwoDayWidgetReceiver::class.java).apply {
                                    action = "com.fly.setThisDayCourseNotice"
                                }
                                val repeatPendingIntent =
                                    PendingIntent.getBroadcast(
                                        context, 1, repeatIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                    )
                                val courseIntent = Intent(context, TwoDayWidgetReceiver::class.java).apply {
                                    action = "com.fly.courseNotice"
                                }
                                val pendingIntent =
                                    PendingIntent.getBroadcast(
                                        context, 0, courseIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                    )
                                alarmManager?.cancel(repeatPendingIntent)
                                alarmManager?.cancel(pendingIntent)
                                Repository.updateSettings {
                                    it.toBuilder()
                                        .setOpenCourseNotice(false)
                                        .build()
                                }
                            }

                        }
                    },
                    startNoticeClick = {
                        showEditTimeDialog = true
                    }
                )
                ThankWakeUP()
            }
        }
    )
}

@Composable
fun NotificationDescription(){
    NormalStepCard{
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = MaterialTheme.colors.primary, fontSize = 15.sp, fontWeight = FontWeight.Bold)) {
                    append("重要说明：")
                }
                withStyle(style = SpanStyle(color = MaterialTheme.colors.primary, fontSize = 15.sp)) {
                    append("课程提醒是一个实验性功能，不能特别保证准确性。耗电量应该不会过高。")
                }
                withStyle(style = SpanStyle(color = MaterialTheme.colors.onSecondary, fontSize = 15.sp)) {
                    append("同时需要注意的是，提醒小概率需要半天才可正常运行。你需要完成下方的所有步骤以实现相对及时的课程提醒.")
                }
            },
            modifier = Modifier.padding(2.dp)
        )
    }
}
@Composable
fun SetTwoDayWidget(
    haveWidgetId:Boolean = false,
    addWidgetClick:() -> Unit = {}
){
   NormalStepCard{
        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.primary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("第一步：")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.onSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("添加可以显示两日课程的桌面小部件")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.onSecondary,
                        fontSize = 15.sp
                    )
                ) {
                    append("\n当前状态：")
                }
                withStyle(
                    style = SpanStyle(
                        color = primaryOrRed(onOff = haveWidgetId),
                        fontSize = 15.sp
                    )
                ) {
                    append(
                        if (haveWidgetId) {
                            "已添加"
                        } else {
                            "未添加"
                        }
                    )
                }
            },
        )
        TextButton(
            onClick = { addWidgetClick() },
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
        ) {
            Icon(imageVector = Icons.Default.SettingsApplications, contentDescription = null, modifier = Modifier.size(20.dp))
            Text(text = "点击添加", fontSize=14.sp)
        }
    }
}

@Composable
fun RequestAlarm(
    haveAlarmPer:Boolean = false,
    requestAlarmClick:() -> Unit = {}
){
    NormalStepCard{
        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.primary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("第二步：")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.onSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("开启课表精确提醒权限")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.onSecondary,
                        fontSize = 15.sp
                    )
                ) {
                    append("\n当前状态：")
                }
                withStyle(
                    style = SpanStyle(
                        color = primaryOrRed(onOff = haveAlarmPer),
                        fontSize = 15.sp
                    )
                ) {
                    append(
                        if (haveAlarmPer) {
                            "预计已开启，需要您手动检查一下"
                        } else {
                            "未开启"
                        }
                    )
                }
            },
        )
        TextButton(
            onClick = { requestAlarmClick() },
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp),
        ) {
            Icon(imageVector = Icons.Default.SettingsApplications, contentDescription = null, modifier = Modifier.size(20.dp))
            Text(text = "跳转设置", fontSize=14.sp)
        }
    }
}


@Composable
fun RequestAutoStart(
    requestAutoStartClick: () -> Unit = {}
){
    NormalStepCard{
        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.primary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("第三步：")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.onSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("开启课表APP自启动权限，关闭APP省电策略")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.onSecondary,
                        fontSize = 15.sp
                    )
                ) {
                    append("\n当前状态：")
                }
                withStyle(
                    style = SpanStyle(
                        color = primaryOrRed(onOff = true),
                        fontSize = 15.sp
                    )
                ) {
                    append(
                        "请转到设置自行检查"
                    )
                }
            },
        )
        TextButton(
            onClick = { requestAutoStartClick() },
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp),
        ) {
            Icon(imageVector = Icons.Default.SettingsApplications, contentDescription = null, modifier = Modifier.size(20.dp))
            Text(text = "跳转设置", fontSize=14.sp)
        }
    }
}

@Composable
fun StartCourseNotice(
    isStartNotice:Boolean = false,
    triggerMinutes: Long = 10,
    onCheckChange:() -> Unit = {},
    startNoticeClick:() -> Unit = {}
){
    val switcherId = "startNoticeSwitcher"
    val editButtonId = "editButton"
    NormalStepCard{
        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.primary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("第四步：")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.onSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("开启提醒开关并设置提前时间")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.onSecondary,
                        fontSize = 15.sp
                    )
                ) {
                    append("\n当前状态：")
                }
                withStyle(
                    style = SpanStyle(
                        color = primaryOrRed(onOff = isStartNotice),
                        fontSize = 15.sp
                    )
                ) {
                    append(
                        if (isStartNotice) {
                            "已开启"
                        } else {
                            "未开启"
                        }
                    )
                }
                appendInlineContent(switcherId)
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.onSecondary,
                        fontSize = 15.sp
                    )
                ) {
                    append("\n提前时间：${triggerMinutes}分钟")
                }
                appendInlineContent(editButtonId)
            },
            inlineContent = mapOf(
                Pair(
                    switcherId,
                    InlineTextContent(
                        Placeholder(
                            width = 14.em,
                            height = 3.em,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    ) {
                        Switch(checked = isStartNotice, onCheckedChange = { onCheckChange() })
                    }
                ),
                Pair(
                    editButtonId,
                    InlineTextContent(
                        Placeholder(
                            width = 10.em,
                            height = 3.em,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    ){
                        TextButton(onClick = { startNoticeClick() }, contentPadding = PaddingValues(4.dp)) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "编辑", modifier = Modifier.size(18.dp))
                            Text(text = "编辑时间", fontSize = 14.sp)
                        }
                    }
                )
            )
        )
    }
}

@Composable
fun ThankWakeUP(){
    Text(
        text = "本方案参考了WakeUp课程表的方法，特此感谢",
        modifier = Modifier.fillMaxWidth(0.8f).padding(top = 4.dp),
        textAlign = TextAlign.Center,
        fontSize = 13.sp,
        color = Color.LightGray
    )
}

@Composable
fun NormalStepCard(
    content:@Composable ColumnScope.() -> Unit
){
    Box(modifier = Modifier.padding(4.dp)){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xB7DDDDDD))
                .padding(8.5.dp)
        ) {
            content()
        }
    }
}

@Composable
fun OnLifecycleEvent(onEvent: LifecycleOwner.(event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

private fun checkAutoStart(
    context: Context
){

    val mIntent =  Intent()
    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    mIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
    mIntent.data = Uri.fromParts("package", context.packageName, null)
    context.startActivity(mIntent)
}
@Composable
private fun primaryOrRed(onOff:Boolean) =  if (onOff){MaterialTheme.colors.primary}else{Color.Red}

@Composable
fun OutlineTextFiledTimeDialog(
    value:String,
    onDismissRequest: () -> Unit,
    onRes:(Long) -> Unit,
){
    var tempValue by rememberSaveable{ mutableStateOf(value) }
    var isError by rememberSaveable{ mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = {
            Text(text = "设置提前分钟数")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()){
                OutlinedTextField(
                    value = tempValue,
                    onValueChange = {tempValue =it},
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = isError,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "范围：1~120", color = if (isError){ Color.Red }else{Color.Unspecified})
            }
        },
        buttons = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp), horizontalArrangement = Arrangement.End){
                TextButton(
                    onClick = {
                        val temp = tempValue.toLongOrNull()
                        if (temp != null && temp in 1..120) {
                            onRes(tempValue.toLong())
                        } else {
                            isError = true
                        }
                    }
                ) {
                    Text(text = "确认")
                }
            }
        },
    )
}

//@Composable
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
//    showSystemUi = true
//)
//fun NoticeSettingPreview(){
//    NorScTheme{
//        Column{
//            NormalTopBar(label = "课程提醒配置") {}
//            NotificationDescription()
//            SetTwoDayWidget(true)
//            RequestAlarm()
//            RequestAutoStart()
//            StartCourseNotice()
//        }
//    }
//}
//
//@Composable
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
//    showSystemUi = true, fontScale = 1.0f
//)
//fun NoticeSettingPreviewLight(){
//    NorScTheme{
//
//        Column{
//            NormalTopBar(label = "课程提醒配置") {}
//            NotificationDescription()
//            SetTwoDayWidget()
//            RequestAlarm()
//            RequestAutoStart()
//            StartCourseNotice(true)
//        }
//    }
//}