package org.pablo.mygas.data.local.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class GasStationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val municipioId: String,
    val gasStationId: String,
)
