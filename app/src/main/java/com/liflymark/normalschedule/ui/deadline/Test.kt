package com.liflymark.normalschedule.ui.deadline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension


@Composable()
@Preview()
fun AndroidPreview_TODOCard() {
    Box(Modifier.size(640.dp, 640.dp)) {
        TODOCard()
    }
}

@Composable()
fun TODOCard() {
    Row(
        Modifier
            .clip(RoundedCornerShape(10.0.dp))
            .height(142.0.dp)
            .background(Color(0.8f, 0.9f, 1.0f, 1.0f))
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(47.0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(15.0.dp))
        Text("距离四六级考试还有", Modifier.wrapContentHeight(Alignment.Top).size(206.0.dp, 31.0.dp), style = LocalTextStyle.current.copy(color = Color.Black, textAlign = TextAlign.Left, fontSize = 0.0.sp))

        ConstraintLayout(modifier = Modifier.size(73.0.dp, 73.0.dp)) {
            val (Ellipse_1, a) = createRefs()
            Text(
                "188",
                Modifier.wrapContentHeight(Alignment.Top).constrainAs(a) {
                    start.linkTo(parent.start, 15.0.dp)
                    top.linkTo(parent.top, 18.5.dp)
                    width = Dimension.value(42.0.dp)
                    height = Dimension.value(35.0.dp)
                }, style = LocalTextStyle.current.copy(color = Color(1.0f, 1.0f, 1.0f, 1.0f), textAlign = TextAlign.Left, fontSize = 28.0.sp))

        }
        Text("天", Modifier.wrapContentHeight(Alignment.Top).size(18.0.dp, 23.0.dp), style = LocalTextStyle.current.copy(color = Color(0.35f, 0.35f, 0.35f, 1.0f), textAlign = TextAlign.Left, fontSize = 18.0.sp))

        Spacer(modifier = Modifier.width(15.0.dp))
    }
}