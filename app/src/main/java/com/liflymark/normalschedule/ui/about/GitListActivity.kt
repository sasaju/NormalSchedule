package com.liflymark.normalschedule.ui.about

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.model.GitProject
import kotlinx.android.synthetic.main.activity_git_list.*

class GitListActivity : AppCompatActivity() {

    private val gitProjectList = ArrayList<GitProject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_git_list)
        ImmersionBar.with(this).init()
        initList()
        val layoutManager = LinearLayoutManager(this)
        gitList.layoutManager = layoutManager
        val adapter = GitListAdapter(this,gitProjectList)
        gitList.adapter = adapter


    }

    private fun initList(){
        gitProjectList.add(GitProject("NiceSpinner:下拉组件", "https://github.com/arcadefire/nice-spinner"))
        gitProjectList.add(GitProject("ImmersionBar:沉浸式状态栏实现", "https://github.com/gyf-dev/ImmersionBar"))
        gitProjectList.add(GitProject("material-dialogs:一款美观的Dialog","https://github.com/afollestad/material-dialogs"))
        gitProjectList.add(GitProject("groupie", "https://github.com/lisawray/groupie"))
        gitProjectList.add(GitProject("Toasty", "https://github.com/GrenderG/Toasty"))
        gitProjectList.add(GitProject("IcyTimeTable:布局课表页面", "https://github.com/Icyrockton/IcyTimeTable"))
        gitProjectList.add(GitProject("Wakeup课程表", "https://github.com/YZune/WakeupSchedule_Kotlin"))
        gitProjectList.add(GitProject("Glide","https://github.com/bumptech/glide"))
        gitProjectList.add(GitProject("docker-easyconnect:服务器使用easyconnect", "https://github.com/Hagb/docker-easyconnect"))
        gitProjectList.add(GitProject("androidx.core:core-ktx", "https://github.com/google"))
        gitProjectList.add(GitProject("androidx.appcompat:appcompat", "https://github.com/google"))
        gitProjectList.add(GitProject("com.google.android.material:material", "https://github.com/google"))
        gitProjectList.add(GitProject("androidx.constraintlayout:constraintlayout", "https://github.com/google"))
        gitProjectList.add(GitProject("androidx.recyclerview:recyclerview", "https://github.com/google"))
        gitProjectList.add(GitProject("androidx.lifecycle:lifecycle-extensions", "https://github.com/google"))
        gitProjectList.add(GitProject("androidx.room:room-ktx", "https://github.com/google"))
    }

    fun openBrowser(url: String){
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            println("当前手机未安装浏览器")
        }
    }
}