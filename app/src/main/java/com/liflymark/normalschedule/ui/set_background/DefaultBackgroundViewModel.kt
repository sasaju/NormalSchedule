package com.liflymark.normalschedule.ui.set_background

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.UserBackgroundBean
import kotlinx.coroutines.launch

class DefaultBackgroundViewModel:ViewModel() {
    var userBackgroundUri = ""
    val pathState = mutableStateOf(Uri.parse(""))
    init {
        viewModelScope.launch {
            pathState.value = Repository.loadBackground2()
        }
    }

    suspend fun updateBackground(){
        Log.d("DefaultView", "update")
        Repository.updateBackground(UserBackgroundBean(userBackgroundUri))
        pathState.value = Repository.loadBackground2()
    }

}