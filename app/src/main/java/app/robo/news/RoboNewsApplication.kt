package app.robo.news

import android.util.Log
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RoboNewsApplication : MultiDexApplication() {

    companion object {
        private val TAG = "RoboNewsApplication"

        private lateinit var myApplication: RoboNewsApplication

        @Synchronized
        fun getInstance(): RoboNewsApplication {
            return myApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        myApplication = this
    }
}