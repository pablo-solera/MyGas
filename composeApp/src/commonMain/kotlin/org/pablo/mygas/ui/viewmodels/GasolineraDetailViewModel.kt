package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.pablo.mygas.data.local.database.RoomGasStationAdapter
import org.pablo.mygas.data.remote.adapters.PrecioilGasStationAdapter
import org.pablo.mygas.data.toDomain
import org.pablo.mygas.domain.models.GasStation


data class GasolineraDetailState(
    val isFavorite: Boolean? = false,
    val gasStation: GasStation? = null,
)

class GasolineraDetailViewModel(
    private val precioilGasStationAdapter: PrecioilGasStationAdapter,
    private val roomAdapter: RoomGasStationAdapter
) : ViewModel() {

    private val _state = MutableStateFlow(GasolineraDetailState())
    val state: StateFlow<GasolineraDetailState> = _state.asStateFlow()


    fun getGasStationById(id: Int) {
        viewModelScope.launch {
            val gasStation = precioilGasStationAdapter.getById(id)
            _state.update {
                it.copy(
                    gasStation = gasStation.toDomain()
                )
            }
        }
    }

    fun checkFavorite(id: Int) {
        viewModelScope.launch {
            roomAdapter.observeFavoriteIds().collect { ids ->
                _state.update {
                    it.copy(
                        isFavorite = ids.contains(id)
                    )
                }
            }
        }
    }

    fun insertGasStationId(id: Int) {
        viewModelScope.launch {
            roomAdapter.addFavorite(id)
        }
    }

    fun removeGasStationId(id: Int) {
        viewModelScope.launch {
            roomAdapter.removeFavorite(id)
        }
    }


}

