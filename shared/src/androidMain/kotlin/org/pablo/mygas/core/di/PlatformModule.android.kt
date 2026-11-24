package org.pablo.mygas.core.di

import androidx.room.RoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import org.pablo.mygas.core.utils.AndroidMapLauncher
import org.pablo.mygas.core.utils.MapLauncher
import org.pablo.mygas.data.local.database.AppDatabase
import org.pablo.mygas.data.local.database.androidDatabaseBuilder

actual val platformModule: Module = module {
    single<MapLauncher> { AndroidMapLauncher(androidContext()) }
    single<RoomDatabase.Builder<AppDatabase>> { androidDatabaseBuilder(androidContext()) }
}
