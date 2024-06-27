package kr.sjh.pickup

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PickUpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}