package com.liflymark.icytimetable

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log

fun Canvas.drawTextAtTop(text: String, rect: Rect, paint: Paint) { //顶部draw
    val baseX = rect.centerX().toFloat() - paint.measureText(text) / 2f
    val textBounds = Rect().apply { paint.getTextBounds(text, 0, text.length - 1, this) }
    val baseY = rect.top+(2 *
            if (textBounds.height() != 0) textBounds.height().toFloat()
            else -(paint.fontMetrics.ascent + paint.fontMetrics.descent)).toFloat()
    drawText(text, baseX, baseY, paint)
}

fun Canvas.drawTextAtBottom(text: String, rect: Rect, paint: Paint) { //底部draw
    val baseX = rect.centerX().toFloat() - paint.measureText(text) / 2f
    val textBounds = Rect().apply { paint.getTextBounds(text, 0, text.length - 1, this) }
    val baseY = rect.bottom - (
            if (textBounds.height() != 0) textBounds.height().toFloat()
            else -(paint.fontMetrics.ascent + paint.fontMetrics.descent)).toFloat()
    drawText(text, baseX, baseY, paint)
}
fun Canvas.drawTextAtCenter(text: String, rect: Rect, paint: Paint) {
    val baseX = rect.centerX().toFloat() - paint.measureText(text) / 2f
    val textBounds = Rect().apply { paint.getTextBounds(text, 0, text.length - 1, this) }
    val baseY = rect.centerY() +
            if (textBounds.height() != 0) textBounds.height() / 2f
            else -(paint.fontMetrics.ascent + paint.fontMetrics.descent) / 2f
    drawText(text, baseX, baseY, paint)
}
val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()