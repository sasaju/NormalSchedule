package com.liflymark.normalschedule.ui.import_show_score

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
import kotlinx.android.synthetic.main.fragment_import_login.*
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
        UltimateBarX.with(this)
            .transparent()
            .applyStatusBar()

        viewModel.getId()// 获取cookie
        viewModel.idLiveData.observe(this, Observer { result ->
            val idResponse = result.getOrNull()
            if (idResponse == null){
                Toast.makeText(this, "服务异常，无法登陆", Toast.LENGTH_SHORT).show()
            } else {
                this.id = idResponse.id
                Toast.makeText(this, "服务正常，可以登陆", Toast.LENGTH_SHORT).show()
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

//        viewModel.insertCourseLiveData.observe(viewLifecycleOwner, Observer { result->
//            val number = result.getOrNull()
//            if (number == "0") {
//                Log.d("ImportLoginFragment", "成功")
//            } else {
//                Log.d("ImportLoginFragment", "错误")
//           }
//        })

        btnSign.setOnClickListener {
            // 判断是否输入学号密码并提交数据至ViewModel层以更新数据
            userName = user.text.toString()
            userPassword = password.text.toString()
            val yzm = et_code.text.toString()
            when {
                userName == "" -> Toast.makeText(this, "请输入学号", Toast.LENGTH_SHORT).show()
                userPassword == "" -> Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
                else -> viewModel.putValue(userName, userPassword, id)
            }
        }


    }
}