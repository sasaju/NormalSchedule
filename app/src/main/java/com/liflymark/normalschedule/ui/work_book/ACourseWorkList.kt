package com.liflymark.normalschedule.ui.work_book

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.pager.ExperimentalPagerApi
import com.liflymark.normalschedule.logic.bean.HomeworkBean
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.score_detail.WaitDialog
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun ACourseWorkList(
    courseName: String,
    workBookViewModel: WorkBookViewModel = viewModel()
) {
    NorScTheme {
        UiControl()
        Scaffold(
            topBar = {
                NormalTopBar(label = "单个作业本")
            },
            content = {
                Log.d("ACourse", "content")
                SingleCourseWorkList(courseName = courseName, workBookViewModel = workBookViewModel)
            }
        )
    }
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun ACourseWorkList(
    courseName: String,
    navController: NavController,
    workBookViewModel: WorkBookViewModel = viewModel()
) {
    NorScTheme {
        UiControl()
        Scaffold(
            topBar = {
                NormalTopBar(label = "单个作业本") {
                    navController.navigateUp()
                }
            },
            content = {
                Log.d("ACourse", "content")
                SingleCourseWorkList(courseName = courseName, workBookViewModel = workBookViewModel)
            }
        )
    }
}


@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun SingleCourseWorkList(
    courseName: String,
    workBookViewModel: WorkBookViewModel
) {
    val workList = workBookViewModel.courseWorkList(courseName = courseName)
        .collectAsState(initial = workBookViewModel.initWorkList)
    val newWorkList = remember {
        mutableStateListOf<HomeworkBean>()
    }
    val homeworkNewBean = workBookViewModel.newBeanInit(courseName = courseName)
        .collectAsState(initial = null)
    val context = LocalContext.current
    val showAddDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf(false) }
    val waitingDialog = remember { mutableStateOf(false) }
    val editingBean = remember {
        mutableStateOf<HomeworkBean?>(null)
    }
    val editingNew = workBookViewModel.newBeanInit(courseName).collectAsState(
        null
    )
    val deleted  = remember {
        mutableStateOf(0)
    }

    val scope = rememberCoroutineScope()

    editingBean.value?.let {
        AddNewOrEditDialog(
            show = showAddDialog,
            homeworkBean = it,
            newBean = { i ->
                newWorkList.add(i)
                Log.d("Acour", "newworklist add")
            },
            deleteId = {
                scope.launch{
                    workBookViewModel.deleteWork(it)
                    deleted.value += 1
                }
            }
        )
    }
    editingBean.value?.let {
        EditNewOrEditDialog(
            show = showEditDialog,
            homeworkBean = it,
            newBean = { i ->
                newWorkList.add(i)
                Log.d("Acour", "newworklist add")
            },
            deleteId = {
                scope.launch{
                    workBookViewModel.deleteWork(it)
                    deleted.value += 1
                }
            }
        )
    }
    WaitDialog(openDialog = waitingDialog)

    LaunchedEffect(newWorkList.size) {
        Log.d("Acour", "newworklist added")
        waitingDialog.value = true
        workBookViewModel.insertWork(newWorkList)
        waitingDialog.value = false
        newWorkList.clear()
    }

    LaunchedEffect(deleted.value){
        Log.d("Acour", "delete")
        waitingDialog.value = true
        waitingDialog.value = false
    }

    Log.d("Acour", "Colom")
    Column(
        modifier = Modifier
            .padding(horizontal = 3.dp, vertical = 2.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        key(999) {
            AddIconButton {
                if (homeworkNewBean.value?.id ?: -1 == -1) {
                    Toasty.error(context, "发生内部错误，请稍后点击").show()
                } else {
                    showAddDialog.value = true
                    editingBean.value = editingNew.value
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
        }

        for (i in workList.value) {
            Log.d("Acour", i.workContent)
            Log.d("Acour", "循环一次")
            key(i.id) {
                SingleWork(
                    homeworkBean = i,
                    cardClick = {
                        editingBean.value = i
                        showEditDialog.value = true
                    },
                    finishedChange = {
                        newWorkList.add(i)
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SingleWork(
    homeworkBean: HomeworkBean,
    cardClick: () -> Unit,
    finishedChange: (newBean: HomeworkBean) -> Unit,
) {
    val init = if (homeworkBean.finished) {
        "已完成"
    } else {
        "未完成"
    }
    var finishedOrString by remember { mutableStateOf(init) }
    var finished by remember { mutableStateOf(homeworkBean.finished) }
    Card(
        onClick = { cardClick() },
        backgroundColor = Color.Transparent,
        contentColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .height(162.dp)
    ) {
        // 设置相应背景
        if (finished) {
            FinishedBackground()
        } else {
            UnfinishedBackground()
        }

        //具体内容
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .background(Color.Transparent)
        ) {
            Icon(
                Icons.Default.Bookmark,
                null,
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .fillMaxWidth()
                    .height(85.dp),
            ) {
                Text(
                    text = homeworkBean.workContent,
                    style = MaterialTheme.typography.h6,
                    maxLines = 3
                )
            }
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 10.dp, horizontal = 5.dp),
                crossAxisAlignment = FlowCrossAxisAlignment.Center,
                mainAxisAlignment = MainAxisAlignment.SpaceAround
            ) {
                DateText(
                    text = "创建:",
                    millis = homeworkBean.createDate,
                    modifier = Modifier.weight(1f)
                )
                DateText(
                    text = "截止:",
                    millis = homeworkBean.deadLine,
                    modifier = Modifier.weight(1f)
                )
                TextCheckBox(text = finishedOrString, checked = finished) {
                    finished = it
                    homeworkBean.finished = it
                    finishedChange(homeworkBean)
                    finishedOrString = if (it) {
                        "已完成"
                    } else {
                        "未完成"
                    }
                }
            }
        }
    }
}

@Composable
fun DateText(
    modifier: Modifier = Modifier,
    text: String,
    millis: Long
) {
    val dateText = GetDataUtil.getDateStrByMillis(millis = millis)
    Text(text = "$text$dateText", modifier)
}

@Composable
fun TextCheckBox(
    checked: Boolean,
    text: String,
    onCheckChange: (check: Boolean) -> Unit
) {
    var checkedIn by remember { mutableStateOf(checked) }
    Row(
        modifier = Modifier.clickable {
            checkedIn = !checkedIn
            onCheckChange(checkedIn)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checkedIn,
            onCheckedChange = {
                checkedIn = it
                onCheckChange(it)
            })
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text)
    }
}


@Composable
fun FinishedBackground() {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF3B2667), Color(0xFFBC78EC))))
    )
}

@Composable
fun UnfinishedBackground() {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF69FF97), Color(0xFF00E4FF))))
    )
}

@Composable
fun AddIconButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                Brush.horizontalGradient(listOf(Color(0xFF5EFCE8), Color(0xFF736EFE)))
            )
            .clickable(onClickLabel = "添加作业") { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Add, null)
    }
}


@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun AcoursePreview() {
    SingleWork(
        HomeworkBean(
            id = 999,
            "未查询到",
            "张子龙你多重了\n张子龙你胖了不",
            deadLine = 0,
            finished = false,
            createDate = 0
        ),
        {},
        {}
    )
}