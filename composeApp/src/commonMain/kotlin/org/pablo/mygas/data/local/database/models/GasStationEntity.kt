package org.pablo.mygas.data.local.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorite_gas_stations")
data class GasStationEntity(
    @PrimaryKey
    val id: Int
)
