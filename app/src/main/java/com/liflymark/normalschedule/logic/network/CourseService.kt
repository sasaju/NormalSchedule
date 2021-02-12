package com.liflymark.normalschedule.logic.network

import android.graphics.Bitmap
import android.media.Image
import com.liflymark.normalschedule.logic.model.CourseResponse
import com.liflymark.normalschedule.logic.model.IdResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CourseService {

    @GET("timetable/getid")
    fun getId():Call<IdResponse>

    @GET("timetable/captcha/{sessionId}")
    fun getCaptcha(@Path("sessionId") sessionId: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("timetable/")
    fun getCourse(@Field("user") user:String,
                  @Field("password") passWord: String,
                  @Field("yzm") captcha: String,
                  @Field("headers") headers: String):Call<CourseResponse>
}