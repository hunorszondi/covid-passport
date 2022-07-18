package com.hunorszondi.covidpassport

import android.app.Application
import com.pusher.pushnotifications.PushNotifications

import com.hunorszondi.covidpassport.utils.ResourceUtil

/**
 * Entering point of the application. Used for initializing utils.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ResourceUtil.createInstance(applicationContext)
        Session.createInstance(applicationContext)
        PushNotifications.start(applicationContext, Config().pusherInstance)
    }
}