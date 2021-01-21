package com.liflymark.normalschedule.ui.import_course

import android.media.Image
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.ui.Course

class CourseViewModel: ViewModel() {
    private val getImageTimesLiveData = MutableLiveData<Int>()

    private var id = Repository.getId().value.toString()
    private var formMapLiveData = MutableLiveData<MutableMap<String, String>>()

    val imageLiveData = Transformations.switchMap(getImageTimesLiveData) {
        Repository.getCaptcha(id)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    val courseLiveData = Transformations.switchMap(formMapLiveData) { formMap ->
        Repository.getCourse(formMap.getOrDefault("user", ""),
                            formMap.getOrDefault("password", ""),
                            formMap.getOrDefault("yzm", ""),
                            formMap.getOrDefault("headers", ""))
    }

    fun refreshId() {
        id = Repository.getId().value.toString()
    }

    fun getImageFirst() {
        getImageTimesLiveData.value = 0
    }

    fun getImageAgain() {
        getImageTimesLiveData.value = getImageTimesLiveData.value?.plus(1)
    }

    fun postForm(user: String, password: String, yzm: String, headers: String) {
        formMapLiveData.value?.set("user", user)
        formMapLiveData.value?.set("password", password)
        formMapLiveData.value?.set("yzm", yzm)
        formMapLiveData.value?.set("headers", headers)
    }
}