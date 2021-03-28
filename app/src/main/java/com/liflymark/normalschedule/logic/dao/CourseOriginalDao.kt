package com.liflymark.normalschedule.logic.dao

import androidx.room.*
import com.liflymark.normalschedule.logic.bean.CourseBean

@Dao
interface CourseOriginalDao {


//    @Transaction
//    suspend fun insertSingleCourse(courseBean: CourseBean) {
//        insertCourse(courseBean)
//    }


    @Insert
    suspend fun insertCourse(course: CourseBean): Long

    @Update
    suspend fun updateCourse(newCourse: CourseBean)

    @Query("select * from CourseBean")
    suspend fun loadAllCourse(): List<CourseBean>

    @Query("delete from CourseBean where courseName = :courseName")
    suspend fun deleteCourseByName(courseName: String)

    @Query("select * from CourseBean where courseName = :courseName")
    suspend fun loadCourseByName(courseName: String): List<CourseBean>

    @Query("select * from CourseBean where courseName =:courseName and classSessions = :courseStart and classDay = :whichColumn")
    suspend fun loadCourseByNameAndStart(courseName: String, courseStart: Int, whichColumn: Int): List<CourseBean>

    @Delete
    suspend fun deleteCourse(course: CourseBean)

}