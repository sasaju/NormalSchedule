package com.liflymark.normalschedule.logic.network

import com.liflymark.normalschedule.logic.model.DevBoardResponse
import com.liflymark.normalschedule.logic.model.SchoolBusResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DevBoardService {
    @GET("bulletin/")
    fun getBulletin(): Call<DevBoardResponse>

    @GET("tool/schoolbus/{type}")
    fun getSchoolBusTime(@Path("type")searchType: String): Call<SchoolBusResponse>
}