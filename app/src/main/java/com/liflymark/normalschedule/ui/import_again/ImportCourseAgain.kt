package com.liflymark.normalschedule.ui.import_again

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import kotlinx.coroutines.CoroutineScope

class ImportCourseAgain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun finish() {
        Repository.saveLogin()
        super.finish()
    }
}