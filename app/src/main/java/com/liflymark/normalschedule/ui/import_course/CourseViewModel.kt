package com.liflymark.normalschedule.ui.import_course

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.model.AllCourse
import kotlinx.coroutines.launch

class CourseViewModel: ViewModel() {
    private var id = ""
    private var user = ""
    private var password = ""
    private var yzm = ""
    private var formMapLiveData = MutableLiveData<String>()
    private var formMapNewLiveData = MutableLiveData<String>()
    private var formVisitLiveData = MutableLiveData<String>()
    private var getIdOrNotLiveData = MutableLiveData<Int>()
    private var getImageTimesLiveData = MutableLiveData<Int>()
    private var courseListLiveData = MutableLiveData<List<AllCourse>>()
    private var deleteCourseBean = MutableLiveData<Int>()
//    private var courseDatabaseLiveData = MutableLiveData(0)

    val idLiveData = Transformations.switchMap(getIdOrNotLiveData) {
        Repository.getId2()
    }
    val courseLiveData = Transformations.switchMap(formMapLiveData) { _ ->
        Repository.getCourse2(user, password, yzm, id)
    }
    val courseNewLiveData = Transformations.switchMap(formMapNewLiveData) { _ ->
        Repository.getCourse2(user, password)
    }
    val courseVisitLiveData = Transformations.switchMap(formVisitLiveData){
        Repository.getVisitCourse()
    }
    val imageLiveData = Transformations.switchMap(getImageTimesLiveData) {
        Repository.getCaptcha(id)
    }
    private var _accountLiveData = MutableLiveData<List<String>>()
    val accountLiveData = Transformations.map(_accountLiveData){ it }


    fun getId() {
        getIdOrNotLiveData.value = 1
        getImageTimesLiveData.value = 0
    }

    fun refreshId() {
        if (getIdOrNotLiveData.value!! >= 1){
            getIdOrNotLiveData.value = getIdOrNotLiveData.value!! +1
        } else {
            return
        }
    }

    fun getImage(id: String) {
        val temp = getIdOrNotLiveData.value?:0
        this.id = id
        getImageTimesLiveData.value = temp + 1
    }

    fun putValue(user: String, password: String, yzm: String, id: String) {
        this.user = user
        this.password = password
        this.id = id
        if (yzm == ""){
            this.yzm = "abcde"
        }else {
            this.yzm = yzm
        }
        formMapLiveData.value = user + password + yzm
    }

    fun putValue(user: String, password: String){
        this.user = user
        this.password = password
        formMapNewLiveData.value = user + password
    }

    fun putValue(){
        formVisitLiveData.value = "0000"
    }

    fun deleteAllCourseBean(){
        deleteCourseBean.value = deleteCourseBean.value?.plus(1)
    }


    fun saveAccount(user: String, password: String) = Repository.saveAccount(user, password)
    fun isAccountSaved() = Repository.isAccountSaved()

    fun getAccount(){
        viewModelScope.launch {
            val userAndPw = Repository.getSavedAccount()
            _accountLiveData.value = listOf(userAndPw["user"]!!, userAndPw["password"]!!)
        }
    }
}