package com.liflymark.normalschedule.logic

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.liveData
import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.bean.*
import com.liflymark.normalschedule.logic.dao.AccountDao
import com.liflymark.normalschedule.logic.dao.AccountDataDao
import com.liflymark.normalschedule.logic.dao.AppDatabase
import com.liflymark.normalschedule.logic.dao.SentenceDao
import com.liflymark.normalschedule.logic.model.*
import com.liflymark.normalschedule.logic.network.NormalScheduleNetwork
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import com.liflymark.normalschedule.ui.show_timetable.getNeededClassList
import com.liflymark.schedule.data.Settings
import com.liflymark.schedule.data.twoColorItem
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

object Repository {

    private val dataBase = AppDatabase.getDatabase(NormalScheduleApplication.context)
    private val courseDao = dataBase.courseDao()
    private val backgroundDao = dataBase.backgroundDao()
    private val homeworkDao = dataBase.homeworkDao()
    private val startBulletinDao = dataBase.StartBulletinDao()
    fun getDefaultString() = listOf(
        listOf("#12c2e9", "#FFFC354C", "#FF0ABFBC"),
        listOf("#376B78", "#FFC04848", "#FF480048"),
        listOf("#f64f59", "#ff5f2c82", "#ff49a09d"),
        listOf("#CBA689", "#FFDC2424", "#DF5B69BE"),
        listOf("#ffffbb33", "#ff24C6DC", "#ff514A9D"),
        listOf("#8202F2", "#ffE55D87", "#ff5FC3E4"),
        listOf("#F77CC2", "#ff5C258D", "#ff4389A2"),
        listOf("#4b5cc4", "#ff134E5E", "#ff71B280"),
        listOf("#426666", "#ff085078", "#ff85D8CE"),
        listOf("#40de5a", "#ff4776E6", "#ff8E54E9"),
        listOf("#f0c239", "#5B59FF", "#D2934B"),
        listOf("#725e82", "#A91A2980", "#ff26D0CE"),
        listOf("#c32136", "#ffAA076B", "#E692088F"),
        listOf("#b35c44", "#FFFFA8C3", "#FFDCE083"),
    )

    private fun getDefaultStringTwo() = listOf(
        listOf("#6E3CBC", "#30cfd0", "#330867"),
        listOf("#7267CB", "#667eea", "#764ba2"),
        listOf("#98BAE7", "#9890e3", "#b1f4cf"),
        listOf("#B8E4F0", "#2af598", "#009efd"),
        listOf("#009DAE", "#00c6fb", "#005bea"),
        listOf("#009DAE", "#00c6fb", "#005bea"),
        listOf("#38A3A5", "#b721ff", "#21d4fd"),
        listOf("#57CC99", "#0acffe", "#495aff"),
        listOf("#80ED99", "#007adf", "#00ecbc"),
        listOf("#7FC8A9", "#7DE2FC", "#B9B6E5"),
        listOf("#D5EEBB", "#6C44EC", "#3AC1FC"),
        listOf("#3EDBF0", "#5A49F5", "##2C9CA6"),
        listOf("#93e37d", "#7164F5", "#21BEF1"),
        listOf("#7868E6", "#511DDC", "#5AA2FC"),
    )

    fun getId() = fire(Dispatchers.IO) {
        val id = NormalScheduleNetwork.getId()
        if (id.id != "") {
            Result.success(id)
        } else {
            Result.failure(RuntimeException("Can't get id!"))
        }
    }

    fun getId2() = liveData {
        try {
            val result = NormalScheduleNetwork.getId()
            emit(result)
        } catch (e: Exception) {
            emit(IdResponse(""))
        }
    }

    fun getId3() = fireFlow(Dispatchers.IO) {
        Result.success(NormalScheduleNetwork.getId())
    }

    fun getId4() = liveData {
        try {
            val result = NormalScheduleNetwork.getId()
            emit(result)
        } catch (e: Exception) {
            emit(IdResponse("love"))
        }
    }

    fun getId5() = flow {
        val result = NormalScheduleNetwork.getId()
        emit(result)
    }.catch {
        emit(IdResponse("love"))
    }

    suspend fun getId6(): IdResponse {
        Log.d("Repo", "GetID6")
        return try {
            NormalScheduleNetwork.getId()
        } catch (e: Exception) {
            IdResponse("love")
        }
    }

    fun cancelAll() = NormalScheduleNetwork.cancelAll()

    fun getCaptcha(sessionId: String) = liveData(Dispatchers.IO) {
        try {
            val img = NormalScheduleNetwork.getCaptcha(sessionId).bytes()
            val imgStream = BitmapFactory.decodeByteArray(img, 0, img.size)
            emit(imgStream)
        } catch (e: Exception) {
            emit(null)
        }
    }

//    fun getCourse(user:String, password:String, yzm:String, headers:String) = fire(Dispatchers.IO) {
//        val courseResponse = NormalScheduleNetwork.getCourse(user, password, yzm, headers)
//        Result.success(courseResponse)
//    }
//
//    fun getCourse(user: String, password: String) = fire(Dispatchers.IO){
//        val courseResponse = NormalScheduleNetwork.getCourse(user, password)
//        Result.success(courseResponse)
//    }

    fun getCourse2(user: String, password: String, yzm: String, headers: String) =
        liveData(Dispatchers.IO) {
            try {
                when {
                    user == "123456" -> {
                        val courseResponse = courseSampleData()
                        emit(courseResponse)
                    }
                    headers != "" -> {
                        val courseResponse =
                            NormalScheduleNetwork.getCourse(user, password, yzm, headers)
//                        Log.d("Repon", "getCourse发生错误")
                        emit(courseResponse)
                    }
                    else -> {
                        val courseResponse = NormalScheduleNetwork.getCourse(user, password)
                        emit(courseResponse)
                    }
                }
            } catch (e: Exception) {
                Log.d("Repon", e.toString())
                emit(null)
            }

        }

    fun getCourse2(user: String, password: String) = liveData(Dispatchers.IO) {
        try {
            if (user == "123456") {
                val courseResponse = courseSampleData()
                emit(courseResponse)
            } else {
                val courseResponse = NormalScheduleNetwork.getCourse(user, password)
                emit(courseResponse)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    fun getVisitCourse() = fire(Dispatchers.IO) {
//        val courseResponse = NormalScheduleNetwork.getVisitCourse()
        val courseResponse = CourseResponse(
            listOf(
                AllCourse(
                    "五四路",
                    4,
                    1,
                    "11111111111111111111111",
                    1,
                    "点击右上角重新导入课程",
                    "",
                    ""
                )
            ), status = "ok"
        )
        Result.success(courseResponse)
    }

    suspend fun loadCourseUnTeacher(
        className: String,
        classDay: Int,
        classSessions: Int,
        continuingSession: Int, buildingName: String
    ): List<CourseBean> {
        return courseDao.loadCourseUnTeacher(
            className,
            classDay,
            classSessions,
            continuingSession,
            buildingName
        )
    }

    suspend fun loadCourseUnTeacher(
        className: String,
        classDay: Int,
        classSessions: Int,
        continuingSession: Int
    ): List<CourseBean> {
        return courseDao.loadCourseUnTeacher(
            className,
            classDay,
            classSessions,
            continuingSession
        )
    }

    fun loadAllCourse2(colorList: List<List<String>> = getDefaultString()) =
        liveData(Dispatchers.IO) {
            emit(Convert.courseBeanToOneByOne2(courseDao.loadAllUnRemoveCourse(), colorList))
        }

    fun getDepartmentList() = flow {
        Log.d("Repo", "getDeprat执行")
        val result = NormalScheduleNetwork.getDepartmentList()
        emit(result)
    }.catch {
        emit(DepartmentList("异常", listOf()))
    }.flowOn(Dispatchers.IO)

    fun loadCourseByMajorToAll(department: String, major: String) = flow {
        val result = NormalScheduleNetwork.getCourseByMajor(department, major)
        emit(result)
    }.catch {
        emit(CourseResponse(listOf(), "异常"))
    }

    fun loadCourseByMajor2(department: String, major: String) = flow {
        val result = NormalScheduleNetwork.getCourseByMajor(department, major)
        val allCourseList = mutableListOf<CourseBean>()
        if (result.status == "读取正常") {
            for (i in result.allCourse) {
                val a = Convert.courseResponseToBean(i)
                allCourseList.add(a)
            }
        }
        emit(Convert.courseBeanToOneByOne2(allCourseList, getDefaultString()))
    }.catch {
        emit(getNeededClassList(getData()))
    }

    fun deleteCourseByName(courseName: String) = liveData(Dispatchers.IO) {
        try {
            courseDao.deleteCourseByName(courseName)
            emit(true)
        } catch (e: Exception) {
            emit(false)
        }
    }

    fun loadCourseByName2(courseName: String) = flow {
        val courseBeanList = courseDao.loadCourseByName(courseName)
        emit(courseBeanList.toList())
    }.catch {
        emit(
            listOf(
                CourseBean(
                    campusName = "五四路校区",
                    classDay = 3,
                    classSessions = 9,
                    classWeek = "111111111111110000000000",
                    continuingSession = 3,
                    courseName = courseName,
                    teacher = "发生错误 ",
                    teachingBuildName = "发生错误 ",
                    color = "#f0c239"
                )
            )
        )
    }.flowOn(Dispatchers.IO)

    suspend fun loadCourseByName(courseName: String) =
        try {
            courseDao.loadCourseByName(courseName)
        } catch (e: Exception) {
            listOf(
                CourseBean(
                    campusName = "五四路校区",
                    classDay = 3,
                    classSessions = 9, classWeek = "111111111111110000000000",
                    continuingSession = 3,
                    courseName = "发生错误",
                    teacher = "发生错误 ",
                    teachingBuildName = "发生错误 ",
                    color = "#f0c239"
                )
            )
        }

    suspend fun deleteCourseByList(courseBeanList: List<CourseBean>) {
        try {
            courseBeanList.forEach {
                courseDao.deleteCourse(it)
                Log.d("Repo", it.toString())
            }
        } catch (e: java.lang.Exception) {
            Log.d("Repos", e.toString())
        }

    }


    fun loadCourseByNameAndStart2(courseName: String, courseStart: Int, whichColumn: Int) = flow {
        val result = courseDao.loadCourseByNameAndStart(courseName, courseStart, whichColumn)
        emit(result[0])
    }
        .catch {
            emit(CourseBean("", 0, 0, "011110", 0, "异常查询", "", "", ""))
        }
        .flowOn(Dispatchers.IO)

    fun getScore(user: String, password: String, id: String) = liveData(Dispatchers.IO) {
        try {
            if (user != "123456") {
                val scoreResponse = NormalScheduleNetwork.getScore(user, password, id)
                emit(scoreResponse)
            } else {
                val scoreResponse = ScoreResponse(
                    listOf(
                        Grade(
                            "培养方案",
                            listOf(
                                listOf(
                                    ThisProjectGrade("实例", "示例课程", 90.0, "2021"),
                                    ThisProjectGrade("实例", "示例课程2", 95.0, "2021")
                                )
                            )
                        ),
                    ),
                    "登陆成功"
                )
                emit(scoreResponse)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    fun getScoreDetail2(user: String, password: String, id: String) = fireFlow(Dispatchers.IO) {
        if (user != "123456") {
            val scoreDetailResponse = NormalScheduleNetwork.getScoreDetail(user, password, id)
            Result.success(scoreDetailResponse)
        } else {
            val a = ScoreDetail(
                listOf(
                    Grades("90", "示例课程", "培养方案", "90.0", "5.0", "90", "99", "20", "5", "88"),
                    Grades("92", "示例课程2", "培养方案", "90.0", "4.0", "92.2", "95", "10", "10", "95")
                ),
                result = "登陆成功"
            )
            Result.success(a)
        }
    }

    fun getScoreDetail(user: String, password: String, id: String) = flow {
        if (user != "123456") {
            val scoreDetailResponse = NormalScheduleNetwork.getScoreDetail(user, password, id)
            emit(scoreDetailResponse)
        } else {
            val a = ScoreDetail(
                listOf(
                    Grades("90", "示例课程", "培养方案", "90.0", "5.0", "90", "99", "20", "5", "88"),
                    Grades("92", "示例课程2", "培养方案", "90.0", "4.0", "92.2", "95", "10", "10", "95")
                ),
                result = "登陆成功"
            )
            emit(a)
        }
    }.catch {
        val a = ScoreDetail(
            listOf(
                Grades("90", "示例课程", "培养方案", "90.0", "5.0", "90", "99", "20", "5", "88"),
                Grades("92", "示例课程2", "培养方案", "90.0", "4.0", "92.2", "95", "10", "10", "95")
            ),
            result = "登陆异常"
        )
        emit(a)
    }

    fun loadAllCourse3(): List<List<OneByOneCourseBean>>? {
        return try {
            Convert.courseBeanToOneByOne2(courseDao.loadAllCourseAs(), getDefaultString())
        } catch (e: Exception) {
            null
        }
    }

    fun getCourseBeanColor(courseBean: CourseBean) = Convert.getCourseBeanColor(courseBean, getDefaultString())

    suspend fun insertCourse2(courseList: List<AllCourse>) {
        for (singleCourse in courseList) {
            try {
                courseDao.insertCourse(Convert.courseResponseToBean(singleCourse))
            } catch (e: Exception) {

            }
        }
    }

    suspend fun insertCourse(courseBean: CourseBean) {
        try {
            courseDao.insertCourse(courseBean)
        } catch (e: Exception) {

        }
    }

    suspend fun insertCourse(courseBean: List<CourseBean>) {
        courseBean.forEach {
            try {
                Log.d("Repo", courseBean.toString())
                courseDao.insertCourse(it)
            } catch (e: Exception) {
                courseDao.updateCourse(it)
            }
        }
    }

    suspend fun deleteAllCourseBean2() {
        try {
            courseDao.deleteAllCourseBean()
        } catch (e: Exception) {
//            Result.failure<Exception>(e)
        }
    }


    suspend fun updateBackground(background: UserBackgroundBean) {
        return try {
            backgroundDao.insertBackground(background)
        } catch (e: Exception) {
            backgroundDao.updateBackground(background)
        }
    }

    fun loadBackground() = liveData(Dispatchers.IO) {
        val resources = NormalScheduleApplication.context.resources
        val resourceId =
            R.drawable.main_background_4 // r.mipmap.yourmipmap; R.drawable.yourdrawable
        val uriBeepSound = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(resourceId))
            .appendPath(resources.getResourceTypeName(resourceId))
            .appendPath(resources.getResourceEntryName(resourceId))
            .build()
        try {
            val a = backgroundDao.loadLastBackground()
            if (a.userBackground != "0") {
                emit(Uri.parse(a.userBackground))
            } else {
                Log.d("ShowBackgroudRE", uriBeepSound.toString())
                emit(uriBeepSound)
            }
        } catch (e: Exception) {
            emit(uriBeepSound)
        }
    }

    suspend fun loadBackground2(): Uri {
        val resources = NormalScheduleApplication.context.resources
        val resourceId =
            R.drawable.main_background_4 // r.mipmap.yourmipmap; R.drawable.yourdrawable
        val uriBeepSound = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(resourceId))
            .appendPath(resources.getResourceTypeName(resourceId))
            .appendPath(resources.getResourceEntryName(resourceId))
            .build()
        try {
            val a = backgroundDao.loadLastBackground()
            if (a.userBackground != "0") {
                return Uri.parse(a.userBackground)
            } else {
                Log.d("ShowBackgroudRE", uriBeepSound.toString())
                return uriBeepSound
            }
        } catch (e: Exception) {
            return uriBeepSound
        }
    }

    suspend fun loadBackgroundFileName(): String? {
        return try {
            val a = backgroundDao.loadLastBackground()
            if (a.userBackground != "0") {
                a.userBackground.split('/').last()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            null
        }
    }

    fun getSentences(force: Boolean = false) = flow {
        try {
            if (SentenceDao.isSentenceSaved() && !force) {
                val resultList = SentenceDao.getSentences()
                emit(OneSentencesResponse(resultList, "local"))
            } else {
                val result = NormalScheduleNetwork.getSentences()
                SentenceDao.saveSentence(result.result)
                emit(result)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    fun loginToSpace(user: String, password: String, id: String) = flow {
        try {
            val result = NormalScheduleNetwork.loginToSpace(user, password, id)
            emit(result)
        } catch (e: Exception) {
            emit(SpaceLoginResponse("登陆异常"))
        }
    }.flowOn(Dispatchers.IO)
        .catch {
            emit(SpaceLoginResponse("登陆异常"))
        }

    fun getBulletin() =
        flow {
            val result = NormalScheduleNetwork.getBulletin()
            emit(result)
        }
            .flowOn(Dispatchers.IO)
            .catch {
                val error = Bulletin("admin", "作者服务器炸了，有事烧纸", "????", "作者服务器炸了")
                emit(DevBoardResponse(listOf(error), status = "error"))
            }

    fun getSpaceRooms(id: String, roomName: String, searchDate: String) = flow {
        val result = NormalScheduleNetwork.getSpaceRooms(id, roomName, searchDate)
        emit(result)
    }.catch {
        val errorRoom = Room("查询失败", "0", "00000000000", "无")
        emit(SpaceResponse(roomList = listOf(errorRoom), roomName = roomName))
    }.flowOn(Dispatchers.IO)

    fun getSchoolBusTime(searchType: String) = flow {
        val result = NormalScheduleNetwork.getSchoolBusTime(searchType = searchType)
        emit(result)
    }.catch {
        val error = SchoolBusResponse(
            nowDay = "error",
            timeList = TimeList(
                fiveToSeven = listOf(),
                sevenToFive = listOf()
            )
        )
        emit(error)
    }

    suspend fun addHomework(homeworkBean: HomeworkBean): Long {
        return homeworkDao.insertHomeWork(homeworkBean)
    }

    fun loadHomeworkByName(courseName: String) =
        flow {
            val result = homeworkDao.loadHomeworkByName(courseName)
            Log.d("Repo", "加载一次")
            emit(result)
        }
            .flowOn(Dispatchers.IO)
            .catch {
                val init = HomeworkBean(
                    id = 999,
                    "未查询到",
                    "无",
                    deadLine = 0,
                    finished = false,
                    createDate = 0
                )
                emit(listOf(init))
            }

    fun getNewBeanInit(courseName: String) = flow {
        val lastId = homeworkDao.getLastId()
        Log.d("Repo", "NewBean加载一次")
        val newId = lastId + 1
        emit(
            HomeworkBean(
                id = newId,
                courseName = courseName,
                workContent = "",
                createDate = GetDataUtil.getDayMillis(0),
                deadLine = GetDataUtil.getDayMillis(7),
                finished = false
            )
        )

    }
        .catch {
            emit(
                HomeworkBean(
                    id = 0,
                    courseName = courseName,
                    workContent = "",
                    createDate = GetDataUtil.getDayMillis(0),
                    deadLine = GetDataUtil.getDayMillis(7),
                    finished = false
                )
            )
        }
        .flowOn(Dispatchers.IO)

    fun loadWorkCourseName() =
        flow {
            Log.d("Repo", "leadName加载一次")
            val result = homeworkDao.loadHasWorkCourse()
            emit(result)
        }
            .catch {
                emit(listOf())
            }
            .flowOn(Dispatchers.IO)

    fun loadUnFinishCourseName() =
        flow {
            Log.d("Repo", "leadName加载一次")
            val result = homeworkDao.loadHasWorkCourse()
            val result_ = mutableListOf<String>()
            for (singleName in result) {
                val homeworkList = homeworkDao.loadHomeworkByName(singleName)
                homeworkList.forEach {
                    if (!it.finished) {
                        result_.add(it.courseName)
                    }
                }
            }
            emit(result_.toList())
        }
            .catch {
                emit(listOf())
            }
            .flowOn(Dispatchers.IO)

    suspend fun deleteHomeworkByName(courseName: String): String {
        return try {
            homeworkDao.deleteHomeworkByName(courseName = courseName)
            "success"
        } catch (e: Exception) {
            "error"
        }
    }

    suspend fun deleteHomeworkById(id: Int): String {
        return try {
            homeworkDao.deleteHomeworkById(id)
            "success"
        } catch (e: Exception) {
            "error"
        }
    }

    fun getScheduleSettings(): Flow<Settings> {
        val defaultString = getDefaultString()
        return AccountDataDao.scheduleSettings.map {
            val new = it.toBuilder()
            if (new.coursePerHeight == 0) {
                new.coursePerHeight = 70
            }
            if (new.courseNameFontSize == 0F) {
                new.courseNameFontSize = 13F
            }
            if (new.courseTeacherFontSize == 0F) {
                new.courseTeacherFontSize = 10F
            }
            if (new.courseCardAlpha == 0F) {
                new.courseCardAlpha = 0.75F
            }
            if (new.courseBorderAlpha == 0) {
                new.courseBorderAlpha = 50
            }
            if (new.colorsList.isEmpty()) {
                new.addAllColors(colorListSetting(defaultString))
            }
            if (new.courseCardRadius == 0F) {
                new.courseCardRadius = 4F
            }
            if (new.timetableIconColor == 0) {
                new.timetableIconColor = 0xFF000000.toInt()
            }
            new.build()
        }
    }

    fun getScheduleSettingsColorList(): Flow<List<twoColorItem>> {
        return AccountDataDao.scheduleSettings.map {
            val new = it.toBuilder()
            if (new.colorsList.isEmpty()) {
                new.addAllColors(colorListSetting(getDefaultString()))
            }
            new.colorsList
        }
    }

    fun getColorListAsync(): List<twoColorItem> {
        val settingsList = AccountDataDao.getColorListAsyc()
        return if (settingsList.isEmpty()) {
            colorListSetting(getDefaultString())
        } else {
            AccountDataDao.getColorListAsyc()
        }
    }

    private fun colorListSetting(colorList: List<List<String>>): List<twoColorItem> {
        val res = mutableListOf<twoColorItem>()
        colorList.forEach { twoColor ->
            val two = twoColorItem.newBuilder().apply {
                addAllColorItem(twoColor)
            }.build()
            res.add(two)
        }
        return res.toList()
    }

    fun colorListSettingToStringList(colorList: List<twoColorItem>): List<List<String>> {
        val res = mutableListOf<List<String>>()
        colorList.forEach { items ->
            res.add(items.colorItemList)
        }
        return res.toList()
    }

    fun colorStringListToTwoItems(
        colorStringList: List<List<String>> = getDefaultString()
    ): List<twoColorItem> {
        return colorListSetting(colorStringList)
    }

    suspend fun updateMode(mode: Int): Int {
        return try {
            AccountDataDao.updateColorMode(mode = mode)
            0
        } catch (e: Exception) {
            1
        }
    }

    fun getShowDarkBack() = AccountDataDao.getDarkShowBack()

    suspend fun updateShowDarkBack(show: Boolean): Int {
        return try {
            AccountDataDao.updateDarkShowBack(show)
            0
        } catch (e: Exception) {
            1
        }
    }

    suspend fun updateSettings(setSettings: (settings: Settings) -> Settings) {
        AccountDataDao.updateSettings {
            Log.d("Repo", "SaveSettings 1")
            setSettings(it)
        }
    }

    suspend fun updateSettings(settings: Settings) {
        AccountDataDao.updateSettings {
            Log.d("Repo", "SaveSettings 1")
            settings
        }
    }

    fun loadAllCourseName() = flow {
        val res = courseDao.loadAllCourseName()
        emit(res)
    }.flowOn(Dispatchers.IO)

    suspend fun loadAllCourseNameNoFlow() = courseDao.loadAllCourseName()

    suspend fun unRemovedCourseToRemoved(
        courseList: List<String>
    ) {
        courseList.forEach { courseName ->
            val courseBeanList = courseDao.loadCourseByName(courseName)
            courseBeanList.forEach {
                Log.d("rep", it.toString())
                it.removed = true
                Log.d("rep", it.toString())
            }
            courseDao.updateCourse(courseBeanList)
        }
    }

    suspend fun removedCourseToUnRemoved() {
        val removedCourseBeanList = courseDao.loadRemovedCourse()
        val removedName = removedCourseBeanList.map { it.courseName }.toSet()
        removedCourseBeanList.forEach {
            it.removed = false
        }
        removedName.forEach {
            courseDao.deleteCourseByName(it)
        }
        courseDao.updateCourse(removedCourseBeanList)
    }

    fun getExamArrange(user: String, password: String, id: String) = flow {
        val res = NormalScheduleNetwork.getExamArrange(user, password, id)
        Log.d("RepoExam", res.toString())
        emit(res)
    }
        .catch {
            val fail = ExamArrangeResponse(
                result = "查询异常",
                arrange_list = listOf()
            )
            emit(fail)
        }
        .flowOn(Dispatchers.IO)

    fun getNewVersion(versionCode: String) = flow {
        val res = NormalScheduleNetwork.getNewVersion(versionCode)
        emit(res)
    }.catch {
        CheckUpdateResponse(
            status = "404",
            result = "链接服务器失败",
            force = null,
            newUrl = null
        )
    }

    suspend fun getNewVerison2(versionCode: String) =
        NormalScheduleNetwork.getNewVersion(versionCode)

    // 只要本地没有存储公告每次打开都会请求，存储公告以后每天访问一遍公告
    suspend fun getNewStartBulletin2(versionCode: Int): Bulletin2? {
        try {
            val lastBulletin2 = startBulletinDao.getLastBulletin2()
            val lastId = (lastBulletin2?.id) ?: 0
            val lastUpdate = AccountDataDao.getLastUpdate()
            return if (lastBulletin2 == null || !GetDataUtil.thisStringIsToday(lastUpdate)) {
                val res = NormalScheduleNetwork.getStartBulletin(lastId, versionCode)
                AccountDataDao.setLastUpdate(GetDataUtil.getTodayDateString())
                Log.d("getNewStart", "访问一次")
                if (res.bulletin_list.isEmpty()) {
                    // 如果今天没有公告就更新一次每日一句
                    getSentences(true).last()
                    null
                } else {
                    startBulletinDao.insertStartBulletin(res.bulletin_list)
                    res.bulletin_list.last()
                }
            } else {
                Log.d("getNewStart", "today have visited")
                null
            }
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun loadAllStartBull() = startBulletinDao.loadAllBulletin2()

    // 研究生导入
    fun loginWebVPN(user: String, password: String) = flow {
        val res = NormalScheduleNetwork.loginWebVPN(user, password)
        emit(res)
    }
        .flowOn(Dispatchers.IO)
        .catch {
            emit(
                GraduateWeb(
                    cookies = "",
                    result = "访问异常"
                )
            )
        }

    fun loginURP(
        user: String,
        password: String,
        yzm: String,
        cookies: String
    ) = flow {
        val res = NormalScheduleNetwork.loginURP(user, password, yzm, cookies)
        emit(res)
    }
        .flowOn(Dispatchers.IO)
        .catch {
            GraduateResponse(
                allCourse = listOf(),
                result = "访问异常",
                status = "no"
            )
        }

    fun getGraduateCaptcha(cookies: String) = flow {
        val img = NormalScheduleNetwork.getGraduateCaptcha(cookies = cookies).bytes()
        val imgStream = BitmapFactory.decodeByteArray(img, 0, img.size)
        emit(imgStream)
    }.catch {
        emit(null)
    }
        .flowOn(Dispatchers.IO)

//    suspend fun getGraduateCaptcha2(cookies: String):Bitmap = withContext(Dispatchers.IO) {
//        val img = NormalScheduleNetwork.getGraduateCaptcha(cookies = cookies).bytes()
//        val imgStream = BitmapFactory.decodeByteArray(img, 0, img.size)
//        imgStream
//    }


    fun getScoreDetail() = AccountDataDao.getScoreDetail()
    fun setScoreDetail(detail: String) = AccountDataDao.updateScoreDetail(detail)

    /**用于河大春学期17周调课事项
     * 第7周周六（4月16日）补第17周周一（6月20日）课程。
     * 第8周周六（4月23日）补第17周周二（6月21日）课程。
     * 第9周周六（4月30日）补第17周周三（6月22日）课程。
     * 第10周周六（5月7日）补第17周周四（6月23日）课程。
     * 第11周周六（5月14日）补第17周周五（6月24日）课程。
     * 第12周周六（5月21日）补第17周周日（6月26日）课程。
     */
    suspend fun autoChangeCourseBean():Boolean {
        try{
            val allCourse = courseDao.loadAllUnRemoveCourse()
            val changeMap = mapOf(
                1 to 7,
                2 to 8,
                3 to 9,
                4 to 10,
                5 to 11,
                7 to 12,
            )
            for (singleCourse in allCourse) {
                if (singleCourse.classWeek.substring(
                        16,
                        17
                    ) == "1" && singleCourse.classDay in changeMap.keys
                ) {
                    val newCourse = singleCourse.copy(
                        classWeek = changeMap[singleCourse.classDay]!!.toClassWeek(),
                        courseName = singleCourse.courseName + "[调]",
                    )
                    courseDao.insertCourse(newCourse)
                }
            }
            return true
        } catch (e:Exception){
            return false
        }
    }

//    suspend fun deleteAdjustClass() = courseDao.deleteCourseByAdjust()


    private fun Int.toClassWeek() : String {
        val initStr = Array(24) { 0 }.map { "0" }.toMutableList()
        initStr[this-1] = "1"
        return initStr.joinToString()
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    private fun <T> fireFlow(context: CoroutineContext, block: suspend () -> Result<T>) =
        flow {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }.flowOn(context)


    fun saveAccount(user: String, password: String) = AccountDao.saveAccount(user, password)
    fun getSavedAccount() = AccountDao.getSavedAccount()
    fun isAccountSaved() = AccountDao.isAccountSaved()
    fun importAgain() = AccountDao.importedAgain()
    fun saveLogin() = AccountDao.saveLogin()

    // 0-未读取，1-已经显示快速跳转，3-已经显示过快速跳转，已完成重大Bug检测, 4-已完成新的新手引导
    suspend fun saveUserVersion(version: Int = 1) = AccountDataDao.saveUserVersion(version)
    fun getNewUserOrNot() = AccountDataDao.getNewUserOrNot()
    fun getUserVersion() = AccountDataDao.getUserVersion()
    suspend fun getUserVersionS() = AccountDataDao.getUserVersionS()

    fun joinQQGroup(context: Context) {
        val key = "IQn1Mh09oCQwvfVXljBPgCkkg8SPfjZP"
        val intent = Intent()
        intent.data =
            Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toasty.error(context, "未安装QQ").show()
        }
    }

    suspend fun saveSpaceSelected(school:String, buildingName: String){
        AccountDao.saveSelected(school, buildingName)
    }

    fun readSpaceSelected() = AccountDao.readSelected()
    private fun courseSampleData() = CourseResponse(
        listOf(
            AllCourse(
                "五四路",
                4,
                2,
                "11111111111111111111111",
                4,
                "物理化学",
                "王老师",
                "第九教学楼808"
            ),
            AllCourse(
                "五四路",
                6,
                3,
                "11111111111111111111111",
                2,
                "这是一个示例课程",
                "张老师",
                "九教999"
            ),
            AllCourse(
                "五四路",
                5,
                2,
                "11111111111111111111111",
                2,
                "点击右上角重新导入课程",
                "王老师",
                "第九教学楼808"
            ),
            AllCourse(
                "五四路",
                3,
                3,
                "11111111111111111111111",
                3,
                "这是一个示例课程",
                "张老师",
                "九教999"
            ),
            AllCourse(
                "五四路",
                2,
                2,
                "11111111111111111111111",
                5,
                "点击右上角重新导入课程",
                "王老师",
                "第九教学楼808"
            ),
            AllCourse(
                "五四路",
                2,
                9,
                "11111111111111111111111",
                2,
                "这是一个示例课程",
                "张老师",
                "九教999"
            ),
            AllCourse(
                "五四路",
                1,
                2,
                "11111111111111111111111",
                1,
                "点击右上角重新导入课程",
                "王老师",
                "第九教学楼808"
            ),
            AllCourse(
                "五四路",
                3,
                9,
                "11111111111111111111111",
                2,
                "这是一个示例课程",
                "张老师",
                "九教999"
            ),
        ), status = "yes"
    )


}

