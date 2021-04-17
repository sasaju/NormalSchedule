package com.liflymark.normalschedule.logic.dao

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.liflymark.normalschedule.NormalScheduleApplication

/*
* 此功能在未对密码进行md5加密时不建议使用
* */

object AccountDao {
    private const val sharedPrefsFile = "userAccount"
    private val mainKey = MasterKey.Builder(NormalScheduleApplication.context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    fun saveAccount(user: String, password: String){
        with (sharedPreferences().edit()) {
            putString("passwordEncrypt", password)
            apply()
        }
        with(normalSharePreferences().edit()) {
            putString("userYes", user)
            commit()
        }
    }

    fun getSavedAccount(): Map<String?, String?> {
        val user = normalSharePreferences().getString("userYes", "")
        val password =  sharedPreferences().getString("passwordEncrypt", "")
        return mapOf("user" to user, "password" to password)
    }

    fun isAccountSaved() = normalSharePreferences().contains("userYes")

    fun newUserShowed(){
        normalSharePreferences().edit(){
            putInt("version", 1)
        }
    }

    fun getNewUserOrNot(): Boolean {
        val userVersion = normalSharePreferences().getInt("version", 0)
        return userVersion < 1
    }

    fun clearSharePreferences(){
        with(sharedPreferences().edit()){
            clear()
            apply()
        }
        with(normalSharePreferences().edit()){
            clear()
            apply()
        }
    }

//    private fun sharedPreferences() = NormalScheduleApplication.context.getSharedPreferences("normal_schedule", Context.MODE_PRIVATE)
    private fun sharedPreferences() = EncryptedSharedPreferences.create(
        NormalScheduleApplication.context,
        sharedPrefsFile,
        mainKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private fun normalSharePreferences() = NormalScheduleApplication.context.getSharedPreferences("normal_schedule", Context.MODE_PRIVATE)
}