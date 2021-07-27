package com.liflymark.normalschedule.ui.login_space_room

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.liflymark.normalschedule.ui.class_course.Item
import com.liflymark.normalschedule.ui.class_course.ui.theme.NormalScheduleTheme
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import es.dmoral.toasty.Toasty

class ShowSpaceActivity : ComponentActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(ShowSpaceViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ids = intent.getStringExtra("ids")
        setContent {
            NormalScheduleTheme {
                UiControl()
                Column {
                    NormalTopBar(label = "查询空教室")
                    SelectAndShow(ids = ids, viewModel)
                }
            }
        }
    }
}

@Composable
fun SelectAndShow(ids:String?, ssViewModel: ShowSpaceViewModel = viewModel()){
    Column {
        SayHowToUse()
        SelectRoom(ids, ssViewModel)
        ShowSpaceResult()
    }
}

@Composable
fun SayHowToUse(){
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        CenterText(text = "有颜色代表未占用(空教室)，无颜色代表已占用",modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(3.dp))
        CenterText(
            text = "目前仅支持新区和本部的部分教室",
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 12.sp, color = Color(0xFF28B1FA))
        )
    }
}

@Composable
fun SelectRoom(ids:String?, ssViewModel: ShowSpaceViewModel = viewModel()){
    val activity = LocalContext.current as ShowSpaceActivity
    val roomListOf = listOf("六教", "七教","八教", "九教", "A1", "A2","A3", "A4")
    if (ids == null){
        Toasty.error(activity, "缺少参数").show()
        activity.finish()
    }

    val expandClassRoom = remember { mutableStateOf(false) }
    val roomName = remember { mutableStateOf("六教") }

    val expandSchool = remember { mutableStateOf(false) }
    val schoolName = remember { mutableStateOf("五四路校区") }

    val expandDate = remember { mutableStateOf(false) }
    val threeDate = remember { mutableStateOf(ssViewModel.getThreeDay()) }
    val dateName = remember { mutableStateOf(ssViewModel.getThreeDay()[0]) }

    val roomList =  remember { mutableStateListOf("六教", "七教","八教", "九教", "A1", "A2","A3", "A4") }
    val schoolList = remember { mutableStateListOf("五四路校区","七一路校区") }

    LaunchedEffect(roomName.value){
        if (roomName.value in roomListOf){
            if (ids != null) {
                ssViewModel.getSpaceRoom(ids, roomName.value, dateName.value)
            }
        }
    }
    LaunchedEffect(dateName.value){
        if (roomName.value in roomListOf){
            if (ids != null) {
                ssViewModel.getSpaceRoom(ids, roomName.value, dateName.value)
            }
        }
    }
    LaunchedEffect(schoolName.value){
        roomList.clear()
        when(schoolName.value){
            "五四路校区" -> roomList.addAll(listOf("六教", "七教","八教", "九教"))
            "七一路校区" -> roomList.addAll(linkedSetOf("A1", "A2","A3", "A4"))
        }
    }
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {

        // 选择校区
        Box{
            TextButton(onClick = { expandSchool.value = true }) {
                Text(text = schoolName.value, fontSize = 18.sp)
            }

            DropdownMenu(
                expanded = expandSchool.value,
                onDismissRequest = { expandSchool.value = false },
                modifier = Modifier.heightIn(20.dp,280.dp)
            ) {
                for (i in schoolList){
                    Item(itemName = i){
                        schoolName.value = it
                        expandSchool.value = false
                    }
                }
            }
        }

        // 选择教室
        Box{
            TextButton(onClick = { expandDate.value = true }) {
                Text(text = roomName.value, fontSize = 18.sp)
            }

            DropdownMenu(
                expanded = expandDate.value,
                onDismissRequest = { expandDate.value = false },
                modifier = Modifier.heightIn(20.dp,280.dp)
            ) {
                for (i in roomList){
                    Item(itemName = i){
                        roomName.value = it
                        expandDate.value = false
                    }
                }
            }
        }

        // 选择日期
        Box{
            TextButton(onClick = { expandClassRoom.value = true }) {
                Text(text = dateName.value, fontSize = 18.sp)
            }

            DropdownMenu(
                expanded = expandClassRoom.value,
                onDismissRequest = { expandClassRoom.value = false },
                modifier = Modifier.heightIn(20.dp,280.dp)
            ) {
                for (i in threeDate.value){
                    Item(itemName = i){
                        dateName.value = it
                        expandClassRoom.value = false
                    }
                }
            }
        }
    }

}

@Composable
fun ShowSpaceResult(ssViewModel: ShowSpaceViewModel = viewModel()){
    val spaceResult = ssViewModel.spaceResult
        .observeAsState(initial = ssViewModel.initialSpace)
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        for (space in spaceResult.value.roomList){
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(0.98f)) {
                CenterText(
                    text = space.classroomName,
                    modifier = Modifier
                        .weight(0.7f)
                        .height(30.dp),
                    textStyle = TextStyle(fontSize = 18.sp)
                )
//                Text(text = space.placeNum, modifier = Modifier.weight(0.5f))
//                Text(text = space.type, modifier = Modifier.weight(0.5f),maxLines = 1)
                for (i in space.spaceNow.indices){
                    if (space.spaceNow[i] == '1'){
                        ClassIntBox(text = "${i+1}", modifier = Modifier.weight(0.3f))
                    }else{
                        ClassIntBox(text = "${i+1}", modifier = Modifier.weight(0.3f),
                            color = Color(
                            0xAE28B1FA)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SingleRowSpace(){
    Row {
        repeat(11){
            Spacer(modifier = Modifier
                .size(10.dp)
                .background(Color.Green))
            Spacer(modifier = Modifier.size(10.dp))
        }
    }

}

@Composable
fun CenterText(
    modifier: Modifier = Modifier,
    text:String,
    textStyle: TextStyle = TextStyle()
){
    Box(contentAlignment = Alignment.Center, modifier = modifier){
        Text(text = text, style = textStyle)
    }
}


@Composable
fun ClassIntBox(
    modifier: Modifier = Modifier,
    text:String,
    color: Color = Color.White
){
    Box(modifier = modifier.background(color = color), contentAlignment = Alignment.Center){
        Text(
            text = text,
            color = contentColorFor(backgroundColor = color)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview7() {
    NormalScheduleTheme {
        SingleRowSpace()
    }
}