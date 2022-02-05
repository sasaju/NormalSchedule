package com.liflymark.normalschedule.ui.exam_arrange

import android.util.Log
import androidx.lifecycle.*
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.utils.CalendarUtil

class LoginToExamViewModel:ViewModel() {
    var ids = ""
    private var user = ""
    private var password = ""
    private var num = 0
    private var formMapLiveData = MutableLiveData<Int>()

    val arrangeLiveData = Transformations.switchMap(formMapLiveData){ nums ->
        Repository. getExamArrange(user, password, ids).asLiveData().map {
            if (it.result!="登陆成功"){it.result += nums}
            it
        }
    }

    fun putValue(user: String, password: String) {
        this.user = user
        this.password = password
        this.num += 1
        formMapLiveData.value = num
    }

    fun saveAccount() = Repository.saveAccount(this.user, this.password)
}