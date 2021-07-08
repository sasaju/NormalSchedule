package com.liflymark.normalschedule.ui.score_detail

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.ui.import_show_score.ImportScoreViewModel
import com.liflymark.normalschedule.ui.score_detail.ui.theme.NormalScheduleTheme
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

class LoginToScoreActivity : ComponentActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(ImportScoreViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getId()
        setContent {
            NormalScheduleTheme {
                Input(viewModel)
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Input(viewModel: ImportScoreViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
          scaffoldState: ScaffoldState = rememberScaffoldState()) {
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val activity = (LocalContext.current as? Activity)
    scope.launch{
        if (viewModel.isAccountSaved()){
            user = viewModel.getSavedAccount()["user"].toString()
            password = viewModel.getSavedAccount()["password"].toString()
        }
        scaffoldState.snackbarHostState.showSnackbar("aaa")

    }
    Image(
        painter = painterResource(id = R.drawable.main_background_4),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Card(modifier = Modifier
            .height(300.dp)
            .fillMaxWidth(0.95f)
            .alpha(0.8f)
            .padding(5.dp),elevation = 5.dp, shape =  RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {

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
                Spacer(modifier = Modifier.height(30.dp))
                Button(onClick = {
                    scope.launch {
                        checkInputAndShow(userName = user, userPassword = password, viewModel = viewModel, scaffoldState=scaffoldState)
                    }

                }) {
                    Text(text = "登陆并查看成绩")
                }
            }
        }
    }
}


suspend fun checkInputAndShow(userName:String, userPassword: String, viewModel: ImportScoreViewModel, scaffoldState:ScaffoldState){
    val ids =  viewModel.id

    when {
        ids == "" -> scaffoldState.snackbarHostState.showSnackbar(message = "链接错误")
        userName == "" -> scaffoldState.snackbarHostState.showSnackbar(message = "请输入学号")
        userPassword == "" -> scaffoldState.snackbarHostState.showSnackbar(message = "请输入密码")
        else -> {
            scaffoldState.snackbarHostState.showSnackbar(message = "正在提交，请等待")
            viewModel.putValue(userName, userPassword, ids)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview3() {
    NormalScheduleTheme {
        Input()
    }
}