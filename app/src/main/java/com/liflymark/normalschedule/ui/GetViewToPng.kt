package com.liflymark.normalschedule.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.liflymark.normalschedule.R
import kotlinx.coroutines.launch


class GetViewToPng : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(this).inflate(R.layout.activity_test, null, false)
        val drawingCacheEnabled = true
        setContentView(view)
        view.setDrawingCacheEnabled(drawingCacheEnabled)
        view.buildDrawingCache(drawingCacheEnabled)
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        val drawingCache: Bitmap = view.getDrawingCache()
        val bitmap: Bitmap?
        bitmap = Bitmap.createBitmap(drawingCache)
        view.setDrawingCacheEnabled(false)
        MediaStore.Images.Media.insertImage(contentResolver, bitmap!!, "title", "")
        lifecycle.coroutineScope.launch {

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