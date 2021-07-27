package com.liflymark.normalschedule.ui.score_detail

import android.util.Log
import androidx.lifecycle.*
import com.liflymark.normalschedule.logic.Repository

class LoginToScoreViewModel:ViewModel() {
    var id = ""
    private var user = ""
    private var password = ""
    private var getIdOrNotLiveData = MutableLiveData(0)
    private var formMapLiveData = MutableLiveData<Map<String, String>>()

    val scoreDetailState = Transformations.switchMap(formMapLiveData){
        Repository.getScoreDetail2(it["user"]!!, it["password"]!!, it["id"]!!).asLiveData()
    }

    val idLiveData = Transformations.switchMap(getIdOrNotLiveData) {
        Repository.getId3().asLiveData()
    }
//    val scoreLiveData = Transformations.switchMap(formMapLiveData) { _ ->
//        Repository.getScore(user, password, id)
//    }

    fun getId() {
        getIdOrNotLiveData.value = getIdOrNotLiveData.value?.plus(1)
        Log.d("LoginViewModel", "getID")
    }


    fun putValue(user: String, password: String,id: String) {
        this.user = user
        this.password = password
        this.id = id
        Log.d("LoginViewModel", "putValue运行")
        formMapLiveData.value = mapOf("user" to user, "password" to password, "id" to id)
    }


    fun saveAccount(user: String, password: String) = Repository.saveAccount(user, password)
    fun getSavedAccount() = Repository.getSavedAccount()
    fun isAccountSaved() = Repository.isAccountSaved()
}