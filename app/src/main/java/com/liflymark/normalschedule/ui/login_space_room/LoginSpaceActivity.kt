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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.ui.class_course.ui.theme.NormalScheduleTheme
import com.liflymark.normalschedule.ui.score_detail.*
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

class LoginSpaceActivity : ComponentActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(LoginSpaceViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NorScTheme() {
                UiControl()
                Scaffold(
                    topBar = {
                        NormalTopBar(label = "空教室查询")
                    },
                    content = {
                        InputSpace(viewModel)
                    }
                )
            }
        }
    }
}

@Composable
fun InputSpace(lsViewModel: LoginSpaceViewModel = viewModel()) {
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val openWaitDialog = remember {
        mutableStateOf(false)
    }
    val ids = lsViewModel.idLiveData.observeAsState(initial = lsViewModel.initialResultId)
    val spaceResult =
        lsViewModel.loginSpaceLiveData.observeAsState(initial = lsViewModel.initialSpace)
    val activity = (LocalContext.current as LoginSpaceActivity)
    ProgressDialog(openDialog = openWaitDialog, label = "正在链接\n教务系统"){
        Spacer(modifier = Modifier
            .width(100.dp)
            .height(5.dp)
            .padding(2.dp)
            .background(Color.Gray))
        TextButton(onClick = {
//            val intent = Intent(activity, ShowSpaceActivity::class.java).apply {
//                putExtra("ids", "love")
//            }
            Repository.cancelAll()
            openWaitDialog.value = false
//            activity.startActivity(intent)
//            activity.finish()
        }) {
            Text(text = "进入离线查询")
        }
    }
    LaunchedEffect(key1 = true, block = { lsViewModel.getId() })
    LaunchedEffect(ids.value) {
        openWaitDialog.value = ids.value.id == ""
        lsViewModel.id = ids.value.id
        if (ids.value.id == "love"){
            Toasty.info(activity, "无法链接至教务系统，查询结果可能不准确").show()
            val intent = Intent(activity, ShowSpaceActivity::class.java).apply {
                putExtra("ids", ids.value.id)
            }
            activity.startActivity(intent)
            activity.finish()
        }else{
            launch {
                if (lsViewModel.isAccountSaved()) {
                    user = lsViewModel.getSavedAccount()["user"].toString()
                    password = lsViewModel.getSavedAccount()["password"].toString()
                }
            }
        }
    }
    LaunchedEffect(spaceResult.value) {
        Log.d("SpaceACti", spaceResult.value.result)
        when (spaceResult.value.result) {
            "登陆成功" -> {
                Toasty.success(activity, "登陆成功").show()
                val intent = Intent(activity, ShowSpaceActivity::class.java).apply {
                    putExtra("ids", ids.value.id)
                }
                activity.startActivity(intent)
                activity.finish()
            }
            "初始化" -> {
                Toasty.success(activity, "正在链接，请等待").show()
            }
            else -> {
                Toasty.error(activity, spaceResult.value.result).show()
            }
        }
    }
//    Canvas(modifier = Modifier.fillMaxSize()) {
//        val canvasWidth = size.width
//        val canvasHeight = size.height
//        val lightBlue = Color(0xff2196f3)
//        drawPath(
//            path = starPath(canvasWidth/2,canvasHeight/2),
//            color = lightBlue,
//        )
//    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(40.dp))
        Card(
            modifier = Modifier
                .height(350.dp)
                .fillMaxWidth(0.95f)
                .alpha(0.8f)
                .padding(5.dp), elevation = 5.dp, shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it },
                    label = { Text("请输入学号") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(0.95f)
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("请输入统一认证密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(0.95f)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(0.95f)) {
                    Checkbox(checked = true, onCheckedChange = null)
                    Text(text = "记住密码")
                }

                Spacer(modifier = Modifier.height(30.dp))
                Button(onClick = {
                    checkInputAndShow(
                        activity, user, password, lsViewModel,
                        lsViewModel.id, error = { openWaitDialog.value = false })
                    openWaitDialog.value = true
                }) {
                    Text(text = "登陆以查询空教室")
                }
            }
        }
    }
}

fun checkInputAndShow(
    activity: LoginSpaceActivity,
    userName: String, userPassword: String,
    viewModel: LoginSpaceViewModel,
    ids: String,
    error: () -> Unit = {}
) {
    when {
        ids == "" -> {
            Toasty.info(activity, "访问服务器异常，请重试", Toasty.LENGTH_SHORT).show()
            error()
        }
        userName == "" -> {
            Toasty.info(activity, "请输入学号", Toasty.LENGTH_SHORT).show()
            error()
        }
        userPassword == "" -> {
            Toasty.info(activity, "请输入密码", Toasty.LENGTH_SHORT).show()
            error()
        }
        else -> {
            viewModel.putValue(userName, userPassword, ids)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview6() {
    NormalScheduleTheme {
    }
}