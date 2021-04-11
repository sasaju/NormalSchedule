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

class ShowTimetableViewModel: ViewModel() {
    private var courseListLiveData = MutableLiveData(0)
    private val myHandler = SavedStateHandle()
    val TEXTVIEW_WIDTH = "TEXTVIEW_WIDTH"
    private var courseDatabaseLiveData = MutableLiveData(0)
    private var backgroundId = MutableLiveData(0)
    private var updateCourseLiveData = MutableLiveData(0)
    private var needDeleteCourseNameLiveData = MutableLiveData("")
    private val saveClassToSQLLiveData = MutableLiveData<Boolean>()
    private var deleteCourseBean = MutableLiveData<Int>()


    val courseDatabaseLiveDataVal = Transformations.switchMap(courseDatabaseLiveData) {
        Repository.loadAllCourse()
    }


    val backgroundUriStringLiveData = Transformations.switchMap(backgroundId){
        Repository.loadBackground()
    }

    val deleteCourseBeanByNameLiveData = Transformations.switchMap(needDeleteCourseNameLiveData){
        needDeleteCourseNameLiveData.value?.let { it1 -> Repository.deleteCourseByName(it1) }
    }

    val deleteCourseLiveData = Transformations.map(deleteCourseBean){
        Repository.deleteAllCourseBean()
        Log.d("CourseViewModel", "执行完毕")
    }

    val updateCourseLiveDataVal = Transformations.switchMap(updateCourseLiveData) {
        Repository.loadAllCourse()
    }

    val saveClassOrNot = Transformations.map(saveClassToSQLLiveData){
        true
    }

    fun loadAllCourse() {
        courseDatabaseLiveData.value = courseDatabaseLiveData.value?.plus(1)
    }

    fun updateCourse(){
        updateCourseLiveData.value?.plus(1)
    }

    fun deleteCourse(courseItem:String){
        needDeleteCourseNameLiveData.value = courseItem
    }

    fun saveTextWidth(width: Float) {
        myHandler.set(TEXTVIEW_WIDTH, width)
    }

    fun setBackground(){
        Log.d("ShowViewModel", "setback")
        backgroundId.value = backgroundId.value?.plus(1)
    }

    fun deleteAllCourseBean(){
        deleteCourseBean.value = deleteCourseBean.value?.plus(1)
    }

    fun savedClass(){
        saveClassToSQLLiveData.value = true
    }

    suspend fun loadCourseByNameAndStart(courseName: String, courseStart: Int, whichColumn: Int) =
            Repository.loadCourseByNameAndStart(courseName, courseStart, whichColumn)

    fun saveAccount(user: String, password: String) = AccountDao.saveAccount(user, password)
    fun getSavedAccount() = AccountDao.getSavedAccount()
    fun isAccountSaved() = AccountDao.isAccountSaved()

    fun saveUserVersion() = Repository.saveUserVersion()
    fun getNewUserOrNot() = Repository.getNewUserOrNot()

    suspend fun insertOriginalCourse(allCourseList: List<AllCourse>) {
        Log.d("CourseViewModel", "获取到课程")
        Repository.insertCourse2(allCourseList)
    }

    suspend fun deleteAllCourse(){
        Repository.deleteAllCourseBean2()
    }

    fun getClassDetailDialog(_context: Context, courseBean: CourseBean): MaterialDialog {
        Log.d("dialog", 123456.toString())
        val dialog = MaterialDialog(_context)
                .customView(R.layout.item_course_detail)
        Log.d("dialog", 123456.toString())
        val customView = dialog.getCustomView()
        Log.d("dialog", 123456.toString())
        val courseTime = customView.findViewById<AppCompatTextView>(R.id.et_time)
        val weekNum = customView.findViewById<AppCompatTextView>(R.id.et_weeks)
        val courseTeacher = customView.findViewById<AppCompatTextView>(R.id.et_teacher)
        val courseRoom = customView.findViewById<AppCompatTextView>(R.id.et_room)

        val oneList = courseBean.classWeek.whichIs1()
        val courseStartToEnd = {
            "    第${courseBean.classSessions} - ${courseBean.classSessions+courseBean.continuingSession}节"
        }


        courseTime.text = getWeekNumFormat(oneList)
        weekNum.text = when(courseBean.classDay){
            1 -> "周一$courseStartToEnd"
            2 -> "周二$courseStartToEnd"
            3 -> "周三$courseStartToEnd"
            4 -> "周四$courseStartToEnd"
            5 -> "周五$courseStartToEnd"
            6 -> "周六$courseStartToEnd"
            7 -> "周日$courseStartToEnd"
            else -> "错误"
        }
        courseTeacher.text = courseBean.teacher
        courseRoom.text = courseBean.teachingBuildName

        val closeButton = customView.findViewById<AppCompatTextView>(R.id.ib_delete)
//        closeButton.setOnClickListener {
//            dialog.dismiss()
//        }

        return dialog
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