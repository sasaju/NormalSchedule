package com.liflymark.normalschedule.logic.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.provider.CalendarContract
import android.text.TextUtils
import android.util.Log
import com.liflymark.normalschedule.logic.model.Arrange
import java.text.SimpleDateFormat
import java.util.*


object CalendarUtil {
    private const val CALENDER_URL = "content://com.android.calendar/calendars"
    private const val CALENDER_EVENT_URL = "content://com.android.calendar/events"
    private const val CALENDER_REMINDER_URL = "content://com.android.calendar/reminders"

    private const val CALENDARS_NAME = "boohee"
    private const val CALENDARS_ACCOUNT_NAME = "BOOHEE@boohee.com"
    private const val CALENDARS_ACCOUNT_TYPE = "com.android.boohee"
    private const val CALENDARS_DISPLAY_NAME = "BOOHEE账户"

    /**
     * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
     * 获取账户成功返回账户id，否则返回-1
     */
    private fun checkAndAddCalendarAccount(context: Context): Int {
        val oldId = checkCalendarAccount(context)
        return if (oldId >= 0) {
            oldId
        } else {
            val addId = addCalendarAccount(context)
            if (addId >= 0) {
                checkCalendarAccount(context)
            } else {
                -1
            }
        }
    }

    /**
     * 检查是否存在现有账户，存在则返回账户id，否则返回-1
     */
    private fun checkCalendarAccount(context: Context): Int {
        val userCursor: Cursor? =
            context.contentResolver.query(Uri.parse(CALENDER_URL), null, null, null, null)
        return try {
            if (userCursor == null) { //查询返回空值
                return -1
            }
            val count: Int = userCursor.getCount()
            val index = userCursor.getColumnIndex(CalendarContract.Calendars._ID)
            if (count > 0 && index >= 0) { //存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst()
                userCursor.getInt(userCursor.getColumnIndexOrThrow(CalendarContract.Calendars._ID))
            } else {
                -1
            }
        } finally {
            userCursor?.close()
        }
    }

    /**
     * 添加日历账户，账户创建成功则返回账户id，否则返回-1
     */
    private fun addCalendarAccount(context: Context): Long {
        val timeZone: TimeZone = TimeZone.getDefault()
        val value = ContentValues()
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME)
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME)
        value.put(CalendarContract.Calendars.VISIBLE, 1)
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE)
        value.put(
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
            CalendarContract.Calendars.CAL_ACCESS_OWNER
        )
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1)
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID())
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME)
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0)
        var calendarUri = Uri.parse(CALENDER_URL)
        calendarUri = calendarUri.buildUpon()
            .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
            .build()
        val result = context.contentResolver.insert(calendarUri, value)
        return if (result == null) -1 else ContentUris.parseId(result)
    }

    /**
     * 添加日历事件
     */
    private fun addCalendarEvent(
        context: Context?,
        title: String?,
        description: String?,
        location: String?,
        reminderTime: Long,
        endTime:Long,
        previousDate: Int
    ) {
        if (context == null) {
            return
        }
        val calId = checkAndAddCalendarAccount(context) //获取日历账户的id
        if (calId < 0) { //获取账户id失败直接返回，添加日历事件失败
            return
        }

        //添加日历事件
        val start = reminderTime // 开始时间
        val end = endTime // 结束时间
        val event = ContentValues()
        event.put("title", title)
        event.put("description", description)
        event.put("calendar_id", calId) //插入账户的id
        event.put(CalendarContract.Events.EVENT_LOCATION, location)
        event.put(CalendarContract.Events.DTSTART, start)
        event.put(CalendarContract.Events.DTEND, end)
        event.put(CalendarContract.Events.HAS_ALARM, 0) //设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai") //这个是时区，必须有
        val newEvent = context.contentResolver.insert(Uri.parse(CALENDER_EVENT_URL), event) ?: return
        Log.d("CalendarUtil", location!!.toString())
        //事件提醒的设定
        val values = ContentValues()
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent))
        values.put(CalendarContract.Reminders.MINUTES, previousDate * 24 * 60) // 提前previousDate天有提醒
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        val uri = context.contentResolver.insert(Uri.parse(CALENDER_REMINDER_URL), values)
            ?: //添加事件提醒失败直接返回
            return
    }

    /**
     * 删除日历事件
     */
    fun deleteCalendarEvent(context: Context?, title: String) {
        if (context == null) {
            return
        }
        val eventCursor: Cursor? =
            context.contentResolver.query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null)
        try {
            if (eventCursor == null) { //查询返回空值
                return
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                eventCursor.moveToFirst()
                while (!eventCursor.isAfterLast()) {
                    val eventTitle: String =
                        eventCursor.getString(eventCursor.getColumnIndexOrThrow("title"))
                    if (!TextUtils.isEmpty(title) && title == eventTitle) {
                        val id: Int =
                            eventCursor.getInt(eventCursor.getColumnIndexOrThrow(CalendarContract.Calendars._ID)) //取得id
                        val deleteUri =
                            ContentUris.withAppendedId(Uri.parse(CALENDER_EVENT_URL), id.toLong())
                        val rows = context.contentResolver.delete(deleteUri, null, null)
                        if (rows == -1) { //事件删除失败
                            return
                        }
                    }
                    eventCursor.moveToNext()
                }
            }
        } finally {
            eventCursor?.close()
        }
    }

    suspend fun addExamArrange(context: Context, arranges: List<Arrange>) {
        arranges.forEach { arrange ->
            // 匹配考试课程中的名称
            val courseNameAndNumber = arrange.examName
            val courseNumberRegex = "[(（^].*[)）\$]".toRegex()
            val courseNumber = courseNumberRegex.find(courseNameAndNumber)
            val courseName = courseNumber?.value?.let { courseNameAndNumber.replace(it, "") }
                ?: courseNameAndNumber


            // 提取考试地点
            val examBuildingStringList = arrange.examBuilding.split(" ")
            val examBuilding =  (examBuildingStringList.getOrNull(examBuildingStringList.lastIndex - 1) ?: "") + (examBuildingStringList.getOrNull(examBuildingStringList.lastIndex)?: "")

            // 匹配日期
            val startDateRegex = "\\d{4}(-|/|.)\\d{1,2}\\1\\d{1,2}".toRegex()
            val startDate = startDateRegex.find(arrange.examTime)

            // 匹配时间
            val startTimeRegex = "\\d{2}(:)\\d{2}(-)\\d{2}(:)\\d{2}".toRegex()
            val startAndEndTime = startTimeRegex.find(arrange.examTime)

            // 增加日程
            if (startAndEndTime != null && startDate != null) {
                val startDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

                val startTime = startAndEndTime.value.split("-")[0]
                val endTime = startAndEndTime.value.split("-")[1]

                val startDateDate = startDateFormat.parse("${startDate.value} $startTime")
                val endDateDate = startDateFormat.parse("${startDate.value} $endTime")

                val startDateMillis =
                    GregorianCalendar().apply { time = startDateDate!! }.timeInMillis
                val endDateMillis = GregorianCalendar().apply { time = endDateDate!! }.timeInMillis
                deleteCalendarEvent(context, "$courseName-考试")
                addCalendarEvent(
                    context,
                    title = "$courseName-考试",
                    description = "",
                    location = examBuilding,
                    reminderTime = startDateMillis,
                    endTime = endDateMillis,
                    previousDate = 7
                )
            }
        }
    }
}