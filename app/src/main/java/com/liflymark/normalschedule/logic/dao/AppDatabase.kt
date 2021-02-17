package com.liflymark.normalschedule.logic.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.liflymark.normalschedule.logic.bean.CourseBean

@Database(version = 1, entities = [CourseBean::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun courseDao(): CourseOriginalDao

    companion object {

        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, "app_database.db")
                .build().apply {
                    instance = this
                }
        }
    }
}
