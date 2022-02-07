package com.liflymark.normalschedule.ui.exam_arrange

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.sign_in_compose.SignUIAll
import com.liflymark.normalschedule.ui.theme.NorScTheme
import kotlinx.coroutines.launch


class ExamActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UiControl()
            val state = rememberScaffoldState()
            NorScTheme {
                Scaffold(
                    scaffoldState = state,
                    topBar = {
                        NormalTopBar(label = "考试安排")
                    },
                    content = {
                        LoginToExam(state = state)
                    }
                )
            }
        }
    }
}

@Composable
fun UiControl() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFF2196F3),
            darkIcons = useDarkIcons
        )
        systemUiController.setNavigationBarColor(
            color = Color.White,
            darkIcons = useDarkIcons
        )
    }
}

@Composable
fun LoginToExam(
    state: ScaffoldState
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current as ExamActivity
    val loginExamViewModel: LoginToExamViewModel = viewModel()
    var loginText by rememberSaveable { mutableStateOf("正在加载...") }
    var loginEnable by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val examResult = loginExamViewModel.arrangeLiveData.observeAsState()
    LaunchedEffect(examResult.value){
        if (examResult.value?.result == "登陆成功") {
            val arrangeList = examResult.value!!.arrange_list
            val intent = Intent(context, ShowArrangeActivity::class.java).apply {
                putExtra("detail_list", Convert.examArrangeToJson(arrangeList))
            }
            loginExamViewModel.saveAccount()
            loginText = "登录"
            context.startActivity(intent)
            context.finish()
        } else {
            loginText = "登录"
            loginEnable = true
            examResult.value?.result?.let { state.snackbarHostState.showSnackbar(it) }
        }
    }
    SignUIAll(
        scaffoldState = state,
        onLove = {
            loginText = "连接异常"
            loginEnable = false
        },
        onSuccess = {
            loginExamViewModel.ids = it
            Log.d("ExamActivty", loginExamViewModel.ids)
            loginText = "登录"
            loginEnable = true
        },
        loginButton = { user, password ->
            Button(
                enabled = loginEnable,
                onClick =
                {
                    focusManager.clearFocus()
                    Log.d("ExamActivty", loginExamViewModel.ids)
                    val result = checkInputAndShow(
                        userName =  user,
                        userPassword = password,
                        viewModel = loginExamViewModel,
                        buttonText = loginText
                    )
                    scope.launch {
                        if (result != null) {
                            state.snackbarHostState.showSnackbar(result)
                        }else{
                            loginText = "正在登录..."
                            loginEnable = false
                        }
                    }
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    loginText.split("").forEach {
                        Text(text = it, style = MaterialTheme.typography.h6, maxLines = 1)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "登录后可以把考试安排导入到日历哦~")
        }
    )
}

fun checkInputAndShow(
    userName: String, userPassword: String,
    viewModel: LoginToExamViewModel,
    buttonText:String,
):String? {
    return when {
        buttonText != "登录" || viewModel.ids=="" -> { buttonText }
        userName == "" -> { "请输入学号" }
        userPassword == "" -> { "请输入密码" }
        else -> {
            viewModel.putValue(userName, userPassword)
            null
        }
    }
}
