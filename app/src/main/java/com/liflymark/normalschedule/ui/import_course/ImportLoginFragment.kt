package com.liflymark.normalschedule.ui.import_course

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import kotlinx.android.synthetic.main.fragment_import_login.*

class ImportLoginFragment: Fragment() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(CourseViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_import_login, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        var userName = user.text.toString()
//        var userPassword = password.text.toString()
        // val yzm = ivCode.toString()
        val yzm = ""

        // Toast.makeText(activity, "已运行", Toast.LENGTH_SHORT).show()
        btnSign.setOnClickListener {
            // Toast.makeText(activity, "已运行", Toast.LENGTH_SHORT).show()
            val userName = user.text.toString()
            val userPassword = password.text.toString()
            Log.d("userName", userName)
            if (userName == "") {
                Toast.makeText(activity, "请输入学号", Toast.LENGTH_SHORT).show()
            }
            if (userPassword == "") {
                Toast.makeText(activity, "请输入密码", Toast.LENGTH_SHORT).show()
            }


            viewModel.refreshId()
            viewModel.putValue(userName, userPassword, yzm)
//            Repository.getCourse("","", "", "")
            viewModel.courseLiveData.observe(viewLifecycleOwner, Observer { result ->
                Log.d("T", "OK")
                Log.d("Result", result.toString())
                Toast.makeText(activity, "成", Toast.LENGTH_SHORT).show()
            })
            // Log.d("Test", viewModel.courseLiveData.toString())
        }
    }
}