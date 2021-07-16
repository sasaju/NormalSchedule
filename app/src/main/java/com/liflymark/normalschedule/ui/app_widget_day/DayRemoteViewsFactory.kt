package com.liflymark.normalschedule.ui.app_widget_day

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean

class DayRemoteViewsFactory(private val mContext: Context, intent: Intent?) :RemoteViewsFactory {
    companion object {
        var mList: MutableList<OneByOneCourseBean> = mutableListOf()

    }

    override fun onCreate() {

//        for (i in 0..4) {
//            mList.add(OneByOneCourseBean("${i}课程",0,0,0, Color.White))
//        }

    }

    override fun onDataSetChanged() {
//        val myScope : CoroutineScope = object : CoroutineScope {
//            override val coroutineContext: CoroutineContext = Dispatchers.IO // no job added i.e + SupervisorJob()
//        }
        val a = Repository.loadAllCourse3()
        a?.get(0)?.let { mList.addAll(it.filter { it1 ->
            it1.whichColumn == 1
        }) }
    }

    override fun onDestroy() {
        mList.clear()
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (position < 0 || position >= mList.size) return null
        val content = mList[position]
        val rv = RemoteViews(
            mContext.packageName,
            R.layout.app_widget_day_item
        )
        rv.setTextViewText(R.id.course_name_day, content.courseName)
        val intent = Intent()
        intent.putExtra("content", content.courseName)
        rv.setOnClickFillInIntent(R.id.course_name_day, intent)
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

}