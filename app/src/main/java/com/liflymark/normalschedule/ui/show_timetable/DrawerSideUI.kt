package com.liflymark.normalschedule.ui.show_timetable

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.DrawerState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Stairs
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.liflymark.normalschedule.ui.about.AboutActivity
import com.liflymark.normalschedule.ui.import_show_score.ImportScoreActivity
import com.liflymark.normalschedule.ui.set_background.DefaultBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerNavHost(drawerState: DrawerState){
    Column {
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.DarkGray))
        NavButton(DefaultBackground(), drawerState,
            Icons.Filled.Image, "更换背景")
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .padding(2.dp)
            .background(Color.Gray))
        NavButton(activity = ImportScoreActivity(), drawerState = drawerState,
            icon = Icons.Filled.Stairs, text = "成绩查询")
        NavButton(activity = AboutActivity(), drawerState = drawerState,
            icon = Icons.Filled.Info, text = "关于软件")

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

@Preview(showBackground = true)
@Composable
fun Test(){
    Spacer(modifier = Modifier.width(10.dp))
    Icon(Icons.Filled.Image, null,modifier = Modifier.height(50.dp))
    Text(text = "    更换背景",
        fontSize = 18.sp,)
}