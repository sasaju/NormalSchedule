package com.liflymark.normalschedule.logic.dao

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.liflymark.normalschedule.NormalScheduleApplication

object AccountDao {
    private const val sharedPrefsFile = "userAccount"
    private val mainKey = MasterKey.Builder(NormalScheduleApplication.context)
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

    fun getSavedAccount(): Map<String?, String?> {
        val user = normalSharePreferences().getString("userYes", "")
        val password =  pwSharedPreferences().getString("passwordEncrypt", "")
        return mapOf("user" to user, "password" to password)
    }


    //重新导课可以后改为
    //fun isAccountSaved() = normalSharePreferences().getBoolean("loginOrNot", false)
    fun isAccountSaved() = normalSharePreferences().contains("userYes")

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

    fun clearSharePreferences(){
        with(pwSharedPreferences().edit()){
            clear()
            apply()
        }
        with(normalSharePreferences().edit()){
            clear()
            apply()
        }
        with(normalSharePreferences().edit()) {
            putBoolean("loginOrNot", false)
            commit()
        }
    }

//    private fun sharedPreferences() = NormalScheduleApplication.context.getSharedPreferences("normal_schedule", Context.MODE_PRIVATE)
    private fun pwSharedPreferences() = EncryptedSharedPreferences.create(
        NormalScheduleApplication.context,
        sharedPrefsFile,
        mainKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private fun normalSharePreferences() = NormalScheduleApplication.context.getSharedPreferences("normal_schedule", Context.MODE_PRIVATE)
}