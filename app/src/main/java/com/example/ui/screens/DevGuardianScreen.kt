package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Quest3HardwareStatus
import com.example.ui.components.Spatial3DObjectViewer
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.PassthroughAmber
import com.example.ui.theme.PassthroughEmerald
import com.example.ui.theme.QuestCyan
import com.example.ui.theme.QuestPurple

@Composable
fun DevGuardianScreen(
    hardwareStatus: Quest3HardwareStatus,
    onSetRefreshRate: (Int) -> Unit,
    spatialAudioDegrees: Float,
    onSetSpatialAudioDegrees: (Float) -> Unit,
    isAudioPlaying: Boolean,
    onToggleAudioPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Frame Rate & Refresh Rate Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13172C)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(QuestCyan, PassthroughEmerald)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "FPS",
                                tint = QuestCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DISPLAY REFRESH RATE & PERFORMANCE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "Target: ${hardwareStatus.refreshRateHz} FPS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PassthroughEmerald
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(72, 90, 120).forEach { rate ->
                            val isSelected = hardwareStatus.refreshRateHz == rate
                            Button(
                                onClick = { onSetRefreshRate(rate) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) PassthroughEmerald else Color(0xFF1B213D),
                                    contentColor = if (isSelected) Color.Black else Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("rate_btn_${rate}")
                            ) {
                                Text(text = "${rate}Hz", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // 360° Spatial Audio Sound Origin Test Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14192E)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(QuestPurple, QuestCyan)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Hearing,
                                contentDescription = "Audio",
                                tint = QuestPurple,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "360° HEAD-TRACKED SPATIAL AUDIO TEST",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = onToggleAudioPlay,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAudioPlaying) PassthroughEmerald else QuestCyan,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("toggle_audio_play")
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Audio Play",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAudioPlaying) "PLAYING" else "TEST AUDIO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Sound Origin: ${spatialAudioDegrees.toInt()}°",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.width(130.dp)
                        )
                        Slider(
                            value = spatialAudioDegrees,
                            onValueChange = onSetSpatialAudioDegrees,
                            valueRange = 0f..360f,
                            colors = SliderDefaults.colors(thumbColor = QuestPurple, activeTrackColor = QuestPurple),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("slider_audio_angle")
                        )
                    }
                }
            }
        }

        // Guardian Safety System Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13172C)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(PassthroughAmber, QuestCyan)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Guardian",
                            tint = PassthroughAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GUARDIAN BOUNDARY & PASSTHROUGH CUTOUT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Boundary Active: ${hardwareStatus.guardianStatus}",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    Text(
                        text = "Floor Distance: Calibrated at 0.0m ground level",
                        fontSize = 12.sp,
                        color = PassthroughEmerald
                    )
                }
            }
        }

        // 3D Object & Model Hardware Inspector (Height 380dp)
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ViewInAr,
                        contentDescription = "3D Inspector",
                        tint = QuestCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "QUEST 3 HARDWARE & MODEL INSPECTOR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Spatial3DObjectViewer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                )
            }
        }
    }
}
