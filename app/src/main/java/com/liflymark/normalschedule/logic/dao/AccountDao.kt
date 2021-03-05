package com.liflymark.normalschedule.logic.dao

import android.content.Context
import com.google.gson.Gson
import androidx.core.content.edit
import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.logic.model.IdResponse

/*
* 此功能在未对密码进行md5加密时不建议使用
* */

object AccountDao {
    fun saveAccount(user: String, password: String){
        sharedPreferences().edit {
            putString("user", user)
            putString("password", password)
        }
    }

    fun getSavedAccount(): Map<String?, String?> {
        val user = sharedPreferences().getString("user", "")
        val password = sharedPreferences().getString("password", "")
        return mapOf("user" to user, "password" to password)
    }

    fun isAccountSaved() = sharedPreferences().contains("user")

    private fun sharedPreferences() = NormalScheduleApplication.context.getSharedPreferences("normal_schedule", Context.MODE_PRIVATE)
}