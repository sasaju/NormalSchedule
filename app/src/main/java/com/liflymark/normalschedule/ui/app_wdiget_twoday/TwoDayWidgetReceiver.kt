package com.liflymark.normalschedule.ui.app_wdiget_twoday

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.notice.sendCourseNotification
import com.liflymark.normalschedule.logic.notice.sendTestNotification
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TwoDayWidgetReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = TwoDayWidgetProvider()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName =
            ComponentName(context.packageName, checkNotNull(javaClass.canonicalName))
        Log.d("weRecive", intent.action.toString())
        when(intent.action){
            "com.fly.courseNotice" -> {
                Log.d("TwoDay", "courseNotice接收")
                val title = intent.getStringExtra("title") ?: "内容传入错误"
                val description = intent.getStringExtra("description") ?: "无描述"
                val isTest = intent.getBooleanExtra("isTest", false)
                val nowDateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                if (isTest) {
                    sendTestNotification(context)
                } else {
                    sendCourseNotification(context, textTitle = title, textContent = description)
                    runBlocking { Repository.setCourseNoticeLast(title+description, nowDateStr) }
                }

                // 此处不执行work，亦可通过onUpdate->setRepeat->发送com.fly.setThisDayCourseNotice实现worker执行
                // 但出于可读性和稳定性考虑，此处选择多执行一次
                val setNoticeRequest = OneTimeWorkRequestBuilder<CourseNoticeWorker>()
                    .build()
                WorkManager.getInstance(context).enqueueUniqueWork("setCourseNotice",
                    ExistingWorkPolicy.KEEP,setNoticeRequest)
                onUpdate(
                    context,
                    appWidgetManager,
                    appWidgetManager.getAppWidgetIds(componentName)
                )
            }
            "com.fly.setThisDayCourseNotice" -> {
                Log.d("TwoDay", "setThisDayCourseNotice接收")
                val setNoticeRequest = OneTimeWorkRequestBuilder<CourseNoticeWorker>()
                    .build()
                WorkManager.getInstance(context).enqueueUniqueWork(
                    "setCourseNotice",
                    ExistingWorkPolicy.KEEP, setNoticeRequest
                )
            }
        }
    }
}