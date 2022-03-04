package com.liflymark.normalschedule.ui.graduate_import

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.model.GraduateResponse
import com.liflymark.normalschedule.logic.model.GraduateWeb
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.util.concurrent.Flow

class GraduateImportViewModel:ViewModel() {
    private var loginCount = 0
    private val captcha = MutableStateFlow<Int>(0)
    var user = ""
    var password = ""
    private var yzm = ""
    var cookies = ""
    val loginWebVPNState = mutableStateOf<String?>(null)
    val loginURPState = mutableStateOf<GraduateResponse?>(null)
    val loginVPNorNot = mutableStateOf(false)
    val showWarning = mutableStateOf(true)
    val showCounter = flow {
        var sec = 9
        repeat(sec){
            kotlinx.coroutines.delay(1000L)
            sec -= 1
            emit(sec)
        }
    }


    @OptIn(FlowPreview::class)
    val captchaFlow = captcha.flatMapConcat {
        if (cookies!=""){
            Repository.getGraduateCaptcha(cookies)
        }else{
            flow { emit(null) }
        }
    }
//    @OptIn(FlowPreview::class)
//    val captchaFlow = captcha.flatMapConcat { Repository.getGraduateCaptcha(cookies) }

    suspend fun loginVPN(user:String, password:String){
        loginCount += 1
        this.user = user
        this.password = password
        if (user=="" || password==""){
            loginWebVPNState.value = "请输入账号密码$loginCount"
            return
        }
        val res = Repository.loginWebVPN(user, password).last()
        Log.d("GradusateViewmodel", res.toString())
        if (res.result=="登陆成功"){
            this.cookies = res.cookies
            loginVPNorNot.value = true
            getCaptcha()
        }
        loginWebVPNState.value = res.result + loginCount.toString()
    }

    suspend fun loginURP(yzm:String){
        this.yzm = yzm
        if (yzm!="")
            loginURPState.value = Repository.loginURP(user, password, yzm, cookies).last()
        else
            loginURPState.value = GraduateResponse(listOf(),"请输入验证码","no")
    }

    fun getCaptcha() {
        captcha.value += 1
    }

}