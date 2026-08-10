package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.HorizonTopBar
import com.example.ui.screens.DevGuardianScreen
import com.example.ui.screens.HandTrackingScreen
import com.example.ui.screens.PassthroughMRScreen
import com.example.ui.screens.Spatial2DWindowScreen
import com.example.ui.screens.SpatialAnchorsScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.Quest3Theme
import com.example.viewmodel.Quest3ViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: Quest3ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Quest3Theme {
                val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
                val isMixedRealityMode by viewModel.isMixedRealityMode.collectAsStateWithLifecycle()
                val passthroughFilter by viewModel.passthroughFilter.collectAsStateWithLifecycle()
                val passthroughOpacity by viewModel.passthroughOpacity.collectAsStateWithLifecycle()
                val isMeshOverlayVisible by viewModel.isMeshOverlayVisible.collectAsStateWithLifecycle()
                val isOcclusionEnabled by viewModel.isOcclusionEnabled.collectAsStateWithLifecycle()
                val windowConfig by viewModel.windowConfig.collectAsStateWithLifecycle()
                val hardwareStatus by viewModel.hardwareStatus.collectAsStateWithLifecycle()
                val handInfo by viewModel.handTrackingState.collectAsStateWithLifecycle()
                val spatialAudioDegrees by viewModel.spatialAudioDegrees.collectAsStateWithLifecycle()
                val isAudioPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
                val detectedObjects by viewModel.detectedRoomObjects.collectAsStateWithLifecycle()
                val savedAnchors by viewModel.savedAnchors.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        HorizonTopBar(
                            selectedTab = selectedTab,
                            onTabSelected = { viewModel.selectTab(it) },
                            isMixedRealityMode = isMixedRealityMode,
                            onModeToggled = { viewModel.setMixedRealityMode(it) },
                            hardwareStatus = hardwareStatus
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(CyberBackground)
                            .padding(innerPadding)
                    ) {
                        if (isMixedRealityMode && selectedTab == 0) {
                            PassthroughMRScreen(
                                filterMode = passthroughFilter,
                                onFilterChanged = { viewModel.setPassthroughFilter(it) },
                                opacity = passthroughOpacity,
                                onOpacityChanged = { viewModel.setPassthroughOpacity(it) },
                                isMeshOverlayVisible = isMeshOverlayVisible,
                                onToggleMeshOverlay = { viewModel.toggleMeshOverlay() },
                                isOcclusionEnabled = isOcclusionEnabled,
                                onToggleOcclusion = { viewModel.toggleOcclusion() },
                                detectedObjects = detectedObjects
                            )
                        } else {
                            when (selectedTab) {
                                0 -> Spatial2DWindowScreen(
                                    windowConfig = windowConfig,
                                    onUpdateConfig = { radius, width, height, distance, opacity, isCurved, isPinned, presetName ->
                                        viewModel.updateWindowConfig(radius, width, height, distance, opacity, isCurved, isPinned, presetName)
                                    }
                                )
                                1 -> PassthroughMRScreen(
                                    filterMode = passthroughFilter,
                                    onFilterChanged = { viewModel.setPassthroughFilter(it) },
                                    opacity = passthroughOpacity,
                                    onOpacityChanged = { viewModel.setPassthroughOpacity(it) },
                                    isMeshOverlayVisible = isMeshOverlayVisible,
                                    onToggleMeshOverlay = { viewModel.toggleMeshOverlay() },
                                    isOcclusionEnabled = isOcclusionEnabled,
                                    onToggleOcclusion = { viewModel.toggleOcclusion() },
                                    detectedObjects = detectedObjects
                                )
                                2 -> SpatialAnchorsScreen(
                                    anchors = savedAnchors,
                                    onCreateAnchor = { type, name, color ->
                                        viewModel.createNewAnchor(type, name, color)
                                    },
                                    onAnchorPositionChanged = { anchor, x, y, z ->
                                        viewModel.updateAnchorPosition(anchor, x, y, z)
                                    },
                                    onAnchorRotationChanged = { anchor, rot ->
                                        viewModel.updateAnchorRotation(anchor, rot)
                                    },
                                    onTogglePinned = { anchor ->
                                        viewModel.toggleAnchorPinned(anchor)
                                    },
                                    onDeleteAnchor = { anchor ->
                                        viewModel.deleteAnchor(anchor)
                                    },
                                    onResetDefaults = {
                                        viewModel.resetAnchorsToDefault()
                                    }
                                )
                                3 -> HandTrackingScreen(
                                    handInfo = handInfo,
                                    onUpdateHandGesture = { isLeft, gesture ->
                                        viewModel.updateHandGesture(isLeft, gesture)
                                    },
                                    onPointerMoved = { x, y ->
                                        viewModel.updatePointerPosition(x, y)
                                    }
                                )
                                4 -> DevGuardianScreen(
                                    hardwareStatus = hardwareStatus,
                                    onSetRefreshRate = { rate -> viewModel.setRefreshRate(rate) },
                                    spatialAudioDegrees = spatialAudioDegrees,
                                    onSetSpatialAudioDegrees = { deg -> viewModel.setSpatialAudioDegrees(deg) },
                                    isAudioPlaying = isAudioPlaying,
                                    onToggleAudioPlay = { viewModel.toggleSpatialAudioPlay() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
