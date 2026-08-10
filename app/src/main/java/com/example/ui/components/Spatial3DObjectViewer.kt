package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.PassthroughEmerald
import com.example.ui.theme.QuestCyan
import com.example.ui.theme.QuestPurple
import kotlin.math.cos
import kotlin.math.sin

enum class Model3DType(val displayName: String) {
    QUEST_3_HEADSET("Quest 3 Headset"),
    TOUCH_PLUS_CONTROLLER("Touch Plus Controller"),
    CYBER_PORTAL("Spatial Portal"),
    HOLOGRAM_CUBE("Spatial Grid Cube")
}

@Composable
fun Spatial3DObjectViewer(
    modifier: Modifier = Modifier
) {
    var selectedModel by remember { mutableStateOf(Model3DType.QUEST_3_HEADSET) }
    var rotationYDeg by remember { mutableFloatStateOf(45f) }
    var rotationXDeg by remember { mutableFloatStateOf(15f) }
    var isWireframe by remember { mutableStateOf(true) }
    var modelScale by remember { mutableFloatStateOf(1.0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
            .background(Color(0xFF0C0F21))
    ) {
        // 3D Rendering Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            // Draw Background Spatial Ring
            drawCircle(
                color = QuestCyan.copy(alpha = 0.15f),
                radius = 180f * modelScale,
                center = Offset(cx, cy),
                style = Stroke(width = 2f)
            )

            when (selectedModel) {
                Model3DType.QUEST_3_HEADSET -> drawQuest3Headset3D(cx, cy, rotationYDeg, rotationXDeg, modelScale, isWireframe)
                Model3DType.TOUCH_PLUS_CONTROLLER -> drawController3D(cx, cy, rotationYDeg, modelScale, isWireframe)
                Model3DType.CYBER_PORTAL -> drawCyberPortal3D(cx, cy, rotationYDeg, modelScale, isWireframe)
                Model3DType.HOLOGRAM_CUBE -> drawCube3D(cx, cy, rotationYDeg, rotationXDeg, modelScale, isWireframe)
            }
        }

        // Controls Panel Overlay
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xEE12162B)),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(QuestCyan, QuestPurple)))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Model Selector Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Model3DType.entries.forEach { model ->
                        val isSelected = selectedModel == model
                        Button(
                            onClick = { selectedModel = model },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) QuestCyan else Color(0xFF1E2542),
                                contentColor = if (isSelected) Color.Black else Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("btn_model_${model.name}")
                        ) {
                            Text(
                                text = model.displayName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rotation Y Slider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.RotateRight,
                        contentDescription = "Rotate",
                        tint = QuestCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Rotate Y: ${rotationYDeg.toInt()}°", fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = rotationYDeg,
                        onValueChange = { rotationYDeg = it },
                        valueRange = 0f..360f,
                        colors = SliderDefaults.colors(thumbColor = QuestCyan, activeTrackColor = QuestCyan),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("slider_rotate_3d")
                    )
                }

                // Wireframe Switch & Scale Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Wireframe Mesh", fontSize = 12.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = isWireframe,
                            onCheckedChange = { isWireframe = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PassthroughEmerald),
                            modifier = Modifier.testTag("switch_wireframe")
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.width(180.dp)
                    ) {
                        Text(text = "Scale", fontSize = 12.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Slider(
                            value = modelScale,
                            onValueChange = { modelScale = it },
                            valueRange = 0.6f..1.6f,
                            colors = SliderDefaults.colors(thumbColor = PassthroughEmerald, activeTrackColor = PassthroughEmerald)
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawQuest3Headset3D(
    cx: Float,
    cy: Float,
    rotY: Float,
    rotX: Float,
    scale: Float,
    wireframe: Boolean
) {
    val radY = Math.toRadians(rotY.toDouble())
    val cosY = cos(radY).toFloat()
    val sinY = sin(radY).toFloat()

    val w = 180f * scale
    val h = 90f * scale
    val depth = 100f * scale

    val color = QuestCyan

    // Front Visor Curve
    val visorLeft = Offset(cx - w * cosY, cy - h / 2f)
    val visorRight = Offset(cx + w * cosY, cy - h / 2f)
    val visorBottomLeft = Offset(cx - w * cosY, cy + h / 2f)
    val visorBottomRight = Offset(cx + w * cosY, cy + h / 2f)

    val path = Path().apply {
        moveTo(visorLeft.x, visorLeft.y)
        lineTo(visorRight.x, visorRight.y)
        lineTo(visorBottomRight.x, visorBottomRight.y)
        lineTo(visorBottomLeft.x, visorBottomLeft.y)
        close()
    }

    if (wireframe) {
        drawPath(path, color, style = Stroke(width = 3f))
    } else {
        drawPath(path, color.copy(alpha = 0.40f))
        drawPath(path, color, style = Stroke(width = 2f))
    }

    // Quest 3 Front Passthrough Cameras (3 Pills)
    val camR = 12f * scale
    val offsetCam = 40f * scale * cosY

    drawCircle(PassthroughEmerald, camR, Offset(cx - offsetCam, cy))
    drawCircle(PassthroughEmerald, camR, Offset(cx, cy))
    drawCircle(PassthroughEmerald, camR, Offset(cx + offsetCam, cy))
}

private fun DrawScope.drawController3D(
    cx: Float,
    cy: Float,
    rotY: Float,
    scale: Float,
    wireframe: Boolean
) {
    val handleR = 24f * scale
    val handleH = 140f * scale

    // Handle
    drawRect(
        color = QuestPurple,
        topLeft = Offset(cx - handleR, cy - handleH / 2f),
        size = Size(handleR * 2f, handleH),
        style = if (wireframe) Stroke(width = 2f) else androidx.compose.ui.graphics.drawscope.Fill
    )

    // Touchpad Plate
    drawCircle(
        color = QuestCyan,
        radius = 32f * scale,
        center = Offset(cx, cy - handleH / 2f),
        style = if (wireframe) Stroke(width = 2f) else androidx.compose.ui.graphics.drawscope.Fill
    )
}

private fun DrawScope.drawCyberPortal3D(
    cx: Float,
    cy: Float,
    rotY: Float,
    scale: Float,
    wireframe: Boolean
) {
    val r = 120f * scale
    drawCircle(PassthroughEmerald, r, Offset(cx, cy), style = Stroke(width = 4f))
    drawCircle(QuestCyan, r * 0.75f, Offset(cx, cy), style = Stroke(width = 2f))
    drawCircle(QuestPurple, r * 0.50f, Offset(cx, cy), style = Stroke(width = 1.5f))
}

private fun DrawScope.drawCube3D(
    cx: Float,
    cy: Float,
    rotY: Float,
    rotX: Float,
    scale: Float,
    wireframe: Boolean
) {
    val s = 100f * scale
    val radY = Math.toRadians(rotY.toDouble())
    val cosY = cos(radY).toFloat() * s
    val sinY = sin(radY).toFloat() * s

    val p1 = Offset(cx - cosY, cy - s + sinY)
    val p2 = Offset(cx + cosY, cy - s - sinY)
    val p3 = Offset(cx + cosY, cy + s - sinY)
    val p4 = Offset(cx - cosY, cy + s + sinY)

    val path = Path().apply {
        moveTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(p3.x, p3.y)
        lineTo(p4.x, p4.y)
        close()
    }

    drawPath(path, QuestCyan, style = Stroke(width = 3f))
}
