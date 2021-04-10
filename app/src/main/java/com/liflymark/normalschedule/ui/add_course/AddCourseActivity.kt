package com.liflymark.normalschedule.ui.add_course

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toolbar
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.customview.getCustomView
import com.liflymark.icytimetable.dp
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.utils.Dialog
import com.liflymark.normalschedule.ui.import_course.CourseViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_add_course.*
import kotlinx.android.synthetic.main.activity_default_background.*
import kotlinx.android.synthetic.main.activity_default_background.image_toolbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class AddCourseActivity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(AddCourseActivityViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_add_course)
        val tool_bar = findViewById<androidx.appcompat.widget.Toolbar?>(R.id.add_course_toolbar)
        tool_bar.fitsSystemWindows = true   
        course_detail.fitsSystemWindows = true
        setSupportActionBar(tool_bar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        supportActionBar?.setHomeButtonEnabled(true); //设置返回键可用


        ll_weeks.setOnClickListener {
            val dialog = Dialog.getSelectClassTime(this)
            val dialogView = dialog.getCustomView()
            val startWeek = dialogView.findViewById<EditText>(R.id.select_start_week)
            val endWeek = dialogView.findViewById<EditText>(R.id.select_end_week)
            Log.d("AddCourseActivity", "123")
            dialog.apply {
                positiveButton {
                    val result = viewModel.setClassTime(startWeek, endWeek)
                    Toasty.info(this@AddCourseActivity, result, Toasty.LENGTH_SHORT).show()
                    if (result == "设置成功"){
                        dismiss()
                    }
                }
            }
            dialog.show()
        }
        ll_time.setOnClickListener {
            Dialog.getSelectWeekAndSection(this)
        }
        save_button.setOnClickListener {
            setCourse(course_name_text, et_teacher, et_room)
        }

        viewModel.updateUiLiveData.observe(this){
            et_weeks_.text = it
        }
    }

    fun setClassWeek(week:Int, startSection: Int, endSection: Int){
        val result = viewModel.setClassWeek(week, startSection, endSection)
        et_time.text = result
    }

    fun setCourse(courseName:EditText, teacher:EditText, buildName: EditText){
        GlobalScope.launch {
            val result = viewModel.insertCourse(courseName, teacher, buildName)
            runOnUiThread(){
                Toasty.info(this@AddCourseActivity, result, Toasty.LENGTH_SHORT).show()
            }
            if (result == "更新成功，重启生效")
                this@AddCourseActivity.finish()
        }
    }
}