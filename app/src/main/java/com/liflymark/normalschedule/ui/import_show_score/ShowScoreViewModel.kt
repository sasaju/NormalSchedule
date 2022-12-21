package com.liflymark.normalschedule.ui.import_show_score

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.liflymark.normalschedule.logic.model.Grade
import com.liflymark.normalschedule.logic.utils.Convert
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class ShowScoreViewModel():ViewModel() {
    var gradeString = ""
    val allGradeList = mutableStateListOf<Grade>()
    fun setGradeStringOrNot(gradeStringOrEmpty: String){
        if (gradeStringOrEmpty != "")
            allGradeList.addAll(Convert.jsonToAllGrade(gradeStringOrEmpty))
    }
    val showTipsTime = flow<Int> {
        repeat(8){
            delay(1000)
            emit(it)
        }
    }

}