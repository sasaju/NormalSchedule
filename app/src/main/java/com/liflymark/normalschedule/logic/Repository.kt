package com.liflymark.normalschedule.logic

import androidx.lifecycle.liveData
import com.liflymark.normalschedule.logic.network.NormalScheduleNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object Repository {

    fun getId() = fire(Dispatchers.IO) {
        val id =NormalScheduleNetwork.getId()
        if (id != "") {
            Result.success(id)
        } else {
            Result.failure(RuntimeException("Can't get id!"))
        }
    }

    fun getCaptcha(sessionId: String) = fire(Dispatchers.IO) {
        val img = NormalScheduleNetwork.getCaptcha(sessionId)
        Result.success(img)
    }

    fun getCourse(user:String, password:String, yzm:String, headers:String) = fire(Dispatchers.IO) {
        val courseResponse = NormalScheduleNetwork.getCourse(user, password, yzm, headers)
        when(courseResponse.status) {
            "yes", "no" -> Result.success(courseResponse)
            else -> Result.failure(RuntimeException(courseResponse.status))
        }
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