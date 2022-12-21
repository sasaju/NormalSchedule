package com.liflymark.normalschedule.logic.network


import com.liflymark.normalschedule.logic.model.LogoutResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LogoutService {
    @FormUrlEncoded
    @POST("applogout/")
    fun logout(
        @Field("user") user:String,
        @Field("password") password:String,
        @Field("qq") description:String,
        @Field("id") sessionId:String
    ): Call<LogoutResponse>
}