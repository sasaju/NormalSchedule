package com.liflymark.normalschedule

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.liflymark.normalschedule.logic.dao.SettingsSerializer
import com.liflymark.schedule.data.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class NormalScheduleApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var settingData: DataStore<Settings>
        lateinit var settingFirst:Settings
    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        settingData = context.settingsStore
        settingFirst = runBlocking { context.settingsStore.data.first() }
    }

    private val Context.settingsStore: DataStore<Settings> by dataStore(
        fileName = "settings.pb",
        serializer = SettingsSerializer
    )

}