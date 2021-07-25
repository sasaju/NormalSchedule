package com.liflymark.normalschedule.ui.import_course

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gyf.immersionbar.ImmersionBar
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.model.AllCourse
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.Dialog
import com.liflymark.normalschedule.ui.class_course.ClassCourseActivity
import com.liflymark.normalschedule.ui.import_again.ImportCourseAgain
import com.liflymark.normalschedule.ui.show_timetable.ShowTimetableActivity2
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_import_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ImportLoginFragment: Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(CourseViewModel::class.java) }
    var userName = ""
    var userPassword = ""
    private var id = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_import_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ImmersionBar.with(this).init()
        if (viewModel.isAccountSaved()){
//            val intent = Intent(context, ImportScoreActivity::class.java).apply {
//                putExtra("isSaved", true)
//            }
            val intent = Intent(context, ShowTimetableActivity2::class.java).apply{
                putExtra("isSaved", true)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        if (activity is ImportCourseAgain){
            viewModel.accountLiveData.observe(viewLifecycleOwner){
                user.text = SpannableStringBuilder(it[0])
                password.text = SpannableStringBuilder(it[1])
            }
            viewModel.getAccount()
        }

        select_sign_method.attachDataSource(listOf("统一认证", "URP登陆"))
        select_sign_method.setOnSpinnerItemSelectedListener { parent, view, position, id ->
            when(position){
                0 -> {
                    input_code.visibility = View.INVISIBLE
                    rl_code.visibility = View.INVISIBLE
                    tips_text.visibility = View.VISIBLE
                    et_code.setText("")
                }
                1 -> {
                    input_code.visibility = View.VISIBLE
                    rl_code.visibility = View.VISIBLE
                    tips_text.visibility = View.INVISIBLE
                }
            }
        }
        viewModel.getId()// 获取cookie
        viewModel.idLiveData.observe(viewLifecycleOwner, { result ->
            if (result == null) {
                server_status.text = "目前可能仅允许“统一认证”登陆，如失败请尝试班级导入"
                select_sign_method.attachDataSource(listOf("统一认证"))
                id = ""
                server_status.setTextColor(Color.RED)
            } else {
                this.id = result.id
                server_status.text = "服务器正常"
            }
        })


        viewModel.courseLiveData.observe(viewLifecycleOwner, Observer { result ->

            if (result == null) {
                activity?.let { Toasty.error(it, "登陆异常，重启app试试", Toasty.LENGTH_SHORT).show() }
            } else {
                val allCourseList = result.allCourse
                when (result.status) {
                    "yes" -> {
                        saveAccount()
                        activity?.let {
                            Toasty.success(it, "登陆成功，解析成功", Toasty.LENGTH_SHORT).show()
                        }
//                        viewModel.insertOriginalCourse(allCourseList)
//                        for (singleCourse in allCourseList) {
//                            Log.d("ImportLoginFragment", singleCourse.toString())
//                        }
                        if (activity is MainActivity) {
                            val intent = Intent(context, ShowTimetableActivity2::class.java).apply {
                                putExtra("isSaved", false)
                                putExtra("courseList", Convert.allCourseToJson(allCourseList))
                                putExtra("user", userName)
                                putExtra("password", userPassword)
                            }
                            startActivity(intent)
                            activity?.finish()
                        }
                        if (activity is ImportCourseAgain) {
                            val intent = Intent(context, ShowTimetableActivity2::class.java).apply {
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
                        activity?.let {
                            Toasty.error(
                                it,
                                "登陆成功，解析异常，请务必检查课程表是否正确",
                                Toasty.LENGTH_LONG
                            ).show()
                        }
                        if (activity is MainActivity) {
                            val intent = Intent(context, ShowTimetableActivity2::class.java).apply {
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
                    else -> {
                        activity?.let {
                            Toasty.error(it, result!!.status, Toasty.LENGTH_SHORT).show()
                        }
                        viewModel.getImage(id)
                    }
                }


            }
        })

        viewModel.courseVisitLiveData.observe(viewLifecycleOwner, { result ->
            val course = result.getOrNull()

            if (course == null) {
                val allCourseList = listOf(
                    AllCourse(
                    "五四路",
                    1,
                    1,
                    "11111111111111111111111",
                    1,
                    "点击右上角导入课程",
                    "",
                    ""
                )
                )
                val intent = Intent(context, ShowTimetableActivity2::class.java).apply {
                    putExtra("isSaved", true)
                    putExtra("courseList", Convert.allCourseToJson(allCourseList))
                    putExtra("user", userName)
                    putExtra("password", userPassword)
                }
                startActivity(intent)
                activity?.finish()
            } else {
                val allCourseList = course.allCourse
                val intent = Intent(context, ShowTimetableActivity2::class.java).apply {
                    putExtra("isSaved", false)
                    putExtra("courseList", Convert.allCourseToJson(allCourseList))
                    putExtra("user", userName)
                    putExtra("password", userPassword)
                }
                startActivity(intent)
                activity?.finish()
            }
        })

        viewModel.imageLiveData.observe(viewLifecycleOwner, {
            ivCode.visibility = View.VISIBLE
            progress_bar.visibility = View.INVISIBLE
            if (it != null)
                ivCode.setImageBitmap(it)
        })
        ivCode.setOnClickListener {
            progress_bar.visibility = View.VISIBLE
            viewModel.getImage(id)
        }



        btnSign.setOnClickListener {
            // 判断是否输入学号密码并提交数据至ViewModel层以更新数据
            userName = user.text.toString()
            userPassword = password.text.toString()
            val yzm = et_code.text.toString()
            when {
                !agree_or_not.isChecked ->  activity?.let { it1 -> Toasty.warning(it1, "您未同意用户协议", Toasty.LENGTH_SHORT).show() }
                userName == "" -> activity?.let { it1 -> Toasty.warning(it1, "请输入学号", Toasty.LENGTH_SHORT).show() }
                userPassword == "" -> activity?.let { it1 -> Toasty.warning(it1, "请输入密码", Toasty.LENGTH_SHORT).show() }
                id == "" -> viewModel.putValue(userName, userPassword)
                else -> viewModel.putValue(userName, userPassword, yzm, id)
            }
        }

        contact.setOnClickListener {
            context?.let { it1 -> Dialog.getContractDialog(it1) }?.show()
        }

        btnSignByClass.setOnClickListener {
            val intent = Intent(context,ClassCourseActivity::class.java).apply {
                putExtra("allowImport", true)
            }
            startActivity(intent)
            activity?.finish()
        }

        btnSignByVisitor.setOnClickListener {
            viewModel.putValue()
            viewModel.saveAccount("visit", "visit")
        }
    }


    private fun saveAccount() {
        userName = user.text.toString()
        userPassword = password.text.toString()
        viewModel.saveAccount(userName, userPassword)
    }
}