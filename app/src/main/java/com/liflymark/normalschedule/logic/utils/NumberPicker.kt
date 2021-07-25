package com.liflymark.normalschedule.logic.utils

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.VerticalPager
import com.liflymark.normalschedule.ui.edit_course.ui.theme.NormalScheduleTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
@ExperimentalPagerApi
@ExperimentalMaterialApi
fun StringPicker(
    modifier: Modifier=Modifier,
    strList:List<String>,
    pagerState: PagerState,
    reverseLayout:Boolean = false,
    itemSpacing: Dp = 15.dp,
    pageChange:(it:Int)->Unit,
){

    VerticalPager(
        state = pagerState,
        reverseLayout = reverseLayout,
        itemSpacing = itemSpacing,
        modifier = modifier
    ) { page ->
        Text(strList[page],fontSize = 19.sp)
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            pageChange(page)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewNumberPicker() {
    NormalScheduleTheme {

    }
}