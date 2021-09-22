package com.liflymark.normalschedule.logic

import android.content.ContentResolver
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

object  Repository {

    private val dataBase = AppDatabase.getDatabase(NormalScheduleApplication.context)
    private val courseDao = dataBase.courseDao()
    private val backgroundDao = dataBase.backgroundDao()
    private val homeworkDao = dataBase.homeworkDao()

    fun getId() = fire(Dispatchers.IO) {
        val id =NormalScheduleNetwork.getId()
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
        } catch (e: Exception){
            emit(IdResponse(""))
        }
    }

    fun getId3() = fireFlow(Dispatchers.IO){
        Result.success(NormalScheduleNetwork.getId())
    }

    fun getId4() = liveData {
        try {
            val result = NormalScheduleNetwork.getId()
            emit(result)
        } catch (e: Exception){
            emit(IdResponse("love"))
        }
    }

    fun cancelAll() = NormalScheduleNetwork.cancelAll()

    fun getCaptcha(sessionId: String) =  liveData(Dispatchers.IO) {
        try {
            val img = NormalScheduleNetwork.getCaptcha(sessionId).bytes()
            val imgStream = BitmapFactory.decodeByteArray(img, 0, img.size)
            emit(imgStream)
        } catch (e: Exception){
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

    fun getCourse2(user:String, password:String, yzm:String, headers:String) = liveData(Dispatchers.IO){
        try {
            when {
                user=="123456" -> {
                    val courseResponse = CourseResponse(listOf(
                        AllCourse(
                            "五四路",
                            4,
                            2,
                            "11111111111111111111111",
                            1,
                            "点击右上角重新导入课程",
                            "",
                            ""
                        ),
                        AllCourse(
                            "五四路",
                            3,
                            3,
                            "11111111111111111111111",
                            2,
                            "这是一个示例课程",
                            "张老师",
                            "九教999"
                        ),
                    ), status = "yes")
                    emit(courseResponse)
                }
                headers!="" -> {
                    val courseResponse = NormalScheduleNetwork.getCourse(user, password, yzm, headers)
                    Log.d("Repon","getCourse发生错误")
                    emit(courseResponse)
                }
                else -> {
                    val courseResponse = NormalScheduleNetwork.getCourse(user, password)
                    emit(courseResponse)
                }
            }
        } catch (e: Exception){
            Log.d("Repon",e.toString())
            emit(null)
        }

    }

    fun getCourse2(user: String, password: String) = liveData(Dispatchers.IO){
        try {
            if (user=="123456"){
                val courseResponse = CourseResponse(listOf(
                    AllCourse(
                        "五四路",
                        4,
                        2,
                        "11111111111111111111111",
                        1,
                        "点击右上角重新导入课程",
                        "",
                        ""
                    ),
                    AllCourse(
                        "五四路",
                        3,
                        3,
                        "11111111111111111111111",
                        2,
                        "这是一个示例课程",
                        "张老师",
                        "九教999"
                    ),
                ), status = "yes")
                emit(courseResponse)
            } else {
                val courseResponse = NormalScheduleNetwork.getCourse(user, password)
                emit(courseResponse)
            }
        } catch (e: Exception){
            emit(null)
        }
    }

    fun getVisitCourse() = fire(Dispatchers.IO){
//        val courseResponse = NormalScheduleNetwork.getVisitCourse()
        val courseResponse = CourseResponse(listOf(
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
        ), status = "ok")
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

    fun loadAllCourse2() = liveData(Dispatchers.IO) {
        emit(Convert.courseBeanToOneByOne2(courseDao.loadAllCourse()))
    }

    fun getDepartmentList() = flow {
        Log.d("Repo", "getDeprat执行")
        val result = NormalScheduleNetwork.getDepartmentList()
        emit(result)
    }.catch {
        emit(DepartmentList("异常", listOf()))
    }.flowOn(Dispatchers.IO)

    fun loadCourseByMajorToAll(department: String, major:String) = flow{
        val result = NormalScheduleNetwork.getCourseByMajor(department, major)
        emit(result)
    }.catch {
        emit(CourseResponse(listOf(), "异常"))
    }

    fun loadCourseByMajor2(department: String, major:String) = flow {
        val result = NormalScheduleNetwork.getCourseByMajor(department, major)
        val allCourseList = mutableListOf<CourseBean>()
        if (result.status == "读取正常"){
            for (i in result.allCourse){
                val a = Convert.courseResponseToBean(i)
                allCourseList.add(a)
            }
        }
        emit(Convert.courseBeanToOneByOne2(allCourseList))
    }.catch {
        emit(getNeededClassList(getData()))
    }

    fun deleteCourseByName(courseName:String) = liveData(Dispatchers.IO){
        try {
            courseDao.deleteCourseByName(courseName)
            emit(true)
        } catch (e:Exception){
            emit(false)
        }
    }

    fun loadCourseByName2(courseName: String) = flow {
        val courseBeanList = courseDao.loadCourseByName(courseName)
        emit(courseBeanList.toList())
    }.catch {
        emit(listOf(
            CourseBean(
                campusName="五四路校区",
                classDay=3,
                classSessions=9, classWeek="111111111111110000000000", continuingSession=3,
                courseName=courseName,
                teacher="发生错误 ",
                teachingBuildName="发生错误 ",
                color="#f0c239")
            )
        )
    }.flowOn(Dispatchers.IO)

    suspend fun deleteCourseByList(courseBeanList: List<CourseBean>){
        courseDao.deleteCourse(courseBeanList)
    }


    fun loadCourseByNameAndStart2(courseName: String, courseStart: Int, whichColumn: Int) = flow {
        val result = courseDao.loadCourseByNameAndStart(courseName, courseStart, whichColumn)
        emit(result[0])
    }
        .catch {
            emit(CourseBean("",0,0,"011110",0,"异常查询","","",""))
        }
        .flowOn(Dispatchers.IO)

    fun getScore(user: String, password: String, id:String) = liveData(Dispatchers.IO) {
        try {
            if (user!="123456") {
                val scoreResponse = NormalScheduleNetwork.getScore(user, password, id)
                emit(scoreResponse)
            } else{
                val scoreResponse = ScoreResponse(
                    listOf(
                        Grade(
                            "培养方案",
                            listOf(
                                listOf(
                                    ThisProjectGrade("实例", "示例课程",90.0,"2021"),
                                    ThisProjectGrade("实例", "示例课程2",95.0,"2021")
                                )
                            )
                        ),
                    ),
                    "登陆成功"
                )
                emit(scoreResponse)
            }
        } catch (e: Exception){
            emit(null)
        }
    }

    fun getScoreDetail2(user: String, password: String, id:String) = fireFlow(Dispatchers.IO) {
        if (user!="123456") {
            val scoreDetailResponse = NormalScheduleNetwork.getScoreDetail(user, password, id)
            Result.success(scoreDetailResponse)
        } else{
            val a = ScoreDetail(
                listOf(
                    Grades("90","示例课程","培养方案", "90.0","5.0","90","99","20","5","88"),
                    Grades("92","示例课程2","培养方案", "90.0","4.0","92.2","95","10","10","95")
                ),
                result = "登陆成功"
            )
            Result.success(a)
        }
    }

    fun loadAllCourse3(): List<List<OneByOneCourseBean>>? {
        return try {
            Convert.courseBeanToOneByOne2(courseDao.loadAllCourseAs())
        } catch (e:Exception){
            null
        }
    }

    suspend fun insertCourse2(courseList: List<AllCourse>) {
        for (singleCourse in courseList) {
            try {
                courseDao.insertCourse(Convert.courseResponseToBean(singleCourse))
            } catch (e:Exception){

            }
        }
    }

    suspend fun insertCourse(courseBean:CourseBean) {
        try {
            courseDao.insertCourse(courseBean)
        } catch (e:Exception){

        }
    }

    suspend fun insertCourse(courseBean:List<CourseBean>) {
        try {
            courseDao.insertCourse(courseBean)
        } catch (e:Exception){

        }
    }

    suspend fun deleteAllCourseBean2(){
        try {
            courseDao.deleteAllCourseBean()
        } catch (e:Exception){
//            Result.failure<Exception>(e)
        }
    }


    suspend fun updateBackground(background: UserBackgroundBean) {
        return try {
            backgroundDao.insertBackground(background)
        } catch (e:Exception){
            backgroundDao.updateBackground(background)
        }
    }

    fun loadBackground() = liveData(Dispatchers.IO){
        val resources = NormalScheduleApplication.context.resources
        val resourceId = R.drawable.main_background_4 // r.mipmap.yourmipmap; R.drawable.yourdrawable
        val uriBeepSound = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(resourceId))
            .appendPath(resources.getResourceTypeName(resourceId))
            .appendPath(resources.getResourceEntryName(resourceId))
            .build()
        try {
            val a = backgroundDao.loadLastBackground()
            if (a.userBackground != "0"){
                emit(Uri.parse(a.userBackground))
            } else {
                emit(uriBeepSound)
            }
        } catch (e:Exception){
            emit(uriBeepSound)
        }
    }

    fun getSentences(force:Boolean = false) = flow {
        try {
            if (SentenceDao.isSentenceSaved() && !force){
                val resultList = SentenceDao.getSentences()
                emit(OneSentencesResponse(resultList, "local"))
            } else {
                val result = NormalScheduleNetwork.getSentences()
                SentenceDao.saveSentence(result.result)
                emit(result)
            }
        } catch (e:Exception){
            emit(null)
        }
    }

    fun loginToSpace(user: String, password: String, id: String) = flow {
        try {
            val result = NormalScheduleNetwork.loginToSpace(user, password, id)
            emit(result)
        } catch (e:Exception){
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

    fun getSpaceRooms(id: String, roomName:String, searchDate: String) = flow {
        val result = NormalScheduleNetwork.getSpaceRooms(id, roomName, searchDate)
        emit(result)
    }.catch {
        val errorRoom = Room("查询失败","0","00000000000","无")
        emit(SpaceResponse(roomList = listOf(errorRoom), roomName=roomName))
    }.flowOn(Dispatchers.IO)

    fun getSchoolBusTime(searchType:String) = flow {
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

    suspend fun addHomework(homeworkBean: HomeworkBean):Long{
        return homeworkDao.insertHomeWork(homeworkBean)
    }

    fun loadHomeworkByName(courseName:String) =
        flow {
            val result = homeworkDao.loadHomeworkByName(courseName)
            Log.d("Repo", "加载一次")
            emit(result)
        }
        .flowOn(Dispatchers.IO)
        .catch {
            val init = HomeworkBean(id=999, "未查询到", "无", deadLine = 0, finished = false,createDate = 0)
            emit(listOf(init))
        }

    fun getNewBeanInit(courseName: String)  = flow {
        val lastId = homeworkDao.getLastId()
        Log.d("Repo", "NewBean加载一次")
        val newId = lastId + 1
        emit( HomeworkBean(
            id = newId,
            courseName = courseName,
            workContent = "",
            createDate = GetDataUtil.getDayMillis(0),
            deadLine = GetDataUtil.getDayMillis(7),
            finished = false
        ))

    }
        .catch {
            emit( HomeworkBean(
                id = 0,
                courseName = courseName,
                workContent = "",
                createDate = GetDataUtil.getDayMillis(0),
                deadLine = GetDataUtil.getDayMillis(7),
                finished = false
            ))
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
            for (singleName in result){
                val homeworkList = homeworkDao.loadHomeworkByName(singleName)
                homeworkList.forEach {
                    if (!it.finished){
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
        }catch (e:Exception){
            "error"
        }
    }

    suspend fun deleteHomeworkById(id: Int):String{
        return try {
            homeworkDao.deleteHomeworkById(id)
            "success"
        }catch (e:Exception){
            "error"
        }
    }

    fun getScheduleSettings() = AccountDataDao.scheduleSettings

    suspend fun updateMode(mode:Int):Int{
        return try {
            AccountDataDao.updateColorMode(mode = mode)
            0
        } catch (e:Exception) {
            1
        }
    }

    fun getShowDarkBack() = AccountDataDao.getDarkShowBack()

    suspend fun updateShowDarkBack(show:Boolean):Int{
        return try {
            AccountDataDao.updateDarkShowBack(show)
            0
        } catch (e:Exception){
            1
        }
    }

    suspend fun updateSettings(setSettings:(settings:Settings)->Settings){
        AccountDataDao.updateSettings {
            setSettings(it)
        }
    }

    // 200正常，300服务端返回异常，301未连接，302未登陆
    fun getUserType() = flow {
        val user = getSavedAccount()["user"]
        if (user != null){
            val res = NormalScheduleNetwork.getUserType(user)
            emit(res)
        }else{
            emit(UserTypeResponse(statusCode = 302, content = "您未登陆"))
        }
    }.catch {
        emit(UserTypeResponse(statusCode = 301, content = "未知，无法链接服务器"))
    }.flowOn(Dispatchers.IO)

    fun getNewCourse(userNumber: String) = flow {
        val res = NormalScheduleNetwork.getNewCourse(userNumber)
        emit(res)
    }.flowOn(Dispatchers.IO)

    fun gotNewCourse(userNumber: String, pk:Int) = flow {
        val res = NormalScheduleNetwork.gotNewCourse(userNumber, pk)
        emit(res)
    }
        .catch {
            emit(GotResponse(301))
        }
        .flowOn(Dispatchers.IO)

    fun uploadNewCourse(userNumber: String, userCode:String, beanListStr:String) = flow {
        val res  = NormalScheduleNetwork.uploadNewCourse(
            userNumber, userCode, beanListStr
        )
        emit(res)
    }.flowOn(Dispatchers.IO)

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
            liveData<Result<T>>(context) {
                val result = try {
                    block()
                } catch (e: Exception) {
                    Result.failure<T>(e)
                }
                emit(result)
            }

    private fun <T> fire2(context: CoroutineContext, block: suspend () -> T? ) =
        liveData<T?>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                emit(null)
            }
        }

    private fun <T> fireFlow(context: CoroutineContext, block: suspend () -> Result<T>) =
        flow {
            val result = try {
                block()
            } catch (e: Exception){
                Result.failure<T>(e)
            }
            emit(result)
        }.flowOn(context)

    fun saveAccount(user: String, password: String) = AccountDao.saveAccount(user, password)
    fun getSavedAccount() = AccountDao.getSavedAccount()
    fun isAccountSaved() = AccountDao.isAccountSaved()
    fun importAgain() = AccountDao.importedAgain()
    fun saveLogin() = AccountDao.saveLogin()

    suspend fun saveUserVersion(version:Int = 1) = AccountDataDao.saveUserVersion(version)
    fun getNewUserOrNot() = AccountDataDao.getNewUserOrNot()
}

