package com.liflymark.normalschedule.ui.app_widget_week

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.ui.app_widget_day.DayRemoteViewsService

class WeekAppWidgetProvider: AppWidgetProvider() {
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray?
    ) {
//        super.onUpdate(context, appWidgetManager, appWidgetIds)

//        val listView = views.setRemoteAdapter()
        appWidgetIds?.forEach { appWidgetId  ->
            val randomNumber=(Math.random()*100).toInt()
            val views: RemoteViews = RemoteViews(
                context?.packageName,
                R.layout.app_widget_week
            )
            val intent = Intent(context, DayRemoteViewsService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.putExtra("random", randomNumber)
            intent.data = Uri.fromParts("content", (appWidgetId+randomNumber).toString(),null)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}