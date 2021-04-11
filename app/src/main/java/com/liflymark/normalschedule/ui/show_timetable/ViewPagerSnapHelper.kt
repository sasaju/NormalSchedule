package com.liflymark.normalschedule.ui.show_timetable

import android.util.Log
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.liflymark.normalschedule.logic.bean.CourseBean


class ViewPagerSnapHelper(private val activity: ShowTimetableActivity, private val courseList:List<CourseBean>) : PagerSnapHelper() {

    override fun findTargetSnapPosition(
            layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int
    ): Int {
        val position = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
//        if (position >= layoutManager.itemCount-1){
//            activity.refreshToolbar(0)
//            return 0
//        } else if (position < 0){
//            activity.refreshToolbar(layoutManager.itemCount-1)
//            return layoutManager.itemCount-1
//        }
        activity.refreshToolbar(position)
        return position
//        return if (position >= layoutManager.itemCount-1
//        ) {
//            0
//        } else {
//            position
//        }
    }
}
