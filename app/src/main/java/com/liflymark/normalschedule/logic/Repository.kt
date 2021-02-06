package com.liflymark.normalschedule.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.liflymark.normalschedule.logic.network.NormalScheduleNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object Repository {

    fun getId() = fire(Dispatchers.IO) {
        val id =NormalScheduleNetwork.getId()
        Result.success("1236")
//        // Log.d("Repository", id)
//        if (id != "") {
//            // Log.d("Repository", id)
//            Result.success(id)
//        } else {
//            // Log.d("Repository", "failure")
//            Result.failure(RuntimeException("Can't get id!"))
//        }
    }


    fun getCaptcha(sessionId: String) = fire(Dispatchers.IO) {
        val img = NormalScheduleNetwork.getCaptcha(sessionId)
        Result.success(img)
    }

    fun getCourse(user:String, password:String, yzm:String, headers:String) = fire(Dispatchers.IO) {
        val courseResponse = NormalScheduleNetwork.getCourse(user, password, yzm, headers)
        Result.success(courseResponse)
    //        when(courseResponse.status) {
//            "yes", "no" -> Result.success(courseResponse)
//            else -> Result.failure(RuntimeException(courseResponse.status))
//        }
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
}
