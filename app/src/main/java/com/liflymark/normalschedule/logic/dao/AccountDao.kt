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
    fun saveAccount(account: IdResponse){
        sharedPreferences().edit {
            putString("account", Gson().toJson(account))
        }
    }

    fun getSavedAccount(): IdResponse {
        val idJson = sharedPreferences().getString("account", "")
        return Gson().fromJson(idJson, IdResponse::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("account")

    private fun sharedPreferences() = NormalScheduleApplication.context.getSharedPreferences("normal_schedule", Context.MODE_PRIVATE)
}