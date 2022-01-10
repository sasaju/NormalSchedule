package com.liflymark.normalschedule.ui.app_widget_miui

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.GetDataUtil


class DayNewWidgetProvider: AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Convert.onUpdateMIUIWidget(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        // Protect against rogue update broadcasts (not really a security issue,
        // just filter bad broacasts out so subclasses are less likely to crash).
        val action = intent!!.action
        if ("miui.appwidget.action.APPWIDGET_UPDATE" == action) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisAppWidget = ComponentName(
                context!!.packageName,
                this::class.java.name
            )
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
//            val appWidgets = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            if (appWidgetIds != null) {
                onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds)
            }
        }else{
            super.onReceive(context, intent)
        }
    }
}