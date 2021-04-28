package com.liflymark.icytimetable

import android.util.Log
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.View
import androidx.core.util.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

class IcyTimeTableManager(
        private val perCourseTime: Int,//每节课的时间
        private val perCourseHeight: Int,// 每节课的高度
        private val columnCount: Int,//列数
        private val totalCoursePerDay: Int,//每天的课程有多少节     从0开始 假设此值为2    则有 0-2 (0-1 , 1-2 )  两节课
        private val computeCourseInfo: (position: Int) -> BaseCourse
) : RecyclerView.LayoutManager() {
    abstract class BaseCourse {
        abstract val start: Int
        abstract val end: Int
        abstract val whichColumn: Int
    }
    //base class
    data class EmptyCourseInfo(
            override val start: Int,
            override val end: Int,
            override val whichColumn: Int
    ) : BaseCourse()// place holder

    private data class CourseInfo(
            override val start: Int,
            override val end: Int,
            override val whichColumn: Int,
            val adapterPosition: Int, // position in adapter
            val positionInColumn: Int // position in column (column = whichColumn)
    ) : BaseCourse()

    companion object {
        private const val TAG = "IcyTimeTableManager"
        private const val TIME_NOT_DEFINED = -1
    }

    private var columnWidth = 0 // 每列的宽度大小
    private val courses = ArrayList<CourseInfo>()  //所有课程信息 包含在adapter中的位置  每列中的位置
    private val columns = SparseArray<ArrayList<CourseInfo>>() //每一列(每一天)所对应的课程
    private fun CourseInfo.totalTime(): Int = (end - start) * perCourseTime //课程总时间
    private fun CourseInfo.height(): Int = (end - start) * perCourseHeight
    private val top = SparseIntArray() //key = 列  value = 最顶部的item在adapter中的position
    private val bottom = SparseIntArray() //key = 列 value = 最底部的item在adapter中的position
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        )

    override fun collectAdjacentPrefetchPositions(dx: Int, dy: Int, state: RecyclerView.State?, layoutPrefetchRegistry: LayoutPrefetchRegistry?) {
        super.collectAdjacentPrefetchPositions(dx, dy, state, layoutPrefetchRegistry)
    }



    private fun resetInternal() {
        top.clear()
        bottom.clear()
    }

    private fun resetData() {
        courses.clear()
        columns.clear()
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            resetData()
            return
        }


        columnWidth = (width - paddingLeft - paddingRight) / columnCount

        calculateColumns()

        resetInternal()
        detachAndScrapAttachedViews(recycler)


        val offsetY = paddingTop
        var offsetX = paddingLeft
        for (i in 0 until columnCount) {
            offsetX += fillColumn(
                    columns[i].first(),
                    offsetX, offsetY, recycler
            )

            if (offsetX > parentRight.value)  //break退出 已经填充完可以看见的位置了
                break
        }
    }

    private val parentRight = lazy { width - paddingRight }
    private val parentBottom = lazy { height - paddingBottom }
    private fun fillColumn(
            startCourseInfo: CourseInfo,
            offsetX: Int,
            startY: Int,
            recycler: RecyclerView.Recycler
    ): Int {
        var offsetY = startY
        val nowColumn = startCourseInfo.whichColumn//当前所在的行
        val courses = columns[nowColumn] ?: return 0

        for (i in startCourseInfo.positionInColumn until courses.size) {
            val courseInfo = courses[i]
            val (width, height) = fillSingleCourse(courseInfo, offsetX, offsetY, Direction.NONE, recycler)
            offsetY += height
            if (i == startCourseInfo.positionInColumn)
                top.put(nowColumn, courseInfo.adapterPosition) //更新顶部信息
            bottom.put(nowColumn, courseInfo.adapterPosition) //更新底部信息
            if (offsetY > parentBottom.value)  //break退出 已经填充完可以看见的位置了
                break
        }
        return columnWidth
    }

    private fun fillSingleCourse(
            courseInfo: CourseInfo,
            offsetX: Int,
            offsetY: Int,
            direction: Direction,
            recycler: RecyclerView.Recycler
    ): Pair<Int, Int> {
        val view = recycler.getViewForPosition(courseInfo.adapterPosition)//从recycler中获取view
        addView(view)
        measureChild(view, courseInfo)
        
        val width = getDecoratedMeasuredWidth(view)
        val height = getDecoratedMeasuredHeight(view)
        val left = offsetX
        val top = if (direction == Direction.TOP) offsetY - height else offsetY
        val right = left + width
        val bottom = top + height
        layoutDecorated(view, left, top, right, bottom)
        return width to height
    }

    private fun measureChild(view: View, courseInfo: CourseInfo) {
        val layoutParams = view.layoutParams as RecyclerView.LayoutParams
        layoutParams.width = columnWidth
        layoutParams.height = courseInfo.height()
        Log.d(TAG, "measureChild: ${layoutParams.leftMargin} ${layoutParams.rightMargin}")
        //val insets = Rect().apply { calculateItemDecorationsForChild(view, this) }
        val widthSpec = getChildMeasureSpec(
                width, widthMode, paddingLeft + paddingRight,
                layoutParams.width,
                canScrollHorizontally()
        )
        val heightSpec = getChildMeasureSpec(
                height, heightMode, paddingTop + paddingBottom,
                layoutParams.height,
                canScrollVertically()
        )

        view.measure(widthSpec, heightSpec)
    }

    private fun calculateColumns() {
        resetData()
        (0 until itemCount).forEach {
            val baseCourse = computeCourseInfo(it)
            var column = columns.get(baseCourse.whichColumn)
            if (column == null) {
                column = ArrayList()
                columns.put(baseCourse.whichColumn, column)
            }

            val courseInfo = CourseInfo(
                    baseCourse.start,
                    baseCourse.end,
                    baseCourse.whichColumn,
                    it,
                    column.size
            )
            column.add(courseInfo)
            courses.add(courseInfo)
        }
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun scrollVerticallyBy(
            dy: Int,
            recycler: RecyclerView.Recycler,
            state: RecyclerView.State
    ): Int {
        if (dy == 0) return 0
        val realDy = calculateVerticallyScrollAmount(dy)
        if (realDy == 0) return 0
        offsetChildrenVertical(-realDy)
        if (realDy > 0) {
            recycleTop(recycler)

            bottom.forEach { nowColumn, position ->
                val view = findViewByPosition(position) ?: return@forEach
                val bottom = getDecoratedBottom(view)
                if (bottom < parentBottom.value) {
                    val left = getDecoratedLeft(view)
                    val course = courses.getOrNull(position) ?: return@forEach
                    val next =
                        columns[nowColumn].getOrNull(course.positionInColumn + 1) ?: return@forEach
                    addCourseToColumn(next, left, bottom, true, recycler)
                }
            }
        }else{ ///向上滑
            recycleBottom(recycler)
            top.forEach { nowColumn, position ->
                val view = findViewByPosition(position) ?: return@forEach
                val top = getDecoratedTop(view)
                if (top > paddingTop) {
                    val left = getDecoratedLeft(view)
                    val course = courses.getOrNull(position) ?: return@forEach
                    val prev =
                        columns.get(nowColumn).getOrNull(course.positionInColumn - 1) ?: return@forEach
                    addCourseToColumn(prev, left, top, false, recycler)
                }

            }
        }
        return  realDy
    }

    override fun collectInitialPrefetchPositions(adapterItemCount: Int, layoutPrefetchRegistry: LayoutPrefetchRegistry?) {
        super.collectInitialPrefetchPositions(adapterItemCount, layoutPrefetchRegistry)
//        var targetPos: Int = anchorPos
//        for (i in 0 until adapterItemCount) {
//            if (targetPos in 0 until adapterItemCount) {
//                layoutPrefetchRegistry!!.addPosition(targetPos, 0)
//            } else {
//                break // no more to prefetch
//            }
//            targetPos += direction
//        }
    }




    enum class Direction {
        TOP,
        BOTTOM,
        NONE
    }

    private fun addCourseToColumn(
            startCourse: CourseInfo,
            offsetX: Int,
            startY: Int,
            isAppend: Boolean,
            recycler: RecyclerView.Recycler
    ): Int {
        val column = columns[startCourse.whichColumn] ?: return 0
        val direction = if (isAppend) Direction.BOTTOM else Direction.TOP//填充位置
        var offsetY = startY
        val fillRange =
            if (isAppend) startCourse.positionInColumn until column.size
            else startCourse.positionInColumn downTo 0
        for (i in fillRange) {
            val course = column[i]
            val (width, height) = fillSingleCourse(course, offsetX, offsetY, direction, recycler)
            if (isAppend) { //向下滑动
                bottom.put(course.whichColumn, course.adapterPosition)
                offsetY += height
                if (offsetY > parentBottom.value) return offsetY - startY
            } else { //向上滑动
                top.put(course.whichColumn, course.adapterPosition)
                offsetY -= height
                if (offsetY < paddingTop) return startY - offsetY
            }
        }
        return (offsetY - startY).absoluteValue
    }

    private fun recycleTop(recycler: RecyclerView.Recycler) {
        
        (0 until this.columnCount).forEach outer@{ nowColumn ->
            val column = columns[nowColumn]
            val top = courses[top[nowColumn]]
            val bottom = courses[bottom[nowColumn]]
            column.subList(top.positionInColumn, bottom.positionInColumn).forEach ha@{ course ->
                val view = findViewByPosition(course.adapterPosition) ?: return@ha
                if (getDecoratedBottom(view) >= paddingTop) return@outer
                removeAndRecycleView(view, recycler)
                val next = column[course.positionInColumn + 1].adapterPosition
                this.top.put(nowColumn, next)
            }
        }
    }

    private fun recycleBottom(recycler: RecyclerView.Recycler) {
        (0 until columnCount).forEach outer@ { nowColumn ->
            val column = columns[nowColumn]
            val top = courses[top[nowColumn]]
            val bottom = courses[bottom[nowColumn]]
            column.subList(top.positionInColumn, bottom.positionInColumn + 1).asReversed()
                .forEach ha@{ course ->
                    val view = findViewByPosition(course.adapterPosition) ?: return@ha
                    if (getDecoratedTop(view) <= parentBottom.value) return@outer
                    removeAndRecycleView(view, recycler)
                    val pre = column[course.positionInColumn - 1].adapterPosition
                    this.bottom.put(nowColumn, pre)
                }
        }
    }


    private fun calculateVerticallyScrollAmount(dy: Int): Int {
        if (dy > 0) { //向下滑

            val bottomView = findBottomView() ?: return 0
            val course = courses.getOrNull(bottomView.adapterPosition) ?: return 0
            val bottom = getDecoratedBottom(bottomView)
            return if (course.end == totalCoursePerDay)
                if (bottom == parentBottom.value) 0
                else min(dy, bottom - parentBottom.value)
            else
                dy
        } else {
            val topView = findTopView() ?: return 0
            val course = courses.getOrNull(topView.adapterPosition) ?: return 0
            val top = getDecoratedTop(topView)
            return if (course.start == 0)
                if (top == paddingTop) 0
                else max(top - paddingTop, dy)
            else dy
        }
    }

    private fun findBottomView(): View? {//找到最底部的的view
        var maxBottom: Int? = null
        var maxView: View? = null
        bottom.forEach { _, adapterPosition ->
            val view = findViewByPosition(adapterPosition) ?: return@forEach
            val bottom = getDecoratedBottom(view)
            if (maxView == null) {
                maxView = view
                maxBottom = bottom
                return@forEach
            }
            maxBottom?.let {
                if (bottom > it) {
                    maxView = view
                    maxBottom = bottom
                }
            }
        }
        return maxView
    }

    private fun findTopView(): View? { //找到最顶部的view
        var minTop: Int? = null
        var minView: View? = null
        top.forEach { _, position ->
            val view = findViewByPosition(position) ?: return@forEach
            val top = getDecoratedTop(view)
            if (minView == null) {
                minView = view
                minTop = top
                return@forEach
            }
            minTop?.let {
                if (top < it) {
                    minView = view
                    minTop = top
                }
            }
        }
        return minView
    }

    private inline val View.adapterPosition
        get() = (layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
}