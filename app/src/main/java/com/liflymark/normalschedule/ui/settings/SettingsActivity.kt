package com.liflymark.normalschedule.ui.settings

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.afollestad.materialdialogs.MaterialDialog
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.utils.SingleSelectDialog
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import com.liflymark.schedule.data.Settings
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsAll()
        }
    }
}

@Composable
fun SettingsAll() {
    NorScTheme {
        UiControl()
        Scaffold(
            topBar = {
                NormalTopBar(label = "设置")
            },
            content = {
                SettingsContent()
            }
        )
    }
}

@Composable
fun SettingsContent(){
    val context = LocalContext.current
    var nowColor by remember {
        mutableStateOf("主题")
    }
    val settings = Repository.getScheduleSettings().collectAsState(
        initial = Settings.getDefaultInstance()
    )
    var perHeight by remember {
        mutableStateOf(0)
    }
    val scope = rememberCoroutineScope()
    var showDarkBack by remember { mutableStateOf(settings.value.darkShowBack) }
    Log.d("Settings", showDarkBack.toString())
    LaunchedEffect(settings.value){
        nowColor =
            if (settings.value.colorMode == 0){ "纯色主题" } else { "渐变色主题" }
        perHeight =
            if (settings.value.coursePerHeight==0){ 70 } else{ settings.value.coursePerHeight }
        showDarkBack = settings.value.darkShowBack
    }

    val showNorSetDialog = remember { mutableStateOf(false) }
    var selectInit by remember { mutableStateOf(0) }
    var selectList by remember { mutableStateOf(listOf("")) }
    var dialogResult by remember { mutableStateOf("") }
    if (showNorSetDialog.value){
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

    LaunchedEffect(dialogResult){
        when(dialogResult){
            "渐变色主题" ->{
                scope.launch {
                    Repository.updateMode(1)
                    nowColor = "渐变色主题"
                }
            }
            "纯色主题" ->{
            scope.launch {
                    Repository.updateMode(0)
                    nowColor = "纯色主题"
                }
            }
            "显示"->{
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
            else -> {}
        }
    }

    SettingsPart(
        label = "常规"
    ) {
        SettingsItem(
            title = "课程格子主题",
            description = nowColor
        ) {
            selectInit = if (nowColor == "纯色主题"){0}else{1}
            selectList = listOf("纯色主题", "渐变色主题")
            showNorSetDialog.value = true
        }
        SettingsItem(
            title = "课程格子高度",
            description = "高度：${perHeight}dp"
        ) {
            Toasty.info(context, "暂未开发").show()
        }
        SettingsItem(
            title = "夜间模式显示背景图",
            description = if(showDarkBack){"显示"}else{"不显示"}
        ) {
            selectInit = if (showDarkBack)(0)else{1}
            selectList = listOf("显示", "不显示")
            showNorSetDialog.value = true
        }
        SettingsItem(
            title = "时间列字体颜色",
            description = "暂时只支持黑白色设置"
        ) {

        }
    }
}

@Composable
fun SettingsPart(
    label:String,
    content:@Composable () -> Unit
){
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
    title:String,
    description:String,
    onCLick:() -> Unit
){
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
                withStyle(style =
                    SpanStyle(
                        fontSize = MaterialTheme.typography.body1.fontSize,
                        color = Color.Black
                    )
                ){
                    append(title)
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = MaterialTheme.typography.body1.fontSize,
                        color = Color.Gray
                    )
                ){
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