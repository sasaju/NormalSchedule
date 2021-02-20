package com.liflymark.normalschedule.ui.show_timetable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.bean.CourseBean

class ScheduleRecyclerAdapter(private val courseList: List<CourseBean>,
                              private val textWidth: Int,
): RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val courseTextView: AppCompatTextView = view.findViewById(R.id.tv_course_name)
        val cardView: MaterialCardView = view.findViewById(R.id.cv_course)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                  .inflate(R.layout.show_timetable_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course = courseList[position]
        holder.courseTextView.text = course.id
    }

    override fun getItemCount() = courseList.size
}