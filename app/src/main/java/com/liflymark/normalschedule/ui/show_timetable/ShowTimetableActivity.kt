package com.liflymark.normalschedule.ui.show_timetable

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import kotlinx.android.synthetic.main.activity_show_timetable.*
import kotlinx.android.synthetic.main.fragment_header_toolbar.*
import kotlinx.android.synthetic.main.fragment_show_course_list.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class ShowTimetableActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(ShowTimetableViewModel::class.java) }
    private lateinit var courseList: List<CourseBean>
    private lateinit var adapter: ScheduleRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        // 设置toolbar和状态栏
        val decorView = window.decorView
        decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_show_timetable)
        toolbar.title=""
        setSupportActionBar(toolbar)
        // setContentView(R.layout.activity_show_timetable)
        tv_date.text = GetDataUtil.getNowDateTime()
        refreshToolbar(0)
        GlobalScope.launch {
            if (!intent.getBooleanExtra("isSaved", false)) {
                val allCourseListJson = intent.getStringExtra("courseList")?:""
                val allCourseList = Convert.jsonToAllCourse(allCourseListJson)
                viewModel.insertOriginalCourse(allCourseList)
            }
        }

        viewModel.courseDatabaseLiveDataVal.observe(this, Observer { it ->
            if (intent.getBooleanExtra("isSaved", false)) {
                courseList = it.getOrNull()!!
            } else {
                val allCourseListJson = intent.getStringExtra("courseList") ?: ""
                val allCourseList = Convert.jsonToAllCourse(allCourseListJson)
                val courseList_ = mutableListOf<CourseBean>()
                for (singleCourse in allCourseList) {
                    courseList_.add(Convert.courseResponseToBean(singleCourse))
                    // Log.d("Repository", Convert().courseResponseToBean(singleCourse).toString())
                }
                courseList = courseList_
            }
            val layoutManager = LinearLayoutManager(this)
            all_week_schedule_recyclerview.layoutManager = layoutManager
            adapter = ScheduleRecyclerAdapter(this, courseList)
            all_week_schedule_recyclerview.adapter = adapter
            val snapHelper = ViewPagerSnapHelper(this)
            snapHelper.attachToRecyclerView(all_week_schedule_recyclerview)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            adapter.notifyDataSetChanged()
        })
        viewModel.loadAllCourse()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    fun refreshToolbar(position: Int){
        val firstWeekMonthDayDate = GetDataUtil.getFirstWeekMondayDate()
        val sdf = SimpleDateFormat()
        sdf.format(firstWeekMonthDayDate)
        tv_week.text = "第${position+1}周  非本周"
        if (position+1 == GetDataUtil.whichWeekNow(firstWeekMonthDayDate) && GetDataUtil.dateMinusDate(sdf, GetDataUtil.getNowTime()) > 0){
            tv_week.text = "第${position+1}周  当前周"
        }

    }

    private fun showWaitingDialog() {
        /* 等待Dialog具有屏蔽其他控件的交互能力
     * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
     * 下载等事件完成后，主动调用函数关闭该Dialog
     */

    }

}
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // 设置toolbar和状态栏
//        val decorView = window.decorView
//        decorView.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        window.statusBarColor = Color.TRANSPARENT
//        setContentView(R.layout.activity_show_timetable)
//        toolbar.title=""
//        setSupportActionBar(toolbar)
//
//
//        // 设置RecyclerView
//        thread {
//            val courseList = viewModel.loadAllCourse()
//            refreshUi(courseList)
//        }
//
//        // 获取TextView的宽度width
//        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        val dm = DisplayMetrics()
//        wm.defaultDisplay.getMetrics(dm)    // 由于需要兼容旧API，暂时无法更新
//        val a = dm.widthPixels
//        Log.d("ShowTimetableActivity", DensityUtil.px2dip(this,a.toFloat()).toString())
//
//
//
//    }
//


//    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
//        super.onCreate(savedInstanceState, persistentState)
//        val decorView = window.decorView
//        decorView.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        window.statusBarColor = Color.TRANSPARENT
//        setContentView(R.layout.activity_main)
//    }
