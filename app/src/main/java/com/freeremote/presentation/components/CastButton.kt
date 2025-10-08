package com.freeremote.presentation.components

import android.util.Log
import androidx.compose.runtime.Composable
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
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MediaRouteButton(context).apply {
                try {
                    // Ensure CastContext is initialized before setting up the button
                    CastContext.getSharedInstance(context)

                    // Wire up the button to Cast framework
                    CastButtonFactory.setUpMediaRouteButton(context, this)

                    Log.d("CastButton", "Cast button initialized successfully")
                } catch (e: Exception) {
                    Log.e("CastButton", "Failed to initialize Cast button", e)
                    // Button will still be created but won't function if Cast is not available
                    isEnabled = false
                }
            }
        },
        update = { button ->
            // MediaRouteButton handles its own state internally
        }
    )
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