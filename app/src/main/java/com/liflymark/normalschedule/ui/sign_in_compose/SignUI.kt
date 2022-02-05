package com.liflymark.normalschedule.ui.sign_in_compose

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.google.accompanist.insets.statusBarsHeight
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.utils.coil.transform.BlurTransformation



@Composable
fun SignUIAll(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onLove:() -> Unit = {},
    onSuccess:(id:String) -> Unit = {},
    loginButton:@Composable ((user:String, password:String) -> Unit)?
) {
    val viewModel: SignUIViewModel = viewModel()
//    val idSate = viewModel.idFlow.collectAsState(initial = null)
    val idSate = remember { viewModel.idState }
    val backRoute = viewModel.backgroundRoute.observeAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    LaunchedEffect(idSate.value){
        when (idSate.value) {
            "" -> { scaffoldState.snackbarHostState.showSnackbar("正在链接服务器") }
            "love" -> {
                onLove()
                scaffoldState.snackbarHostState.showSnackbar("链接失败")
            }
            else -> {
                onSuccess(idSate.value)
                scaffoldState.snackbarHostState.showSnackbar("连接成功")
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
                    LoginAccountPassword{ user, password ->
                        if (loginButton != null) {
                            loginButton(user, password)
                        }
//                        Button(
//                            onClick = { /*TODO*/ },
//                            shape = RoundedCornerShape(20.dp),
//                            modifier = Modifier.fillMaxWidth(0.8f)
//                        ) {
//                            Row(
//                                modifier =  Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                "登录".split("").forEach {
//                                    Text(text = it, style = MaterialTheme.typography.h6, maxLines = 1)
//                                }
//                            }
//                        }
//                        Spacer(modifier = Modifier.height(10.dp))
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
fun LoginAccountPassword(
    loginButton:@Composable ((user:String, password:String) -> Unit)?,
){
    val viewModel:SignUIViewModel = viewModel()
    val accountSate = viewModel.accountFlow.collectAsState(initial = mapOf("user" to "", "password" to ""))
    var user by remember{ viewModel.userSate }
    var password by remember { viewModel.passwordSate }
    var visual by remember { mutableStateOf<VisualTransformation>(PasswordVisualTransformation()) }
    var visualIcon by remember { mutableStateOf(Icons.Default.Visibility) }
    LaunchedEffect(accountSate.value){
        viewModel.passwordSate.value = accountSate.value["password"].toString()
        viewModel.userSate.value = accountSate.value["user"].toString()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "登录URP教务系统", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(20.dp))
        LoginTextFiled(
            onValueChange = {user = it}, 
            value = user,
            label = { Text("请输入学号") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(0.95f),
            leadingIcon = {Icon(imageVector = Icons.Default.Person, contentDescription = "学号")}
        )
        Spacer(modifier = Modifier.height(20.dp))
        LoginTextFiled(
            onValueChange = {password = it}, 
            value = password,
            label = { Text(text = "请输入密码")},
            visualTransformation = visual,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(0.95f),
            leadingIcon = {Icon(imageVector = Icons.Default.Lock, contentDescription = "密码")},
            trailingIcon = {
                IconButton(onClick = {
                    visual = if (visual== VisualTransformation.None) { PasswordVisualTransformation() } else { VisualTransformation.None}
                    visualIcon = if (visualIcon == Icons.Default.VisibilityOff){ Icons.Default.Visibility }else{Icons.Default.VisibilityOff}
                }) {
                    Icon(imageVector = visualIcon, contentDescription = "查看")
                }
            }
        )
        if (loginButton != null) {
            Spacer(modifier = Modifier.height(40.dp))
            loginButton(user, password)
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun LoginTextFiled(
    modifier: Modifier=Modifier,
    value:String="",
    onValueChange:(value:String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
){
    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        modifier = modifier,
        leadingIcon=leadingIcon,
        trailingIcon = trailingIcon,
        label= label,
        placeholder = placeholder,
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
        ),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation
    )
}
