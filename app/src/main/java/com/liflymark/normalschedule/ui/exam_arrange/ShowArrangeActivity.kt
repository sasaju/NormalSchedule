package com.liflymark.normalschedule.ui.exam_arrange

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.liflymark.normalschedule.logic.model.Arrange
import com.liflymark.normalschedule.logic.utils.CalendarUtil
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import com.liflymark.normalschedule.ui.tool_box.BoardCard
import kotlinx.coroutines.launch
import java.util.*

class ShowArrangeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val allExamArrange = intent.getStringExtra("detail_list")?:"[]"
        val allExamArrangeList = Convert.jsonToExamArrange(allExamArrange)
        setContent {
            NorScTheme {
                // A surface container using the 'background' color from the theme
                UiControl()
                ShowArrangePage(allExamArrangeList = allExamArrangeList)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ShowArrangePage(allExamArrangeList:List<Arrange>){
    var showFloat by rememberSaveable { mutableStateOf(true) }
    val context = LocalContext.current
    var requestGrant by rememberSaveable { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val multiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
        )
    )
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            NormalTopBar(label = "考试安排")
        },
        content = {
            it
            ExamArrangeContent(allExamArrangeList){ offset -> showFloat = offset<=0 }
            if (requestGrant){
                RequestGrant(
                    multiplePermissionsState = multiplePermissionsState,
                    navigateToSettingsScreen = {
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package",context.packageName, null)
                        )
                    },
                    scaffoldState = scaffoldState,
                    hadShowedGrant = {
                        scaffoldState.snackbarHostState.showSnackbar("正在保存，请不要重复点击，请保持页面开启")
                        CalendarUtil.addExamArrange(context, allExamArrangeList)
                        scaffoldState.snackbarHostState.showSnackbar("保存完毕，日历会提前七天提醒，也可以进入日历自行修改天数", duration = SnackbarDuration.Long)
                        requestGrant = false
                    }
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "导入日历日程") },
                onClick = {
                    requestGrant = true
                },
                icon = { Icon(Icons.Default.CalendarToday, "导入日程至日历") }
            )
        },

    )
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RequestGrant(
    multiplePermissionsState: MultiplePermissionsState,
    navigateToSettingsScreen: () -> Unit,
    scaffoldState: ScaffoldState,
    hadShowedGrant:suspend () -> Unit
) {
    var doNotShowRationale by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    when {
        multiplePermissionsState.allPermissionsGranted -> {
            LaunchedEffect(Unit) {hadShowedGrant()}
        }
        multiplePermissionsState.shouldShowRationale || multiplePermissionsState.revokedPermissions.isNotEmpty() ->
        {
            if (doNotShowRationale) {
                LaunchedEffect(key1 = Unit, block = {
                    scaffoldState.snackbarHostState.showSnackbar(message = "您已拒绝授予，如需授予请重新打开该页面")
                })
            } else {
                var show by rememberSaveable{ mutableStateOf(true)}
                if (show) {
                    AlertDialog(
                        onDismissRequest = {
                            multiplePermissionsState.launchMultiplePermissionRequest()
                            show = false
                        },
                        confirmButton = {
                            TextButton(onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }) {
                                Text(text = "授予权限")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("拒绝将无法使用该功能")
                                }
                                show = false
                                doNotShowRationale = true
                            }) {
                                Text(text = "拒绝并不再提示")

                            }
                        },
                        title = {
                            Text(text = "应用需要您授予权限，用以导入日历事件")
                        },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = false
                        )
                    )
                }
            }
        }

        else -> {
            var show by rememberSaveable{ mutableStateOf(true)}
            if (show){
                AlertDialog(
                    onDismissRequest = {
                        show = false
                    },
                    confirmButton = {
                        TextButton(onClick = navigateToSettingsScreen ) {
                            Text(text = "去设置授予")
                        }
                    },
                    title = {
                        Text(text = "应用需要您授予权限，用以导入日历事件")
                    },
                    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
                )
            }
        }
    }
}
@Composable
fun ExamArrangeContent(
    arrangeList: List<Arrange>,
    downSlide:(offset:Int) -> Unit
){
    val scrollState = rememberScrollState()
    var offset by rememberSaveable { mutableStateOf(0) }
    var lastOffset by rememberSaveable {
        mutableStateOf(0)
    }
    LaunchedEffect(scrollState.value){
        offset =  scrollState.value - lastOffset
        lastOffset = scrollState.value
        downSlide(offset)
    }
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(top = 4.dp)
    ) {
        item{
            Text(
                text = "结果极小概率发生缺失，请谨慎参考！",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        items(count = arrangeList.size, key={"net$it"}){ index: Int ->
            ExamArrangeCard(examArrange = arrangeList[index])
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}



@Composable
fun ExamArrangeCard(
    examArrange: Arrange
){
    BoardCard(
        modifier = Modifier
            .wrapContentHeight()
            .padding(2.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(horizontal = 3.dp)) {
            Text(text = examArrange.examName, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = examArrange.examTime, style = MaterialTheme.typography.subtitle1)
            Text(text = examArrange.examWeekName, style = MaterialTheme.typography.subtitle1)
            Text(text = examArrange.examBuilding, style = MaterialTheme.typography.subtitle1)
            Text(text = examArrange.examSeat, style = MaterialTheme.typography.subtitle1)
            Text(text = examArrange.examIdCard, style = MaterialTheme.typography.subtitle1)
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}
