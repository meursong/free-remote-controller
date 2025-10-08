package com.freeremote.presentation.screens.chromecast

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.freeremote.domain.manager.DialManager
import com.freeremote.presentation.components.CastButton

@Composable
fun ChromecastControlScreen(
    navController: NavController,
    viewModel: ChromecastViewModel = hiltViewModel()
) {
    val haptic = LocalHapticFeedback.current
    val deviceState by viewModel.castState.collectAsState()
    val dialState by viewModel.dialState.collectAsState()
    val volume by viewModel.volume.collectAsState()

    // Get device name from either Cast or DIAL connection
    val deviceName = when (val state = deviceState) {
        is ChromecastViewModel.CastState.Connected -> state.deviceName
        else -> when (val dState = dialState) {
            is DialManager.ConnectionState.Connected -> dState.device.name
            else -> "light house"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with device name and controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Settings button
                IconButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.openSettings()
                }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Device name with dropdown
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Circle,
                        contentDescription = "Status",
                        tint = if (deviceState is ChromecastViewModel.CastState.Connected ||
                                  dialState is DialManager.ConnectionState.Connected)
                            Color.Green else Color.Gray,
                        modifier = Modifier.size(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = deviceName,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Select device",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Power button
                IconButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.disconnect()
                }) {
                    Icon(
                        Icons.Default.PowerSettingsNew,
                        contentDescription = "Power",
                        tint = Color.Green,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // App Launcher Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AppLauncherButton(
                    appName = "Netflix",
                    backgroundColor = Color(0xFFE50914),
                    textColor = Color.White,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.launchNetflix()
                    }
                )
                AppLauncherButton(
                    appName = "YouTube",
                    backgroundColor = Color(0xFFFF0000),
                    textColor = Color.White,
                    isYouTube = true,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.launchYouTube()
                    }
                )
                AppLauncherButton(
                    appName = "티빙\n(TVING)",
                    backgroundColor = Color(0xFFFF153C),
                    textColor = Color.White,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.launchDisneyPlus() // Placeholder
                    }
                )
                AppLauncherButton(
                    appName = "Wavve\n(웨이브)",
                    backgroundColor = Color(0xFF0066FF),
                    textColor = Color.White,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.launchPrimeVideo() // Placeholder
                    }
                )
            }

            // Circular D-Pad Control
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                // Outer circle with gradient border
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFF6A4C93),
                                    Color(0xFF4361EE),
                                    Color(0xFF3F37C9),
                                    Color(0xFF6A4C93)
                                )
                            )
                        )
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                )

                // Direction buttons
                // Up
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        // Add navigation up logic
                    },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 20.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Up",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Down
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        // Add navigation down logic
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Down",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Left
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.skipBackward()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 20.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Left",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Right
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.skipForward()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 20.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Right",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Center OK button
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFF6A4C93),
                                    Color(0xFF4361EE),
                                    Color(0xFF3F37C9),
                                    Color(0xFF6A4C93)
                                )
                            ),
                            shape = CircleShape
                        )
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.togglePlayPause()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Empty center button - you can add "OK" text or play/pause icon here if needed
                }
            }

            // Page indicator dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == 0) Color.White else Color.Gray
                            )
                    )
                }
            }

            // Bottom Control Buttons
            Column {
                // Volume and Channel controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Volume controls
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        VolumeButton(
                            icon = Icons.Default.Add,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.setVolume(minOf(1f, volume + 0.1f))
                            }
                        )
                        Text(
                            "VOL",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            "${(volume * 100).toInt()}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        VolumeButton(
                            icon = Icons.Default.Remove,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.setVolume(maxOf(0f, volume - 0.1f))
                            }
                        )
                    }

                    // Center buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BottomControlButton(
                            icon = Icons.Default.Keyboard,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                // Add keyboard action
                            }
                        )
                        BottomControlButton(
                            icon = Icons.Default.Home,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.goHome()
                            }
                        )
                        BottomControlButton(
                            icon = Icons.Default.Apps,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                // Add apps grid action
                            }
                        )
                    }

                    // Channel controls (placeholder)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        VolumeButton(
                            icon = Icons.Default.KeyboardArrowUp,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                // Add channel up
                            }
                        )
                        Text(
                            "CH",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        VolumeButton(
                            icon = Icons.Default.KeyboardArrowDown,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                // Add channel down
                            }
                        )
                    }
                }

                // Bottom row buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BottomControlButton(
                        icon = Icons.Default.VolumeUp,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.toggleMute()
                        }
                    )
                    BottomControlButton(
                        icon = Icons.Default.Mic,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            // Add voice control
                        }
                    )
                    BottomControlButton(
                        icon = Icons.Default.ArrowBack,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            navController.navigateUp()
                        }
                    )
                }

                // Navigation bar buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Icon(
                        Icons.Default.Circle,
                        contentDescription = "Home",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Cast button (hidden but functional)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            CastButton(
                modifier = Modifier.size(0.dp) // Hidden but still functional
            )
        }
    }
}

@Composable
fun AppLauncherButton(
    appName: String,
    backgroundColor: Color,
    textColor: Color,
    isYouTube: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 75.dp, height = 75.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isYouTube) {
            // YouTube specific design with play button
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "YouTube",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        } else {
            Text(
                text = when {
                    appName == "Netflix" -> "N"
                    appName.contains("Wavve") -> "W"
                    appName.contains("TVING") -> "티빙"
                    else -> appName.take(1)
                },
                color = textColor,
                fontSize = if (appName.contains("TVING")) 16.sp else 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun VolumeButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 60.dp, height = 40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4361EE),
                        Color(0xFF3F37C9)
                    )
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun BottomControlButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color(0xFF1C1C1E))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}