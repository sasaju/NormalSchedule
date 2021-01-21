package com.liflymark.normalschedule

import android.app.Application
import android.content.Context

class NormalScheduleApplication : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}