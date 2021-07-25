package com.liflymark.normalschedule.logic.network

import com.liflymark.normalschedule.logic.model.CourseResponse
import com.liflymark.normalschedule.logic.model.DepartmentList
import com.liflymark.normalschedule.logic.model.IdResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CourseService {

    @GET("timetable/getid/")
    fun getId():Call<IdResponse>

    @GET("timetable/captcha/{sessionId}")
    fun getCaptcha(@Path("sessionId") sessionId: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("timetable/")
    fun getCourse(@Field("user") user:String,
                  @Field("password") passWord: String,
                  @Field("yzm") captcha: String,
                  @Field("headers") headers: String):Call<CourseResponse>

    @GET("timetable/vist/")
    fun getVisit(): Call<CourseResponse>

    @GET("class/departmentList")
    fun getDepartmentList():Call<DepartmentList>

    @GET("class/{department}/{major}.json")
    fun getCourseByClass(@Path("department")department:String,
                         @Path("major")major: String): Call<CourseResponse>

    @FormUrlEncoded
    @POST("newtimetable/")
    fun getCourseByNew(@Field("user")user: String,
                       @Field("password")passWord: String): Call<CourseResponse>

}