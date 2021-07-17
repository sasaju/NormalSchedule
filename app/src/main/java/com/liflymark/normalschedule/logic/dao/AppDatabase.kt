package com.liflymark.normalschedule.logic.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.bean.UserBackgroundBean

@Database(version = 2, entities = [CourseBean::class, UserBackgroundBean::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun courseDao(): CourseOriginalDao
    abstract fun backgroundDao(): BackgroundDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("create table UserBackgroundBean (id integer primary key autoincrement not null, userBackground text not null)")
            }
        }
        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, "app_database.db")
                    .addMigrations(MIGRATION_1_2)
                    .allowMainThreadQueries()
                    .build().apply {
                    instance = this
                    }
        }
    }
}
