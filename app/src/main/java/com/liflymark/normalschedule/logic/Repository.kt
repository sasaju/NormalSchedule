package com.liflymark.normalschedule.logic

import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.liveData
import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.bean.UserBackgroundBean
import com.liflymark.normalschedule.logic.dao.AccountDao
import com.liflymark.normalschedule.logic.dao.AppDatabase
import com.liflymark.normalschedule.logic.model.AllCourse
import com.liflymark.normalschedule.logic.model.IdResponse
import com.liflymark.normalschedule.logic.network.NormalScheduleNetwork
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.Dialog
import kotlinx.coroutines.Dispatchers
import okhttp3.Dispatcher
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object  Repository {

    private val dataBase = AppDatabase.getDatabase(NormalScheduleApplication.context)
    private val courseDao = dataBase.courseDao()
    private val backgroundDao = dataBase.backgroundDao()

    fun getId() = fire(Dispatchers.IO) {
        val id =NormalScheduleNetwork.getId()
        if (id.id != "") {
            Result.success(id)
        } else {
            Result.failure(RuntimeException("Can't get id!"))
        }
    }

    fun getCaptcha(sessionId: String) = fire(Dispatchers.IO) {
        val img = NormalScheduleNetwork.getCaptcha(sessionId).bytes()
        val imgStream = BitmapFactory.decodeByteArray(img, 0, img.size)
        Result.success(imgStream)
    }

    fun getCourse(user:String, password:String, yzm:String, headers:String) = fire(Dispatchers.IO) {
        val courseResponse = NormalScheduleNetwork.getCourse(user, password, yzm, headers)
        Result.success(courseResponse)
    }

    fun getCourse(user: String, password: String) = fire(Dispatchers.IO){
        val courseResponse = NormalScheduleNetwork.getCourse(user, password)
        Result.success(courseResponse)
    }

    fun getVisitCourse() = fire(Dispatchers.IO){
        val courseResponse = NormalScheduleNetwork.getVisitCourse()
        Result.success(courseResponse)
    }

    fun insertCourse(courseList: List<AllCourse>) = fire(Dispatchers.IO) {
        for (singleCourse in courseList) {
            courseDao.insertCourse(Convert.courseResponseToBean(singleCourse))
            // Log.d("Repository", Convert().courseResponseToBean(singleCourse).toString())
        }

        Result.success("0")
    }

    fun loadAllCourse() = fire(Dispatchers.IO){
        Result.success(courseDao.loadAllCourse())
    }

    fun deleteCourseByName(courseName:String) = fire(Dispatchers.IO){
        courseDao.deleteCourseByName(courseName)
        Result.success("0")
    }

    fun loadCourseByName(courseName: String) = fire(Dispatchers.IO){
        val courseList = courseDao.loadCourseByName(courseName)
        Result.success(courseList)
    }

    fun deleteAllCourseBean() = fire(Dispatchers.IO){
        courseDao.deleteAllCourseBean()
        Log.d("CourseViewModel", "执行完毕")
        Result.success("0")
    }

    suspend fun loadCourseByNameAndStart(courseName: String, courseStart: Int, whichColumn: Int) =
            courseDao.loadCourseByNameAndStart(courseName, courseStart, whichColumn)

    fun getScore(user: String, password: String, id:String) = fire(Dispatchers.IO) {
        val scoreResponse = NormalScheduleNetwork.getScore(user, password, id)
        Result.success(scoreResponse)
    }

    suspend fun insertCourse2(courseList: List<AllCourse>) {
        for (singleCourse in courseList) {
            try {
                courseDao.insertCourse(Convert.courseResponseToBean(singleCourse))
            } catch (e:Exception){
                Result.failure<Exception>(e)
            }

            // Log.d("Repository", Convert().courseResponseToBean(singleCourse).toString())
        }
    }

    suspend fun deleteAllCourseBean2(){
        try {
            courseDao.deleteAllCourseBean()
        } catch (e:Exception){
            Result.failure<Exception>(e)
        }
    }



    suspend fun insertBackground(background: UserBackgroundBean) = backgroundDao.insertBackground(background)

    suspend fun updateBackground(background: UserBackgroundBean) {
        Log.d("Repository", background.userBackground)
        return try {
            backgroundDao.insertBackground(background)
        } catch (e:Exception){
            backgroundDao.updateBackground(background)
        }
    }

    fun loadBackground() = fire(Dispatchers.IO){
        Log.d("Repository", backgroundDao.loadLastBackground().toString())
        Result.success(backgroundDao.loadLastBackground())
    }

    suspend fun deleteBackground(background: UserBackgroundBean) = backgroundDao.deleteAllBackground(background)




    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
            liveData<Result<T>>(context) {
                val result = try {
                    block()
                } catch (e: Exception) {
                    Result.failure<T>(e)
                }
                emit(result)
            }

    fun saveAccount(user: String, password: String) = AccountDao.saveAccount(user, password)
    fun getSavedAccount() = AccountDao.getSavedAccount()
    fun isAccountSaved() = AccountDao.isAccountSaved()
    fun clearSharePreference() = AccountDao.clearSharePreferences()

    fun saveUserVersion() = AccountDao.newUserShowed()
    fun getNewUserOrNot() = AccountDao.getNewUserOrNot()
}

