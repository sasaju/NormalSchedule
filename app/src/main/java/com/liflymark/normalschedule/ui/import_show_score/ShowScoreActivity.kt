package com.liflymark.normalschedule.ui.import_show_score

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.utils.Convert
import com.zackratos.ultimatebarx.library.UltimateBarX
import com.zackratos.ultimatebarx.library.bean.BarConfig
import kotlinx.android.synthetic.main.activity_show_score.*
import kotlinx.android.synthetic.main.item_project_score.*
import kotlinx.android.synthetic.main.item_score.*
import kotlin.math.sin

class ShowScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = BarConfig.newInstance()          // 创建配置对象
            .fitWindow(true)                          // 布局是否侵入状态栏（true 不侵入，false 侵入）
            .color(Color.TRANSPARENT)                         // 状态栏背景颜色（色值）
            .light(true)
        setContentView(R.layout.activity_show_score)
        UltimateBarX.with(this)                       // 对当前 Activity 或 Fragment 生效
            .config(config)                           // 使用配置
            .applyStatusBar()                         // 应用到状态栏


        val allGradeListString = intent.getStringExtra("grade_list_string")?:""
        val allGradeList = Convert.jsonToAllGrade(allGradeListString)
        val allGradeListSize = allGradeList.size
        all_grade.removeAllViews()
        for (i in 0 until allGradeListSize){
            val singleProjectLayout = LayoutInflater.from(this).inflate(R.layout.item_project_score, all_grade, false)
            val showTermTextView = singleProjectLayout.findViewById<TextView>(R.id.show_term)
            showTermTextView.text = (allGradeList.size-i).toString()
            val oneProjectLayout = singleProjectLayout.findViewById<LinearLayout>(R.id.one_project_score)
            val singleProjectGrade = allGradeList[i]
            for (t in singleProjectGrade.thisProjectGradeList) {
                oneProjectLayout.removeAllViews()
                for (a in t) {
                    val singleScoreLayout =
                        LayoutInflater.from(this)
                            .inflate(R.layout.item_score, one_project_score, false)
                    val courseNameTextView =
                        singleScoreLayout.findViewById(R.id.course_name) as TextView
                    val courseTypeTextView =
                        singleScoreLayout.findViewById(R.id.course_type) as TextView
                    val scoreTextView =
                        singleScoreLayout.findViewById(R.id.course_score) as TextView

                    courseNameTextView.text = a.courseName
                    courseTypeTextView.text = a.attributeName
                    scoreTextView.text = a.score.toString()
                    all_grade.addView(singleScoreLayout)
                }
            }
        }



    }
}