package com.liflymark.normalschedule.ui.login_space_room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.model.Room
import com.liflymark.normalschedule.logic.model.SpaceResponse
import com.liflymark.normalschedule.logic.utils.GetDataUtil

class ShowSpaceViewModel:ViewModel() {
    private val spaceDateLiveData = MutableLiveData("")
    private var ids = ""
    private var roomName = ""
    private var searchDate = ""
    private val errorRoom = Room("未查询","0","00000000000","无")
    val initialSpace = SpaceResponse(roomList = listOf(errorRoom), roomName=roomName)
    val spaceResult = Transformations.switchMap(spaceDateLiveData){
        Repository.getSpaceRooms(ids, roomName, searchDate).asLiveData()
    }

    fun getSpaceRoom(id: String, roomName:String, searchDate: String)  {
        this.ids = id
        this.roomName = roomName
        this.searchDate = searchDate
        spaceDateLiveData.value = id + roomName + searchDate
    }

    fun getThreeDay() = GetDataUtil.getThreeDay()

}