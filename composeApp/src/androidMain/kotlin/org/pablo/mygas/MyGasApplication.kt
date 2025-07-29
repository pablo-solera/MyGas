package org.pablo.mygas

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.pablo.mygas.di.initKoin
import org.pablo.mygas.utils.AppContext

class MyGasApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // AppContext.setUp(applicationContext)
        initKoin{
            androidContext(this@MyGasApplication)
        }
    }
}