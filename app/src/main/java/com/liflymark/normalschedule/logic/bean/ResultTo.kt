package com.liflymark.normalschedule.logic.bean

import java.io.Serializable

class ResultTo<out T> internal constructor(
    private val value: Any?
){
    var isSuccess =  true
    @Suppress("UNCHECKED_CAST")
    fun getOrNull(): T?=
        when{
            isSuccess -> value as T
            else -> null
        }

    fun nowFailure(){
        isSuccess = false
    }

    fun <T> failure(exception: Throwable): ResultTo<T> =
        ResultTo(exception.toString())
}