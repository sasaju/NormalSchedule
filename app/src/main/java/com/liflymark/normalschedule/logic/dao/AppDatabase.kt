package com.liflymark.normalschedule.logic.dao

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.liflymark.normalschedule.logic.bean.*

@Database(
    version = 7,
    entities = [CourseBean::class, UserBackgroundBean::class, HomeworkBean::class, Bulletin2::class],
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4, spec = AppDatabase.DeleteId::class),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),// 创建Bulletin2表
        AutoMigration(from = 6, to = 7)// 增加课序号字段
    ]
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun courseDao(): CourseOriginalDao
    abstract fun backgroundDao(): BackgroundDao
    abstract fun homeworkDao(): HomeworkDao
    abstract fun StartBulletinDao(): StartBulletinDao

    // 采用多个键作为主键 故移除id字段
    @DeleteColumn(columnName = "id", tableName = "CourseBean")
    class DeleteId : AutoMigrationSpec { }


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
