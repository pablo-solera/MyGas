package org.pablo.mygas.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.pablo.mygas.data.local.database.models.GasStationEntity
import kotlin.reflect.KClass


@Dao
interface GasStationDao {

    @Query("SELECT id FROM favorite_gas_stations")
    fun observeFavoriteIds(): Flow<List<Int>>

    @Query("SELECT id FROM favorite_gas_stations")
    suspend fun getAllFavoriteIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(gasStation: GasStationEntity)

    @Query("DELETE FROM favorite_gas_stations WHERE id = :gasStationId")
    suspend fun deleteById(gasStationId: Int)

}