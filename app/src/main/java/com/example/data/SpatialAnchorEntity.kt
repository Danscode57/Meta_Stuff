package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spatial_anchors")
data class SpatialAnchorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // HEADSET, CONTROLLER, VIRTUAL_SCREEN, CYBER_PORTAL, ORB, WIDGET_CLOCK
    val posX: Float,
    val posY: Float,
    val posZ: Float,
    val rotationY: Float,
    val scale: Float,
    val colorHex: String,
    val isPinned: Boolean,
    val timestamp: Long
)
