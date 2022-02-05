package com.liflymark.normalschedule.ui.login_space_room

import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.*
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.model.IdResponse
import com.liflymark.normalschedule.logic.model.SpaceLoginResponse

class LoginSpaceViewModel:ViewModel() {
    var ids = ""
    val initialResultId = IdResponse("")
    val initialSpace = SpaceLoginResponse("初始化")
    private var user = ""
    private var password = ""
    var nums = 0
    private var getIdOrNotLiveData = MutableLiveData<Int>()
    private var formMapLiveData = MutableLiveData<Map<String, String>>()
    init {
        Log.d("LoginSpace", "init")
    }
    val loginSpaceLiveData = Transformations.switchMap(formMapLiveData){ map ->
        Repository.loginToSpace(map["user"]!!, map["password"]!!, map["id"]!!).asLiveData().map {
            if (it.result!="登陆成功"){it.result += nums}
            it
        }
    }

    val idLiveData = Transformations.switchMap(getIdOrNotLiveData) {
        Repository.getId4()
    }

    fun getId() {
        getIdOrNotLiveData.value = 1
    }


    fun putValue(user: String, password: String,id: String) {
        this.user = user
        this.password = password
        nums+=1
        formMapLiveData.value = mapOf("user" to user, "password" to password, "id" to id)
    }


    fun saveAccount() = Repository.saveAccount(user, password)
    fun getSavedAccount() = Repository.getSavedAccount()
    fun isAccountSaved() = Repository.isAccountSaved()
}