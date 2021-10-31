package com.liflymark.normalschedule.logic.network


import com.liflymark.normalschedule.logic.model.ExamArrangeResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ExamArrangeService {
    @FormUrlEncoded
    @POST("/examarrannge/")
    fun getExamArrange(
        @Field("user")userNumber:String,
        @Field("password")password:String,
        @Field("id")sessionId:String
    ): Call<ExamArrangeResponse>
}