package com.liflymark.normalschedule.ui.app_widget_new_day

import android.content.Intent
import android.util.Log
import android.widget.RemoteViewsService
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.GetDataUtil

class DayNewRvService: RemoteViewsService() {
    private val mList: MutableList<OneByOneCourseBean> = mutableListOf()
    override fun onCreate() {
        super.onCreate()
        Log.d("service", "start")
    }
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        val allCourse = Repository.loadAllCourse3()
        val nowWeekNum = GetDataUtil.whichWeekNow()
        val nowDayNum = GetDataUtil.getNowWeekNum()
        allCourse?.get(nowWeekNum)?.let {
            mList.addAll(it.filter { it1 ->
                it1.whichColumn == nowDayNum
            })
        }
        Log.d("DayService", mList.toString())
        return DayNewRemoteFactory(mList)
    }

    override fun onDestroy() {
        mList.clear()
        super.onDestroy()
    }
}