package com.liflymark.normalschedule.ui.show_timetable

import android.util.Log
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView


class ViewPagerSnapHelper(private val activity: ShowTimetableActivity) : PagerSnapHelper() {

    override fun findTargetSnapPosition(
            layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int
    ): Int {
        val position = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
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
