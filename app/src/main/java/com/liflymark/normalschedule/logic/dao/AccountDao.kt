package com.liflymark.normalschedule.logic.dao

import android.content.Context
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.NormalScheduleApplication.Companion.context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object AccountDao {
    private const val sharedPrefsFile = "userAccount"
    private val mainKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    fun saveAccount(user: String, password: String){
        with (pwSharedPreferences().edit()) {
            putString("passwordEncrypt", password)
            apply()
        }
        with(normalSharePreferences().edit()) {
            putString("userYes", user)
            putBoolean("loginOrNot", true)
            commit()
        }
    }

    fun saveLogin(){
        with(normalSharePreferences().edit()) {
            putBoolean("loginOrNot", true)
            apply()
        }
    }

    fun getSavedAccount(): Map<String, String> {
        val user = normalSharePreferences().getString("userYes", "")!!
        val password =  pwSharedPreferences().getString("passwordEncrypt", "")!!
        return mapOf("user" to user, "password" to password)
    }

    fun isAccountSaved() = normalSharePreferences().getBoolean("loginOrNot", false)

    fun newUserShowed(){
        normalSharePreferences().edit(){
            putInt("version", 1)
            commit()
        }
    }

    fun getNewUserOrNot(): Boolean {
        val userVersion = normalSharePreferences().getInt("version", 0)
        return userVersion < 1
    }

    fun importedAgain(){

        with(normalSharePreferences().edit()) {
            putBoolean("loginOrNot", false)
            commit()
        }
    }

//    private fun sharedPreferences() = NormalScheduleApplication.context.getSharedPreferences("normal_schedule", Context.MODE_PRIVATE)
    private fun pwSharedPreferences() = EncryptedSharedPreferences.create(
        context,
        sharedPrefsFile,
        mainKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private fun normalSharePreferences() = context.getSharedPreferences("normal_schedule", Context.MODE_PRIVATE)


    /**
     * 以下为空教室存储Dao
     */
    private val Context.spaceStore: DataStore<Preferences> by preferencesDataStore(name = "spaceroom")
    val SCHOOL_KEY = stringPreferencesKey("school")
    val BUILDING_KEY = stringPreferencesKey("building")

    fun readSelected(): Flow<Map<String, String>> {
        val readFlow: Flow<Map<String, String>> = context.spaceStore.data
            .map { preferences ->
                // No type safety.
                val school = preferences[SCHOOL_KEY] ?: "五四路校区"
                val building = preferences[BUILDING_KEY] ?: "六教"
                mapOf(SCHOOL_KEY.name to school, BUILDING_KEY.name to building)
            }
        return readFlow
    }

    suspend fun saveSelected(school:String, building:String){
        context.spaceStore.edit { settings ->
            settings[SCHOOL_KEY] = school
            settings[BUILDING_KEY] = building
        }
    }

}