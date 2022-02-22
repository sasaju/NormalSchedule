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
import com.liflymark.schedule.data.twoColorItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

object AccountDataDao {
    fun getNewUserOrNot() = run {
        val context = NormalScheduleApplication.context
        context.dataStore.data
            .map { preferences ->
                preferences[intPreferencesKey("userVersion")]?:0 < 1
            }
    }

    fun getUserVersion()= run {
        val context = NormalScheduleApplication.context
        context.dataStore.data
            .map { preferences ->
                preferences[intPreferencesKey("userVersion")]
            }
    }

    suspend fun getUserVersionS(): Int {
        val context = NormalScheduleApplication.context
        return context.dataStore.data
            .map { it[intPreferencesKey("userVersion")] }.first()?:0
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

    // 同步写入成绩详情的缓存
    fun updateScoreDetail(scoreDetail:String) = runBlocking {
        context.settingsStore.updateData {
            it.toBuilder()
                .setScoreDetail(scoreDetail).build()
        }
    }

    // 同步读取成绩详情缓存
    fun getScoreDetail(): String = runBlocking {
        scheduleSettings.map { settings ->
            settings.scoreDetail
        }.first()
    }

    fun getColorListAsyc(): MutableList<twoColorItem> = runBlocking {
        scheduleSettings.map { value: Settings ->
            value.colorsList
        }.first()
    }

    suspend fun getLastUpdate():String {
        val res = scheduleSettings.map { it.lastUpdate }.first()
        return if (res==""){
            "2000-01-01"
        }else{
            res
        }
    }

    suspend fun setLastUpdate(last:String) {
        context.settingsStore.updateData {
            it.toBuilder()
                .setLastUpdate(last)
                .build()
        }
    }

    suspend fun updateSettings(setSettings:(settings:Settings)->Settings){
        context.settingsStore.updateData {
            setSettings(it)
        }

    }
    suspend fun updateSettings(settings: Settings){
        context.settingsStore.updateData {
            settings
        }
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "accountInfo")

}