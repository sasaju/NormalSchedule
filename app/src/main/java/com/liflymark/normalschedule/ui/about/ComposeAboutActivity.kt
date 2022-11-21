package com.liflymark.normalschedule.ui.about

import android.R.attr.path
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.afollestad.materialdialogs.MaterialDialog
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.utils.Dialog
import com.liflymark.normalschedule.logic.utils.RomUtil
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import es.dmoral.toasty.Toasty


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
                        it
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
            Toasty.error(this,"当前手机未安装浏览器").show()
        }
    }

}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun AboutPage(){
    val context = LocalContext.current
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
        ,
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
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { dialog.show() }) {
                    Text("隐私政策")
                }
                Spacer(
                    modifier = Modifier
                        .width(10.dp)
                        .padding(horizontal = 4.25.dp, vertical = 8.dp)
                        .background(Color.Gray)
                        .height(15.dp)
                )
                TextButton(onClick = { userDialog.show() }) {
                    Text(text = "用户协议")
                }
            }
        }
    }
}

@Composable
fun Introduce(){
    val context = LocalContext.current
    val activity = LocalContext.current as ComposeAboutActivity
    val pm = context.packageManager
    val versionCode2 = PackageInfoCompat.getLongVersionCode(pm.getPackageInfo(context.packageName,0)).toInt()
    val versionName = pm.getPackageInfo(context.packageName, 0).versionName
    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        pm.getPackageInfo(context.packageName,0).longVersionCode.toInt()
    } else {
        //noinspection deprecation
        pm.getPackageInfo(context.packageName, 0).versionCode
    }
    val checkNewOrNot = rememberSaveable { mutableStateOf(false) }
//    var checkRes by remember {
//        mutableStateOf(
//            CheckUpdateResponse(
//                result = "正在查询",
//                status = "301",
//                force = null,
//                newUrl = null
//            )
//        )
//    }
    LaunchedEffect(checkNewOrNot.value){
        if (checkNewOrNot.value){
            Toasty.info(context, "正在查询新版本", Toasty.LENGTH_SHORT).show()
            val res = Repository.getNewVerison2(versionCode = versionCode2.toString())
            if (res.status == "200"){
                Toasty.success(context, res.result).show()
                val uri = Uri.parse(res.newUrl)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            }else{
                Toasty.success(context,res.result).show()
            }
        }
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        SingleIconButton(
            icon =Icons.Default.BubbleChart,
            text = "当前版本：$versionName"
        ) {
            checkNewOrNot.value = true
        }
        SingleIconButtonTo(
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
        SingleIconButtonTo(
            icon = Icons.Default.Star,
            text = "用户协议及隐私政策"
        ) {
            val url = if(RomUtil.isVivo){"https://liflymark.top/privacy2/"}else{"https://liflymark.top/privacy/"}
            activity.openBrowser(url)
        }
        SingleIconButtonTo(
            icon = Icons.Default.EmojiNature,
            text = "项目开源"
        ) {
            activity.openBrowser("https://github.com/sasaju/NormalSchedule")
        }
        SingleIconButton(
            icon = Icons.Default.Category,
            text = "关于开发组"
        ) {
            MaterialDialog(context)
                .title(text = "关于开发组")
                .message(text = "开发者：\n  河北大学 | 大四药物制剂在读@符号 \n  (QQ:1289142675) \nLOGO、背景图绘制：\n   河北大学 | 大四药学在读@Mr.")
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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.padding(10.dp))
            Spacer(modifier = Modifier.width(19.dp))
            Text(text = text)
        }
    }
}

@Composable
fun SingleIconButtonTo(
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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.padding(10.dp))
            Spacer(modifier = Modifier.width(19.dp))
            Text(text = text)
            Spacer(modifier = Modifier.width(5.dp))
            Icon(Icons.Default.CallMade, contentDescription = null, modifier = Modifier.size(15.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreViewAbout(){
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = {  }) {
            Text("隐私政策")
        }
        Text(text = " | ")
        TextButton(onClick = {  }) {
            Text(text = "用户协议")
        }
    }
}