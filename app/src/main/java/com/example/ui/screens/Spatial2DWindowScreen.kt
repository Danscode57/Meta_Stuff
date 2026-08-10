package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppShortcut
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SpatialWindowConfig
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.PassthroughEmerald
import com.example.ui.theme.QuestCyan
import com.example.ui.theme.QuestPurple

data class QuestApp(
    val id: String,
    val title: String,
    val category: String,
    val icon: ImageVector,
    val color: Color,
    val description: String
)

val questAppsList = listOf(
    QuestApp("app_1", "Spatial Web Browser", "Productivity", Icons.Default.Language, QuestCyan, "Multi-tab WebXR spatial browser panel"),
    QuestApp("app_2", "Virtual Desktop Streamer", "Remote PC", Icons.Default.DesktopWindows, QuestPurple, "Low-latency Wi-Fi 6E PC desktop streaming"),
    QuestApp("app_3", "Spatial Code Studio", "Developer", Icons.Default.Code, PassthroughEmerald, "Multi-screen Kotlin & Web code workspace"),
    QuestApp("app_4", "YouTube 360 & 180", "Media", Icons.Default.OndemandVideo, Color(0xFFFF3333), "Immersive spatial video playback panel"),
    QuestApp("app_5", "Horizon Feed & Spatial Chat", "Social", Icons.Default.AppShortcut, Color(0xFFFFB703), "Meta Horizon avatars & multiplayer space")
)

val windowPresets = listOf(
    "Single Focus Curve",
    "Dual Screen Multitask",
    "Triple Wrap Workstation",
    "Ultra-Wide Theater"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Spatial2DWindowScreen(
    windowConfig: SpatialWindowConfig,
    onUpdateConfig: (radius: Int, width: Int, height: Int, distance: Float, opacity: Float, isCurved: Boolean, isPinned: Boolean, presetName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeApp by remember { mutableStateOf<QuestApp?>(questAppsList.first()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Curved Spatial Window Preview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101428)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(QuestCyan, QuestPurple)))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Curved Panel Background Simulation
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        activeApp?.color?.copy(alpha = 0.25f) ?: QuestCyan.copy(alpha = 0.2f),
                                        Color(0xFF090B16)
                                    )
                                )
                            )
                            .border(1.5.dp, activeApp?.color ?: QuestCyan, RoundedCornerShape(16.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            // Window Header Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = activeApp?.icon ?: Icons.Default.Tv,
                                        contentDescription = "App Icon",
                                        tint = activeApp?.color ?: QuestCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = activeApp?.title ?: "2D Spatial Panel",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${windowConfig.selectedPresetName} • ${windowConfig.distanceMeters}m distance",
                                            fontSize = 12.sp,
                                            color = PassthroughEmerald
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (windowConfig.isPinnedToRoom) {
                                        Icon(
                                            imageVector = Icons.Default.PushPin,
                                            contentDescription = "Pinned",
                                            tint = PassthroughEmerald,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = "${windowConfig.windowWidthDp}x${windowConfig.windowHeightDp}dp",
                                        fontSize = 11.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Active App Workspace Preview Content
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF060812))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = activeApp?.icon ?: Icons.Default.Tv,
                                        contentDescription = "Active App",
                                        tint = activeApp?.color ?: QuestCyan,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = activeApp?.description ?: "Spatial Window Execution Workspace",
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Curvature Radius: ${windowConfig.curvatureRadiusDp}dp • Opacity: ${(windowConfig.opacityPercent * 100).toInt()}%",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Layout Preset Selector Pills
        item {
            Column {
                Text(
                    text = "SPATIAL WINDOW PRESETS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuestCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    windowPresets.forEach { preset ->
                        val isSelected = windowConfig.selectedPresetName == preset
                        Button(
                            onClick = {
                                when (preset) {
                                    "Single Focus Curve" -> onUpdateConfig(1200, 850, 540, 1.5f, 0.95f, true, false, preset)
                                    "Dual Screen Multitask" -> onUpdateConfig(1600, 1200, 600, 1.8f, 0.90f, true, true, preset)
                                    "Triple Wrap Workstation" -> onUpdateConfig(2000, 1500, 650, 2.2f, 0.85f, true, true, preset)
                                    "Ultra-Wide Theater" -> onUpdateConfig(900, 1800, 800, 3.0f, 1.0f, true, false, preset)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) QuestCyan else Color(0xFF161B30),
                                contentColor = if (isSelected) Color.Black else Color.White
                            ),
                            modifier = Modifier.testTag("preset_btn_${preset.replace(" ", "_")}")
                        ) {
                            Text(text = preset, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Window Geometry & Curved Spatial Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14182B)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(QuestCyan, PassthroughEmerald)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Geometry Controls",
                                tint = QuestCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Spatial Panel Geometry Controls",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }

                        // Curve Toggle Switch
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Curved", fontSize = 12.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = windowConfig.isCurved,
                                onCheckedChange = { isCurved ->
                                    onUpdateConfig(
                                        windowConfig.curvatureRadiusDp,
                                        windowConfig.windowWidthDp,
                                        windowConfig.windowHeightDp,
                                        windowConfig.distanceMeters,
                                        windowConfig.opacityPercent,
                                        isCurved,
                                        windowConfig.isPinnedToRoom,
                                        windowConfig.selectedPresetName
                                    )
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = QuestCyan),
                                modifier = Modifier.testTag("switch_curved_panel")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Curvature Radius Slider
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Curve Radius: ${windowConfig.curvatureRadiusDp}dp",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.width(150.dp)
                        )
                        Slider(
                            value = windowConfig.curvatureRadiusDp.toFloat(),
                            onValueChange = { radius ->
                                onUpdateConfig(
                                    radius.toInt(),
                                    windowConfig.windowWidthDp,
                                    windowConfig.windowHeightDp,
                                    windowConfig.distanceMeters,
                                    windowConfig.opacityPercent,
                                    windowConfig.isCurved,
                                    windowConfig.isPinnedToRoom,
                                    windowConfig.selectedPresetName
                                )
                            },
                            valueRange = 600f..2500f,
                            colors = SliderDefaults.colors(thumbColor = QuestCyan, activeTrackColor = QuestCyan),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Spatial Distance Slider (meters)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Distance: ${String.format("%.1f", windowConfig.distanceMeters)}m",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.width(150.dp)
                        )
                        Slider(
                            value = windowConfig.distanceMeters,
                            onValueChange = { dist ->
                                onUpdateConfig(
                                    windowConfig.curvatureRadiusDp,
                                    windowConfig.windowWidthDp,
                                    windowConfig.windowHeightDp,
                                    dist,
                                    windowConfig.opacityPercent,
                                    windowConfig.isCurved,
                                    windowConfig.isPinnedToRoom,
                                    windowConfig.selectedPresetName
                                )
                            },
                            valueRange = 0.5f..4.0f,
                            colors = SliderDefaults.colors(thumbColor = PassthroughEmerald, activeTrackColor = PassthroughEmerald),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Quest 3 Spatial App Launcher Section
        item {
            Text(
                text = "HORIZON OS 2D APP LAUNCHER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = QuestCyan,
                letterSpacing = 1.sp
            )
        }

        items(questAppsList) { app ->
            val isSelected = activeApp?.id == app.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { activeApp = app }
                    .testTag("app_card_${app.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF18203D) else Color(0xFF111424)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(if (isSelected) app.color else CyberCardBorder, CyberCardBorder)
                    )
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(app.color.copy(alpha = 0.2f))
                                .border(1.dp, app.color, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = app.icon,
                                contentDescription = app.title,
                                tint = app.color,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = app.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = app.description,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Active",
                            tint = app.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
