package com.liflymark.normalschedule.logic.utils.betterrecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflate(@LayoutRes layoutRes: Int) : View {
    val inflater = LayoutInflater.from(context)
    return inflater.inflate(layoutRes, this, false)
}
