package com.liflymark.normalschedule.logic.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ServiceCreator {

    private const val BASE_URL = "https://liflymark.top/"
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .callTimeout(1, TimeUnit.MINUTES)
            .build()

    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            // .addConverterFactory(ScalarsConverterFactory.create())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            // .addCallAdapterFactory()
            .build()

    fun <T>create(serviceClass: Class<T>) : T = retrofit.create(serviceClass)
    inline fun <reified T> create(): T = create(T::class.java)

}