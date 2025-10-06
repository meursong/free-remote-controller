package com.freeremote.presentation.screens.remote

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteControlScreen(
    navController: NavController,
    viewModel: RemoteViewModel = hiltViewModel()
) {
    val haptic = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("거실 TV", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* 기기 설정 */ }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Device Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Power Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    RemoteButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.sendCommand("POWER")
                        },
                        modifier = Modifier.size(60.dp),
                        backgroundColor = Color(0xFFFF453A),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Filled.PowerSettingsNew,
                            contentDescription = "Power",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Volume and Channel Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Volume Controls
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("볼륨", color = Color.Gray, fontSize = 12.sp)
                        RemoteButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.sendCommand("VOL_UP")
                            },
                            modifier = Modifier.size(60.dp)
                        ) {
                            Icon(
                                Icons.Filled.VolumeUp,
                                contentDescription = "Volume Up",
                                tint = Color.White
                            )
                        }
                        RemoteButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.sendCommand("VOL_DOWN")
                            },
                            modifier = Modifier.size(60.dp)
                        ) {
                            Icon(
                                Icons.Filled.VolumeDown,
                                contentDescription = "Volume Down",
                                tint = Color.White
                            )
                        }
                    }

                    // Mute Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.height(156.dp)
                    ) {
                        RemoteButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.sendCommand("MUTE")
                            },
                            modifier = Modifier.size(60.dp)
                        ) {
                            Icon(
                                Icons.Filled.VolumeOff,
                                contentDescription = "Mute",
                                tint = Color.White
                            )
                        }
                    }

                    // Channel Controls
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("채널", color = Color.Gray, fontSize = 12.sp)
                        RemoteButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.sendCommand("CH_UP")
                            },
                            modifier = Modifier.size(60.dp)
                        ) {
                            Icon(
                                Icons.Filled.KeyboardArrowUp,
                                contentDescription = "Channel Up",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        RemoteButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.sendCommand("CH_DOWN")
                            },
                            modifier = Modifier.size(60.dp)
                        ) {
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Channel Down",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                // Direction Pad
                DirectionPad(
                    onUpClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.sendCommand("UP")
                    },
                    onDownClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.sendCommand("DOWN")
                    },
                    onLeftClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.sendCommand("LEFT")
                    },
                    onRightClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.sendCommand("RIGHT")
                    },
                    onCenterClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.sendCommand("OK")
                    }
                )

                // Control Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RemoteButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.sendCommand("BACK")
                        },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    RemoteButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.sendCommand("HOME")
                        },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = "Home",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    RemoteButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.sendCommand("MENU")
                        },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = "Menu",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Number Pad
                NumberPad(
                    onNumberClick = { number ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.sendCommand("NUM_$number")
                    }
                )
            }
        }
    }
}

@Composable
fun RemoteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF2C2C2E),
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(12.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (isPressed) backgroundColor.copy(alpha = 0.6f)
                else backgroundColor
            )
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(color = Color.White, bounded = true)
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun DirectionPad(
    onUpClick: () -> Unit,
    onDownClick: () -> Unit,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    onCenterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(200.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Up button
        RemoteButton(
            onClick = onUpClick,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.TopCenter)
        ) {
            Icon(
                Icons.Filled.KeyboardArrowUp,
                contentDescription = "Up",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // Down button
        RemoteButton(
            onClick = onDownClick,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.BottomCenter)
        ) {
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = "Down",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // Left button
        RemoteButton(
            onClick = onLeftClick,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.CenterStart)
        ) {
            Icon(
                Icons.Filled.KeyboardArrowLeft,
                contentDescription = "Left",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // Right button
        RemoteButton(
            onClick = onRightClick,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.CenterEnd)
        ) {
            Icon(
                Icons.Filled.KeyboardArrowRight,
                contentDescription = "Right",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // Center OK button
        RemoteButton(
            onClick = onCenterClick,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.Center),
            backgroundColor = Color(0xFF48484A),
            shape = CircleShape
        ) {
            Text(
                "OK",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun NumberPad(
    onNumberClick: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 1..3) {
                NumberButton(number = i, onClick = { onNumberClick(i) })
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 4..6) {
                NumberButton(number = i, onClick = { onNumberClick(i) })
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 7..9) {
                NumberButton(number = i, onClick = { onNumberClick(i) })
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            NumberButton(number = 0, onClick = { onNumberClick(0) })
        }
    }
}

@Composable
fun NumberButton(
    number: Int,
    onClick: () -> Unit
) {
    RemoteButton(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        backgroundColor = Color(0xFF3A3A3C)
    ) {
        Text(
            text = number.toString(),
            color = Color.White,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp
        )
    }
}