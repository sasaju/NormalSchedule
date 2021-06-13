package com.liflymark.normalschedule.logic.utils

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.bean.getInitial
import com.liflymark.normalschedule.logic.model.AllCourse
import com.liflymark.normalschedule.logic.model.Grade
import java.lang.Exception

internal object Convert {
    lateinit var oneCourse: OneByOneCourseBean

    fun courseResponseToBean(courseResponse: AllCourse): CourseBean {
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
        val num = string2Unicode(courseResponse.courseName).toInt()
        val color = try {
            colorList[num % colorList.count()]
        } catch (e: Exception) {
            colorList[0]
        }
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

    fun string2Unicode(string: String): String {
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


