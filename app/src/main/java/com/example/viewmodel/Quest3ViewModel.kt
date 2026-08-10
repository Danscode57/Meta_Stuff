package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.SpatialAnchorEntity
import com.example.model.DetectedRoomObject
import com.example.model.HandGestureType
import com.example.model.HandTrackingInfo
import com.example.model.PassthroughFilterMode
import com.example.model.Quest3HardwareStatus
import com.example.model.SpatialWindowConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class Quest3ViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.spatialAnchorDao()

    // 0: 2D Spatial Window, 1: Passthrough MR, 2: Spatial Anchors, 3: Hand & Gesture, 4: Dev & Guardian
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Global Mode: false = 2D Spatial Panel, true = Mixed Reality Passthrough
    private val _isMixedRealityMode = MutableStateFlow(false)
    val isMixedRealityMode: StateFlow<Boolean> = _isMixedRealityMode.asStateFlow()

    // Passthrough settings
    private val _passthroughFilter = MutableStateFlow(PassthroughFilterMode.FULL_COLOR)
    val passthroughFilter: StateFlow<PassthroughFilterMode> = _passthroughFilter.asStateFlow()

    private val _passthroughOpacity = MutableStateFlow(1.0f)
    val passthroughOpacity: StateFlow<Float> = _passthroughOpacity.asStateFlow()

    private val _isMeshOverlayVisible = MutableStateFlow(true)
    val isMeshOverlayVisible: StateFlow<Boolean> = _isMeshOverlayVisible.asStateFlow()

    private val _isOcclusionEnabled = MutableStateFlow(true)
    val isOcclusionEnabled: StateFlow<Boolean> = _isOcclusionEnabled.asStateFlow()

    // Spatial 2D Window Settings
    private val _windowConfig = MutableStateFlow(SpatialWindowConfig())
    val windowConfig: StateFlow<SpatialWindowConfig> = _windowConfig.asStateFlow()

    // Hardware status
    private val _hardwareStatus = MutableStateFlow(Quest3HardwareStatus())
    val hardwareStatus: StateFlow<Quest3HardwareStatus> = _hardwareStatus.asStateFlow()

    // Hand tracking
    private val _handTrackingState = MutableStateFlow(HandTrackingInfo())
    val handTrackingState: StateFlow<HandTrackingInfo> = _handTrackingState.asStateFlow()

    // Spatial Audio Direction Angle
    private val _spatialAudioDegrees = MutableStateFlow(45f)
    val spatialAudioDegrees: StateFlow<Float> = _spatialAudioDegrees.asStateFlow()

    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    // Room objects detected by passthrough depth sensors
    private val _detectedRoomObjects = MutableStateFlow(
        listOf(
            DetectedRoomObject("obj_1", "Workstation Desk", "Furniture", 1.2f, 0.25f, 0.55f, 0.50f, 0.25f),
            DetectedRoomObject("obj_2", "Primary Monitor", "Display", 1.4f, 0.35f, 0.30f, 0.30f, 0.22f),
            DetectedRoomObject("obj_3", "Ergonomic Chair", "Furniture", 0.9f, 0.80f, 0.60f, 0.25f, 0.35f),
            DetectedRoomObject("obj_4", "OLED Smart TV", "Display", 2.8f, 0.05f, 0.20f, 0.35f, 0.30f),
            DetectedRoomObject("obj_5", "Coffee Table", "Furniture", 1.8f, 0.40f, 0.75f, 0.35f, 0.15f)
        )
    )
    val detectedRoomObjects: StateFlow<List<DetectedRoomObject>> = _detectedRoomObjects.asStateFlow()

    // Room DB anchors
    val savedAnchors: StateFlow<List<SpatialAnchorEntity>> = dao.getAllAnchors().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Pre-populate default anchors if empty
        viewModelScope.launch {
            dao.getAllAnchors().collect { list ->
                if (list.isEmpty()) {
                    seedDefaultAnchors()
                }
            }
        }
    }

    private fun seedDefaultAnchors() {
        viewModelScope.launch {
            val defaults = listOf(
                SpatialAnchorEntity(
                    id = "anc_1",
                    name = "Quest 3 Hologram",
                    type = "HEADSET",
                    posX = 0.0f,
                    posY = 1.3f,
                    posZ = 1.8f,
                    rotationY = 25f,
                    scale = 1.0f,
                    colorHex = "#00F0FF",
                    isPinned = true,
                    timestamp = System.currentTimeMillis()
                ),
                SpatialAnchorEntity(
                    id = "anc_2",
                    name = "Touch Plus Controller",
                    type = "CONTROLLER",
                    posX = -0.6f,
                    posY = 1.0f,
                    posZ = 1.5f,
                    rotationY = 310f,
                    scale = 1.0f,
                    colorHex = "#8A2BE2",
                    isPinned = false,
                    timestamp = System.currentTimeMillis() - 1000
                ),
                SpatialAnchorEntity(
                    id = "anc_3",
                    name = "Cyber Room Portal",
                    type = "CYBER_PORTAL",
                    posX = 0.8f,
                    posY = 1.5f,
                    posZ = 2.4f,
                    rotationY = 180f,
                    scale = 1.2f,
                    colorHex = "#00FF9D",
                    isPinned = true,
                    timestamp = System.currentTimeMillis() - 2000
                ),
                SpatialAnchorEntity(
                    id = "anc_4",
                    name = "Spatial Clock Widget",
                    type = "WIDGET_CLOCK",
                    posX = 0.0f,
                    posY = 2.0f,
                    posZ = 2.0f,
                    rotationY = 0f,
                    scale = 0.9f,
                    colorHex = "#FFB703",
                    isPinned = true,
                    timestamp = System.currentTimeMillis() - 3000
                )
            )
            defaults.forEach { dao.insertAnchor(it) }
        }
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun setMixedRealityMode(enabled: Boolean) {
        _isMixedRealityMode.value = enabled
    }

    fun setPassthroughFilter(filter: PassthroughFilterMode) {
        _passthroughFilter.value = filter
    }

    fun setPassthroughOpacity(opacity: Float) {
        _passthroughOpacity.value = opacity.coerceIn(0.1f, 1.0f)
    }

    fun toggleMeshOverlay() {
        _isMeshOverlayVisible.value = !_isMeshOverlayVisible.value
    }

    fun toggleOcclusion() {
        _isOcclusionEnabled.value = !_isOcclusionEnabled.value
    }

    fun updateWindowConfig(
        radius: Int = windowConfig.value.curvatureRadiusDp,
        width: Int = windowConfig.value.windowWidthDp,
        height: Int = windowConfig.value.windowHeightDp,
        distance: Float = windowConfig.value.distanceMeters,
        opacity: Float = windowConfig.value.opacityPercent,
        isCurved: Boolean = windowConfig.value.isCurved,
        isPinned: Boolean = windowConfig.value.isPinnedToRoom,
        presetName: String = windowConfig.value.selectedPresetName
    ) {
        _windowConfig.value = SpatialWindowConfig(
            curvatureRadiusDp = radius,
            windowWidthDp = width,
            windowHeightDp = height,
            distanceMeters = distance,
            opacityPercent = opacity,
            isCurved = isCurved,
            isPinnedToRoom = isPinned,
            selectedPresetName = presetName
        )
    }

    fun setRefreshRate(rateHz: Int) {
        _hardwareStatus.value = _hardwareStatus.value.copy(refreshRateHz = rateHz)
    }

    fun updateHandGesture(isLeft: Boolean, gesture: HandGestureType, strength: Float = 0.9f) {
        val current = _handTrackingState.value
        _handTrackingState.value = if (isLeft) {
            current.copy(leftGesture = gesture, leftPinchStrength = strength)
        } else {
            current.copy(rightGesture = gesture, rightPinchStrength = strength)
        }
    }

    fun updatePointerPosition(xRatio: Float, yRatio: Float) {
        _handTrackingState.value = _handTrackingState.value.copy(
            pointerXRatio = xRatio.coerceIn(0f, 1f),
            pointerYRatio = yRatio.coerceIn(0f, 1f)
        )
    }

    fun setSpatialAudioDegrees(degrees: Float) {
        _spatialAudioDegrees.value = (degrees % 360f + 360f) % 360f
    }

    fun toggleSpatialAudioPlay() {
        _isAudioPlaying.value = !_isAudioPlaying.value
    }

    fun createNewAnchor(type: String, name: String, colorHex: String) {
        viewModelScope.launch {
            val newAnchor = SpatialAnchorEntity(
                id = "anc_" + UUID.randomUUID().toString().take(8),
                name = name,
                type = type,
                posX = (Math.random() * 2.0 - 1.0).toFloat(),
                posY = (1.0 + Math.random() * 0.8).toFloat(),
                posZ = (1.2 + Math.random() * 2.0).toFloat(),
                rotationY = (Math.random() * 360).toFloat(),
                scale = 1.0f,
                colorHex = colorHex,
                isPinned = false,
                timestamp = System.currentTimeMillis()
            )
            dao.insertAnchor(newAnchor)
        }
    }

    fun updateAnchorPosition(anchor: SpatialAnchorEntity, newX: Float, newY: Float, newZ: Float) {
        viewModelScope.launch {
            dao.updateAnchor(anchor.copy(posX = newX, posY = newY, posZ = newZ))
        }
    }

    fun updateAnchorRotation(anchor: SpatialAnchorEntity, newRotationY: Float) {
        viewModelScope.launch {
            dao.updateAnchor(anchor.copy(rotationY = newRotationY))
        }
    }

    fun toggleAnchorPinned(anchor: SpatialAnchorEntity) {
        viewModelScope.launch {
            dao.updateAnchor(anchor.copy(isPinned = !anchor.isPinned))
        }
    }

    fun deleteAnchor(anchor: SpatialAnchorEntity) {
        viewModelScope.launch {
            dao.deleteAnchor(anchor)
        }
    }

    fun resetAnchorsToDefault() {
        viewModelScope.launch {
            dao.clearAll()
            seedDefaultAnchors()
        }
    }
}
