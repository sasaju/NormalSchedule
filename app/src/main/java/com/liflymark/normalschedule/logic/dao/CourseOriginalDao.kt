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
    fun loadAllCourse(): List<CourseBean>

    @Delete
    suspend fun deleteCourse(course: CourseBean)

}