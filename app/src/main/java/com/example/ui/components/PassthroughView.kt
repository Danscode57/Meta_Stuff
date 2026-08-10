package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.DetectedRoomObject
import com.example.model.PassthroughFilterMode
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.PassthroughAmber
import com.example.ui.theme.PassthroughEmerald
import com.example.ui.theme.QuestCyan
import com.example.ui.theme.QuestPurple

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PassthroughView(
    filterMode: PassthroughFilterMode,
    onFilterChanged: (PassthroughFilterMode) -> Unit,
    opacity: Float,
    onOpacityChanged: (Float) -> Unit,
    isMeshOverlayVisible: Boolean,
    onToggleMeshOverlay: () -> Unit,
    isOcclusionEnabled: Boolean,
    onToggleOcclusion: () -> Unit,
    detectedObjects: List<DetectedRoomObject>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "meshPulse")
    val gridPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gridPulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
            .background(Color(0xFF070913))
    ) {
        // Live Passthrough Camera & Spatial Shader Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Background shader color depending on active PassthroughFilterMode
            drawPassthroughBackground(filterMode, opacity, canvasWidth, canvasHeight)

            // Draw Spatial Depth Mesh Grid lines if enabled
            if (isMeshOverlayVisible || filterMode == PassthroughFilterMode.DEPTH_MESH) {
                drawRoomMeshGrid(
                    filterMode = filterMode,
                    pulseAlpha = gridPulseAlpha,
                    width = canvasWidth,
                    height = canvasHeight
                )
            }

            // Draw Detected Room Objects Bounding Boxes and Distance Tags
            detectedObjects.forEach { obj ->
                drawDetectedObjectBoundingBox(
                    obj = obj,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    filterMode = filterMode
                )
            }

            // Draw Horizon Passthrough Crosshair Pointer
            drawPassthroughCrosshair(canvasWidth, canvasHeight)
        }

        // Overlay Controls Panel (Floating Bar at Bottom)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xEE0D101E),
                            Color(0xFF0B0E1B)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Filter Chip Selector
            Text(
                text = "PASSTHROUGH SHADER & DEPTH FILTERS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = QuestCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PassthroughFilterMode.entries.forEach { mode ->
                    val isSelected = filterMode == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterChanged(mode) },
                        label = {
                            Text(
                                text = mode.displayName,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = QuestCyan,
                            containerColor = Color(0xFF181D33)
                        ),
                        modifier = Modifier.testTag("filter_chip_${mode.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Opacity & Toggles Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Opacity Slider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Opacity,
                        contentDescription = "Opacity",
                        tint = QuestCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Blend: ${(opacity * 100).toInt()}%",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = opacity,
                        onValueChange = onOpacityChanged,
                        valueRange = 0.1f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = QuestCyan,
                            activeTrackColor = QuestCyan,
                            inactiveTrackColor = Color.DarkGray
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("opacity_slider")
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Toggle Switches
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Mesh Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleMeshOverlay() }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Mesh Grid",
                            tint = if (isMeshOverlayVisible) PassthroughEmerald else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Mesh", fontSize = 12.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = isMeshOverlayVisible,
                            onCheckedChange = { onToggleMeshOverlay() },
                            colors = SwitchDefaults.colors(checkedThumbColor = PassthroughEmerald),
                            modifier = Modifier.testTag("switch_mesh")
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Occlusion Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleOcclusion() }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Occlusion",
                            tint = if (isOcclusionEnabled) QuestPurple else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Occlude", fontSize = 12.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = isOcclusionEnabled,
                            onCheckedChange = { onToggleOcclusion() },
                            colors = SwitchDefaults.colors(checkedThumbColor = QuestPurple),
                            modifier = Modifier.testTag("switch_occlusion")
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawPassthroughBackground(
    mode: PassthroughFilterMode,
    opacity: Float,
    width: Float,
    height: Float
) {
    val bgBrush = when (mode) {
        PassthroughFilterMode.FULL_COLOR -> Brush.radialGradient(
            colors = listOf(Color(0xFF283244), Color(0xFF0F1420)),
            center = Offset(width / 2, height / 2),
            radius = width
        )
        PassthroughFilterMode.DEPTH_MESH -> Brush.linearGradient(
            colors = listOf(Color(0xFF080D1A), Color(0xFF04060E))
        )
        PassthroughFilterMode.NIGHT_VISION -> Brush.radialGradient(
            colors = listOf(Color(0xFF003D1A), Color(0xFF000F06)),
            center = Offset(width / 2, height / 2),
            radius = width
        )
        PassthroughFilterMode.CYBER_THERMAL -> Brush.linearGradient(
            colors = listOf(Color(0xFF4A002E), Color(0xFF10002B), Color(0xFF001133))
        )
        PassthroughFilterMode.EDGE_CONTOUR -> Brush.linearGradient(
            colors = listOf(Color(0xFF111111), Color(0xFF000000))
        )
    }

    drawRect(
        brush = bgBrush,
        size = Size(width, height),
        alpha = opacity
    )
}

private fun DrawScope.drawRoomMeshGrid(
    filterMode: PassthroughFilterMode,
    pulseAlpha: Float,
    width: Float,
    height: Float
) {
    val gridColor = when (filterMode) {
        PassthroughFilterMode.NIGHT_VISION -> PassthroughEmerald
        PassthroughFilterMode.CYBER_THERMAL -> QuestPurple
        else -> QuestCyan
    }.copy(alpha = pulseAlpha)

    val numCols = 16
    val numRows = 10
    val cellWidth = width / numCols
    val cellHeight = height / numRows

    // Vertical Perspective Grid Lines
    for (i in 0..numCols) {
        val x = i * cellWidth
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 1f
        )
    }

    // Horizontal Perspective Grid Lines
    for (j in 0..numRows) {
        val y = j * cellHeight
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f
        )
    }

    // Simulated 3D Room Corner Perspective lines
    val centerX = width / 2f
    val centerY = height / 2f
    val path = Path().apply {
        moveTo(0f, 0f)
        lineTo(centerX - 100f, centerY - 60f)
        moveTo(width, 0f)
        lineTo(centerX + 100f, centerY - 60f)
        moveTo(0f, height)
        lineTo(centerX - 100f, centerY + 60f)
        moveTo(width, height)
        lineTo(centerX + 100f, centerY + 60f)
    }

    drawPath(
        path = path,
        color = gridColor.copy(alpha = pulseAlpha * 1.2f),
        style = Stroke(width = 1.5f)
    )
}

private fun DrawScope.drawDetectedObjectBoundingBox(
    obj: DetectedRoomObject,
    canvasWidth: Float,
    canvasHeight: Float,
    filterMode: PassthroughFilterMode
) {
    val x = obj.posXRatio * canvasWidth
    val y = obj.posYRatio * canvasHeight
    val w = obj.widthRatio * canvasWidth
    val h = obj.heightRatio * canvasHeight

    val boxColor = when (obj.category) {
        "Furniture" -> PassthroughEmerald
        "Display" -> QuestCyan
        else -> PassthroughAmber
    }

    // Bounding Box Rect
    drawRect(
        color = boxColor.copy(alpha = 0.85f),
        topLeft = Offset(x, y),
        size = Size(w, h),
        style = Stroke(width = 2f)
    )

    // Corner Accents
    val cornerLen = 14f
    // Top-Left corner
    drawLine(boxColor, Offset(x, y), Offset(x + cornerLen, y), strokeWidth = 4f)
    drawLine(boxColor, Offset(x, y), Offset(x, y + cornerLen), strokeWidth = 4f)
    // Top-Right corner
    drawLine(boxColor, Offset(x + w, y), Offset(x + w - cornerLen, y), strokeWidth = 4f)
    drawLine(boxColor, Offset(x + w, y), Offset(x + w, y + cornerLen), strokeWidth = 4f)
    // Bottom-Left corner
    drawLine(boxColor, Offset(x, y + h), Offset(x + cornerLen, y + h), strokeWidth = 4f)
    drawLine(boxColor, Offset(x, y + h), Offset(x, y + h - cornerLen), strokeWidth = 4f)
    // Bottom-Right corner
    drawLine(boxColor, Offset(x + w, y + h), Offset(x + w - cornerLen, y + h), strokeWidth = 4f)
    drawLine(boxColor, Offset(x + w, y + h), Offset(x + w, y + h - cornerLen), strokeWidth = 4f)

    // Fill semi-transparent tint inside box
    drawRect(
        color = boxColor.copy(alpha = 0.12f),
        topLeft = Offset(x, y),
        size = Size(w, h)
    )
}

private fun DrawScope.drawPassthroughCrosshair(width: Float, height: Float) {
    val cx = width / 2f
    val cy = height / 2f
    val r = 16f

    // Center Crosshair Circle
    drawCircle(
        color = QuestCyan,
        radius = r,
        center = Offset(cx, cy),
        style = Stroke(width = 2f)
    )

    // Crosshair Lines
    drawLine(QuestCyan, Offset(cx - r - 10f, cy), Offset(cx - r + 4f, cy), strokeWidth = 2f)
    drawLine(QuestCyan, Offset(cx + r - 4f, cy), Offset(cx + r + 10f, cy), strokeWidth = 2f)
    drawLine(QuestCyan, Offset(cx, cy - r - 10f), Offset(cx, cy - r + 4f), strokeWidth = 2f)
    drawLine(QuestCyan, Offset(cx, cy + r - 4f), Offset(cx, cy + r + 10f), strokeWidth = 2f)

    drawCircle(
        color = PassthroughEmerald,
        radius = 3f,
        center = Offset(cx, cy)
    )
}
