package com.liflymark.normalschedule.logic.network

import com.liflymark.normalschedule.logic.model.OneSentencesResponse
import retrofit2.Call
import retrofit2.http.GET

interface SentenceService {

    @GET("/sentence/")
    fun getSentencesList():Call<OneSentencesResponse>

}