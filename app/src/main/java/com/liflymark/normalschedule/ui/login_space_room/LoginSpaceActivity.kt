package com.liflymark.normalschedule.ui.login_space_room

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.utils.LoadingDialogBottom
import com.liflymark.normalschedule.ui.class_course.ui.theme.NormalScheduleTheme
import com.liflymark.normalschedule.ui.score_detail.*
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.sign_in_compose.SignUIAll
import com.liflymark.normalschedule.ui.theme.NorScTheme
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

class LoginSpaceActivity : ComponentActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(LoginSpaceViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NorScTheme {
                UiControl()
                val state = rememberScaffoldState()
                Scaffold(
                    scaffoldState = state,
                    topBar = {
                        NormalTopBar(label = "空教室查询")
                    },
                    content = {
                        LoginToSpace(state = state)
                    }
                )
            }
        }
    }
}

@Composable
fun LoginToSpace(
    state: ScaffoldState,
    loginSpaceViewModel: LoginSpaceViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    val activity = LocalContext.current as LoginSpaceActivity
    var loginText by rememberSaveable { mutableStateOf("正在加载...") }
    var loginEnable by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val spaceResult = loginSpaceViewModel.loginSpaceLiveData.observeAsState(initial = loginSpaceViewModel.initialSpace)
    LaunchedEffect(spaceResult.value) {
        when (spaceResult.value.result) {
            "登陆成功" -> {
                val intent = Intent(activity, ShowSpaceActivity::class.java).apply {
                    putExtra("ids", loginSpaceViewModel.ids)
                }
                activity.startActivity(intent)
                scope.launch {
                    loginSpaceViewModel.saveAccount()
                    activity.finish()
                }
            }
            "初始化" -> {
                state.snackbarHostState.showSnackbar("初始化中")
            }
            else -> {
                loginText="登录"
                state.snackbarHostState.showSnackbar(spaceResult.value.result)
            }
        }
    }
    SignUIAll(
        scaffoldState = state,
        onLove = {
            loginText = "连接异常"
            val intent = Intent(activity, ShowSpaceActivity::class.java).apply {
                putExtra("ids", "love")
            }
            Repository.cancelAll()
            activity.startActivity(intent)
            activity.finish()
        },
        onSuccess = {
            loginSpaceViewModel.ids = it
            loginText = "登录"
            loginEnable = true
        },
        loginButton = { user, password ->
            Button(
                enabled = true,
                onClick =
                {
                    val result = checkInputAndShow(
                        userName = user,
                        userPassword = password,
                        viewModel = loginSpaceViewModel,
                        buttonText = loginText
                    )
                    focusManager.clearFocus()
                    scope.launch {
                        if (result != null) {
                            state.snackbarHostState.showSnackbar(result)
                        } else {
                            loginText = "正在登录..."
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
            OutlinedButton(
                onClick = {
                    val intent = Intent(activity, ShowSpaceActivity::class.java).apply {
                        putExtra("ids", "love")
                    }
                    Repository.cancelAll()
                    activity.startActivity(intent)
                    activity.finish()
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    "查看离线数据".split("").forEach {
                        Text(text = it, style = MaterialTheme.typography.h6, maxLines = 1)
                    }
                }
            }
        }
    )
}

fun checkInputAndShow(
    userName: String, userPassword: String,
    viewModel: LoginSpaceViewModel,
    buttonText: String,
): String? {
    return when {
        buttonText != "登录" || viewModel.ids == "" -> {
            buttonText
        }
        userName == "" -> {
            "请输入学号"
        }
        userPassword == "" -> {
            "请输入密码"
        }
        else -> {
            viewModel.putValue(userName, userPassword, viewModel.ids)
            null
        }
    }
}
