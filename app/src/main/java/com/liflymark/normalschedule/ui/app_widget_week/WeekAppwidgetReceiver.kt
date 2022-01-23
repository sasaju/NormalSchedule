package com.liflymark.normalschedule.ui.app_widget_week

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.liflymark.normalschedule.logic.Repository

class WeekAppwidgetReceiver:GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeekAppwidgetAppwidget()

}