package com.liflymark.normalschedule.ui.import_show_score

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gyf.immersionbar.ImmersionBar
import com.liflymark.normalschedule.databinding.ActivityImportScoreBinding
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.Dialog
import com.liflymark.normalschedule.logic.utils.RomUtil
import es.dmoral.toasty.Toasty

class ImportScoreActivity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(ImportScoreViewModel::class.java) }
    private lateinit var binding: ActivityImportScoreBinding
    var userName = ""
    var userPassword = ""
    private var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImportScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSign.text = "登陆以导入成绩"
//        binding.inputId.hint = "请输入统一认证密码"
        Toasty.Config.getInstance().setTextSize(15).apply()
        setSupportActionBar(binding.importToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        supportActionBar?.setHomeButtonEnabled(true); //设置返回键可用
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
        if (!RomUtil.isVivo){ binding.tvTitle.text = "登陆HBU教务系统" }
        val saved = viewModel.isAccountSaved()
        if (saved) {
            binding.user0.text = SpannableStringBuilder(viewModel.getSavedAccount()["user"])
            binding.password0.text = SpannableStringBuilder(viewModel.getSavedAccount()["password"])
        }

        val progressDialog = Dialog.getProgressDialog(this, "正在加载内容")
        progressDialog.show()
        viewModel.getId()// 获取cookie
        viewModel.idLiveData.observe(this) {
            val idResponse = it?.id
            if (idResponse == null || idResponse == ""){
                Toasty.error(this, "服务异常，去工具箱-公告栏看看吧", Toasty.LENGTH_SHORT).show()
                progressDialog.dismiss()
            } else {
                this.id = idResponse
                Toasty.success(this, "服务正常，可以登陆", Toasty.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        }

        viewModel.scoreLiveData.observe(this, Observer { result ->

            if (result == null) {
                Toasty.error(this, "登陆异常，重启app试试", Toasty.LENGTH_SHORT).show()
                progressDialog.dismiss()
            } else {
                val allGradeList = result.grade_list
                when (result.result) {
                    // 此处服务信息传输易造成错误 后期需修改
                    "登陆成功" -> {
                        // saveAccount()
                        if (binding.saveOrNot.isChecked) {
                            viewModel.saveAccount(userName, userPassword)
                        } else {
                            viewModel.saveAccount(userName, "")
                        }
                        val intent = Intent(this, ShowScoreActivity::class.java).apply {
                            putExtra("grade_list_string", Convert.allGradeToJson(allGradeList))
                        }
                        startActivity(intent)
                        this.finish()
                    }

                    else -> {
                        result.result.let { Toasty.error(this, it, Toasty.LENGTH_SHORT).show() }
                    }
                }


            }
        })


        binding.btnSign.setOnClickListener {
            // 判断是否输入学号密码并提交数据至ViewModel层以更新数据
            userName = binding.user0.text.toString()
            userPassword = binding.password0.text.toString()
            progressDialog.show()
            when {
                userName == "" -> {
                    Toasty.info(this, "请输入学号", Toasty.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
                userPassword == "" -> {
                    Toasty.info(this, "请输入密码", Toasty.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
                else -> viewModel.putValue(userName, userPassword, id)
            }
        }


    }
}