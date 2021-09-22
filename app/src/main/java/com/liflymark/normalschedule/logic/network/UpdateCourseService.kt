package com.liflymark.normalschedule.logic.network

import com.liflymark.normalschedule.logic.model.GetNewCourseResponse
import com.liflymark.normalschedule.logic.model.GotResponse
import com.liflymark.normalschedule.logic.model.UploadResponse
import com.liflymark.normalschedule.logic.model.UserTypeResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UpdateCourseService {

    @FormUrlEncoded
    @POST("/updatecourse/getmytype")
    fun getUserType(
        @Field("userNumber")userNumber: String
    ):Call<UserTypeResponse>

    @FormUrlEncoded
    @POST("/updatecourse/get")
    fun getNewCourse(
        @Field("userNumber")userNumber: String
    ):Call<GetNewCourseResponse>

    @FormUrlEncoded
    @POST("/updatecourse/upload")
    fun uploadNewCourse(
        @Field("userNumber")userNumber: String,
        @Field("userCode")userCode: String,
        @Field("beanListStr")beanListStr:String,
    ):Call<UploadResponse>

    @FormUrlEncoded
    @POST("/updatecourse/got")
    fun gotNewCourse(
        @Field("userNumber")userNumber:String,
        @Field("pk")pk:Int,
    ):Call<GotResponse>
}