package com.liflymark.normalschedule.ui.show_timetable

import androidx.lifecycle.ViewModel
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.CourseBean

class ShowTimetableViewModel: ViewModel() {
    fun loadAllCourse(): List<CourseBean> {
        return Repository.loadAllCourse()
    }
}