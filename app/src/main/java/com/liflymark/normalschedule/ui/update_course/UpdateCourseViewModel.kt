package com.liflymark.normalschedule.ui.update_course

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.liflymark.normalschedule.logic.Repository

class UpdateCourseViewModel : ViewModel() {
    private val uploadMutable = MutableLiveData<Int>()
    var courseNames:List<String>? = null
    var userCode = ""
    var userNumber =""
    val uploadResponse = Transformations.switchMap(uploadMutable){
        Repository.uploadNewCourse(courseNames, userNumber, userCode).asLiveData()
    }
    fun uploadCourse(
        courseNames: List<String>?,
        userNumber: String,
        userCode: String
    ) {
        this.courseNames = courseNames
        this.userCode = userCode
        this.userNumber = userNumber
        if (uploadMutable.value == null){
            uploadMutable.value = 0
        }else{
            uploadMutable.value!!.plus(1)
        }
    }
}