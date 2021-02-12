package com.liflymark.normalschedule.ui.import_course

import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.graphics.contains
import androidx.core.graphics.drawable.toIcon
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.model.IdResponse
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_import_login.*
import java.io.InputStream

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
        viewModel.getId()// 获取cookie
        viewModel.idLiveData.observe(viewLifecycleOwner, Observer { result ->
            // 仅装有id
            // Log.d("ImportLoginFragment", result.getOrNull().toString())
            val idResponse = result.getOrNull()
            if (idResponse == null){
                Toast.makeText(activity, "服务异常，无法登陆", Toast.LENGTH_SHORT).show()
            } else {
                this.id = idResponse.id
                Toast.makeText(activity, "服务正常，可以登陆", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.courseLiveData.observe(viewLifecycleOwner, Observer { result ->

            if (result.getOrNull() == null) {
                Toast.makeText(activity, "登陆异常，重启app试试", Toast.LENGTH_SHORT).show()
            }
            when(result.getOrNull()?.status) {
                "yes" -> {
                    Toast.makeText(activity, "登陆成功，解析成功", Toast.LENGTH_SHORT).show()
                    // viewModel.saveAccount(userName, userPassword)
                }
                "no" -> {
                    Toast.makeText(activity, "登陆成功，解析异常，请务必检查课程表是否正确", Toast.LENGTH_SHORT).show()
                    // viewModel.saveAccount(userName, userPassword)
                }
                else -> Toast.makeText(activity, result.getOrNull()!!.status, Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(activity, result.getOrNull()!!.status, Toast.LENGTH_SHORT).show()
        })

        ivCode.setOnClickListener {
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
    }
}