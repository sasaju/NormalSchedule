package com.liflymark.normalschedule.ui.tool_box

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.model.Bulletin
import com.liflymark.normalschedule.logic.model.DevBoardResponse
import com.liflymark.normalschedule.logic.model.SchoolBusResponse
import com.liflymark.normalschedule.logic.model.TimeList

class ToolBoxViewModel:ViewModel() {
    private val getBulletinValue = MutableLiveData<Int>(0)
    private val getBusTimeValue = MutableLiveData("now")

    val initialBulletin = DevBoardResponse(
        bulletin_list = listOf(Bulletin("admin", "正在链接作者的服务器", "????", "请等待")),
        status = "no"
    )
    val initialSchoolBus = SchoolBusResponse(
        nowDay = "error",
        timeList = TimeList(
            fiveToSeven = listOf(),
            sevenToFive = listOf()
        )
    )

    val bulletinsLiveData = Transformations.switchMap(getBulletinValue){
        Repository.getBulletin().asLiveData()
    }

    val busTimeLiveData = Transformations.switchMap(getBusTimeValue){
        Repository.getSchoolBusTime(it).asLiveData()
    }


    fun getBulletin(){
        getBulletinValue.value = getBulletinValue.value?.plus(1)
    }
    fun getBusTime(nowType:String){
        getBusTimeValue.value = nowType
    }

    fun getTypeToString(type:String):String{
        return when(type){
            "workday" -> "工作日"
            "weekday" -> "双休日"
            "holiday" -> "法定假日或寒暑假"
            else -> "未知"
        }
    }
}