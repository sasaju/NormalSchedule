package com.liflymark.normalschedule.ui.work_book

import androidx.lifecycle.*
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.HomeworkBean
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import kotlinx.coroutines.launch

class WorkBookViewModel:ViewModel() {
    val courseNameListFLow = Repository.loadWorkCourseName()
    val initCourseName = listOf("正在查询")

    fun courseWorkList(courseName:String) =
        Repository.loadHomeworkByName(courseName = courseName)

    val initWorkList:List<HomeworkBean> = listOf()

    fun newBeanInit(courseName: String) = Repository.getNewBeanInit(courseName)

    fun addNewInit(lastId: Int, courseName: String): HomeworkBean {
        val newId = lastId + 1
        return HomeworkBean(
            id = newId,
            courseName = courseName,
            workContent = "",
            createDate = GetDataUtil.getDayMillis(0),
            deadLine = GetDataUtil.getDayMillis(7),
            finished = false
        )
    }

    suspend fun insertWork(workList:List<HomeworkBean>){
        for (work in workList) {
            Repository.addHomework(work)
        }
    }

    suspend fun deleteWork(workId:Int){
        Repository.deleteHomeworkById(id = workId)
    }
}