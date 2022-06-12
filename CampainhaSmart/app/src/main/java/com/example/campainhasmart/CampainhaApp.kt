package com.example.campainhasmart

import android.app.Application
import com.example.campainhasmart.model.Repository
import timber.log.Timber

class CampainhaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(object : Timber.DebugTree() {
            /**
             * Override [log] to modify the tag and add a "global tag" prefix to it. You can rename the String "global_tag_" as you see fit.
             */
            override fun log(
                priority: Int, tag: String?, message: String, t: Throwable?
            ) {
                super.log(priority, "Timber_$tag", message, t)
            }

        })

        val repository= Repository.getRepository(applicationContext)


    }
}