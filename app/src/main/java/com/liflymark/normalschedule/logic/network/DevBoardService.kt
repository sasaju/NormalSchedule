package com.liflymark.normalschedule.logic.network

import com.liflymark.normalschedule.logic.bean.StartBulletinBean
import com.liflymark.normalschedule.logic.model.DevBoardResponse
import com.liflymark.normalschedule.logic.model.SchoolBusResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DevBoardService {
    @GET("bulletin/")
    fun getBulletin(): Call<DevBoardResponse>

    @GET("bulletin/start/")
    fun getStartBulletin(@Query("id") id:Int):Call<StartBulletinBean>

    @GET("tool/schoolbus/{type}")
    fun getSchoolBusTime(@Path("type")searchType: String): Call<SchoolBusResponse>
}