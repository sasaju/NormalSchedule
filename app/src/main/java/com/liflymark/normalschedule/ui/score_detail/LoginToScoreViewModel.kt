package com.liflymark.normalschedule.ui.score_detail

import android.util.Log
import androidx.lifecycle.*
import com.liflymark.normalschedule.logic.Repository

class LoginToScoreViewModel:ViewModel() {
    var ids = ""
    private var user = ""
    private var password = ""
    private var nums = 0
    private var getIdOrNotLiveData = MutableLiveData(0)
    private var formMapLiveData = MutableLiveData<Map<String, String>>()

    val scoreDetailState = Transformations.switchMap(formMapLiveData){ map ->
        Repository.getScoreDetail(map["user"]!!, map["password"]!!, map["id"]!!).asLiveData().map {
            if (it.result!="登陆成功"){it.result += nums}
            it
        }
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
        this.ids = id
        Log.d("LoginViewModel", "putValue运行")
        formMapLiveData.value = mapOf("user" to user, "password" to password, "id" to id)
    }
    fun putValue(user: String, password: String) {
        this.user = user
        this.password = password
        nums += 1
        formMapLiveData.value = mapOf("user" to user, "password" to password, "id" to ids)
    }

    fun saveAccount(user: String, password: String) = Repository.saveAccount(user, password)
    fun getSavedAccount() = Repository.getSavedAccount()
    fun isAccountSaved() = Repository.isAccountSaved()
}