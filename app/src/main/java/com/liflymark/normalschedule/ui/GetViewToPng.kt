package com.liflymark.normalschedule.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.coroutineScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.utils.LoadingDialog
import com.liflymark.normalschedule.logic.utils.StringPicker
import com.liflymark.normalschedule.ui.class_course.ui.theme.NormalScheduleTheme
import kotlinx.coroutines.launch
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream


class GetViewToPng : ComponentActivity() {
    @OptIn(ExperimentalPagerApi::class, androidx.compose.material.ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val view = LayoutInflater.from(this).inflate(R.layout.activity_test, null, false)
//        val drawingCacheEnabled = true
//        setContentView(view)
//        view.setDrawingCacheEnabled(drawingCacheEnabled)
//        view.buildDrawingCache(drawingCacheEnabled)
//        view.measure(
//            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//        );
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//        val drawingCache: Bitmap = view.getDrawingCache()
//        val bitmap: Bitmap?
//        bitmap = Bitmap.createBitmap(drawingCache)
//        view.setDrawingCacheEnabled(false)
////        MediaStore.Images.Media.insertImage(contentResolver, bitmap!!, "title", "")
//        saveImage(bitmap,this, "test")
//        lifecycle.coroutineScope.launch {
//            
//        }
        setContent { 
            NormalScheduleTheme {
                LoadingDialog(showDialog = true, text = "正在请求", onDismissRequest = {  })
            }
        }
    }

    private fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName)
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory = File(Environment.getExternalStorageDirectory().toString() + separator + folderName)
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = System.currentTimeMillis().toString() + ".png"
            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            if (file.absolutePath != null) {
                val values = contentValues()
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                // .DATA is deprecated in API 29
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
        }
    }

    private fun contentValues() : ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 获取一个 View 的缓存视图
     * (前提是这个View已经渲染完成显示在页面上)
     * @param view
     * @return
     */
    fun getCacheBitmapFromView(view: View): Bitmap? {

        return null
    }
}