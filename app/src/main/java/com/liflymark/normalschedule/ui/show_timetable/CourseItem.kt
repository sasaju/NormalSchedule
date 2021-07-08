package com.liflymark.normalschedule.ui.show_timetable

import android.view.View
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.databinding.ItemCourseBinding
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.xwray.groupie.viewbinding.BindableItem

class CourseItem(private val data: OneByOneCourseBean) : BindableItem<ItemCourseBinding>() {
    override fun getLayout(): Int {
        return R.layout.item_course
    }

    override fun bind(viewBinding: ItemCourseBinding, position: Int) {
        TODO("Not yet implemented")
    }

    override fun initializeViewBinding(view: View): ItemCourseBinding {
        TODO("Not yet implemented")
    }


}