package com.liflymark.normalschedule.logic

import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.liveData
import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.dao.AccountDao
import com.liflymark.normalschedule.logic.dao.AppDatabase
import com.liflymark.normalschedule.logic.model.AllCourse
import com.liflymark.normalschedule.logic.model.IdResponse
import com.liflymark.normalschedule.logic.network.NormalScheduleNetwork
import com.liflymark.normalschedule.logic.utils.Convert
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object Repository {

    private val dataBase = AppDatabase.getDatabase(NormalScheduleApplication.context)
    private val courseDao = dataBase.courseDao()

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
        Log.d("Repository", "获取到课程")
        Result.success(courseResponse)
    }

    fun insertCourse(courseList: List<AllCourse>) = fire(Dispatchers.IO) {
        for (singleCourse in courseList) {
            courseDao.insertCourse(Convert.courseResponseToBean(singleCourse))
            // Log.d("Repository", Convert().courseResponseToBean(singleCourse).toString())
        }

        Result.success("0")
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


    fun loadAllCourse() = fire(Dispatchers.IO){
        Result.success(courseDao.loadAllCourse())
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun saveAccount(account: IdResponse) = AccountDao.saveAccount(account)
}
