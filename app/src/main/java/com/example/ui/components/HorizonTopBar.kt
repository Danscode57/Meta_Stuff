package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.Quest3HardwareStatus
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.PassthroughEmerald
import com.example.ui.theme.QuestCyan
import com.example.ui.theme.QuestPurple

data class TabItem(val title: String, val icon: ImageVector)

val navTabs = listOf(
    TabItem("2D Panel", Icons.Default.Tv),
    TabItem("Passthrough MR", Icons.Default.Camera),
    TabItem("Spatial Anchors", Icons.Default.PinDrop),
    TabItem("Hand & Gesture", Icons.Default.Handshake),
    TabItem("Dev & Guardian", Icons.Default.DeveloperMode)
)

@Composable
fun HorizonTopBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    isMixedRealityMode: Boolean,
    onModeToggled: (Boolean) -> Unit,
    hardwareStatus: Quest3HardwareStatus
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CyberSurface,
                        CyberSurface.copy(alpha = 0.95f)
                    )
                )
            )
            .border(1.dp, CyberCardBorder)
            .padding(top = 12.dp, bottom = 4.dp)
    ) {
        // Top System Bar Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quest 3 Identity Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(QuestCyan, QuestPurple))
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ViewInAr,
                        contentDescription = "Quest 3 Logo",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "META QUEST 3",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PassthroughEmerald.copy(alpha = 0.2f))
                                .border(0.5.dp, PassthroughEmerald, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${hardwareStatus.refreshRateHz}Hz",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PassthroughEmerald
                            )
                        }
                    }
                    Text(
                        text = "Snapdragon XR2 Gen 2 • Spatial Panel OS",
                        fontSize = 11.sp,
                        color = Color.LightGray.copy(alpha = 0.8f)
                    )
                }
            }

            // Mode Toggle Pill (2D Panel vs Passthrough MR)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF181C2E))
                    .border(1.dp, QuestCyan.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ModePillButton(
                    text = "2D Panel",
                    icon = Icons.Default.Tv,
                    isSelected = !isMixedRealityMode,
                    onClick = { onModeToggled(false) },
                    testTag = "mode_2d_button"
                )
                ModePillButton(
                    text = "Passthrough MR",
                    icon = Icons.Default.ViewInAr,
                    isSelected = isMixedRealityMode,
                    onClick = { onModeToggled(true) },
                    testTag = "mode_mr_button"
                )
            }

            // Telemetry Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TelemetryBadge(
                    icon = Icons.Default.BatteryChargingFull,
                    text = "${hardwareStatus.batteryPercent}%",
                    tint = PassthroughEmerald
                )
                TelemetryBadge(
                    icon = Icons.Default.Wifi,
                    text = "${hardwareStatus.passthroughLatencyMs}ms",
                    tint = QuestCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Navigation Bar
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = QuestCyan,
            edgePadding = 12.dp,
            divider = {}
        ) {
            navTabs.forEachIndexed { index, tab ->
                val isSelected = selectedTab == index
                val tabColor by animateColorAsState(
                    targetValue = if (isSelected) QuestCyan else Color.Gray,
                    label = "tabColor"
                )

                Tab(
                    selected = isSelected,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.testTag("tab_${index}"),
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = tabColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.title,
                                color = tabColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ModePillButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) QuestCyan else Color.Transparent,
        label = "pillBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.Black else Color.White,
        label = "pillText"
    )

    Row(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = textColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun TelemetryBadge(icon: ImageVector, text: String, tint: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF141829))
            .border(0.5.dp, tint.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = tint,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}
