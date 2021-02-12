package com.liflymark.normalschedule.logic.dao

import android.content.Context
import com.google.gson.Gson
import androidx.core.content.edit
import com.liflymark.normalschedule.NormalScheduleApplication

/*
* 此功能在未对密码进行md5加密时不建议使用
* */

object AccountDao {
    fun saveAccount(user:String, password: String){
        sharedPreferences().edit {
            putString("user", Gson().toJson(user))
            putString("password", Gson().toJson(password))
        }
    }

    private fun sharedPreferences() = NormalScheduleApplication.context.getSharedPreferences("normal_schedule", Context.MODE_PRIVATE)
}