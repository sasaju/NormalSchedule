package com.liflymark.normalschedule.logic.utils

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.protobuf.LazyStringList
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.bean.getInitial
import com.liflymark.normalschedule.logic.model.AllCourse
import com.liflymark.normalschedule.logic.model.Arrange
import com.liflymark.normalschedule.logic.model.Grade
import com.liflymark.normalschedule.logic.model.Grades
import com.liflymark.schedule.data.Settings

internal object Convert {
    val String.color
            get() = Color(android.graphics.Color.parseColor(this))
    fun courseResponseToBean(courseResponse: AllCourse): CourseBean {
        val color = stringToColor(courseResponse.courseName)
        return CourseBean(
            courseResponse.campusName,
            courseResponse.classDay,
            courseResponse.classSessions,
            courseResponse.classWeek,
            courseResponse.continuingSession,
            courseResponse.courseName,
            courseResponse.teacher,
            courseResponse.teachingBuildName,
            color,
            courseNumber = courseResponse.coureNumber,
            colorIndex = courseNameToIndex(courseResponse.courseName, colorListLength = 13)
        )
    }

    fun courseBeanToOneByOne2(courseBeanList: List<CourseBean>, colorList:List<List<String>>): List<List<OneByOneCourseBean>> {
        val allWeekList = mutableListOf<MutableList<OneByOneCourseBean>>()
        val maxWeek = courseBeanList.getOrElse(0) { getInitial()[0] }.classWeek.length
        repeat(maxWeek) {
            val singleWeekList = mutableListOf<OneByOneCourseBean>()
            allWeekList.add(singleWeekList)
        }
        for (i in 0 until maxWeek) {
            for (courseBean in courseBeanList) {
                val name =
                    courseBean.courseName + "\n" + courseBean.teachingBuildName + "\n" + courseBean.teacher
                when (courseBean.classWeek[i].toString()) {
                    "1" -> {
                        // index<0为兼容老版本APP， removed代表是否无视主题色强行应用用户自己设置的颜色，remove目前只能配置纯色颜色
                        val twoColorList =
                            when {
                                courseBean.colorIndex < 0 -> {
                                    listOf(
                                        courseBean.color.color,
                                        stringToBrush(courseBean.color.color.value, 1).getOrNull(1)?:courseBean.color.color,
                                        stringToBrush(courseBean.color.color.value, 1).getOrNull(2)?:courseBean.color.color
                                    )
                                }
                                courseBean.removed -> {
                                    listOf(
                                        courseBean.color.color,
                                        (colorList[courseBean.colorIndex].getOrNull(1)?:courseBean.color).color,
                                        (colorList[courseBean.colorIndex].getOrNull(2)?:courseBean.color).color,
                                    )
                                }
                                else -> {
                                    listOf(
                                        colorList[courseBean.colorIndex][0].color,
                                        (colorList[courseBean.colorIndex].getOrNull(1)?:courseBean.color).color,
                                        (colorList[courseBean.colorIndex].getOrNull(2)?:courseBean.color).color,
                                    )
                                }
                            }
                        val a = OneByOneCourseBean(
                            courseName = name,
                            start = courseBean.classSessions,
                            end = courseBean.classSessions + courseBean.continuingSession - 1,
                            whichColumn = courseBean.classDay,
                            color = courseBean.color.color,
                            twoColorList = twoColorList
                        )
                        allWeekList[i].add(a)
                    }
//                    "0" -> oneCourseList.add(OneByOneCourseBean("0",courseBean.classDay, 0, 0))
//                    else -> oneCourseList.add(OneByOneCourseBean("0",courseBean.classDay, 0, 0))
                }
            }
        }
        return allWeekList
    }

    fun allCourseToJson(allCourseList: List<AllCourse>): String =  Gson().toJson(allCourseList)

    fun allGradeToJson(allGradeList: List<Grade>): String {
        return Gson().toJson(allGradeList)
    }

    fun jsonToAllCourse(str: String): List<AllCourse> {
        lateinit var a: List<AllCourse>
        val listType = object : TypeToken<List<AllCourse>>() {}.type
        a = Gson().fromJson(str, listType)
        return a
    }

    fun jsonToAllGrade(str: String): List<Grade> {
        lateinit var a: List<Grade>
        val listType = object : TypeToken<List<Grade>>() {}.type
        a = Gson().fromJson(str, listType)
        return a
    }

    fun detailGradeToJson(detailGrade: List<Grades>): String{
        return Gson().toJson(detailGrade)
    }

    fun jsonToGradesList(str: String): List<Grades>{
        lateinit var a: List<Grades>
        val listType = object : TypeToken<List<Grades>>() {}.type
        a = Gson().fromJson(str, listType)
        return a
    }

    fun examArrangeToJson(examArrange: List<Arrange>):String{
        return Gson().toJson(examArrange)
    }

    fun jsonToExamArrange(str: String): List<Arrange>{
        lateinit var a: List<Arrange>
        val listType = object : TypeToken<List<Arrange>>() {}.type
        a = Gson().fromJson(str, listType)
        return a
    }

    fun string2Unicode(string: String): String {
        val unicode = StringBuffer()
        for (i in string.indices) {
            // 取出每一个字符
            val c = string[i]
            // 转换为unicode
            // unicode.append("\\u" + Integer.toHexString(c.toInt()))
            unicode.append(Integer.toHexString(c.toInt())[0])
            if (i > 2) {
                break
            }
        }
        return unicode.toString()
    }

    fun colorStringToLong(colorString: String): Long{
        var colorStr = colorString.replace("#", "")
        while (true){
            if (colorStr.length >= 8)
                break
            colorStr = "f$colorStr"
        }
        return colorStr.toLong(16)
    }

    fun colorStringToInt(colorString: String): Int{
        var colorStr = colorString.replace("#", "")
        while (true){
            if (colorStr.length >= 8)
                break
            colorStr = "f$colorStr"
        }
        return colorStr.toLong(16).toInt()
    }

    fun stringToColor(name: String): String {
        val colorList = arrayListOf<String>()
        colorList.apply {
            add("#12c2e9")
            add("#376B78")
            add("#f64f59")
            add("#CBA689")
            add("#ffffbb33")
            add("#8202F2")
            add("#F77CC2")
            add("#4b5cc4")
            add("#426666")
            add("#40de5a")
            add("#f0c239")
            add("#725e82")
            add("#c32136")
            add("#b35c44")
        }
        return try {
            val num = string2Unicode(name).toInt()
            colorList[num % colorList.count()]
        } catch (e: Exception) {
            colorList[0]
        }
    }

    fun courseNameToIndex(name: String, colorListLength:Int):Int{
        return try {
            val num = string2Unicode(name).toInt()
            Log.d("Convert", "$name:${num%colorListLength}, num:$num")
            num % colorListLength
        } catch (e: Exception) {
            0
        }
    }

    fun stringToBrush(color:ULong, mode:Int=1):List<Color>{
        val colorList = mutableListOf<Color>()
        if (mode == 0){
//            return Brush.linearGradient(listOf(Color(color), Color(color)))
            colorList.add(Color(color))
            colorList.add(Color(color))
        }else{
            when(color){
                0xff12c2e9.toColorULong() -> colorList.addAll(listOf(Color(0xFFFC354C), Color(0xFF0ABFBC)))
                0xff376B78.toColorULong() -> colorList.addAll(listOf(Color(0xFFC04848), Color(0xFF480048)))
                0xfff64f59.toColorULong() -> colorList.addAll(listOf(Color(0xff5f2c82), Color(0xff49a09d)))
                0xffCBA689.toColorULong() -> colorList.addAll(listOf(Color(0xFFDC2424), Color(0xDF5B69BE)))
                0xffffbb33.toColorULong() -> colorList.addAll(listOf(Color(0xff24C6DC), Color(0xff514A9D)))
                0xff8202F2.toColorULong() -> colorList.addAll(listOf(Color(0xffE55D87), Color(0xff5FC3E4)))
                0xffF77CC2.toColorULong() -> colorList.addAll(listOf(Color(0xff5C258D), Color(0xff4389A2)))
                0xff4b5cc4.toColorULong() -> colorList.addAll(listOf(Color(0xff134E5E), Color(0xff71B280)))
                0xff426666.toColorULong() -> colorList.addAll(listOf(Color(0xff085078), Color(0xff85D8CE)))
                0xff40de5a.toColorULong() -> colorList.addAll(listOf(Color(0xff4776E6), Color(0xff8E54E9)))
                0xfff0c239.toColorULong() -> colorList.addAll(listOf(Color(0xff1D2B64), Color(0xffF8CDDA)))
                0xff725e82.toColorULong() -> colorList.addAll(listOf(Color(0xA91A2980), Color(0xff26D0CE)))
                0xffc32136.toColorULong() -> colorList.addAll(listOf(Color(0xffAA076B), Color(0xE692088F)))
                0xffb35c44.toColorULong() -> colorList.addAll(listOf(Color(0xFFFFA8C3), Color(0xFFDCE083)
                ))
                Color.Transparent.value -> colorList.addAll(listOf(Color.Transparent, Color.Transparent))
                else -> colorList.addAll(listOf(Color(0xffE55D87), Color(0xff5FC3E4)))
            }
        }
        return colorList.toList()
    }


    fun Long.toColorULong() = (this.toULong() and 0xffffffffUL) shl 32

    fun onUpdateMIUIWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ){
        appWidgetIds.forEach { appWidgetId ->
//            val pendingIntent: PendingIntent = Intent(context, ExampleActivity::class.java)
//                .let { intent ->
//                    PendingIntent.getActivity(context, 0, intent, 0)
//                }

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.miui_appwidget_day
            )
//            Thread.sleep(10000)
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            pendingIntent.let {
                views.setOnClickPendingIntent(R.id.course_1, it)
                views.setOnClickPendingIntent(R.id.course_2, it)
                views.setOnClickPendingIntent(R.id.no_class, it)
            }

            val intentSync = Intent(context, this::class.java)
            intentSync.action = "miui.appwidget.action.APPWIDGET_UPDATE"

            val pendingSync = PendingIntent.getBroadcast(
                context,
                0,
                intentSync,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            ) //You need to specify a proper flag for the intent. Or else the intent will become deleted.

            views.setOnClickPendingIntent(R.id.week_num, pendingSync)
            // get today course
            val mList = mutableListOf<OneByOneCourseBean>()
            val allCourse = Repository.loadAllCourse3()
            val nowWeekNum = GetDataUtil.whichWeekNow() + 1
            val nowDayNum = GetDataUtil.getNowWeekNum()
            allCourse?.get(nowWeekNum - 1)?.let {
                mList.addAll(it.filter { it1 ->
                    it1.whichColumn == nowDayNum && !GetDataUtil.hadOvered(it1.end)
                })
            }
            Log.d("miuiPro", mList.toString())
            views.setTextViewText(R.id.week_now, GetDataUtil.getDayOfWeek())
            views.setTextViewText(R.id.week_num, "第${nowWeekNum}周")

            mList.sortBy { it.start }
            // set when have one course, two course, zero course or more course
            when (mList.size) {
                0 -> {
                    views.setViewVisibility(R.id.course_1, View.GONE)
                    views.setViewVisibility(R.id.course_2, View.GONE)
                    views.setViewVisibility(R.id.no_class, View.VISIBLE)
                }
                1 -> {
                    views.setViewVisibility(R.id.course_1, View.VISIBLE)
                    views.setViewVisibility(R.id.course_2, View.GONE)
                    views.setViewVisibility(R.id.no_class, View.GONE)
                    val courseInfo1 = mList[0]
                    val courseNameBuild = courseInfo1.courseName.split("\n")
                    val courseName = courseNameBuild.getOrNull(0) ?: ""
                    val courseBuild = courseNameBuild.getOrNull(1) ?: ""
                    val (nowType, minusRes) = GetDataUtil.hadStartedOrOver(
                        courseInfo1.start,
                        courseInfo1.end
                    )
                    val courseTimeStr = "距离${nowType}${minusRes}分钟"
                    views.setTextViewText(R.id.app_course_name_1, courseName)
                    views.setTextViewText(R.id.app_course_teacher_1, courseBuild)
                    views.setTextViewText(R.id.app_course_time_1, courseTimeStr)
                    views.setViewVisibility(R.id.course_2, View.INVISIBLE)
                }
                else -> {
                    val courseInfo1 = mList[0]
                    val courseNameBuild = courseInfo1.courseName.split("\n")
                    val courseName = courseNameBuild.getOrNull(0) ?: ""
                    val courseBuild = courseNameBuild.getOrNull(1) ?: ""
                    val (nowType, minusRes) = GetDataUtil.hadStartedOrOver(
                        courseInfo1.start,
                        courseInfo1.end
                    )
                    val courseTimeStr = "距离${nowType}${minusRes}分钟"
                    views.setTextViewText(R.id.app_course_name_1, courseName)
                    views.setTextViewText(R.id.app_course_teacher_1, courseBuild)
                    views.setTextViewText(R.id.app_course_time_1, courseTimeStr)

                    val courseInfo2 = mList[1]
                    val courseNameBuild2 = courseInfo2.courseName.split("\n")
                    val courseName2 = courseNameBuild2.getOrNull(0) ?: ""
                    val courseBuild2 = courseNameBuild2.getOrNull(1) ?: ""
                    val courseTimeStr2 = "${GetDataUtil.getStartTime(courseInfo2.start)} - " +
                            GetDataUtil.getEndTime(courseInfo2.end)
                    views.setViewVisibility(R.id.no_class, View.GONE)
                    views.setViewVisibility(R.id.course_1, View.VISIBLE)
                    views.setViewVisibility(R.id.course_2, View.VISIBLE)
                    views.setTextViewText(R.id.app_course_name_2, courseName2)
                    views.setTextViewText(R.id.app_course_teacher_2, courseBuild2)
                    views.setTextViewText(R.id.app_course_time_2, courseTimeStr2)
                }
            }
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
//    fun getAllOneCourse(courseBeanList: List<CourseBean>): List<List<OneByOneCourseBean>> {
//        val allOneCourseList = mutableListOf<List<OneByOneCourseBean>>()
//        val needAllOneByOneCourseList = mutableListOf<List<List<OneByOneCourseBean>>>()
//        for (i in courseBeanList) {
//            allOneCourseList.add(courseBeanToOneByOne(i))
//        }
//
//        for (t in 0..19){
//            val aOneCourseList = mutableListOf<OneByOneCourseBean>()
//            for(a in allOneCourseList) {
//                if (a[t].courseName!="0")
//                    aOneCourseList.add(a[t])
//            }
//            needAllOneByOneCourseList.add(aOneCourseList.toList())
//        }
//
//
//
//        return allOneCourseList
//    }


