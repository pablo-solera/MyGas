package org.pablo.mygas.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.pablo.mygas.data.local.database.models.GasStationEntity

@Database(entities = [GasStationEntity::class], version = 1)
@ConstructedBy(GasStationDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gasStationDao(): GasStationDao


}
@Suppress("KotlinNoActualForExpect")
expect object GasStationDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
const val DATABASE_NAME = "my_gas.db"
expect fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>

fun getRoomDatabase(): AppDatabase {
    return provideDatabaseBuilder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
