package org.pablo.mygas

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.pablo.mygas.core.di.initKoin

class MyGasApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@MyGasApplication)
        }
    }
}