package com.freeremote.presentation.components

import android.app.Activity
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
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
    val context = LocalContext.current
    val activity = context as? Activity

    val isCastAvailable = remember {
        try {
            CastContext.getSharedInstance(context)
            true
        } catch (e: Exception) {
            Log.e("CastButton", "Cast is not available on this device", e)
            false
        }
    }

    if (isCastAvailable && activity is FragmentActivity) {
        AndroidView(
            modifier = modifier,
            factory = { ctx ->
                // Create a themed context with a proper background color to avoid translucent background error
                val themedContext = ContextThemeWrapper(activity, androidx.mediarouter.R.style.Theme_MediaRouter_Light)

                // Create a MediaRouteButton with the themed context
                MediaRouteButton(themedContext).apply {
                    try {
                        // Wire up the button to Cast framework using the activity context
                        CastButtonFactory.setUpMediaRouteButton(activity, this)
                        Log.d("CastButton", "Cast button initialized successfully with themed FragmentActivity")
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
    } else if (isCastAvailable) {
        Log.w("CastButton", "Cast is available but activity is not FragmentActivity: ${activity?.javaClass?.simpleName}")
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