package com.liflymark.normalschedule.logic.utils

import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.model.AllCourse

internal object Convert {
    lateinit var oneCourse: OneByOneCourseBean

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

    fun courseBeanToOneByOne(courseBeanList: List<CourseBean>): List<List<OneByOneCourseBean>> {
        val allOneCourseList = mutableListOf<List<OneByOneCourseBean>>()
        for (i in 0..19){
            val oneCourseList = mutableListOf<OneByOneCourseBean>()
            for (courseBean in courseBeanList) {
                val name = courseBean.courseName + "\n" + courseBean.teachingBuildName + "\n" + courseBean.teacher

                when(courseBean.classWeek[i].toString()){
                    "1" -> oneCourseList.add(OneByOneCourseBean(
                        name,
                        courseBean.classSessions-1,
                        courseBean.classSessions+courseBean.continuingSession-1,
                        courseBean.classDay-1))
//                    "0" -> oneCourseList.add(OneByOneCourseBean("0",courseBean.classDay, 0, 0))
//                    else -> oneCourseList.add(OneByOneCourseBean("0",courseBean.classDay, 0, 0))
                }
            }
            allOneCourseList.add(oneCourseList)
        }
        return allOneCourseList
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


}