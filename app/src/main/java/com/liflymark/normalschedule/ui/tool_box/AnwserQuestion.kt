package com.liflymark.normalschedule.ui.tool_box

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import es.dmoral.toasty.Toasty

@ExperimentalMaterialApi
@Composable
fun QuestionAllPage(
    navController: NavController
){
    NorScTheme {
        Scaffold(
            topBar = {
                NormalTopBar(label = "常见问题") {
                    navController.navigateUp()
                }
            },
            content = {
                QuestionContent()
            }
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun QuestionContent(){
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        QuestionCard(
            onClick = {  },
            question = "1.为什么有时候成绩无法查询？",
            answer = "成绩数据目前全部来源于教务系统，如果教务系统无法通过EasyConnect访问，那么也就无法查询，" +
                    "开发者未来可能会加入本地缓存，用来显示查询历史，但时间未确定，请持续关注。"
        )
        QuestionCard(
            onClick = {  },
            question = "2.如何调整桌面小部件大小？",
            answer = "请自行百度：”你的手机品牌+调整小部件大小“"
        )
        QuestionCard(
            onClick = {
                val key = "IQn1Mh09oCQwvfVXljBPgCkkg8SPfjZP"
                val intent = Intent()
                intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key")
                // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
                // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    context.startActivity(intent)
                } catch (e: Exception){
                    Toasty.error(context, "未安装QQ").show()
                }
            },
            question = "3.能否加入xxx功能？",
            answer = "社交功能不加，广告功能不加，笔记功能不加，其他功能建议欢迎加入反馈群或发邮件反馈。（直接点击可跳转至QQ反馈群）"
        )
        QuestionCard(
            onClick = {  },
            question = "4.为什么开发速度这么慢，质量也不高？",
            answer = "开发者只有一个人，UI、逻辑处理、后台搭建全部为一个人，平时还需上课、做实验，精力和能力实在有限。如果有愿意一起开发、帮我设计UI的小伙伴欢迎联系我。"
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun QuestionCard(
    onClick:() -> Unit,
    question:String,
    answer:String
){
    Card(
        onClick = { onClick() },
        elevation = 1.dp,
        modifier = Modifier
            .padding(vertical = 2.dp, horizontal = 3.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth()
        ) {
            Text(text = question, style = MaterialTheme.typography.h6,fontStyle = FontStyle.Italic)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = answer, style = MaterialTheme.typography.body1, fontStyle = FontStyle.Normal, color = Color.Gray)
        }
    }
}


@ExperimentalMaterialApi
@Composable
@Preview(showBackground = true)
fun AnswerPreView(){
    NorScTheme {
        QuestionContent()

    }
}