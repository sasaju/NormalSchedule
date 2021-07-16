package com.liflymark.normalschedule.ui.app_widget_day

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.R

class DayAppWidgetProvider: AppWidgetProvider() {
    var clickAction = "com.liflymark.DayAppWidgetProvider.onclick"
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds?.forEach { appWidgetId ->
            // Create an Intent to launch ExampleActivity

            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            val views: RemoteViews = RemoteViews(
                context?.packageName,
                R.layout.app_widget_day
            ).apply {
                setOnClickPendingIntent(R.id.start_text, pendingIntent)
            }

            val intent = Intent(context, DayRemoteViewsService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            views.setRemoteAdapter(R.id.course_day_list, intent)

            views.setEmptyView(R.id.course_day_list, R.layout.app_widget_none_data)



            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }

    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = clickAction
        if (action == "refresh"){
            val mgr =AppWidgetManager.getInstance(context)
            val cn = ComponentName(context, DayAppWidgetProvider::class.java)
        }
    }
}