package com.liflymark.normalschedule.ui.tool_box

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SingleDayCircle(){
    Surface(
        shape = RoundedCornerShape(100)
    ) {

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CalendarPreview(){
    SingleDayCircle()
}