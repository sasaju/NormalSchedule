package com.liflymark.normalschedule.ui.abase

import android.content.res.Configuration
import android.content.res.Resources
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity:AppCompatActivity() {
    override fun getResources(): Resources {
        val res = super.getResources()
        if (res.configuration.fontScale != 1f) {
            //非默认值
            val newConfig = Configuration()
            newConfig.setToDefaults() //设置默认
            res.updateConfiguration(newConfig, res.displayMetrics)
        }
        return res
    }
}

open class BaseComment:ComponentActivity() {
    override fun getResources(): Resources {
        val res = super.getResources()
        if (res.configuration.fontScale != 1f) {
            //非默认值
            val newConfig = Configuration()
            newConfig.setToDefaults() //设置默认
            res.updateConfiguration(newConfig, res.displayMetrics)
        }
        return res
    }
}