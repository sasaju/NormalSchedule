package com.liflymark.normalschedule.ui.app_widget_new_day

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.GetDataUtil

class DayNewRemoteFactory(
//    private val mContext: Context,
    private val mList:MutableList<OneByOneCourseBean>
    ): RemoteViewsFactory {

    override fun onCreate() {}

    override fun onDataSetChanged() {
        if (!GetDataUtil.startSchool() || GetDataUtil.getNowWeekNum() > 19) {
            mList.clear()
        }
    }

    override fun onDestroy() {
        val intent = Intent(NormalScheduleApplication.context,DayNewRvService::class.java)
        NormalScheduleApplication.context.stopService(intent)
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (position < 0 || position >= mList.size) return null
        mList.sortBy {
            it.start
        }
        val content = mList[position]
        val nameSplit = content.courseName.split("\n").toMutableList()
        if (nameSplit.size < 2){
            repeat(3-nameSplit.size){
                nameSplit.add("")
            }
        }
        val rv = RemoteViews(
            NormalScheduleApplication.context.packageName,
            R.layout.appwidget_new_day_item
        )
        val startTime = getStartTime(content.start)
        val endTime = getEndTime(content.end)
        val needName = "$startTime - $endTime"
        val nowWeek = GetDataUtil.whichWeekNow()+1
        rv.setTextViewText(R.id.now_week, nowWeek.toString())
        rv.setTextViewText(R.id.app_course_name_1, nameSplit[0])
        rv.setTextViewText(R.id.app_course_teacher, "${nameSplit[1]}\n${nameSplit[2]}")
        rv.setTextViewText(R.id.app_course_time, needName)

        val fillIntent = Intent()
        val extras = Bundle()
        extras.putInt(DayNewWidgetProvider.extraItem, position)
        fillIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.single_course_app,fillIntent)
        Log.d("DayNEwFac", "u1")
        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
    fun getStartTime(rowNumber: Int): String {
        return when(rowNumber){
            1->"08:00"
            2->"08:55"
            3->"10:10"
            4->"11:05"
            5->"14:30"
            6->"15:25"
            7->"16:20"
            8->"17:15"
            9->"19:00"
            10->"19:55"
            11->"20:50"
            else->"00:00"
        }
    }

    fun getEndTime(rowNumber: Int): String {
        return when(rowNumber){
            1->"08:45"
            2->"09:40"
            3->"10:55"
            4->"11:50"
            5->"15:15"
            6->"16:10"
            7->"17:05"
            8->"18:00"
            9->"19:45"
            10->"20:40"
            11->"21:35"
            else->"00:00"
        }
    }
}