package com.liflymark.normalschedule.ui.show_timetable

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.annotation.LongDef
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.Dialog
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import com.liflymark.normalschedule.logic.utils.betterrecyclerview.EndlessRecyclerOnScrollListener
import com.liflymark.normalschedule.ui.about.AboutActivity
import com.liflymark.normalschedule.ui.course_detail.CourseDetailActivity
import com.liflymark.normalschedule.ui.import_show_score.ImportScoreActivity
import com.liflymark.normalschedule.ui.set_background.DefaultBackground
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.OnItemLongClickListener
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_import_score.*
import kotlinx.android.synthetic.main.activity_show_timetable.*
import kotlinx.android.synthetic.main.fragment_header_toolbar.*
import kotlinx.android.synthetic.main.fragment_import_login.view.*
import kotlinx.android.synthetic.main.fragment_show_course_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class ShowTimetableActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(ShowTimetableViewModel::class.java) }
    private lateinit var courseList: List<CourseBean>
    private lateinit var adapter: ScheduleRecyclerAdapter
    private val nowWeek =  GetDataUtil.whichWeekNow(GetDataUtil.getFirstWeekMondayDate())


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        // 设置toolbar和状态栏
        val decorView = window.decorView
        decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT
//        val config = BarConfig.newInstance()          // 创建配置对象
//                .fitWindow(false)                          // 布局是否侵入状态栏（true 不侵入，false 侵入）
//                .color(Color.TRANSPARENT)                         // 状态栏背景颜色（色值）
//                .light(true)
        setContentView(R.layout.activity_show_timetable)
        toolbar.title=""
        setSupportActionBar(toolbar)
//        UltimateBarX.with(this)                       // 对当前 Activity 或 Fragment 生效
//                .config(config)                           // 使用配置
//                .applyStatusBar()                         // 应用到状态栏
        // setContentView(R.layout.activity_show_timetable)
        tv_date.text = GetDataUtil.getNowDateTime()
        refreshToolbar(0)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        navView.setNavigationItemSelectedListener {

            drawerLayout.closeDrawers()
            when(it.itemId){
                R.id.set_background -> {
                    val intent = Intent(this, DefaultBackground::class.java)
                    startActivity(intent)
                }
                R.id.navGrade -> {
                    val intent = Intent(this, ImportScoreActivity::class.java)
                    startActivity(intent)
                }
                R.id.about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                }
            }

            true
        }

        val dialog = MaterialDialog(this@ShowTimetableActivity).apply {
            title(text = "保存课程表至本地")
            message(text = "正在保存中....请不要关闭APP！")
        }
        GlobalScope.launch {
            if (!intent.getBooleanExtra("isSaved", false)) {
                val allCourseListJson = intent.getStringExtra("courseList")?:""
                val allCourseList = Convert.jsonToAllCourse(allCourseListJson)
                val user = intent.getStringExtra("user")?:""
                val password = intent.getStringExtra("password")?:""
                runOnUiThread {
                    dialog.show()
                }
                viewModel.insertOriginalCourse(allCourseList)
                viewModel.saveAccount(user, password)
                runOnUiThread{
                    dialog.message(text = "当前课表已保存")
                    dialog.positiveButton(text = "知道了")
                }
            }
        }



        val layoutManager = LinearLayoutManager(this)
        object : EndlessRecyclerOnScrollListener(layoutManager) {
            override fun onLoadMore(current_page: Int) {
                TODO()
            }
        }

        viewModel.courseDatabaseLiveDataVal.observe(this, Observer { it ->
            courseList = if (intent.getBooleanExtra("isSaved", false)) {
                it.getOrNull()!!
            } else {
                val allCourseListJson = intent.getStringExtra("courseList") ?: ""
                val allCourseList = Convert.jsonToAllCourse(allCourseListJson)
                val courseList_ = mutableListOf<CourseBean>()
                for (singleCourse in allCourseList) {
                    courseList_.add(Convert.courseResponseToBean(singleCourse))
                    // Log.d("Repository", Convert().courseResponseToBean(singleCourse).toString())
                }
                courseList_
            }
            all_week_schedule_recyclerview.layoutManager = layoutManager
            adapter = ScheduleRecyclerAdapter(this, courseList)
            all_week_schedule_recyclerview.adapter = adapter
            val snapHelper = ViewPagerSnapHelper(this)
            snapHelper.attachToRecyclerView(all_week_schedule_recyclerview)
            layoutManager.isItemPrefetchEnabled = true
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            adapter.notifyDataSetChanged()
            moveToPosition(layoutManager, nowWeek - 1)
            refreshToolbar(nowWeek - 1)
        })
        viewModel.loadAllCourse()

        viewModel.backgroundUriStringLiveData.observe(this, Observer { it ->
            val result = it.getOrNull()
            if (result == null){
                Log.d("ShowTimetableAc", "result is null")
                setBackground(R.drawable.main_background_4)
            } else {
                val imageUri = Uri.parse(result.userBackground)
                setBackground(imageUri)
            }
        })
        viewModel.setBackground()

        all_date.setOnClickListener{
            moveToPosition(layoutManager, nowWeek - 1)
            refreshToolbar(nowWeek - 1)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.backgroundUriStringLiveData.observe(this, Observer { it ->
            val result = it.getOrNull()
            when {
                result == null -> {
                    setBackground(R.drawable.main_background_4)
                }
                result.userBackground == "0" -> {
                    setBackground(R.drawable.main_background_4)
                }
                else -> {
                    val imageUri = Uri.parse(result.userBackground)
                    setBackground(imageUri)
                }
            }
        })
        viewModel.setBackground()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_course -> {
                Toasty.info(this, "暂未开发", Toasty.LENGTH_SHORT).show()
            }
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    fun refreshToolbar(position: Int){
        val firstWeekMonthDayDate = GetDataUtil.getFirstWeekMondayDate()
        val sdf = SimpleDateFormat()
        sdf.format(firstWeekMonthDayDate)
        tv_week.text = "第${position+1}周  非本周"
        if (position+1 == GetDataUtil.whichWeekNow(firstWeekMonthDayDate) && GetDataUtil.dateMinusDate(
                GetDataUtil.getNowTime(),
                sdf
            ) >= 0){
            tv_week.text = "第${position+1}周  当前周"
        }

    }


    private fun moveToPosition(manager: LinearLayoutManager, n: Int) {
        manager.scrollToPositionWithOffset(n, 0);
        manager.stackFromEnd = true;
    }


    private fun showWaitingDialog() {
        /* 等待Dialog具有屏蔽其他控件的交互能力
     * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
     * 下载等事件完成后，主动调用函数关闭该Dialog
     */

    }

    private fun setBackground(path:Uri){
        Glide.with(this).load(path)
                .into(object : SimpleTarget<Drawable?>() {
                    override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable?>?
                    ) {
                        drawerLayout.background = resource
                    }
                })
    }

    private fun setBackground(backgroundId: Int){
        Glide.with(this).load(backgroundId)
                .into(object : SimpleTarget<Drawable?>() {
                    override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable?>?
                    ) {
                        drawerLayout.background = resource
                    }
                })
    }

    val onItemClickListener = OnItemClickListener { item, _ ->
        if (item is CourseItem) {
            val realCourseMessage = item.getData().courseName.split("\n")
            GlobalScope.launch{
                val courseBeanList = viewModel.loadCourseByNameAndStart(realCourseMessage[0],
                        item.getData().start+1,
                        item.getData().whichColumn+1)
                runOnUiThread{
                    val dialog = Dialog.getClassDetailDialog(this@ShowTimetableActivity, courseBeanList[0])
                    dialog.show()
                }
            }

        }
    }


    val onItemLongClickListener = OnItemLongClickListener { item, _ ->
        if (item is CourseItem) {
            val realCourseName = item.getData().courseName.split("\n")[0]
            viewModel.deleteCourseBeanByNameLiveData.observe(this, Observer {
                if (it.isFailure){
                    Toasty.error(this,"删除操作失败", Toasty.LENGTH_SHORT).show()
                }
            })

            val dialog = MaterialDialog(this)
                    .title(text = "你在进行一步敏感操作")
                    .message(text = "你将删除《${realCourseName}》的所有课程\n无法恢复，务必谨慎删除！！！\n如失误删除请重新导入")
                    .positiveButton(text = "我已知晓，仍然删除") { _ ->
                        viewModel.deleteCourse(realCourseName)
                        Toasty.success(this, "删除成功，重启app生效", Toasty.LENGTH_LONG).show()
                    }
                    .negativeButton(text = "取消") { _ ->
                        Toasty.info(this,"删除操作取消", Toasty.LENGTH_SHORT).show()
                    }
                    .cancelOnTouchOutside(false)
            dialog.show()
            return@OnItemLongClickListener true
        }
        false
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
