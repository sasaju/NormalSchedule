package com.liflymark.normalschedule.ui.score_detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.ui.score_detail.ui.theme.NormalScheduleTheme
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.test.ui.theme.TestTheme
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

class LoginToScoreActivity : ComponentActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(LoginToScoreViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UiControl()
            NormalScheduleTheme {
                Scaffold(
                    topBar = {
                        NormalTopBar(label = "成绩明细")
                    },
                    content = {
                        Input(viewModel)
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
    }
}


@Composable
fun Input(loginToScoreViewModel: LoginToScoreViewModel = viewModel()) {
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val openWaitDialog = remember {
        mutableStateOf(false)
    }
    val activity = (LocalContext.current as? LoginToScoreActivity)
    WaitDialog(openDialog = openWaitDialog)
    LaunchedEffect(true){
        launch{
            if (loginToScoreViewModel.isAccountSaved()){
                user = loginToScoreViewModel.getSavedAccount()["user"].toString()
                password = loginToScoreViewModel.getSavedAccount()["password"].toString()
            }
            loginToScoreViewModel.getId()
            if (activity != null) {
                refreshId(activity = activity, viewModel = loginToScoreViewModel, openDialog = openWaitDialog)
            } else {
                loginToScoreViewModel.id = ""
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
                    checkInputAndShow(activity!!,user, password, loginToScoreViewModel,
                        openWaitDialog, loginToScoreViewModel.id)
                    openWaitDialog.value = true
                }) {
                    Text(text = "登陆并查看成绩")
                }
            }
        }
    }
}


fun checkInputAndShow(
    activity: LoginToScoreActivity,
    userName:String, userPassword: String,
    viewModel: LoginToScoreViewModel,
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

fun refreshId(activity: LoginToScoreActivity,viewModel: LoginToScoreViewModel, openDialog: MutableState<Boolean>){
    viewModel.idLiveData.observe(activity) {
        if (it.isSuccess) {
            viewModel.id = it.getOrNull()?.id ?: ""
            Toasty.success(activity, "访问服务器成功").show()
        }
    }
    viewModel.getId()
    viewModel.scoreDetailState.observe(activity) {
        Log.d("Log", "内容更新")
        if (it.isSuccess) {
            val result = it.getOrNull()
            if (result != null) {
                if (result.result == "登陆成功") {
                    val gradeList = result.grade_list
                    val intent = Intent(activity, ShowDetailScoreActivity::class.java).apply {
                        putExtra("detail_list", Convert.detailGradeToJson(gradeList))
                    }
                    activity.startActivity(intent)
                    activity.finish()
                } else {
                    Toasty.error(activity, result.result).show()
                }
            } else {
                Toasty.error(activity, "访问失败，访问结果为null").show()
            }
        } else {
            Toasty.error(activity, "访问失败，访问未成功").show()
        }
        openDialog.value = false
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview3() {
    TestTheme {
        Column(modifier = Modifier
            .background(Color.LightGray)
            .size(100.dp)
            .verticalScroll(rememberScrollState()))
        {


        }
    }
}