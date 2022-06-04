package com.example.campainhasmart

import android.app.Application
import timber.log.Timber

class CampainhaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}