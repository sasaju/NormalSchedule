package com.liflymark.normalschedule.ui.settings

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.utils.SingleSelectDialog
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import com.liflymark.schedule.data.Settings
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContent {
            SettingsAll()
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "上课提醒"
            val descriptionText = "用于发送上课提醒"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("COURSE_NOTICE", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}

@Composable
fun SettingsAll() {
    NorScTheme {
        UiControl()
        SettingsNavHost()
    }
}

@Composable
fun SettingsMainPage(
    navController: NavController
) {
    Scaffold(
        topBar = {
            NormalTopBar(label = "设置")
        },
        content = {
            it
            SettingsContent(navController = navController)
        }
    )
}

@Composable
fun SettingsContent(
    navController: NavController
) {
    val context = LocalContext.current
    var nowColor by remember {
        mutableStateOf("主题")
    }
    val settings = Repository.getScheduleSettings().collectAsState(
        initial = Settings.getDefaultInstance()
    )
    var perHeight by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    var showDarkBack by remember { mutableStateOf(settings.value.darkShowBack) }
    var showHomeWorkIcon by remember { mutableStateOf("") }

    LaunchedEffect(settings.value) {
        nowColor =
            if (settings.value.colorMode == 0) {
                "纯色主题"
            } else {
                "渐变色主题"
            }
        perHeight =
            if (settings.value.coursePerHeight == 0) {
                70
            } else {
                settings.value.coursePerHeight
            }
        showDarkBack = settings.value.darkShowBack
        showHomeWorkIcon = if (settings.value.showRight != 0) {
            "显示标识"
        } else {
            "不显示标识"
        }
    }

    val showNorSetDialog = remember { mutableStateOf(false) }
    var selectInit by remember { mutableStateOf(0) }
    var selectList by remember { mutableStateOf(listOf("")) }
    var dialogResult by remember { mutableStateOf("") }
    if (showNorSetDialog.value) {
        SingleSelectDialog(
            modifier = Modifier.fillMaxWidth(),
            showDialog = showNorSetDialog,
            selectNumInit = selectInit,
            selectList = selectList,
            result = {
                dialogResult = it
            }
        )
    }

    LaunchedEffect(dialogResult) {
        when (dialogResult) {
            "渐变色主题" -> {
                scope.launch {
                    Repository.updateMode(1)
                    nowColor = "渐变色主题"
                }
            }
            "纯色主题" -> {
                scope.launch {
                    Repository.updateMode(0)
                    nowColor = "纯色主题"
                }
            }
            "显示" -> {
                scope.launch {
                    Repository.updateShowDarkBack(true)
                    showDarkBack = true
                }
            }
            "不显示" -> {
                scope.launch {
                    Repository.updateShowDarkBack(false)
                    showDarkBack = false
                }
            }
            "显示标识" -> {
                scope.launch {
                    Repository.updateSettings {
                        it.toBuilder()
                            .setShowRight(1)
                            .build()
                    }
                    showDarkBack = false
                }
            }
            "不显示标识" -> {
                scope.launch {
                    Repository.updateSettings {
                        it.toBuilder()
                            .setShowRight(0)
                            .build()
                    }
                    showDarkBack = false
                }
            }
            else -> {}
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SettingsPart(
            label = "常规"
        ) {
            SettingsItem(
                title = "课程格子主题",
                description = nowColor
            ) {
                selectInit = if (nowColor == "纯色主题") {
                    0
                } else {
                    1
                }
                selectList = listOf("纯色主题", "渐变色主题")
                showNorSetDialog.value = true
            }
            SettingsItem(
                title = "课程格子及其他外观配置",
                description = "更细致的配置项"
            ) {
                navController.navigate("courseCardSettings")
            }
            SettingsItem(
                title = "夜间模式显示背景图",
                description = if (showDarkBack) {
                    "显示"
                } else {
                    "不显示"
                }
            ) {
                selectInit = if (showDarkBack) (0) else {
                    1
                }
                selectList = listOf("显示", "不显示")
                showNorSetDialog.value = true
            }
            SettingsItem(
                title = "有作业时右下角显示标识",
                description = showHomeWorkIcon
            ) {
                selectInit = if (showDarkBack) (0) else {
                    1
                }
                selectList = listOf("显示标识", "不显示标识")
                showNorSetDialog.value = true
            }
            //        SettingsItem(
            //            title = "时间列字体颜色",
            //            description = "暂时只支持黑白色设置"
            //        ) {
            //
            //        }
        }
        Spacer(modifier = Modifier.height(10.dp))
        SettingsPart(label = "课程提醒通知") {
            SettingsItem(
                title = "配置课程提醒通知",
                description = "进行一些额外的配置，实现课前提醒通知"
            ) {
              navController.navigate("notificationSettings")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        SettingsPart(label = "帮助") {
            SettingsItem(title = "重新查看帮助", description = "重新打开时，将在主界面重新查看帮助") {
                scope.launch {
                    Repository.saveUserVersion(3)
                    (context as Activity).finish()
                }
            }
            SettingsItem(title = "加入反馈群", description = "欢迎加入反馈群") {
                Repository.joinQQGroup(context)
            }
        }
    }
}

@Composable
fun SettingsPart(
    label: String,
    content: @Composable () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = Color.LightGray,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body1,
            color = Color.Gray,
            modifier = Modifier
                .padding(
                    top = 10.dp,
                    start = 10.dp,
                    end = 2.dp,
                    bottom = 3.dp
                )
        )
        content()
    }
}

@Composable
fun SettingsItem(
    title: String,
    description: String,
    onCLick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable {
                onCLick()
            }
            .padding(start = 10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            buildAnnotatedString {
                withStyle(
                    style =
                    SpanStyle(
                        fontSize = MaterialTheme.typography.body1.fontSize,
                        color = Color.Black
                    )
                ) {
                    append(title)
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = MaterialTheme.typography.body1.fontSize,
                        color = Color.Gray
                    )
                ) {
                    append("\n$description")
                }
            }

        )
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    NorScTheme {
        SettingsPart(
            label = "常规"
        ) {
            SettingsItem(
                title = "课程格子主题",
                description = "渐变色主题"
            ) {

            }
            SettingsItem(
                title = "课程格子高度",
                description = "高度：70dp"
            ) {

            }
            SettingsItem(
                title = "夜间模式显示背景图",
                description = "显示"
            ) {

            }
        }
    }
}