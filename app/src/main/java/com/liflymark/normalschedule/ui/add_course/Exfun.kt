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
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import com.liflymark.normalschedule.ui.app_widget_miui.DayNewWidgetProvider


fun updateWidget(context: Context) {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val thisAppWidget = ComponentName(
        context.packageName,
        DayNewWidgetProvider::class.java.name
    )
    val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
    Convert.onUpdateMIUIWidget(context, appWidgetManager, appWidgetIds)
}