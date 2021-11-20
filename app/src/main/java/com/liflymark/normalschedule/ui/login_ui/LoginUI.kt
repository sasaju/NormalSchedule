package com.liflymark.normalschedule.ui.login_ui

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.ui.theme.NorScTheme
import java.util.Collections.rotate

@OptIn(ExperimentalAnimationApi::class, coil.annotation.ExperimentalCoilApi::class)
@Composable
fun LoginHeader(){
    val primaryColor =  MaterialTheme.colors.primary
    val secondColor = MaterialTheme.colors.secondary
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawPath(
            path = headerLine(size),
            color = primaryColor
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.333F),
        contentAlignment = Alignment.Center,
    ){
        var visible by remember { mutableStateOf(true) }
        val density = LocalDensity.current
        AnimatedVisibility(
            visible = visible,
            enter =  slideInVertically(
                // Slide in from 40 dp from the top.
                initialOffsetY = { with(density) { -40.dp.roundToPx() } }
            ) + expandVertically(
                // Expand from the top.
                expandFrom = Alignment.Top
            ) + fadeIn(
                // Fade in with the initial alpha of 0.3f.
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .size(150.dp),
                color = Color.Transparent
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = R.mipmap.ic_launcher_foreground
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.background(Color.Transparent)
                )
            }
        }
    }
}

fun headerLine(size: Size):Path{
    val path = Path()
    val lastY = size.height/3.4F
    path.moveTo(0F, lastY)
    val lastX = size.width
    path.cubicTo(
        size.width/2F, lastY + 0.1F * size.height,
       size.width/2F, lastY - 0.2F * size.height,
        size.width, lastY
    )
    path.lineTo(size.width, 0F)
    path.lineTo(0F,0F)
    path.lineTo(0F, lastY)
    return path
}

//fun secondLine(size: Size):Path{
//    val path = Path()
//    val lastY = size.height/3.5F
//    path.moveTo(size.width/3, lastY)
//    path.cubicTo(
//        size.width/2F, lastY,
//        size.width/2F, lastY - 0.2F * size.height + 10F,
//        size.width, lastY
//    )
//    path.moveTo(size.width/3, lastY)
//    return path
//}


@Preview(showBackground = true)
@Composable
fun LoginUIPre() {
    NorScTheme {
        LoginHeader()
    }
}