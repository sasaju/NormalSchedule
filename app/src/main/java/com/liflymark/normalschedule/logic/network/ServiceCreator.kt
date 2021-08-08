package com.liflymark.normalschedule.logic.network

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit


object ServiceCreator {
    private const val BASE_URL = "https://liflymark.top/"
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()


    fun cancelAll(){
        okHttpClient.dispatcher().cancelAll()
        Log.d("Service", okHttpClient.dispatcher().maxRequests.toString())
        Log.d("Service", okHttpClient.dispatcher().maxRequestsPerHost.toString())
    }

    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            // .addConverterFactory(ScalarsConverterFactory.create())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            // .addCallAdapterFactory()
            .build()

    private val actLiveMap: ConcurrentHashMap<String, Boolean> = ConcurrentHashMap() // 标记Activity是否存活

    fun markPageAlive(actName: String?) {
        actLiveMap[actName!!] = true
    }

    fun markPageDestroy(actName: String?) {
        actLiveMap[actName!!] = false
    }

    fun <T>create(serviceClass: Class<T>) : T = retrofit.create(serviceClass)
    inline fun <reified T> create(): T = create(T::class.java)

}