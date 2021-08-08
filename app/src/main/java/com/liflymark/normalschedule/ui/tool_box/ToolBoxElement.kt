package com.liflymark.normalschedule.ui.tool_box

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow


@Composable
fun SinglePart(titleIcon:ImageVector, titleName:String, content: @Composable () -> Unit){
    val mainColor = Color.Gray
    Card(elevation = 2.dp, modifier = Modifier
        .fillMaxWidth()
        .padding(3.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            Row {
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    titleIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = mainColor
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = titleName, fontSize = 12.sp, color = mainColor)
                Spacer(modifier = Modifier.height(10.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisAlignment = FlowMainAxisAlignment.Center,
                lastLineMainAxisAlignment = FlowMainAxisAlignment.SpaceAround,
                mainAxisSpacing = 2.dp,
                crossAxisSpacing = 2.dp
            ) {
                content()
            }

        }
    }
}



@Composable
fun SingleButton(icon:ImageVector,description: String,onClick:()->Unit){
    Surface(shape = RoundedCornerShape(4.dp)) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(85.dp)
                .padding(2.dp)
                .clickable { onClick() }
        ) {
            Icon(
                icon, contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = description)
        }
    }
}

@Composable
fun CenterCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    border: BorderStroke? = null,
    elevation: Dp = 1.dp,
    content: @Composable () -> Unit
){
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
        Surface(
            modifier = modifier,
            shape = shape,
            color = backgroundColor,
            contentColor = contentColor,
            elevation = elevation,
            border = border,
            content = content
        )
    }
}


//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview9() {
//    NorScTheme {
//        Column(modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())) {
//            SinglePart(
//                titleIcon = Icons.Default.Message,
//                titleName = "开发者公告"
//            ) {
//                SingleButton(Icons.Filled.StickyNote2, "公告栏"){
//
//                }
//            }
//
//            SinglePart(
//                titleIcon = Icons.Default.EmojiSymbols,
//                titleName = "校内生活"
//            ) {
//                SingleButton(Icons.Filled.CalendarToday, "校历") {
//
//                }
//                SingleButton(Icons.Filled.DirectionsBus, "校车时刻表") {
//
//                }
//                SingleButton(Icons.Filled.LunchDining, "作息时刻表") {
//
//                }
//            }
//
//            SinglePart(
//                titleIcon = Icons.Default.Fastfood,
//                titleName = "吃喝玩乐"
//            ) {
//                SingleButton(Icons.Filled.ShoppingBag, "购物指南") {
//
//                }
//                SingleButton(Icons.Filled.FoodBank, "美食指南") {
//
//                }
//            }
//        }
//    }
//}