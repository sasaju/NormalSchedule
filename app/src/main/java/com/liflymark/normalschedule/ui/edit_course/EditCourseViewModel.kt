package com.liflymark.normalschedule.ui.edit_course

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.CourseBean
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class EditCourseViewModel:ViewModel() {
    val initCourseBean = listOf(CourseBean(
        campusName="五四路校区",
        classDay=3,
        classSessions=9, classWeek="111111111111110000000000", continuingSession=3,
        courseName="未知",
        teacher="未知 ",
        teachingBuildName="未知",
        color="#f0c239"
    ))
    private val deleteClass = mutableListOf<CourseBean>()
    private val newClass = mutableListOf<CourseBean>()

    fun loadCourseByName(courseName: String) = Repository.loadCourseByName2(courseName)

    fun addDeleteClass(beanList: List<CourseBean>){
        deleteClass.addAll(beanList)
    }

    fun addNewClass(beanList:List<CourseBean>){
        Log.d("Edit", beanList[0].courseName)
        newClass.addAll(beanList)
    }

    suspend fun updateClass(){
        Repository.deleteCourseByList(deleteClass)
        Repository.insertCourse(newClass)
    }
}