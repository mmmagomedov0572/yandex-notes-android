package com.mmmagomedov.notes.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AndroidNotesApplication: Application() {
    override fun onCreate() {
        super.onCreate()

    }
}