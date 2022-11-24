package com.liflymark.normalschedule

import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import org.junit.Test

import org.junit.Assert.*
import java.util.GregorianCalendar

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun regex_isCorrect(){
        val testText = "考试时间: 15周 2021-12-01 星期三 08:00-09:30"
        val startDateRegex = "\\d{4}(-|/|.)\\d{1,2}\\1\\d{1,2}".toRegex()
        val startTimeRegex = "\\d{2}(:)\\d{2}(-)\\d{2}(:)\\d{2}".toRegex()
        val startTime = startDateRegex.find(testText)
        val startRealTime = startTimeRegex.find(testText)
        assertEquals(startTime!!.value, "2021-12-01")
        assertEquals(startRealTime!!.value, "08:00-09:30")
    }

    @Test
    fun regex_courseName(){
        val testText = "（17SDC00028-01）药物化学"
        val courseNumberRegex = "[(（^].*[)）\$]".toRegex()
        assertEquals(testText.replace(courseNumberRegex.find(testText)!!.value, ""), "药物化学")
    }

    @Test
    fun get_courseLocation(){
        val testText = "地点: 五四路校区 五四路校区综合教学楼 207"
        val test = testText.split(" ")
        print(test.last() + test[test.lastIndex-1])
    }

    fun test_adance_time(){
        val cal = GregorianCalendar()
        GetDataUtil.getAdvancedTimeMillis(1,50000)
    }
}