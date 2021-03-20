package com.liflymark.normalschedule.ui.import_show_score

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.NormalScheduleApplication.Companion.context
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.ui.import_course.CourseViewModel
import com.liflymark.normalschedule.ui.show_timetable.ShowTimetableActivity
import com.zackratos.ultimatebarx.library.UltimateBarX
import com.zackratos.ultimatebarx.library.addStatusBarTopPadding
import com.zackratos.ultimatebarx.library.bean.BarConfig
import com.zackratos.ultimatebarx.library.statusBarHeight
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_import_score.*
import kotlinx.android.synthetic.main.activity_show_score.*
import kotlinx.android.synthetic.main.fragment_import_login.*
import kotlinx.android.synthetic.main.fragment_import_login.btnSign
import kotlinx.android.synthetic.main.fragment_import_login.et_code
import kotlinx.android.synthetic.main.fragment_import_login.input_pwd
import kotlinx.android.synthetic.main.fragment_import_login.password
import kotlinx.android.synthetic.main.fragment_import_login.rl_code
import kotlinx.android.synthetic.main.fragment_import_login.user
import kotlinx.android.synthetic.main.fragment_import_login.view.*

class ImportScoreActivity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(ImportScoreViewModel::class.java) }
    var userName = ""
    var userPassword = ""
    private var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_score)
        btnSign.text = "登陆以导入成绩"
        et_code.visibility = View.INVISIBLE
        rl_code.visibility = View.INVISIBLE
        input_pwd.hint = "请输入统一认证密码"

        val config = BarConfig.newInstance()          // 创建配置对象
                .fitWindow(true)                          // 布局是否侵入状态栏（true 不侵入，false 侵入）
                .colorRes(R.color.lightBlue)           // 状态栏背景颜色（色值）
                .light(true)
        Toasty.Config.getInstance().setTextSize(15).apply()
        setSupportActionBar(import_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        supportActionBar?.setHomeButtonEnabled(true); //设置返回键可用
        UltimateBarX.with(this)                       // 对当前 Activity 或 Fragment 生效
                .config(config)                           // 使用配置
                .applyStatusBar()

        val saved = viewModel.isAccountSaved()
        if (saved) {
            user0.text = SpannableStringBuilder(viewModel.getSavedAccount()["user"])
            password0.text = SpannableStringBuilder(viewModel.getSavedAccount()["password"])
        }

        viewModel.getId()// 获取cookie
        viewModel.idLiveData.observe(this, Observer { result ->
            val idResponse = result.getOrNull()
            if (idResponse == null){
                Toasty.error(this, "服务异常，无法登陆", Toast.LENGTH_SHORT).show()
            } else {
                this.id = idResponse.id
                Toasty.success(this, "服务正常，可以登陆", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.scoreLiveData.observe(this, Observer { result ->

            val score = result.getOrNull()

            if (score == null) {
                Toast.makeText(this, "登陆异常，重启app试试", Toast.LENGTH_SHORT).show()
            } else {
                val allGradeList = score.grade_list
                when (score.result) {
                    // 此处服务信息传输易造成错误 后期需修改
                    "登陆成功" -> {
                        // saveAccount()
                        if (save_or_not.isChecked){
                            viewModel.saveAccount(userName, userPassword)
                        }
                        val intent = Intent(this, ShowScoreActivity::class.java).apply {
                            putExtra("grade_list_string", Convert.allGradeToJson(allGradeList))
                        }
                        startActivity(intent)
                        this.finish()
                    }

                    else -> {
                        Toast.makeText(this, result.getOrNull()?.result, Toast.LENGTH_SHORT).show()
                    }
                }


            }
        })


        btnSign.setOnClickListener {
            // 判断是否输入学号密码并提交数据至ViewModel层以更新数据
            userName = user0.text.toString()
            userPassword = password0.text.toString()
            val yzm = et_code.text.toString()
            when {
                userName == "" -> Toast.makeText(this, "请输入学号", Toast.LENGTH_SHORT).show()
                userPassword == "" -> Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
                else -> viewModel.putValue(userName, userPassword, id)
            }
        }


    }
}