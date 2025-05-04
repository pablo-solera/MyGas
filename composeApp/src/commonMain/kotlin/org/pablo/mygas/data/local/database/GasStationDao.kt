package org.pablo.mygas.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.pablo.mygas.data.local.database.models.GasStationEntity


@Dao
interface GasStationDao {

    @Query("SELECT * FROM gas_station")
    suspend fun getAllGasStations(): Flow<List<GasStationEntity>>

    @Insert
    suspend fun insertGasStation(gasStation: GasStationEntity)

    @Delete
    suspend fun deleteGasStation(gasStation: GasStationEntity)

}