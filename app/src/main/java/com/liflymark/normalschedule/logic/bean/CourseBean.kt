package com.liflymark.normalschedule.logic.bean

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.INTEGER
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.liflymark.normalschedule.logic.utils.Convert

/** 课程data类
 * APP中极为关键的一个类
 * @param campusName 校区
 * @param color 颜色配置，实例：#ff51555
 * @param colorIndex 为适配主题色，配置一个数字，数字代表一个colorList中的index
 * @param removed 代表是否移除主题色的限制，如果为false-0，则使用colorIndex作为颜色依据，如果为true-1，则使用color作为颜色依据
 * 不过无论是开发难度和用户操作难度，实现脱离主题配置都十分的不容易，所以暂时放弃。
 */
@Keep
@Entity(primaryKeys = ["courseName", "teacher", "classWeek", "classDay", "classSessions", "continuingSession", "removed"])
data class CourseBean(
    var campusName: String,
    var classDay: Int,
    var classSessions: Int,
    var classWeek: String,
    var continuingSession: Int,
    var courseName: String,
    var teacher: String,
    var teachingBuildName: String,
    var color: String,
    @ColumnInfo(defaultValue = "") var courseNumber: String = "",
    @ColumnInfo(defaultValue = "-1") var colorIndex: Int = -1,
    @ColumnInfo(defaultValue = "0") var removed: Boolean = false
)

fun getInitial(): List<CourseBean> {
    return listOf(CourseBean(
        campusName="五四路校区",
        classDay=3,
        classSessions=9, classWeek="111111111111110000000000", continuingSession=3,
        courseName="毛泽东思想与中国特色社会主义理论概论",
        teacher="刘卫萍* 耿金龙 ",
        teachingBuildName="第九教学楼402",
        color="#f0c239"))
}