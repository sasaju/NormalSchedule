package com.liflymark.normalschedule.ui.set_background

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.UserBackgroundBean

class DefaultBackgroundViewModel:ViewModel() {
    var userBackgroundUri = ""
    val pathState =
        Repository.loadBackground()

    suspend fun updateBackground(){
        Log.d("DefaultView", "update")
        Repository.updateBackground(UserBackgroundBean(userBackgroundUri))
    }

}