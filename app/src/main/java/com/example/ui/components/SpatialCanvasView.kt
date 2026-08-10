package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SpatialAnchorEntity
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.PassthroughAmber
import com.example.ui.theme.PassthroughEmerald
import com.example.ui.theme.QuestCyan
import com.example.ui.theme.QuestPurple

@Composable
fun SpatialCanvasView(
    anchors: List<SpatialAnchorEntity>,
    onAnchorPositionChanged: (SpatialAnchorEntity, Float, Float, Float) -> Unit,
    onAnchorRotationChanged: (SpatialAnchorEntity, Float) -> Unit,
    onTogglePinned: (SpatialAnchorEntity) -> Unit,
    onDeleteAnchor: (SpatialAnchorEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAnchorId by remember { mutableStateOf<String?>(null) }
    val selectedAnchor = anchors.find { it.id == selectedAnchorId }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
            .background(Color(0xFF090C1A))
    ) {
        // Interactive 2D/3D Spatial Coordinate Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(anchors) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val activeId = selectedAnchorId ?: return@detectDragGestures
                        val target = anchors.find { it.id == activeId } ?: return@detectDragGestures

                        val canvasW = size.width.toFloat()
                        val canvasH = size.height.toFloat()

                        // Convert screen drag delta to 3D spatial meter delta
                        val deltaX = (dragAmount.x / canvasW) * 4.0f
                        val deltaZ = (dragAmount.y / canvasH) * 4.0f

                        val updatedX = (target.posX + deltaX).coerceIn(-2.0f, 2.0f)
                        val updatedZ = (target.posZ + deltaZ).coerceIn(0.5f, 4.5f)

                        onAnchorPositionChanged(target, updatedX, target.posY, updatedZ)
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // Spatial Grid Base
            drawSpatialFloorGrid(w, h)

            // User Head Position Marker
            drawUserHeadMarker(w, h)

            // Draw all spatial anchors
            anchors.forEach { anchor ->
                val isSelected = anchor.id == selectedAnchorId
                drawSpatialAnchorNode(
                    anchor = anchor,
                    canvasWidth = w,
                    canvasHeight = h,
                    isSelected = isSelected
                )
            }
        }

        // Tap targets overlay for anchor selection
        Box(modifier = Modifier.fillMaxSize()) {
            anchors.forEach { anchor ->
                // Map (posX, posZ) to Screen X/Y
                val normX = (anchor.posX + 2.0f) / 4.0f
                val normY = (anchor.posZ - 0.5f) / 4.0f

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = (normX * 320).dp,
                            top = (normY * 320).dp
                        )
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { selectedAnchorId = anchor.id }
                        .testTag("anchor_node_${anchor.id}")
                )
            }
        }

        // Selected Anchor Control Panel Card
        AnimatedVisibility(
            visible = selectedAnchor != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            selectedAnchor?.let { anchor ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14192E)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(QuestCyan, QuestPurple)))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ViewInAr,
                                    contentDescription = "Anchor",
                                    tint = QuestCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = anchor.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "X: ${String.format("%.2f", anchor.posX)}m  Y: ${String.format("%.2f", anchor.posY)}m  Z: ${String.format("%.2f", anchor.posZ)}m",
                                        fontSize = 11.sp,
                                        color = PassthroughEmerald
                                    )
                                }
                            }

                            Row {
                                IconButton(
                                    onClick = { onTogglePinned(anchor) },
                                    modifier = Modifier.testTag("pin_anchor_button")
                                ) {
                                    Icon(
                                        imageVector = if (anchor.isPinned) Icons.Default.PushPin else Icons.Default.Pin,
                                        contentDescription = "Pin",
                                        tint = if (anchor.isPinned) PassthroughAmber else Color.Gray
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onDeleteAnchor(anchor)
                                        selectedAnchorId = null
                                    },
                                    modifier = Modifier.testTag("delete_anchor_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFFF5252)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Rotation Y Slider
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RotateRight,
                                contentDescription = "Rotation",
                                tint = QuestPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Rotation Y: ${anchor.rotationY.toInt()}°",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Slider(
                                value = anchor.rotationY,
                                onValueChange = { onAnchorRotationChanged(anchor, it) },
                                valueRange = 0f..360f,
                                colors = SliderDefaults.colors(
                                    thumbColor = QuestPurple,
                                    activeTrackColor = QuestPurple
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawSpatialFloorGrid(w: Float, h: Float) {
    val gridColor = QuestCyan.copy(alpha = 0.20f)
    val rows = 12
    val cols = 12

    for (i in 0..cols) {
        val x = (i / cols.toFloat()) * w
        drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
    }
    for (j in 0..rows) {
        val y = (j / rows.toFloat()) * h
        drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
    }
}

private fun DrawScope.drawUserHeadMarker(w: Float, h: Float) {
    val cx = w / 2f
    val cy = h - 60f

    // User Head Icon Position (Origin)
    drawCircle(
        color = QuestCyan,
        radius = 16f,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = Color.Black,
        radius = 8f,
        center = Offset(cx, cy)
    )

    // FOV Cone Arc
    val conePath = Path().apply {
        moveTo(cx, cy)
        lineTo(cx - 180f, 0f)
        lineTo(cx + 180f, 0f)
        close()
    }
    drawPath(
        path = conePath,
        color = QuestCyan.copy(alpha = 0.08f)
    )
}

private fun DrawScope.drawSpatialAnchorNode(
    anchor: SpatialAnchorEntity,
    canvasWidth: Float,
    canvasHeight: Float,
    isSelected: Boolean
) {
    val normX = (anchor.posX + 2.0f) / 4.0f
    val normY = (anchor.posZ - 0.5f) / 4.0f

    val cx = normX * canvasWidth
    val cy = normY * canvasHeight

    val color = parseHexColor(anchor.colorHex)

    // Selection Halo Rings
    if (isSelected) {
        drawCircle(
            color = QuestCyan,
            radius = 28f,
            center = Offset(cx, cy),
            style = Stroke(width = 3f)
        )
    }

    // Anchor Node Circle
    drawCircle(
        color = color,
        radius = 16f,
        center = Offset(cx, cy)
    )

    // Center Dot
    drawCircle(
        color = Color.White,
        radius = 5f,
        center = Offset(cx, cy)
    )

    // Ray line from user origin to anchor
    drawLine(
        color = color.copy(alpha = 0.40f),
        start = Offset(canvasWidth / 2f, canvasHeight - 60f),
        end = Offset(cx, cy),
        strokeWidth = 1.5f
    )
}

private fun parseHexColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        QuestCyan
    }
}
