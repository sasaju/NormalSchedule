package com.liflymark.normalschedule.ui.app_widget_new_day

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.lifecycle.viewmodel.compose.viewModel
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.GetDataUtil


class DayNewWidgetProvider: AppWidgetProvider() {
    companion object {
        const val startAction = "com.liflymark.start.normal"
        const val extraItem = "com.liflymark.extra.normal"
    }
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach { appWidgetId ->
            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.appwidget_new_day
            )
            Log.d("NewProwvider OnUpdate", "update 1")
            val intent = Intent(context, DayNewRvService::class.java)
            views.setTextViewText(R.id.date_text, GetDataUtil.getNowDateTime())
            views.setTextViewText(R.id.week_text, getWeekString(GetDataUtil.getNowWeekNum()))
//            views.setRemoteAdapter(R.id.new_appwidget_list, null)
            views.setRemoteAdapter(R.id.new_appwidget_list, intent)

//            val intentNew: Intent = Intent(context, this.javaClass).apply {
//                action = startAction
//                data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
//            }
//            val pendingIntent = PendingIntent.getBroadcast(context, 0,intentNew, PendingIntent.FLAG_UPDATE_CURRENT)
            val miuiIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }
            val pendingIntent = PendingIntent.getActivity(context, 0, miuiIntent, 0)
            views.setPendingIntentTemplate(R.id.new_appwidget_list, pendingIntent)
            val intentSync = Intent(context, this::class.java)
            intentSync.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE //You need to specify the action for the intent. Right now that intent is doing nothing for there is no action to be broadcasted.

            val pendingSync = PendingIntent.getBroadcast(
                context,
                0,
                intentSync,
                PendingIntent.FLAG_UPDATE_CURRENT
            ) //You need to specify a proper flag for the intent. Or else the intent will become deleted.

            views.setOnClickPendingIntent(R.id.date_text, pendingSync)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.new_appwidget_list)
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
//        if ("miui.appwidget.action.APPWIDGET_UPDATE" == intent!!.action) {
//            val appWidgets = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
//            onUpdate(context!!, AppWidgetManager.getInstance(context), appWidgets!!) //或者自定义刷新逻辑
//        } else {
//            super.onReceive(context, intent)
//        }

//        if ("start" == intent!!.action){
//            val intentNew = Intent(context, MainActivity::class.java)
//            context!!.startActivity(intentNew)
//        }
//        val mgr = AppWidgetManager.getInstance(context)
//        if (intent!!.action.equals(startAction)) {
////            val appWidgetId = intent.getIntExtra(
////                AppWidgetManager.EXTRA_APPWIDGET_ID,
////                AppWidgetManager.INVALID_APPWIDGET_ID
////            )
////            val viewIndex = intent.getIntExtra(extraItem, 0)
////            Toast.makeText(context, "Touched view $viewIndex", Toast.LENGTH_SHORT).show()
//            Log.d("NewProvider", startAction)
//            val intentNew = Intent(context, MainActivity::class.java).apply {
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            }
//            context!!.startActivity(intentNew)
//        }
//
        super.onReceive(context, intent)
        Log.d("DayNewWidgetProvider", "onReceive")
        if (intent!!.action ==  AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            Log.d("DayNewWidgetProvider", "RECIVE aTION")
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisAppWidget = ComponentName(
                context!!.packageName,
                this::class.java.name
            )
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.new_appwidget_list)
            onUpdate(context, appWidgetManager, appWidgetIds)
//            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.new_appwidget_list)
        }
    }

    fun getWeekString(whichColumn:Int): String =
        when(whichColumn){
            1 -> "周一"
            2 -> "周二"
            3 -> "周三"
            4 -> "周四"
            5 -> "周五"
            6 -> "周六"
            7 -> "周日"
            else -> "错误"
        }

}