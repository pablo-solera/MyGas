package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.pablo.mygas.data.local.database.GasStationDao
import org.pablo.mygas.data.local.database.RoomGasStationAdapter
import org.pablo.mygas.data.remote.adapters.PrecioilGasStationAdapter
import org.pablo.mygas.data.toDomain
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.repositories.GasStationRepository

data class GasolineraFavoriteState(
    val gasStationIds: List<Int> = emptyList(),
    val gasStations: List<GasStation>? = emptyList(), // Lista con la información completa
    val isLoading: Boolean = false
)

class GasolineraFavoriteViewModel(
    private val roomAdapter: RoomGasStationAdapter,
    private val precioilGasStationAdapter: PrecioilGasStationAdapter


) : ViewModel() {

    private val _state = MutableStateFlow(GasolineraFavoriteState())
    val state: StateFlow<GasolineraFavoriteState> = _state.asStateFlow()

    init {
        // Iniciamos la observación automáticamente al crear el ViewModel
        observeFavoriteIds()
    }

    fun observeFavoriteIds() {
        viewModelScope.launch {
            roomAdapter.observeFavoriteIds().collect { ids ->
                _state.update { it.copy(gasStationIds = ids, isLoading = true) }

                if (ids.isEmpty()) {
                    _state.update { it.copy(gasStations = emptyList(), isLoading = false) }
                    return@collect
                }

                // Lanzamos todas las peticiones en paralelo
                try {
                    val deferredStations = ids.map { id ->
                        async {
                            precioilGasStationAdapter.getById(id).toDomain()
                        }
                    }

                    val stations = deferredStations.awaitAll()

                    _state.update {
                        it.copy(gasStations = stations, isLoading = false)
                    }
                } catch (e: Exception) {
                    // Manejar error (ej. sin conexión)
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

}
