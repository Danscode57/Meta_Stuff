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
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DetectedRoomObject
import com.example.model.PassthroughFilterMode
import com.example.ui.components.PassthroughView
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.PassthroughAmber
import com.example.ui.theme.PassthroughEmerald
import com.example.ui.theme.QuestCyan
import com.example.ui.theme.QuestPurple

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PassthroughMRScreen(
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
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Furniture", "Display")

    val filteredObjects = if (selectedCategory == "All") {
        detectedObjects
    } else {
        detectedObjects.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Live Passthrough Camera & Depth Shader Canvas (Height 380dp)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = "Passthrough",
                            tint = QuestCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "QUEST 3 DUAL 4MP RGB PASSTHROUGH",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(PassthroughEmerald)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE FEED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PassthroughEmerald
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                PassthroughView(
                    filterMode = filterMode,
                    onFilterChanged = onFilterChanged,
                    opacity = opacity,
                    onOpacityChanged = onOpacityChanged,
                    isMeshOverlayVisible = isMeshOverlayVisible,
                    onToggleMeshOverlay = onToggleMeshOverlay,
                    isOcclusionEnabled = isOcclusionEnabled,
                    onToggleOcclusion = onToggleOcclusion,
                    detectedObjects = filteredObjects,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                )
            }
        }

        // Room Objects Scan Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = "Room Sensor",
                        tint = PassthroughEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DETECTED ROOM SURFACES & OBJECTS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PassthroughEmerald,
                        letterSpacing = 1.sp
                    )
                }

                // Category Filter Pills
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(text = cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PassthroughEmerald,
                                selectedLabelColor = Color.Black
                            ),
                            modifier = Modifier.testTag("cat_chip_${cat}")
                        )
                    }
                }
            }
        }

        // List of Detected Room Objects
        items(filteredObjects) { obj ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("detected_obj_${obj.id}"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF12162B)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(
                            when (obj.category) {
                                "Furniture" -> PassthroughEmerald
                                "Display" -> QuestCyan
                                else -> PassthroughAmber
                            },
                            CyberCardBorder
                        )
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (obj.category) {
                                        "Furniture" -> PassthroughEmerald.copy(alpha = 0.2f)
                                        "Display" -> QuestCyan.copy(alpha = 0.2f)
                                        else -> PassthroughAmber.copy(alpha = 0.2f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CenterFocusWeak,
                                contentDescription = obj.name,
                                tint = when (obj.category) {
                                    "Furniture" -> PassthroughEmerald
                                    "Display" -> QuestCyan
                                    else -> PassthroughAmber
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = obj.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Category: ${obj.category}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // Distance Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E2642))
                            .border(1.dp, QuestCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${String.format("%.1f", obj.distanceMeters)}m away",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuestCyan
                        )
                    }
                }
            }
        }
    }
}
