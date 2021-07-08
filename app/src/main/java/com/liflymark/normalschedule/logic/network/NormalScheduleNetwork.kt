package com.liflymark.normalschedule.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NormalScheduleNetwork {
    private val CourseService = ServiceCreator.create(CourseService::class.java)
    private val ScoreService = ServiceCreator.create(ScoreService::class.java)
    private val SentenceService = ServiceCreator.create(SentenceService::class.java)

    suspend fun getId() = CourseService.getId().await()

    suspend fun getCaptcha(sessionID: String) = CourseService.getCaptcha(sessionID).await()

    suspend fun getCourse(user:String,password:String, yzm:String, headers:String) =
        CourseService.getCourse(user, password, yzm, headers).await()

    suspend fun getCourse(user: String, password: String) =
        CourseService.getCourseByNew(user, password).await()

    suspend fun getDepartmentList() =
        CourseService.getDepartmentList().await()

    suspend fun getScore(user:String, password:String, id:String) =
        ScoreService.getScore(user, password, id).await()

    suspend fun getVisitCourse() =
        CourseService.getVisit().await()

    suspend fun getSentences() = SentenceService.getSentencesList().await()

    suspend fun getScoreDetail(user:String, password:String, id:String) =
        ScoreService.getScoreDetail(user, password, id).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("Response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}