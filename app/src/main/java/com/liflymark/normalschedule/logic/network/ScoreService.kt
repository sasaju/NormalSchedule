package com.liflymark.normalschedule.logic.network

import com.liflymark.normalschedule.logic.model.CourseResponse
import com.liflymark.normalschedule.logic.model.IdResponse
import com.liflymark.normalschedule.logic.model.ScoreDetail
import com.liflymark.normalschedule.logic.model.ScoreResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ScoreService {
    @GET("score/")
    fun getId(): Call<IdResponse>

    @FormUrlEncoded
    @POST("score/")
    fun getScore(@Field("user") user:String,
                  @Field("password") passWord: String,
                  @Field("id") id: String): Call<ScoreResponse>

    @FormUrlEncoded
    @POST("scoredetail/")
    fun getScoreDetail(@Field("user") user:String,
                 @Field("password") passWord: String,
                 @Field("id") id: String): Call<ScoreDetail>
}