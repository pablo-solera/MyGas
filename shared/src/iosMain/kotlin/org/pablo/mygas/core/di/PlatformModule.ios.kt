package org.pablo.mygas.core.di

import androidx.room.RoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import org.pablo.mygas.core.utils.IosMapLauncher
import org.pablo.mygas.core.utils.MapLauncher
import org.pablo.mygas.data.local.database.AppDatabase
import org.pablo.mygas.data.local.database.iosDatabaseBuilder

actual val platformModule: Module = module {
    single<MapLauncher> { IosMapLauncher() }
    single<RoomDatabase.Builder<AppDatabase>> { iosDatabaseBuilder() }
}
