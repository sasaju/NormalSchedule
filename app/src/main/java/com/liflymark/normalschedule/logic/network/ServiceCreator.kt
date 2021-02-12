package com.liflymark.normalschedule.logic.network

import android.graphics.BitmapFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object ServiceCreator {

    private const val BASE_URL = "http://tttaaaa/"

    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            // .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            // .addCallAdapterFactory()
            .build()

    fun <T>create(serviceClass: Class<T>) : T = retrofit.create(serviceClass)
    inline fun <reified T> create(): T = create(T::class.java)

}