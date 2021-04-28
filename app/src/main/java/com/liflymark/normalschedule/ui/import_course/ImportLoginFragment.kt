package com.liflymark.normalschedule.ui.import_course

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gyf.immersionbar.ImmersionBar
import com.jaredrummler.materialspinner.MaterialSpinner
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.model.Structure
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.Dialog
import com.liflymark.normalschedule.logic.utils.Dialog.getSelectDepartmentAndClass
import com.liflymark.normalschedule.ui.import_again.ImportCourseAgain
import com.liflymark.normalschedule.ui.show_timetable.ShowTimetableActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_import_login.*
import org.angmarch.views.NiceSpinner

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
        ImmersionBar.with(this).init()
        if (viewModel.isAccountSaved()){
            val intent = Intent(context, ShowTimetableActivity::class.java).apply {
                putExtra("isSaved", true)
            }
            startActivity(intent)
            activity?.finish()
            return
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
        viewModel.idLiveData.observe(viewLifecycleOwner, Observer { result ->
            // 仅装有id
            // Log.d("ImportLoginFragment", result.getOrNull().toString())
            val idResponse = result.getOrNull()
            if (idResponse == null){
                server_status.text = "目前仅允许“统一认证”登陆，登陆可能会很慢请耐心等待"
                select_sign_method.attachDataSource(listOf("统一认证"))
                id = ""
                server_status.setTextColor(Color.RED)
            } else {
                this.id = idResponse.id
                server_status.text = "服务器正常"
            }
        })

        viewModel.courseNewLiveData.observe(viewLifecycleOwner, Observer { result ->
            val course = result.getOrNull()
            if (course == null) {
                activity?.let { Toasty.error(it, "登陆异常，重启app试试", Toasty.LENGTH_SHORT).show() }
            } else {
                val allCourseList = course.allCourse
                when (course.status) {
                    "yes" -> {
                        saveAccount()
                        activity?.let { Toasty.success(it, "登陆成功，解析成功", Toasty.LENGTH_SHORT).show() }
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
                        if (activity is ImportCourseAgain) {
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
                        activity?.let { Toasty.error(it, "登陆成功，解析异常，请务必检查课程表是否正确", Toasty.LENGTH_LONG).show() }
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
                        activity?.let { Toasty.error(it, result.getOrNull()!!.status, Toasty.LENGTH_SHORT).show() }
                        viewModel.getImage(id)
                    }
                }


            }
        })

        viewModel.courseLiveData.observe(viewLifecycleOwner, Observer { result ->

            val course = result.getOrNull()
            if (course == null) {
                activity?.let { Toasty.error(it, "登陆异常，重启app试试", Toasty.LENGTH_SHORT).show() }
            } else {
                val allCourseList = course.allCourse
                when (course.status) {
                    "yes" -> {
                        saveAccount()
                        activity?.let { Toasty.success(it, "登陆成功，解析成功", Toasty.LENGTH_SHORT).show() }
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
                        if (activity is ImportCourseAgain) {
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
                        activity?.let { Toasty.error(it, "登陆成功，解析异常，请务必检查课程表是否正确", Toasty.LENGTH_LONG).show() }
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
                        activity?.let { Toasty.error(it, result.getOrNull()!!.status, Toasty.LENGTH_SHORT).show() }
                        viewModel.getImage(id)
                    }
                }


            }
        })

        ivCode.setOnClickListener {
            progress_bar.visibility = View.VISIBLE
            viewModel.getImage(id)
            viewModel.imageLiveData.observe(viewLifecycleOwner, Observer { image ->
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
            val dialog = getSelectDepartmentAndClass(requireContext(), listOf(Structure("药学院", listOf("19药物制剂"))))
            val departmentSpinner = dialog.findViewById<MaterialSpinner>(R.id.department)
            val majorSpinner = dialog.findViewById<MaterialSpinner>(R.id.major)
            departmentSpinner.setItems("Ice Cream Sandwich", "Jelly Bean", "KitKat", "Lollipop", "Marshmallow")
            majorSpinner.setItems(listOf("药物制剂", "药学"))
            dialog.show()
            activity?.let { it1 -> Toasty.info(it1, "暂未开发 敬请期待", Toasty.LENGTH_SHORT).show() }
        }

    }


    private fun saveAccount() {
        userName = user.text.toString()
        userPassword = password.text.toString()
        viewModel.saveAccount(userName, userPassword)
    }
}