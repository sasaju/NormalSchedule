package com.liflymark.normalschedule.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

@Composable
fun LogoutPage(
    navController: NavController
){
    Scaffold(
        topBar = {
            NormalTopBar(label = "注销账号"){
                navController.navigateUp()
            }
        },
        content = {
            it
            Login(){
                navController.navigateUp()
            }
        }
    )
}


@Composable
fun Login(
    errorMax:() -> Unit
){
    var user by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var id by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var status by rememberSaveable {
        mutableStateOf("")
    }
    val context = LocalContext.current
    var errorNum by rememberSaveable { mutableStateOf(0) }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextWarning()
        Text(text = status)
        Row{
            Text(text = "账号")
            TextField(value =user, onValueChange = { user=it })
        }
        Row{
            Text(text = "密码")
            TextField(value =password, onValueChange = { password=it })
        }
        Button(onClick = {
            status = "正在执行身份验证"
            if (password!=""&&user!=""){
                if (errorNum>4){
                    errorMax()
                }
                scope.launch {
                    val res = Repository.getId6()
                    if (res.id!="love"){
                        id =res.id
                    }else{
                        Toasty.error(context,"当前注销失败，无法与服务器取得联系，请重新打开该页面试试，如情况紧急请联系开发者").show()
                    }
                    val logRes = Repository.logoutApp(user, password, id, "")
                    if (logRes.result!="登陆成功"){
                        errorNum += 1
                    }
                    status = logRes.msg
                }
            }else{
                Toasty.error(context,"验证失败").show()
            }
        }) {
            Text(text = "注销")
        }
    }
}


@Composable
fun TextWarning(){
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "注销后无法在此APP登陆，请慎重使用！！！",
            style = MaterialTheme.typography.h4,
        )
        Text(
            text = "注销遇到任何问题可以邮件联系lifly@lifly.cn，我会在48小时内处理",
        )
    }
}
