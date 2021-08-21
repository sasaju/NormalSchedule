package com.liflymark.normalschedule.ui.login_space_room

import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.*
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.model.IdResponse
import com.liflymark.normalschedule.logic.model.SpaceLoginResponse

class LoginSpaceViewModel:ViewModel() {
    var id = ""
    val initialResultId = IdResponse("")
    val initialSpace = SpaceLoginResponse("初始化")
    private var user = ""
    private var password = ""
    private var getIdOrNotLiveData = MutableLiveData<Int>()
    private var formMapLiveData = MutableLiveData<Map<String, String>>()

    val loginSpaceLiveData = Transformations.switchMap(formMapLiveData){
        Repository.loginToSpace(it["user"]!!, it["password"]!!, it["id"]!!).asLiveData()
    }

    val idLiveData = Transformations.switchMap(getIdOrNotLiveData) {
        Repository.getId4()
    }

    fun getId() {
        getIdOrNotLiveData.value = 1
    }


    fun putValue(user: String, password: String,id: String) {
        formMapLiveData.value = mapOf("user" to user, "password" to password, "id" to id)
    }


    fun saveAccount(user: String, password: String) = Repository.saveAccount(user, password)
    fun getSavedAccount() = Repository.getSavedAccount()
    fun isAccountSaved() = Repository.isAccountSaved()
}