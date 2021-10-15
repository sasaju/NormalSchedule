package com.liflymark.normalschedule.ui.update_course

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import kotlinx.coroutines.launch

class UpdateCourseActivity : ComponentActivity() {
    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NorScTheme {
                UiControl()
                // A surface container using the 'background' color from the theme
                UpdateScaffold()
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun UpdateScaffold(){
    val pagerState = rememberPagerState(
//        pageCount = 3,
//        initialOffscreenLimit = 3
    )
    val userType = Repository.getUserType().collectAsState(initial = null)
    val allCourseName = Repository.loadAllCourseName().collectAsState(initial = listOf(""))
    val networkCourse = Repository.getNewCourse().collectAsState(initial = null)
    val userNumber = Repository.getSavedAccount()["user"]
    Scaffold(
        topBar = {
            NormalTopBar(label = "公共调课")
        },
        bottomBar = {
            UpdateCourseBottomBar(pagerState)
        },
        content = {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                count = 3
            ) { page ->
                when(page){
                    NavigationItem.Home.pagerCount ->{
                        if (userType.value == null){
                            OnlyError(content = "正在验证您的身份...")
                        }else{
                            ExampleUser(userType.value!!)
                        }
                    }
                    NavigationItem.Profile.pagerCount -> {
                        if (userType.value == null){
                            OnlyError(content = "正在验证您的身份...")
                        }else{
                            ExampleUser(userType.value!!)
                        }
                    }
                    NavigationItem.Upload.pagerCount -> {
                        if (userNumber == null || userNumber.length<6){
                            OnlyError(content = "无法验证你的身份")
                        } else {
                            UpdateHome(
                                networkCourse = networkCourse.value,
                                userNumber = userNumber
                            )
                        }
                    }
                }
            }
        }
    )
}

@ExperimentalPagerApi
@Composable
fun UpdateCourseBottomBar(pagerState: PagerState){
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Upload,
        NavigationItem.Profile,
    )
    val scope = rememberCoroutineScope()
    BottomNavigation {
        items.forEach {
            BottomNavigationItem(
                icon = { Icon(it.icon, contentDescription = null) },
                label = { Text(text = it.title) },
                selected = pagerState.currentPage == it.pagerCount,
                onClick =
                {
                    scope.launch {
                        pagerState.animateScrollToPage(it.pagerCount)
                    }
                },
            )
        }
    }
}

/** 显示单一Text
 *
 */
@Composable
fun OnlyError(content:String){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = content,
            color = Color.Gray,
            style = MaterialTheme.typography.h5
        )
    }
}

@Composable
fun SingleCourse(
    selected: Boolean,
    onClick:() -> Unit,
    text:String,
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Checkbox(
                checked = selected,
                onCheckedChange = {
                    onClick()
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = text)
        }
    }
}

@Composable
fun NoSelectCourse(
    onClick:() -> Unit,
    text:String,
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = text)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview3() {
    NorScTheme {
    }
}