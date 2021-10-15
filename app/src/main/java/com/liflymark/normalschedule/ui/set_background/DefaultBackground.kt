package com.liflymark.normalschedule.ui.set_background

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import coil.load
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.utils.CoilEngine
import com.liflymark.normalschedule.logic.utils.GifLoader
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_default_background.*
import kotlinx.android.synthetic.main.activity_show_score.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File


class DefaultBackground : AppCompatActivity() {
    private val REQUEST_CODE_CHOOSE_BG = 23

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(DefaultBackgroundViewModel::class.java)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_default_background)
        setSupportActionBar(image_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        supportActionBar?.setHomeButtonEnabled(true); //设置返回键可用
        userImage.setOnClickListener {
            PictureSelector.create(this)
                .openGallery(1)
                .imageEngine(CoilEngine.create())
                .isCamera(false)
                .isGif(true)
                .selectionMode(PictureConfig.SINGLE)
                .forResult(PictureConfig.CHOOSE_REQUEST)
        }
        defaultBackground1.setOnClickListener {
            launch {
                viewModel.userBackgroundUri = "0"
                viewModel.updateBackground()
                Toasty.success(this@DefaultBackground, "已切换回默认背景", Toasty.LENGTH_SHORT).show()
            }

        }

        imageView4.setOnClickListener {
            launch {
                viewModel.userBackgroundUri = "0"
                viewModel.updateBackground()
                Toasty.success(this@DefaultBackground, "已切换回默认背景", Toasty.LENGTH_SHORT).show()
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    Log.d("BackGround", PictureSelector.obtainMultipleResult(data).toString())
                    val uri = PictureSelector.obtainMultipleResult(data)[0].path
                    launch {
                        val realUri = if(SDK_INT<29){ Uri.fromFile(File(uri)).toString() }else{ uri }
                        viewModel.userBackgroundUri = realUri
                        viewModel.updateBackground()
                        val imageLoader = GifLoader(this@DefaultBackground)
                        Log.d("BackGround",uri.toString())
                        if (SDK_INT<29) {
                            userImage.load(File(uri), imageLoader)
                        }else{
                            userImage.load(uri)
                        }
                        userImageText.text = "点击此处更换"
                        Toasty.success(this@DefaultBackground, "读取成功，返回看看吧", Toasty.LENGTH_SHORT)
                            .show()
                        Log.d("DefaultBackground", uri.toString())
                    }
                }
                else -> {
                }
            }
        }
    }


    private fun launch(block: suspend CoroutineScope.() -> Unit): Job = lifecycleScope.launch {
        lifecycle.whenStarted(block)
    }
}