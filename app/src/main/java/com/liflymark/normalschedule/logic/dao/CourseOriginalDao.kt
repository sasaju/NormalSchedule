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

    @Insert
    suspend fun insertCourse(course: List<CourseBean>)

    @Update
    suspend fun updateCourse(newCourse: CourseBean)

    @Update fun updateCourse(newCourse: List<CourseBean>)

    @Query("select * from CourseBean where removed = 0")
    suspend fun loadAllUnRemoveCourse(): List<CourseBean>

    @Query("select * from CourseBean where removed = 0")
    fun loadAllCourseAs(): List<CourseBean>

    @Query("select * from CourseBean where courseName=:courseName and classDay=:classDay and classSessions=:classSessions and continuingSession=:continuingSession and teachingBuildName=:buildingName and removed = 0" )
    suspend fun loadCourseUnTeacher(courseName: String, classDay:Int, classSessions:Int, continuingSession:Int, buildingName:String):List<CourseBean>

    @Query("select * from CourseBean where courseName=:courseName and classDay=:classDay and classSessions=:classSessions and continuingSession=:continuingSession and removed = 0" )
    suspend fun loadCourseUnTeacher(courseName: String, classDay:Int, classSessions:Int, continuingSession:Int):List<CourseBean>

    @Query("delete from CourseBean where courseName = :courseName and removed = 0")
    suspend fun deleteCourseByName(courseName: String)

    @Query("delete from CourseBean where courseName =:courseName and classSessions = :courseStart and classDay = :whichColumn and removed = 0")
    suspend fun deleteCourseByNameAndStart(courseName: String, courseStart: Int, whichColumn: Int)

    @Query("select * from CourseBean where courseName = :courseName and removed = 0")
    suspend fun loadCourseByName(courseName: String): List<CourseBean>

    @Query("select * from CourseBean where courseName =:courseName and classSessions = :courseStart and classDay = :whichColumn and removed = 0")
    suspend fun loadCourseByNameAndStart(courseName: String, courseStart: Int, whichColumn: Int): List<CourseBean>

    @Query("delete from CourseBean where courseName is not null")
    suspend fun deleteAllCourseBean()

    @Query("select distinct courseName from CourseBean")
    suspend fun loadAllCourseName():List<String>

    @Query("select * from CourseBean where removed = 1")
    suspend fun loadRemovedCourse():List<CourseBean>

    @Query("delete from CourseBean where removed = 1")
    suspend fun deleteRemovedCourseBean()

    @Delete
    suspend fun deleteCourse(course: CourseBean)

    @Delete
    suspend fun deleteCourse(course: List<CourseBean>)

}