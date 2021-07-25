package com.liflymark.normalschedule.ui.score_detail

import android.util.Log
import androidx.compose.ui.graphics.Path
import kotlin.math.cos
import kotlin.math.sin


fun starPath(x:Float,y:Float,r1:Float=60f, r2: Float=120f): Path {
    val path = Path()
    var x1=0f;var x2=0f;var y1=0f;var y2=0f
    val startx = r1 * cos( 18 / 180 * Math.PI).toFloat() + x
    val starty = - r1 * sin(18 / 180 * Math.PI).toFloat() + y
    path.moveTo(startx,starty)
    var a = 0
    for (i in 0..4) {
        x1 = r1 * cos((54 + a * 72) / 180f * Math.PI).toFloat() + x
        y1 = r1 * sin((54 + a * 72) / 180f * Math.PI).toFloat() + y
        x2 = r2 * cos((18 + a * 72) / 180f * Math.PI).toFloat() + x
        y2 = r2 * sin((18 + a * 72) / 180f * Math.PI).toFloat() + y
        Log.d("star", "x1=$x1 y1=$y1 x2=$x2 y2=$y2 i=$a")
        path.lineTo(x2,y2)
        path.lineTo(x1,y1)
        a += 1
    }

    path.close()
    return path
}