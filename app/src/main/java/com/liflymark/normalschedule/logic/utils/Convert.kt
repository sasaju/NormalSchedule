package com.liflymark.normalschedule.logic.utils

import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.model.AllCourse
import com.liflymark.normalschedule.ui.show_timetable.MyCourse

internal object Convert {
    lateinit var oneCourse:MyCourse

    fun courseResponseToBean(courseResponse: AllCourse) = CourseBean(
            courseResponse.campusName,
            courseResponse.classDay,
            courseResponse.classSessions,
            courseResponse.classWeek,
            courseResponse.continuingSession,
            courseResponse.courseName,
            courseResponse.teacher,
            courseResponse.teachingBuildName
    )

    fun courseBeanToOneByOne(courseBeanList: List<CourseBean>): List<List<MyCourse>> {
        val allOneCourseList = mutableListOf<List<MyCourse>>()
        for (i in 0..19){
            val oneCourseList = mutableListOf<MyCourse>()
            for (courseBean in courseBeanList) {
                val name = courseBean.courseName + "\n" + courseBean.teachingBuildName + "\n" + courseBean.teacher

                when(courseBean.classWeek[i].toString()){
                    "1" -> oneCourseList.add(MyCourse(
                        name,
                        courseBean.classSessions-1,
                        courseBean.classSessions+courseBean.continuingSession-1,
                        courseBean.classDay-1))
//                    "0" -> oneCourseList.add(MyCourse("0",courseBean.classDay, 0, 0))
//                    else -> oneCourseList.add(MyCourse("0",courseBean.classDay, 0, 0))
                }
            }
            allOneCourseList.add(oneCourseList)
        }
        return allOneCourseList
    }

//    fun getAllOneCourse(courseBeanList: List<CourseBean>): List<List<MyCourse>> {
//        val allOneCourseList = mutableListOf<List<MyCourse>>()
//        val needAllOneByOneCourseList = mutableListOf<List<List<MyCourse>>>()
//        for (i in courseBeanList) {
//            allOneCourseList.add(courseBeanToOneByOne(i))
//        }
//
//        for (t in 0..19){
//            val aOneCourseList = mutableListOf<MyCourse>()
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


}