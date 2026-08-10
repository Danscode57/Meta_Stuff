package com.example.model

enum class PassthroughFilterMode(val displayName: String, val description: String) {
    FULL_COLOR("RGB Passthrough", "Native dual 4MP full-color passthrough cameras with real-time depth blend"),
    DEPTH_MESH("Spatial Room Mesh", "LiDAR/Depth sensor real-time room geometry wireframe mesh"),
    NIGHT_VISION("Infrared Vision", "Enhanced low-light infrared passthrough with emerald amplification"),
    CYBER_THERMAL("Thermal Spatial", "Cyberpunk heatmap gradient isolating warm object reflections"),
    EDGE_CONTOUR("Contour Detector", "High-contrast structural edge highlighting for spatial orientation")
}

data class SpatialWindowConfig(
    val curvatureRadiusDp: Int = 1200,
    val windowWidthDp: Int = 850,
    val windowHeightDp: Int = 540,
    val distanceMeters: Float = 1.5f,
    val opacityPercent: Float = 0.90f,
    val isCurved: Boolean = true,
    val isPinnedToRoom: Boolean = false,
    val selectedPresetName: String = "Single Focus Curve"
)

data class Quest3HardwareStatus(
    val batteryPercent: Int = 94,
    val isCharging: Boolean = false,
    val ipdDistanceMm: Int = 63,
    val refreshRateHz: Int = 90,
    val passthroughLatencyMs: Float = 2.4f,
    val wifiSpeedMbps: Int = 1200,
    val roomTemperatureC: Int = 28,
    val memoryUsageGb: Float = 4.2f,
    val memoryTotalGb: Float = 8.0f,
    val guardianStatus: String = "Room-scale (3.5m x 3.0m)"
)

enum class HandGestureType(val label: String, val iconName: String) {
    NONE("Neutral Hand", "PanTool"),
    INDEX_PINCH("Air Pinch", "TouchApp"),
    PALM_UP("Horizon Wrist Menu", "HomeWork"),
    INDEX_POINT("Direct Spatial Touch", "AdsClick"),
    FIST_GRAB("Physics Grip", "FrontHand"),
    OPEN_PALM("Raycast Beam", "FlashOn")
}

data class HandTrackingInfo(
    val isLeftHandTracked: Boolean = true,
    val isRightHandTracked: Boolean = true,
    val leftGesture: HandGestureType = HandGestureType.INDEX_PINCH,
    val rightGesture: HandGestureType = HandGestureType.OPEN_PALM,
    val leftPinchStrength: Float = 0.85f,
    val rightPinchStrength: Float = 0.20f,
    val pointerXRatio: Float = 0.5f,
    val pointerYRatio: Float = 0.5f
)

data class DetectedRoomObject(
    val id: String,
    val name: String,
    val category: String, // Desk, Furniture, Display, Appliance, Wall
    val distanceMeters: Float,
    val posXRatio: Float,
    val posYRatio: Float,
    val widthRatio: Float,
    val heightRatio: Float
)
