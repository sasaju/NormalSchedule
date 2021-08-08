package com.liflymark.normalschedule.logic.dao

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.logic.network.NormalScheduleNetwork
import kotlinx.coroutines.flow.Flow
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
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "accountInfo")
}