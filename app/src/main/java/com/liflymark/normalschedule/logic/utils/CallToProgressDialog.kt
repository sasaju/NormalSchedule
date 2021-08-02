package com.liflymark.normalschedule.logic.utils

import android.content.Context
import android.util.AttributeSet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.AbstractComposeView
import com.liflymark.normalschedule.ui.edit_course.ui.theme.NormalScheduleTheme
import com.liflymark.normalschedule.ui.score_detail.ProgressDialog

class CallToProgressDialog @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbstractComposeView(context, attrs, defStyle) {

    var showDialog = mutableStateOf(true)

    @Composable
    override fun Content() {
        NormalScheduleTheme {
            ProgressDialog(openDialog = showDialog, label = "正在尝试导入")
        }
    }
}