package com.liflymark.normalschedule.ui.about

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.liflymark.normalschedule.R
import com.zackratos.ultimatebarx.library.UltimateBarX
import com.zackratos.ultimatebarx.library.bean.BarConfig

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val config = BarConfig.newInstance()          // 创建配置对象
                .fitWindow(true)                          // 布局是否侵入状态栏（true 不侵入，false 侵入）
                .color(Color.TRANSPARENT)                         // 状态栏背景颜色（色值）
                .light(true)
        UltimateBarX.with(this)                       // 对当前 Activity 或 Fragment 生效
                .config(config)                           // 使用配置
                .applyStatusBar()
    }
}