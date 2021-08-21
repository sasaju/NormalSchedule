package com.liflymark.normalschedule.logic.dao

import androidx.room.*
import com.liflymark.normalschedule.logic.bean.HomeworkBean

@Dao
interface HomeworkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeWork(homeworkBean: HomeworkBean):Long

    @Query("select * from HomeworkBean where courseName = :courseName")
    suspend fun loadHomeworkByName(courseName:String):List<HomeworkBean>

    @Query("delete from HomeworkBean where id not null")
    suspend fun deleteAllHomework()

    @Query("delete from HomeworkBean where courseName = :courseName")
    suspend fun deleteHomeworkByName(courseName: String)

    @Query("delete from HomeworkBean where id = :id")
    suspend fun deleteHomeworkById(id: Int)

    @Query("select distinct courseName from HomeworkBean")
    suspend fun loadHasWorkCourse():List<String>

    @Query("select id from HomeworkBean order by id desc limit 0,1")
    suspend fun getLastId():Int
    @Delete
    suspend fun deleteHomework(homeWorkBean:HomeworkBean)

}