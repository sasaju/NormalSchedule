package com.liflymark.normalschedule.ui.update_course

import android.content.Intent
import android.net.Uri
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import es.dmoral.toasty.Toasty

@Composable
fun ExplainDialog(
    onDismissRequest:() -> Unit,
    howTo:() -> Unit
){
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        text = {
            Text(text = "普通用户：仅有查看并导入同年级专业的导课数据。\n认证用户：普通用户权限基础上同时拥有上传本班级导课数据的权限\n" +
                    "未登陆用户：请在成绩查询界面登陆成功一次以验证身份\n未知用户：您可能是游客登陆，请尝试在成绩查询界面登陆成功一次以验证身份")
        },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
            }) {
                Text(text = "知道了")
            }
        },
        dismissButton = {
            TextButton(onClick = { howTo() }) {
                Text(text = "如何成为认证用户？")
            }
        }
    )
}

@Composable
fun HowToDialog(
    onDismissRequest:() -> Unit,
){
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        text = {
            Text(text = "加入QQ反馈群-》联系群主-》提供学号和一卡通照片（头像、姓名请打马赛克，仅留出学号和专业即可） -》提供一个昵称-》完成认证\n" +
                    "注意：如果您是您班学委或班长请特别说明，将优先通过。其他身份将酌情通过，请知悉。")
        },
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest()
            }) {
                Text(text = "知道了")
            }
        },
        confirmButton = {
            val context = LocalContext.current
            TextButton(onClick = {
                val key = "IQn1Mh09oCQwvfVXljBPgCkkg8SPfjZP"
                val intent = Intent()
                intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key")
                // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    context.startActivity(intent)
                } catch (e: Exception){
                    Toasty.error(context, "未安装QQ").show()
                }
            }) {
                Text(text = "加入反馈群")
            }
        }
    )
}
