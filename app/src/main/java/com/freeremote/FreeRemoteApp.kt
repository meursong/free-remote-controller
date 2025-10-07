package com.freeremote

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FreeRemoteApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}