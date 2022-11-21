package com.liflymark.normalschedule.ui.show_timetable

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.DrawerState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.statusBarsHeight
import com.liflymark.normalschedule.logic.utils.RomUtil
import com.liflymark.normalschedule.ui.about.ComposeAboutActivity
import com.liflymark.normalschedule.ui.class_course.ClassCourseActivity
import com.liflymark.normalschedule.ui.exam_arrange.ExamActivity
import com.liflymark.normalschedule.ui.import_show_score.ImportScoreActivity
import com.liflymark.normalschedule.ui.login_space_room.ShowSpaceActivity
import com.liflymark.normalschedule.ui.score_detail.LoginToScoreActivity
import com.liflymark.normalschedule.ui.set_background.SetBackground
import com.liflymark.normalschedule.ui.settings.SettingsActivity
import com.liflymark.normalschedule.ui.theme.NorScTheme
import com.liflymark.normalschedule.ui.tool_box.ToolBoxActivity
import com.liflymark.normalschedule.ui.work_book.WorkBookActivity
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@Composable
fun DrawerNavHost(drawerState: DrawerState){
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        OneSentence()

        NavButton(SetBackground(), drawerState,
            Icons.Filled.Wallpaper, "更换背景")
        NavButton(WorkBookActivity(), drawerState,
            Icons.Filled.MenuBook, "作业本")

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .padding(2.dp)
            .background(Color.Gray))

        NavButton(activity = ImportScoreActivity(), drawerState = drawerState,
            icon = Icons.Filled.Stairs, text = "成绩查询")
        NavButton(activity = LoginToScoreActivity(), drawerState = drawerState,
            icon = Icons.Filled.TrendingUp, text = "本学期成绩明细")
        NavButton(activity = ShowSpaceActivity(),
            drawerState = drawerState,
            icon = Icons.Filled.EventSeat, text = "空教室查询")

//        NavButton(
//            activity = UpdateCourseActivity(), drawerState = drawerState,
//            icon = Icons.Filled.Rule, text = "公共调课"
//        )
        NavButton(
            activity = ExamActivity(),
            drawerState = drawerState,
            icon = Icons.Filled.Rule,
            text = "考试安排"
        )


        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .padding(2.dp)
            .background(Color.Gray))

        NavButton(activity = ClassCourseActivity(), drawerState = drawerState,
            icon = Icons.Filled.Margin, text = "班级课程查询")

        NavButton(
            activity = ToolBoxActivity(),
            drawerState = drawerState,
            icon = Icons.Filled.WorkOutline, text = if(RomUtil.isVivo){"工具箱"}else{"河大工具箱"}
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .padding(2.dp)
            .background(Color.Gray))

        NavButton(
            activity = SettingsActivity(),
            drawerState = drawerState,
            icon = Icons.Filled.Settings,
            text = "设置"
        )
        NavButton(activity = ComposeAboutActivity(), drawerState = drawerState,
            icon = Icons.Filled.Info, text = "关于软件")

    }
}

@ExperimentalAnimationApi
@Composable
fun OneSentence(viewModel:ShowTimetableViewModel = viewModel()){
    val sentencesList= viewModel.sentenceLiveData.observeAsState()
    val context = LocalContext.current
    val status = sentencesList.value?.status
    val result = sentencesList.value?.result
    var expand by remember { mutableStateOf(false) }
    var clickTimes by remember {
        mutableStateOf(0)
    }
    Box(
        Modifier
            .clickable {
                expand = !expand
                clickTimes += 1
                if (clickTimes > 3) {
                    viewModel.fetchSentence(force = true)
                    Toasty
                        .success(context, "已从网络端重新加载")
                        .show()
                    clickTimes = 0
                }
            }
            .wrapContentHeight(), contentAlignment = Alignment.Center
    ) {
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .statusBarsHeight(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xff2196f3), MaterialTheme.colors.background)
                )
            )
        )
        Column(
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "『",fontSize = 14.sp, modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp), textAlign = TextAlign.Start)
            Text(
                text = "${result?.get(0)?.sentence}",
                maxLines = 4,
                fontSize = 17.5.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            Text(text = "』", fontSize = 14.sp, modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp), textAlign = TextAlign.End)
            AnimatedVisibility(visible = expand) {
                Text(text = "————${result?.get(0)?.author}", fontSize = 15.sp, textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
            }
        }
    }

}

@Composable
fun NavButton(
    activity: AppCompatActivity,
    drawerState: DrawerState,
    icon:ImageVector,
    text: String
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Row(modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(2.dp)
        .clickable {
            val intent = Intent(context, activity::class.java)
            context.startActivity(intent)
            scope.launch {
                drawerState.close()
            }
        },verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Icon(icon, null,modifier = Modifier.height(50.dp))
        Text(text = "    $text",
            fontSize = 18.sp,)

    }

}
@Composable
fun NavButton(
    activity: ComponentActivity,
    drawerState: DrawerState,
    icon:ImageVector,
    text: String,
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Row(modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(2.dp)
        .clickable {
            val intent = Intent(context, activity::class.java)
            context.startActivity(intent)
            scope.launch {
                drawerState.close()
            }
        },verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Icon(icon, null,modifier = Modifier.height(50.dp))
        Text(text = "    $text",
            fontSize = 18.sp,)

    }
}


@Preview(showBackground = true)
@Composable
fun Test(){
    NorScTheme {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.DarkGray)
        )
        Text(text = "adfasdfasdf")
    }
}