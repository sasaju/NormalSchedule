package com.liflymark.normalschedule.logic.dao

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import androidx.core.content.edit
import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.logic.model.IdResponse
import com.liflymark.normalschedule.logic.utils.AESUtils

/*
* 此功能在未对密码进行md5加密时不建议使用
* */

object AccountDao {
    private val aes = AESUtils
    private var PASSWORD_STRING = "qws871bz73msl9x8"

    fun saveAccount(user: String, password: String){
        sharedPreferences().edit {
            putString("userYes", user)
            putString("password", aes.encrypt(PASSWORD_STRING, password))
            aes.encrypt(PASSWORD_STRING, password)?.let { Log.d("AccountDao", it) }
        }
    }

    fun getSavedAccount(): Map<String?, String?> {
        val user = sharedPreferences().getString("userYes", "")
        val password =  aes.decrypt(PASSWORD_STRING,sharedPreferences().getString("password", "")?:"")
        return mapOf("user" to user, "password" to password)
    }

    fun isAccountSaved() = sharedPreferences().contains("userYes")

    private fun sharedPreferences() = NormalScheduleApplication.context.getSharedPreferences("normal_schedule", Context.MODE_PRIVATE)
}