package com.liflymark.normalschedule.ui.show_timetable

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.liflymark.icytimetable.IcyTimeTableHelper
import com.liflymark.icytimetable.IcyTimeTableManager
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.Convert
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_show_course_list.*

class ScheduleRecyclerAdapter(private val activity: ShowTimetableActivity,
        private val courseList: List<CourseBean>,
): RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val singleWeekCourseRecyclerView: RelativeLayout  = view.findViewById(R.id.single_week_schedule)
        var scheduleRecyclerView: RecyclerView = view.findViewById(R.id.schedule_recyclerview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                  .inflate(R.layout.fragment_show_course_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        refreshUi(holder.scheduleRecyclerView, courseList, position)
        Log.d("Adapter", position.toString())
    }


    override fun getItemCount() = courseList.size - 5

    private fun refreshUi(schedule_recyclerview: RecyclerView,courseList: List<CourseBean>, position: Int): RecyclerView{
//        this.courseList = courseList
//        val layoutManager = LinearLayoutManager(this)
//        schedule_recyclerview.layoutManager = layoutManager
//        val adapter = ScheduleRecyclerAdapter(courseList, 0)
//        schedule_recyclerview.adapter = adapter
//
        val data = Convert.courseBeanToOneByOne(courseList).toList()

        val totalCoursePerDay = 10
        val columnCount = 7
        val gapFilling = IcyTimeTableHelper.gapFilling(data[position], totalCoursePerDay, columnCount)
        val icyRowInfo = IcyTimeTableHelper.getIcyRowInfo(gapFilling)

        // Log.d("ShowTimetableActivity",data[0].toString())

        val adapter = GroupAdapter<GroupieViewHolder>()
        schedule_recyclerview.addItemDecoration(
                MyRowInfoDecoration(
                        activity.resources.getDimensionPixelSize(R.dimen.paddingLeft),
                        activity.resources.getDimensionPixelSize(R.dimen.perCourseHeight),
                        Color.BLACK,
                        activity.resources.getDimension(R.dimen.numberSize),
                        activity.resources.getDimension(R.dimen.textSize)
                        ,
                        icyRowInfo,
                        totalCoursePerDay
                )
        )
        schedule_recyclerview.addItemDecoration(
                MyColInfoDecoration(columnCount,activity.resources.getDimensionPixelSize(R.dimen.paddingTop),Color.GRAY,Color.WHITE,Color.BLUE,
                        activity.resources.getDimension(R.dimen.textSize))
        )
        schedule_recyclerview.layoutManager = IcyTimeTableManager(
                45,
                activity.resources.getDimensionPixelSize(R.dimen.perCourseHeight),
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
        return schedule_recyclerview
    }
}