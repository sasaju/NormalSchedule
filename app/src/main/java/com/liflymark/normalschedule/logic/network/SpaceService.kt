package com.liflymark.normalschedule.logic.network

import com.liflymark.normalschedule.logic.model.SpaceLoginResponse
import com.liflymark.normalschedule.logic.model.SpaceResponse
import retrofit2.Call
import retrofit2.http.*

interface SpaceService {
    @FormUrlEncoded
    @POST("spaceroom/")
    fun loginToSpace(
        @Field("user") user:String,
        @Field("password") passWord: String,
        @Field("headers") headers: String,
        @Field("yzm") captcha: String = "",
    ): Call<SpaceLoginResponse>

    @GET("spaceroom/room/{sessionId}/{roomName}/{searchDate}")
    fun getSpaceRooms(
        @Path("sessionId") sessionId: String,
        @Path("roomName") roomName: String,
        @Path("searchDate") searchDate: String
    ):Call<SpaceResponse>
}