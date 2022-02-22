package com.liflymark.normalschedule.ui.app_widget_new_day

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class DayNewWidgetReceiver:GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = DayNewWidgetProvider2()
}