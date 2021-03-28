package com.liflymark.normalschedule.ui.course_detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.liflymark.normalschedule.logic.Repository

class CourseDetailViewModel : ViewModel() {
    private var courseName = ""
    private var weekNum = ""
    private var allWeekNum = ""
    private var sessionNum = ""
    private val _needSelectCourseNameLiveData = MutableLiveData<String>()

    val resultCourseList  = Transformations.switchMap(_needSelectCourseNameLiveData){
        Repository.loadCourseByName(courseName)
    }

    fun putCourseBeanName(courseName: String){
        this.courseName = courseName
        _needSelectCourseNameLiveData.value = this.courseName
    }
}