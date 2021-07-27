package com.liflymark.normalschedule.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.gyf.immersionbar.ImmersionBar
import com.liflymark.normalschedule.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_about.*
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element


class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        ImmersionBar.with(this).init()

        val versionElement = Element()
        versionElement.title = "\uE6BC  version: 0.0.1"

        val authorElement = Element()
        authorElement.title = "\uE6A3  关于开发组"
        authorElement.setOnClickListener{
            MaterialDialog(this)
                .title(text = "关于开发组")
                .message(text = "开发者：\n  河北大学 | 大三药物制剂在读@符号 \n  (QQ:1289142675) \nLOGO、背景图绘制：\n   河北大学 | 大三药学在读@Mr.")
                .positiveButton(text = "知道了")
                .show()
        }

        val joinQQGroupElement = Element()
        joinQQGroupElement.title = "\uE6C7 加入QQ反馈群"
        joinQQGroupElement.setOnClickListener {
            val key = "IQn1Mh09oCQwvfVXljBPgCkkg8SPfjZP"
            intent = Intent()
            intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key");
            // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                startActivity(intent)
            } catch (e: Exception){
                Toasty.error(this, "未安装QQ").show()
            }
        }

        val gitListElement = Element()
        gitListElement.title = "\uE6D5 开源许可"
        gitListElement.setOnClickListener {
             val intent = Intent(this, GitListActivity::class.java)
            startActivity(intent)
        }




        val aboutPage: View = AboutPage(this)
            .isRTL(false)
            .enableDarkMode(false)
            .setCustomFont(ResourcesCompat.getFont(this, R.font.iconfont))
            .setDescription("一款针对河北大学教务系统的课表APP")
            .addItem(versionElement)
            .addItem(joinQQGroupElement)
            .addItem(gitListElement)
            .addItem(authorElement)
            .create()



        scroll_about.addView(aboutPage)
    }
}

