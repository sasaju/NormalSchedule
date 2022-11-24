package com.liflymark.normalschedule.ui.app_wdiget_twoday

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.dao.CourseNoticeDao
import com.liflymark.normalschedule.logic.notice.setAlarmForCourseNotice
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CourseNoticeWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams){
    val context = appContext
    override fun doWork(): Result {
        val res = runBlocking {
            val mList = mutableListOf<OneByOneCourseBean>()
            val nowWeekNum = GetDataUtil.whichWeekNow() + 1
            val nowDayNum = GetDataUtil.getNowWeekNum()
            val allCourse = Repository.loadAllCourse3()
            val noticeStart = Repository.getScheduleSettingsNotice().first()
            val triggerMillis = Repository.getCourseNoticeTimeFlow().first()
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val havePer =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }
            if (noticeStart && havePer){
                allCourse?.get(nowWeekNum - 1)?.let {
                    mList.addAll(it.filter { it1 ->
                        it1.whichColumn == nowDayNum && !GetDataUtil.hadOvered(it1.end)
                    })
                    mList.sortBy { course -> course.start }
                }
                createNextAlarm(context, mList, triggerMillis)
                return@runBlocking Result.success()
            }else{
                return@runBlocking Result.failure()
            }
        }

        return res
    }

    private suspend fun createNextAlarm(
        context: Context,
        courseBeanList: List<OneByOneCourseBean>,
        advanceTimeMillis:Long
    ){
        val courseBean = courseBeanList.firstOrNull() ?: return
        val courseNameBuild2 = courseBean.courseName.split("\n")
        val courseName2 = courseNameBuild2.getOrNull(0) ?: ""
        val courseBuild2 = courseNameBuild2.getOrNull(1) ?: ""
        val courseTimeStr2 = "${GetDataUtil.getStartTime(courseBean.start)} - " +
                GetDataUtil.getEndTime(courseBean.end)
        val courseNotice = CourseNoticeDao.getCourseNotice()
        val nowDateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        // 避免重复通知检查上次通知的内容，如果内容相同且日期为今天则通知下一个
        if (
            courseNotice.lastNotice == "$courseName2$courseBuild2 - $courseTimeStr2"
            && nowDateStr==courseNotice.date
        ) {
            val nextCourseBean = courseBeanList.getOrNull(1)
            if (nextCourseBean!=null){
                val nextCourseNameBuild2 = nextCourseBean.courseName.split("\n")
                val nextCourseName2 = nextCourseNameBuild2.getOrNull(0) ?: ""
                val nextCourseBuild2 = nextCourseNameBuild2.getOrNull(1) ?: ""
                val nextCourseTimeStr2 = "${GetDataUtil.getStartTime(nextCourseBean.start)} - " +
                        GetDataUtil.getEndTime(nextCourseBean.end)
                setAlarmForCourseNotice(
                    context,
                    GetDataUtil.getAdvancedTimeMillis(nextCourseBean.start, advanceTimeMillis),
                    nextCourseName2,
                    "$nextCourseBuild2 - $nextCourseTimeStr2"
                )
                Repository.setCourseNoticeNow("$nextCourseName2$nextCourseBuild2 - $nextCourseTimeStr2")
                return
            }
        }

        // 没重复就通知第一个
        if (courseNotice.nowNotice !="$courseName2$courseBuild2 - $courseTimeStr2"
            || nowDateStr!=courseNotice.date
        ){
            Log.d("Worker", "1")
            setAlarmForCourseNotice(
                context,
                GetDataUtil.getAdvancedTimeMillis(courseBean.start, advanceTimeMillis),
                courseName2,
                "$courseBuild2 - $courseTimeStr2"
            )
            Repository.setCourseNoticeNow("$courseName2$courseBuild2 - $courseTimeStr2")
        }
    }
}