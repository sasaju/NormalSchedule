package com.liflymark.normalschedule.ui.sign_in_compose

import android.util.Log
import androidx.compose.material.DrawerState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.Repository.getSavedAccount
import com.liflymark.normalschedule.logic.model.IdResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SignUIViewModel:ViewModel() {
    private val refreshGetId = MutableStateFlow<Int>(0)
    private val refreshAccount = MutableStateFlow(0)
    val userSate = mutableStateOf("")
    val passwordSate = mutableStateOf("")
    val idState = mutableStateOf("")

    init {
        viewModelScope.launch {
            idState.value = Repository.getId6().id
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val accountFlow:Flow<Map<String, String>> = refreshAccount.flatMapLatest {
        Log.d("SignUIViewModel", "emit account")
        flow {
            if(Repository.isAccountSaved()){
                emit(getSavedAccount())
            }else{
                emit(mapOf("user" to "", "password" to ""))
            }
        }.catch {
            emit(mapOf("user" to "", "password" to ""))
        }
    }
    
    val backgroundRoute = Repository.loadBackground()
    fun refreshId(){
        viewModelScope.launch {
            idState.value = Repository.getId6().id
        }
    }
}