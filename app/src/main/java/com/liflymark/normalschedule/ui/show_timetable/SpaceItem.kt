package com.liflymark.normalschedule.ui.show_timetable

import android.view.View
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.databinding.ItemSpaceBinding
import com.xwray.groupie.viewbinding.BindableItem

class SpaceItem: BindableItem<ItemSpaceBinding>() {
    override fun getLayout(): Int= R.layout.item_space

    override fun bind(viewBinding: ItemSpaceBinding, position: Int) {
        //viewBinding.num=position.toString()
        viewBinding.num=""
    }

    override fun initializeViewBinding(view: View): ItemSpaceBinding= ItemSpaceBinding.bind(view)
}