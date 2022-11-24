package com.liflymark.normalschedule.logic.notice

/**
 * 该文件视为工具类，以下方法不再在Repository中封装
 */
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.ui.app_wdiget_twoday.TwoDayWidgetReceiver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
/**
 * COURSE_NOTICE：用于课程提醒
 */
private fun createCourseNotification(
    context: Context,
    textTitle: String,
    textContent: String,
    pendingIntent: PendingIntent
): NotificationCompat.Builder {
    return NotificationCompat.Builder(context, "COURSE_NOTICE")
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
}

private fun createTestNotification(
    context: Context,
    textTitle: String,
    textContent: String,
): NotificationCompat.Builder {
    return NotificationCompat.Builder(context, "COURSE_NOTICE")
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setAutoCancel(true)
}

fun sendTestNotification(
    context: Context
){
    val testNotice = createTestNotification(context, textTitle = "这是一条测试", "当你看到它时，说明APP可以发送通知")
    with(NotificationManagerCompat.from(context)) {
        notify(20, testNotice.build())
    }
}

fun sendCourseNotification(
    context: Context,
    textTitle: String,
    textContent: String
){
    val notifyIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val notifyPendingIntent = PendingIntent.getActivity(
        context, 20, notifyIntent, PendingIntent.FLAG_IMMUTABLE
    )
    val notice = createCourseNotification(context, textTitle, textContent, notifyPendingIntent)
    with(NotificationManagerCompat.from(context)) {
        notify(20, notice.build())
    }

}

/**
 * 定时发送“显示课程通知的广播”
 */
fun setAlarmForCourseNotice(
    context:Context,
    triggerAtMillis:Long,
    title:String,
    description: String,
    test:Boolean=false
){
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    if (alarmManager==null){
        Log.d("CreateNotice", "alarmManager is null")
    }
    val intent = Intent(context, TwoDayWidgetReceiver::class.java).apply {
        action = "com.fly.courseNotice"
        putExtra("title",title)
        putExtra("description", description)
        putExtra("isTest", test)
    }
    val pendingIntent =
        PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    if (!test){alarmManager?.cancel(pendingIntent)}
    alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
//    val notifyIntent = Intent(context, MainActivity::class.java).apply {
//        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//    }
//    val notifyPendingIntent = PendingIntent.getActivity(
//        context, 0, notifyIntent, PendingIntent.FLAG_IMMUTABLE
//    )
//    alarmManager!!.setAlarmClock(AlarmManager.AlarmClockInfo(System.currentTimeMillis()+5000,notifyPendingIntent ), pendingIntent)
}

/**
 * 设置循环任务，保证每天能够激活课程提醒
 */
fun setRepeatCourseNoticeCheck(
    context:Context,
    force:Boolean = false
){
    var over: Boolean
    val nowDayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    var allowNotice:Boolean
    runBlocking {
        val noticeSetting = Repository.getCourseNotice()
        val setting = Repository.getScheduleSettings().first()
        over = noticeSetting.repeatSetDate == nowDayStr
        allowNotice = setting.openCourseNotice
    }
    if (!allowNotice) return
    if (over && !force) return
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    val intent = Intent(context, TwoDayWidgetReceiver::class.java).apply {
        action = "com.fly.setThisDayCourseNotice"
    }
    val pendingIntent =
        PendingIntent.getBroadcast(
            context, 1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    val calendar: Calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 2)
    }
    alarmManager?.cancel(pendingIntent)
    alarmManager?.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
    runBlocking {
        Log.d("CreateNotice","重置一次")
        Repository.setCourseNoticeRepeatDate(nowDayStr)
    }
}