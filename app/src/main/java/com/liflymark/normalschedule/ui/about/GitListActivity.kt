package com.liflymark.normalschedule.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.liflymark.normalschedule.logic.model.GitProject
import com.liflymark.normalschedule.ui.theme.NorScTheme

class GitListActivity : ComponentActivity() {

    private val gitProjectList = ArrayList<GitProject>()
//    private lateinit var binding: ActivityGitListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NorScTheme {
                ProjectListAll()
            }
        }
//        binding = ActivityGitListBinding.inflate(layoutInflater)
//        ImmersionBar.with(this).init()
//        initList()
//        val layoutManager = LinearLayoutManager(this)
//        binding.gitList.layoutManager = layoutManager
//        val adapter = GitListAdapter(this,gitProjectList)
//        binding.gitList.adapter = adapter
//        setContentView(binding.root)


    }

    private fun initList(){
        gitProjectList.add(GitProject("NiceSpinner:下拉组件", "https://github.com/arcadefire/nice-spinner"))
        gitProjectList.add(GitProject("ImmersionBar:沉浸式状态栏实现", "https://github.com/gyf-dev/ImmersionBar"))
        gitProjectList.add(GitProject("Toasty", "https://github.com/GrenderG/Toasty"))
        gitProjectList.add(GitProject("Wakeup课程表", "https://github.com/YZune/WakeupSchedule_Kotlin"))
        gitProjectList.add(GitProject("docker-easyconnect:服务器使用easyconnect", "https://github.com/Hagb/docker-easyconnect"))
        gitProjectList.add(GitProject("Androidx", "https://github.com/google"))
        gitProjectList.add(GitProject("Material Dialogs","https://github.com/afollestad/material-dialogs"))
        gitProjectList.add(GitProject("Retrofit2","https://github.com/square/retrofit"))
        gitProjectList.add(GitProject("Coil", "https://github.com/coil-kt/coil"))
        gitProjectList.add(GitProject("Gson", "https://github.com/google/gson"))
        gitProjectList.add(GitProject("Accompanist", "https://github.com/google/accompanist"))
        gitProjectList.add(GitProject("PictureSelector","https://github.com/LuckSiege/PictureSelector"))
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