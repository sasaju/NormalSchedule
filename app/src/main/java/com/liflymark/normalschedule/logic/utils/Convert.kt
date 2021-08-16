package com.liflymark.normalschedule.logic.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.bean.getInitial
import com.liflymark.normalschedule.logic.model.AllCourse
import com.liflymark.normalschedule.logic.model.Grade
import com.liflymark.normalschedule.logic.model.Grades
import com.liflymark.schedule.data.Settings
import java.lang.Exception

internal object Convert {
    lateinit var oneCourse: OneByOneCourseBean

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
            color
        )
    }

    fun courseBeanToOneByOne(courseBeanList: List<CourseBean>): List<List<OneByOneCourseBean>> {
//        Log.d("Convert0", courseBeanList.toString())
        val allOneCourseList = mutableListOf<List<OneByOneCourseBean>>()
        for (i in 0..20) {
            val oneCourseList = mutableListOf<OneByOneCourseBean>()
            for (courseBean in courseBeanList) {
                val name =
                    courseBean.courseName + "\n" + courseBean.teachingBuildName + "\n" + courseBean.teacher
                when (courseBean.classWeek[i].toString()) {
                    "1" -> {
                        val a = OneByOneCourseBean(
                            name,
                            courseBean.classSessions - 1,
                            courseBean.classSessions + courseBean.continuingSession - 1,
                            courseBean.classDay - 1,
                            Color(courseBean.color.toLong())
                        )
                        oneCourseList.add(a)
                    }
//                    "0" -> oneCourseList.add(OneByOneCourseBean("0",courseBean.classDay, 0, 0))
//                    else -> oneCourseList.add(OneByOneCourseBean("0",courseBean.classDay, 0, 0))
                }
            }
            allOneCourseList.add(oneCourseList)
        }
//        Log.d("Convert3", allOneCourseList.toString())
        return allOneCourseList
    }

    fun courseBeanToOneByOne2(courseBeanList: List<CourseBean>): List<List<OneByOneCourseBean>> {
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
                        val a = OneByOneCourseBean(
                            name,
                            courseBean.classSessions,
                            courseBean.classSessions + courseBean.continuingSession - 1,
                            courseBean.classDay,
                            Color(colorStringToLong(courseBean.color))
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

    fun allCourseToJson(allCourseList: List<AllCourse>): String {
        return Gson().toJson(allCourseList)
    }

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

    fun settingsToJson(settings: Settings):String{
        return Gson().toJson(settings)
    }
    private fun string2Unicode(string: String): String {
        val unicode = StringBuffer()
        for (i in 0 until string.length) {
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
        val num = string2Unicode(name).toInt()
        return try {
            colorList[num % colorList.count()]
        } catch (e: Exception) {
            colorList[0]
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
                0xffCBA689.toColorULong() -> colorList.addAll(listOf(Color(0xFFDC2424), Color(0xFF4A569D)))
                0xffffbb33.toColorULong() -> colorList.addAll(listOf(Color(0xff24C6DC), Color(0xff514A9D)))
                0xff8202F2.toColorULong() -> colorList.addAll(listOf(Color(0xffE55D87), Color(0xff5FC3E4)))
                0xffF77CC2.toColorULong() -> colorList.addAll(listOf(Color(0xff5C258D), Color(0xff4389A2)))
                0xff4b5cc4.toColorULong() -> colorList.addAll(listOf(Color(0xff134E5E), Color(0xff71B280)))
                0xff426666.toColorULong() -> colorList.addAll(listOf(Color(0xff085078), Color(0xff85D8CE)))
                0xff40de5a.toColorULong() -> colorList.addAll(listOf(Color(0xff4776E6), Color(0xff8E54E9)))
                0xfff0c239.toColorULong() -> colorList.addAll(listOf(Color(0xff1D2B64), Color(0xffF8CDDA)))
                0xff725e82.toColorULong() -> colorList.addAll(listOf(Color(0xff1A2980), Color(0xff26D0CE)))
                0xffc32136.toColorULong() -> colorList.addAll(listOf(Color(0xffAA076B), Color(0xff61045F)))
                0xffb35c44.toColorULong() -> colorList.addAll(listOf(Color(0xff403B4A), Color(0xffE7E9BB)))
                Color.Transparent.value -> colorList.addAll(listOf(Color.Transparent, Color.Transparent))
                else -> colorList.addAll(listOf(Color(0xffE55D87), Color(0xff5FC3E4)))
            }
        }
        return colorList.toList()
    }

    private fun Long.toColorULong() = (this.toULong() and 0xffffffffUL) shl 32

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


