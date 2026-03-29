package org.pablo.mygas.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}