package com.liflymark.normalschedule.ui.import_course

import android.media.Image
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.model.CourseResponse
import com.liflymark.normalschedule.ui.Course

class CourseViewModel: ViewModel() {
    private var id = ""
    private var user = ""
    private var password = ""
    private var yzm = ""
    private var formMapLiveData = MutableLiveData<String>()
    private var getIdOrNotLiveData = MutableLiveData<Int>(0)
    private var getImageTimesLiveData = MutableLiveData(0)

    val idLiveData = Transformations.switchMap(getIdOrNotLiveData) {
        Repository.getId()
    }
    val courseLiveData = Transformations.switchMap(formMapLiveData) { _ ->
        Repository.getCourse(user, password, yzm, id)
    }
    val imageLiveData = Transformations.switchMap(getImageTimesLiveData) {
        Repository.getCaptcha(id)
    }

    fun getId() {
        getIdOrNotLiveData.value = 1
        getImageTimesLiveData.value = 0
    }

    fun refreshId() {
        if (getIdOrNotLiveData.value!! >= 1){
            getIdOrNotLiveData.value = getIdOrNotLiveData.value!! +1
        } else {
            return
        }
    }

    fun getImage(id: String) {
        val temp = getIdOrNotLiveData.value?.toInt()
        this.id = id
        if (temp != null) {
            getImageTimesLiveData.value = temp + 1
        }
    }

    fun putValue(user: String, password: String, yzm: String, id: String) {
        this.user = user
        this.password = password
        this.id = id
        if (yzm == ""){
            this.yzm = "abcde"
        }else {
            this.yzm = yzm
        }
        formMapLiveData.value = user + password + yzm
    }
}