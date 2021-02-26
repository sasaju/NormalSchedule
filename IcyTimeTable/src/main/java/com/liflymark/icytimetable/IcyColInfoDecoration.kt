package com.liflymark.icytimetable
import android.content.res.Resources
import android.graphics.*
import android.util.Log
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

abstract class IcyColInfoDecoration(
    private val columnCount: Int, //列数    若输入为7 有7列     0-6列有值
    private val height: Int, //列信息的高度
    private val colTextColor: Int,
    private val selectedTextColor: Int,
    private val selectedBackGroundColor: Int, // 选中背景颜色
    private val colTextSize: Float,
    private val backGroundColor: Int = Color.WHITE,
    private val position: Int
) : RecyclerView.ItemDecoration() {

    private val textPaint = Paint().apply {
        color = colTextColor
        isAntiAlias = true
        textSize = colTextSize
    }
    private val selectedTextPaint = Paint().apply {
        color = selectedTextColor
        isAntiAlias = true
        textSize = colTextSize
    }
    private val backGroundPaint = Paint().apply {
        color = backGroundColor
        isAntiAlias = true
    }
    private val selectedBackGroundPaint = Paint().apply {
        color = selectedBackGroundColor
        isAntiAlias = true
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        c.drawRect(Rect(0, 0, parent.width, height), backGroundPaint)
        if (parent.childCount < 0) return
        val leftView = parent.children.minBy { it.left } ?: return
        val columnWidth = leftView.width//宽度
        val top = 0
        val bottom = height

        val leftUp = leftView.left - columnWidth / 3
        val right_ = leftUp + 10
        val rect_ = Rect(leftUp, top, right_, columnWidth / 2)
        c.drawTextAtCenterUp(getDayOfDate(0), rect_, textPaint)
        c.drawTextAtCenterDown("月", rect_, textPaint)

        for (i in 1..columnCount) {
            val left = leftView.left + (i - 1) * columnWidth
            val right = left + columnWidth
            val rect = Rect(left, top, right, bottom)
            if (isSelected(i, position+1)) { // selected
                val rx = rect.width() / 6f
                val ry = rect.height() / 6f
                val roundRect =
                    RectF(left.toFloat(), top.toFloat() + 28.dp, right.toFloat(), bottom.toFloat() - 28.dp)
                c.drawRoundRect(roundRect, rx, ry, selectedBackGroundPaint)
                c.drawTextAtTop(getDayOfWeek(i), rect, selectedTextPaint)
                c.drawTextAtBottom(
                    getDayOfDate(i),
                    rect,
                    selectedTextPaint
                )
            } else {
                c.drawTextAtTop(getDayOfWeek(i), rect, textPaint)
                c.drawTextAtBottom(getDayOfDate(i), rect, textPaint)
            }
        }

    }

    abstract fun isSelected(nowColumn: Int, whichWeek: Int): Boolean //当前列是否被选中
    open fun getDayOfWeek(nowColumn: Int): String {// 当前列是星期几  星期一,星期二....
        return when (nowColumn) {
            1 -> "一"
            2 -> "二"
            3 -> "三"
            4 -> "四"
            5 -> "五"
            6 -> "六"
            7 -> "日"
            else -> "一"
        }
    }

    abstract fun getDayOfDate(nowColumn: Int): String // 当前列的日期 11,12,13......


    companion object {
        private const val TAG = "IcyColInfoDecoration"
    }
}