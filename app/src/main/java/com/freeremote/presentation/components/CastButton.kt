package com.freeremote.presentation.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext

/**
 * Compose wrapper for MediaRouteButton that enables Cast functionality.
 * This button automatically shows available Cast devices and handles connections.
 *
 * @param modifier Modifier for the button
 * @param tint Color tint for the Cast icon
 */
@Composable
fun CastButton(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isCastAvailable = remember {
        try {
            CastContext.getSharedInstance(context)
            true
        } catch (e: Exception) {
            Log.e("CastButton", "Cast is not available on this device", e)
            false
        }
    }

    if (isCastAvailable) {
        AndroidView(
            modifier = modifier,
            factory = { ctx ->
                MediaRouteButton(ctx).apply {
                    try {
                        // Wire up the button to Cast framework
                        CastButtonFactory.setUpMediaRouteButton(ctx, this)
                        Log.d("CastButton", "Cast button initialized successfully")
                    } catch (e: Exception) {
                        Log.e("CastButton", "Failed to setup Cast button", e)
                        visibility = android.view.View.GONE
                    }
                }
            },
            update = { button ->
                // MediaRouteButton handles its own state internally
            }
        )
    }
}

/**
 * Standalone Cast button that can be used anywhere in the app.
 * Shows connection state and available devices automatically.
 */
@Composable
fun StandaloneCastButton(
    modifier: Modifier = Modifier
) {
    CastButton(
        modifier = modifier,
        tint = Color.White
    )
}