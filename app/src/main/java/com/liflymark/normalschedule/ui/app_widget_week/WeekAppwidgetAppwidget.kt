package com.liflymark.normalschedule.ui.app_widget_week

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.GetDataUtil

class WeekAppwidgetAppwidget() : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    @Composable
    override fun Content() {
        val perHeight = 50
        val allCourse = Repository.loadAllCourse3()
        val nowWeekNum = GetDataUtil.whichWeekNow() + 1
        val thisWeekCourse = allCourse?.getOrElse(nowWeekNum-1){ listOf() }
        Log.d("WeekDayApp",thisWeekCourse.toString())
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(ImageProvider(R.drawable.appwidget_week_background))
        ) {
            Row(
                modifier = GlanceModifier
                    .padding(10.dp)
                    .clickable(actionRunCallback<UpdateAllAction>()),
            ) {
                Spacer(modifier = GlanceModifier.width(10.dp))
                Text(
                    text = "第 $nowWeekNum 周",
                    style = TextStyle(fontSize = 18.sp),
                    modifier = GlanceModifier.defaultWeight()
                )
            }
            Row(
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                Spacer(modifier = GlanceModifier.width(20.dp))
                repeat(7) {
                    Text(
                        text = "${it + 1}",
                        modifier = GlanceModifier.defaultWeight(),
                        style = TextStyle(textAlign = TextAlign.Center)
                    )
                }
            }
            LazyColumn(
                modifier = GlanceModifier.fillMaxSize()
            ) {
                item {
                    Row(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .clickable(actionRunCallback<UpdateAllAction>())
                    ) {
                        TimeColumn(perHeight = perHeight)
                        repeat(7) { index ->
                            val nowJieShu = IntArray(12) { it + 1 }.toMutableList()
                            val thisDayCourse =
                                thisWeekCourse?.filter { it.whichColumn == index + 1 }
                                    ?.sortedBy { it.start }
                            Log.d("Appweek", thisDayCourse.toString())
                            Column(modifier = GlanceModifier.defaultWeight()) {
                                thisDayCourse?.let { thisDayCourses ->
                                    for (singleCourse in thisDayCourses) {
                                        val spacerHeight =
                                            perHeight * (singleCourse.start - nowJieShu.getOrElse(0) { 1 })
                                        if (spacerHeight > 0) {
                                            Spacer(modifier = GlanceModifier.height(spacerHeight.dp))
                                        }
                                        SingleClass(singleClass = singleCourse)
                                        nowJieShu -= IntArray(singleCourse.end) { it + 1 }.toMutableList()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class UpdateAllAction() : ActionCallback {
    override suspend fun onRun(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        WeekAppwidgetAppwidget().update(context, glanceId)
    }
}

// 使用repeat会导致glance莫名无法加载暂时使用写死的，以后再试试for循环
@Composable
fun TimeColumn(perHeight: Int) {
    Column(modifier = GlanceModifier.width(20.dp)) {
        Text(
            text = "1",
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(perHeight.dp),
            style = TextStyle(textAlign = TextAlign.Center)
        )
        Text(
            text = "2",
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(perHeight.dp),
            style = TextStyle(textAlign = TextAlign.Center)
        )
        Text(
            text = "3",
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(perHeight.dp),
            style = TextStyle(textAlign = TextAlign.Center)
        )
        Text(
            text = "4",
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(perHeight.dp),
            style = TextStyle(textAlign = TextAlign.Center)
        )
        Text(
            text = "5",
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(perHeight.dp),
            style = TextStyle(textAlign = TextAlign.Center)
        )
        Text(
            text = "6",
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(perHeight.dp),
            style = TextStyle(textAlign = TextAlign.Center)
        )
        Text(
            text = "7",
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(perHeight.dp),
            style = TextStyle(textAlign = TextAlign.Center)
        )
        Text(
            text = "8",
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(perHeight.dp),
            style = TextStyle(textAlign = TextAlign.Center)
        )
        Text(
            text = "9",
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(perHeight.dp),
            style = TextStyle(textAlign = TextAlign.Center)
        )
        Column {
            Text(
                text = "10",
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(perHeight.dp),
                style = TextStyle(textAlign = TextAlign.Center)
            )
            Text(
                text = "11",
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(perHeight.dp),
                style = TextStyle(textAlign = TextAlign.Center)
            )
        }
    }
}


@Composable
fun SingleClass(
    singleClass: OneByOneCourseBean,
    perHeight: Int = 50,
    courseNameSize: Int = 17,
) {
    val courseNameList = singleClass.courseName.split("\n")
    val courseName = courseNameList.getOrElse(0) { "空名称" }
    val courseBuild = courseNameList.getOrElse(1) { "" }
    val courseTeacher = courseNameList.getOrElse(2) { "" }
    val height = perHeight * (singleClass.end - singleClass.start + 1)
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(height.dp)
            .padding(0.95.dp)
            .clickable(actionStartActivity<MainActivity>()),
    ) {
        Text(
            modifier = GlanceModifier
                .cornerRadius(5.dp)
                .background(singleClass.twoColorList[0])
                .fillMaxSize()
                .padding(horizontal = 0.5.dp),
            text = courseName + "\n" + courseBuild,
            style = TextStyle(
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = ColorProvider(Color.White)
            ),
        )
    }
}
