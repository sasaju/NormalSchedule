package com.liflymark.normalschedule.logic.dao

import androidx.room.*
import com.liflymark.normalschedule.logic.bean.UserBackgroundBean

@Dao
interface BackgroundDao {

    @Insert
    suspend fun insertBackground(background: UserBackgroundBean)

    @Update
    suspend fun updateBackground(background: UserBackgroundBean)

    @Query("select * from UserBackgroundBean order by id desc limit 0,1")
    suspend fun loadLastBackground(): UserBackgroundBean

    @Delete
    suspend fun deleteAllBackground(background: UserBackgroundBean)
}