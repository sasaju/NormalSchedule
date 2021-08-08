package com.liflymark.normalschedule.ui.tool_box

import android.os.Bundle
import android.webkit.WebSettings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.liflymark.normalschedule.logic.model.Bulletin
import com.liflymark.normalschedule.logic.utils.CustomWebView
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme

class ToolBoxActivity : ComponentActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(ToolBoxViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            NorScTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(color = MaterialTheme.colors.background) {
//                    DevBoard("Android")
//                }
//            }
            UiControl()
            ToolBoxNavGraph()
        }
    }
}

/**
 * 开发者公告页面-总
 */
@Composable
fun DevBoard(
    navController: NavController,
    tbViewModel: ToolBoxViewModel = viewModel()
) {
    NorScTheme {
//        Column {
//            NormalTopBar(label = "工具箱"){
//                navController.navigateUp()
//            }
//            DevBoardContent(tbViewModel)
//        }
        Scaffold(
            topBar = {
                NormalTopBar(label = "开发者公告") {
                    navController.navigateUp()
                }
            },
            content = {
                DevBoardContent(tbViewModel)
            }
        )
    }
}

/**
 * 开发者公告页面-所有公告
 */
@Composable
fun DevBoardContent(
    tbViewModel: ToolBoxViewModel = viewModel()
) {
    val bulletins =
        tbViewModel.bulletinsLiveData.observeAsState(tbViewModel.initialBulletin)
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        for (bulletin in bulletins.value.bulletin_list) {
            BulletinCard(bulletin)
        }
    }
}

/**
 * 开发者公告-单个公告
 */
@Composable
fun BulletinCard(singleBulletin: Bulletin) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(2.dp)
//    ) {
//        Column(modifier = Modifier.padding(2.dp)) {
//            Text(text = "标题：${singleBulletin.title}")
//            Text(text = "内容：${singleBulletin.content}")
//            Text(text = "日期：${singleBulletin.date}")
//        }
//    }
    BoardCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
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


@Composable
fun HbuCalendar(navController: NavController) {
    Scaffold(
        topBar = {
            NormalTopBar(label = "河大校历") {
                navController.navigateUp()
            }
        },
        content = {
            var rememberWebViewProgress by remember { mutableStateOf(-1) }
            Box {
                CustomWebView(
                    modifier = Modifier.fillMaxSize(),
                    url = "https://liflymark.top/tool/schoolcaledar",
                    onProgressChange = { progress ->
                        rememberWebViewProgress = progress
                    },
                    initSettings = { settings ->
                        settings?.apply {
                            //支持js交互
                            javaScriptEnabled = true
                            //将图片调整到适合webView的大小
                            useWideViewPort = true
                            //缩放至屏幕的大小
                            loadWithOverviewMode = true
                            //缩放操作
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = true
                            //是否支持通过JS打开新窗口
                            javaScriptCanOpenWindowsAutomatically = true
                            //不加载缓存内容
                            cacheMode = WebSettings.LOAD_NO_CACHE
                        }
                    }
                )
            }
            LinearProgressIndicator(
                progress = rememberWebViewProgress * 1.0F / 100F,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (rememberWebViewProgress == 100) 0.dp else 5.dp),
                color = Color.Red
            )
        }
    )
}


/**
 * 全圆角卡片
 */
@Composable
fun BoardCard(
    modifier: Modifier,
    contentAlignment: Alignment = Alignment.TopCenter,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp),
        color = MaterialTheme.colors.primary,
    ) {
        Column(Modifier.padding(0.dp, 3.dp, 0.dp, 0.dp)) {
            Row(Modifier.padding(5.dp)) {
                Icon(Icons.Default.PushPin, null)
                Spacer(modifier = Modifier.width(10.dp))
            }
            Box(contentAlignment = contentAlignment) {
                content()
            }
        }
    }
}


/**
 * 半圆角卡片
 */
@Composable
fun FullCard(
    modifier: Modifier,
    contentAlignment: Alignment = Alignment.TopCenter,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colors.primary,
        shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)
    ) {
        Column(Modifier.padding(0.dp, 3.dp, 0.dp, 0.dp)) {
            Row(Modifier.padding(5.dp)) {
                Icon(Icons.Default.PushPin, null)
                Spacer(modifier = Modifier.width(10.dp))
            }
            Box(contentAlignment = contentAlignment) {
                content()
            }
        }
    }
}

/**
 * 上圆角页面
 */
@Composable
fun RoundedHeader(title: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colors.primary),
        elevation = 0.5.dp,
        shape = RoundedCornerShape(50, 50, 0, 0)
    ) {
        val padding = 16.dp
        Text(
            text = title,
            modifier = Modifier.padding(start = padding, top = padding, end = padding),
            style = MaterialTheme.typography.h6
        )
    }
}

/**
 * 等待开发的界面
 */
@Composable
fun WaitTime(navController: NavController) {
    Scaffold(
        topBar = {
            NormalTopBar(label = "敬请期待") {
                navController.navigateUp()
            }
        },
        content = {
            Text(text = "开发者玩命完善中......")
        }
    )
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview8() {
    NorScTheme {
        SchoolBusCard()
    }
}