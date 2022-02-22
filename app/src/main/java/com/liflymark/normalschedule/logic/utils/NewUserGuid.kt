package com.liflymark.normalschedule.logic.utils

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.liflymark.normalschedule.logic.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class LayoutCoordinatesAndDescription(
    val index:Int,
    val layout:LayoutCoordinates,
    val description: String
)
@Composable
fun ShowBox(
    positions: List<LayoutCoordinatesAndDescription>,
    onFinished: () -> Unit
) {
    if (positions.isEmpty()) {
        return
    }
    val position = positions.sortedBy { it.index }
    var index by rememberSaveable { mutableStateOf(0) }

    var show by rememberSaveable { mutableStateOf(true) }

    val nowLayoutCoordinates = remember(index) { derivedStateOf { position[index] } }
    val layoutAndDes = nowLayoutCoordinates.value
    val x = layoutAndDes.layout.positionInRoot().x
    val y = layoutAndDes.layout.positionInRoot().y
    val width = layoutAndDes.layout.size.width.toFloat()
    val height = layoutAndDes.layout.size.height.toFloat()

    if (show) {
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    val circlePath = Path().apply {
                        addOval(Rect(offset = Offset(x, y), Size(width+10F, height+10F)))
                    }
                    clipPath(circlePath, clipOp = ClipOp.Difference) {
                        drawRect(SolidColor(Color.Black.copy(alpha = 0.8f)))
                    }
                }
//                .clickable(
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = null,
//                    enabled = false
//                ) {
//                    if (index + 1 < positions.size) {
//                        index += 1
//                    } else {
//                        show = false
//                        onFinished()
//                    }
//                }
            ,
            contentAlignment = Alignment.TopStart
        ) {
            val xDp = with(LocalDensity.current) { (x + width / 2).toDp() }
            val yDp = with(LocalDensity.current) { (y + height).toDp() }
            val singleCardWidth = 160.dp
            fun setFitDp(nowDp:Dp, max:Dp):Dp{
                return when{
                    nowDp.value<0F -> {
                        10.dp
                    }
                    nowDp.value>max.value -> {
                        max-10.dp
                    }
                    else -> {
                        nowDp
                    }
                }
            }
            Card(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .width(singleCardWidth)
                    .offset(setFitDp(xDp - singleCardWidth/2, maxWidth-singleCardWidth), setFitDp(yDp + 10.dp, maxHeight)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text=layoutAndDes.description,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onBackground,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider()
                    TextButton(
                        onClick = {
                            Log.d("NewUserGuide", "index：$index")
                            if (index + 1 < positions.size) {
                                index += 1
                            } else {
                                show = false
                                onFinished()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "知道了")
                    }
                }
            }
        }
    }
}



private val HighlightExtraRadius = 8.dp
private val HighlightAppearanceAnimationSpec =
    tween<Float>(durationMillis = 500, delayMillis = 2000, easing = LinearOutSlowInEasing)
private val HighlightDisappearanceAnimationSpec =
    tween<Float>(durationMillis = 250, easing = FastOutLinearInEasing)
private fun Density.calculateClipPath(
    highlight: LayoutCoordinates?,
    parent: LayoutCoordinates?
) = if (highlight != null && parent != null) {
    val highlightRect = parent.localBoundingBoxOf(highlight)
    val circleRect = Rect(
        center = highlightRect.center,
        radius = highlightRect.maxDimension / 2f + HighlightExtraRadius.toPx()
    )
    Path.combine(
        PathOperation.Difference,
        Path().apply {
            addRect(parent.size.toSize().toRect())
        },
        Path().apply {
            addOval(circleRect)
        }
    )
} else {
    null
}

@Composable
fun TutorialOverlay(
    scope:CoroutineScope = rememberCoroutineScope(),
    description:String = "",
    newUserOrNot:Boolean = false,
    content: @Composable (Modifier) -> Unit
) {
    var parentCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var highlightCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val animatedAlpha = remember { Animatable(0f) }
    LaunchedEffect(animatedAlpha) {
        animatedAlpha.animateTo(1f, HighlightAppearanceAnimationSpec)
    }
    val tutorialIsActive by remember(animatedAlpha) { derivedStateOf { animatedAlpha.value != 0f } }
    Box(
        modifier = Modifier
            .onGloballyPositioned { parentCoordinates = it }
    ) {
        content(
            Modifier
                .onGloballyPositioned { highlightCoordinates = it }
                .drawBehind {
                    if (tutorialIsActive) {
                        drawCircle(
                            Color.Transparent,
                            alpha = animatedAlpha.value,
                            radius = size.maxDimension / 2f + HighlightExtraRadius.toPx()
                        )
                    }
                }
        )
        if (tutorialIsActive && newUserOrNot) {
            Box(
                Modifier
                    .fillMaxSize()
                    .drawWithCache {
                        val path = calculateClipPath(highlightCoordinates, parentCoordinates)
                        onDrawBehind {
                            if (path != null && tutorialIsActive) {
                                drawPath(path, Color.Black, alpha = 0.8f * animatedAlpha.value)
                            }
                        }
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        scope.launch {
                            Log.d("newUser", "开始存储")
                            animatedAlpha.animateTo(0f, HighlightDisappearanceAnimationSpec)
                            Repository.saveUserVersion(3)
                            Log.d("newUser", "存储完毕")
                        }
                    }
            ) {
                Text(
                    description,
                    Modifier
                        .align(Alignment.Center)
                        .padding(48.dp)
                        .graphicsLayer { alpha = animatedAlpha.value },
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                )
            }
        }
    }
}