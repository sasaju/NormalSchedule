package com.liflymark.normalschedule.ui.settings

import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.GifLoader
import com.liflymark.normalschedule.ui.show_timetable.SingleClass2
import com.liflymark.normalschedule.ui.show_timetable.getEndTime
import com.liflymark.normalschedule.ui.show_timetable.getStartTime
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import com.liflymark.schedule.data.Settings
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.reflect.KMutableProperty1

@Composable
fun SettingsCardPage(
    navController: NavController
){
    var newSettings by remember{ mutableStateOf<Settings?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showCloseDialog by rememberSaveable { mutableStateOf(false) }

    // 拦截返回键
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher
    val backCallback = remember {
        object : OnBackPressedCallback(!showCloseDialog) {
            override fun handleOnBackPressed() {
                showCloseDialog = true
            }
        }
    }
    LaunchedEffect(showCloseDialog){
        backCallback.isEnabled = !showCloseDialog
    }
    DisposableEffect(backDispatcher) {
        // Add callback to the backDispatcher
        backDispatcher.addCallback(backCallback)

        // When the effect leaves the Composition, remove the callback
        onDispose {
            backCallback.remove()
        }
    }

    if (showCloseDialog){
        AlertDialog(
            onDismissRequest = { showCloseDialog=false },
            title = { Text(text = "您还没有保存您的修改")},
            text = { Text(text = "是否保存您的修改")},
            confirmButton = {
                TextButton(onClick = {
                    if (newSettings!=null){
                        scope.launch {
                            Repository.updateSettings(newSettings!!)
                            Toasty.success(context, "保存成功").show()
                            showCloseDialog = false
                            navController.navigateUp()
                        }
                    }
                }) {
                    Text(text = "保存并退出")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCloseDialog = false
                    navController.navigateUp()
                }) {
                    Text(text = " 直接退出")
                }
            }
        )
    }

    Scaffold(
        topBar = {
             NormalTopBar(label = "配置课程格子") {
                 showCloseDialog = true
             }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "保存修改") },
                onClick = {
                    if (newSettings!=null){
                        scope.launch {
                            Repository.updateSettings(newSettings!!)
                            Toasty.success(context, "保存成功").show()
                            navController.navigateUp()
                        }
                    }
                },
                icon = {Icon(Icons.Default.Save,"Save")}
            )
        },
        content = {
            SettingsPreviewAndControl(onValueChange = { newSettings = it })
        }
    )
}

@Composable
fun SettingsPreviewAndControl(
    onValueChange: (settings: Settings) -> Unit
){
    val settings = Repository.getScheduleSettings().collectAsState(initial = null)
    BackgroundImage()
    settings.value?.let { nowSetting ->
        var newSettings by remember { mutableStateOf(nowSetting) }
        LaunchedEffect(newSettings){
            onValueChange(newSettings)
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            ShowPreView(
                settings =newSettings,
                modifier = Modifier
                    .weight(1.6f)
                    .background(Color.Transparent)
            )
            Box(
                modifier = Modifier
                    .weight(6f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colors.background)
            ){
                SetCardControl(
                    settings = newSettings,
                    onValueChange ={ newSettings = it } ,
                    onValueChangeFinish = { newSettings = it }
                )
            }
        }
    }
}

@Composable
fun SetCardControl(
    modifier: Modifier = Modifier,
    settings: Settings,
    onValueChange: (settings: Settings) -> Unit,
    onValueChangeFinish: (settings: Settings) -> Unit
) {
    var settingsState by remember { mutableStateOf(settings) }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedButton(onClick = {
            val newSettingsBuilder = settingsState.toBuilder()
            newSettingsBuilder.apply {
                coursePerHeight = 70
                courseNameFontSize = 13F
                courseCardAlpha = 0.75F
                courseBorderSize = 0F
                courseBorderAlpha = 50
            }
            settingsState = newSettingsBuilder.build()
            onValueChange(newSettingsBuilder.build())
            onValueChangeFinish(newSettingsBuilder.build())
        }) {
            Text(text = "全部恢复默认")
        }
        // 课程格子高度
        SettingSliderItem(
            value = settingsState.coursePerHeight.toFloat(),
            startClick =
            {
                val newSettings = settingsState
                    .toBuilder()
                    .setCoursePerHeight(settingsState.coursePerHeight - 1)
                    .build()
                settingsState = newSettings
                onValueChange(newSettings)
            },
            endClick =
            {
                val newSettings = settingsState
                    .toBuilder()
                    .setCoursePerHeight(settingsState.coursePerHeight + 1)
                    .build()
                settingsState = newSettings
                onValueChange(newSettings)
            },
            onValueChange =
            {
                val newSettings = settingsState
                    .toBuilder()
                    .setCoursePerHeight(it.toInt())
                    .build()
                settingsState = newSettings
                onValueChange(newSettings)
            },
            onValueChangeFinish = { onValueChangeFinish(settingsState) },
            contentTitle = "课程格子高度",
            contentDescription = "当前${settingsState.coursePerHeight}dp, 默认：70dp",
            valueRange = 50F..150F
        )

        // 课程格子字体大小
        SettingSliderItem(
            value = settingsState.courseNameFontSize,
            startClick =
            {
                val newSettings = settingsState
                    .toBuilder()
                    .setCourseNameFontSize(settingsState.courseNameFontSize.roundToInt() - 1F)
                    .build()
                settingsState = newSettings
                onValueChange(newSettings)
            },
            endClick =
            {
                val newSettings = settingsState
                    .toBuilder()
                    .setCourseNameFontSize(settingsState.courseNameFontSize.roundToInt() + 1F)
                    .build()
                settingsState = newSettings
                onValueChange(newSettings)
            },
            onValueChange =
            {
                val newSettings = settingsState
                    .toBuilder()
                    .setCourseNameFontSize(it)
                    .build()
                settingsState = newSettings
                onValueChange(newSettings)
            },
            onValueChangeFinish = { onValueChangeFinish(settingsState) },
            contentTitle = "课程名称大小",
            contentDescription = "当前${(settingsState.courseNameFontSize * 100).roundToInt()/100.0}sp, 默认：13sp",
            valueRange = 10F..30F
        )

        // 课程格子透明度
        SettingSliderItem(
            valueRange = 0F..1F,
            value = settingsState.courseCardAlpha,
            startClick =
            {
                val newSettings = settingsState
                    .toBuilder()
                    .setCourseCardAlpha(settingsState.courseCardAlpha - 0.01F)
                    .build()
                settingsState = newSettings
                onValueChange(newSettings)
            },
            endClick =
            {
                val newSettings = settingsState
                    .toBuilder()
                    .setCourseCardAlpha(settingsState.courseCardAlpha + 0.01F)
                    .build()
                settingsState = newSettings
                onValueChange(newSettings)
            },
            onValueChange =
            {
                val newSettings = settingsState
                    .toBuilder()
                    .setCourseCardAlpha(it)
                    .build()
                settingsState = newSettings
                onValueChange(newSettings)
            },
            onValueChangeFinish = { onValueChangeFinish(settingsState) },
            contentTitle = "课程格子透明度",
            contentDescription = "当前${(settingsState.courseCardAlpha * 100).roundToInt()/100.0}, 默认：0.75",
        )

        // 课程格子边框宽度
        SettingSliderItem(
            valueRange = 0F..5F,
            value = settingsState.courseBorderSize,
            startClick =
            {
                val newSettings = settingsState
                    .toBuilder()
                    .setCourseBorderSize(settingsState.courseBorderSize.roundToInt() - 0.01F)
                    .build()
                settingsState = newSettings
                onValueChange(newSettings)
            },
            endClick =
            {
                val newSettings = settingsState
                    .toBuilder()
                    .setCourseBorderSize(settingsState.courseBorderSize + 0.01F)
                    .build()
                settingsState = newSettings
                onValueChange(newSettings)
            },
            onValueChange =
            {
                val newSettings = settingsState
                    .toBuilder()
                    .setCourseBorderSize(it)
                    .build()
                settingsState = newSettings
                onValueChange(newSettings)
            },
            onValueChangeFinish = { onValueChangeFinish(settingsState) },
            contentTitle = "边框宽度",
            contentDescription = "当前${(settingsState.courseBorderSize * 100).roundToInt()/100.0}dp, 默认：0dp",
        )

        // 课程边框透明度
        SettingSliderItem(
            valueRange = 0F..100F,
            value = settingsState.courseBorderAlpha.toFloat(),
            startClick =
            {
                val newSettings = settingsState
                    .toBuilder()
                    .setCourseBorderAlpha(settingsState.courseBorderAlpha - 1)
                    .build()
                settingsState = newSettings
                onValueChange(newSettings)
            },
            endClick =
            {
                val newSettings = settingsState
                    .toBuilder()
                    .setCourseBorderAlpha(settingsState.courseBorderAlpha + 1)
                    .build()
                settingsState = newSettings
                onValueChange(newSettings)
            },
            onValueChange =
            {
                val newSettings = settingsState
                    .toBuilder()
                    .setCourseBorderAlpha(it.toInt())
                    .build()
                settingsState = newSettings
                onValueChange(newSettings)
            },
            onValueChangeFinish = { onValueChangeFinish(settingsState) },
            contentTitle = "边框透明度",
            contentDescription = "当前${settingsState.courseBorderAlpha}%, 默认：50%",
        )
    }
}

/**
 * 单个配置项
 */
@Composable
fun SettingSliderItem(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    enabled: Boolean = true,
    steps: Int = 0,
    startIcon: ImageVector = Icons.Default.Remove,
    endIcon: ImageVector = Icons.Default.Add,
    startClick: () -> Unit,
    endClick: () -> Unit,
    onValueChange: (value: Float) -> Unit,
    onValueChangeFinish: () -> Unit,
    contentTitle: String,
    contentDescription: String,
) {
    Column {
        TextSlider(
            modifier = Modifier,
            value = value,
            startIcon = startIcon,
            endIcon = endIcon,
            startClick = { startClick() },
            endClick = { endClick() },
            onValueChange = { onValueChange(it) },
            onValueChangeFinish = { onValueChangeFinish() },
            valueRange = valueRange,
            enabled = enabled,
            steps = steps,
            contentTitle = contentTitle,
            contentDescription = contentDescription
        )
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .background(MaterialTheme.colors.secondary)
                .fillMaxWidth()
        )
    }
}

/**
 * 一个带有文字说明的Slider
 */
@Composable
fun TextSlider(
    modifier: Modifier,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    enabled: Boolean = true,
    steps: Int = 0,
    startIcon: ImageVector = Icons.Default.Remove,
    endIcon: ImageVector = Icons.Default.Add,
    startClick: () -> Unit,
    endClick: () -> Unit,
    onValueChange: (value: Float) -> Unit,
    onValueChangeFinish: () -> Unit,
    contentTitle: String,
    contentDescription: String,
) {
    Column(
        modifier = Modifier.padding(5.dp)
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = contentTitle, style = MaterialTheme.typography.subtitle2)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = contentDescription,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSecondary
            )
        }
        IconSlider(
            modifier = modifier,
            value = value,
            startIcon = startIcon,
            endIcon = endIcon,
            startClick = { startClick() },
            endClick = { endClick() },
            onValueChange = { onValueChange(it) },
            onValueChangeFinish = { onValueChangeFinish() },
            valueRange = valueRange,
            enabled = enabled,
            steps = steps
        )
    }
}

/**
 * 一个带有左右图标的Slider封装
 */
@Composable
fun IconSlider(
    modifier: Modifier,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    enabled: Boolean = true,
    steps: Int = 0,
    startIcon: ImageVector = Icons.Default.Remove,
    endIcon: ImageVector = Icons.Default.Add,
    startClick: () -> Unit,
    endClick: () -> Unit,
    onValueChange: (value: Float) -> Unit,
    onValueChangeFinish: () -> Unit
) {
    Row(
        modifier = modifier,
    ) {
        IconButton(onClick = { startClick() }) { Icon(startIcon, "减少") }
        Spacer(modifier = Modifier.width(10.dp))
        Slider(
            value = value,
            onValueChange = { onValueChange(it) },
            onValueChangeFinished = { onValueChangeFinish() },
            steps = steps,
            valueRange = valueRange,
            enabled = enabled,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        IconButton(onClick = { endClick() }) { Icon(endIcon, "增加") }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ShowPreView(
    modifier: Modifier = Modifier,
    settings: Settings
) {
    val iconColor = if (!settings.darkShowBack) {
        MaterialTheme.colors.onBackground
    } else {
        Color.Black
    }
    Row(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.weight(0.6f)) {
            Spacer(modifier = Modifier.height(40.dp))
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
                        .height(settings.coursePerHeight.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
        Column(
            Modifier.weight(1f)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            SingleClass2(
                singleClass = OneByOneCourseBean("药物化学\n第九教学楼888\n张三", 1, 2, 1, Color(0xff12c2e9)),
                courseClick = {},
                settings = settings,
                loadWork = false,
            )
            Spacer(modifier = Modifier.height((settings.coursePerHeight * 2).dp))
            SingleClass2(
                singleClass = OneByOneCourseBean(
                    "大学生职业规划\n第八教学楼666\n张三",
                    1,
                    2,
                    1,
                    Color(0xfff64f59),
                ),
                courseClick = {},
                settings = settings,
                loadWork = false,
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun BackgroundImage() {
    val showDarkBack = Repository.getShowDarkBack().collectAsState(initial = false)
    val path = Repository.loadBackground().observeAsState()
    val context = LocalContext.current
    if (!isSystemInDarkTheme() || showDarkBack.value) {
        CompositionLocalProvider(LocalImageLoader provides GifLoader(context)) {
            Image(
                painter = rememberImagePainter(
                    data = path.value?:R.drawable.main_background_4,
                    builder = {
                        this.error(R.drawable.main_background_4)
                    }
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        Image(
            painter = rememberImagePainter(data = ""),
            contentDescription = null,
            modifier = Modifier
                .background(Color(1, 86, 127))
                .fillMaxSize()
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun CardSetPreview() {
    NorScTheme {
//        BackgroundImage()
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.Transparent)
//        ) {
//            ShowPreView(settings = Settings.getDefaultInstance(), modifier = Modifier
//                .weight(1.6f)
//                .background(Color.Transparent))
//            Box(modifier = Modifier
//                .weight(6f)
//                .fillMaxHeight()
//                .background(Color.DarkGray))
//        }
        SettingsPreviewAndControl({})
    }
}