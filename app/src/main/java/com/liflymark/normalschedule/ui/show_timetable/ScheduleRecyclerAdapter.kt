package com.liflymark.normalschedule.ui.show_timetable

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.liflymark.icytimetable.IcyTimeTableHelper
import com.liflymark.icytimetable.IcyTimeTableManager
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.utils.Convert
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class ScheduleRecyclerAdapter(
        private val activity: ShowTimetableActivity,
        private val courseList: List<CourseBean>,
): RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var scheduleRecyclerView: RecyclerView = view.findViewById(R.id.schedule_recyclerview)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                  .inflate(R.layout.fragment_show_course_list, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        refreshUi(holder.scheduleRecyclerView, courseList, position)
//        Log.d("ScheduleRecyclerAdapter", position.toString())
//        activity.tv_date.text = "第${position+1}周  周四"
    }


    override fun getItemCount(): Int {
        var emptyNum = 0
        val oneByeOneList = Convert.courseBeanToOneByOne(courseList).reversed()
        for (element in oneByeOneList) {
            if (!element.isEmpty()) {
                break
            } else {
                emptyNum ++
            }
        }
        return oneByeOneList.size - emptyNum
    }

    private fun refreshUi(schedule_recyclerview: RecyclerView,courseList: List<CourseBean>, position: Int){
//        this.courseList = courseList
//        val layoutManager = LinearLayoutManager(this)
//        schedule_recyclerview.layoutManager = layoutManager
//        val adapter = ScheduleRecyclerAdapter(courseList, 0)
//        schedule_recyclerview.adapter = adapter
//
        val data = Convert.courseBeanToOneByOne(courseList).toList()

        val totalCoursePerDay = 11
        val columnCount = 7
        val gapFilling = IcyTimeTableHelper.gapFilling(data[position], totalCoursePerDay, columnCount)
        val icyRowInfo = IcyTimeTableHelper.getIcyRowInfo(gapFilling)

        // Log.d("ShowTimetableActivity",data[0].toString())

        val adapter = GroupAdapter<GroupieViewHolder>()
        adapter.apply {
            setOnItemClickListener(activity.onItemClickListener)
            setOnItemLongClickListener(activity.onItemLongClickListener)
        }
        if (schedule_recyclerview.itemDecorationCount > 0){
            schedule_recyclerview.removeItemDecorationAt(0)
            schedule_recyclerview.removeItemDecorationAt(0)
        }

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
                        activity.resources.getDimension(R.dimen.weekSize), position=position, backGroundColor = Color.TRANSPARENT)
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
    }

}