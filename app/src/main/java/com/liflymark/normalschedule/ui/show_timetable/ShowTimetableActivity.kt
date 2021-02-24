package com.liflymark.normalschedule.ui.show_timetable

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.liflymark.icytimetable.IcyTimeTableHelper
import com.liflymark.icytimetable.IcyTimeTableManager
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.DensityUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_show_timetable.*
import kotlinx.android.synthetic.main.fragment_header_toolbar.*
import kotlinx.android.synthetic.main.fragment_show_course_list.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.handleCoroutineException
import kotlin.concurrent.thread

class ShowTimetableActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(ShowTimetableViewModel::class.java) }
    private lateinit var courseList: List<CourseBean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_timetable)
        viewModel.courseDatabaseLiveDataVal.observe(this, Observer { it ->
            courseList = it.getOrNull()!!
            refreshUi(courseList)

        })
        viewModel.loadAllCourse()
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
    private fun refreshUi(courseList: List<CourseBean>){
//        this.courseList = courseList
//        val layoutManager = LinearLayoutManager(this)
//        schedule_recyclerview.layoutManager = layoutManager
//        val adapter = ScheduleRecyclerAdapter(courseList, 0)
//        schedule_recyclerview.adapter = adapter
//
        val data = Convert.courseBeanToOneByOne(courseList).toList()

        val totalCoursePerDay = 10
        val columnCount = 7
        val gapFilling = IcyTimeTableHelper.gapFilling(data[0], totalCoursePerDay, columnCount)
        val icyRowInfo = IcyTimeTableHelper.getIcyRowInfo(gapFilling)

        Log.d("ShowTimetableActivity",data[0].toString())

        val adapter = GroupAdapter<GroupieViewHolder>()
        schedule_recyclerview.addItemDecoration(
            MyRowInfoDecoration(
                resources.getDimensionPixelSize(R.dimen.paddingLeft),
                resources.getDimensionPixelSize(R.dimen.perCourseHeight),
                Color.BLACK,
                resources.getDimension(R.dimen.numberSize),
                resources.getDimension(R.dimen.textSize)
                ,
                icyRowInfo,
                totalCoursePerDay
            )
        )
        schedule_recyclerview.addItemDecoration(
            MyColInfoDecoration(columnCount,resources.getDimensionPixelSize(R.dimen.paddingTop),Color.GRAY,Color.WHITE,Color.BLUE,
                resources.getDimension(R.dimen.textSize))
        )
        schedule_recyclerview.layoutManager = IcyTimeTableManager(
            45,
            resources.getDimensionPixelSize(R.dimen.perCourseHeight),
            columnCount,
            totalCoursePerDay
        ) {
            gapFilling[it]
        }
        schedule_recyclerview.adapter = adapter
        gapFilling.map {
            when (it) {
                is IcyTimeTableManager.EmptyCourseInfo -> SpaceItem()
                is OneByOneCourseBean -> CourseItem(it)
                else -> SpaceItem()
            }
        }.let(adapter::update)

    }
}


//    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
//        super.onCreate(savedInstanceState, persistentState)
//        val decorView = window.decorView
//        decorView.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        window.statusBarColor = Color.TRANSPARENT
//        setContentView(R.layout.activity_main)
//    }
