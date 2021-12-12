package com.liflymark.normalschedule.ui.app_widget_miui

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RemoteViews
import androidx.lifecycle.viewmodel.compose.viewModel
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.GetDataUtil


class DayNewWidgetProvider: AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach { appWidgetId ->
//            val pendingIntent: PendingIntent = Intent(context, ExampleActivity::class.java)
//                .let { intent ->
//                    PendingIntent.getActivity(context, 0, intent, 0)
//                }

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.miui_appwidget_day
            )
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }
            pendingIntent.let {
                views.setOnClickPendingIntent(R.id.course_1, it)
                views.setOnClickPendingIntent(R.id.course_2, it)
                views.setOnClickPendingIntent(R.id.no_class, it)
            }

            val intentSync = Intent(context, this::class.java)
            intentSync.action = "miui.appwidget.action.APPWIDGET_UPDATE"

            val pendingSync = PendingIntent.getBroadcast(
                context,
                0,
                intentSync,
                PendingIntent.FLAG_UPDATE_CURRENT
            ) //You need to specify a proper flag for the intent. Or else the intent will become deleted.

            views.setOnClickPendingIntent(R.id.week_num, pendingSync)
            // get today course
            val mList = mutableListOf<OneByOneCourseBean>()
            val allCourse = Repository.loadAllCourse3()
            val nowWeekNum = GetDataUtil.whichWeekNow()
            val nowDayNum = GetDataUtil.getNowWeekNum()
            allCourse?.get(nowWeekNum)?.let {
                mList.addAll(it.filter { it1 ->
                    it1.whichColumn == nowDayNum && !GetDataUtil.hadOvered(it1.end)
                })
            }
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
                    views.setViewVisibility(R.id.course_2, View.GONE)

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
                    views.setTextViewText(R.id.app_course_name_2, courseName2)
                    views.setTextViewText(R.id.app_course_teacher_2, courseBuild2)
                    views.setTextViewText(R.id.app_course_time_2, courseTimeStr2)
                }
            }
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if ("miui.appwidget.action.APPWIDGET_UPDATE" == intent!!.action) {
            val appWidgets = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            if (appWidgets != null) {
                onUpdate(context, AppWidgetManager.getInstance(context), appWidgets)
                intent.action?.let { Log.d("miui", it+"+update") }
            } //或者自定义刷新逻辑
        } else {
            super.onReceive(context, intent)
        }
        intent.action?.let { Log.d("miui", it) }
    }
}