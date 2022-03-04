package com.liflymark.normalschedule.logic.network

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NormalScheduleNetwork {
    private val CourseService = ServiceCreator.create(CourseService::class.java)
    private val ScoreService = ServiceCreator.create(ScoreService::class.java)
    private val SentenceService = ServiceCreator.create(SentenceService::class.java)
    private val SpacesService = ServiceCreator.create(SpaceService::class.java)
    private val DevService = ServiceCreator.create(DevBoardService::class.java)
    private val ExamArrangeService = ServiceCreator.create(ExamArrangeService::class.java)
    private val CheckUpdateService = ServiceCreator.create(CheckUpdateService::class.java)

    suspend fun getId() =
        CourseService.getId().await()

    fun cancelAll() = ServiceCreator.cancelAll()

    suspend fun getCaptcha(sessionID: String) =
        CourseService.getCaptcha(sessionID).await()

    suspend fun getCourse(user:String,password:String, yzm:String, headers:String) =
        CourseService.getCourse(user, password, yzm, headers).await()

    suspend fun getCourse(user: String, password: String) =
        CourseService.getCourseByNew(user, password).await()

    suspend fun getDepartmentList() =
        CourseService.getDepartmentList().await()

    suspend fun getCourseByMajor(department: String, major:String) =
        CourseService.getCourseByClass(department, major).await()

    suspend fun getScore(user:String, password:String, id:String) =
        ScoreService.getScore(user, password, id).await()

    suspend fun getVisitCourse() =
        CourseService.getVisit().await()

    suspend fun getSentences() = SentenceService.getSentencesList().await()

    suspend fun getScoreDetail(user:String, password:String, id:String) =
        ScoreService.getScoreDetail(user, password, id).await()

    suspend fun loginToSpace(user: String, password: String, id: String) =
        SpacesService.loginToSpace(user, password, id).await()

    suspend fun getSpaceRooms(id: String, roomName:String, searchDate: String) =
        SpacesService.getSpaceRooms(id, roomName, searchDate).await()

    suspend fun getBulletin() =
        DevService.getBulletin().await()

    suspend fun getSchoolBusTime(searchType: String) =
        DevService.getSchoolBusTime(searchType).await()

    suspend fun getExamArrange(userNumber: String, password: String, id: String) =
        ExamArrangeService.getExamArrange(userNumber, password, id).await()

    suspend fun getNewVersion(versionCode:String) =
        CheckUpdateService.getNewVersion(versionCode).await()

    suspend fun loginWebVPN(user: String,password: String) =
        CourseService.loginWebVPN(user, password).await()

    suspend fun loginURP(
        user: String,
        password: String,
        yzm: String,
        cookies:String
    ) = CourseService.loginURP(user, password, yzm, cookies).await()

    suspend fun getGraduateCaptcha(cookies: String) = CourseService.getGraduateCaptcha(cookies).await()

    suspend fun getStartBulletin(id:Int) = DevService.getStartBulletin(id).await()

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
                    if (call.isCanceled){
                        call.cancel()
                    }
                    continuation.resumeWithException(t)
                }
            })
        }
    }

}