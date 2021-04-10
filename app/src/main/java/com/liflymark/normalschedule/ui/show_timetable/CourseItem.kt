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
       // viewBinding.num=position.toString()
//        //最优解决办法为在data中获取颜色
//        val colorList = arrayListOf<String>()
//        colorList.apply {
//            add("#12c2e9")
//            add("#376B78")
//            add("#f64f59")
//            add("#CBA689")
//            add("#ffffbb33")
//            add("#8202F2")
//            add("#F77CC2")
//        }
        val list = data.courseName.split("\n")
        viewBinding.num = list[0] + "\n" + list[1]
        viewBinding.color = data.color
        viewBinding.teacher = list[2]
    }


    fun getData() = data

    override fun initializeViewBinding(view: View): ItemCourseBinding = ItemCourseBinding.bind(view)
}