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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.example.data.SpatialAnchorEntity
import com.example.ui.components.SpatialCanvasView
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.PassthroughAmber
import com.example.ui.theme.PassthroughEmerald
import com.example.ui.theme.QuestCyan
import com.example.ui.theme.QuestPurple

data class PresetAnchorTemplate(
    val type: String,
    val name: String,
    val colorHex: String
)

val anchorTemplates = listOf(
    PresetAnchorTemplate("HEADSET", "Quest 3 Hologram", "#00F0FF"),
    PresetAnchorTemplate("CONTROLLER", "Touch Plus Controller", "#8A2BE2"),
    PresetAnchorTemplate("CYBER_PORTAL", "Cyber Room Portal", "#00FF9D"),
    PresetAnchorTemplate("WIDGET_CLOCK", "Spatial Clock Widget", "#FFB703"),
    PresetAnchorTemplate("VIRTUAL_SCREEN", "Virtual Floating Screen", "#3A86FF")
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpatialAnchorsScreen(
    anchors: List<SpatialAnchorEntity>,
    onCreateAnchor: (type: String, name: String, colorHex: String) -> Unit,
    onAnchorPositionChanged: (SpatialAnchorEntity, Float, Float, Float) -> Unit,
    onAnchorRotationChanged: (SpatialAnchorEntity, Float) -> Unit,
    onTogglePinned: (SpatialAnchorEntity) -> Unit,
    onDeleteAnchor: (SpatialAnchorEntity) -> Unit,
    onResetDefaults: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Interactive 3D Spatial Canvas (Height 360dp)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PinDrop,
                            contentDescription = "Spatial Anchors",
                            tint = QuestCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3D SPATIAL ANCHORS ROOM CANVAS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onResetDefaults,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PassthroughAmber),
                        modifier = Modifier.testTag("reset_anchors_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Reset Defaults", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                SpatialCanvasView(
                    anchors = anchors,
                    onAnchorPositionChanged = onAnchorPositionChanged,
                    onAnchorRotationChanged = onAnchorRotationChanged,
                    onTogglePinned = onTogglePinned,
                    onDeleteAnchor = onDeleteAnchor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                )
            }
        }

        // Drop New Anchor Action Bar
        item {
            Column {
                Text(
                    text = "DROP NEW 3D SPATIAL ANCHOR",
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
                    anchorTemplates.forEach { template ->
                        Button(
                            onClick = { onCreateAnchor(template.type, template.name, template.colorHex) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B223D)),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(QuestCyan, QuestPurple))),
                            modifier = Modifier.testTag("add_template_${template.type}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = QuestCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = template.name, fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Active Persistent Anchors List Section
        item {
            Text(
                text = "PERSISTENT ROOM ANCHORS (${anchors.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PassthroughEmerald,
                letterSpacing = 1.sp
            )
        }

        items(anchors) { anchor ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("anchor_item_${anchor.id}"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13172C)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(QuestCyan, CyberCardBorder)))
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
                                .background(QuestCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ViewInAr,
                                contentDescription = anchor.name,
                                tint = QuestCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = anchor.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "X: ${String.format("%.2f", anchor.posX)}m | Y: ${String.format("%.2f", anchor.posY)}m | Z: ${String.format("%.2f", anchor.posZ)}m",
                                fontSize = 12.sp,
                                color = PassthroughEmerald
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onTogglePinned(anchor) }) {
                            Icon(
                                imageVector = if (anchor.isPinned) Icons.Default.PushPin else Icons.Default.Pin,
                                contentDescription = "Pin",
                                tint = if (anchor.isPinned) PassthroughAmber else Color.Gray
                            )
                        }
                        IconButton(onClick = { onDeleteAnchor(anchor) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFFF5252)
                            )
                        }
                    }
                }
            }
        }
    }
}
