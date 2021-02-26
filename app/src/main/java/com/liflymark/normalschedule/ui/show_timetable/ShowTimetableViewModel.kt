package com.liflymark.normalschedule.ui.show_timetable

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.CourseBean
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import com.liflymark.normalschedule.logic.model.AllCourse

class ShowTimetableViewModel: ViewModel() {
    private var courseListLiveData = MutableLiveData(0)
    private val myHandler = SavedStateHandle()
    val TEXTVIEW_WIDTH = "TEXTVIEW_WIDTH"
    private var courseDatabaseLiveData = MutableLiveData(0)

    val courseDatabaseLiveDataVal = Transformations.switchMap(courseDatabaseLiveData) {
        Repository.loadAllCourse()
    }

    fun loadAllCourse() {
        courseDatabaseLiveData.value = courseDatabaseLiveData.value?.plus(1)
    }

    fun saveTextWidth(width: Float) {
        myHandler.set(TEXTVIEW_WIDTH, width)
    }


    suspend fun insertOriginalCourse(allCourseList: List<AllCourse>) {
        Log.d("CourseViewModel", "获取到课程")
        Repository.insertCourse2(allCourseList)
    }
}