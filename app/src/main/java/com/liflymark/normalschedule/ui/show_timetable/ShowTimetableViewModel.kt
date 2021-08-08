package com.liflymark.normalschedule.ui.show_timetable

import android.content.Context
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.*
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.dao.AccountDao
import com.liflymark.normalschedule.logic.model.AllCourse
import com.liflymark.normalschedule.logic.model.OneSentencesResponse
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShowTimetableViewModel: ViewModel() {
    private var courseDatabaseLiveData = MutableLiveData(0)
    private var backgroundId = MutableLiveData(0)
    private var needDeleteCourseNameLiveData = MutableLiveData<String>()
    private var _sentenceLiveDate = MutableLiveData<Boolean>(false)
    var showToast = 0

    val newUserFLow = Repository.getNewUserOrNot()
    val courseDatabaseLiveDataVal = Transformations.switchMap(courseDatabaseLiveData) {
        Repository.loadAllCourse2()
    }


    val backgroundUriStringLiveData = Transformations.switchMap(backgroundId){
        Repository.loadBackground()
    }

    val deleteCourseBeanByNameLiveData = Transformations.switchMap(needDeleteCourseNameLiveData){
        needDeleteCourseNameLiveData.value?.let { it1 -> Repository.deleteCourseByName(it1) }
    }
    val sentenceLiveData = Transformations.switchMap(_sentenceLiveDate){
        Repository.getSentences(it).asLiveData()
    }


    fun loadAllCourse() {
        courseDatabaseLiveData.value = courseDatabaseLiveData.value?.plus(1)
        Log.d("ShowTimetable", "loadAllCourse执行")
    }

    fun deleteCourse(courseItem:String){
        needDeleteCourseNameLiveData.value = courseItem
    }

    fun setBackground(){
        Log.d("ShowViewModel", "setback")
        backgroundId.value = backgroundId.value?.plus(1)
    }


    suspend fun insertOriginalCourse(allCourseList: List<AllCourse>) {
        Log.d("CourseViewModel", "获取到课程")
        Repository.insertCourse2(allCourseList)
    }

    suspend fun deleteAllCourse(){
        Repository.deleteAllCourseBean2()
    }


    fun mergeClass(className: String,
                   classDay: Int,
                   classSessions: Int,
                   continuingSession: Int,
                   buildingName: String){
        viewModelScope.launch {
            var classWeekResult = Integer.parseInt("000000000000000000000000", 2)
            var teacher = ""
            val classList = Repository.loadCourseUnTeacher(className, classDay, classSessions, continuingSession, buildingName)
            for (singleBean in classList){
                val singleBeanWeek = Integer.parseInt(singleBean.classWeek,2)
                classWeekResult = singleBeanWeek or classWeekResult
                teacher += "${singleBean.teacher} "
            }

            var classWeekResultStr = Integer.toBinaryString(classWeekResult)
            val length = classWeekResultStr.length
            if (classWeekResultStr.length < 24){
                repeat(24-length){
                    classWeekResultStr = "0$classWeekResultStr"
                }
            }
            val newBean = CourseBean(
                campusName = "河北大学",
                classDay = classDay,
                classSessions = classSessions,
                classWeek = classWeekResultStr,
                continuingSession = continuingSession,
                courseName = className,
                teacher = teacher,
                teachingBuildName = buildingName,
                color = Convert.stringToColor(className)
            )
            if (Integer.parseInt("000000000000000000000000", 2)!=classWeekResult) {
                Repository.insertCourse(newBean)
                Repository.deleteCourseByList(classList)
//                loadAllCourse()
            }
        }
    }

    fun mergeClass(className: String,
                   classDay: Int,
                   classSessions: Int,
                   continuingSession: Int){
        viewModelScope.launch {
            var classWeekResult = Integer.parseInt("000000000000000000000000", 2)
            var teacher = ""
            val classList = Repository.loadCourseUnTeacher(className, classDay, classSessions, continuingSession)
            for (singleBean in classList){
                val singleBeanWeek = Integer.parseInt(singleBean.classWeek,2)
                classWeekResult = singleBeanWeek or classWeekResult
                teacher += "${singleBean.teacher} "
            }

            var classWeekResultStr = Integer.toBinaryString(classWeekResult)
            val length = classWeekResultStr.length
            if (classWeekResultStr.length < 24){
                repeat(24-length){
                    classWeekResultStr = "0$classWeekResultStr"
                }
            }
            val newBean = CourseBean(
                campusName = "河北大学",
                classDay = classDay,
                classSessions = classSessions,
                classWeek = classWeekResultStr,
                continuingSession = continuingSession,
                courseName = className,
                teacher = teacher,
                teachingBuildName = "多个教室",
                color = Convert.stringToColor(className)
            )
            if (Integer.parseInt("000000000000000000000000", 2)!=classWeekResult) {
                Repository.insertCourse(newBean)
                Repository.deleteCourseByList(classList)
//                loadAllCourse()
            }
        }
    }

    fun fetchSentence(force:Boolean = false) {
        _sentenceLiveDate.value = force
    }


    fun getNowWeek(): Int{
        return  GetDataUtil.whichWeekNow()
    }

    fun startSchool(): Boolean{
        return GetDataUtil.startSchool()
    }

    fun startSchoolDay(): Int{
        return GetDataUtil.startSchoolDay()
    }

    fun startHoliday(): Boolean{
        return GetDataUtil.whichWeekNow() > 19
    }

    private fun String.whichIs1(): List<Int>{
        val oneList = mutableListOf<Int>()
        for (i in this.indices){
            when(this[i].toString()){
                "1" -> oneList.add(i+1)
                else -> Log.d("Dialog", "异常")
            }
        }
        return oneList
    }

    private fun getWeekNumFormat(oneList: List<Int>): String {
        var courseTimeFormat = "第"
        var type = 0
        for (i in oneList.indices-1){
            if (i == 0){
                continue
            }
            type = if (oneList[i+1]-oneList[i] == 1 && oneList[i]-oneList[i-1] == 1) {
                // 连续
                0
            } else if (oneList[i+1]-oneList[i] == 2 && oneList[i]-oneList[i-1] == 2){
                // 单周或双周
                1
            } else {
                // 未知
                2
            }
        }
        when (type){
            0 ->{
                courseTimeFormat += " ${oneList.first() - oneList.last()} 周"
            }
            1 ->{
                courseTimeFormat += if (oneList.first() % 2 == 1) {
                    "${oneList.first() - oneList.last()} 单周"
                } else {
                    "${oneList.first() - oneList.last()} 双周"
                }
            }
            2 ->{
                for (i in oneList){
                    courseTimeFormat += "$i,"
                }
                courseTimeFormat += "周"
            }
        }
        return courseTimeFormat
    }
}