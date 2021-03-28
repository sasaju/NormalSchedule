package com.liflymark.icytimetable


/*
       Helper class for gapFilling
 */
class IcyTimeTableHelper {
    companion object {
        private const val TAG = "IcyTimeTableHelper"
        fun gapFilling(
            dataIn: List<IcyTimeTableManager.BaseCourse>,
            totalCoursePerDay: Int,
            columnCount: Int
        )
        //每天的课程总数     列数
                : List<IcyTimeTableManager.BaseCourse> {  //填充空白
            if (dataIn.isEmpty())
                return dataIn
            val list = ArrayList<IcyTimeTableManager.BaseCourse>()
            val sorted = dataIn.sortedBy { it.start }

            (0 until columnCount).forEach { nowColumn -> //从第0列到columnCount-1列
                val thisColumnData = sorted.filter { it.whichColumn == nowColumn } //此列的数据
                if (thisColumnData.isEmpty()){  //特殊情况  此天都没有课
                    list.add(
                        IcyTimeTableManager.EmptyCourseInfo(
                            0,totalCoursePerDay,nowColumn
                        )
                    )
                    return@forEach
                }
                thisColumnData.forEachIndexed { index, t ->

                    // 第一种边界情况    上边界
                    if (index == 0 && t.start > 0) {
                        list.add(
                            IcyTimeTableManager.EmptyCourseInfo(
                                0, t.start, t.whichColumn
                            )
                        )
                    }


                    list.add(t)

                    // 第二种边界情况    下边界
                    if (index == thisColumnData.size - 1 && t.end != totalCoursePerDay) {
                        list.add(
                            IcyTimeTableManager.EmptyCourseInfo(
                                t.end, totalCoursePerDay, t.whichColumn
                            )
                        )
                    }

                    // 中间填充
                    val nextT = thisColumnData.getOrNull(index + 1) ?: return@forEach
                    if (t.end != nextT.start) {
                        list.add(
                            IcyTimeTableManager.EmptyCourseInfo(
                                t.end, nextT.start, t.whichColumn
                            )
                        )
                    }
                }

            }
            return  list
        }
        fun getIcyRowInfo(data:List<IcyTimeTableManager.BaseCourse>):List<IcyRowTimeInfoDecoration.IcyRowInfo>{
            val list= mutableListOf<IcyRowTimeInfoDecoration.IcyRowInfo>()
            data.map { list.add(IcyRowTimeInfoDecoration.IcyRowInfo(it.start + 1)) }
            return list
        }
    }
}