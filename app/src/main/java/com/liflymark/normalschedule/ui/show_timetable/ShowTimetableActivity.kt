package com.liflymark.normalschedule.ui.show_timetable

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.Dialog
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import com.liflymark.normalschedule.logic.utils.betterrecyclerview.EndlessRecyclerOnScrollListener
import com.liflymark.normalschedule.ui.about.AboutActivity
import com.liflymark.normalschedule.ui.add_course.AddCourseActivity
import com.liflymark.normalschedule.ui.import_again.ImportCourseAgain
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
        toolbar.inflateMenu(R.menu.menu_main)
        setSupportActionBar(toolbar)
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
        if (viewModel.getNewUserOrNot())
            newUserGuide()

        val dialog = MaterialDialog(this)
                .title(text = "保存课表至本地")
                .message(text = "正在保存请不要关闭APP....")
                .positiveButton(text = "知道了")

        GlobalScope.launch {
            if (!intent.getBooleanExtra("isSaved", false)) {
                runOnUiThread {
                    dialog.show()
                }
                val allCourseListJson = intent.getStringExtra("courseList")?:""
                val allCourseList = Convert.jsonToAllCourse(allCourseListJson)
                val user = intent.getStringExtra("user")?:""
                val password = intent.getStringExtra("password")?:""
                viewModel.insertOriginalCourse(allCourseList)
                runOnUiThread {
                    dialog.message(text = "已保存至本地")
                }
            }
        }



        val layoutManager = LinearLayoutManager(this)
        object : EndlessRecyclerOnScrollListener(layoutManager) {
            override fun onLoadMore(current_page: Int) {
                TODO()
            }
        }

        viewModel.courseDatabaseLiveDataVal.observe(this, Observer {
            if (intent.getBooleanExtra("isSaved", true)) {
                courseList = it.getOrNull()!!
            } else {
                val allCourseListJson = intent.getStringExtra("courseList") ?: ""
                val allCourseList = Convert.jsonToAllCourse(allCourseListJson)
                val courseList0 = mutableListOf<CourseBean>()
                for (singleCourse in allCourseList) {
                    courseList0.add(Convert.courseResponseToBean(singleCourse))
                    // Log.d("Repository", Convert().courseResponseToBean(singleCourse).toString())
                }
                courseList = courseList0.toList()
            }
            Log.d("ShowTimetableActivity", courseList.toString())
            adapter = ScheduleRecyclerAdapter(this, courseList)
            all_week_schedule_recyclerview.adapter = adapter
            all_week_schedule_recyclerview.layoutManager = layoutManager
            val snapHelper = ViewPagerSnapHelper(this)
            snapHelper.attachToRecyclerView(all_week_schedule_recyclerview)
            layoutManager.isItemPrefetchEnabled = true
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            // adapter.notifyDataSetChanged()
            moveToPosition(layoutManager, nowWeek - 1)
            refreshToolbar(nowWeek - 1)
        })
        viewModel.loadAllCourse()

//        viewModel.updateCourseLiveDataVal.observe(this, Observer {
//            val courseList = it.getOrNull()
//            if (courseList != null){
//                Log.d("updateCourseLiveDataVal", courseList.toString())
//                adapter = ScheduleRecyclerAdapter(this, courseList)
//                all_week_schedule_recyclerview.adapter = adapter
//            }
//        })

//        viewModel.backgroundUriStringLiveData.observe(this, Observer { it ->
//            val result = it.getOrNull()
//            if (result == null){
//                Log.d("ShowTimetableAc", "result is null")
//                setBackground(R.drawable.main_background_4)
//            } else {
//                val imageUri = Uri.parse(result.userBackground)
//                setBackground(imageUri)
//            }
//        })
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
                val intent = Intent(this, AddCourseActivity::class.java)
                startActivity(intent)
                // Toasty.info(this, "暂未开发", Toasty.LENGTH_SHORT).show()
            }
            R.id.import_course -> {
                val dialog = Dialog.getImportAgain(this)
                dialog.show()
                dialog.positiveButton {
                    val intent = Intent(this, ImportCourseAgain::class.java)
                    val p = this.getSharedPreferences("normal_schedule", Context.MODE_PRIVATE)
                    val edit = p.edit()
                    edit.clear()
                    edit.apply()
                    startActivity(intent)
                    this.finish()
                }
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
//        manager.stackFromEnd = true
    }


    private fun showWaitingDialog() {
        /* 等待Dialog具有屏蔽其他控件的交互能力
     * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
     * 下载等事件完成后，主动调用函数关闭该Dialog
     */

    }

    private fun setBackground(path: Uri){
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

    private fun newUserGuide(){
        val display = windowManager.defaultDisplay
        // Load our little droid guy
        val droid = ContextCompat.getDrawable(this, R.drawable.add)
        val droidTarget = Rect(0, 0, (droid?.getIntrinsicWidth() ?: 0) * 2, (droid?.getIntrinsicHeight()
            ?: 0) * 2)
        droidTarget.offset((display.getWidth()*0.45).toInt(), (display.getHeight() * 0.2).toInt())
        TapTargetSequence(this)
            .targets(
                TapTarget.forToolbarNavigationIcon(
                    toolbar, "请仔细阅读提示", "这里被点击或者向左滑动可以查看更多功能\n" +
                            "点击指示位置以继续"
                ).cancelable(false),
                TapTarget.forView(
                    findViewById(R.id.all_date),
                    "日期显示栏 | 点击跳转至当前周",
                    "点击此处立即跳转至当前周 \n点击指示位置以继续"
                )
                    .cancelable(
                        false
                    ),
                // Likewise, this tap target will target the search button
                TapTarget.forToolbarMenuItem(
                    toolbar,
                    R.id.add_course,
                    "点击这里可以增加单个课程",
                    "主要是调课时使用"
                )
                    .dimColor(android.R.color.black)
                    .outerCircleColor(R.color.colorAccent)
                    .targetCircleColor(android.R.color.black)
                    .transparentTarget(true)
                    .textColor(android.R.color.black)
                    .cancelable(false),
                TapTarget.forToolbarMenuItem(
                    toolbar,
                    R.id.import_course,
                    "点击这里可以重新导入课程 | 请谨慎使用该功能",
                    "重新导课将会清空当前课表，请谨慎使用"
                )
                    .dimColor(android.R.color.black)
                    .outerCircleColor(R.color.colorAccent)
                    .targetCircleColor(android.R.color.black)
                    .transparentTarget(true)
                    .textColor(android.R.color.black)
                    .cancelable(false),
                TapTarget.forBounds(droidTarget, "长按可以选择删除该课程 \n单击可以查看课程详情")
                    .transparentTarget(true)
                    .cancelable(false)
//                TapTarget.forToolbarMenuItem(toolbar, R.id.add_course, "").cancelable(false)
            )
            .listener(object : TapTargetSequence.Listener {
                // This listener will tell us when interesting(tm) events happen in regards
                // to the sequence
                override fun onSequenceFinish() {
                    viewModel.saveUserVersion()
                }

                override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {
                    // Perform action for the current target
                }

                override fun onSequenceCanceled(lastTarget: TapTarget) {
                    // Boo
                }
            })
            .start()
    }

    val onItemClickListener = OnItemClickListener { item, _ ->
        if (item is CourseItem) {
            val realCourseMessage = item.getData().courseName.split("\n")
            GlobalScope.launch{
                val courseBeanList = viewModel.loadCourseByNameAndStart(
                    realCourseMessage[0],
                    item.getData().start + 1,
                    item.getData().whichColumn + 1
                )
                runOnUiThread{
                    val dialog = Dialog.getClassDetailDialog(
                        this@ShowTimetableActivity,
                        courseBeanList[0]
                    )
                    dialog.show()
                }
            }

        }
    }


    val onItemLongClickListener = OnItemLongClickListener { item, _ ->
        if (item is CourseItem) {
            val realCourseName = item.getData().courseName.split("\n")[0]
            viewModel.deleteCourseBeanByNameLiveData.observe(this, Observer {
                if (it.isFailure) {
                    Toasty.error(this, "删除操作失败", Toasty.LENGTH_SHORT).show()
                }
            })

            val dialog = MaterialDialog(this)
                    .title(text = "你在进行一步敏感操作")
                    .message(text = "你将删除《${realCourseName}》的所有课程\n无法恢复，务必谨慎删除！！！\n如失误删除请重新导入")
                    .positiveButton(text = "仍然删除") { _ ->
                        viewModel.deleteCourse(realCourseName)
                        viewModel.updateCourse()
                        Toasty.success(this, "成功，重启app生效", Toasty.LENGTH_LONG).show()
                    }
                    .negativeButton(text = "取消") { _ ->
                        Toasty.info(this, "删除操作取消", Toasty.LENGTH_SHORT).show()
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
