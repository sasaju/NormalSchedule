package com.liflymark.normalschedule.ui.about

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar


@Composable
fun ProjectListAll(){
    UiControl()
    Scaffold(
        topBar = {
            NormalTopBar(label = "开源库")
        },
        content = {
            ProjectsList(projectList = allProjects())
        }
    )
}

@Composable
fun ProjectsList(
    projectList:List<NameAndLicense>
){
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        projectList.forEach {
            item(it.projectName){
                SingleProject(project = it.projectName, license = it.license)
            }
        }
    }
}


@Composable
fun SingleProject(
    project: String,
    license: String,
){
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 4.dp)
            .wrapContentHeight()
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                expanded = !expanded
            }
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = project, style = MaterialTheme.typography.h5, modifier = Modifier.padding(start = 5.dp))
        Spacer(modifier = Modifier.height(5.dp))
        AnimatedVisibility(visible = expanded){
            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().height(300.dp),
                color = Color.LightGray
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(text = license, style = MaterialTheme.typography.body1, modifier = Modifier.padding(start = 5.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Spacer(modifier = Modifier
            .height(2.dp)
            .background(Color.Gray)
        )
    }
}