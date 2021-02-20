package com.liflymark.normalschedule.ui.show_timetable

import android.view.View
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.databinding.ItemCourseBinding
import com.xwray.groupie.viewbinding.BindableItem

class CourseItem(private val data: MyCourse) : BindableItem<ItemCourseBinding>() {
    override fun getLayout(): Int = R.layout.item_course

    override fun bind(viewBinding: ItemCourseBinding, position: Int) {
       // viewBinding.num=position.toString()
        viewBinding.num = data.courseName
    }
    override fun initializeViewBinding(view: View): ItemCourseBinding = ItemCourseBinding.bind(view)
}