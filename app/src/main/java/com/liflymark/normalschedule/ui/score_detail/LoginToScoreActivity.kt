package com.liflymark.normalschedule.ui.score_detail

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.sign_in_compose.SignUIAll
import com.liflymark.normalschedule.ui.theme.NorScTheme
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

class LoginToScoreActivity : ComponentActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(LoginToScoreViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UiControl()
            NorScTheme {
                val state = rememberScaffoldState()
                Scaffold(
                    scaffoldState = state,
                    topBar = {
                        NormalTopBar(label = "成绩明细")
                    },
                    content = {
                        LoginToScoreDetail(state = state)
                    }
                )
            }
        }
    }
}
@Composable
fun LoginToScoreDetail(
    state: ScaffoldState
) {
    val focusManager = LocalFocusManager.current
    val activity = LocalContext.current as LoginToScoreActivity
    val loginScoreViewModel: LoginToScoreViewModel = viewModel()
    var loginText by rememberSaveable { mutableStateOf("正在加载...") }
    var loginEnable by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scoreResult = loginScoreViewModel.scoreDetailState.observeAsState()
    LaunchedEffect(scoreResult.value){
        if (scoreResult.value?.result == "登陆成功" && scoreResult.value!=null) {
            val gradeList = scoreResult.value!!.grade_list
            val intent = Intent(activity, ShowDetailScoreActivity::class.java).apply {
                putExtra("detail_list", Convert.detailGradeToJson(gradeList))
            }
            loginText = "登录"
            activity.startActivity(intent)
            activity.finish()
        } else {
            loginText = "登录"
            loginEnable = true
            scoreResult.value?.result?.let { state.snackbarHostState.showSnackbar(it) }
        }
    }
    SignUIAll(
        scaffoldState = state,
        onLove = {
            loginText = "连接异常"
        },
        onSuccess = {
            loginScoreViewModel.ids = it
            loginText = "登录"
            loginEnable = true
        },
        loginButton = { user, password ->
            Button(
                enabled = loginEnable,
                onClick =
                {
                    focusManager.clearFocus()
                    val result = checkInputAndShow(
                        userName =  user,
                        userPassword = password,
                        viewModel = loginScoreViewModel,
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
            OutlinedButton(
                onClick = {
                    val detail = Repository.getScoreDetail()
                    if (detail == ""){
                        Toasty.info(activity, "当前没有缓存数据").show()
                    } else {
                        val intent = Intent(activity, ShowDetailScoreActivity::class.java).apply {
                            putExtra("detail_list", detail)
                        }
                        Repository.cancelAll()
                        activity.startActivity(intent)
                        activity.finish()
                    }
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    "查看缓存数据".split("").forEach {
                        Text(text = it, style = MaterialTheme.typography.h6, maxLines = 1)
                    }
                }
            }
        }
    )
}

fun checkInputAndShow(
    userName: String, userPassword: String,
    viewModel: LoginToScoreViewModel,
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

@Composable
fun UiControl(){
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


//fun refreshId(activity: LoginToScoreActivity,viewModel: LoginToScoreViewModel, openDialog: MutableState<Boolean>, success:() -> Unit = {}){
//    viewModel.idLiveData.observe(activity) {
//        if (it.isSuccess) {
//            viewModel.ids = it.getOrNull()?.id ?: ""
//            success()
//            Toasty.success(activity, "访问服务器成功").show()
//        }
//    }
//    viewModel.getId()
//    viewModel.scoreDetailState.observe(activity) {
//        Log.d("Log", "内容更新")
//        if (it.isSuccess) {
//            val result = it.getOrNull()
//            if (result != null) {
//                if (result.result == "登陆成功") {
//                    val gradeList = result.grade_list
//                    val intent = Intent(activity, ShowDetailScoreActivity::class.java).apply {
//                        putExtra("detail_list", Convert.detailGradeToJson(gradeList))
//                    }
//                    activity.startActivity(intent)
//                    activity.finish()
//                } else {
//                    Toasty.error(activity, result.result).show()
//                }
//            } else {
//                Toasty.error(activity, "访问失败，访问结果为null").show()
//            }
//        } else {
//            Toasty.error(activity, "访问失败，访问未成功").show()
//        }
//        openDialog.value = false
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview3() {
//    NorScTheme {
//        Column(modifier = Modifier
//            .background(Color.LightGray)
//            .size(100.dp)
//            .verticalScroll(rememberScrollState()))
//        {
//
//
//        }
//    }
//}