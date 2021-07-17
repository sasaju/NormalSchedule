package com.liflymark.normalschedule.ui.app_widget_day

import android.content.Intent
import android.widget.RemoteViewsService

class DayRemoteViewsService: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return DayRemoteViewsFactory(this.applicationContext, intent)
    }
}