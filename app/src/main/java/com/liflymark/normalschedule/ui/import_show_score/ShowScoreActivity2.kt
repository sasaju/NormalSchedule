package com.liflymark.normalschedule.ui.import_show_score

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults.indicatorLine
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.liflymark.normalschedule.logic.model.ThisProjectGrade
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import kotlinx.coroutines.launch

class ShowScoreActivity2 : ComponentActivity() {
    private val viewModel by lazy { ViewModelProvider(this)[ShowScoreViewModel::class.java] }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val allGradeListString = intent.getStringExtra("grade_list_string")?:""
        viewModel.setGradeStringOrNot(allGradeListString)
        setContent {
            NorScTheme {
                UiControl()
                Scaffold(
                    topBar = {
                        NormalTopBar(label = "成绩单")
                    },
                    content = {
                        Spacer(modifier = Modifier.padding(it))
                        ShowScorePage()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ShowScorePage(){
    val viewModel:ShowScoreViewModel = viewModel()
    var indexAllGrade by rememberSaveable { mutableStateOf(0) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val showProjectList = viewModel.allGradeList[indexAllGrade]
    val pagerState = rememberPagerState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val showTips = viewModel.showTipsTime.collectAsState(initial = 0)
        if (showTips.value!=7){ Text(text = "一切以教务系统结果为准(${7-showTips.value}s)") }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "当前培养方案：")
            Column{
                TextButton(onClick = {
                    expanded = !expanded
                }) {
                    Text(
                        text = viewModel.allGradeList[indexAllGrade].project,
                        maxLines = 1,
                        style = MaterialTheme.typography.body1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (expanded) {
                    DropDownToSelectAllGrade(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        selectList = viewModel.allGradeList.map { it.project },
                        selected = { indexAllGrade = it }
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
        Spacer(modifier = Modifier.height(5.dp))
        ShowScoreAndXueQi(projectGrades = showProjectList.thisProjectGradeList, pagerState = pagerState)
        ProjectGradesListPage(pagerState = pagerState, projectGrades = showProjectList.thisProjectGradeList)
    }
}
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ShowScoreAndXueQi(
    pagerState: PagerState,
    projectGrades: List<List<ThisProjectGrade>>
){
//    var selectXueQi by rememberSaveable{ mutableStateOf(0) }
//    val s = rememberScrollState()
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxWidth()) {
        ScrollableTabRow(
            edgePadding = 10.dp,
            selectedTabIndex = pagerState.currentPage,
            indicator =
            {tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            },
            backgroundColor = MaterialTheme.colors.background,
            divider = {}
        ) {
            repeat(projectGrades.size){
                Tab(
                    selected = pagerState.currentPage==it,
                    onClick =
                    {
                        scope.launch{ pagerState.animateScrollToPage(it) }
                    },
                    content = {
                        Text(text = "第0${projectGrades.size-it}\n学期")
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProjectGradesListPage(
    pagerState: PagerState,
    projectGrades: List<List<ThisProjectGrade>>
){
    HorizontalPager(
        count = projectGrades.size, state = pagerState
    ) {
        ProjectGradesList(projectGrades[it])
    }
}

@Composable
fun ProjectGradesList(singleProjectGrade: List<ThisProjectGrade>){
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(2.dp)
        .clip(MaterialTheme.shapes.medium)
    ){
        items(count = singleProjectGrade.size, key = { singleProjectGrade[it].courseName }){
            Column{
                SingleGrade(grade = singleProjectGrade[it])
                Box(modifier = Modifier.padding(top = 5.dp, end = 5.dp)) {
                    Spacer(
                        modifier = Modifier
                            .height(0.5.dp)
                            .fillMaxWidth()
                            .background(Color.Gray)
                    )
                }
            }
        }
    }
}


@Composable
fun SingleGrade(grade:ThisProjectGrade){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(text = grade.courseName, modifier = Modifier.weight(6f), fontSize = 18.5.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = grade.attributeName, modifier = Modifier.weight(1.5f),fontSize = 18.5.sp)
        Text(text = grade.score.toString(), modifier = Modifier.weight(3f), fontSize = 18.5.sp)
//        Text(text = grade.time)
    }
}

@Composable
fun DropDownToSelectAllGrade(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    selectList:List<String>,
    selected:(index:Int) -> Unit
){
    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
        selectList.forEachIndexed { index, s ->
            DropdownMenuItem(onClick = {
                selected(index)
                onDismissRequest()
            }) {
                Text(text = s)
            }
        }
    }
}
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    NormalScheduleTheme {
//        Greeting("Android")
//    }
//}