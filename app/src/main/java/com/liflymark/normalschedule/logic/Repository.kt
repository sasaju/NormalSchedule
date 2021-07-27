package com.liflymark.normalschedule.logic

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.liveData
import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.bean.UserBackgroundBean
import com.liflymark.normalschedule.logic.bean.getData
import com.liflymark.normalschedule.logic.dao.AccountDao
import com.liflymark.normalschedule.logic.dao.AppDatabase
import com.liflymark.normalschedule.logic.dao.SentenceDao
import com.liflymark.normalschedule.logic.model.*
import com.liflymark.normalschedule.logic.network.NormalScheduleNetwork
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.ui.show_timetable.getNeededClassList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object  Repository {

    private val dataBase = AppDatabase.getDatabase(NormalScheduleApplication.context)
    private val courseDao = dataBase.courseDao()
    private val backgroundDao = dataBase.backgroundDao()

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

    fun getCaptcha(sessionId: String) =  liveData(Dispatchers.IO) {
        try {
            val img = NormalScheduleNetwork.getCaptcha(sessionId).bytes()
            val imgStream = BitmapFactory.decodeByteArray(img, 0, img.size)
            emit(imgStream)
        } catch (e: Exception){
            emit(null)
        }
    }

    fun getCourse(user:String, password:String, yzm:String, headers:String) = fire(Dispatchers.IO) {
        val courseResponse = NormalScheduleNetwork.getCourse(user, password, yzm, headers)
        Result.success(courseResponse)
    }

    fun getCourse(user: String, password: String) = fire(Dispatchers.IO){
        val courseResponse = NormalScheduleNetwork.getCourse(user, password)
        Result.success(courseResponse)
    }

    fun getCourse2(user:String, password:String, yzm:String, headers:String) = liveData(Dispatchers.IO){
        try {
            val courseResponse = NormalScheduleNetwork.getCourse(user, password, yzm, headers)
            emit(courseResponse)
        } catch (e: Exception){
            emit(null)
        }

    }

    fun getCourse2(user: String, password: String) = liveData(Dispatchers.IO){
        try {
            val courseResponse = NormalScheduleNetwork.getCourse(user, password)
            emit(courseResponse)
        } catch (e: Exception){
            emit(null)
        }
    }

    fun getVisitCourse() = fire(Dispatchers.IO){
        val courseResponse = NormalScheduleNetwork.getVisitCourse()
        Result.success(courseResponse)
    }

    fun insertCourse(courseList: List<AllCourse>) = liveData(Dispatchers.IO) {
        try {
            for (singleCourse in courseList) {
                courseDao.insertCourse(Convert.courseResponseToBean(singleCourse))
                // Log.d("Repository", Convert().courseResponseToBean(singleCourse).toString())
            }
            emit(true)
        } catch (e: Exception){
            emit(false)
        }

    }

    //    fun loadAllCourse() = fire(Dispatchers.IO){
//        Result.success(courseDao.loadAllCourse())
//    }
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

    fun loadCourseByName(courseName: String) = fire(Dispatchers.IO){
        val courseList = courseDao.loadCourseByName(courseName)
        Result.success(courseList)
    }

    fun loadCourseByName2(courseName: String) = flow<List<CourseBean>> {
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

    fun deleteAllCourseBean() = liveData(Dispatchers.IO){
        courseDao.deleteAllCourseBean()
        emit("0")
    }

    suspend fun deleteCourseByList(courseBeanList: List<CourseBean>){
        courseDao.deleteCourse(courseBeanList)
    }

    fun loadCourseByNameAndStart(courseName: String, courseStart: Int, whichColumn: Int) = fireFlow(Dispatchers.IO){
        val result = courseDao.loadCourseByNameAndStart(courseName, courseStart, whichColumn)
        Result.success(result)
    }


    fun getScore(user: String, password: String, id:String) = liveData(Dispatchers.IO) {
        try {
            val scoreResponse = NormalScheduleNetwork.getScore(user, password, id)
            emit(scoreResponse)
        } catch (e: Exception){
            emit(null)
        }
    }

    fun getScoreDetail(user: String, password: String, id:String) = liveData(Dispatchers.IO) {
        try {
            val scoreDetailResponse = NormalScheduleNetwork.getScoreDetail(user, password, id)
            emit(scoreDetailResponse)
        } catch (e: Exception){
            emit(null)
        }
    }

    fun getScoreDetail2(user: String, password: String, id:String) = fireFlow(Dispatchers.IO) {
        val scoreDetailResponse = NormalScheduleNetwork.getScoreDetail(user, password, id)
        Result.success(scoreDetailResponse)
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

    suspend fun deleteCourseByNameAndStart(): Int {
        return try {
            courseDao.deleteAllCourseBean()
            0
        } catch (e:Exception){
            1
        }
    }



    suspend fun insertBackground(background: UserBackgroundBean) = backgroundDao.insertBackground(background)

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

    fun loadBackground2() = flow {
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
            emit(Uri.parse("0"))
        }
    }

    suspend fun deleteBackground(background: UserBackgroundBean) = backgroundDao.deleteAllBackground(background)

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

    fun getSpaceRooms(id: String, roomName:String, searchDate: String) = flow {
        val result = NormalScheduleNetwork.getSpaceRooms(id, roomName, searchDate)
        emit(result)
    }.catch {
        val errorRoom = Room("查询失败","0","00000000000","无")
        emit(SpaceResponse(roomList = listOf(errorRoom), roomName=roomName))
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

    fun saveUserVersion() = AccountDao.newUserShowed()
    fun getNewUserOrNot() = AccountDao.getNewUserOrNot()
}

