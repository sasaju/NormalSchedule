package com.liflymark.normalschedule.logic

import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.logic.Repository.getCourse
import com.liflymark.normalschedule.logic.network.CourseService
import com.liflymark.normalschedule.logic.network.NormalScheduleNetwork
import com.liflymark.normalschedule.logic.network.NormalScheduleNetwork.getCourse
import kotlinx.coroutines.delay

suspend fun main(){
    // val a = NormalScheduleNetwork.getId()
    val a = NormalScheduleNetwork.getCourse("20191801075", "fei123698745", "", "adbylxBkvTCS-fpckcODx")
    delay(200)
    print(a)
}