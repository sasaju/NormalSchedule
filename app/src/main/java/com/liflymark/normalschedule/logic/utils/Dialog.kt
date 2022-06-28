package com.liflymark.normalschedule.logic.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.utils.text_field.BackgroundTransparentTextFiled
import kotlinx.coroutines.launch
import java.util.*

object Dialog {
    fun getContractDialog(
        _context: Context,
        yes:() -> Unit = {},
        no:() -> Unit = {},
    ): MaterialDialog {
        val dialog = MaterialDialog(_context)
                .title(text = "APP隐私政策")
                .message(text = "发布时间：2021年8月21日\n更新时间：2022年6月27日\n除本隐私权政策另有规定外，在未征得您事先许可的情况下，本应用不会在未经您允许的情况下收集您的隐私信息。本应用会不时更新本隐私权政策。 您在同意本应用服务使用协议之时，即视为您已经同意本隐私权政策全部内容。" +
                        "\n\n" +
                        "1. 适用范围\n" +
                        "(a) 在使用应用时，您提交的账号、密码、班级、课程信息、成绩信息。\n" +
                        "(b) 在您使用本应用网络服务，或访问本应用平台网页时，本应用自动接收并记录的您的浏览器和计算机上的信息，包括但不限于您的IP地址、浏览器的类型、使用的语言、访问日期和时间、软硬件特征信息及您需求的网页记录等数据； \n\n" +
                        "2. 信息存储和交换 \n" +
                        "本应用会在本地，加密存储您的密码，且其他应用无法通过常规手段访问。网络端不会记录您的密码。虽然有上述安全措施，仍然建议用户不要安装未知安全性的APP。\n" +
                        "\n" +
                        "在您使用登陆导入课表或其他需要登陆情况时，会将账号密码提交至服务器，服务器不会记录你的除" +
                        "学号以外的任何信息（如密码、课程表内容、姓名、专业），记录学号仅用于统计本应用用户数，开发者无从得知学号对应的密码、" +
                        "姓名等其他任何信息。虽然网络通信已加密，但仍然建议用户不要使用未知安全性的网络。\n" +
                        "\n" +
                        "3.账号注销\n"+
                        "(a)若需注销账号请将学号和一卡通学号面（除学号外均可打码）邮件至1289142675@qq.com,开发者将在48小时之内处理，注销后的账号无法在此APP登陆。\n"+
                        " 4.敏感权限清单  \n" +
                        "(a)外部存储权限：用于在更换背景时读取设备中的图片\n" +
                        "(b)日历日程增加和删除：用于导入和更新考试安排至日历日程\n" +
                        "(c)联网权限：用于导入课表、查询成绩、查询空教室等基础\n"+
                        "5.本隐私政策的更改 \n" +
                        "(a)如果决定更改隐私政策，我们会在本政策中、网站中以及我们认为适当的位置发布这些更改，以便您了解我们如何收集、使用您的个人信息，哪些人可以访问这些信息，以及在什么情况下我们会透露这些信息。 \n" +
                        "(b)本人保留随时修改本政策的权利，因此请经常查看。\n\n" +
                        "6.APP运营方信息\n"+
                        "运营者姓名：李飞；\n 邮箱：1289142675@qq.com\n办公地址：河北省沧州市献县商业局小区；\n\n用户隐私信息保护负责人电话：15511777580\n开发者名称：考试不挂科（献县）科技发展技术服务中心"+
                                "\n著作权归APP运营者所有。")
                .positiveButton(text = "已阅读并同意"){ yes() }
                .negativeButton(text = "拒绝并停止使用"){ no() }
        return dialog
    }

    fun getUerContract(
        _context: Context,
        yes:() -> Unit = {},
        no:() -> Unit = {},
    ): MaterialDialog{
        val dialog = MaterialDialog(_context)
            .title(text = "用户协议")
            .message(text =
                    "(a)本应用在导入课表时会模拟您登陆教务系统的操作，如果您错误次数过多造成无法登陆开发者概不负责。 \n" +
                    "(b)开发者会尽量保证您的课表、成绩等信息正确但不做出任何绝对正确的承诺，请在使用前仔细检查，如有必要请自行登陆相关网站进行核实。因为信息不准确造成的不良后果（如挂科、旷课）开发者概不负责。\n" +
                    "(c)开发者保留随时修改本用户协议的权利，因此请经常查看。"
            )
            .positiveButton(text = "已阅读并同意"){ yes() }
            .negativeButton(text = "拒绝并停止使用"){ no() }
        return dialog
    }

    fun getImportAgain(_context: Context): MaterialDialog{
        val dialog = MaterialDialog(_context)
                .title(text ="请谨慎使用此功能")
                .message(text = "即将进入重新导入界面，需要将清除当前课表")
                .positiveButton(text = "清空当前课表")
                .negativeButton(text = "取消")
        return dialog
    }


//    fun getSelectWeekAndSection(_context: Context) {
//        val allWeek = listOf("周一","周二","周三","周四","周五","周六","周日",)
//        val startSection = mutableListOf<String>()
//        val endSection = mutableListOf<String>()
//        for (i in 1..11){
//            startSection.add("第 $i 节")
//            endSection.add("第 $i 节")
//        }
//        val dialog = OptionsPickerBuilder(_context, OnOptionsSelectListener{ week, startSection_, endSection_, _ ->
//            if (_context is AddCourseActivity){
//                _context.setClassWeek(week, startSection_, endSection_)
//            }
//        })
//                .setTitleText("选择时间")
//                .setCyclic(true, false, false)
//                .isDialog(true)
//                .setSubmitColor(R.color.purple_500)
//                .setCancelColor(R.color.purple_500)
//                .setTitleBgColor(0xFF666666.toInt())
//                .build<Any>()
//        dialog.setNPicker(allWeek, startSection.toList(), endSection.toList())
//        dialog.show()
//    }

    fun getProgressDialog(_context: Context): MaterialDialog {
        val dialog = MaterialDialog(_context).customView(R.layout.dialog_progress)
        dialog.apply {
            cancelable(true)
            cancelOnTouchOutside(false)
        }
        return dialog
    }
    fun getProgressDialog(_context: Context, text:String): MaterialDialog {
        val dialog = MaterialDialog(_context).customView(R.layout.dialog_progress)
        val textView = dialog.getCustomView().findViewById<TextView>(R.id.progress_text)
        textView.text = text
        dialog.apply {
            cancelable(true)
            cancelOnTouchOutside(false)
        }
        return dialog
    }

    fun String.whichIs1(): List<Int>{
        val oneList = mutableListOf<Int>()
        for (i in this.indices){
            when(this[i].toString()){
                "1" -> oneList.add(i + 1)
            }
        }
        if(oneList.size==0){
            oneList.add(0)
        }
        return oneList
    }

    fun getWeekNumFormat(oneList: List<Int>): String {
        var courseTimeFormat = "第"
        val type: Int
        if (oneList.last()-oneList.first() == oneList.size-1){
            type = 0
        } else {
//            for (i in oneList){
//                if (i % 2 == 1){
//                    type = 1
//                } else if (type == 1){
//                    type = 2
//                }
//            }
            val singleSize = oneList.filter { it%2 == 0 }.size
            val doubleSize = oneList.filter { (it+1)%2 == 0 }.size
            type = if (singleSize==oneList.size || doubleSize==oneList.size){
                1
            } else {
                2
            }
        }

        when (type){
            0 -> {
                courseTimeFormat += " ${oneList.first()} - ${oneList.last()} 周"
            }
            1 -> {
                courseTimeFormat += if (oneList.first() % 2 == 1) {
                    "${oneList.first()} - ${oneList.last()} 单周"
                } else {
                    "${oneList.first()} - ${oneList.last()} 双周"
                }
            }
            2 -> {
                for (i in oneList) {
                    courseTimeFormat += "$i,"
                }
                courseTimeFormat += "周"
            }
        }
        return courseTimeFormat
    }


    @SuppressLint("CheckResult")
    fun getColorPickerDialog(
        context: Context,
        colors: IntArray,
        onResult:(dialog:MaterialDialog, color:Int) -> Unit,
    ):MaterialDialog {
        return MaterialDialog(context).apply {
            title(text="选择颜色")
            colorChooser(colors) { dialog, color ->
                onResult(dialog, color)
            }
            positiveButton(text="确定")
        }
    }
}
@Composable
fun SelectWeekDialog(showDialog: MutableState<Boolean>,maxWeekNum:Int = 19, initialR:String, block:(String)->Unit){
    val result = remember {
        mutableStateOf(initialR)
    }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(text = "选择周数")
            },
            text = {
                SelectContent(maxWeekNum = maxWeekNum,initialR = initialR){
                    result.value = it
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    block(result.value)
                }) {
                    Text(text = "确定")
                }
            }
        )
    }
}

@Composable
fun SelectContent(
    maxWeekNum: Int = 19,  //原则上不得大于24
    initialR: String = "0000000000000000000000000",
    resultR:(res:String)->Unit
){
    val initialList = initialR.split("").toMutableList()
    val selectList = remember { mutableStateListOf<String>() }
    initialList.removeFirst()
    initialList.removeLast()
    selectList.addAll(initialList)
    Log.d("Dialog","清空一次$initialList")
    Column {
        FlowRow(modifier = Modifier.fillMaxWidth()) {
            for (i in 1..maxWeekNum){
                if (selectList[i-1] == "0"){
                    key(i){
                        IntBox(count = i, false){
                            if (it[i] == true){
                                selectList[i-1] = "1"
                            }else{
                                selectList[i-1] = "0"
                            }
                            resultR(selectList.joinToString(""))
                        }
                    }
                } else {
                    key(i) {
                        IntBox(count = i, true) {
                            if (it[i] == true) {
                                selectList[i - 1] = "1"
                            } else {
                                selectList[i - 1] = "0"
                            }
                            resultR(selectList.joinToString(""))
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row{
            TextButton(onClick = {
                for (t in 0 until selectList.size){
                    if (t % 2 == 0){
                        selectList[t] = "1"
                    } else {
                        selectList[t] = "0"
                    }
                }
            }) {
                Text(text = "单周")
            }
            TextButton(onClick = {
                for (t in 0 until selectList.size){
                    if (t % 2 == 0){
                        selectList[t] = "0"
                    } else {
                        selectList[t] = "1"
                    }
                }
            }) {
                Text(text = "双周")
            }
            TextButton(onClick = {
                for (t in 0 until selectList.size){
                    selectList[t] = "1"
                }
            }) {
                Text(text = "全选")
            }
            TextButton(onClick = {
                for (t in 0 until selectList.size){
                    selectList[t] = "0"
                }
            }) {
                Text(text = "全不选")
            }
        }
    }
    LaunchedEffect(selectList){
        Log.d("DialogListChange",selectList.joinToString(""))

    }
}

@Composable
fun IntBox(count:Int, selectedOr: Boolean=false, selectChanged:(select:Map<Int, Boolean>)->Unit = {}){
    var selected by remember {
        mutableStateOf(selectedOr)
    }
    val backgroundColor = if(selected){Color(0xff2196f3)}else{Color.Transparent}
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(100),
        modifier = Modifier.padding(2.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    selected = !selected
                }
        ) {
            Text(text = "$count", color = contentColorFor(backgroundColor = backgroundColor))
        }
    }
    LaunchedEffect(selected){
        selectChanged(mapOf(count to selected))
    }
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun SelectSessionDialog(
    showDialog: MutableState<Boolean>,
    initialWeek:Int=0,
    initialStart:Int=0,
    initialEnd:Int=0,
    result:(week:Int, start:Int, end:Int)->Unit
){
    var week1:Int = initialWeek
    var start1 = initialStart
    var end1 = initialEnd
    if (showDialog.value) {
        Dialog(
            onDismissRequest = { showDialog.value = false },
            DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colors.background
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .width(300.dp)
                        .verticalScroll(rememberScrollState()),
                ){
                    Text(text = "  \n   选择周数 \n", fontSize = 19.sp)
                    SelectSessionContent(initialWeek, initialStart, initialEnd){week,start,end ->
                        week1 = week
                        start1 = start
                        end1 = end
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(0.98f),horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = {
                            result(week1, start1, end1)
                            showDialog.value = false
                        }) { Text(text = "确定")}
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun SelectSessionContent2(
    initialWeek: Int, initialStart: Int, initialEnd: Int,
    result: (week: Int, start: Int, end: Int) -> Unit,
){
    var selectWeek by rememberSaveable { mutableStateOf(initialWeek) }
    var selectStart by rememberSaveable { mutableStateOf(initialStart) }
    var selectEnd by rememberSaveable { mutableStateOf(initialEnd) }
    val weekList = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    val startSection = mutableListOf<String>()
    val endSection = mutableListOf<String>()
    for (i in 1..11){
        startSection.add("第 $i 节")
        endSection.add("第 $i 节")
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        StringPicker2(
            strList = weekList,
            value = selectWeek,
            onValueChange = {
                selectWeek = it
                result(selectWeek, selectStart, selectEnd)
            }
        )
        Spacer(modifier = Modifier.width(10.dp))
        StringPicker2(
            strList = startSection,
            value = selectStart,
            onValueChange = {
                selectStart = it
                if (selectEnd<it)
                    selectEnd = it
                result(selectWeek, selectStart, selectEnd)
            }
        )
        Spacer(modifier = Modifier.width(10.dp))
        StringPicker2(
            strList = endSection,
            value = selectEnd,
            onValueChange = {
                selectEnd = it
                if (selectStart > it)
                    selectStart = it
                result(selectWeek, selectStart, selectEnd)
            }
        )
    }
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun SelectSessionContent(
    initialWeek: Int, initialStart: Int, initialEnd: Int,
    result: (week: Int, start: Int, end: Int) -> Unit,
){
    val scope = rememberCoroutineScope()
    val weekList = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    val startSection = mutableListOf<String>()
    val endSection = mutableListOf<String>()
    for (i in 1..11){
        startSection.add("第 $i 节")
        endSection.add("第 $i 节")
    }

    val weekListPager = rememberPagerState(
//        pageCount = weekList.size,
        initialPage = initialWeek,
//        initialOffscreenLimit = weekList.size,
//        infiniteLoop = false
    )
    val startPager = rememberPagerState(
//        pageCount = startSection.size,
        initialPage = initialStart,
//        initialOffscreenLimit = startSection.size,
//        infiniteLoop = false
    )
    val endPager = rememberPagerState(
//        pageCount = endSection.size,
        initialPage = initialEnd,
//        initialOffscreenLimit = endSection.size,
//        infiniteLoop = false
    )
    Box(
        modifier = Modifier
            .height(200.dp)
            .width(300.dp),
        contentAlignment = Alignment.Center,
        propagateMinConstraints = true
    ){
        Row(modifier = Modifier
            .height(200.dp)
            .width(300.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            StringPicker(
                strList = weekList,
                pagerState = weekListPager,
                pageChange = {
                    Log.d("Dialog", it.toString())
                    result(weekListPager.currentPage, startPager.currentPage, endPager.currentPage)
                },
            )
            Spacer(modifier = Modifier.width(20.dp))
            StringPicker(
                strList = startSection,
                pagerState = startPager,
                pageChange = {
                    Log.d("Dialog", it.toString())
                    scope.launch {
                        if (endPager.currentPage < it){
                            endPager.animateScrollToPage(it)
                        }
                    }
                    result(weekListPager.currentPage, startPager.currentPage, endPager.currentPage)
                },
                modifier = Modifier.fillMaxHeight()
            )
            Spacer(modifier = Modifier.width(20.dp))
            StringPicker(
                strList = endSection,
                pagerState = endPager,
                pageChange = {
                    Log.d("Dialog", it.toString())
                    scope.launch {
                        if (startPager.currentPage > it){
                            startPager.animateScrollToPage(it)
                        }
                    }
                    result(weekListPager.currentPage, startPager.currentPage, endPager.currentPage)
                },
                modifier = Modifier.fillMaxHeight()
            )
        }
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(2.dp)
                    .background(Color.Gray))
                Spacer(modifier = Modifier
                    .height(40.dp)
                    .width(0.dp))
                Spacer(modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(2.dp)
                    .background(Color.Gray))
            }
        }
    }
}

@Composable
fun EditDialog(showDialog: MutableState<Boolean>, initialString:String, resultR: (res: String) -> Unit){
    var editString by remember {
        mutableStateOf(initialString)
    }
    if (showDialog.value){
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {},
            text = {
                   TextField(value = editString , onValueChange ={
                       editString = it
                   } )
            },
            dismissButton = {
                TextButton(onClick = {
                    resultR(editString)
                    showDialog.value = false
                }) {
                    Text(text = "确定")
                }
            },
            confirmButton = {}
        )
    }
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun SelectDateDialog(
    showDialog: MutableState<Boolean>,
    initialYear:Int=0,
    initialMonth:Int=0,
    initialDay:Int=0,
    selectMillis:(millis:Long) -> Unit
){
    var year1 = initialYear
    var month1 = initialMonth
    var day1 = initialDay
    val yearList = listOf("2021","2022","2023")
    if (showDialog.value) {
        Dialog(
            onDismissRequest = { showDialog.value = false },
            DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .width(300.dp)
                    .verticalScroll(rememberScrollState())
                    .background(Color.White),
            ){
                Text(text = "  \n   选择日期 \n", fontSize = 19.sp)
                SelectDate(
                    year = year1,
                    initialMonth = month1,
                    initialDay =day1,
                    result = {year, month, day ->
                        year1 = year
                        month1 = month
                        day1 = day
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(0.98f),horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        val calendar = GregorianCalendar().apply {
                            set(Calendar.YEAR, yearList[year1].toInt())
                            set(Calendar.MONTH, month1)
                            set(Calendar.DATE, day1+1)
                        }
                        selectMillis(calendar.timeInMillis)
                        showDialog.value = false
                    }) { Text(text = "确定")}
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun SelectDate(
    year:Int,
    initialMonth:Int,
    initialDay:Int,
    result: (year: Int, month: Int, day: Int) -> Unit
){
    val scope = rememberCoroutineScope()
    val yearList = listOf("2021","2022","2023")
    val monthList = (1..12).toList().map { it.toString() }
    val monthStrList = monthList.map {
        "${it}月"
    }
    val dayList =  remember {
        mutableStateOf(GetDataUtil.getMonthAllDay(
            year = yearList[year].toInt(),
            month = monthList[initialMonth].toInt()
        ))
    }
    Log.d("Dialog","重组一次")

    var yearIndex by rememberSaveable { mutableStateOf(year) }
    var monthIndex by rememberSaveable { mutableStateOf(initialMonth) }
    var dayIndex by rememberSaveable { mutableStateOf(initialDay) }
    Box(
        modifier = Modifier
            .height(200.dp)
            .width(300.dp),
        contentAlignment = Alignment.Center,
        propagateMinConstraints = true
    ){
        Row(modifier = Modifier
            .height(200.dp)
            .width(300.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            StringPicker2(
                strList = yearList,
                value = year,
                onValueChange = {
                    Log.d("Dialog", it.toString())
                    yearIndex = it
                    result(yearIndex, monthIndex, dayIndex)
                },
//                modifier = Modifier.fillMaxHeight()
            )
            Spacer(modifier = Modifier.width(15.dp))
            StringPicker2(
                strList = monthStrList,
                value = initialMonth,
                onValueChange = {
                    Log.d("Dialog", it.toString())
                    monthIndex = it
                    val year_ =yearList[yearIndex].toInt()
                    val month_ = monthList[monthIndex].toInt()-1
                    scope.launch {
                        dayList.value = GetDataUtil.getMonthAllDay(year_,month_)
                    }
                    result(yearIndex, monthIndex, dayIndex)
//                    result(weekListPager.currentPage, startPager.currentPage, endPager.currentPage)
                },
            )
            Spacer(modifier = Modifier.width(15.dp))
            StringPicker2(
                strList = dayList.value,
                value = initialDay,
                onValueChange = {
                    Log.d("Dialog", it.toString())
                    dayIndex = it
                    result(yearIndex, monthIndex, dayIndex)
                },
            )
        }
//        Box(modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ){
//            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//                Spacer(modifier = Modifier
//                    .fillMaxWidth(0.9f)
//                    .height(2.dp)
//                    .background(Color.Gray))
//                Spacer(modifier = Modifier
//                    .height(40.dp)
//                    .width(0.dp))
//                Spacer(modifier = Modifier
//                    .fillMaxWidth(0.9f)
//                    .height(2.dp)
//                    .background(Color.Gray))
//            }
//        }
    }
}


/**
 * 单选弹窗
 */
@Composable
fun SingleSelectDialog(
    showDialog: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    selectNumInit:Int,
    selectList:List<String>,
    result:(res:String) -> Unit
){
    var selectNum by remember {
        mutableStateOf(selectNumInit)
    }
    if (showDialog.value) {
        AlertDialog(
            title = { Text(text = "请选择") },
            modifier = modifier,
            onDismissRequest = { showDialog.value = false },

            confirmButton = {
                    TextButton(onClick = {
                        result(selectList[selectNum])
                        showDialog.value = false
                    }) {
                        Text(text = "确定")
                    }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    repeat(selectList.size) {
                        key(it) {
                            RadioTextButton(
                                selected = selectNum == it,
                                text = selectList[it],
                                onClick = { selectNum = it })
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun RadioTextButton(
    selected:Boolean,
    text:String,
    onClick: () -> Unit
){
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
//            modifier = Modifier.padding(15.dp),
            selected = selected,
            onClick = { onClick() }
        )
        Text(text = text, style = MaterialTheme.typography.body1)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextFieldDialog(
    value:String,
    placeHolder:String,
    onDismissRequest:()->Unit,
    onClick: (res:String) -> Unit,
    properties:DialogProperties = DialogProperties(dismissOnClickOutside = false),
    shapes: CornerBasedShape = MaterialTheme.shapes.medium,
){
    var text by rememberSaveable{ mutableStateOf(value) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit){
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    CornerDialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
        shapes = shapes,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(5.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            BackgroundTransparentTextFiled(
                value = text,
                onValueChange = { text = it },
                placeHolder = placeHolder,
                modifier = Modifier.focusRequester(focusRequester)
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(
                onClick = {
                    onClick(text)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "确定")
            }
        }
    }
}

@Composable
fun CornerDialog(
    onDismissRequest:()->Unit,
    properties:DialogProperties = DialogProperties(),
    shapes: CornerBasedShape = MaterialTheme.shapes.medium,
    content:@Composable () -> Unit
){
    Dialog(
        onDismissRequest = {
            onDismissRequest()
        },
        properties = properties
    ) {
        Surface(
            shape = shapes
        ) {
            content()
        }
    }
}




@ExperimentalMaterialApi
@ExperimentalPagerApi
@Preview(showBackground = true)
@Composable
fun DialogPreView(){
//    SelectSessionContent(0,0,0){
//            _,_,_ ->
//    }
    SelectDate(0,0,0){
        _,_,_->
    }
}