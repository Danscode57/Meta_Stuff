package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FrontHand
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HandGestureType
import com.example.model.HandTrackingInfo
import com.example.ui.components.HandTrackingCanvas
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.PassthroughAmber
import com.example.ui.theme.PassthroughEmerald
import com.example.ui.theme.QuestCyan
import com.example.ui.theme.QuestPurple

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HandTrackingScreen(
    handInfo: HandTrackingInfo,
    onUpdateHandGesture: (isLeft: Boolean, gesture: HandGestureType) -> Unit,
    onPointerMoved: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var airTapCount by remember { mutableIntStateOf(14) }
    var hapticTestCount by remember { mutableIntStateOf(8) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Hand Tracking Skeleton & Raycast Canvas
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = "Hand Tracking",
                            tint = QuestCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "QUEST 3 HAND TRACKING & RAYCAST SANDBOX",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }

                    // Air Tap Counter Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1B233D))
                            .border(1.dp, QuestCyan, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "AIR TAPS: $airTapCount",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuestCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                HandTrackingCanvas(
                    handInfo = handInfo,
                    onPointerMoved = onPointerMoved,
                    onAirTapTriggered = { airTapCount++ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                )
            }
        }

        // Gesture Selector Matrix Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13172C)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(QuestCyan, QuestPurple)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TEST HAND GESTURE STATES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuestCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HandGestureType.entries.forEach { gesture ->
                            val isLeftActive = handInfo.leftGesture == gesture
                            val isRightActive = handInfo.rightGesture == gesture

                            Button(
                                onClick = {
                                    onUpdateHandGesture(true, gesture)
                                    onUpdateHandGesture(false, gesture)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isLeftActive || isRightActive) QuestCyan else Color(0xFF1C223B)
                                ),
                                modifier = Modifier.testTag("gesture_btn_${gesture.name}")
                            ) {
                                Text(
                                    text = gesture.label,
                                    fontSize = 12.sp,
                                    color = if (isLeftActive || isRightActive) Color.Black else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Touch Plus Controller Haptics Test Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14182C)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(PassthroughEmerald, QuestCyan)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = "Haptics",
                                tint = PassthroughEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TOUCH PLUS HAPTIC FEEDBACK TEST",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "Rumbles: $hapticTestCount",
                            fontSize = 12.sp,
                            color = PassthroughEmerald
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { hapticTestCount++ },
                            colors = ButtonDefaults.buttonColors(containerColor = QuestCyan),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("haptic_soft_btn")
                        ) {
                            Text(text = "Soft Pulse (UI Tap)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { hapticTestCount += 2 },
                            colors = ButtonDefaults.buttonColors(containerColor = QuestPurple),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("haptic_heavy_btn")
                        ) {
                            Text(text = "Heavy Rumble (Impact)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
