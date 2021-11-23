package com.liflymark.normalschedule.ui.app_widget_new_day

import android.content.Intent
import android.widget.RemoteViewsService

class DayNewRvService: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return DayNewRemoteFactory(this)
    }
}