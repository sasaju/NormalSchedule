package com.liflymark.normalschedule.ui.set_background

import android.util.Log
import androidx.lifecycle.ViewModel
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.UserBackgroundBean

class DefaultBackgroundViewModel:ViewModel() {
    var userBackgroundUri = ""

    suspend fun updateBackground(){
        Log.d("DefaultView", "update")
        Repository.updateBackground(UserBackgroundBean(userBackgroundUri))
    }

}