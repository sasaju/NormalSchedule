package com.liflymark.normalschedule.ui.update_course

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.liflymark.normalschedule.logic.model.UserTypeResponse

/** “我的”页面
 * @param userType 获取用户身份返回结果
 */
@Composable
fun ExampleUser(userType: UserTypeResponse){
    val userMap = mapOf(
        200 to userType.content,
        300 to "未知类型",
        301 to "链接服务器失败",
        302 to "未登录用户"
    )
    var showExplainDialog by remember { mutableStateOf(false) }
    var showHowDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.AccountCircle,
                null,
                modifier = Modifier
                    .size(60.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 14.sp
                    )
                ){
                    append("您当前的用户类型：")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.primary,
                        fontSize = 14.sp
                    )
                ){
                    userMap[userType.statusCode]?.let { append(it) }
                }
            },
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showExplainDialog = true
                }
        )
        if (showExplainDialog){
            ExplainDialog(
                onDismissRequest = {
                    showExplainDialog = false
                },
                howTo = {
                    showExplainDialog = false
                    showHowDialog = true
                }
            )
        }
        if(showHowDialog){
            HowToDialog {
                showHowDialog = false
            }
        }
    }
}