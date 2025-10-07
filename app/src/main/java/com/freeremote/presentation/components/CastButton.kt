package com.freeremote.presentation.components

import android.content.Context
import android.view.ContextThemeWrapper
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.framework.CastButtonFactory
import com.freeremote.R

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
            // Create MediaRouteButton with proper theme
            val themedContext = ContextThemeWrapper(context, R.style.Theme_FreeRemoteController)
            MediaRouteButton(themedContext).apply {
                // Wire up the button to Cast framework
                CastButtonFactory.setUpMediaRouteButton(context, this)

                // Apply tint color to the icon
                setRemoteIndicatorDrawable(R.drawable.mr_button_light)
                drawable?.setTint(tint.toArgb())
            }
        },
        update = { button ->
            // Update tint if it changes
            button.drawable?.setTint(tint.toArgb())
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