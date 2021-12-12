package com.liflymark.normalschedule.logic.network

import com.liflymark.normalschedule.logic.model.CheckUpdateResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CheckUpdateService {

    @FormUrlEncoded
    @POST("checkveriosn/")
    fun getNewVersion(
        @Field("version")versionCode:String
    ):Call<CheckUpdateResponse>
}