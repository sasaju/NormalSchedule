package com.liflymark.normalschedule.logic.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ServiceCreator {

    private const val BASE_URL = "http://81.70.81.94/"
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .callTimeout(3, TimeUnit.MINUTES)
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