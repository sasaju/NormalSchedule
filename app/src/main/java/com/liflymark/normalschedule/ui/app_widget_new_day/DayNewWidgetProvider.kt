package com.liflymark.normalschedule.ui.app_widget_new_day

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.AnalogClock
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.liflymark.normalschedule.R

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
                R.layout.appwidget_new_day
            )


            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if ("miui.appwidget.action.APPWIDGET_UPDATE" == intent!!.action) {
            val appWidgets = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            onUpdate(context!!, AppWidgetManager.getInstance(context), appWidgets!!) //或者自定义刷新逻辑
        } else {
            super.onReceive(context, intent)
        }
    }
}