package com.liflymark.normalschedule.logic.dao

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.liflymark.schedule.data.CourseNotice
import com.liflymark.normalschedule.NormalScheduleApplication.Companion.context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object CourseNoticeDao {
    private val Context.courseNoticeStore: DataStore<CourseNotice> by dataStore(
        fileName = "courseNotice.pb",
        serializer = CourseNoticeSerializer
    )
    private val courseNotice = context.courseNoticeStore.data

    suspend fun setCourseNotice(
        lastCourse:String,
        nowCourse:String,
        date:String
    ){
        context.courseNoticeStore.updateData { currentNotice ->
            currentNotice.toBuilder()
                .setLastNotice(lastCourse)
                .setNowNotice(nowCourse)
                .setDate(date)
                .build()
        }
    }

    suspend fun setCourseNoticeNow(
        nowCourse:String,
    ){
        context.courseNoticeStore.updateData { currentNotice ->
            currentNotice.toBuilder()
                .setNowNotice(nowCourse)
                .build()
        }
    }
    suspend fun setCourseNoticeRepeatDate(
        date: String,
    ){
        context.courseNoticeStore.updateData { currentNotice ->
            currentNotice.toBuilder()
                .setRepeatSetDate(date)
                .build()
        }
    }
    suspend fun setCourseNoticeLast(
        lastCourse:String,
        date: String
    ){
        context.courseNoticeStore.updateData { currentNotice ->
            currentNotice.toBuilder()
                .setLastNotice(lastCourse)
                .setDate(date)
                .build()
        }
    }

    suspend fun setCourseNoticeTrigger(
        triggerMillis:Long
    ){
        context.courseNoticeStore.updateData { currentNotice ->
            currentNotice.toBuilder()
                .setTriggerAtMillis(triggerMillis)
                .build()
        }
    }

    suspend fun getCourseNotice() = runBlocking { courseNotice.first() }

    fun getCourseNoticeFlow() = courseNotice
}