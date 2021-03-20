package com.liflymark.normalschedule.ui.import_course

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.ui.show_timetable.ShowTimetableActivity
import com.zackratos.ultimatebarx.library.UltimateBarX
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_import_login.*

class ImportLoginFragment: Fragment() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(CourseViewModel::class.java) }
    var userName = ""
    var userPassword = ""
    private var id = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_import_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        UltimateBarX.with(this)
                .transparent()
                .applyStatusBar()
        if (viewModel.isAccountSaved()){
            val intent = Intent(context, ShowTimetableActivity::class.java).apply {
                putExtra("isSaved", true)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        viewModel.getId()// 获取cookie
        viewModel.idLiveData.observe(viewLifecycleOwner, Observer { result ->
            // 仅装有id
            // Log.d("ImportLoginFragment", result.getOrNull().toString())
            val idResponse = result.getOrNull()
            if (idResponse == null){
                activity?.let { Toasty.error(it, "服务异常，无法登陆", Toast.LENGTH_SHORT).show() }
            } else {
                this.id = idResponse.id
                activity?.let { Toasty.success(it, "服务正常，可以登陆", Toast.LENGTH_SHORT).show() }
            }
        })

        viewModel.courseLiveData.observe(viewLifecycleOwner, Observer { result ->

            val course = result.getOrNull()

            if (course == null) {
                Toast.makeText(activity, "登陆异常，重启app试试", Toast.LENGTH_SHORT).show()
            } else {
                val allCourseList = course.allCourse
                when (course.status) {
                    "yes" -> {
                        saveAccount()
                        activity?.let { Toasty.success(it, "登陆成功，解析成功", Toast.LENGTH_SHORT).show() }
//                        viewModel.insertOriginalCourse(allCourseList)
//                        for (singleCourse in allCourseList) {
//                            Log.d("ImportLoginFragment", singleCourse.toString())
//                        }
                        if(activity is MainActivity) {
                            val intent = Intent(context, ShowTimetableActivity::class.java).apply {
                                putExtra("isSaved", false)
                                putExtra("courseList", Convert.allCourseToJson(allCourseList))
                                putExtra("user", userName)
                                putExtra("password", userPassword)
                            }
                            startActivity(intent)
                            activity?.finish()
                        }
                        // viewModel.saveAccount(userName, userPassword)
                    }
                    "no" -> {
                        activity?.let { Toasty.error(it, "登陆成功，解析异常，请务必检查课程表是否正确", Toast.LENGTH_LONG).show() }
                        if(activity is MainActivity) {
                            val intent = Intent(context, ShowTimetableActivity::class.java).apply {
                                putExtra("isSaved", true)
                            }
                            startActivity(intent)
                            activity?.finish()
                        }
                        // viewModel.saveAccount(userName, userPassword)
                    }
                    else -> {
                        Toast.makeText(activity, result.getOrNull()!!.status, Toast.LENGTH_SHORT).show()
                        viewModel.getImage(id)
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

        ivCode.setOnClickListener {
            progress_bar.visibility = View.VISIBLE
            // Toast.makeText(activity, "正在尝试获取图片", Toast.LENGTH_SHORT).show()
            viewModel.getImage(id)
            viewModel.imageLiveData.observe(viewLifecycleOwner, Observer { image ->
                // Toast.makeText(activity, "正在尝试加载图片", Toast.LENGTH_SHORT).show()
                ivCode.visibility = View.VISIBLE
                progress_bar.visibility = View.INVISIBLE
                val inputStream = image.getOrNull()
                ivCode.setImageBitmap(inputStream)
            })
        }

        btnSign.setOnClickListener {
            // 判断是否输入学号密码并提交数据至ViewModel层以更新数据
            userName = user.text.toString()
            userPassword = password.text.toString()
            val yzm = et_code.text.toString()
            when {
                userName == "" -> Toast.makeText(activity, "请输入学号", Toast.LENGTH_SHORT).show()
                userPassword == "" -> Toast.makeText(activity, "请输入密码", Toast.LENGTH_SHORT).show()
                else -> viewModel.putValue(userName, userPassword, yzm, id)
            }
        }


//        testButton.setOnClickListener {
////            thread {
////                val a = viewModel.loadAllCourse()
////                var n = 0
////                for (i in a) {
////                    Log.d("ImportResult", i.toString())
////                    n++
////                }
////                Log.d("ImportResult", n.toString())
////            }
//            val intent = Intent(context, ShowTimetableActivity::class.java).apply {
//                putExtra("isSaved", true)
//            }
//            startActivity(intent)
//            activity?.finish()
//            return@setOnClickListener
//        }
    }

    private fun saveAccount() {
        userName = user.text.toString()
        userPassword = password.text.toString()
        viewModel.saveAccount(userName, userPassword)
    }
}