package com.liflymark.normalschedule.ui.update_course

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.liflymark.normalschedule.logic.model.GetNewCourseResponse
import com.liflymark.normalschedule.logic.utils.Convert

// 调课页面-网络端课程显示界面
@Composable
fun NetworkCourse(
    modifier: Modifier,
    networkCourse: GetNewCourseResponse?,
    postCourse:(listString:List<String>) -> Unit
) {
    Column {
        if (networkCourse != null && networkCourse.content.isNotEmpty()) {
            SingleUserCourseCard(
                modifier = Modifier.fillMaxWidth(),
                apply = true,
                content = networkCourse.content[0]
            )
        } else {
            OnlyError(content = "您的同级专业班级中没有人上传哦！")
        }
    }
}

/**调课页面 - Card组件
 *
 */
@Composable
fun SingleUserCourseCard(
    modifier: Modifier,
    apply: Boolean,
    content: GetNewCourseResponse.Content,
){
    val beanList = Convert.StringToListBean(content.fields.bean_list_str)
    val courseNameList = beanList.map { it.courseName }.toSet()
    Card(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(0.8f)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                courseNameList.forEach {
                    NoSelectCourse(
                        onClick = {

                        },
                        text =it
                    )
                }
                if (courseNameList.isEmpty()){
                    OnlyError(content = "该用户已认证，但未上传课程")
                }
            }
            Column(
                modifier = Modifier
                    .weight(0.2f)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                TextButton(onClick = {

                }) {
                    Text(text = if(apply){ "取消应用" } else { "应用" })
                }
            }
        }
    }
}
