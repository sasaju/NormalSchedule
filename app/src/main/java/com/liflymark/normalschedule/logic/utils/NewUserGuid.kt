package com.liflymark.normalschedule.logic.utils

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.liflymark.normalschedule.logic.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


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