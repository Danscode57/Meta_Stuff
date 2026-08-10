package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HandGestureType
import com.example.model.HandTrackingInfo
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.PassthroughEmerald
import com.example.ui.theme.QuestCyan
import com.example.ui.theme.QuestPurple
import kotlinx.coroutines.launch

@Composable
fun HandTrackingCanvas(
    handInfo: HandTrackingInfo,
    onPointerMoved: (Float, Float) -> Unit,
    onAirTapTriggered: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rippleRadius = remember { Animatable(0f) }
    val rippleAlpha = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
            .background(Color(0xFF0A0D1B))
    ) {
        // Interactive Hand Skeletal Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val ratioX = offset.x / size.width
                        val ratioY = offset.y / size.height
                        onPointerMoved(ratioX, ratioY)
                        onAirTapTriggered()

                        // Trigger air tap ripple
                        scope.launch {
                            rippleRadius.snapTo(10f)
                            rippleAlpha.snapTo(1f)
                            launch { rippleRadius.animateTo(120f, tween(400)) }
                            launch { rippleAlpha.animateTo(0f, tween(400)) }
                        }
                    }
                }
                .testTag("hand_tracking_canvas")
        ) {
            val w = size.width
            val h = size.height

            // Pointer position on spatial screen
            val px = handInfo.pointerXRatio * w
            val py = handInfo.pointerYRatio * h

            // Draw Left Hand Skeleton Overlay
            if (handInfo.isLeftHandTracked) {
                drawHandSkeleton(
                    wristPos = Offset(w * 0.25f, h * 0.75f),
                    gesture = handInfo.leftGesture,
                    pinchStrength = handInfo.leftPinchStrength,
                    isLeft = true,
                    targetPointer = Offset(px, py)
                )
            }

            // Draw Right Hand Skeleton Overlay
            if (handInfo.isRightHandTracked) {
                drawHandSkeleton(
                    wristPos = Offset(w * 0.75f, h * 0.75f),
                    gesture = handInfo.rightGesture,
                    pinchStrength = handInfo.rightPinchStrength,
                    isLeft = false,
                    targetPointer = Offset(px, py)
                )
            }

            // Draw Pointer Target Target Circle & Ripple
            drawPointerTarget(
                pointerPos = Offset(px, py),
                rippleR = rippleRadius.value,
                rippleA = rippleAlpha.value
            )
        }

        // Live Hand Status Bar Card
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xDD12172A)),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(QuestCyan, PassthroughEmerald)))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HandBadge(
                    label = "LEFT HAND",
                    gesture = handInfo.leftGesture,
                    strength = handInfo.leftPinchStrength,
                    color = QuestCyan
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(Color.DarkGray)
                )

                HandBadge(
                    label = "RIGHT HAND",
                    gesture = handInfo.rightGesture,
                    strength = handInfo.rightPinchStrength,
                    color = QuestPurple
                )
            }
        }
    }
}

@Composable
private fun HandBadge(
    label: String,
    gesture: HandGestureType,
    strength: Float,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.TouchApp,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = gesture.label, fontSize = 12.sp, color = Color.White)
        }
    }
}

private fun DrawScope.drawHandSkeleton(
    wristPos: Offset,
    gesture: HandGestureType,
    pinchStrength: Float,
    isLeft: Boolean,
    targetPointer: Offset
) {
    val handColor = if (isLeft) QuestCyan else QuestPurple

    val palmCenter = wristPos + Offset(0f, -80f)
    val indexTip = if (gesture == HandGestureType.INDEX_PINCH) {
        palmCenter + Offset(if (isLeft) -20f else 20f, -120f)
    } else {
        palmCenter + Offset(if (isLeft) -50f else 50f, -160f)
    }
    val thumbTip = palmCenter + Offset(if (isLeft) -30f else 30f, -100f)
    val middleTip = palmCenter + Offset(0f, -180f)
    val ringTip = palmCenter + Offset(if (isLeft) 30f else -30f, -160f)
    val pinkyTip = palmCenter + Offset(if (isLeft) 50f else -50f, -130f)

    // Draw Joint Connections
    val joints = listOf(
        wristPos to palmCenter,
        palmCenter to indexTip,
        palmCenter to thumbTip,
        palmCenter to middleTip,
        palmCenter to ringTip,
        palmCenter to pinkyTip
    )

    joints.forEach { (start, end) ->
        drawLine(
            color = handColor.copy(alpha = 0.70f),
            start = start,
            end = end,
            strokeWidth = 3f
        )
    }

    // Draw Joint Nodes
    val nodes = listOf(wristPos, palmCenter, indexTip, thumbTip, middleTip, ringTip, pinkyTip)
    nodes.forEach { node ->
        drawCircle(color = handColor, radius = 6f, center = node)
        drawCircle(color = Color.White, radius = 2f, center = node)
    }

    // Draw Raycast Beam from Index Tip to Target
    drawLine(
        color = handColor.copy(alpha = 0.40f),
        start = indexTip,
        end = targetPointer,
        strokeWidth = 2f
    )
}

private fun DrawScope.drawPointerTarget(
    pointerPos: Offset,
    rippleR: Float,
    rippleA: Float
) {
    // Target Reticle
    drawCircle(
        color = QuestCyan,
        radius = 12f,
        center = pointerPos,
        style = Stroke(width = 2f)
    )
    drawCircle(
        color = PassthroughEmerald,
        radius = 4f,
        center = pointerPos
    )

    // Air Tap Ripple Burst
    if (rippleA > 0f) {
        drawCircle(
            color = QuestCyan.copy(alpha = rippleA),
            radius = rippleR,
            center = pointerPos,
            style = Stroke(width = 3f)
        )
    }
}
