package org.pablo.mygas.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val databaseDirectory = File(System.getProperty("user.home"), ".mygas")
        .apply { mkdirs() }
    val dbFile = File(databaseDirectory, DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}
