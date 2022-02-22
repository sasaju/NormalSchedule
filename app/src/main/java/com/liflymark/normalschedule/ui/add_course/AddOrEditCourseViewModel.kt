package com.liflymark.normalschedule.ui.add_course

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.utils.Convert
import kotlinx.coroutines.launch

class AddOrEditCourseViewModel(courseName:String):ViewModel() {
    val courseListState = mutableStateListOf<CourseBean>()
    val courseColorIndex = mutableStateOf(-1)
    private val deleteList:MutableList<CourseBean> = mutableStateListOf()

    init {
        courseListState.clear()
        viewModelScope.launch {
            val list = Repository.loadCourseByName(courseName)
            courseColorIndex.value = if (list.getOrNull(0) != null){list[0].colorIndex}else{-1}
            courseListState.addAll(list)
            deleteList.addAll(list.map { it.copy() })
        }
    }

    suspend fun saveChange(courseNewName: String, colorIndex: Int){
        Repository.deleteCourseByList(deleteList)
        Log.d("AddOrEditCourse", "delete:"+deleteList.size.toString()+deleteList.toList().toString())
        courseListState.map {
            it.courseName = courseNewName
            it.colorIndex =if (colorIndex==-1){ Convert.courseNameToIndex(courseNewName,13) }else{ colorIndex }
        }
        Repository.insertCourse(courseListState)
        Log.d("AddOrEditCourse", "add:"+courseListState.size.toString()+courseListState.toList().toString())

    }
}