package com.liflymark.normalschedule.ui.app_wdiget_twoday

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.notice.setRepeatCourseNoticeCheck
import com.liflymark.normalschedule.logic.utils.AppWidgetRow
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import com.liflymark.normalschedule.ui.app_widget_new_day.SingleDayOneCourse

class TwoDayWidgetProvider:GlanceAppWidget() {
    @Composable
    override fun Content() {
        TwoDaySmall()
        Log.d("APPWidgetTwoDay","更新一次")
    }
}

@Composable
fun TwoDaySmall(){
//    val mList = mutableListOf<OneByOneCourseBean>()
//    val allCourse = Repository.loadAllCourse3()
//    val nowWeekNum = GetDataUtil.whichWeekNow() + 1
//    val nowDayNum = GetDataUtil.getNowWeekNum()
//
//    allCourse?.get(nowWeekNum - 1)?.let {
//        mList.addAll(it.filter { it1 ->
//            it1.whichColumn == nowDayNum && !GetDataUtil.hadOvered(it1.end)
//        })
//        mList.sortBy { course -> course.start }
//    }

    AppWidgetRow {
        TodayCourse(glanceModifier = GlanceModifier.defaultWeight())
        Column(GlanceModifier.fillMaxHeight()){
            Spacer(
                modifier = GlanceModifier
                    .height(14.dp)
            )
            Spacer(
                modifier = GlanceModifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.LightGray)
            )
        }
        TomorrowCourse(glanceModifier = GlanceModifier.defaultWeight())
    }
}

@Composable
fun TodayCourse(
    glanceModifier: GlanceModifier
){
    val mList = mutableListOf<OneByOneCourseBean>()
    val allCourse = Repository.loadAllCourse3()
    val nowWeekNum = GetDataUtil.whichWeekNow() + 1
    val nowDayNum = GetDataUtil.getNowWeekNum()
    val context = LocalContext.current
    allCourse?.get(nowWeekNum - 1)?.let {
        mList.addAll(it.filter { it1 ->
            it1.whichColumn == nowDayNum && !GetDataUtil.hadOvered(it1.end)
        })
        mList.sortBy { course -> course.start }
    }
    // 设置重复任务每次设置为2点，超过两点会立即执行，因为显示通知后会执行两次设置下一个通知worker，所以会执行两次worker
    setRepeatCourseNoticeCheck(context = context)
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .then(glanceModifier)
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .clickable(actionRunCallback<UpdateTwoDayAction>()),
        ) {
            Text(
                text = GetDataUtil.getDayOfWeek(),
                style = TextStyle(
                    color = ColorProvider(Color(0xff3685d5)),
                    fontSize = 13.sp
                )
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = "第${nowWeekNum}周",
                style = TextStyle(
                    color = ColorProvider(Color(0xff3685d5)),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        LazyColumn(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            mList.forEach { singleCourse ->
                item {
                    val courseNameBuild2 = singleCourse.courseName.split("\n")
                    val courseName2 = courseNameBuild2.getOrNull(0) ?: ""
                    val courseBuild2 = courseNameBuild2.getOrNull(1) ?: ""
                    val courseTimeStr2 = "${GetDataUtil.getStartTime(singleCourse.start)} - " +
                            GetDataUtil.getEndTime(singleCourse.end)
                    SingleDayOneCourse(
                        courseName = courseName2,
                        building = courseBuild2,
                        time = courseTimeStr2
                    )
                }
                item {
                    Spacer(modifier = GlanceModifier.height(4.dp))
                }
            }
            if (mList.isEmpty()) {
                item {
                    Text(
                        text = "\n\n今天没课辣~\n\\(^o^)/~",
                        style = TextStyle(
                            color = ColorProvider(Color.Gray),
                            textAlign = TextAlign.Center
                        ),
                        modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>()).fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun TomorrowCourse(
    glanceModifier: GlanceModifier
){
    val mList = mutableListOf<OneByOneCourseBean>()
    val allCourse = Repository.loadAllCourse3()
    val nowWeekNum = GetDataUtil.whichWeekNow(add = 1) + 1
    allCourse?.get(nowWeekNum - 1)?.let {
        mList.addAll(it)
        mList.sortBy { course -> course.start }
    }
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .then(glanceModifier)
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .clickable(actionRunCallback<UpdateTwoDayAction>()),
        ) {
            Text(
                text = GetDataUtil.getDayOfWeek(add = 1),
                style = TextStyle(
                    color = ColorProvider(Color(0xff3685d5)),
                    fontSize = 13.sp
                )
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = "明天",
                style = TextStyle(
                    color = ColorProvider(Color(0xff3685d5)),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        LazyColumn{
            mList.forEach {  singleCourse ->
                item {
                    val courseNameBuild2 = singleCourse.courseName.split("\n")
                    val courseName2 = courseNameBuild2.getOrNull(0) ?: ""
                    val courseBuild2 = courseNameBuild2.getOrNull(1) ?: ""
                    val courseTimeStr2 = "${GetDataUtil.getStartTime(singleCourse.start)} - " +
                            GetDataUtil.getEndTime(singleCourse.end)
                    SingleDayOneCourse(courseName = courseName2, building = courseBuild2, time = courseTimeStr2)
                }
                item {
                    Spacer(modifier = GlanceModifier.height(4.dp))
                }
            }
            if(mList.isEmpty()){
                item{
                    Text(
                        text = "\n\n明天没课辣~\n\\(^o^)/~",
                        style = TextStyle(
                            color = ColorProvider(Color.Gray),
                            textAlign = TextAlign.Center
                        ),
                        modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>()).fillMaxWidth()
                    )
                }
            }
        }
    }
}

class UpdateTwoDayAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        TwoDayWidgetProvider().update(context, glanceId)
    }
}

