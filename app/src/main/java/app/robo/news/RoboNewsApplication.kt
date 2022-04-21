package app.robo.news

import android.util.Log
import androidx.multidex.MultiDexApplication
import app.robo.news.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class RoboNewsApplication : MultiDexApplication() {

    companion object {
        private const val TAG = "RoboNewsApplication"

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
        startKoin {
            androidContext(this@RoboNewsApplication)
            modules(appModule)
        }
    }
}