package com.liflymark.normalschedule.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.afollestad.materialdialogs.MaterialDialog
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.utils.Dialog
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_import_login.*

class ComposeAboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UiControl()
            NorScTheme {
                Scaffold(
                    topBar = {
                        NormalTopBar(label = "关于")
                    },
                    content = {
                        AboutPage()
                    }
                )
            }
        }
    }

    fun openBrowser(url: String){
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: Exception) {
            println("当前手机未安装浏览器")
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun AboutPage(){
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Image(
            painter = rememberImagePainter(
                data = R.mipmap.ic_launcher
            ),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text("河大课表", style = MaterialTheme.typography.h5, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(25.dp))
        Text(text = "一款针对河北大学教务系统的课表APP", color = Color.Gray)
        Spacer(modifier = Modifier.height(30.dp))

        Introduce()
    }
}

@Composable
fun Introduce(){
    val context = LocalContext.current
    val activity = LocalContext.current as ComposeAboutActivity
    val pm = context.packageManager
    val versionName = pm.getPackageInfo(context.packageName, 0).versionName
    val dialog = remember {
        Dialog.getContractDialog(
            context,
            yes = {
                Toasty.success(context, "您已同意隐私政策及用户协议").show()
            },
            no = {
                Toasty.info(context,"如果您拒绝该隐私政策或用户协议请立即关闭应用程序").show()
            }
        )
    }
    val userDialog = remember {
        Dialog.getUerContract(
            context,
            yes = {
                Toasty.info(context, "请点击登陆按钮上方的复选框以再次确认").show()
            },
            no = {
                Toasty.info(context,"如果您拒绝该隐私政策或用户协议请立即关闭应用程序").show()
            }
        )
    }
    Column {
        SingleIconButton(
            icon =Icons.Default.BubbleChart,
            text = "当前版本：$versionName") {}
        SingleIconButton(
            icon = Icons.Default.Groups, 
            text = "加入反馈群" 
        ) {
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
        }
        SingleIconButton(
            icon = Icons.Default.Adb,
            text = "开源许可"
        ) {
            val intent = Intent(context, GitListActivity::class.java)
            context.startActivity(intent)
        }
        SingleIconButton(
            icon = Icons.Default.Star,
            text = "用户协议及隐私政策"
        ) {
            activity.openBrowser("https://liflymark.top/privacy/")
        }
        SingleIconButton(
            icon = Icons.Default.Star,
            text = "隐私政策(内置)"
        ) {
            dialog.show()
        }
        SingleIconButton(
            icon = Icons.Default.Star,
            text = "用户协议(内置)"
        ) {
            userDialog.show()
        }
        SingleIconButton(
            icon = Icons.Default.Category,
            text = "关于开发组"
        ) {
            MaterialDialog(context)
                .title(text = "关于开发组")
                .message(text = "开发者：\n  河北大学 | 大三药物制剂在读@符号 \n  (QQ:1289142675) \nLOGO、背景图绘制：\n   河北大学 | 大三药学在读@Mr.")
                .positiveButton(text = "知道了")
                .show()
        }
    }
}

@Composable
fun SingleIconButton(
    icon:ImageVector,
    text:String,
    onClick:() -> Unit
){
    Box(modifier = Modifier.clickable {
        onClick()
    }) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.padding(10.dp))
            Spacer(modifier = Modifier.width(19.dp))
            Text(text = text)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreViewAbout(){
    SingleIconButton(Icons.Default.AccessAlarm, "当前版本：0.0.1"){}
}