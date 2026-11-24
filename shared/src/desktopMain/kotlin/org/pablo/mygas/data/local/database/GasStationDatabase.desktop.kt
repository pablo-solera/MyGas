package org.pablo.mygas.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

fun desktopDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val databaseDirectory = File(System.getProperty("user.home"), ".mygas").apply { mkdirs() }
    return Room.databaseBuilder<AppDatabase>(
        name = File(databaseDirectory, DATABASE_NAME).absolutePath,
    )
}
