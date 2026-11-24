package org.pablo.mygas.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.context.GlobalContext


actual fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val appContext = GlobalContext.get().get<Context>()
    val dbFile = appContext.getDatabasePath(DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}