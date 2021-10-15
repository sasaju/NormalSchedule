package com.liflymark.normalschedule.ui.app_widget_week

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import com.liflymark.normalschedule.ui.app_widget_day.DayRemoteViewsFactory

class WeekRemoteViewFactory(private val mContext: Context, intent: Intent) : RemoteViewsFactory {
    private val mAppWidgetId: Int = (intent.data?.schemeSpecificPart?.toInt() ?: 0) -
            intent.getIntExtra("random", 0)
    companion object {
        var mList: MutableList<List<OneByOneCourseBean>> = mutableListOf()

    }
    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        val a = Repository.loadAllCourse3()
        val nowWeekNum = GetDataUtil.whichWeekNow()
        val nowDayNum = GetDataUtil.getNowWeekNum()
        if (a != null) {
            mList.addAll(a)
        }
        if (!GetDataUtil.startSchool() || GetDataUtil.getNowWeekNum()>19){
            mList.clear()
        }
    }

    override fun onDestroy() {
        mList.clear()
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun getViewAt(p0: Int): RemoteViews {
        val rv = RemoteViews(
            mContext.packageName,
            R.layout.app_widget_day_item
        )
        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}