package com.liflymark.normalschedule.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.ReduceCapacity
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.compose.ui.zIndex
import coil.annotation.ExperimentalCoilApi
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.GifLoader
import com.liflymark.normalschedule.ui.show_timetable.SingleClass2
import com.liflymark.normalschedule.ui.show_timetable.getEndTime
import com.liflymark.normalschedule.ui.show_timetable.getStartTime
import com.liflymark.normalschedule.ui.theme.NorScTheme
import com.liflymark.schedule.data.Settings

@Composable
fun SetCardControl(
    modifier: Modifier = Modifier,
    settings: Settings
){
    Column(
        modifier = modifier
    ) {
        Slider(
            value = 0F,
            onValueChange = {},
            valueRange = 10F..150F
        )
    }
}
/**
 * 一个带有文字说明的Slider
 */
@Composable
fun TextSlider(
    modifier: Modifier,
    value:Float,
    valueRange:ClosedFloatingPointRange<Float> = 0f..1f,
    enabled:Boolean = true,
    steps:Int = 0,
    startIcon:ImageVector,
    endIcon:ImageVector,
    startClick:() -> Unit,
    endClick:() -> Unit,
    onValueChange:(value:Float) -> Unit,
    onValueChangeFinish:() -> Unit
){

}

/**
 * 一个带有左右图标的Slider封装
 */
@Composable
fun IconSlider(
    modifier: Modifier,
    value:Float,
    valueRange:ClosedFloatingPointRange<Float> = 0f..1f,
    enabled:Boolean = true,
    steps:Int = 0,
    startIcon:ImageVector,
    endIcon:ImageVector,
    startClick:() -> Unit,
    endClick:() -> Unit,
    onValueChange:(value:Float) -> Unit,
    onValueChangeFinish:() -> Unit
){
    Row(
        modifier = modifier,
    ){
        IconButton(onClick = { startClick() }) { Icon(startIcon, "减少") }
        Spacer(modifier = Modifier.width(10.dp))
        Slider(
            value = value,
            onValueChange = {onValueChange(it)},
            onValueChangeFinished = {onValueChangeFinish()},
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
                        .height(70.dp),
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
            )
            Spacer(modifier = Modifier.height((70*2).dp))
            SingleClass2(
                singleClass = OneByOneCourseBean(
                    "大学生职业规划\n第八教学楼666张三\n",
                    1,
                    2,
                    1,
                    Color(0xfff64f59)
                ),
                courseClick = {},
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun BackgroundImage() {
    val showDarkBack = Repository.getShowDarkBack().collectAsState(initial = false)
    val context = LocalContext.current
    if (!isSystemInDarkTheme() || showDarkBack.value) {
        CompositionLocalProvider(LocalImageLoader provides GifLoader(context)) {
            Image(
                painter = rememberImagePainter(
                    data = R.drawable.main_background_4,
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
fun CardSetPreview(){
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
        IconSlider(
            modifier = Modifier
                .fillMaxWidth(),
            value = 0.5F,
            startIcon = Icons.Default.Remove,
            endIcon = Icons.Default.Add,
            startClick = {  },
            endClick = { },
            onValueChange = {},
            onValueChangeFinish = {}
        )
    }
}