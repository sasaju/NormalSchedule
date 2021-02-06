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
    private val getImageTimesLiveData = MutableLiveData<Int>()

    private var id = ""
    private var user = ""
    private var password = ""
    private var yzm = ""
    private var formMapLiveData = MutableLiveData<String>()
    private var idLiveData = MutableLiveData<String>()

//    val imageLiveData = Transformations.switchMap(getImageTimesLiveData) {
//        Repository.getCaptcha(id)
//    }

    val courseLiveData = Transformations.switchMap(formMapLiveData) { _ ->
        this.id = Repository.getId().value.toString()
        Repository.getCourse(user, password, yzm, "asdfasfasdfasdf")
    }

    fun refreshId() {
        Log.d("ViewModel", this.id)
        this.id = Repository.getId().value.toString()
    }

    fun getImage() {
        Repository.getCaptcha(id)
    }

    fun putValue(user: String, password: String, yzm: String) {
        this.user = user
        this.password = password
        if (yzm == ""){
            this.yzm = "abcde"
        }else {
            this.yzm = yzm
        }
        formMapLiveData.value = user + password + yzm + id
    }
}