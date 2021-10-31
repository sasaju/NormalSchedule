package com.liflymark.normalschedule.ui.exam_arrange

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.ui.score_detail.*
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch


class ExamActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UiControl()
            NorScTheme {
                Scaffold(
                    topBar = {
                        NormalTopBar(label = "考试安排")
                    },
                    content = {
                        Input()
                    }
                )
            }
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


@Composable
fun Input(loginToExamViewModel: LoginToExamViewModel = viewModel()) {
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val openWaitDialog = remember {
        mutableStateOf(false)
    }
    val activity = (LocalContext.current as? ExamActivity)
    WaitDialog(openDialog = openWaitDialog)
    LaunchedEffect(true){
        launch{
            if (loginToExamViewModel.isAccountSaved()){
                user = loginToExamViewModel.getSavedAccount()["user"].toString()
                password = loginToExamViewModel.getSavedAccount()["password"].toString()
            }
            loginToExamViewModel.getId()
            if (activity != null) {
                refreshId(activity = activity, viewModel = loginToExamViewModel, openDialog = openWaitDialog)
            } else {
                loginToExamViewModel.id = ""
            }
        }
    }
//    Image(
//        painter = painterResource(id = R.drawable.main_background_4),
//        contentDescription = null,
//        modifier = Modifier.fillMaxSize(),
//        contentScale = ContentScale.FillBounds
//    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val lightBlue = Color(0xff2196f3)
        drawPath(
            path = starPath(canvasWidth/2,canvasHeight/2),
            color = lightBlue,
//            style = Stroke(width = 4F)
        )
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(40.dp))
        Card(modifier = Modifier
            .height(350.dp)
            .fillMaxWidth(0.95f)
            .alpha(0.8f)
            .padding(5.dp),elevation = 5.dp, shape =  RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
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
                    checkInputAndShow(activity!!,user, password, loginToExamViewModel,
                        openWaitDialog, loginToExamViewModel.id)
                    openWaitDialog.value = true
                }) {
                    Text(text = "登陆并查看成绩")
                }
            }
        }
    }
}


fun checkInputAndShow(
    activity: ExamActivity,
    userName:String, userPassword: String,
    viewModel: LoginToExamViewModel,
    openWaitDialog: MutableState<Boolean>,
    ids: String
){
    when {
        ids == "" -> {
            Toasty.info(activity, "访问服务器异常，请重试", Toasty.LENGTH_SHORT).show()
            openWaitDialog.value = false
        }
        userName == "" -> {
            Toasty.info(activity, "请输入学号", Toasty.LENGTH_SHORT).show()
            openWaitDialog.value = false
        }
        userPassword == "" -> {
            Toasty.info(activity, "请输入密码", Toasty.LENGTH_SHORT).show()
            openWaitDialog.value = false
        }
        else -> {
            viewModel.putValue(userName, userPassword, ids)
        }
    }
}

fun refreshId(activity: ExamActivity, viewModel: LoginToExamViewModel, openDialog: MutableState<Boolean>){
    viewModel.idLiveData.observe(activity) {
        if (it.isSuccess) {
            viewModel.id = it.getOrNull()?.id ?: ""
            Toasty.success(activity, "访问服务器成功").show()
            openDialog.value = false
        }
    }
    viewModel.getId()
    viewModel.scoreDetailState.observe(activity) {
        Log.d("Log", "内容更新")
        if (it.result == "登陆成功") {
            val arrangeList = it.arrange_list
            val intent = Intent(activity, ShowArrangeActivity::class.java).apply {
                putExtra("detail_list", Convert.examArrangeToJson(arrangeList))
            }
            activity.startActivity(intent)
            activity.finish()
        } else {
            Toasty.error(activity, it.result).show()
        }
        openDialog.value = false
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview4() {
    NorScTheme {
        Greeting("Android")
    }
}