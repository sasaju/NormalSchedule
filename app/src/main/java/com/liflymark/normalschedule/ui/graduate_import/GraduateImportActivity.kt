package com.liflymark.normalschedule.ui.graduate_import

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.CornerDialog
import com.liflymark.normalschedule.logic.utils.coil.transform.BlurTransformation
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.show_timetable.ShowTimetableActivity2
import com.liflymark.normalschedule.ui.sign_in_compose.LoginAccountPassword
import com.liflymark.normalschedule.ui.sign_in_compose.LoginTextFiled
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.sign_in_compose.SignUIViewModel
import com.liflymark.normalschedule.ui.theme.NorScTheme
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

class GraduateImportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NorScTheme {
                // A surface container using the 'background' color from the theme
                UiControl()
                val state = rememberScaffoldState()
                Scaffold(
                    scaffoldState = state,
                    topBar = { NormalTopBar(label = "研究生导入") },
                    content = {
                        it
                        SignUIAll(scaffoldState = state){  _,_ -> }
                    }
                )
            }
        }
    }
}
@Composable
fun SignUIAll(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    loginButton:@Composable ((user:String, password:String) -> Unit)?
) {
    val scope = rememberCoroutineScope()
    val viewModel: SignUIViewModel = viewModel()
    val graduateImportViewModel:GraduateImportViewModel = viewModel()
    val backRoute = viewModel.backgroundRoute.observeAsState()
    val context = LocalContext.current
    val activity = context as GraduateImportActivity
    val focusManager = LocalFocusManager.current
    val loginVPN = remember { graduateImportViewModel.loginWebVPNState }
    val loginURP = remember {  graduateImportViewModel.loginURPState }
    val captcha = graduateImportViewModel.captchaFlow.collectAsState(initial = null)
    val yzmFocusRequester = FocusRequester()

    val loginVPNorNot = rememberSaveable{ graduateImportViewModel.loginVPNorNot }
    var buttonEnable by rememberSaveable { mutableStateOf(true) }
    var buttonText by rememberSaveable { mutableStateOf("登录") }
    val showCounter = graduateImportViewModel.showCounter.collectAsState(initial = 8)
    val showWarning = remember { graduateImportViewModel.showWarning }
    WarnGraduateDialog(
        show = showWarning.value,
        remainSec = showCounter.value
    ) {
        showWarning.value = false
    }

    LaunchedEffect(loginVPNorNot.value){
        if (loginVPNorNot.value){
            scaffoldState.snackbarHostState.showSnackbar("请输入验证码")
        }
    }
    LaunchedEffect(loginVPN.value){
        if (loginVPN.value=="登陆成功"){
            focusManager.clearFocus()
            yzmFocusRequester.requestFocus()
            loginVPNorNot.value = true
        }else{
            loginVPN.value?.let { scaffoldState.snackbarHostState.showSnackbar(it) }
        }
    }
    LaunchedEffect(loginURP.value){
        loginURP.value?.let {
            if (it.result!="登陆成功，解析成功"){
                Toasty.warning(context, it.result).show()
            }else{
                Toasty.success(context, it.result).show()
            }
            if(it.status=="yes"){
                val intent = Intent(context, ShowTimetableActivity2::class.java).apply {
                    putExtra("isSaved", false)
                    putExtra("courseList", Convert.allCourseToJson(it.allCourse))
                    putExtra("user", graduateImportViewModel.user)
                    putExtra("password", graduateImportViewModel.password)
                }
                Repository.saveAccount(graduateImportViewModel.user, graduateImportViewModel.password)
                activity.startActivity(intent)
                activity.finish()
            }else{
                graduateImportViewModel.getCaptcha()
            }
        }
    }

    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .transformations(BlurTransformation(context, radius = 25F, sampling = 5F))
                .crossfade(true)
                .data(backRoute.value)
                .build()
        ),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = { focusManager.clearFocus() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() })
        ,
        contentScale = ContentScale.Crop
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.TopCenter
    ) {
        val imageHeight = 60
        val spaceHeight = 50
        Column(
        ) {
            Spacer(modifier = Modifier.height((imageHeight/2 + spaceHeight).dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(2.dp)
                    .alpha(0.9f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.2.dp, Color(0xFF80D4FF)),
                backgroundColor = MaterialTheme.colors.background
            ) {
                Column(
                    modifier = Modifier.wrapContentHeight()
                ) {
                    LoginAccountPassword(forGraduate = "-研究生", readOnly = loginVPNorNot.value){ user, password ->
//                        if (loginButton != null) {
//                            loginButton(user, password)
//                        }
                        var yzm by rememberSaveable { mutableStateOf("") }
                        if(loginVPNorNot.value){
                            Row{
                                Text(text = "如右侧无显示\n请点击右侧空白处", textAlign = TextAlign.Center)
                                AsyncImage(
                                    model = ImageRequest
                                        .Builder(context = context)
                                        .data(captcha.value)
                                        .build(),
                                    "验证码",
                                    modifier = Modifier
                                        .height(30.dp)
                                        .width(90.dp)
                                        .background(Color.Gray)
                                        .clickable {
                                            graduateImportViewModel.getCaptcha()
                                        }
                                    ,
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            LoginTextFiled(
                                value = yzm,
                                onValueChange = { yzm = it },
                                placeholder = {
                                    Text(text = "请输入验证码")
                                },
                                modifier = Modifier
                                    .focusRequester(yzmFocusRequester)
                            )
                        }
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                buttonEnable = false
                                buttonText = "登录中..."
                                scope.launch{
                                    if (!loginVPNorNot.value) {
                                        graduateImportViewModel.loginVPN(user, password)
                                    } else {
                                        graduateImportViewModel.loginURP(yzm = yzm)
                                    }
                                    buttonEnable = true
                                    buttonText = "登录"
                                }
                            },
                            enabled=buttonEnable,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Row(
                                modifier =  Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                buttonText.split("").forEach {
                                    Text(text = it, style = MaterialTheme.typography.h6, maxLines = 1)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
//                        OutlinedButton(
//                            onClick = { /*TODO*/ },
//                            shape = RoundedCornerShape(20.dp),
//                            modifier = Modifier.fillMaxWidth(0.8f)
//                        ) {
//                            Row(
//                                modifier =  Modifier.fillMaxWidth(0.8f),
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                "查看离线数据".split("").forEach {
//                                    Text(text = it, style = MaterialTheme.typography.h6, maxLines = 1)
//                                }
//                            }
//                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier.background(Color.Transparent)
        ){
            Spacer(modifier = Modifier.height(spaceHeight.dp))
            Box(
                modifier = Modifier
                    .background(Color.Transparent)
                    .clip(CircleShape),
                propagateMinConstraints = false
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context = context)
                        .crossfade(true)
                        .data(R.mipmap.ic_launcher_foreground)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFF80D4FF)),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }
}

@Composable
fun WarnGraduateDialog(
    show:Boolean,
    remainSec:Int,
    onDismissRequest: () -> Unit
){
    if (show){
        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
            title = {
                Text(text = "研究生导入课程注意事项")
            },
            text = {
                Column{
                    Text(text = "导入后请仔细检查是否缺课少课!!!", color=Color.Red)
                    Text(
                        text = "1.该导入功能仍处于实验性阶段，极有可能导入缺失，请务必仔细检查，仔细检查，仔细检查！！！\n" +
                                "2.因导入缺失造成的不良后果开发者概不负责\n" +
                                "3.目前依然不支持使用研究生账号使用APP内的其他查询功能（如成绩查询、考试安排查询）\n"+
                                "4.如果课程缺失可以在“关于”界面加入反馈群反馈，但不保证开发者有时间解决。我也是在校生，时间也是有限的"
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onDismissRequest() },
                    enabled = remainSec==0
                ) {
                    if (remainSec==0){ Text(text = "接受并已知晓上述内容") }
                    else{ Text(text = "剩余${remainSec}秒") }
                }
            }
        )
    }
}

