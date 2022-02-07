package com.liflymark.normalschedule.logic.dao

import androidx.room.*
import com.liflymark.normalschedule.logic.bean.Bulletin2

@Dao
interface StartBulletinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStartBulletin(bulletin2: Bulletin2):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStartBulletin(bulletin2: List<Bulletin2>)

    @Query("delete from Bulletin2 where id=:id ")
    suspend fun deleteStartBulletinById(id:Int)

    @Query("select * from Bulletin2")
    suspend fun loadAllBulletin2():List<Bulletin2>

    @Query("select * from Bulletin2 order by id desc limit 0,1")
    suspend fun getLastBulletin2():Bulletin2?


    @Delete
    suspend fun deleteBulletin2(bulletin2: Bulletin2)

}