package com.liflymark.normalschedule.logic.utils

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.model.Structure
import kotlinx.coroutines.launch
import java.util.*

class test:AndroidViewModel(NormalScheduleApplication()){

}

object Dialog {
    fun getContractDialog(
        _context: Context,
        yes:() -> Unit = {},
        no:() -> Unit = {},
    ): MaterialDialog {
        val dialog = MaterialDialog(_context)
                .title(text = "APP隐私政策")
                .message(text = "发布时间：2021年8月21日\n更新时间：2021年9月18日\n本应用尊重并保护所有使用服务用户的个人隐私权。为了给您提供更准确、更有个性化的服务，本应用会按照本隐私权政策的规定使用和披露您的个人信息。但本应用将以高度的勤勉、审慎义务对待这些信息。除本隐私权政策另有规定外，在未征得您事先许可的情况下，本应用不会将这些信息对外披露或向第三方提供。本应用会不时更新本隐私权政策。 您在同意本应用服务使用协议之时，即视为您已经同意本隐私权政策全部内容。本隐私权政策属于本应用服务使用协议不可分割的一部分。 " +
                        "确保用户充分理解本协议中各条款，请审慎阅读并选择接受或不接受本协议。" +
                        "同意并点击确认本协议条款，才能成为本APP用户，并享受各类服务。登录、使用等行为将视为对本协议的接受，并同意接受本协议各项条款的约束。" +
                        "若不同意本协议，或对本协议中的条款存在任何疑问，请立即停止使用该程序，并可以选择不使用APP服务。\n\n" +
                        "1. 适用范围\n" +
                        "(a) 在使用应用时，您提交的账号、密码、班级、课程信息、成绩信息。\n" +
                        "(b) 在您使用本应用网络服务，或访问本应用平台网页时，本应用自动接收并记录的您的浏览器和计算机上的信息，包括但不限于您的IP地址、浏览器的类型、使用的语言、访问日期和时间、软硬件特征信息及您需求的网页记录等数据； \n" +
                        "2.在您使用登陆导入课表或其他需要登陆情况时，会将账号密码提交至服务器，服务器不会记录你的除" +
                        "学号以外的任何信息（如密码、课程表内容、姓名、专业），记录学号仅用于统计本应用用户数，开发者无从得知学号对应的密码、" +
                        "姓名等其他任何信息。虽然网络通信已加密，但仍然建议用户不要使用未知安全性的网络。\n" +
                        "2. 信息存储和交换 \n" +
                        "本应用会在本地，加密存储您的密码，且其他应用无法通过常规手段访问。网络端不会记录您的密码。虽然有上述安全措施，仍然建议用户不要安装未知安全性的APP。\n" +
                        "\n" +
                        "\n" +
                        "3.账号注销\n"+
                        "(a)若需注销账号请将学号和一卡通学号面（除学号外均可打码）邮件至1289142675@qq.com,开发者将在48小时之内处理，注销后的账号无法在此APP登陆。\n"+
                        "4.本隐私政策的更改 \n" +
                        "(a)如果决定更改隐私政策，我们会在本政策中、网站中以及我们认为适当的位置发布这些更改，以便您了解我们如何收集、使用您的个人信息，哪些人可以访问这些信息，以及在什么情况下我们会透露这些信息。 \n" +
                        "(b)本人保留随时修改本政策的权利，因此请经常查看。\n\n" +
                        "\nAPP运营者信息：李飞； 办公地址：河北省沧州市献县商业局小区；用户隐私信息保护负责人电话：15511777580\n" +
                        "开发者名称：考试不挂科（献县）科技发展技术服务中心\n" +
                                "著作权归APP运营者所有。")
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

    fun getClassDetailDialog(_context: Context, courseBean: CourseBean): MaterialDialog {
        val dialog = MaterialDialog(_context)
                .customView(R.layout.item_course_detail)

        val customView = dialog.getCustomView()
        val courseName = customView.findViewById<AppCompatTextView>(R.id.tv_item)
        val courseTime = customView.findViewById<AppCompatTextView>(R.id.et_time)
        val weekNum = customView.findViewById<AppCompatTextView>(R.id.et_weeks)
        val courseTeacher = customView.findViewById<AppCompatTextView>(R.id.et_teacher)
        val courseRoom = customView.findViewById<AppCompatTextView>(R.id.et_room)

        val oneList = courseBean.classWeek.whichIs1()
        val courseStartToEnd =
            "    第${courseBean.classSessions} - ${courseBean.classSessions+courseBean.continuingSession-1}节"


        courseName.text= courseBean.courseName
        courseTime.text = getWeekNumFormat(oneList)
        weekNum.text = when(courseBean.classDay){
            1 -> "周一$courseStartToEnd"
            2 -> "周二$courseStartToEnd"
            3 -> "周三$courseStartToEnd"
            4 -> "周四$courseStartToEnd"
            5 -> "周五$courseStartToEnd"
            6 -> "周六$courseStartToEnd"
            7 -> "周日$courseStartToEnd"
            else -> "错误"
        }
        courseTeacher.text = courseBean.teacher
        courseRoom.text = courseBean.teachingBuildName

        val closeButton = customView.findViewById<AppCompatTextView>(R.id.ib_delete)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    fun getSelectClassTime(_context: Context): MaterialDialog{
        val dialog = MaterialDialog(_context).customView(R.layout.dialog_select_class_time)
            .title(text = "输入周数")
        dialog.apply {
            noAutoDismiss()
            cancelable(false)
            cancelOnTouchOutside(true)
        }
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

    fun getSelectDepartmentAndClass(_context: Context, departmentAndMajorList: List<Structure>): MaterialDialog {
        val dialog = MaterialDialog(_context)
                .customView(R.layout.dialog_select_class)
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
            DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .width(300.dp)
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
                ,
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
fun SelectSessionContent(initialWeek:Int, initialStart:Int, initialEnd:Int,
                         result:(week:Int, start:Int, end:Int)->Unit,
){
    val scope = rememberCoroutineScope()
    val weekList = listOf("周一","周二","周三","周四","周五","周六","周日",)
    val startSection = mutableListOf<String>()
    val endSection = mutableListOf<String>()
    for (i in 1..11){
        startSection.add("第 $i 节")
        endSection.add("第 $i 节")
    }

    val weekListPager = rememberPagerState(
        pageCount = weekList.size,
        initialPage = initialWeek,
        initialOffscreenLimit = weekList.size,
        infiniteLoop = false
    )
    val startPager = rememberPagerState(
        pageCount = startSection.size,
        initialPage = initialStart,
        initialOffscreenLimit = startSection.size,
        infiniteLoop = false
    )
    val endPager = rememberPagerState(
        pageCount = endSection.size,
        initialPage = initialEnd,
        initialOffscreenLimit = endSection.size,
        infiniteLoop = false
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
                modifier = Modifier.fillMaxHeight()
            )
            Spacer(modifier = Modifier.width(15.dp))
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
            Spacer(modifier = Modifier.width(15.dp))
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
                    .background(Color.White)
                ,
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


    val yearListPager = rememberPagerState(
        pageCount = yearList.size,
        initialPage = year,
        initialOffscreenLimit = yearList.size,
        infiniteLoop = false
    )
    val monthPager = rememberPagerState(
        pageCount = monthStrList.size,
        initialPage = initialMonth,
        initialOffscreenLimit = monthList.size,
        infiniteLoop = false
    )
    val dayPager = rememberPagerState(
        pageCount = dayList.value.size,
        initialPage = initialDay,
        initialOffscreenLimit = dayList.value.size,
        infiniteLoop = false
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
                strList = yearList,
                pagerState = yearListPager,
                pageChange = {
                    Log.d("Dialog", it.toString())
                    result(yearListPager.currentPage, monthPager.currentPage, dayPager.currentPage)
                },
                modifier = Modifier.fillMaxHeight()
            )
            Spacer(modifier = Modifier.width(15.dp))
            StringPicker(
                strList = monthStrList,
                pagerState = monthPager,
                pageChange = {
                    Log.d("Dialog", it.toString())
                    val year_ =yearList[yearListPager.currentPage].toInt()
                    val month_ = monthList[monthPager.currentPage].toInt()-1
                    scope.launch {
                        dayList.value = GetDataUtil.getMonthAllDay(year_,month_)
                    }
                    result(yearListPager.currentPage, monthPager.currentPage, dayPager.currentPage)
//                    result(weekListPager.currentPage, startPager.currentPage, endPager.currentPage)
                },
                modifier = Modifier.fillMaxHeight()
            )
//            TextButton(onClick = {
//                scope.launch {
//                    monthPager.scrollToPage(monthPager.currentPage+1)
//                }
//            }) {
//                Text(text = "+")
//            }
            Spacer(modifier = Modifier.width(15.dp))
            StringPicker(
                strList = dayList.value,
                pagerState = dayPager,
                pageChange = {
                    Log.d("Dialog", it.toString())
                    result(yearListPager.currentPage, monthPager.currentPage, dayPager.currentPage)
//                    result(weekListPager.currentPage, startPager.currentPage, endPager.currentPage)
                },
                modifier = Modifier.fillMaxHeight()
            )
//            TextButton(onClick = {
//                scope.launch {
//                    dayPager.scrollToPage(dayPager.currentPage+1)
//                }
//            }) {
//                Text(text = "+")
//            }
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
            modifier = Modifier.padding(15.dp),
            selected = selected,
            onClick = { onClick() }
        )
        Text(text = text, style = MaterialTheme.typography.body1)
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