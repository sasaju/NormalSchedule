package com.liflymark.normalschedule.ui.add_course

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import com.liflymark.normalschedule.ui.app_widget_miui.DayNewWidgetProvider


fun updateWidget(context: Context) {
    // R.layout.demo_appwidget_normal_example 为小部件布局文件，这里仅示例
    val views: RemoteViews = RemoteViews(
        context.packageName,
        com.liflymark.normalschedule.R.layout.miui_appwidget_day
    )
//            Thread.sleep(10000)
    val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
        .let { intent ->
            PendingIntent.getActivity(context, 0, intent,  PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    pendingIntent.let {
        views.setOnClickPendingIntent(R.id.course_1, it)
        views.setOnClickPendingIntent(R.id.course_2, it)
        views.setOnClickPendingIntent(R.id.no_class, it)
    }

    val intentSync = Intent(context, DayNewWidgetProvider::class.java)
    intentSync.action = "miui.appwidget.action.APPWIDGET_UPDATE"

    val pendingSync = PendingIntent.getBroadcast(
        context,
        0,
        intentSync,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    ) //You need to specify a proper flag for the intent. Or else the intent will become deleted.

    views.setOnClickPendingIntent(R.id.week_num, pendingSync)
    // get today course
    val mList = mutableListOf<OneByOneCourseBean>()
    val allCourse = Repository.loadAllCourse3()
    val nowWeekNum = GetDataUtil.whichWeekNow()+1
    val nowDayNum = GetDataUtil.getNowWeekNum()
    allCourse?.get(nowWeekNum-1)?.let {
        mList.addAll(it.filter { it1 ->
            it1.whichColumn == nowDayNum && !GetDataUtil.hadOvered(it1.end)
        })
    }
    Log.d("miuiPro", mList.toString())
    views.setTextViewText(R.id.week_now, GetDataUtil.getDayOfWeek())
    views.setTextViewText(R.id.week_num, "第${nowWeekNum}周")
    // set when have one course, two course, zero course or more course
    when (mList.size){
        0 -> {
            views.setViewVisibility(R.id.course_1, View.GONE)
            views.setViewVisibility(R.id.course_2, View.GONE)
            views.setViewVisibility(R.id.no_class, View.VISIBLE)
        }
        1 -> {
            views.setViewVisibility(R.id.course_1, View.VISIBLE)
            views.setViewVisibility(R.id.course_2, View.GONE)
            views.setViewVisibility(R.id.no_class, View.GONE)
            val courseInfo1 = mList[0]
            val courseNameBuild = courseInfo1.courseName.split("\n")
            val courseName = courseNameBuild.getOrNull(0)?:""
            val courseBuild = courseNameBuild.getOrNull(1)?:""
            val (nowType, minusRes) = GetDataUtil.hadStartedOrOver(courseInfo1.start, courseInfo1.end)
            val courseTimeStr = "距离${nowType}${minusRes}分钟"
            views.setTextViewText(R.id.app_course_name_1, courseName)
            views.setTextViewText(R.id.app_course_teacher_1, courseBuild)
            views.setTextViewText(R.id.app_course_time_1, courseTimeStr)
            views.setViewVisibility(R.id.course_2, View.INVISIBLE)
        }
        else -> {
            val courseInfo1 = mList[0]
            val courseNameBuild = courseInfo1.courseName.split("\n")
            val courseName = courseNameBuild.getOrNull(0)?:""
            val courseBuild = courseNameBuild.getOrNull(1)?:""
            val (nowType, minusRes) = GetDataUtil.hadStartedOrOver(courseInfo1.start, courseInfo1.end)
            val courseTimeStr = "距离${nowType}${minusRes}分钟"
            views.setTextViewText(R.id.app_course_name_1, courseName)
            views.setTextViewText(R.id.app_course_teacher_1, courseBuild)
            views.setTextViewText(R.id.app_course_time_1, courseTimeStr)

            val courseInfo2 = mList[1]
            val courseNameBuild2 = courseInfo2.courseName.split("\n")
            val courseName2 = courseNameBuild2.getOrNull(0)?:""
            val courseBuild2 = courseNameBuild2.getOrNull(1)?:""
            val courseTimeStr2 = "${GetDataUtil.getStartTime(courseInfo2.start)} - " +
                    GetDataUtil.getEndTime(courseInfo2.end)
            views.setViewVisibility(R.id.no_class, View.GONE)
            views.setViewVisibility(R.id.course_1, View.VISIBLE)
            views.setViewVisibility(R.id.course_2, View.VISIBLE)
            views.setTextViewText(R.id.app_course_name_2, courseName2)
            views.setTextViewText(R.id.app_course_teacher_2, courseBuild2)
            views.setTextViewText(R.id.app_course_time_2, courseTimeStr2)
        }
    }
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val thisAppWidget = ComponentName(
        context.packageName,
        DayNewWidgetProvider::class.java.name
    )
    val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
//            val appWidgets = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
    if (appWidgetIds != null) {
        DayNewWidgetProvider().onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds)
    }
    // Tell the AppWidgetManager to perform an update on the current app widget
//    appWidgetManager.updateAppWidget(appWidgetId, views)
//    appWidgetManager.updateAppWidget(componentName, remoteViews)
}