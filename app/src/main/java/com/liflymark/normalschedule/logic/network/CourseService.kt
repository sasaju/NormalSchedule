package com.liflymark.normalschedule.logic.network

import android.media.Image
import com.liflymark.normalschedule.logic.model.CourseResponse
import retrofit2.Call
import retrofit2.http.*

interface CourseService {
    @GET("timetable/getid")
    fun getId():Call<String>

    @GET("timetable/captcha/{sessionId}")
    fun getCaptcha(@Path("sessionId") sessionId: String): Call<Image>

    @FormUrlEncoded
    @POST("timetable")
    fun getCourse(@Field("user") user:String,
                  @Field("password") passWord: String,
                  @Field("yzm") captcha: String,
                  @Field("headers") headers: String):Call<CourseResponse>
}