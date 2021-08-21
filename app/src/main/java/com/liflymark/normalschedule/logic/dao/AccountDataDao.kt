package com.liflymark.normalschedule.logic.dao

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.NormalScheduleApplication.Companion.context
import com.liflymark.schedule.data.Settings
import kotlinx.coroutines.flow.map

object AccountDataDao {
    fun getNewUserOrNot() = run {
        val context = NormalScheduleApplication.context
        context.dataStore.data
            .map { preferences ->
                preferences[intPreferencesKey("userVersion")]?:0 < 1
            }
    }

    suspend fun saveUserVersion(version:Int = 1){
        val context = NormalScheduleApplication.context
        context.dataStore.edit {
            it[intPreferencesKey("userVersion")] = version
        }
    }


    // Proto内容
    private val Context.settingsStore: DataStore<Settings> by dataStore(
        fileName = "settings.pb",
        serializer = SettingsSerializer
    )
    val scheduleSettings = context.settingsStore.data

    fun getDarkShowBack() = context.settingsStore.data.map {
        it.darkShowBack
    }

    /**
     * 0-非渐变 默认
     * 1-渐变色
     */
    suspend fun updateColorMode(mode: Int){
        context.settingsStore.updateData {
            it.toBuilder()
                .setColorMode(mode)
                .build()
        }
    }

    suspend fun updateDarkShowBack(show:Boolean){
        context.settingsStore.updateData {
            it.toBuilder()
                .setDarkShowBack(show)
                .build()
        }
    }

    suspend fun updateSettings(setSettings:(settings:Settings)->Settings){
        context.settingsStore.updateData {
            setSettings(it)
        }
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "accountInfo")

}