package com.liflymark.normalschedule.ui.app_widget_day

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.utils.GetDataUtil




class DayAppWidgetProvider: AppWidgetProvider() {
    var clickAction = "com.liflymark.DayAppWidgetProvider.onclick"
    @SuppressLint("ResourceType")
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds?.forEach { appWidgetId ->
            // Create an Intent to launch ExampleActivity
            val randomNumber=(Math.random()*100).toInt()
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
            val nowWeekNum = GetDataUtil.whichWeekNow()
            val nowDayNum = GetDataUtil.getNowWeekNum()
            val intent = Intent(context, DayRemoteViewsService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.putExtra("random", randomNumber)
            intent.data = Uri.fromParts("content", (appWidgetId+randomNumber).toString(),null)
            views.setTextViewText(
                R.id.start_text,
                GetDataUtil.getNowMonth(whichColumn = nowDayNum, whichWeek = nowWeekNum)
            )
            views.setRemoteAdapter(R.id.course_day_list, intent)

            views.setEmptyView(R.id.course_day_list, R.layout.app_widget_none_data)

//            val intentSync = Intent(context, DayAppWidgetProvider::class.java)
//            intentSync.action =
//                AppWidgetManager.ACTION_APPWIDGET_UPDATE //You need to specify the action for the intent. Right now that intent is doing nothing for there is no action to be broadcasted.
//
//            val pendingSync = PendingIntent.getBroadcast(
//                context,
//                0,
//                intentSync,
//                PendingIntent.FLAG_UPDATE_CURRENT
//            ) //You need to specify a proper flag for the intent. Or else the intent will become deleted.
//
//            remoteV.setOnClickPendingIntent(R.id.imageButtonSync, pendingSync)

            Log.d("Appwidget", "小部件Update")
//            appWidgetManager?.notifyAppWidgetViewDataChanged()
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