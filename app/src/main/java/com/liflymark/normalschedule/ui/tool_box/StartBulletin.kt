package com.liflymark.normalschedule.ui.tool_box

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.liflymark.normalschedule.logic.bean.Bulletin2
import com.liflymark.normalschedule.ui.show_timetable.StartBulletinDialog
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme

@ExperimentalMaterialApi
@Composable
fun StartBulletinAllPage(
    navController: NavController
){
    NorScTheme {
        Scaffold(
            topBar = {
                NormalTopBar(label = "开屏公告") {
                    navController.navigateUp()
                }
            },
            content = {
                StartBulletinContent()
            }
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun StartBulletinContent(){
    val context = LocalContext.current
    val viewModel:ToolBoxViewModel = viewModel()
    var showDialog by remember{ mutableStateOf(false) }
    var selectedDialog:Bulletin2? by remember { mutableStateOf(null) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(viewModel.startBulletinsSate.size){ index: Int ->
            StartBulletinCard(
                singleBulletin = viewModel.startBulletinsSate[index].copy(force_update = false)
            ) {
                selectedDialog = it
                showDialog = true
            }
        }
    }
    if (selectedDialog!=null && showDialog){
        StartBulletinDialog(onDismiss = { showDialog=false }, bulletin2 = selectedDialog!!)
    }
}

@Composable
fun StartBulletinCard(
    singleBulletin:Bulletin2,
    onClick:(selected:Bulletin2)->Unit
){
    BoardCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clickable { onClick(singleBulletin) }
    ) {
        Column(Modifier.padding(horizontal = 3.dp)) {
            Text(text = singleBulletin.title, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = singleBulletin.content, style = MaterialTheme.typography.subtitle1)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "--${singleBulletin.date}",
                modifier = Modifier.fillMaxWidth(0.98f),
                textAlign = TextAlign.Right
            )
        }
    }
}

