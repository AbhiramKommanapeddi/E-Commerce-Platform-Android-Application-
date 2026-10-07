package com.tutedude.ecommerce

import android.app.Application
import com.tutedude.ecommerce.util.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EcommerceApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize notification channels
        NotificationHelper.createNotificationChannel(this)
    }
}
