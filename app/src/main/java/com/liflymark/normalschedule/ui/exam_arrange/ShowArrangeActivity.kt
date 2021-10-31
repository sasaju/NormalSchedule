package com.liflymark.normalschedule.ui.exam_arrange

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.liflymark.normalschedule.logic.model.Arrange
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.ui.exam_arrange.ui.theme.NormalScheduleTheme
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import com.liflymark.normalschedule.ui.tool_box.BoardCard

class ShowArrangeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val allExamArrange = intent.getStringExtra("detail_list")?:"[]"
        val allExamArrangeList = Convert.jsonToExamArrange(allExamArrange)
        setContent {
            NorScTheme {
                // A surface container using the 'background' color from the theme
                UiControl()
                Scaffold(
                    topBar = {
                        NormalTopBar(label = "考试安排")
                    },
                    content = {
                        ExamArrangeContent(allExamArrangeList)
                    }
                )
            }
        }
    }
}

@Composable
fun ExamArrangeContent(
    arrangeList: List<Arrange>
){
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 4.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "该功能还尚不完善，结果可能发生缺失，请谨慎参考！", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        if (arrangeList.isEmpty()){
            Text(text = "未在教务系统查询到考试安排", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        for (singleArrange in arrangeList){
            ExamArrangeCard(examArrange = singleArrange)
        }
    }
}



@Composable
fun ExamArrangeCard(
    examArrange: Arrange
){
    BoardCard(
        modifier = Modifier
            .wrapContentHeight()
            .padding(2.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(horizontal = 3.dp)) {
            Text(text = examArrange.examName, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = examArrange.examTime, style = MaterialTheme.typography.subtitle1)
            Text(text = examArrange.examWeekName, style = MaterialTheme.typography.subtitle1)
            Text(text = examArrange.examBuilding, style = MaterialTheme.typography.subtitle1)
            Text(text = examArrange.examSeat, style = MaterialTheme.typography.subtitle1)
            Text(text = examArrange.examIdCard, style = MaterialTheme.typography.subtitle1)
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}

@Composable
fun Greeting2(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview5() {
    NormalScheduleTheme {
        Greeting2("Android")
    }
}