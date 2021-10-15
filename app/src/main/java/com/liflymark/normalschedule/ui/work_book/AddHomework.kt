package com.liflymark.normalschedule.ui.work_book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.liflymark.normalschedule.logic.bean.HomeworkBean
import com.liflymark.normalschedule.logic.utils.*
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import java.util.*


@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun EditNewOrEditDialog(
    show: MutableState<Boolean>,
    homeworkBean: HomeworkBean,
    deleteId: (beanId: Int) -> Unit,
    newBean: (newHomeworkBean: HomeworkBean) -> Unit
) {
    val valueInit = homeworkBean.workContent
    val showSelectDialog = remember {
        mutableStateOf(false)
    }
    var dateStr by remember {
        mutableStateOf(GetDataUtil.getDateStrByMillis(homeworkBean.deadLine))
    }
    val beanCalendar = GetDataUtil.getCalendarByMillis(homeworkBean.deadLine)
    SelectDateDialog(
        showDialog = showSelectDialog,
        initialYear = beanCalendar.get(Calendar.YEAR)-2021,
        initialMonth = beanCalendar.get(Calendar.MONTH),
        initialDay = beanCalendar.get(Calendar.DATE)-1,
        selectMillis = {
            homeworkBean.deadLine = it
            dateStr = GetDataUtil.getDateStrByMillis(it)
        }
    )
    if (show.value) {
        Dialog(
            onDismissRequest = { show.value = false },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = true),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.White)
                    .padding(10.dp)
            ) {
                EditTextFiled(
                    valueInit = valueInit,
                    onValueChange = {
                        homeworkBean.workContent = it
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "截止日期：")
                    TextButton(onClick = {
                        showSelectDialog.value = true
                    }) {
                        Text(text = dateStr)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        show.value = false
                    }) {
                        Text(text = "取消")
                    }
                    TextButton(onClick = {
                        deleteId(homeworkBean.id)
                        show.value = false
                    }) {
                        Text(text = "删除")
                    }
                    TextButton(onClick = {
                        newBean(homeworkBean)
                        show.value = false
                    }) {
                        Text(text = "保存")
                    }
                }

            }
        }
    }
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun AddNewOrEditDialog(
    show: MutableState<Boolean>,
    homeworkBean: HomeworkBean,
    deleteId: (beanId: Int) -> Unit,
    newBean: (newHomeworkBean: HomeworkBean) -> Unit
) {
    val valueInit = homeworkBean.workContent
    if (show.value) {
        Dialog(
            onDismissRequest = { show.value = false },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = true),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.White)
                    .padding(10.dp)
            ) {
                EditTextFiled(
                    valueInit = valueInit,
                    onValueChange = {
                        homeworkBean.workContent = it
                    }
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "截止日期：")
                    SelectDeadLine(
                    ) {
                        homeworkBean.deadLine = it
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        show.value = false
                    }) {
                        Text(text = "取消")
                    }
                    TextButton(onClick = {
                        deleteId(homeworkBean.id)
                        show.value = false
                    }) {
                        Text(text = "删除")
                    }
                    TextButton(onClick = {
                        newBean(homeworkBean)
                        show.value = false
                    }) {
                        Text(text = "保存")
                    }
                }

            }
        }
    }
}


@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun SelectDeadLine(
    modifier: Modifier = Modifier,
    addMillis: (millis: Long) -> Unit
) {
    val stringList = listOf("明天","一周后", "两周后")
    StringPicker2(
        modifier = modifier,
        value = 1,
        strList = stringList,
        onValueChange = {
            val addDay = when (stringList[it]) {
                "明天" -> 7
                "一周后" -> 1
                "两周后" -> 14
                else -> 0
            }
            addMillis(GetDataUtil.getDayMillis(addDay))
        }
    )
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun ShowDeadLine(
    modifier: Modifier = Modifier,
    deadMillis: Long,
    addMillis: (millis: Long) -> Unit
) {
//    val stringList = listOf("明天","一周后", "两周后")
//    val pagerState = rememberPagerState(
//        pageCount = stringList.size,
//        initialPage = 1,
//        initialOffscreenLimit = stringList.size
//    )
//    StringPicker(
//        modifier = modifier,
//        strList = stringList,
//        pagerState = pagerState,
//        pageChange = {
//            val addDay = when (it) {
//                0 -> 7
//                1 -> 1
//                2 -> 14
//                else -> 0
//            }
//            addMillis(GetDataUtil.getDayMillis(addDay))
//        }
//    )
    val nowString = GetDataUtil.getDateStrByMillis(deadMillis)
    var newDeadLine by remember {
        mutableStateOf(nowString)
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        TextButton(onClick = {

        }) {
            Text(text = newDeadLine, style = MaterialTheme.typography.h6)
        }
    }
}

@Composable
fun EditTextFiled(
    valueInit: String,
    onValueChange: (value: String) -> Unit
) {
    var value by remember { mutableStateOf(valueInit) }
    TextField(
        value = value,
        textStyle = MaterialTheme.typography.h6,
        onValueChange =
        {
            value = it
            onValueChange(it)
        },
        modifier = Modifier
            .height(140.dp)
            .fillMaxWidth(),
        placeholder = {
            Text(text = "在这里输入作业")
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color(0x6B28C76F)
        ),
        maxLines = 10
    )
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun DialogContentPreview() {
    val homeworkBean = HomeworkBean(
        id = 999,
        "未查询到",
        "张子龙你多重了\n张子龙你胖了不",
        deadLine = 0,
        finished = false,
        createDate = 0
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        EditTextFiled(
            valueInit = "aa",
            onValueChange = {
                homeworkBean.workContent = it
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "截止日期：")
            SelectDeadLine(
                modifier = Modifier.height(80.dp)
            ) {
                homeworkBean.deadLine = it
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = {
            }) {
                Text(text = "取消")
            }
            TextButton(onClick = {
            }) {
                Text(text = "保存")
            }

        }

    }
}
