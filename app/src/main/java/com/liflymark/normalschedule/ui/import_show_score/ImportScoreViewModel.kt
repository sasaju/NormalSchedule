package com.liflymark.normalschedule.ui.import_show_score

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.liflymark.normalschedule.logic.Repository

class ImportScoreViewModel:ViewModel() {
    private var id = ""
    private var user = ""
    private var password = ""
    private var getIdOrNotLiveData = MutableLiveData<Int>(0)
    private var formMapLiveData = MutableLiveData<String>()

    val idLiveData = Transformations.switchMap(getIdOrNotLiveData) {
        Repository.getId()
    }
    val scoreLiveData = Transformations.switchMap(formMapLiveData) { _ ->
        Repository.getScore(user, password, id)
    }

    fun getId() {
        getIdOrNotLiveData.value = 1
    }

    fun refreshId() {
        if (getIdOrNotLiveData.value!! >= 1){
            getIdOrNotLiveData.value = getIdOrNotLiveData.value!! +1
        } else {
            return
        }
    }

    fun putValue(user: String, password: String,id: String) {
        this.user = user
        this.password = password
        this.id = id
        formMapLiveData.value = user + password
    }
}