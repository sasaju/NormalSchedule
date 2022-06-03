package com.liflymark.normalschedule.ui.login_space_room

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.dao.AccountDao
import com.liflymark.normalschedule.logic.model.Room
import com.liflymark.normalschedule.logic.model.SpaceResponse
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
    val schoolNameState = mutableStateOf("五四路校区")
    val buildNameState = mutableStateOf("六教")
    init {
        viewModelScope.launch {
            val savedMap = Repository.readSpaceSelected().first()
            schoolNameState.value = savedMap[AccountDao.SCHOOL_KEY.name].toString()
            buildNameState.value = savedMap[AccountDao.BUILDING_KEY.name].toString()
        }
    }

    fun getSpaceRoom(id: String, roomName:String, searchDate: String)  {
        this.ids = id
        this.roomName = roomName
        this.searchDate = searchDate
        spaceDateLiveData.value = id + roomName + searchDate
    }

    fun getThreeDay() = GetDataUtil.getThreeDay()

}