package com.liflymark.normalschedule.ui.add_course

import android.support.v4.app.INotificationSideChannel
import android.text.Editable
import android.util.Log
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.model.AllCourse

class AddCourseActivityViewModel: ViewModel() {
    private var startEndClassTimeLiveData = MutableLiveData<List<Int>>()
    private var classTimeExistOrNot = false
    private var classWeekAndSectionExistOrNot = false
    private var classWeekAndSection = mutableListOf<Int>()

    val updateUiLiveData = Transformations.map(startEndClassTimeLiveData) { startEndList ->
        "第 ${startEndList[0]} - ${startEndList[1]} 周"
    }

    fun setClassTime(startEditText: EditText, endEditText: EditText): String {
        return if (startEditText.text.toString() == "" || endEditText.text.toString() == ""){
            "周数不可为空"
        } else {
            val startEditTextInt = startEditText.text.toString().toInt()
            val endEditTextInt = endEditText.text.toString().toInt()
            if (startEditTextInt > endEditTextInt){
                "起始周不得大于结束周"
            } else if (startEditTextInt < 1 || endEditTextInt > 19){
                "起始周不得小于1\n结束数周不得超过19"
            } else {
                startEndClassTimeLiveData.value = listOf(startEditTextInt, endEditTextInt)
                classTimeExistOrNot = true
                "设置成功"
            }
        }
    }

    fun setClassWeek(week:Int, startSection: Int, endSection: Int): String {
        val allWeek = listOf("周一","周二","周三","周四","周五","周六","周日",)
        classWeekAndSection.apply {
            add(week)
            add(startSection)
            if (startSection > endSection){
                add(startSection)
            } else{
                add(endSection)
            }
        }
        classWeekAndSectionExistOrNot = true
        if (startSection > endSection)
            return "${allWeek[week]}    第${startSection+1} - ${startSection+1} 节"
        return "${allWeek[week]}    第${startSection+1} - ${endSection+1} 节"
    }

    suspend fun insertCourse(courseName:EditText, teacher:EditText, buildName: EditText): String {
        val sqlTeacher = when(teacher.text.toString()){
            "" -> " "
            else -> teacher.text.toString()
        }
        val sqlBuilding = when(buildName.text.toString()){
            "" -> " "
            else -> buildName.text.toString()
        }
        if (classTimeExistOrNot && classWeekAndSectionExistOrNot){
            val allCourse = AllCourse(
                    courseName = courseName.text.toString(),
                    classDay = classWeekAndSection[0]+1,
                    classSessions = classWeekAndSection[1]+1,
                    classWeek = to01(startEndClassTimeLiveData.value!![0], startEndClassTimeLiveData.value!![1]),
                    continuingSession = classWeekAndSection[2] - classWeekAndSection[1] +1,
                    teacher = sqlTeacher,
                    teachingBuildName = sqlBuilding,
                    campusName = ""
            )
            Repository.insertCourse2(listOf(allCourse))
            return "更新成功，重启生效"
        }
        return "更新失败，请检查输入是否正确"
    }

    private fun to01(startWeek: Int, endWeek: Int): String {
        var result = ""
        for (i in 1..24){
            result += if (i in startWeek .. endWeek){
                "1"
            } else{
                "0"
            }
            Log.d("AddCourseModel", result)
        }
        return result
    }
}