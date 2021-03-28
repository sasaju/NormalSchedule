package com.liflymark.normalschedule.ui.set_background

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.Picture
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.drawToBitmap
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.bumptech.glide.Glide
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.ui.show_timetable.ShowTimetableViewModel
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_default_background.*
import kotlinx.android.synthetic.main.activity_show_score.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DefaultBackground : AppCompatActivity() {
    private val REQUEST_CODE_CHOOSE_BG = 23

    private val viewModel by lazy { ViewModelProviders.of(this).get(DefaultBackgroundViewModel::class.java) }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_default_background)
        setSupportActionBar(image_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        supportActionBar?.setHomeButtonEnabled(true); //设置返回键可用
        userImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/*"
                }
            try {
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_BG)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
        defaultBackground1.setOnClickListener {
            launch {
                viewModel.userBackgroundUri = "0"
                viewModel.updateBackground()
                Toasty.success(this@DefaultBackground,"已切换回默认背景", Toasty.LENGTH_SHORT).show()
            }

        }

        imageView4.setOnClickListener {
            launch {
                viewModel.userBackgroundUri = "0"
                viewModel.updateBackground()
                Toasty.success(this@DefaultBackground,"已切换回默认背景", Toasty.LENGTH_SHORT).show()
            }
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE_BG && resultCode == RESULT_OK){
            val uri = data?.data
            if (uri != null) {
//                viewModel.table.background = uri.toString()
                Log.d("DefaultBackground", uri.toString())
                launch {
                    viewModel.userBackgroundUri = uri.toString()
                    viewModel.updateBackground()
                    Glide.with(this@DefaultBackground).load(uri)
                            .fitCenter()
                            .into(userImage)
                    userImageText.text = "点击此处更换"
                    Toasty.success(this@DefaultBackground, "读取成功，请重启app以应用修改",Toasty.LENGTH_SHORT).show()
                    Log.d("DefaultBackground", uri.toString())
                }

            }
        }
    }

    private fun launch(block: suspend CoroutineScope.() -> Unit): Job = lifecycleScope.launch {
        lifecycle.whenStarted(block)
    }
}