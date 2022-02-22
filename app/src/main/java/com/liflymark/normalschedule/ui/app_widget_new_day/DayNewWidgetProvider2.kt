package com.liflymark.normalschedule.ui.app_widget_new_day

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import com.liflymark.normalschedule.ui.app_widget_week.WeekAppwidgetAppwidget

class DayNewWidgetProvider2():GlanceAppWidget() {
    @Composable
    override fun Content(){
        val mList = mutableListOf<OneByOneCourseBean>()
        val allCourse = Repository.loadAllCourse3()
        val nowWeekNum = GetDataUtil.whichWeekNow() + 1
        val nowDayNum = GetDataUtil.getNowWeekNum()
        allCourse?.get(nowWeekNum - 1)?.let {
            mList.addAll(it.filter { it1 ->
                it1.whichColumn == nowDayNum && !GetDataUtil.hadOvered(it1.end)
            })
        }
        Column(
            modifier = GlanceModifier
                .background(ImageProvider(R.drawable.miui_appwidget_back))
                .padding(11.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clickable(actionRunCallback<UpdateAllAction>())
                ,
            ){
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
            ){
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
                            text = "\n\n今天没课辣~",
                            style = TextStyle(
                                color = ColorProvider(Color.Gray),
                                textAlign = TextAlign.Center
                            ),
                            modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>())
                        )
                    }
                }
            }
        }
        
    }
}

@Composable
fun SingleDayOneCourse(
    courseName:String,
    building:String,
    time:String
){
    Row(
        modifier = GlanceModifier
            .background(ImageProvider(R.drawable.miui_appwidget_course))
            .fillMaxWidth()
            .height(65.dp)
            .clickable(actionStartActivity<MainActivity>())
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = GlanceModifier.width(4.dp))
        Spacer(
            modifier = GlanceModifier
                .width(5.dp)
                .height(45.dp)
                .background(ImageProvider(R.drawable.miui_bar))
        )
        Spacer(modifier = GlanceModifier.width(4.dp))
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = courseName,
                style = TextStyle(
                    fontSize = 13.sp,
                    color = ColorProvider(Color(0xFF333333)),
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
            )
            Text(
                text = building,
                style = TextStyle(
                    fontSize = 11.sp,
                    color = ColorProvider(Color(0x80000000)),
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
            )
            Text(
                text = time,
                style = TextStyle(
                    fontSize = 10.sp,
                    color = ColorProvider(Color(0x4D000000)),
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
            )
        }
    }
}

class UpdateAllAction() : ActionCallback {
    override suspend fun onRun(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        DayNewWidgetProvider2().update(context, glanceId)
    }
}